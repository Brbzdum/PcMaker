package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Category;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Поиск по имени
    Optional<Category> findByName(String name);
    List<Category> findByNameContaining(String name);
    
    // Поиск по иерархии
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findRootCategories();
    
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId")
    List<Category> findChildCategories(@Param("parentId") Long parentId);

    // Добавляем недостающие методы
    List<Category> findByParentId(Long parentId);
    
    List<Category> findByParentIsNull();
    
    @Query("SELECT c FROM Category c WHERE c.id = :categoryId")
    Optional<Category> findById(@Param("categoryId") Long categoryId);
    
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.name = :name")
    boolean existsByName(@Param("name") String name);
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.id = :id")
    boolean existsById(@Param("id") Long id);

    /**
     * Найти категорию по slug (URL-дружественному имени)
     */
    Category findBySlug(String slug);
    
    /**
     * Найти категории по списку идентификаторов
     */
    @Query("SELECT c FROM Category c WHERE c.id IN :ids")
    List<Category> findByIds(@Param("ids") List<Long> ids);
    
    /**
     * Проверить наличие продуктов в категории
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.category.id = :categoryId")
    boolean hasProducts(@Param("categoryId") Long categoryId);
} 