package ru.bek.compshp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.bek.compshp.model.Category;
import ru.bek.compshp.model.Manufacturer;
import ru.bek.compshp.model.Role;
import ru.bek.compshp.model.User;
import ru.bek.compshp.model.enums.RoleName;
import ru.bek.compshp.repository.CategoryRepository;
import ru.bek.compshp.repository.ManufacturerRepository;
import ru.bek.compshp.repository.RoleRepository;
import ru.bek.compshp.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Класс для инициализации тестовых данных и администратора
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Инициализируем все роли
        initRoles();
        
        // Создаем администратора если его еще нет
        createAdminIfNotExists();
        
        // Инициализируем базовые категории и производителей
        initBasicData();
    }
    
    /**
     * Инициализирует все роли в системе
     */
    private void initRoles() {
        for (RoleName roleName : RoleName.values()) {
            roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    Role savedRole = roleRepository.save(newRole);
                    System.out.println("Создана роль: " + roleName);
                    return savedRole;
                });
        }
    }

    /**
     * Создает администратора в базе данных, если его нет
     */
    private void createAdminIfNotExists() {
        // Получаем роль ADMIN из базы
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Роль ROLE_ADMIN не найдена"));

        // Проверяем, есть ли пользователь admin в базе
        Optional<User> adminUser = userRepository.findByUsername("admin");
        
        if (adminUser.isEmpty()) {
            User newAdmin = new User();
            newAdmin.setUsername("admin");
            newAdmin.setEmail("admin@pcmaker.com");
            newAdmin.setPassword(passwordEncoder.encode("admin"));
            newAdmin.setName("Администратор");
            newAdmin.setActive(true);
            newAdmin.setRoles(Set.of(adminRole));
            
            userRepository.save(newAdmin);
            
            System.out.println("Администратор создан: admin/admin");
        }
    }
    
    /**
     * Инициализирует базовые категории и производителей
     */
    private void initBasicData() {
        // Создаем базовые категории если их нет
        if (categoryRepository.count() == 0) {
            // Родительские категории
            Category pcComponents = createCategory("Компьютерные комплектующие", "Комплектующие для сборки ПК", null, true);
            Category peripherals = createCategory("Периферия", "Устройства ввода-вывода", null, false);
            Category laptops = createCategory("Ноутбуки", "Портативные компьютеры", null, false);
            
            // Подкатегории компьютерных комплектующих
            createCategory("Процессоры", "CPU для настольных компьютеров", pcComponents, true);
            createCategory("Видеокарты", "GPU для игр и работы", pcComponents, true);
            createCategory("Материнские платы", "Основа компьютера", pcComponents, true);
            createCategory("Оперативная память", "RAM модули", pcComponents, true);
            createCategory("Блоки питания", "Источники питания для ПК", pcComponents, true);
            createCategory("Корпуса", "Корпуса для компьютеров", pcComponents, true);
            createCategory("Охлаждение", "Системы охлаждения", pcComponents, true);
            createCategory("Накопители", "HDD, SSD и другие устройства хранения", pcComponents, true);
            
            // Подкатегории периферии
            createCategory("Мониторы", "Устройства отображения", peripherals, false);
            createCategory("Клавиатуры", "Устройства ввода", peripherals, false);
            createCategory("Мыши", "Манипуляторы", peripherals, false);
            createCategory("Наушники", "Аудио устройства", peripherals, false);
            
            System.out.println("Базовые категории созданы");
        }
        
        // Создаем базовых производителей если их нет
        if (manufacturerRepository.count() == 0) {
            List<String> manufacturers = List.of(
                "Intel", "AMD", "NVIDIA", "ASUS", "MSI", "Gigabyte", 
                "Corsair", "Kingston", "Crucial", "Samsung", "Western Digital",
                "Seagate", "Cooler Master", "NZXT", "be quiet!", "Thermaltake",
                "Logitech", "Razer", "SteelSeries", "HyperX", "EVGA", "Zotac",
                "Dell", "HP", "Lenovo", "ACER", "Apple"
            );
            
            for (String name : manufacturers) {
                createManufacturer(name, "Производитель компьютерной техники и комплектующих");
            }
            
            System.out.println("Базовые производители созданы");
        }
    }
    
    /**
     * Вспомогательный метод для создания категории
     */
    private Category createCategory(String name, String description, Category parent, boolean isPcComponent) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setParent(parent);
        category.setPcComponent(isPcComponent);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        // Генерируем slug из названия (заменяем пробелы на дефисы и приводим к нижнему регистру)
        category.setSlug(name.toLowerCase().replace(' ', '-'));
        
        return categoryRepository.save(category);
    }
    
    /**
     * Вспомогательный метод для создания производителя
     */
    private Manufacturer createManufacturer(String name, String description) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(name);
        manufacturer.setDescription(description);
        manufacturer.setCreatedAt(LocalDateTime.now());
        manufacturer.setUpdatedAt(LocalDateTime.now());
        
        return manufacturerRepository.save(manufacturer);
    }
} 