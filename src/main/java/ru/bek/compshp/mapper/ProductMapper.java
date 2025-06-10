package ru.bek.compshp.mapper;

import org.springframework.stereotype.Component;
import ru.bek.compshp.dto.ProductDto;
import ru.bek.compshp.model.Category;
import ru.bek.compshp.model.Manufacturer;
import ru.bek.compshp.model.Product;
import ru.bek.compshp.model.enums.ComponentType;
import ru.bek.compshp.repository.CategoryRepository;
import ru.bek.compshp.repository.ManufacturerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    
    private final CategoryRepository categoryRepository;
    private final ManufacturerRepository manufacturerRepository;
    
    public ProductMapper(CategoryRepository categoryRepository, ManufacturerRepository manufacturerRepository) {
        this.categoryRepository = categoryRepository;
        this.manufacturerRepository = manufacturerRepository;
    }
    
    /**
     * Преобразует сущность Product в DTO
     * @param product сущность
     * @return DTO
     */
    public ProductDto toDto(Product product) {
        if (product == null) return null;
        
        ProductDto dto = ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .isActive(product.getIsActive())
                .imagePath(product.getImagePath())
                .specs(product.getSpecs())
                .build();
        
        // Устанавливаем тип компонента
        if (product.getComponentType() != null) {
            dto.setComponentType(product.getComponentType().name());
        }
        
        // Устанавливаем данные производителя
        if (product.getManufacturer() != null) {
            dto.setManufacturerId(product.getManufacturer().getId());
            dto.setManufacturer(product.getManufacturer().getName());
        }
        
        // Устанавливаем ID категории
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategory(product.getCategory().getName());
        }
        
        return dto;
    }
    
    /**
     * Преобразует список сущностей Product в список DTO
     * @param products список сущностей
     * @return список DTO
     */
    public List<ProductDto> toDtoList(List<Product> products) {
        if (products == null) return null;
        return products.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Преобразует DTO в сущность Product
     * @param dto DTO
     * @return сущность
     */
    public Product toEntity(ProductDto dto) {
        if (dto == null) return null;
        
        Product product = new Product();
        
        // Копируем простые поля
        product.setId(dto.getId());
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setIsActive(dto.getIsActive());
        product.setImagePath(dto.getImagePath());
        product.setSpecs(dto.getSpecs());
        
        // Устанавливаем тип компонента
        if (dto.getComponentType() != null) {
            try {
                product.setComponentType(ComponentType.valueOf(dto.getComponentType()));
            } catch (IllegalArgumentException e) {
                // Игнорируем неправильный тип компонента
            }
        }
        
        // Устанавливаем производителя
        if (dto.getManufacturerId() != null) {
            Manufacturer manufacturer = manufacturerRepository.findById(dto.getManufacturerId()).orElse(null);
            product.setManufacturer(manufacturer);
        }
        
        // Устанавливаем категорию
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            product.setCategory(category);
        }
        
        return product;
    }
    
    /**
     * Обновляет существующую сущность Product данными из DTO
     * @param product существующая сущность
     * @param dto DTO с новыми данными
     * @return обновленная сущность
     */
    public Product updateEntityFromDto(Product product, ProductDto dto) {
        if (product == null || dto == null) return product;
        
        // Обновляем простые поля
        if (dto.getTitle() != null) product.setTitle(dto.getTitle());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getStock() != null) product.setStock(dto.getStock());
        if (dto.getIsActive() != null) product.setIsActive(dto.getIsActive());
        if (dto.getImagePath() != null) product.setImagePath(dto.getImagePath());
        if (dto.getSpecs() != null) product.setSpecs(dto.getSpecs());
        
        // Обновляем тип компонента
        if (dto.getComponentType() != null) {
            try {
                product.setComponentType(ComponentType.valueOf(dto.getComponentType()));
            } catch (IllegalArgumentException e) {
                // Игнорируем неправильный тип компонента
            }
        }
        
        // Обновляем производителя
        if (dto.getManufacturerId() != null) {
            Manufacturer manufacturer = manufacturerRepository.findById(dto.getManufacturerId()).orElse(null);
            product.setManufacturer(manufacturer);
        }
        
        // Обновляем категорию
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            product.setCategory(category);
        }
        
        return product;
    }
} 