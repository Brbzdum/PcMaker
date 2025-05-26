package ru.compshp.mapper;

import org.springframework.stereotype.Component;
import ru.compshp.dto.CategoryDTO;
import ru.compshp.model.Category;

@Component
public class CategoryMapper {
    
    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        dto.setHasChildren(!category.getChildren().isEmpty());
        
        return dto;
    }

    public Category toEntity(CategoryDTO dto) {
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