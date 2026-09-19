package com.blackgoku.moviebooking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Time-bound seat holds backed by Redis. A hold is a single SETNX-with-TTL key -
 * a claim that expires automatically if the user never completes payment, with no
 * background cleanup job required.
 * <p>
 * The one subtlety worth remembering: a plain "check ownership, then act" against
 * Redis is itself a check-then-act race if done as two separate commands. Every
 * operation here is either a single atomic Redis command (SETNX) or a Lua script
 * executed atomically on the server (extend-if-owner) - never a GET followed by a
 * separate SET/EXPIRE from the application side.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeatHoldService {

    private static final Duration INITIAL_HOLD_TTL = Duration.ofMinutes(10);
    private static final Duration CONFIRM_EXTENSION_TTL = Duration.ofSeconds(15);
    private static final String EXTEND_IF_OWNER_SCRIPT = """
            if redis.call('get', KEYS[1]) == ARGV[1] then
                return redis.call('expire', KEYS[1], ARGV[2])
            else
                return 0
            end
            """;
    private final StringRedisTemplate redisTemplate;

    private String holdKey(String showId, String seatId) {
        return "hold:%s:%s".formatted(showId, seatId);
    }

    public boolean tryHoldSeat(String showId, String seatId, String userId) {
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(holdKey(showId, seatId), userId, INITIAL_HOLD_TTL);
        return Boolean.TRUE.equals(acquired);
    }

    /**
     * Holds a batch of seats. If ANY seat in the batch is unavailable, all holds
     * acquired so far in this call are rolled back - never leave a user half-holding
     * a seat map, which would be confusing and would leak un-released holds.
     */
    public List<String> tryHoldSeats(String showId, List<String> seatIds, String userId) {
        List<String> held = new ArrayList<>();
        for (String seatId : seatIds) {
            if (tryHoldSeat(showId, seatId, userId)) {
                held.add(seatId);
            } else {
                held.forEach(s -> releaseHold(showId, s, userId));
                return List.of();
            }
        }
        return held;
    }

    /**
     * Atomically verifies ownership and extends TTL to cover the confirm/payment flow.
     */
    public boolean extendHoldIfOwner(String showId, String seatId, String userId) {
        Long result = redisTemplate.execute(
                RedisScript.of(EXTEND_IF_OWNER_SCRIPT, Long.class),
                List.of(holdKey(showId, seatId)),
                userId, String.valueOf(CONFIRM_EXTENSION_TTL.getSeconds())
        );
        return result != null && result == 1L;
    }

    public void releaseHold(String showId, String seatId, String userId) {
        String key = holdKey(showId, seatId);
        String currentHolder = redisTemplate.opsForValue().get(key);
        if (userId.equals(currentHolder)) {
            redisTemplate.delete(key);
        }
    }

    public boolean isHeldByAnyoneElse(String showId, String seatId, String userId) {
        String holder = redisTemplate.opsForValue().get(holdKey(showId, seatId));
        return holder != null && !holder.equals(userId);
    }

    public boolean isFree(String showId, String seatId) {
        return redisTemplate.opsForValue().get(holdKey(showId, seatId)) == null;
    }
}
