package com.sih.traffic.domain;

import com.sih.traffic.domain.enums.TrainType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * A platform belongs to exactly one Station. Compatibility with train types
 * (e.g. a short platform not fit for a long freight rake) is captured via
 * compatibleTrainTypes so the (future) allocation logic never assigns an
 * incompatible train to it.
 */
@Entity
@Table(name = "platform",
        uniqueConstraints = @UniqueConstraint(name = "uk_platform_station_number",
                columnNames = {"station_id", "platform_number"}))
@Getter
@Setter
@NoArgsConstructor
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_platform_station"))
    private Station station;

    @NotBlank
    @Size(max = 10)
    @Column(name = "platform_number", nullable = false, length = 10)
    private String platformNumber;

    @NotNull
    @Positive
    @Column(name = "length_m", nullable = false)
    private Double lengthM;

    @ElementCollection(targetClass = TrainType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "platform_compatible_train_type",
            joinColumns = @JoinColumn(name = "platform_id"),
            foreignKey = @ForeignKey(name = "fk_platform_compat_platform"))
    @Enumerated(EnumType.STRING)
    @Column(name = "train_type", length = 20)
    private Set<TrainType> compatibleTrainTypes = new HashSet<>();
}
