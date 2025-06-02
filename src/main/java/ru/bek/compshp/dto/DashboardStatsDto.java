package ru.bek.compshp.dto;

import lombok.Builder;
import lombok.Data;
import ru.bek.compshp.model.Order;
import ru.bek.compshp.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DTO для передачи статистики на дашборд админки
 */
@Data
@Builder
public class DashboardStatsDto {
    // Общая статистика
    private long totalUsers;
    private long totalProducts;
    private long totalOrders;
    private long totalConfigurations;
    
    // Статистика за последний месяц
    private long newUsersThisMonth;
    private long newOrdersThisMonth;
    private BigDecimal revenueThisMonth;
    
    // Финансовая статистика
    private BigDecimal totalRevenue;
    
    // Информация о заказах
    private List<Order> recentOrders;
    private Map<String, Long> ordersByStatus;
    
    // Информация о продуктах
    private List<Product> lowStockProducts;
} 