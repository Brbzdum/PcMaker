package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Category;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentId(Long parentId);
    Category findByName(String name);

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findRootCategories();

    @Query("SELECT c FROM Category c WHERE c.parent.id = ?1")
    List<Category> findChildCategories(Long parentId);

    @Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
    List<Category> findCategoriesByNameContaining(String name);
} 