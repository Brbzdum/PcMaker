package ru.compshp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.compshp.model.CompatibilityCache;
import ru.compshp.model.CompatibilityRule;
import ru.compshp.model.Product;
import ru.compshp.repository.CompatibilityCacheRepository;
import ru.compshp.repository.CompatibilityRuleRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для проверки совместимости компонентов
 * Основные функции:
 * - Проверка совместимости компонентов
 * - Кэширование результатов проверок
 * - Расчет энергопотребления
 * - Управление правилами совместимости
 */
@Service
@RequiredArgsConstructor
public class CompatibilityService {
    private final CompatibilityCacheRepository cacheRepository;
    private final CompatibilityRuleRepository ruleRepository;
    private final ObjectMapper objectMapper;

    public ResponseEntity<?> checkCompatibility(Product cpu, Product motherboard, Product gpu, Product psu, Product pcCase) {
        try {
            // Генерируем ключ кэша
            String cacheKey = generateCacheKey(cpu, motherboard, gpu, psu, pcCase);

            // Проверяем кэш
            Optional<CompatibilityCache> cachedResult = cacheRepository.findByCacheKey(cacheKey);
            if (cachedResult.isPresent()) {
                return ResponseEntity.ok(objectMapper.readValue(cachedResult.get().getResult(), Map.class));
            }

            Map<String, Object> result = new HashMap<>();
            List<String> notes = new ArrayList<>();

            // Проверяем совместимость CPU и материнской платы
            if (cpu != null && motherboard != null) {
                boolean cpuMoboCompatible = checkCpuMoboCompatibility(cpu, motherboard);
                result.put("cpu_motherboard", cpuMoboCompatible);
                if (!cpuMoboCompatible) {
                    notes.add("CPU не совместим с материнской платой");
                }
            }

            // Проверяем совместимость GPU и корпуса
            if (gpu != null && pcCase != null) {
                boolean gpuCaseCompatible = checkGpuCaseCompatibility(gpu, pcCase);
                result.put("gpu_case", gpuCaseCompatible);
                if (!gpuCaseCompatible) {
                    notes.add("GPU не помещается в корпус");
                }
            }

            // Проверяем совместимость PSU и энергопотребления
            if (psu != null && cpu != null && gpu != null) {
                boolean psuPowerCompatible = checkPsuPowerCompatibility(psu, cpu, gpu);
                result.put("psu_power", psuPowerCompatible);
                if (!psuPowerCompatible) {
                    notes.add("Блок питания недостаточной мощности");
                }
            }

            result.put("notes", notes);
            result.put("compatible", !notes.isEmpty());

            // Сохраняем результат в кэш
            CompatibilityCache cache = new CompatibilityCache();
            cache.setCacheKey(cacheKey);
            cache.setResult(objectMapper.writeValueAsString(result));
            cacheRepository.save(cache);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error checking compatibility: " + e.getMessage());
        }
    }

    public ResponseEntity<?> calculatePowerRequirement(Product cpu, Product gpu, Set<Product> additionalComponents) {
        try {
            int totalPower = 0;

            // Добавляем энергопотребление CPU
            if (cpu != null && cpu.getSpecifications() != null && cpu.getSpecifications().containsKey("tdp")) {
                totalPower += Integer.parseInt(cpu.getSpecifications().get("tdp").toString());
            }

            // Добавляем энергопотребление GPU
            if (gpu != null && gpu.getSpecifications() != null && gpu.getSpecifications().containsKey("powerConsumption")) {
                totalPower += Integer.parseInt(gpu.getSpecifications().get("powerConsumption").toString());
            }

            // Добавляем энергопотребление дополнительных компонентов
            for (Product component : additionalComponents) {
                if (component.getSpecifications() != null && component.getSpecifications().containsKey("powerConsumption")) {
                    totalPower += Integer.parseInt(component.getSpecifications().get("powerConsumption").toString());
                }
            }

            // Добавляем 20% запаса
            totalPower = (int) (totalPower * 1.2);

            return ResponseEntity.ok(totalPower);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error calculating power requirement: " + e.getMessage());
        }
    }

    public boolean checkCpuMoboCompatibility(Product cpu, Product motherboard) {
        try {
            if (cpu.getSpecifications() == null || motherboard.getSpecifications() == null) {
                return false;
            }

            // Проверяем совместимость сокетов
            String cpuSocket = cpu.getSpecifications().get("socket").toString();
            String moboSocket = motherboard.getSpecifications().get("socket").toString();
            if (!cpuSocket.equals(moboSocket)) {
                return false;
            }

            // Проверяем совместимость типов памяти
            String cpuRamType = cpu.getSpecifications().get("ram_type").toString();
            String moboRamType = motherboard.getSpecifications().get("ram_type").toString();
            return cpuRamType.equals(moboRamType);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkGpuCaseCompatibility(Product gpu, Product pcCase) {
        try {
            if (gpu.getSpecifications() == null || pcCase.getSpecifications() == null) {
                return false;
            }

            // Проверяем длину GPU и максимальную длину GPU в корпусе
            double gpuLength = Double.parseDouble(gpu.getSpecifications().get("length").toString());
            double maxGpuLength = Double.parseDouble(pcCase.getSpecifications().get("maxGpuLength").toString());
            return gpuLength <= maxGpuLength;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkPsuPowerCompatibility(Product psu, Product cpu, Product gpu) {
        try {
            if (psu.getSpecifications() == null || cpu.getSpecifications() == null || gpu.getSpecifications() == null) {
                return false;
            }

            // Получаем мощность блока питания
            int psuWattage = Integer.parseInt(psu.getSpecifications().get("wattage").toString());

            // Рассчитываем требуемую мощность
            int requiredPower = 0;
            requiredPower += Integer.parseInt(cpu.getSpecifications().get("tdp").toString());
            requiredPower += Integer.parseInt(gpu.getSpecifications().get("powerConsumption").toString());

            // Добавляем 20% запаса
            requiredPower = (int) (requiredPower * 1.2);

            return psuWattage >= requiredPower;
        } catch (Exception e) {
            return false;
        }
    }

    public List<CompatibilityRule> getRulesBySourceType(String sourceType) {
        return ruleRepository.findBySourceType(sourceType);
    }

    public List<CompatibilityRule> getRulesByTargetType(String targetType) {
        return ruleRepository.findByTargetType(targetType);
    }

    public Optional<CompatibilityRule> getRuleById(Long id) {
        return ruleRepository.findById(id);
    }

    public CompatibilityRule saveRule(CompatibilityRule rule) {
        return ruleRepository.save(rule);
    }

    public void deleteRule(Long id) {
        ruleRepository.deleteById(id);
    }

    private String generateCacheKey(Product... components) {
        return Arrays.stream(components)
            .filter(Objects::nonNull)
            .map(Product::getId)
            .map(String::valueOf)
            .collect(Collectors.joining("_"));
    }
} 