package ru.compshp.model.enums;

/**
 * Статусы заказа
 */
public enum OrderStatus {
    /**
     * Ожидает оплаты
     */
    PENDING,
    
    /**
     * Оплачен
     */
    PAID,
    
    /**
     * В обработке
     */
    PROCESSING,
    
    /**
     * Собран
     */
    ASSEMBLED,
    
    /**
     * Отправлен
     */
    SHIPPED,
    
    /**
     * Доставлен
     */
    DELIVERED,
    
    /**
     * Выполнен
     */
    COMPLETED,
    
    /**
     * Отменен
     */
    CANCELLED,
    
    /**
     * Возврат
     */
    RETURNED
} 