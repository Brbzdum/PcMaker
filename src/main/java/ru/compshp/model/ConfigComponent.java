package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "config_components")
public class ConfigComponent {
    @EmbeddedId
    private ConfigComponentId id;

    @ManyToOne
    @MapsId("configId")
    @JoinColumn(name = "config_id")
    private PCConfiguration configuration;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    // TODO: Добавить метод для проверки совместимости с другими компонентами
    // TODO: Добавить метод для расчета стоимости компонента с учетом количества
    // TODO: Добавить метод для проверки наличия компонента на складе
    // TODO: Добавить метод для обновления количества компонента
}

@Embeddable
@Data
class ConfigComponentId implements java.io.Serializable {
    private Long configId;
    private Long productId;
} 