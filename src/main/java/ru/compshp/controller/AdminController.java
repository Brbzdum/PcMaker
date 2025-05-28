package ru.compshp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.compshp.dto.DashboardStatsDto;
import ru.compshp.dto.ProductDto;
import ru.compshp.dto.UserDto;
import ru.compshp.dto.CategoryDto;
import ru.compshp.model.Category;
import ru.compshp.model.Manufacturer;
import ru.compshp.model.Order;
import ru.compshp.model.Product;
import ru.compshp.model.User;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.model.enums.RoleName;
import ru.compshp.model.enums.OrderStatus;
import ru.compshp.service.AdminService;
import ru.compshp.service.CategoryService;
import ru.compshp.service.ManufacturerService;
import ru.compshp.service.RoleService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
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
    private final RoleService roleService;
    private final CategoryService categoryService;
    
    /**
     * Главная страница админ-панели (дашборд)
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping
    public String dashboard(Model model) {
        DashboardStatsDto stats = adminService.getDashboardStats();
        model.addAttribute("stats", stats);
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
        
        // В реальном приложении здесь должна быть фильтрация по поисковому запросу, статусу и роли
        Page<User> users = adminService.getAllUsers(PageRequest.of(page, size, Sort.by("id")));
        
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("pageTitle", "Пользователи");
        
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
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу пользователей
     */
    @PostMapping("/users/new")
    public String createUser(@Valid @ModelAttribute UserDto userDto, RedirectAttributes redirectAttributes) {
        try {
            User user = adminService.createUser(userDto);
            redirectAttributes.addFlashAttribute("message", "Пользователь успешно создан");
            return "redirect:/admin/users/" + user.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании пользователя: " + e.getMessage());
            return "redirect:/admin/users/new";
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
                .active(user.isActive())
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
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на страницу пользователя
     */
    @PostMapping("/users/{id}/edit")
    public String updateUser(
            @PathVariable Long id, 
            @Valid @ModelAttribute UserDto userDto,
            RedirectAttributes redirectAttributes) {
        
        try {
            User user = adminService.updateUser(id, userDto);
            redirectAttributes.addFlashAttribute("message", "Пользователь успешно обновлен");
            return "redirect:/admin/users/" + user.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении пользователя: " + e.getMessage());
            return "redirect:/admin/users/" + id + "/edit";
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
        
        // В реальном приложении здесь должна быть фильтрация по поисковому запросу и типу компонента
        Page<Product> products = adminService.getAllProducts(PageRequest.of(page, size, Sort.by("id")));
        
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("componentTypes", ComponentType.values());
        model.addAttribute("manufacturers", manufacturerService.getAllManufacturers());
        model.addAttribute("pageTitle", "Продукты");
        
        return "admin/products";
    }
    
    /**
     * Страница с деталями продукта
     * @param id ID продукта
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/products/{id}")
    public String getProductDetails(@PathVariable Long id, Model model) {
        Product product = adminService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("componentTypes", ComponentType.values());
        model.addAttribute("manufacturers", manufacturerService.getAllManufacturers());
        model.addAttribute("pageTitle", "Продукт: " + product.getTitle());
        return "admin/product-details";
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
     * Страница со списком заказов
     * @param page номер страницы
     * @param size размер страницы
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/orders")
    public String getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Page<Order> orders = adminService.getAllOrders(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
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
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/categories")
    public String getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Page<Category> categories = adminService.getAllCategories(PageRequest.of(page, size, Sort.by("name")));
        
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categories.getTotalPages());
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
    
    //
    // Управление производителями
    //
    
    /**
     * Страница со списком производителей
     * @param page номер страницы
     * @param size размер страницы
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/manufacturers")
    public String getManufacturers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Page<Manufacturer> manufacturers = adminService.getAllManufacturers(PageRequest.of(page, size, Sort.by("name")));
        
        model.addAttribute("manufacturers", manufacturers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", manufacturers.getTotalPages());
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
} 