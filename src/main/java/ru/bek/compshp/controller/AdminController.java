package ru.bek.compshp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.bek.compshp.dto.DashboardStatsDto;
import ru.bek.compshp.dto.ProductDto;
import ru.bek.compshp.dto.UserDto;
import ru.bek.compshp.dto.CategoryDto;
import ru.bek.compshp.model.Category;
import ru.bek.compshp.model.Manufacturer;
import ru.bek.compshp.model.Order;
import ru.bek.compshp.model.Product;
import ru.bek.compshp.model.User;
import ru.bek.compshp.model.enums.ComponentType;
import ru.bek.compshp.model.enums.RoleName;
import ru.bek.compshp.model.enums.OrderStatus;
import ru.bek.compshp.service.AdminService;
import ru.bek.compshp.service.CategoryService;
import ru.bek.compshp.service.ManufacturerService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контроллер для админ-панели с использованием Thymeleaf
 * Доступен только для пользователей с ролью ADMIN
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ManufacturerService manufacturerService;
    private final CategoryService categoryService;
    
    /**
     * Главная страница админ-панели (дашборд)
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping
    public String dashboard(Model model) {
        try {
            DashboardStatsDto stats = adminService.getDashboardStats();
            // Убедимся, что все поля объекта имеют значения
            if (stats.getTotalRevenue() == null) {
                stats = DashboardStatsDto.builder()
                    .totalUsers(stats.getTotalUsers())
                    .totalProducts(stats.getTotalProducts())
                    .totalOrders(stats.getTotalOrders())
                    .totalConfigurations(stats.getTotalConfigurations())
                    .newUsersThisMonth(stats.getNewUsersThisMonth())
                    .newOrdersThisMonth(stats.getNewOrdersThisMonth())
                    .revenueThisMonth(BigDecimal.ZERO) // заменяем null на 0
                    .totalRevenue(BigDecimal.ZERO) // заменяем null на 0
                    .recentOrders(stats.getRecentOrders())
                    .ordersByStatus(stats.getOrdersByStatus())
                    .lowStockProducts(stats.getLowStockProducts())
                    .build();
            }
            model.addAttribute("stats", stats);
            System.out.println("Передаю данные в шаблон dashboard (безопасная версия): " + stats);
        } catch (Exception e) {
            // В случае ошибки создаем пустой объект статистики
            System.err.println("Ошибка при получении статистики: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("stats", DashboardStatsDto.builder()
                .totalUsers(0)
                .totalProducts(0)
                .totalOrders(0)
                .totalConfigurations(0)
                .newUsersThisMonth(0)
                .newOrdersThisMonth(0)
                .revenueThisMonth(BigDecimal.ZERO)
                .totalRevenue(BigDecimal.ZERO)
                .recentOrders(List.of())
                .ordersByStatus(Map.of())
                .lowStockProducts(List.of())
                .build());
        }
        
        model.addAttribute("pageTitle", "Дашборд");
        return "admin/dashboard";
    }
    
    /**
     * Страница со списком пользователей
     * @param page номер страницы
     * @param size размер страницы
     * @param search поисковый запрос
     * @param status фильтр по статусу
     * @param role фильтр по роли
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/users")
    public String getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            Model model) {
        
        // Используем параметры фильтрации
        Page<User> users = adminService.getUsersWithFilters(
                PageRequest.of(page, size, Sort.by("id")),
                search,
                status,
                role
        );
        
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("pageTitle", "Пользователи");
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("role", role);
        model.addAttribute("allRoles", Arrays.stream(RoleName.values())
                .map(RoleName::name)
                .collect(Collectors.toList()));
        
        return "admin/users";
    }
    
    /**
     * Страница с деталями пользователя
     * @param id ID пользователя
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/users/{id}")
    public String getUserDetails(@PathVariable Long id, Model model) {
        User user = adminService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Пользователь: " + user.getUsername());
        
        // Получаем список всех ролей для формы редактирования
        model.addAttribute("allRoles", Arrays.stream(RoleName.values())
                .map(RoleName::name)
                .collect(Collectors.toList()));
        
        return "admin/user-details";
    }
    
    /**
     * Форма создания нового пользователя
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("pageTitle", "Создание пользователя");
        
        // Получаем список всех ролей для формы создания
        model.addAttribute("allRoles", Arrays.stream(RoleName.values())
                .map(RoleName::name)
                .collect(Collectors.toList()));
        
        return "admin/user-form";
    }
    
    /**
     * Обработка создания нового пользователя
     * @param userDto данные пользователя
     * @param bindingResult результат валидации
     * @param model модель для передачи данных в представление
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу пользователей или возврат на форму с ошибками
     */
    @PostMapping("/users/new")
    public String createUser(
            @Valid @ModelAttribute UserDto userDto, 
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Если есть ошибки валидации, возвращаемся на форму
        if (bindingResult.hasErrors()) {
            model.addAttribute("userDto", userDto);
            model.addAttribute("pageTitle", "Создание пользователя");
            // Добавляем список ролей, который нужен для формы
            model.addAttribute("allRoles", Arrays.stream(RoleName.values())
                    .map(RoleName::name)
                    .collect(Collectors.toList()));
            
            return "admin/user-form";
        }
        
        try {
            User user = adminService.createUser(userDto);
            redirectAttributes.addFlashAttribute("message", "Пользователь успешно создан");
            return "redirect:/admin/users/" + user.getId();
        } catch (Exception e) {
            model.addAttribute("userDto", userDto);
            model.addAttribute("pageTitle", "Создание пользователя");
            model.addAttribute("error", "Ошибка при создании пользователя: " + e.getMessage());
            // Добавляем список ролей, который нужен для формы
            model.addAttribute("allRoles", Arrays.stream(RoleName.values())
                    .map(RoleName::name)
                    .collect(Collectors.toList()));
            
            return "admin/user-form";
        }
    }
    
    /**
     * Форма редактирования пользователя
     * @param id ID пользователя
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = adminService.getUserById(id);
        
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .active(user.getActive())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toList()))
                .build();
        
        model.addAttribute("userDto", userDto);
        model.addAttribute("pageTitle", "Редактирование пользователя");
        
        // Получаем список всех ролей для формы редактирования
        model.addAttribute("allRoles", Arrays.stream(RoleName.values())
                .map(RoleName::name)
                .collect(Collectors.toList()));
        
        return "admin/user-form";
    }
    
    /**
     * Обработка обновления пользователя
     * @param id ID пользователя
     * @param userDto новые данные пользователя
     * @param bindingResult результат валидации
     * @param model модель для передачи данных в представление
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу пользователя или возврат на форму с ошибками
     */
    @PostMapping("/users/{id}/edit")
    public String updateUser(
            @PathVariable Long id, 
            @Valid @ModelAttribute UserDto userDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Если есть ошибки валидации, возвращаемся на форму
        if (bindingResult.hasErrors()) {
            model.addAttribute("userDto", userDto);
            model.addAttribute("pageTitle", "Редактирование пользователя");
            // Добавляем список всех ролей для формы редактирования
            model.addAttribute("allRoles", Arrays.stream(RoleName.values())
                    .map(RoleName::name)
                    .collect(Collectors.toList()));
            
            return "admin/user-form";
        }
        
        try {
            User user = adminService.updateUser(id, userDto);
            redirectAttributes.addFlashAttribute("message", "Пользователь успешно обновлен");
            return "redirect:/admin/users/" + user.getId();
        } catch (Exception e) {
            model.addAttribute("userDto", userDto);
            model.addAttribute("pageTitle", "Редактирование пользователя");
            model.addAttribute("error", "Ошибка при обновлении пользователя: " + e.getMessage());
            // Добавляем список всех ролей для формы редактирования
            model.addAttribute("allRoles", Arrays.stream(RoleName.values())
                    .map(RoleName::name)
                    .collect(Collectors.toList()));
            
            return "admin/user-form";
        }
    }
    
    /**
     * Обработка изменения статуса активности пользователя
     * @param id ID пользователя
     * @param isActive новый статус
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу пользователя
     */
    @PostMapping("/users/{id}/toggle-active")
    public String toggleUserActive(
            @PathVariable Long id,
            @RequestParam boolean isActive,
            RedirectAttributes redirectAttributes) {
        
        adminService.updateUserActiveStatus(id, isActive);
        
        String status = isActive ? "активирован" : "деактивирован";
        redirectAttributes.addFlashAttribute("message", "Пользователь " + status);
        
        return "redirect:/admin/users/" + id;
    }
    
    /**
     * Обработка удаления пользователя
     * @param id ID пользователя
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу пользователей
     */
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "Пользователь успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении пользователя: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    /**
     * Страница со списком продуктов
     * @param page номер страницы
     * @param size размер страницы
     * @param search поисковый запрос
     * @param componentType фильтр по типу компонента
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/products")
    public String getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String componentType,
            Model model) {
        
        // Используем параметры фильтрации
        Page<Product> products = adminService.getProductsWithFilters(
                PageRequest.of(page, size, Sort.by("id")),
                search,
                componentType
        );
        
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("componentTypes", ComponentType.values());
        model.addAttribute("manufacturers", manufacturerService.getAllManufacturers());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("pageTitle", "Продукты");
        model.addAttribute("search", search);
        model.addAttribute("selectedComponentType", componentType);
        
        return "admin/products";
    }
    
    /**
     * Обработчик страницы просмотра продукта с обработкой возможных ошибок
     */
    @GetMapping("/products/{id}")
    public String getProductDetails(@PathVariable Long id, Model model) {
        try {
            Product product = adminService.getProductById(id);
            model.addAttribute("product", product);
            model.addAttribute("componentTypes", ComponentType.values());
            model.addAttribute("manufacturers", manufacturerService.getAllManufacturers());
            model.addAttribute("pageTitle", "Продукт: " + product.getTitle());
            return "admin/product-details";
        } catch (Exception e) {
            return "redirect:/admin/products";
        }
    }
    
    /**
     * Форма создания нового продукта
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("productDto", new ProductDto());
        model.addAttribute("pageTitle", "Создание продукта");
        model.addAttribute("componentTypes", ComponentType.values());
        model.addAttribute("manufacturers", manufacturerService.getAllManufacturers());
        model.addAttribute("categories", categoryService.getAllCategories());
        
        return "admin/product-form";
    }
    
    /**
     * Обработка создания нового продукта
     * @param productDto данные продукта
     * @param imageFile файл изображения продукта
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу продуктов
     */
    @PostMapping("/products/new")
    public String createProduct(
            @Valid @ModelAttribute ProductDto productDto,
            @RequestParam(required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        
        try {
            Product product = adminService.createProduct(productDto, imageFile);
            redirectAttributes.addFlashAttribute("message", "Продукт успешно создан");
            return "redirect:/admin/products/" + product.getId();
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при загрузке изображения: " + e.getMessage());
            return "redirect:/admin/products/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании продукта: " + e.getMessage());
            return "redirect:/admin/products/new";
        }
    }
    
    /**
     * Форма редактирования продукта
     * @param id ID продукта
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = adminService.getProductById(id);
        
        ProductDto productDto = ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .componentType(product.getComponentType() != null ? product.getComponentType().name() : null)
                .manufacturerId(product.getManufacturer() != null ? product.getManufacturer().getId() : null)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .specs(product.getSpecs())
                .isActive(product.getIsActive())
                .imagePath(product.getImagePath())
                .build();
        
        model.addAttribute("productDto", productDto);
        model.addAttribute("pageTitle", "Редактирование продукта");
        model.addAttribute("componentTypes", ComponentType.values());
        model.addAttribute("manufacturers", manufacturerService.getAllManufacturers());
        model.addAttribute("categories", categoryService.getAllCategories());
        
        return "admin/product-form";
    }
    
    /**
     * Обработка обновления продукта
     * @param id ID продукта
     * @param productDto новые данные продукта
     * @param imageFile новый файл изображения продукта
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу продукта
     */
    @PostMapping("/products/{id}/edit")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute ProductDto productDto,
            @RequestParam(required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        
        try {
            Product product = adminService.updateProduct(id, productDto, imageFile);
            redirectAttributes.addFlashAttribute("message", "Продукт успешно обновлен");
            return "redirect:/admin/products/" + product.getId();
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при загрузке изображения: " + e.getMessage());
            return "redirect:/admin/products/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении продукта: " + e.getMessage());
            return "redirect:/admin/products/" + id + "/edit";
        }
    }
    
    /**
     * Обработка изменения цены продукта
     * @param id ID продукта
     * @param price новая цена
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу продукта
     */
    @PostMapping("/products/{id}/update-price")
    public String updateProductPrice(
            @PathVariable Long id,
            @RequestParam BigDecimal price,
            RedirectAttributes redirectAttributes) {
        
        adminService.updateProductPrice(id, price);
        redirectAttributes.addFlashAttribute("message", "Цена продукта обновлена");
        
        return "redirect:/admin/products/" + id;
    }
    
    /**
     * Обработка изменения количества продукта на складе
     * @param id ID продукта
     * @param stock новое количество
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу продукта
     */
    @PostMapping("/products/{id}/update-stock")
    public String updateProductStock(
            @PathVariable Long id,
            @RequestParam Integer stock,
            RedirectAttributes redirectAttributes) {
        
        adminService.updateProductStock(id, stock);
        redirectAttributes.addFlashAttribute("message", "Количество продукта на складе обновлено");
        
        return "redirect:/admin/products/" + id;
    }
    
    /**
     * Обработка изменения статуса активности продукта
     * @param id ID продукта
     * @param isActive новый статус
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу продукта
     */
    @PostMapping("/products/{id}/toggle-active")
    public String toggleProductActive(
            @PathVariable Long id,
            @RequestParam boolean isActive,
            RedirectAttributes redirectAttributes) {
        
        adminService.updateProductActiveStatus(id, isActive);
        
        String status = isActive ? "активирован" : "деактивирован";
        redirectAttributes.addFlashAttribute("message", "Продукт " + status);
        
        return "redirect:/admin/products/" + id;
    }
    
    /**
     * Обработка удаления продукта
     * @param id ID продукта
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу продуктов
     */
    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Продукт успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении продукта: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }
    
    /**
     * Загружает изображение продукта
     * @param id ID продукта
     * @param imageFile файл изображения
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу продукта
     */
    @PostMapping("/products/{id}/upload-image")
    public String uploadProductImage(
            @PathVariable("id") Long id,
            @RequestParam("image") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        
        try {
            Product product = adminService.updateProductImage(id, imageFile);
            redirectAttributes.addFlashAttribute("message", "Изображение продукта успешно обновлено");
            return "redirect:/admin/products/" + id;
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при загрузке изображения: " + e.getMessage());
            return "redirect:/admin/products/" + id;
        }
    }
    
    /**
     * Страница со списком заказов
     * @param page номер страницы
     * @param size размер страницы
     * @param status фильтр по статусу заказа
     * @param search поисковый запрос
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/orders")
    public String getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Model model) {
        
        OrderStatus orderStatus = status != null ? OrderStatus.valueOf(status) : null;
        
        Page<Order> orders = adminService.getOrdersWithFilters(
                PageRequest.of(page, size, Sort.by("createdAt").descending()),
                orderStatus,
                search
        );
        
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("orderStatuses", OrderStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("search", search);
        model.addAttribute("pageTitle", "Заказы");
        
        return "admin/orders";
    }
    
    /**
     * Страница с деталями заказа
     * @param id ID заказа
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/orders/{id}")
    public String getOrderDetails(@PathVariable Long id, Model model) {
        Order order = adminService.getOrderById(id);
        model.addAttribute("order", order);
        model.addAttribute("orderStatuses", OrderStatus.values());
        model.addAttribute("pageTitle", "Заказ #" + order.getId());
        return "admin/order-details";
    }
    
    /**
     * Обработка изменения статуса заказа
     * @param id ID заказа
     * @param status новый статус
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу заказа
     */
    @PostMapping("/orders/{id}/update-status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status,
            RedirectAttributes redirectAttributes) {
        
        adminService.updateOrderStatus(id, status);
        redirectAttributes.addFlashAttribute("message", "Статус заказа обновлен");
        
        return "redirect:/admin/orders/" + id;
    }
    
    //
    // Управление категориями
    //
    
    /**
     * Страница со списком категорий
     * @param page номер страницы
     * @param size размер страницы
     * @param search поисковый запрос
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/categories")
    public String getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {
        
        Page<Category> categories = adminService.getCategoriesWithFilters(
                PageRequest.of(page, size, Sort.by("name")),
                search
        );
        
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categories.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("pageTitle", "Категории");
        
        return "admin/categories";
    }
    
    /**
     * Страница с деталями категории
     * @param id ID категории
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/categories/{id}")
    public String getCategoryDetails(@PathVariable Long id, Model model) {
        Category category = adminService.getCategoryById(id);
        model.addAttribute("category", category);
        model.addAttribute("pageTitle", "Категория: " + category.getName());
        return "admin/category-details";
    }
    
    /**
     * Форма создания новой категории
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/categories/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("categoryDto", new CategoryDto());
        model.addAttribute("allCategories", categoryService.getAllCategories());
        model.addAttribute("pageTitle", "Создание категории");
        return "admin/category-form";
    }
    
    /**
     * Обработка создания новой категории
     * @param categoryDto данные категории
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу категорий
     */
    @PostMapping("/categories/new")
    public String createCategory(
            @Valid @ModelAttribute CategoryDto categoryDto,
            RedirectAttributes redirectAttributes) {
        
        try {
            Category category = adminService.createCategory(categoryDto);
            redirectAttributes.addFlashAttribute("message", "Категория успешно создана");
            return "redirect:/admin/categories/" + category.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании категории: " + e.getMessage());
            return "redirect:/admin/categories/new";
        }
    }
    
    /**
     * Форма редактирования категории
     * @param id ID категории
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/categories/{id}/edit")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        Category category = adminService.getCategoryById(id);
        
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
        if (category.getParent() != null) {
            categoryDto.setParentId(category.getParent().getId());
        }
        
        model.addAttribute("categoryDto", categoryDto);
        model.addAttribute("allCategories", categoryService.getAllCategories());
        model.addAttribute("pageTitle", "Редактирование категории");
        return "admin/category-form";
    }
    
    /**
     * Обработка обновления категории
     * @param id ID категории
     * @param categoryDto новые данные категории
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу категории
     */
    @PostMapping("/categories/{id}/edit")
    public String updateCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute CategoryDto categoryDto,
            RedirectAttributes redirectAttributes) {
        
        try {
            Category category = adminService.updateCategory(id, categoryDto);
            redirectAttributes.addFlashAttribute("message", "Категория успешно обновлена");
            return "redirect:/admin/categories/" + category.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении категории: " + e.getMessage());
            return "redirect:/admin/categories/" + id + "/edit";
        }
    }
    
    /**
     * Обработка удаления категории
     * @param id ID категории
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу категорий
     */
    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("message", "Категория успешно удалена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении категории: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }
    
    /**
     * Отображает категории в иерархическом виде
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/categories/tree")
    public String getCategoryTree(Model model) {
        List<CategoryDto> rootCategories = categoryService.getRootCategories();
        model.addAttribute("rootCategories", rootCategories);
        model.addAttribute("pageTitle", "Дерево категорий");
        return "admin/category-tree";
    }
    
    //
    // Управление производителями
    //
    
    /**
     * Страница со списком производителей
     * @param page номер страницы
     * @param size размер страницы
     * @param search поисковый запрос
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/manufacturers")
    public String getManufacturers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {
        
        Page<Manufacturer> manufacturers = adminService.getManufacturersWithFilters(
                PageRequest.of(page, size, Sort.by("name")),
                search
        );
        
        model.addAttribute("manufacturers", manufacturers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", manufacturers.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("pageTitle", "Производители");
        
        return "admin/manufacturers";
    }
    
    /**
     * Страница с деталями производителя
     * @param id ID производителя
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/manufacturers/{id}")
    public String getManufacturerDetails(@PathVariable Long id, Model model) {
        Manufacturer manufacturer = adminService.getManufacturerById(id);
        model.addAttribute("manufacturer", manufacturer);
        model.addAttribute("pageTitle", "Производитель: " + manufacturer.getName());
        return "admin/manufacturer-details";
    }
    
    /**
     * Форма создания нового производителя
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/manufacturers/new")
    public String newManufacturerForm(Model model) {
        model.addAttribute("pageTitle", "Создание производителя");
        return "admin/manufacturer-form";
    }
    
    /**
     * Обработка создания нового производителя
     * @param name имя производителя
     * @param description описание производителя
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу производителей
     */
    @PostMapping("/manufacturers/new")
    public String createManufacturer(
            @RequestParam String name,
            @RequestParam String description,
            RedirectAttributes redirectAttributes) {
        
        try {
            Manufacturer manufacturer = adminService.createManufacturer(name, description);
            redirectAttributes.addFlashAttribute("message", "Производитель успешно создан");
            return "redirect:/admin/manufacturers/" + manufacturer.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании производителя: " + e.getMessage());
            return "redirect:/admin/manufacturers/new";
        }
    }
    
    /**
     * Форма редактирования производителя
     * @param id ID производителя
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/manufacturers/{id}/edit")
    public String editManufacturerForm(@PathVariable Long id, Model model) {
        Manufacturer manufacturer = adminService.getManufacturerById(id);
        model.addAttribute("manufacturer", manufacturer);
        model.addAttribute("pageTitle", "Редактирование производителя");
        return "admin/manufacturer-form";
    }
    
    /**
     * Обработка обновления производителя
     * @param id ID производителя
     * @param name новое имя производителя
     * @param description новое описание производителя
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу производителя
     */
    @PostMapping("/manufacturers/{id}/edit")
    public String updateManufacturer(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            RedirectAttributes redirectAttributes) {
        
        try {
            Manufacturer manufacturer = adminService.updateManufacturer(id, name, description);
            redirectAttributes.addFlashAttribute("message", "Производитель успешно обновлен");
            return "redirect:/admin/manufacturers/" + manufacturer.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении производителя: " + e.getMessage());
            return "redirect:/admin/manufacturers/" + id + "/edit";
        }
    }
    
    /**
     * Обработка удаления производителя
     * @param id ID производителя
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу производителей
     */
    @PostMapping("/manufacturers/{id}/delete")
    public String deleteManufacturer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteManufacturer(id);
            redirectAttributes.addFlashAttribute("message", "Производитель успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении производителя: " + e.getMessage());
        }
        return "redirect:/admin/manufacturers";
    }
    
    /**
     * Экспорт списка пользователей в CSV
     * @param search поисковый запрос
     * @param status фильтр по статусу
     * @param role фильтр по роли
     * @return CSV файл с пользователями
     */
    @GetMapping("/users/export/csv")
    public String exportUsersToCSV(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            RedirectAttributes redirectAttributes) {
        
        try {
            adminService.exportUsersToCSV(search, status, role);
            redirectAttributes.addFlashAttribute("message", "Экспорт пользователей выполнен успешно");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при экспорте пользователей: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }
    
    /**
     * Экспорт списка продуктов в CSV
     * @param search поисковый запрос
     * @param componentType фильтр по типу компонента
     * @return CSV файл с продуктами
     */
    @GetMapping("/products/export/csv")
    public String exportProductsToCSV(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String componentType,
            RedirectAttributes redirectAttributes) {
        
        try {
            adminService.exportProductsToCSV(search, componentType);
            redirectAttributes.addFlashAttribute("message", "Экспорт продуктов выполнен успешно");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при экспорте продуктов: " + e.getMessage());
        }
        
        return "redirect:/admin/products";
    }
    
    /**
     * Экспорт списка заказов в CSV
     * @param status фильтр по статусу заказа
     * @param search поисковый запрос
     * @return CSV файл с заказами
     */
    @GetMapping("/orders/export/csv")
    public String exportOrdersToCSV(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            RedirectAttributes redirectAttributes) {
        
        OrderStatus orderStatus = status != null ? OrderStatus.valueOf(status) : null;
        
        try {
            adminService.exportOrdersToCSV(orderStatus, search);
            redirectAttributes.addFlashAttribute("message", "Экспорт заказов выполнен успешно");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при экспорте заказов: " + e.getMessage());
        }
        
        return "redirect:/admin/orders";
    }
    
    /**
     * Страница импорта продуктов из CSV
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/product-import")
    public String showProductImport(Model model) {
        model.addAttribute("pageTitle", "Импорт продуктов");
        return "admin/product-import";
    }
    
    /**
     * Обработка импорта продуктов из CSV
     * @param csvFile файл CSV с данными продуктов
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу продуктов
     */
    @PostMapping("/product-import")
    public String importProducts(
            @RequestParam("csvFile") MultipartFile csvFile,
            RedirectAttributes redirectAttributes) {
        
        try {
            int importedCount = adminService.importProductsFromCSV(csvFile);
            redirectAttributes.addFlashAttribute("message", "Успешно импортировано " + importedCount + " продуктов");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при импорте продуктов: " + e.getMessage());
        }
        
        return "redirect:/admin/products";
    }

    /**
     * Обработчик запроса на удаление изображения продукта
     */
    @PostMapping("/products/{id}/delete-image")
    public String deleteProductImage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteProductImage(id);
            redirectAttributes.addFlashAttribute("message", "Изображение продукта успешно удалено");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении изображения: " + e.getMessage());
        }
        return "redirect:/admin/products/" + id + "/edit";
    }
    
    /**
     * Обработчик для массового импорта продуктов
     */
    @GetMapping("/products/bulk-import")
    public String bulkImportForm(Model model) {
        model.addAttribute("pageTitle", "Массовый импорт продуктов");
        return "admin/product-bulk-import";
    }

    /**
     * Страница отчетов
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/reports")
    public String showReports(Model model) {
        model.addAttribute("pageTitle", "Отчеты");
        return "admin/reports";
    }
    
    /**
     * Отчет по продажам за период
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/reports/sales")
    public String showSalesReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        
        // Здесь будет логика получения данных для отчета
        model.addAttribute("pageTitle", "Отчет по продажам");
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        return "admin/reports-sales";
    }
    
    /**
     * Отчет по популярным продуктам
     * @param limit количество продуктов
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/reports/popular-products")
    public String showPopularProductsReport(
            @RequestParam(defaultValue = "10") int limit,
            Model model) {
        
        // Здесь будет логика получения данных для отчета
        model.addAttribute("pageTitle", "Популярные продукты");
        model.addAttribute("limit", limit);
        
        return "admin/reports-popular-products";
    }
} 