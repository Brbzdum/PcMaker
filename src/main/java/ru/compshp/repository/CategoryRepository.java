package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Category;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с категориями
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Находит категорию по имени
     * @param name имя категории
     * @return категория
     */
    Optional<Category> findByName(String name);
    
    /**
     * Проверяет существование категории с заданным именем
     * @param name имя категории
     * @return true, если категория существует
     */
    boolean existsByName(String name);
    
    /**
     * Находит все категории без родительской категории
     * @return список корневых категорий
     */
    List<Category> findByParentIsNull();
    
    /**
     * Находит все дочерние категории для заданной родительской категории
     * @param parent родительская категория
     * @return список дочерних категорий
     */
    List<Category> findByParent(Category parent);
    
    /**
     * Находит все дочерние категории для заданного ID родительской категории
     * @param parentId ID родительской категории
     * @return список дочерних категорий
     */
    List<Category> findByParentId(Long parentId);
    
    /**
     * Считает количество продуктов в категории
     * @param categoryId ID категории
     * @return количество продуктов
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countProductsByCategoryId(@Param("categoryId") Long categoryId);
    
    List<Category> findByNameContaining(String name);
    
    // Поиск по иерархии
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findRootCategories();
    
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId")
    List<Category> findChildCategories(@Param("parentId") Long parentId);
    
    @Query("SELECT c FROM Category c WHERE c.id = :categoryId")
    Optional<Category> findById(@Param("categoryId") Long categoryId);
    
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
    
    /**
     * Найти категории компонентов ПК
     * @param pcComponent флаг, указывающий на категории компонентов ПК
     * @return список категорий
     */
    List<Category> findByPcComponent(Boolean pcComponent);
} 