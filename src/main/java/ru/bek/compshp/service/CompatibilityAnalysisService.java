package ru.bek.compshp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bek.compshp.dto.CompatibilityAnalysisResult;
import ru.bek.compshp.dto.CompatibilityIssue;
import ru.bek.compshp.dto.CompatibilityIssue.IssueCategory;
import ru.bek.compshp.dto.CompatibilityIssue.IssueType;
import ru.bek.compshp.model.enums.ComponentType;
import ru.bek.compshp.model.ConfigComponent;
import ru.bek.compshp.model.Product;
import ru.bek.compshp.repository.ConfigComponentRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для детального анализа совместимости конфигурации
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompatibilityAnalysisService {
    
    private final ComponentCompatibilityService compatibilityService;
    private final ConfigComponentRepository configComponentRepository;
    
    /**
     * Выполняет полный анализ совместимости конфигурации
     * @param configId ID конфигурации
     * @return детальный результат анализа
     */
    public CompatibilityAnalysisResult analyzeConfiguration(Long configId) {
        List<ConfigComponent> components = configComponentRepository.findByConfigId(configId);
        List<Product> products = components.stream()
                .map(ConfigComponent::getProduct)
                .collect(Collectors.toList());
        
        return analyzeConfiguration(products);
    }
    
    /**
     * Выполняет полный анализ совместимости списка компонентов
     * @param components список компонентов
     * @return детальный результат анализа
     */
    public CompatibilityAnalysisResult analyzeConfiguration(List<Product> components) {
        log.info("Начинаем детальный анализ конфигурации из {} компонентов", components.size());
        
        CompatibilityAnalysisResult result = new CompatibilityAnalysisResult(true);
        
        try {
            if (components.isEmpty()) {
                result.addIssue(new CompatibilityIssue(
                    IssueType.CRITICAL_ERROR,
                    IssueCategory.MISSING_COMPONENT,
                    "Пустая конфигурация",
                    "Конфигурация не содержит ни одного компонента"
                ));
                return result;
            }

            // 1. Проверка обязательных компонентов
            try {
                checkRequiredComponents(components, result);
            } catch (Exception e) {
                log.error("Ошибка при проверке обязательных компонентов", e);
                result.addIssue(new CompatibilityIssue(
                    IssueType.WARNING,
                    IssueCategory.MISSING_COMPONENT,
                    "Ошибка проверки обязательных компонентов",
                    "Не удалось проверить наличие всех обязательных компонентов"
                ));
            }

            // 2. Проверка совместимости пар компонентов
            try {
                checkPairwiseCompatibility(components, result);
            } catch (Exception e) {
                log.error("Ошибка при проверке совместимости компонентов", e);
                result.addIssue(new CompatibilityIssue(
                    IssueType.WARNING,
                    IssueCategory.COMPATIBILITY,
                    "Ошибка проверки совместимости",
                    "Не удалось проверить совместимость всех компонентов"
                ));
            }

            // 3. Анализ энергопотребления
            try {
                analyzePowerConsumption(components, result);
            } catch (Exception e) {
                log.error("Ошибка при анализе энергопотребления", e);
                result.addIssue(new CompatibilityIssue(
                    IssueType.WARNING,
                    IssueCategory.POWER,
                    "Ошибка анализа энергопотребления",
                    "Не удалось проанализировать энергопотребление конфигурации"
                ));
            }

            // 4. Анализ баланса производительности
            try {
                analyzePerformanceBalance(components, result);
            } catch (Exception e) {
                log.error("Ошибка при анализе баланса производительности", e);
                result.addIssue(new CompatibilityIssue(
                    IssueType.WARNING,
                    IssueCategory.BALANCE,
                    "Ошибка анализа баланса",
                    "Не удалось проанализировать баланс производительности"
                ));
            }

            // 5. Анализ тепловыделения
            try {
                analyzeThermalDesign(components, result);
            } catch (Exception e) {
                log.error("Ошибка при анализе тепловыделения", e);
                result.addIssue(new CompatibilityIssue(
                    IssueType.WARNING,
                    IssueCategory.THERMAL,
                    "Ошибка анализа охлаждения",
                    "Не удалось проанализировать систему охлаждения"
                ));
            }

            // 6. Анализ физических размеров
            try {
                analyzePhysicalDimensions(components, result);
            } catch (Exception e) {
                log.error("Ошибка при анализе физических размеров", e);
                result.addIssue(new CompatibilityIssue(
                    IssueType.WARNING,
                    IssueCategory.PHYSICAL,
                    "Ошибка анализа размеров",
                    "Не удалось проанализировать физические размеры компонентов"
                ));
            }

            // 7. Генерация рекомендаций по оптимизации
            try {
                generateOptimizationRecommendations(components, result);
            } catch (Exception e) {
                log.error("Ошибка при генерации рекомендаций", e);
                // Не добавляем проблему для рекомендаций, так как это не критично
            }

            log.info("Анализ завершен. Найдено: {} критических ошибок, {} предупреждений, {} рекомендаций",
                    result.getCriticalErrorCount(), result.getWarningCount(), result.getRecommendationCount());

        } catch (Exception e) {
            log.error("Критическая ошибка при анализе конфигурации", e);
            result = new CompatibilityAnalysisResult(false);
            result.addIssue(new CompatibilityIssue(
                IssueType.CRITICAL_ERROR,
                IssueCategory.COMPATIBILITY,
                "Ошибка анализа конфигурации",
                "Произошла ошибка при анализе совместимости: " + e.getMessage()
            ));
        }
        
        return result;
    }
    
    /**
     * Проверка наличия обязательных компонентов
     */
    private void checkRequiredComponents(List<Product> components, CompatibilityAnalysisResult result) {
        Set<ComponentType> presentTypes = components.stream()
                .map(Product::getComponentType)
                .collect(Collectors.toSet());
        
        for (ComponentType requiredType : ComponentType.values()) {
            if (requiredType.isRequired() && !presentTypes.contains(requiredType)) {
                result.addIssue(new CompatibilityIssue(
                    IssueType.CRITICAL_ERROR,
                    IssueCategory.MISSING_COMPONENT,
                    "Отсутствует обязательный компонент",
                    "Отсутствует обязательный компонент: " + requiredType.getDisplayName(),
                    null, null
                ));
            }
        }
    }
    
    /**
     * Проверка совместимости пар компонентов
     */
    private void checkPairwiseCompatibility(List<Product> components, CompatibilityAnalysisResult result) {
        for (int i = 0; i < components.size(); i++) {
            for (int j = i + 1; j < components.size(); j++) {
                Product product1 = components.get(i);
                Product product2 = components.get(j);
                
                // Проверяем совместимость
                if (!compatibilityService.checkComponentsCompatibility(product1, product2)) {
                    String reason = compatibilityService.getIncompatibilityReason(product1, product2);
                    
                    IssueType issueType = determineIssueType(reason);
                    IssueCategory category = determineIssueCategory(reason);
                    
                    result.addIssue(new CompatibilityIssue(
                        issueType,
                        category,
                        "Несовместимость компонентов",
                        reason != null ? reason : "Компоненты несовместимы",
                        product1.getTitle(),
                        product2.getTitle()
                    ));
                }
            }
        }
    }
    
    /**
     * Анализ энергопотребления
     */
    private void analyzePowerConsumption(List<Product> components, CompatibilityAnalysisResult result) {
        Optional<Product> psuOpt = components.stream()
                .filter(p -> p.getComponentType() == ComponentType.PSU)
                .findFirst();
        
        if (psuOpt.isEmpty()) {
            return; // Уже проверено в checkRequiredComponents
        }
        
        Product psu = psuOpt.get();
        // Для БП ищем мощность в поле "power" (это мощность БП, а не потребление)
        String psuPowerStr = psu.getSpec("power");
        
        // Если не найден "power", пробуем "wattage" как fallback
        if (psuPowerStr.isEmpty()) {
            psuPowerStr = psu.getSpec("wattage");
        }
        
        if (psuPowerStr.isEmpty()) {
            result.addIssue(new CompatibilityIssue(
                IssueType.WARNING,
                IssueCategory.POWER,
                "Неизвестная мощность БП",
                "Не указана мощность блока питания, невозможно проверить достаточность энергоснабжения"
            ));
            return;
        }
        
        try {
            int psuPower = Integer.parseInt(psuPowerStr);
            int totalConsumption = calculateTotalPowerConsumption(components);
            
            log.info("Анализ энергопотребления: БП {}Вт, потребление {}Вт", psuPower, totalConsumption);
            
            if (totalConsumption > psuPower) {
                result.addIssue(new CompatibilityIssue(
                    IssueType.CRITICAL_ERROR,
                    IssueCategory.POWER,
                    "Недостаточная мощность блока питания",
                    String.format("Выбранный блок питания (%dВт) не обеспечит стабильную работу конфигурации. " +
                                "Требуется минимум %dВт. Рекомендуется БП мощностью %dВт или выше.", 
                            psuPower, totalConsumption, (int)(totalConsumption * 1.2))
                ));
            } else if (totalConsumption > psuPower * 0.8) {
                result.addIssue(new CompatibilityIssue(
                    IssueType.WARNING,
                    IssueCategory.POWER,
                    "Высокая нагрузка на БП",
                    String.format("БП загружен на %.1f%% (рекомендуется не более 80%%)", 
                            (double) totalConsumption / psuPower * 100)
                ));
            } else if (totalConsumption < psuPower * 0.3) {
                result.addIssue(new CompatibilityIssue(
                    IssueType.RECOMMENDATION,
                    IssueCategory.POWER,
                    "Избыточная мощность БП",
                    String.format("БП загружен всего на %.1f%%, можно выбрать менее мощный для экономии", 
                            (double) totalConsumption / psuPower * 100)
                ));
            }
        } catch (NumberFormatException e) {
            result.addIssue(new CompatibilityIssue(
                IssueType.WARNING,
                IssueCategory.POWER,
                "Некорректное значение мощности БП",
                "Не удается определить мощность блока питания"
            ));
        }
    }
    
    /**
     * Анализ баланса производительности
     */
    private void analyzePerformanceBalance(List<Product> components, CompatibilityAnalysisResult result) {
        Optional<Product> cpuOpt = components.stream()
                .filter(p -> p.getComponentType() == ComponentType.CPU)
                .findFirst();
        
        Optional<Product> gpuOpt = components.stream()
                .filter(p -> p.getComponentType() == ComponentType.GPU)
                .findFirst();
        
        if (cpuOpt.isEmpty() || gpuOpt.isEmpty()) {
            return;
        }
        
        Product cpu = cpuOpt.get();
        Product gpu = gpuOpt.get();
        
        // Анализ баланса CPU-GPU
        analyzeCpuGpuBalance(cpu, gpu, result);
        
        // Анализ памяти
        analyzeMemoryBalance(components, result);
    }
    
    /**
     * Анализ баланса CPU-GPU
     */
    private void analyzeCpuGpuBalance(Product cpu, Product gpu, CompatibilityAnalysisResult result) {
        String cpuPerformanceStr = cpu.getSpec("performance_score");
        String gpuPerformanceStr = gpu.getSpec("performance_score");
        
        if (cpuPerformanceStr.isEmpty() || gpuPerformanceStr.isEmpty()) {
            return;
        }
        
        try {
            int cpuPerformance = Integer.parseInt(cpuPerformanceStr);
            int gpuPerformance = Integer.parseInt(gpuPerformanceStr);
            
            double ratio = (double) Math.max(cpuPerformance, gpuPerformance) / 
                          Math.min(cpuPerformance, gpuPerformance);
            
            if (ratio > 2.0) {
                String bottleneck = cpuPerformance < gpuPerformance ? "процессор" : "видеокарта";
                result.addIssue(new CompatibilityIssue(
                    IssueType.WARNING,
                    IssueCategory.BALANCE,
                    "Дисбаланс производительности",
                    String.format("Значительный дисбаланс между CPU и GPU. %s может стать узким местом", 
                            bottleneck),
                    cpu.getTitle(),
                    gpu.getTitle()
                ));
            } else if (ratio > 1.5) {
                result.addIssue(new CompatibilityIssue(
                    IssueType.RECOMMENDATION,
                    IssueCategory.BALANCE,
                    "Небольшой дисбаланс производительности",
                    "Рассмотрите возможность более сбалансированного соотношения CPU и GPU"
                ));
            }
        } catch (NumberFormatException e) {
            // Игнорируем ошибки парсинга
        }
    }
    
    /**
     * Анализ баланса памяти
     */
    private void analyzeMemoryBalance(List<Product> components, CompatibilityAnalysisResult result) {
        Optional<Product> ramOpt = components.stream()
                .filter(p -> p.getComponentType() == ComponentType.RAM)
                .findFirst();
        
        Optional<Product> gpuOpt = components.stream()
                .filter(p -> p.getComponentType() == ComponentType.GPU)
                .findFirst();
        
        if (ramOpt.isEmpty()) {
            return;
        }
        
        Product ram = ramOpt.get();
        String ramSizeStr = ram.getSpec("capacity");
        
        if (ramSizeStr.isEmpty()) {
            return;
        }
        
        try {
            int ramSize = Integer.parseInt(ramSizeStr.replaceAll("[^0-9]", ""));
            
            if (ramSize < 8) {
                result.addIssue(new CompatibilityIssue(
                    IssueType.WARNING,
                    IssueCategory.PERFORMANCE,
                    "Недостаточно оперативной памяти",
                    "Рекомендуется минимум 8GB RAM для современных задач"
                ));
            } else if (ramSize > 64) {
                result.addIssue(new CompatibilityIssue(
                    IssueType.RECOMMENDATION,
                    IssueCategory.PERFORMANCE,
                    "Избыточное количество RAM",
                    "Возможно, такое количество RAM избыточно для большинства задач"
                ));
            }
        } catch (NumberFormatException e) {
            // Игнорируем ошибки парсинга
        }
    }
    
    /**
     * Анализ тепловыделения
     */
    private void analyzeThermalDesign(List<Product> components, CompatibilityAnalysisResult result) {
        Optional<Product> cpuOpt = components.stream()
                .filter(p -> p.getComponentType() == ComponentType.CPU)
                .findFirst();
        
        Optional<Product> coolerOpt = components.stream()
                .filter(p -> p.getComponentType() == ComponentType.COOLER)
                .findFirst();
        
        if (cpuOpt.isEmpty() || coolerOpt.isEmpty()) {
            return;
        }
        
        Product cpu = cpuOpt.get();
        Product cooler = coolerOpt.get();
        
        String cpuTdpStr = cpu.getSpec("tdp");
        String coolerTdpStr = cooler.getSpec("max_tdp");
        
        if (cpuTdpStr.isEmpty() || coolerTdpStr.isEmpty()) {
            return;
        }
        
        try {
            int cpuTdp = Integer.parseInt(cpuTdpStr);
            int coolerTdp = Integer.parseInt(coolerTdpStr);
            
            if (cpuTdp > coolerTdp) {
                result.addIssue(new CompatibilityIssue(
                    IssueType.CRITICAL_ERROR,
                    IssueCategory.THERMAL,
                    "Недостаточное охлаждение",
                    String.format("Кулер не справится с охлаждением CPU (TDP %dВт > %dВт)", 
                            cpuTdp, coolerTdp),
                    cpu.getTitle(),
                    cooler.getTitle()
                ));
            } else if (cpuTdp > coolerTdp * 0.8) {
                result.addIssue(new CompatibilityIssue(
                    IssueType.WARNING,
                    IssueCategory.THERMAL,
                    "Недостаточный запас охлаждения",
                    "Рекомендуется кулер с запасом по TDP для стабильной работы"
                ));
            }
        } catch (NumberFormatException e) {
            // Игнорируем ошибки парсинга
        }
    }
    
    /**
     * Анализ физических размеров
     */
    private void analyzePhysicalDimensions(List<Product> components, CompatibilityAnalysisResult result) {
        Optional<Product> caseOpt = components.stream()
                .filter(p -> p.getComponentType() == ComponentType.CASE)
                .findFirst();
        
        Optional<Product> gpuOpt = components.stream()
                .filter(p -> p.getComponentType() == ComponentType.GPU)
                .findFirst();
        
        if (caseOpt.isEmpty() || gpuOpt.isEmpty()) {
            return;
        }
        
        Product pcCase = caseOpt.get();
        Product gpu = gpuOpt.get();
        
        String caseMaxGpuLengthStr = pcCase.getSpec("max_gpu_length");
        String gpuLengthStr = gpu.getSpec("length");
        
        if (caseMaxGpuLengthStr.isEmpty() || gpuLengthStr.isEmpty()) {
            return;
        }
        
        try {
            int caseMaxGpuLength = Integer.parseInt(caseMaxGpuLengthStr);
            int gpuLength = Integer.parseInt(gpuLengthStr);
            
            if (gpuLength > caseMaxGpuLength) {
                result.addIssue(new CompatibilityIssue(
                    IssueType.CRITICAL_ERROR,
                    IssueCategory.PHYSICAL,
                    "Видеокарта не помещается в корпус",
                    String.format("Длина видеокарты (%dmm) превышает максимальную для корпуса (%dmm)", 
                            gpuLength, caseMaxGpuLength),
                    gpu.getTitle(),
                    pcCase.getTitle()
                ));
            }
        } catch (NumberFormatException e) {
            // Игнорируем ошибки парсинга
        }
    }
    
    /**
     * Генерация рекомендаций по оптимизации
     */
    private void generateOptimizationRecommendations(List<Product> components, CompatibilityAnalysisResult result) {
        // Рекомендации по SSD
        boolean hasSsd = components.stream()
                .anyMatch(p -> p.getComponentType() == ComponentType.STORAGE && 
                              p.getSpec("type").toLowerCase().contains("ssd"));
        
        if (!hasSsd) {
            result.addIssue(new CompatibilityIssue(
                IssueType.RECOMMENDATION,
                IssueCategory.PERFORMANCE,
                "Рекомендуется SSD",
                "Добавление SSD значительно улучшит скорость загрузки системы и приложений"
            ));
        }
        
        // Рекомендации по количеству модулей RAM
        long ramModuleCount = components.stream()
                .filter(p -> p.getComponentType() == ComponentType.RAM)
                .count();
        
        if (ramModuleCount == 1) {
            result.addIssue(new CompatibilityIssue(
                IssueType.RECOMMENDATION,
                IssueCategory.PERFORMANCE,
                "Рекомендуется двухканальная память",
                "Использование двух модулей RAM вместо одного улучшит производительность"
            ));
        }
    }
    
    /**
     * Определение типа проблемы на основе описания
     */
    private IssueType determineIssueType(String reason) {
        if (reason == null) {
            return IssueType.CRITICAL_ERROR;
        }
        
        String lowerReason = reason.toLowerCase();
        
        // Критические ошибки
        if (lowerReason.contains("сокет") || lowerReason.contains("socket") ||
            lowerReason.contains("несовместим") || lowerReason.contains("одного типа")) {
            return IssueType.CRITICAL_ERROR;
        }
        
        // Предупреждения
        if (lowerReason.contains("производительность") || lowerReason.contains("баланс") ||
            lowerReason.contains("рекомендуется") || lowerReason.contains("недостаточн")) {
            return IssueType.WARNING;
        }
        
        return IssueType.CRITICAL_ERROR;
    }
    
    /**
     * Определение категории проблемы на основе описания
     */
    private IssueCategory determineIssueCategory(String reason) {
        if (reason == null) {
            return IssueCategory.COMPATIBILITY;
        }
        
        String lowerReason = reason.toLowerCase();
        
        if (lowerReason.contains("производительность") || lowerReason.contains("баланс")) {
            return IssueCategory.BALANCE;
        }
        
        if (lowerReason.contains("энерг") || lowerReason.contains("мощность")) {
            return IssueCategory.POWER;
        }
        
        if (lowerReason.contains("размер") || lowerReason.contains("длина")) {
            return IssueCategory.PHYSICAL;
        }
        
        return IssueCategory.COMPATIBILITY;
    }
    
    /**
     * Расчет общего энергопотребления конфигурации
     */
    private int calculateTotalPowerConsumption(List<Product> components) {
        int total = 0;
        
        for (Product component : components) {
            // ИСКЛЮЧАЕМ БП из расчета потребления - БП поставляет энергию, а не потребляет!
            if (component.getComponentType() == ComponentType.PSU) {
                log.debug("Пропускаем БП {} при расчете потребления", component.getTitle());
                continue; // Пропускаем БП
            }
            
            String powerStr = "";
            
            // Для всех компонентов кроме БП ищем энергопотребление
            // Сначала ищем специфические поля для потребления
            powerStr = component.getSpec("power_consumption");
            if (powerStr.isEmpty()) {
                powerStr = component.getSpec("tdp");
            }
            // НЕ ИЩЕМ поле "power" для не-БП компонентов, так как оно может означать мощность, а не потребление
            
            if (!powerStr.isEmpty()) {
                try {
                    int componentPower = Integer.parseInt(powerStr);
                    total += componentPower;
                    log.debug("Компонент {}: {}Вт (из поля {})", component.getTitle(), componentPower, 
                             component.getSpec("power_consumption").isEmpty() ? "tdp" : "power_consumption");
                } catch (NumberFormatException e) {
                    log.warn("Не удалось распарсить мощность для {}: {}", component.getTitle(), powerStr);
                    // Добавляем базовое потребление если не удалось распарсить
                    int basePower = getBasePowerConsumption(component.getComponentType());
                    total += basePower;
                    log.debug("Компонент {} (базовое потребление после ошибки парсинга): {}Вт", component.getTitle(), basePower);
                }
            } else {
                // Добавляем базовое потребление для компонентов без указанного TDP
                int basePower = getBasePowerConsumption(component.getComponentType());
                total += basePower;
                log.debug("Компонент {} (базовое потребление, нет TDP): {}Вт", component.getTitle(), basePower);
            }
        }
        
        log.info("Общее энергопотребление конфигурации: {}Вт", total);
        return total;
    }
    
    /**
     * Возвращает базовое энергопотребление для типа компонента
     */
    private int getBasePowerConsumption(ComponentType componentType) {
        switch (componentType) {
            case CPU: return 100; // Базовое потребление процессора
            case GPU: return 200; // Базовое потребление видеокарты  
            case RAM: return 5;   // Потребление модуля памяти
            case MB: return 30;   // Потребление материнской платы
            case STORAGE: return 10; // Потребление накопителя
            case COOLER: return 5;   // Потребление системы охлаждения
            case CASE: return 0;     // Корпус не потребляет энергию
            case PSU: return 0;      // БП не потребляет энергию
            default: return 10;      // Для неизвестных компонентов
        }
    }
} 