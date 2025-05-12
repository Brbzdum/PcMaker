package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.*;
import ru.compshp.repository.PromotionRepository;
import ru.compshp.repository.DiscountRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PricingService {
    private final PromotionRepository promotionRepository;
    private final DiscountRepository discountRepository;

    public BigDecimal calculateProductPrice(Product product, int quantity) {
        BigDecimal basePrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        BigDecimal discount = calculateProductDiscount(product, quantity);
        return basePrice.subtract(discount);
    }

    public BigDecimal calculateConfigurationPrice(PCConfiguration config) {
        return config.getComponents().stream()
                .map(component -> calculateProductPrice(component.getProduct(), component.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateOrderPrice(Order order) {
        BigDecimal itemsPrice = order.getItems().stream()
                .map(item -> calculateProductPrice(item.getProduct(), item.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = calculateOrderDiscount(order);
        return itemsPrice.subtract(discount);
    }

    private BigDecimal calculateProductDiscount(Product product, int quantity) {
        BigDecimal totalDiscount = BigDecimal.ZERO;

        // Проверяем акции
        List<Promotion> activePromotions = promotionRepository.findActivePromotions(
                LocalDateTime.now(), product.getCategory().getId());
        
        for (Promotion promotion : activePromotions) {
            if (isPromotionApplicable(promotion, product, quantity)) {
                totalDiscount = totalDiscount.add(calculatePromotionDiscount(promotion, product, quantity));
            }
        }

        // Проверяем скидки
        Optional<Discount> discount = discountRepository.findActiveDiscount(
                LocalDateTime.now(), product.getCategory().getId());
        
        if (discount.isPresent()) {
            totalDiscount = totalDiscount.add(calculateDiscountAmount(discount.get(), product, quantity));
        }

        return totalDiscount;
    }

    private BigDecimal calculateOrderDiscount(Order order) {
        BigDecimal totalDiscount = BigDecimal.ZERO;

        // Проверяем акции на заказ
        List<Promotion> activePromotions = promotionRepository.findActiveOrderPromotions(
                LocalDateTime.now());
        
        for (Promotion promotion : activePromotions) {
            if (isOrderPromotionApplicable(promotion, order)) {
                totalDiscount = totalDiscount.add(calculateOrderPromotionDiscount(promotion, order));
            }
        }

        return totalDiscount;
    }

    private boolean isPromotionApplicable(Promotion promotion, Product product, int quantity) {
        return promotion.getMinQuantity() <= quantity &&
               promotion.getStartDate().isBefore(LocalDateTime.now()) &&
               promotion.getEndDate().isAfter(LocalDateTime.now());
    }

    private boolean isOrderPromotionApplicable(Promotion promotion, Order order) {
        return promotion.getMinOrderAmount().compareTo(order.getTotalPrice()) <= 0 &&
               promotion.getStartDate().isBefore(LocalDateTime.now()) &&
               promotion.getEndDate().isAfter(LocalDateTime.now());
    }

    private BigDecimal calculatePromotionDiscount(Promotion promotion, Product product, int quantity) {
        if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
            return product.getPrice()
                    .multiply(BigDecimal.valueOf(quantity))
                    .multiply(promotion.getDiscountValue())
                    .divide(BigDecimal.valueOf(100));
        } else {
            return promotion.getDiscountValue().multiply(BigDecimal.valueOf(quantity));
        }
    }

    private BigDecimal calculateOrderPromotionDiscount(Promotion promotion, Order order) {
        if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
            return order.getTotalPrice()
                    .multiply(promotion.getDiscountValue())
                    .divide(BigDecimal.valueOf(100));
        } else {
            return promotion.getDiscountValue();
        }
    }

    private BigDecimal calculateDiscountAmount(Discount discount, Product product, int quantity) {
        if (discount.getDiscountType() == DiscountType.PERCENTAGE) {
            return product.getPrice()
                    .multiply(BigDecimal.valueOf(quantity))
                    .multiply(discount.getDiscountValue())
                    .divide(BigDecimal.valueOf(100));
        } else {
            return discount.getDiscountValue().multiply(BigDecimal.valueOf(quantity));
        }
    }
} 