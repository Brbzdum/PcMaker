package ru.bek.compshp.model;

import jakarta.persistence.*;
import lombok.*;
import ru.bek.compshp.model.enums.RoleName;

import java.time.LocalDateTime;

/**
 * Модель роли пользователя
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Override
    public String toString() {
        return "Role{" +
            "id=" + id +
            ", name=" + name +
            ", createdAt=" + createdAt +
            '}';
    }
} 