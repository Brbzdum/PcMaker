package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.Manufacturer;
import ru.compshp.model.Product;
import ru.compshp.repository.ManufacturerRepository;
import ru.compshp.repository.ProductRepository;

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

    public List<Manufacturer> getAllManufacturers() {
        return manufacturerRepository.findAll();
    }

    public Optional<Manufacturer> getManufacturerById(Long id) {
        return manufacturerRepository.findById(id);
    }

    public Optional<Manufacturer> getManufacturerByName(String name) {
        return manufacturerRepository.findByName(name);
    }

    public List<Product> getProductsByManufacturer(Long manufacturerId) {
        return manufacturerRepository.findById(manufacturerId)
            .map(productRepository::findByManufacturer)
            .orElse(List.of());
    }

    @Transactional
    public Manufacturer createManufacturer(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }

    @Transactional
    public Manufacturer updateManufacturer(Long id, Manufacturer manufacturer) {
        return manufacturerRepository.findById(id)
            .map(existingManufacturer -> {
                existingManufacturer.setName(manufacturer.getName());
                existingManufacturer.setDescription(manufacturer.getDescription());
                existingManufacturer.setWebsite(manufacturer.getWebsite());
                existingManufacturer.setLogo(manufacturer.getLogo());
                return manufacturerRepository.save(existingManufacturer);
            })
            .orElseThrow(() -> new RuntimeException("Manufacturer not found"));
    }

    @Transactional
    public void deleteManufacturer(Long id) {
        // Проверяем, есть ли продукты у производителя
        if (!productRepository.findByManufacturerId(id).isEmpty()) {
            throw new RuntimeException("Cannot delete manufacturer with products");
        }

        manufacturerRepository.deleteById(id);
    }

    public List<Manufacturer> getTopManufacturers(int limit) {
        return manufacturerRepository.findTopByProductCount(limit);
    }

    public double calculateManufacturerRating(Long manufacturerId) {
        return manufacturerRepository.findById(manufacturerId)
            .map(manufacturer -> {
                List<Product> products = productRepository.findByManufacturer(manufacturer);
                if (products.isEmpty()) {
                    return 0.0;
                }
                return products.stream()
                    .mapToDouble(Product::getRating)
                    .average()
                    .orElse(0.0);
            })
            .orElse(0.0);
    }

    @Transactional
    public void updateManufacturerRating(Long manufacturerId) {
        manufacturerRepository.findById(manufacturerId).ifPresent(manufacturer -> {
            double rating = calculateManufacturerRating(manufacturerId);
            manufacturer.setRating(rating);
            manufacturerRepository.save(manufacturer);
        });
    }
} 