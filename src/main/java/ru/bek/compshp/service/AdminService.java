package ru.bek.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.bek.compshp.dto.CategoryDto;
import ru.bek.compshp.dto.DashboardStatsDto;
import ru.bek.compshp.dto.ProductDto;
import ru.bek.compshp.dto.UserDto;
import ru.bek.compshp.model.*;
import ru.bek.compshp.model.enums.ComponentType;
import ru.bek.compshp.model.enums.OrderStatus;
import ru.bek.compshp.model.enums.RoleName;
import ru.bek.compshp.repository.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для администрирования системы
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PCConfigurationRepository pcConfigurationRepository;
    private final CategoryRepository categoryRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final PasswordEncoder passwordEncoder;

    // Константы для путей сохранения файлов
    private static final String UPLOAD_DIR = "uploads/";
    private static final String PRODUCT_IMAGES_DIR = UPLOAD_DIR + "products/";

    /**
     * Получает список всех пользователей с пагинацией
     * @param pageable параметры пагинации
     * @return страница пользователей
     */
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Получает список всех пользователей без пагинации
     * @return список всех пользователей
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Получает пользователя по ID
     * @param id ID пользователя
     * @return пользователь
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    /**
     * Обновляет статус активности пользователя
     * @param id ID пользователя
     * @param isActive новый статус
     * @return обновленный пользователь
     */
    @Transactional
    public User updateUserActiveStatus(Long id, boolean isActive) {
        User user = getUserById(id);
        user.setActive(isActive);
        return userRepository.save(user);
    }
    
    /**
     * Создает нового пользователя
     * @param userDto данные пользователя
     * @return созданный пользователь
     */
    @Transactional
    public User createUser(UserDto userDto) {
        // Проверка на существование пользователя
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        
        // Создание нового пользователя
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        
        // Установка ролей
        Set<Role> roles = new HashSet<>();
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            userDto.getRoles().forEach(roleName -> {
                Role role = roleRepository.findByName(RoleName.valueOf(roleName))
                    .orElseThrow(() -> new RuntimeException("Роль не найдена: " + roleName));
                roles.add(role);
            });
        } else {
            // По умолчанию - роль USER
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Роль ROLE_USER не найдена"));
            roles.add(userRole);
        }
        user.setRoles(roles);
        
        return userRepository.save(user);
    }
    
    /**
     * Обновляет данные пользователя
     * @param id ID пользователя
     * @param userDto новые данные пользователя
     * @return обновленный пользователь
     */
    @Transactional
    public User updateUser(Long id, UserDto userDto) {
        User user = getUserById(id);
        
        // Проверка на существование другого пользователя с таким же username или email
        if (!user.getUsername().equals(userDto.getUsername()) && 
            userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        
        if (!user.getEmail().equals(userDto.getEmail()) && 
            userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        
        // Обновление данных
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        
        // Обновление пароля, если он указан
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        // Обновление ролей, если они указаны
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            userDto.getRoles().forEach(roleName -> {
                Role role = roleRepository.findByName(RoleName.valueOf(roleName))
                    .orElseThrow(() -> new RuntimeException("Роль не найдена: " + roleName));
                roles.add(role);
            });
            user.setRoles(roles);
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Удаляет пользователя
     * @param id ID пользователя
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        
        // Проверка наличия заказов у пользователя
        List<Order> userOrders = orderRepository.findByUser(user);
        if (!userOrders.isEmpty()) {
            throw new RuntimeException("Невозможно удалить пользователя с заказами");
        }
        
        // Удаление конфигураций пользователя
        List<PCConfiguration> userConfigs = pcConfigurationRepository.findByUser(user);
        pcConfigurationRepository.deleteAll(userConfigs);
        
        // Удаление пользователя
        userRepository.delete(user);
    }

    /**
     * Получает список всех продуктов с пагинацией
     * @param pageable параметры пагинации
     * @return страница продуктов
     */
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /**
     * Получает продукт по ID
     * @param id ID продукта
     * @return продукт
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Продукт не найден"));
    }

    /**
     * Обновляет цену продукта
     * @param id ID продукта
     * @param price новая цена
     * @return обновленный продукт
     */
    @Transactional
    public Product updateProductPrice(Long id, BigDecimal price) {
        Product product = getProductById(id);
        product.setPrice(price);
        return productRepository.save(product);
    }

    /**
     * Обновляет количество продукта на складе
     * @param id ID продукта
     * @param stock новое количество
     * @return обновленный продукт
     */
    @Transactional
    public Product updateProductStock(Long id, Integer stock) {
        Product product = getProductById(id);
        product.setStock(stock);
        return productRepository.save(product);
    }
    
    /**
     * Создает новый продукт
     * @param productDto данные продукта
     * @param imageFile файл изображения продукта
     * @return созданный продукт
     */
    @Transactional
    public Product createProduct(ProductDto productDto, MultipartFile imageFile) throws IOException {
        Product product = new Product();
        
        // Устанавливаем основные данные продукта
        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        product.setIsActive(productDto.isActive());
        
        // Устанавливаем тип компонента (если есть)
        if (productDto.getComponentType() != null && !productDto.getComponentType().isEmpty()) {
            product.setComponentType(ComponentType.valueOf(productDto.getComponentType()));
        }
        
        // Устанавливаем производителя
        Manufacturer manufacturer = manufacturerRepository.findById(productDto.getManufacturerId())
                .orElseThrow(() -> new RuntimeException("Производитель не найден"));
        product.setManufacturer(manufacturer);
        
        // Устанавливаем категорию
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
        product.setCategory(category);
        
        // Устанавливаем спецификации
        if (productDto.getSpecs() != null) {
            product.setSpecs(productDto.getSpecs());
        }
        
        // Обрабатываем загрузку изображения (если есть)
        if (imageFile != null && !imageFile.isEmpty()) {
            // Создаем директорию, если она не существует
            Path uploadPath = Paths.get(PRODUCT_IMAGES_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Генерируем уникальное имя файла
            String fileName = UUID.randomUUID() + "-" + imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            
            // Сохраняем файл
            Files.copy(imageFile.getInputStream(), filePath);
            
            // Устанавливаем путь к изображению с начальным слешем
            product.setImagePath("/uploads/products/" + fileName);
        }
        
        // Устанавливаем время создания и обновления
        LocalDateTime now = LocalDateTime.now();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        
        return productRepository.save(product);
    }
    
    /**
     * Обновляет данные продукта
     * @param id ID продукта
     * @param productDto новые данные продукта
     * @param imageFile новый файл изображения продукта
     * @return обновленный продукт
     */
    @Transactional
    public Product updateProduct(Long id, ProductDto productDto, MultipartFile imageFile) throws IOException {
        Product product = getProductById(id);
        
        // Обновляем основные данные продукта
        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        product.setIsActive(productDto.isActive());
        
        // Обновляем тип компонента (если есть)
        if (productDto.getComponentType() != null && !productDto.getComponentType().isEmpty()) {
            product.setComponentType(ComponentType.valueOf(productDto.getComponentType()));
        } else {
            product.setComponentType(null);
        }
        
        // Обновляем производителя
        Manufacturer manufacturer = manufacturerRepository.findById(productDto.getManufacturerId())
                .orElseThrow(() -> new RuntimeException("Производитель не найден"));
        product.setManufacturer(manufacturer);
        
        // Обновляем категорию
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
        product.setCategory(category);
        
        // Обновляем спецификации
        if (productDto.getSpecs() != null) {
            product.setSpecs(productDto.getSpecs());
        }
        
        // Обрабатываем загрузку нового изображения (если есть)
        if (imageFile != null && !imageFile.isEmpty()) {
            // Создаем директорию, если она не существует
            Path uploadPath = Paths.get(PRODUCT_IMAGES_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Генерируем уникальное имя файла
            String fileName = UUID.randomUUID() + "-" + imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            
            // Сохраняем файл
            Files.copy(imageFile.getInputStream(), filePath);
            
            // Удаляем старое изображение, если оно есть
            deleteOldProductImage(product);
            
            // Обновляем путь к изображению с начальным слешем
            product.setImagePath("/uploads/products/" + fileName);
        }
        
        // Обновляем время изменения
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
    
    /**
     * Изменяет статус активности продукта
     * @param id ID продукта
     * @param isActive новый статус
     * @return обновленный продукт
     */
    @Transactional
    public Product updateProductActiveStatus(Long id, boolean isActive) {
        Product product = getProductById(id);
        product.setIsActive(isActive);
        return productRepository.save(product);
    }
    
    /**
     * Удаляет продукт
     * @param id ID продукта
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        
        // Проверка наличия продукта в конфигурациях и заказах
        // Реализация зависит от структуры вашей базы данных
        
        // Удаление изображения
        if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
            try {
                String fileName = product.getImagePath().substring(product.getImagePath().lastIndexOf("/") + 1);
                Path filePath = Paths.get(PRODUCT_IMAGES_DIR).resolve(fileName);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Логирование ошибки
                System.err.println("Ошибка при удалении изображения: " + e.getMessage());
            }
        }
        
        // Удаление продукта
        productRepository.delete(product);
    }

    /**
     * Получает список заказов с пагинацией
     * @param pageable параметры пагинации
     * @return страница заказов
     */
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    /**
     * Получает заказ по ID
     * @param id ID заказа
     * @return заказ
     */
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
    }

    /**
     * Обновляет статус заказа
     * @param id ID заказа
     * @param status новый статус
     * @return обновленный заказ
     */
    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        // Добавляем запись в историю статусов
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setChangedAt(LocalDateTime.now());
        order.getStatusHistory().add(history);
        
        return orderRepository.save(order);
    }
    
    /**
     * Сохраняет заказ
     * @param order заказ для сохранения
     * @return сохраненный заказ
     */
    @Transactional
    public Order saveOrder(Order order) {
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(LocalDateTime.now());
        }
        order.setUpdatedAt(LocalDateTime.now());
        
        // Если статус изменился, добавляем запись в историю
        if (order.getId() != null) {
            Order existingOrder = getOrderById(order.getId());
            if (existingOrder.getStatus() != order.getStatus()) {
                OrderStatusHistory history = new OrderStatusHistory();
                history.setOrder(order);
                history.setStatus(order.getStatus());
                history.setChangedAt(LocalDateTime.now());
                order.getStatusHistory().add(history);
            }
        } else {
            // Новый заказ - добавляем первую запись в историю статусов
            OrderStatusHistory history = new OrderStatusHistory();
            history.setOrder(order);
            history.setStatus(order.getStatus());
            history.setChangedAt(LocalDateTime.now());
            order.getStatusHistory().add(history);
        }
        
        return orderRepository.save(order);
    }
    
    /**
     * Получает список категорий с пагинацией
     * @param pageable параметры пагинации
     * @return страница категорий
     */
    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
    
    /**
     * Получает категорию по ID
     * @param id ID категории
     * @return категория
     */
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
    }
    
    /**
     * Создает новую категорию
     * @param categoryDto данные категории
     * @return созданная категория
     */
    @Transactional
    public Category createCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        
        // Установка родительской категории
        if (categoryDto.getParentId() != null) {
            Category parentCategory = getCategoryById(categoryDto.getParentId());
            category.setParent(parentCategory);
        }
        
        // Установка времени создания и обновления
        LocalDateTime now = LocalDateTime.now();
        category.setCreatedAt(now);
        category.setUpdatedAt(now);
        
        return categoryRepository.save(category);
    }
    
    /**
     * Обновляет данные категории
     * @param id ID категории
     * @param categoryDto новые данные категории
     * @return обновленная категория
     */
    @Transactional
    public Category updateCategory(Long id, CategoryDto categoryDto) {
        Category category = getCategoryById(id);
        
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        
        // Обновление родительской категории
        if (categoryDto.getParentId() != null) {
            // Проверка, что родительская категория не является текущей или ее дочерней категорией
            if (categoryDto.getParentId().equals(id)) {
                throw new RuntimeException("Категория не может быть родительской для самой себя");
            }
            
            Category parentCategory = getCategoryById(categoryDto.getParentId());
            category.setParent(parentCategory);
        } else {
            category.setParent(null);
        }
        
        // Обновление времени изменения
        category.setUpdatedAt(LocalDateTime.now());
        
        return categoryRepository.save(category);
    }
    
    /**
     * Удаляет категорию
     * @param id ID категории
     */
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        
        // Проверка наличия дочерних категорий
        List<Category> childCategories = categoryRepository.findByParent(category);
        if (!childCategories.isEmpty()) {
            throw new RuntimeException("Невозможно удалить категорию с дочерними категориями");
        }
        
        // Проверка наличия продуктов в категории
        List<Product> productsInCategory = productRepository.findByCategoryId(id);
        if (!productsInCategory.isEmpty()) {
            throw new RuntimeException("Невозможно удалить категорию, содержащую продукты");
        }
        
        // Удаление категории
        categoryRepository.delete(category);
    }
    
    /**
     * Получает список производителей с пагинацией
     * @param pageable параметры пагинации
     * @return страница производителей
     */
    public Page<Manufacturer> getAllManufacturers(Pageable pageable) {
        return manufacturerRepository.findAll(pageable);
    }
    
    /**
     * Получает производителя по ID
     * @param id ID производителя
     * @return производитель
     */
    public Manufacturer getManufacturerById(Long id) {
        return manufacturerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Производитель не найден"));
    }
    
    /**
     * Создает нового производителя
     * @param name имя производителя
     * @param description описание производителя
     * @return созданный производитель
     */
    @Transactional
    public Manufacturer createManufacturer(String name, String description) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(name);
        manufacturer.setDescription(description);
        
        // Установка времени создания и обновления
        LocalDateTime now = LocalDateTime.now();
        manufacturer.setCreatedAt(now);
        manufacturer.setUpdatedAt(now);
        
        return manufacturerRepository.save(manufacturer);
    }
    
    /**
     * Обновляет данные производителя
     * @param id ID производителя
     * @param name новое имя производителя
     * @param description новое описание производителя
     * @return обновленный производитель
     */
    @Transactional
    public Manufacturer updateManufacturer(Long id, String name, String description) {
        Manufacturer manufacturer = getManufacturerById(id);
        manufacturer.setName(name);
        manufacturer.setDescription(description);
        
        // Обновление времени изменения
        manufacturer.setUpdatedAt(LocalDateTime.now());
        
        return manufacturerRepository.save(manufacturer);
    }
    
    /**
     * Удаляет производителя
     * @param id ID производителя
     */
    @Transactional
    public void deleteManufacturer(Long id) {
        Manufacturer manufacturer = getManufacturerById(id);
        
        // Проверка наличия продуктов производителя
        List<Product> productsOfManufacturer = productRepository.findByManufacturerId(id);
        if (!productsOfManufacturer.isEmpty()) {
            throw new RuntimeException("Невозможно удалить производителя с продуктами");
        }
        
        // Удаление производителя
        manufacturerRepository.delete(manufacturer);
    }

    /**
     * Получает статистику для дашборда
     * @return объект статистики
     */
    public DashboardStatsDto getDashboardStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthAgo = now.minus(30, ChronoUnit.DAYS);
        
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        long totalConfigurations = pcConfigurationRepository.count();
        
        long newUsersThisMonth = userRepository.countByCreatedAtAfter(monthAgo);
        long newOrdersThisMonth = orderRepository.countByCreatedAtAfter(monthAgo);
        
        List<Order> recentOrders = orderRepository.findTop5ByOrderByCreatedAtDesc();
        List<Product> lowStockProducts = productRepository.findByStockLessThan(5);
        
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();
        BigDecimal revenueThisMonth = orderRepository.calculateRevenueAfterDate(monthAgo);
        
        Map<String, Long> ordersByStatus = getOrderCountByStatus();
        
        return DashboardStatsDto.builder()
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .totalOrders(totalOrders)
                .totalConfigurations(totalConfigurations)
                .newUsersThisMonth(newUsersThisMonth)
                .newOrdersThisMonth(newOrdersThisMonth)
                .recentOrders(recentOrders)
                .lowStockProducts(lowStockProducts)
                .totalRevenue(totalRevenue)
                .revenueThisMonth(revenueThisMonth)
                .ordersByStatus(ordersByStatus)
                .build();
    }
    
    /**
     * Получает количество заказов по статусам
     * @return карта статус -> количество
     */
    private Map<String, Long> getOrderCountByStatus() {
        List<Order> allOrders = orderRepository.findAll();
        Map<String, Long> result = new HashMap<>();
        
        for (Order order : allOrders) {
            String status = order.getStatus().toString();
            result.put(status, result.getOrDefault(status, 0L) + 1);
        }
        
        return result;
    }

    /**
     * Получает список пользователей с фильтрацией
     * @param pageable параметры пагинации
     * @param search поисковый запрос (имя, email, username)
     * @param status статус активности
     * @param role роль пользователя
     * @return отфильтрованный список пользователей
     */
    public Page<User> getUsersWithFilters(Pageable pageable, String search, String status, String role) {
        // Здесь нужно реализовать фильтрацию через Specification или кастомный репозиторий
        // Временная реализация - возвращаем всех пользователей
        // TODO: Реализовать полноценную фильтрацию
        return userRepository.findAll(pageable);
    }

    /**
     * Получает список продуктов с фильтрацией
     * @param pageable параметры пагинации
     * @param search поисковый запрос (название, описание)
     * @param componentType тип компонента
     * @return отфильтрованный список продуктов
     */
    public Page<Product> getProductsWithFilters(Pageable pageable, String search, String componentType) {
        if (search != null && !search.isEmpty() && componentType != null && !componentType.isEmpty()) {
            try {
                ComponentType type = ComponentType.valueOf(componentType);
                return productRepository.findByTitleOrDescriptionContainingAndComponentType(search, type, pageable);
            } catch (IllegalArgumentException e) {
                // Если тип компонента не найден, игнорируем его и ищем только по поисковому запросу
                return productRepository.findByTitleOrDescriptionContaining(search, pageable);
            }
        } else if (search != null && !search.isEmpty()) {
            return productRepository.findByTitleOrDescriptionContaining(search, pageable);
        } else if (componentType != null && !componentType.isEmpty()) {
            try {
                ComponentType type = ComponentType.valueOf(componentType);
                return productRepository.findByComponentType(type, pageable);
            } catch (IllegalArgumentException e) {
                // Если тип компонента не найден, возвращаем все продукты
                return productRepository.findAll(pageable);
            }
        } else {
            return productRepository.findAll(pageable);
        }
    }

    /**
     * Получает список заказов с фильтрацией
     * @param pageable параметры пагинации
     * @param status статус заказа
     * @param search поисковый запрос (номер заказа, имя пользователя)
     * @return отфильтрованный список заказов
     */
    public Page<Order> getOrdersWithFilters(Pageable pageable, OrderStatus status, String search) {
        // Здесь нужно реализовать фильтрацию через Specification или кастомный репозиторий
        // Временная реализация - возвращаем все заказы
        // TODO: Реализовать полноценную фильтрацию
        return orderRepository.findAll(pageable);
    }

    /**
     * Получает список категорий с фильтрацией
     * @param pageable параметры пагинации
     * @param search поисковый запрос (название категории)
     * @return отфильтрованный список категорий
     */
    public Page<Category> getCategoriesWithFilters(Pageable pageable, String search) {
        // Здесь нужно реализовать фильтрацию через Specification или кастомный репозиторий
        // Временная реализация - возвращаем все категории
        // TODO: Реализовать полноценную фильтрацию
        return categoryRepository.findAll(pageable);
    }

    /**
     * Получает список производителей с фильтрацией
     * @param pageable параметры пагинации
     * @param search поисковый запрос (название производителя)
     * @return отфильтрованный список производителей
     */
    public Page<Manufacturer> getManufacturersWithFilters(Pageable pageable, String search) {
        // Здесь нужно реализовать фильтрацию через Specification или кастомный репозиторий
        // Временная реализация - возвращаем всех производителей
        // TODO: Реализовать полноценную фильтрацию
        return manufacturerRepository.findAll(pageable);
    }

    /**
     * Обновляет изображение продукта
     * @param id ID продукта
     * @param imageFile новый файл изображения
     * @return обновленный продукт
     * @throws IOException при ошибке загрузки файла
     */
    @Transactional
    public Product updateProductImage(Long id, MultipartFile imageFile) throws IOException {
        Product product = getProductById(id);
        
        // Сохраняем текущие спецификации
        Map<String, String> currentSpecs = product.getSpecs();
        
        if (imageFile != null && !imageFile.isEmpty()) {
            // Создаем директорию, если она не существует
            Path uploadPath = Paths.get(PRODUCT_IMAGES_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Генерируем уникальное имя файла
            String fileName = UUID.randomUUID() + "-" + imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            
            // Сохраняем файл
            Files.copy(imageFile.getInputStream(), filePath);
            
            // Удаляем старое изображение, если оно есть
            if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
                deleteOldProductImage(product);
            }
            
            // Обновляем путь к изображению с начальным слешем
            product.setImagePath("/uploads/products/" + fileName);
        }
        
        // Явно устанавливаем спецификации обратно
        product.setSpecs(currentSpecs);
        
        // Обновляем время изменения
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }

    /**
     * Экспортирует список пользователей в CSV
     * @param search поисковый запрос
     * @param status статус активности
     * @param roleFilter роль пользователя
     * @throws IOException при ошибке создания файла
     */
    public void exportUsersToCSV(String search, String status, String roleFilter) throws IOException {
        // Получаем всех пользователей с фильтрацией
        List<User> users = userRepository.findAll();
        
        // Создаем директорию для экспорта, если её нет
        Path exportDir = Paths.get(UPLOAD_DIR + "export/");
        if (!Files.exists(exportDir)) {
            Files.createDirectories(exportDir);
        }
        
        // Создаем CSV файл
        String fileName = "users_export_" + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString().replace(":", "-") + ".csv";
        Path filePath = exportDir.resolve(fileName);
        
        // Записываем данные в CSV
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Имя пользователя,Email,Имя,Активность,Роли,Дата регистрации\n");
        
        for (User user : users) {
            csv.append(user.getId()).append(",");
            csv.append(user.getUsername()).append(",");
            csv.append(user.getEmail()).append(",");
            csv.append(user.getName()).append(",");
            csv.append(user.getActive()).append(",");
            csv.append(user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.joining(";"))).append(",");
            csv.append(user.getCreatedAt()).append("\n");
        }
        
        Files.write(filePath, csv.toString().getBytes());
    }
    
    /**
     * Экспортирует список продуктов в CSV
     * @param search поисковый запрос
     * @param componentType тип компонента
     * @throws IOException при ошибке создания файла
     */
    public void exportProductsToCSV(String search, String componentType) throws IOException {
        // Получаем все продукты с фильтрацией
        List<Product> products = productRepository.findAll();
        
        // Создаем директорию для экспорта, если её нет
        Path exportDir = Paths.get(UPLOAD_DIR + "export/");
        if (!Files.exists(exportDir)) {
            Files.createDirectories(exportDir);
        }
        
        // Создаем CSV файл
        String fileName = "products_export_" + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString().replace(":", "-") + ".csv";
        Path filePath = exportDir.resolve(fileName);
        
        // Записываем данные в CSV
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Название,Цена,Количество,Тип компонента,Производитель,Категория,Активность\n");
        
        for (Product product : products) {
            csv.append(product.getId()).append(",");
            csv.append(product.getTitle()).append(",");
            csv.append(product.getPrice()).append(",");
            csv.append(product.getStock()).append(",");
            csv.append(product.getComponentType() != null ? product.getComponentType().name() : "").append(",");
            csv.append(product.getManufacturer() != null ? product.getManufacturer().getName() : "").append(",");
            csv.append(product.getCategory() != null ? product.getCategory().getName() : "").append(",");
            csv.append(product.getIsActive()).append("\n");
        }
        
        Files.write(filePath, csv.toString().getBytes());
    }
    
    /**
     * Экспортирует список заказов в CSV
     * @param status статус заказа
     * @param search поисковый запрос
     * @throws IOException при ошибке создания файла
     */
    public void exportOrdersToCSV(OrderStatus status, String search) throws IOException {
        // Получаем все заказы с фильтрацией
        List<Order> orders = orderRepository.findAll();
        
        // Создаем директорию для экспорта, если её нет
        Path exportDir = Paths.get(UPLOAD_DIR + "export/");
        if (!Files.exists(exportDir)) {
            Files.createDirectories(exportDir);
        }
        
        // Создаем CSV файл
        String fileName = "orders_export_" + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString().replace(":", "-") + ".csv";
        Path filePath = exportDir.resolve(fileName);
        
        // Записываем данные в CSV
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Пользователь,Статус,Сумма,Дата создания,Дата обновления\n");
        
        for (Order order : orders) {
            csv.append(order.getId()).append(",");
            csv.append(order.getUser() != null ? order.getUser().getUsername() : "").append(",");
            csv.append(order.getStatus().name()).append(",");
            csv.append(order.getTotalPrice()).append(",");
            csv.append(order.getCreatedAt()).append(",");
            csv.append(order.getUpdatedAt()).append("\n");
        }
        
        Files.write(filePath, csv.toString().getBytes());
    }

    /**
     * Импортирует продукты из CSV файла
     * @param csvFile файл CSV с данными продуктов
     * @return количество импортированных продуктов
     * @throws IOException при ошибке чтения файла
     */
    @Transactional
    public int importProductsFromCSV(MultipartFile csvFile) throws IOException {
        List<String> lines = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))
                .lines()
                .collect(Collectors.toList());
        
        // Пропускаем заголовок
        if (lines.isEmpty()) {
            return 0;
        }
        
        int importedCount = 0;
        boolean isFirstLine = true;
        
        for (String line : lines) {
            if (isFirstLine) {
                isFirstLine = false;
                continue; // Пропускаем заголовок
            }
            
            String[] values = line.split(",");
            if (values.length < 7) {
                continue; // Некорректная строка
            }
            
            try {
                String title = values[0].trim();
                BigDecimal price = new BigDecimal(values[1].trim());
                int stock = Integer.parseInt(values[2].trim());
                String componentTypeName = values[3].trim();
                String manufacturerName = values[4].trim();
                String categoryName = values[5].trim();
                String description = values.length > 6 ? values[6].trim() : "";
                
                // Поиск или создание производителя
                Manufacturer manufacturer = null;
                if (!manufacturerName.isEmpty()) {
                    manufacturer = manufacturerRepository.findByName(manufacturerName)
                            .orElseGet(() -> {
                                Manufacturer newManufacturer = new Manufacturer();
                                newManufacturer.setName(manufacturerName);
                                newManufacturer.setDescription("Импортировано");
                                return manufacturerRepository.save(newManufacturer);
                            });
                }
                
                // Поиск или создание категории
                Category category = null;
                if (!categoryName.isEmpty()) {
                    category = categoryRepository.findByName(categoryName)
                            .orElseGet(() -> {
                                Category newCategory = new Category();
                                newCategory.setName(categoryName);
                                newCategory.setDescription("Импортировано");
                                return categoryRepository.save(newCategory);
                            });
                }
                
                // Создание продукта
                Product product = new Product();
                product.setTitle(title);
                product.setDescription(description);
                product.setPrice(price);
                product.setStock(stock);
                
                if (!componentTypeName.isEmpty()) {
                    try {
                        ComponentType componentType = ComponentType.valueOf(componentTypeName);
                        product.setComponentType(componentType);
                    } catch (IllegalArgumentException e) {
                        // Игнорируем неверный тип компонента
                    }
                }
                
                product.setManufacturer(manufacturer);
                product.setCategory(category);
                product.setIsActive(true);
                product.setCreatedAt(LocalDateTime.now());
                
                productRepository.save(product);
                importedCount++;
                
            } catch (Exception e) {
                // Пропускаем строку с ошибкой
                System.err.println("Ошибка при импорте строки: " + line + " - " + e.getMessage());
            }
        }
        
        return importedCount;
    }

    /**
     * Удаляет изображение продукта
     * @param id ID продукта
     * @return обновленный продукт
     * @throws IOException при ошибке удаления файла
     */
    @Transactional
    public Product deleteProductImage(Long id) throws IOException {
        Product product = getProductById(id);
        
        if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
            // Извлекаем имя файла из пути, игнорируя начальный слеш если он есть
            String imagePath = product.getImagePath();
            if (imagePath.startsWith("/")) {
                imagePath = imagePath.substring(1);
            }
            
            // Получаем имя файла из пути
            String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            Path filePath = Paths.get(PRODUCT_IMAGES_DIR).resolve(fileName);
            
            // Удаляем файл
            Files.deleteIfExists(filePath);
            
            // Очищаем путь к изображению
            product.setImagePath(null);
            product = productRepository.save(product);
        }
        
        return product;
    }

    private void deleteOldProductImage(Product product) throws IOException {
        if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
            // Извлекаем имя файла из пути, игнорируя начальный слеш если он есть
            String imagePath = product.getImagePath();
            if (imagePath.startsWith("/")) {
                imagePath = imagePath.substring(1);
            }
            
            // Получаем имя файла из пути
            String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            Path filePath = Paths.get(PRODUCT_IMAGES_DIR).resolve(fileName);
            
            // Удаляем файл
            Files.deleteIfExists(filePath);
        }
    }
} 