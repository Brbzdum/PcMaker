package ru.compshp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Модель отзыва
 */
@Entity
@Table(name = "reviews")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer rating;

    @Column
    private String comment;

    @Builder.Default
    @Column(name = "is_approved")
    private Boolean isApproved = false;

    @Builder.Default
    @Column(name = "is_verified_purchase")
    private Boolean isVerifiedPurchase = false;

    @Builder.Default
    @Column(name = "report_count")
    private Integer reportCount = 0;

    @Builder.Default
    @Column(name = "is_moderated")
    private Boolean isModerated = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 