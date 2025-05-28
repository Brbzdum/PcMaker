package ru.compshp.mapper;

import org.springframework.stereotype.Component;
import ru.compshp.dto.CategoryDto;
import ru.compshp.model.Category;

@Component
public class CategoryMapper {
    
    public CategoryDto toDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        dto.setHasChildren(!category.getChildren().isEmpty());
        
        return dto;
    }

    public Category toEntity(CategoryDto dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        
        return category;
    }
} 