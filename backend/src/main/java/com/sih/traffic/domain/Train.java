package com.sih.traffic.domain;

import com.sih.traffic.domain.enums.TrainType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "train", uniqueConstraints = @UniqueConstraint(name = "uk_train_number", columnNames = "train_number"))
@Getter
@Setter
@NoArgsConstructor
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 15)
    @Column(name = "train_number", nullable = false, length = 15)
    private String trainNumber;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TrainType type;

    /** Lower number = higher precedence when the optimizer resolves conflicts (later phase). */
    @NotNull
    @Min(1)
    @Max(10)
    @Column(name = "priority", nullable = false)
    private Integer priority;

    @NotNull
    @Positive
    @Column(name = "max_speed_kmph", nullable = false)
    private Integer maxSpeedKmph;

    @NotNull
    @Positive
    @Column(name = "length_m", nullable = false)
    private Double lengthM;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "origin_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_train_origin"))
    private Station origin;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "destination_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_train_destination"))
    private Station destination;
}
