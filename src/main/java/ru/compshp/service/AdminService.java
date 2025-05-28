package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.compshp.dto.CategoryDto;
import ru.compshp.dto.DashboardStatsDto;
import ru.compshp.dto.ProductDto;
import ru.compshp.dto.UserDto;
import ru.compshp.model.*;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.model.enums.OrderStatus;
import ru.compshp.model.enums.RoleName;
import ru.compshp.repository.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
        
        // Заполнение данных продукта
        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        product.setIsActive(true);
        
        // Установка типа компонента
        if (productDto.getComponentType() != null && !productDto.getComponentType().isEmpty()) {
            product.setComponentType(ComponentType.valueOf(productDto.getComponentType()));
        }
        
        // Установка производителя
        if (productDto.getManufacturerId() != null) {
            Manufacturer manufacturer = manufacturerRepository.findById(productDto.getManufacturerId())
                .orElseThrow(() -> new RuntimeException("Производитель не найден"));
            product.setManufacturer(manufacturer);
        }
        
        // Установка категории
        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
            product.setCategory(category);
        }
        
        // Сохранение спецификаций
        if (productDto.getSpecs() != null) {
            product.setSpecs(productDto.getSpecs());
        } else {
            product.setSpecs(new HashMap<>());
        }
        
        // Сохранение продукта для получения ID
        product = productRepository.save(product);
        
        // Обработка изображения
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = product.getId() + "_" + imageFile.getOriginalFilename();
            // Создание директории, если она не существует
            Path uploadPath = Paths.get(PRODUCT_IMAGES_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Сохранение файла
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath);
            
            // Обновление пути к изображению
            product.setImagePath("/uploads/products/" + fileName);
            product = productRepository.save(product);
        }
        
        return product;
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
        
        // Обновление данных продукта
        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        
        // Обновление типа компонента
        if (productDto.getComponentType() != null && !productDto.getComponentType().isEmpty()) {
            product.setComponentType(ComponentType.valueOf(productDto.getComponentType()));
        }
        
        // Обновление производителя
        if (productDto.getManufacturerId() != null) {
            Manufacturer manufacturer = manufacturerRepository.findById(productDto.getManufacturerId())
                .orElseThrow(() -> new RuntimeException("Производитель не найден"));
            product.setManufacturer(manufacturer);
        }
        
        // Обновление категории
        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
            product.setCategory(category);
        }
        
        // Обновление спецификаций
        if (productDto.getSpecs() != null) {
            product.setSpecs(productDto.getSpecs());
        }
        
        // Обработка нового изображения
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = product.getId() + "_" + imageFile.getOriginalFilename();
            // Создание директории, если она не существует
            Path uploadPath = Paths.get(PRODUCT_IMAGES_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Удаление старого изображения, если оно есть
            if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
                String oldFileName = product.getImagePath().substring(product.getImagePath().lastIndexOf("/") + 1);
                Path oldFilePath = uploadPath.resolve(oldFileName);
                Files.deleteIfExists(oldFilePath);
            }
            
            // Сохранение нового файла
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath);
            
            // Обновление пути к изображению
            product.setImagePath("/uploads/products/" + fileName);
        }
        
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
} 