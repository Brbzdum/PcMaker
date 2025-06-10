package ru.bek.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bek.compshp.exception.ResourceNotFoundException;
import ru.bek.compshp.model.Manufacturer;
import ru.bek.compshp.model.Product;
import ru.bek.compshp.repository.ManufacturerRepository;
import ru.bek.compshp.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления производителями
 * Основные функции:
 * - Управление производителями (CRUD)
 * - Получение продуктов по производителю
 * - Управление рейтингом производителей
 */
@Service
@RequiredArgsConstructor
public class ManufacturerService {
    private final ManufacturerRepository manufacturerRepository;
    private final ProductRepository productRepository;

    /**
     * Получить всех производителей
     */
    public List<Manufacturer> getAllManufacturers() {
        return manufacturerRepository.findAll();
    }

    /**
     * Получить производителя по ID
     */
    public Optional<Manufacturer> getManufacturerById(Long id) {
        return manufacturerRepository.findById(id);
    }
    
    /**
     * Получить имя производителя по ID
     * @param id ID производителя
     * @return имя производителя или "Неизвестный производитель", если производитель не найден
     */
    public String getManufacturerNameById(Long id) {
        return manufacturerRepository.findById(id)
                .map(Manufacturer::getName)
                .orElse("Неизвестный производитель");
    }

    /**
     * Получить производителя по имени
     */
    public Optional<Manufacturer> getManufacturerByName(String name) {
        return manufacturerRepository.findByName(name);
    }

    /**
     * Получить продукты производителя
     */
    public List<Product> getProductsByManufacturer(Long manufacturerId) {
        return manufacturerRepository.findById(manufacturerId)
            .map(productRepository::findByManufacturer)
            .orElse(List.of());
    }

    /**
     * Создать нового производителя
     */
    @Transactional
    public Manufacturer createManufacturer(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }

    /**
     * Обновить производителя
     */
    @Transactional
    public Manufacturer updateManufacturer(Long id, Manufacturer manufacturer) {
        return manufacturerRepository.findById(id)
            .map(existingManufacturer -> {
                existingManufacturer.setName(manufacturer.getName());
                existingManufacturer.setDescription(manufacturer.getDescription());
                return manufacturerRepository.save(existingManufacturer);
            })
            .orElseThrow(() -> new ResourceNotFoundException("Manufacturer", "id", id));
    }

    /**
     * Удалить производителя
     */
    @Transactional
    public void deleteManufacturer(Long id) {
        // Проверяем, есть ли продукты у производителя
        if (!productRepository.findByManufacturerId(id).isEmpty()) {
            throw new IllegalStateException("Cannot delete manufacturer with products");
        }
        
        manufacturerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Manufacturer", "id", id));
        manufacturerRepository.deleteById(id);
    }

    /**
     * Получить топ производителей
     */
    public List<Manufacturer> getTopManufacturers(int limit) {
        // Используем PageRequest для ограничения результатов
        return manufacturerRepository.findTopByProductCount(PageRequest.of(0, limit));
    }

    /**
     * Рассчитать рейтинг производителя
     */
    public double calculateManufacturerRating(Long manufacturerId) {
        return manufacturerRepository.findById(manufacturerId)
            .map(manufacturer -> {
                List<Product> products = productRepository.findByManufacturer(manufacturer);
                if (products.isEmpty()) {
                    return 0.0;
                }
                return products.stream()
                    .mapToDouble(product -> product.getAverageRating().doubleValue())
                    .average()
                    .orElse(0.0);
            })
            .orElse(0.0);
    }

    /**
     * Обновить рейтинг производителя
     */
    @Transactional
    public void updateManufacturerRating(Long manufacturerId) {
        manufacturerRepository.findById(manufacturerId).ifPresent(manufacturer -> {
            double rating = calculateManufacturerRating(manufacturerId);
            manufacturer.setRating(rating);
            manufacturerRepository.save(manufacturer);
        });
    }
    
    /**
     * Сохранить производителя
     */
    @Transactional
    public Manufacturer saveManufacturer(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }
    
    /**
     * Проверить существование производителя по имени
     */
    public boolean existsByName(String name) {
        return manufacturerRepository.findByName(name).isPresent();
    }
} 