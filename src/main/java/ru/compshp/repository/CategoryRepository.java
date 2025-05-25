package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Category;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Поиск по родителю
    List<Category> findByParentId(Long parentId);
    List<Category> findByParentIsNull();
    List<Category> findByParent_Id(Long parentId);
    
    // Поиск по имени
    Category findByName(String name);
    List<Category> findByNameContaining(String name);
} 