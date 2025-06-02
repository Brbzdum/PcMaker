package ru.bek.compshp.mapper;

import org.springframework.stereotype.Component;
import ru.bek.compshp.dto.CategoryDto;
import ru.bek.compshp.model.Category;

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
        dto.setSlug(category.getSlug());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        dto.setHasChildren(!category.getChildren().isEmpty());
        
        dto.setPcComponent(category.getIsPcComponent() != null ? category.getIsPcComponent() : false);
        
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
        category.setSlug(dto.getSlug());
        
        Boolean pcComponentValue = false;
        if (dto.getPcComponent() != null) {
            pcComponentValue = dto.getPcComponent();
        }
        category.setIsPcComponent(pcComponentValue);
        
        return category;
    }
} 