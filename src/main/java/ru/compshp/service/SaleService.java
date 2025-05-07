package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.Sale;
import ru.compshp.repository.SaleRepository;
import ru.compshp.repository.OrderRepository;
import ru.compshp.repository.ProductRepository;
import ru.compshp.model.Order;
import ru.compshp.model.enums.OrderStatus;
import ru.compshp.model.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {
    private final SaleRepository saleRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Sale createSale(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot create sale for non-delivered order");
        }

        Sale sale = new Sale();
        sale.setOrder(order);
        sale.setSaleDate(LocalDateTime.now());
        sale.setTotalProfit(calculateProfit(order));

        return saleRepository.save(sale);
    }

    @Transactional(readOnly = true)
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Sale> getSaleById(Long id) {
        return saleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Sale> getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findBySaleDateBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalProfit() {
        return saleRepository.findAll().stream()
                .map(Sale::getTotalProfit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getProfitByCategory(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findBySaleDateBetween(startDate, endDate).stream()
                .flatMap(sale -> sale.getOrder().getOrderItems().stream())
                .collect(Collectors.groupingBy(
                    item -> item.getProduct().getCategory().getName(),
                    Collectors.mapping(
                        item -> item.getPrice().subtract(item.getProduct().getPurchasePrice())
                                .multiply(new BigDecimal(item.getQuantity())),
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                    )
                ));
    }

    @Transactional(readOnly = true)
    public List<Map.Entry<Product, Long>> getPopularProducts(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        return saleRepository.findBySaleDateBetween(startDate, endDate).stream()
                .flatMap(sale -> sale.getOrder().getOrderItems().stream())
                .collect(Collectors.groupingBy(
                    item -> item.getProduct(),
                    Collectors.summingLong(item -> item.getQuantity())
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Product, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<LocalDateTime, BigDecimal> getSalesTrend(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findBySaleDateBetween(startDate, endDate).stream()
                .collect(Collectors.groupingBy(
                    sale -> sale.getSaleDate().toLocalDate().atStartOfDay(),
                    Collectors.mapping(
                        Sale::getTotalProfit,
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                    )
                ));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSalesStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Sale> sales = saleRepository.findBySaleDateBetween(startDate, endDate);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalSales", sales.size());
        statistics.put("totalProfit", getTotalProfit());
        statistics.put("averageOrderValue", calculateAverageOrderValue(sales));
        statistics.put("profitByCategory", getProfitByCategory(startDate, endDate));
        statistics.put("popularProducts", getPopularProducts(startDate, endDate, 5));
        statistics.put("salesTrend", getSalesTrend(startDate, endDate));
        
        return statistics;
    }

    private BigDecimal calculateAverageOrderValue(List<Sale> sales) {
        if (sales.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return sales.stream()
                .map(sale -> sale.getOrder().getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(sales.size()), 2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal calculateProfit(Order order) {
        return order.getOrderItems().stream()
                .map(item -> item.getPrice().subtract(item.getProduct().getPurchasePrice())
                        .multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
} 