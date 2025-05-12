package ru.compshp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.dto.PCConfigurationDTO;
import ru.compshp.model.PCConfiguration;
import ru.compshp.model.Product;
import ru.compshp.model.User;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.repository.PCConfigurationRepository;
import ru.compshp.repository.ProductRepository;
import ru.compshp.repository.UserRepository;
import ru.compshp.util.SecurityUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для управления конфигурациями ПК
 * Основные функции:
 * - Создание и управление конфигурациями
 * - Добавление/удаление компонентов
 * - Проверка совместимости компонентов
 * - Расчет энергопотребления
 * - Управление сохраненными конфигурациями
 */
@Service
@RequiredArgsConstructor
public class ConfiguratorService {
    private final PCConfigurationRepository configRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CompatibilityService compatibilityService;
    private final ObjectMapper objectMapper;

    // Константы для валидации спецификаций
    private static final Map<ComponentType, Set<String>> REQUIRED_SPECS = Map.of(
        ComponentType.CPU, Set.of("socket", "tdp", "ram_type"),
        ComponentType.MOTHERBOARD, Set.of("socket", "formFactor", "ram_slots", "m2Slots"),
        ComponentType.GPU, Set.of("powerConsumption", "length", "pcie_version"),
        ComponentType.PSU, Set.of("wattage", "efficiency"),
        ComponentType.CASE, Set.of("formFactor", "maxGpuLength", "maxCpuCoolerHeight"),
        ComponentType.RAM, Set.of("type", "speed", "capacity"),
        ComponentType.STORAGE, Set.of("type", "capacity", "isM2")
    );

    public ResponseEntity<?> getAvailableComponents(String type, Long currentConfigId, Double maxPrice) {
        try {
            // Получаем текущую конфигурацию, если она есть
            PCConfiguration currentConfig = null;
            if (currentConfigId != null) {
                currentConfig = configRepository.findById(currentConfigId)
                    .orElseThrow(() -> new RuntimeException("Configuration not found"));
            }

            // Получаем все компоненты указанного типа
            List<Product> allComponents = productRepository.findByType(type);

            // Фильтруем по цене, если указана
            if (maxPrice != null) {
                allComponents = allComponents.stream()
                    .filter(p -> p.getPrice() <= maxPrice)
                    .collect(Collectors.toList());
            }

            // Если есть текущая конфигурация, проверяем совместимость
            if (currentConfig != null) {
                allComponents = allComponents.stream()
                    .filter(component -> isCompatibleWithCurrentConfig(component, currentConfig))
                    .collect(Collectors.toList());
            }

            return ResponseEntity.ok(allComponents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting available components: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> createConfiguration(String name, String description) {
        try {
            PCConfiguration config = new PCConfiguration();
            config.setName(name);
            config.setDescription(description);
            config.setUser(getCurrentUser());
            config = configRepository.save(config);
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating configuration: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> addComponent(Long configId, Long productId) {
        try {
            PCConfiguration config = configRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Configuration not found"));

            Product component = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Component not found"));

            // Проверяем совместимость перед добавлением
            if (!isCompatibleWithCurrentConfig(component, config)) {
                return ResponseEntity.badRequest().body("Component is not compatible with current configuration");
            }

            // Добавляем компонент в конфигурацию
            switch (component.getType()) {
                case "CPU" -> config.setCpu(component);
                case "MOTHERBOARD" -> config.setMotherboard(component);
                case "GPU" -> config.setGpu(component);
                case "PSU" -> config.setPsu(component);
                case "CASE" -> config.setPcCase(component);
                case "RAM" -> {
                    if (config.getRam() == null) {
                        config.setRam(new HashSet<>());
                    }
                    config.getRam().add(component);
                }
                case "STORAGE" -> {
                    if (config.getStorage() == null) {
                        config.setStorage(new HashSet<>());
                    }
                    config.getStorage().add(component);
                }
            }

            // Обновляем общую стоимость
            updateTotalPrice(config);
            
            // Обновляем энергопотребление
            updatePowerRequirement(config);

            config = configRepository.save(config);
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding component: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> removeComponent(Long configId, String componentType) {
        try {
            PCConfiguration config = configRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Configuration not found"));

            switch (componentType) {
                case "CPU" -> config.setCpu(null);
                case "MOTHERBOARD" -> config.setMotherboard(null);
                case "GPU" -> config.setGpu(null);
                case "PSU" -> config.setPsu(null);
                case "CASE" -> config.setPcCase(null);
                case "RAM" -> config.setRam(new HashSet<>());
                case "STORAGE" -> config.setStorage(new HashSet<>());
            }

            // Обновляем общую стоимость
            updateTotalPrice(config);
            
            // Обновляем энергопотребление
            updatePowerRequirement(config);

            config = configRepository.save(config);
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error removing component: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getConfiguration(Long configId) {
        try {
            PCConfiguration config = configRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Configuration not found"));
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting configuration: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getSavedConfigurations() {
        try {
            List<PCConfiguration> configs = configRepository.findByUser(getCurrentUser());
            return ResponseEntity.ok(configs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting saved configurations: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> deleteConfiguration(Long configId) {
        try {
            PCConfiguration config = configRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Configuration not found"));

            if (!config.getUser().equals(getCurrentUser())) {
                return ResponseEntity.badRequest().body("You don't have permission to delete this configuration");
            }

            configRepository.delete(config);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting configuration: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getCompatibilityInfo(Long configId) {
        try {
            PCConfiguration config = configRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Configuration not found"));

            Map<String, Object> compatibilityInfo = new HashMap<>();
            
            // Проверяем совместимость CPU и материнской платы
            if (config.getCpu() != null && config.getMotherboard() != null) {
                compatibilityInfo.put("cpu_motherboard", 
                    compatibilityService.checkCpuMoboCompatibility(config.getCpu(), config.getMotherboard()));
            }

            // Проверяем совместимость GPU и корпуса
            if (config.getGpu() != null && config.getPcCase() != null) {
                compatibilityInfo.put("gpu_case", 
                    compatibilityService.checkGpuCaseCompatibility(config.getGpu(), config.getPcCase()));
            }

            // Проверяем совместимость PSU и энергопотребления
            if (config.getPsu() != null && config.getCpu() != null && config.getGpu() != null) {
                compatibilityInfo.put("psu_power", 
                    compatibilityService.checkPsuPowerCompatibility(config.getPsu(), config.getCpu(), config.getGpu()));
            }

            return ResponseEntity.ok(compatibilityInfo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting compatibility info: " + e.getMessage());
        }
    }

    public ResponseEntity<?> calculatePowerRequirement(Long configId) {
        try {
            PCConfiguration config = configRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Configuration not found"));

            if (config.getCpu() == null || config.getGpu() == null) {
                return ResponseEntity.badRequest().body("CPU and GPU are required for power calculation");
            }

            Set<Product> additionalComponents = new HashSet<>();
            if (config.getRam() != null) additionalComponents.addAll(config.getRam());
            if (config.getStorage() != null) additionalComponents.addAll(config.getStorage());

            return compatibilityService.calculatePowerRequirement(
                config.getCpu(), 
                config.getGpu(), 
                additionalComponents
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error calculating power requirement: " + e.getMessage());
        }
    }

    private boolean isCompatibleWithCurrentConfig(Product component, PCConfiguration config) {
        try {
            switch (component.getType()) {
                case "CPU" -> {
                    if (config.getMotherboard() != null) {
                        return compatibilityService.checkCpuMoboCompatibility(component, config.getMotherboard());
                    }
                }
                case "MOTHERBOARD" -> {
                    if (config.getCpu() != null) {
                        return compatibilityService.checkCpuMoboCompatibility(config.getCpu(), component);
                    }
                }
                case "GPU" -> {
                    if (config.getPcCase() != null) {
                        return compatibilityService.checkGpuCaseCompatibility(component, config.getPcCase());
                    }
                }
                case "PSU" -> {
                    if (config.getCpu() != null && config.getGpu() != null) {
                        return compatibilityService.checkPsuPowerCompatibility(component, config.getCpu(), config.getGpu());
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void updateTotalPrice(PCConfiguration config) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        
        if (config.getCpu() != null) totalPrice = totalPrice.add(config.getCpu().getPrice());
        if (config.getMotherboard() != null) totalPrice = totalPrice.add(config.getMotherboard().getPrice());
        if (config.getGpu() != null) totalPrice = totalPrice.add(config.getGpu().getPrice());
        if (config.getPsu() != null) totalPrice = totalPrice.add(config.getPsu().getPrice());
        if (config.getPcCase() != null) totalPrice = totalPrice.add(config.getPcCase().getPrice());
        
        if (config.getRam() != null) {
            for (Product ram : config.getRam()) {
                totalPrice = totalPrice.add(ram.getPrice());
            }
        }
        
        if (config.getStorage() != null) {
            for (Product storage : config.getStorage()) {
                totalPrice = totalPrice.add(storage.getPrice());
            }
        }
        
        config.setTotalPrice(totalPrice);
    }

    private void updatePowerRequirement(PCConfiguration config) {
        if (config.getCpu() != null && config.getGpu() != null) {
            Set<Product> additionalComponents = new HashSet<>();
            if (config.getRam() != null) additionalComponents.addAll(config.getRam());
            if (config.getStorage() != null) additionalComponents.addAll(config.getStorage());

            ResponseEntity<?> powerResult = compatibilityService.calculatePowerRequirement(
                config.getCpu(), 
                config.getGpu(), 
                additionalComponents
            );

            if (powerResult.getBody() instanceof Number) {
                config.setPowerRequirement(((Number) powerResult.getBody()).intValue());
            }
        }
    }

    private User getCurrentUser() {
        return SecurityUtils.getCurrentUser();
    }

    public ResponseEntity<?> checkCompatibility(Long cpuId, Long motherboardId, Long gpuId, Long psuId, Long caseId) {
        Product cpu = productRepository.findById(cpuId)
            .orElseThrow(() -> new RuntimeException("CPU not found"));
        Product motherboard = productRepository.findById(motherboardId)
            .orElseThrow(() -> new RuntimeException("Motherboard not found"));
        Product gpu = productRepository.findById(gpuId)
            .orElseThrow(() -> new RuntimeException("GPU not found"));
        Product psu = productRepository.findById(psuId)
            .orElseThrow(() -> new RuntimeException("PSU not found"));
        Product case = productRepository.findById(caseId)
            .orElseThrow(() -> new RuntimeException("Case not found"));

        return compatibilityService.checkCompatibility(cpu, motherboard, gpu, psu, case);
    }

    public ResponseEntity<?> calculatePowerRequirement(Long cpuId, Long gpuId, Long[] additionalComponents) {
        Product cpu = productRepository.findById(cpuId)
            .orElseThrow(() -> new RuntimeException("CPU not found"));
        Product gpu = productRepository.findById(gpuId)
            .orElseThrow(() -> new RuntimeException("GPU not found"));

        Set<Product> additional = new HashSet<>();
        if (additionalComponents != null) {
            for (Long id : additionalComponents) {
                additional.add(productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Component not found: " + id)));
            }
        }

        return compatibilityService.calculatePowerRequirement(cpu, gpu, additional);
    }

    @Transactional
    public ResponseEntity<?> saveConfiguration(PCConfigurationDTO configDTO) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        PCConfiguration config = new PCConfiguration();
        config.setUser(user);
        config.setName(configDTO.getName());
        config.setDescription(configDTO.getDescription());

        config.setCpu(productRepository.findById(configDTO.getCpuId())
            .orElseThrow(() -> new RuntimeException("CPU not found")));
        config.setMotherboard(productRepository.findById(configDTO.getMotherboardId())
            .orElseThrow(() -> new RuntimeException("Motherboard not found")));
        config.setGpu(productRepository.findById(configDTO.getGpuId())
            .orElseThrow(() -> new RuntimeException("GPU not found")));
        config.setPsu(productRepository.findById(configDTO.getPsuId())
            .orElseThrow(() -> new RuntimeException("PSU not found")));
        config.setPcCase(productRepository.findById(configDTO.getCaseId())
            .orElseThrow(() -> new RuntimeException("Case not found")));

        Set<Product> ram = new HashSet<>();
        for (Long id : configDTO.getRamIds()) {
            ram.add(productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RAM not found: " + id)));
        }
        config.setRam(ram);

        Set<Product> storage = new HashSet<>();
        for (Long id : configDTO.getStorageIds()) {
            storage.add(productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Storage not found: " + id)));
        }
        config.setStorage(storage);

        // Проверка совместимости
        ResponseEntity<?> compatibilityResult = compatibilityService.checkCompatibility(
            config.getCpu(), config.getMotherboard(), config.getGpu(), config.getPsu(), config.getPcCase());
        config.setCompatible(compatibilityResult.getStatusCode().is2xxSuccessful());
        config.setCompatibilityNotes(compatibilityResult.getBody().toString());

        // Расчет общей стоимости
        updateTotalPrice(config);

        // Расчет энергопотребления
        updatePowerRequirement(config);

        config = configRepository.save(config);
        return ResponseEntity.ok(config);
    }

    public ResponseEntity<?> getSavedConfiguration(Long id) {
        PCConfiguration config = configRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Configuration not found"));

        if (!config.getUser().getUsername().equals(SecurityUtils.getCurrentUsername())) {
            return ResponseEntity.badRequest().body("You can only view your own configurations");
        }

        return ResponseEntity.ok(config);
    }

    @Transactional
    public ResponseEntity<?> deleteSavedConfiguration(Long id) {
        PCConfiguration config = configRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Configuration not found"));

        if (!config.getUser().getUsername().equals(SecurityUtils.getCurrentUsername())) {
            return ResponseEntity.badRequest().body("You can only delete your own configurations");
        }

        configRepository.delete(config);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> addConfigurationToCart(Long id) {
        PCConfiguration config = configRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Configuration not found"));

        if (!config.getUser().getUsername().equals(SecurityUtils.getCurrentUsername())) {
            return ResponseEntity.badRequest().body("You can only add your own configurations to cart");
        }

        // TODO: Implement cart service and add configuration to cart
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> getPopularConfigurations() {
        return ResponseEntity.ok(configRepository.findTop10ByOrderByCreatedAtDesc());
    }

    public ResponseEntity<?> getConfiguratorStatistics() {
        // TODO: Implement statistics calculation
        return ResponseEntity.ok().build();
    }
} 