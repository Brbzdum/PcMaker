package ru.compshp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.service.UserService;
import ru.compshp.service.ProductService;
import ru.compshp.service.OrderService;
import ru.compshp.service.SaleService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final SaleService saleService;

    public AdminController(UserService userService, ProductService productService,
                          OrderService orderService, SaleService saleService) {
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
        this.saleService = saleService;
    }

    // TODO: Получить статистику для дашборда
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        // TODO: Вернуть статистику
        return ResponseEntity.ok(/* статистика */ null);
    }

    // TODO: Получить список пользователей
    @GetMapping("/users")
    public ResponseEntity<?> listUsers() {
        // TODO: Вернуть список пользователей
        return ResponseEntity.ok(/* список пользователей */ null);
    }

    // TODO: Получить список товаров
    @GetMapping("/products")
    public ResponseEntity<?> listProducts() {
        // TODO: Вернуть список товаров
        return ResponseEntity.ok(/* список товаров */ null);
    }

    // TODO: Получить список заказов
    @GetMapping("/orders")
    public ResponseEntity<?> listOrders() {
        // TODO: Вернуть список заказов
        return ResponseEntity.ok(/* список заказов */ null);
    }

    // TODO: Получить статистику продаж
    @GetMapping("/sales")
    public ResponseEntity<?> showSales() {
        // TODO: Вернуть статистику продаж
        return ResponseEntity.ok(/* статистика */ null);
    }

    // TODO: Получить список отзывов
    @GetMapping("/reviews")
    public ResponseEntity<?> listReviews() {
        // TODO: Вернуть список отзывов
        return ResponseEntity.ok(/* список отзывов */ null);
    }

    // TODO: Получить список производителей
    @GetMapping("/manufacturers")
    public ResponseEntity<?> listManufacturers() {
        // TODO: Вернуть список производителей
        return ResponseEntity.ok(/* список производителей */ null);
    }

    // TODO: Методы для управления пользователями, товарами, заказами, отзывами, производителями, скидками, акциями, конфигурациями и экспорта данных
} 