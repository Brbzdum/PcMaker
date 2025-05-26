package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "pc_configurations")
@NoArgsConstructor
@AllArgsConstructor
public class PCConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "total_performance")
    private Double totalPerformance;

    @Column(name = "is_compatible")
    private Boolean isCompatible;

    @OneToMany(mappedBy = "configuration", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConfigComponent> components = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isCompatible == null) isCompatible = true;
        if (totalPrice == null) totalPrice = BigDecimal.ZERO;
        if (totalPerformance == null) totalPerformance = 0.0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void updateTotalPerformance() {
        this.totalPerformance = components.stream()
            .mapToDouble(component -> {
                String performanceStr = component.getProduct().getSpec("performance");
                try {
                    return Double.parseDouble(performanceStr);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            })
            .sum();
    }

    public void updateTotalPrice() {
        this.totalPrice = components.stream()
            .map(component -> component.getProduct().getPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
} 