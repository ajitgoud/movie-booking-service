package com.blackgoku.moviebooking.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
class SeatHoldServiceConcurrencyTest {

    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);
    @Resource
    private SeatHoldService seatHoldService;

    @BeforeAll
    static void startRedis() {
        redis.start();
    }

    @AfterAll
    static void stopRedis() {
        redis.stop();
    }

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Test
    void onlyOneThreadShouldSuccessfullyHoldTheSameSeat() throws InterruptedException {
        String showId = "show-concurrency-test";
        String seatId = "A1";
        int threadCount = 50;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        IntStream.range(0, threadCount).forEach(i ->
                executor.submit(() -> {
                    try {
                        startSignal.await();
                        if (seatHoldService.tryHoldSeat(showId, seatId, "user-" + i)) {
                            successCount.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        doneSignal.countDown();
                    }
                })
        );

        startSignal.countDown();
        assertEquals(true, doneSignal.await(10, TimeUnit.SECONDS));
        executor.shutdown();

        assertEquals(1, successCount.get());
    }
}
