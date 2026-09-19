package com.blackgoku.moviebooking.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "screens")
public class Screen {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "theatre_id")
    private Theatre theatre;

    private String screenNumber;
    private String screenType; // 2D, 3D, IMAX

}
