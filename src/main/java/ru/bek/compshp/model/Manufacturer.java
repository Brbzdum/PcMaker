package ru.bek.compshp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель производителя
 */
@Entity
@Table(name = "manufacturers")
@Getter
@Setter
@EqualsAndHashCode(exclude = "products")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Manufacturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Double rating = 0.0;

    @Builder.Default
    @OneToMany(mappedBy = "manufacturer")
    @ToString.Exclude
    private List<Product> products = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Override
    public String toString() {
        return "Manufacturer{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", rating=" + rating +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
} 