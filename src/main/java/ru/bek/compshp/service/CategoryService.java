package ru.bek.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bek.compshp.dto.CategoryDto;
import ru.bek.compshp.exception.CategoryNotFoundException;
import ru.bek.compshp.exception.DuplicateCategoryException;
import ru.bek.compshp.mapper.CategoryMapper;
import ru.bek.compshp.model.Category;
import ru.bek.compshp.model.Product;
import ru.bek.compshp.repository.CategoryRepository;
import ru.bek.compshp.repository.ProductRepository;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Сервис для управления категориями
 * Основные функции:
 * - Управление категориями (CRUD)
 * - Получение продуктов по категории
 * - Управление иерархией категорий
 */
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;
    
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "categories", key = "#id")
    public CategoryDto getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDTO)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }

    @Cacheable(value = "categories", key = "'name:' + #name")
    public CategoryDto getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .map(categoryMapper::toDTO)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with name: " + name));
    }

    @Cacheable(value = "categories", key = "'subcategories:' + #parentId")
    public List<CategoryDto> getSubcategories(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "categories", key = "'root'")
    public List<CategoryDto> getRootCategories() {
        return categoryRepository.findByParentIsNull().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException("Category not found with id: " + categoryId);
        }
        return productRepository.findByCategoryId(categoryId);
    }

    /**
     * Создает slug из имени категории
     * @param name имя категории
     * @return slug
     */
    private String createSlug(String name) {
        if (name == null) {
            return "";
        }
        
        // Транслитерация кириллицы в латиницу
        String transliterated = transliterateRussian(name);
        
        // Нормализация и удаление специальных символов
        String normalized = Normalizer.normalize(transliterated, Normalizer.Form.NFD);
        String noWhitespace = WHITESPACE.matcher(normalized).replaceAll("-");
        String slug = NONLATIN.matcher(noWhitespace).replaceAll("");
        
        return slug.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Транслитерирует русский текст в латиницу
     * @param text исходный текст на русском
     * @return транслитерированный текст
     */
    private String transliterateRussian(String text) {
        char[] cyrillic = {'а','б','в','г','д','е','ё','ж','з','и','й','к','л','м','н','о','п','р','с','т','у','ф','х','ц','ч','ш','щ','ъ','ы','ь','э','ю','я',
                          'А','Б','В','Г','Д','Е','Ё','Ж','З','И','Й','К','Л','М','Н','О','П','Р','С','Т','У','Ф','Х','Ц','Ч','Ш','Щ','Ъ','Ы','Ь','Э','Ю','Я'};
        String[] latin = {"a","b","v","g","d","e","yo","zh","z","i","y","k","l","m","n","o","p","r","s","t","u","f","kh","ts","ch","sh","sch","","y","","e","yu","ya",
                         "A","B","V","G","D","E","YO","ZH","Z","I","Y","K","L","M","N","O","P","R","S","T","U","F","KH","TS","CH","SH","SCH","","Y","","E","YU","YA"};
        
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            boolean replaced = false;
            
            for (int j = 0; j < cyrillic.length; j++) {
                if (c == cyrillic[j]) {
                    sb.append(latin[j]);
                    replaced = true;
                    break;
                }
            }
            
            if (!replaced) {
                sb.append(c);
            }
        }
        
        return sb.toString();
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoryRepository.findByName(categoryDto.getName()).isPresent()) {
            throw new DuplicateCategoryException("Category with name " + categoryDto.getName() + " already exists");
        }

        Category category = categoryMapper.toEntity(categoryDto);
        
        // Генерируем slug из имени категории
        String slug = createSlug(category.getName());
        // Если slug уже существует, добавляем к нему уникальный суффикс
        int suffix = 1;
        String originalSlug = slug;
        while (categoryRepository.findBySlug(slug) != null) {
            slug = originalSlug + "-" + suffix;
            suffix++;
        }
        category.setSlug(slug);
        
        if (categoryDto.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException("Parent category not found"));
            category.setParent(parent);
        }
        
        // Установка значения isPcComponent, если оно не было установлено
        if (category.getPcComponent() == null) {
            category.setPcComponent(false);
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        if (!existingCategory.getName().equals(categoryDto.getName()) &&
            categoryRepository.findByName(categoryDto.getName()).isPresent()) {
            throw new DuplicateCategoryException("Category with name " + categoryDto.getName() + " already exists");
        }

        // Если имя изменилось, обновляем slug
        if (!existingCategory.getName().equals(categoryDto.getName())) {
            // Генерируем slug из нового имени категории
            String slug = createSlug(categoryDto.getName());
            // Если slug уже существует, добавляем к нему уникальный суффикс
            int suffix = 1;
            String originalSlug = slug;
            while (categoryRepository.findBySlug(slug) != null && 
                  (existingCategory.getSlug() == null || !existingCategory.getSlug().equals(slug))) {
                slug = originalSlug + "-" + suffix;
                suffix++;
            }
            existingCategory.setSlug(slug);
        }

        existingCategory.setName(categoryDto.getName());
        existingCategory.setDescription(categoryDto.getDescription());
        
        // Обновление поля isPcComponent, если оно предоставлено
        if (categoryDto.getPcComponent() != null) {
            existingCategory.setPcComponent(categoryDto.getPcComponent());
        }

        if (categoryDto.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException("Parent category not found"));
            existingCategory.setParent(parent);
        } else {
            existingCategory.setParent(null);
        }

        Category updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toDTO(updatedCategory);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        if (!category.getChildren().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with subcategories");
        }

        if (!productRepository.findByCategoryId(id).isEmpty()) {
            throw new IllegalStateException("Cannot delete category with associated products");
        }

        categoryRepository.delete(category);
    }

    @Cacheable(value = "categories", key = "'path:' + #categoryId")
    public List<CategoryDto> getCategoryPath(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));

        List<CategoryDto> result = new ArrayList<>();
        if (category.getParent() != null) {
            result.addAll(getCategoryPath(category.getParent().getId()));
        }
        result.add(categoryMapper.toDTO(category));
        return result;
    }

    /**
     * Проверяет, содержится ли категория с указанным ID в дереве категорий
     * @param category корневая категория для проверки
     * @param id ID искомой категории
     * @return true, если категория найдена
     */
    public boolean isPresent(Category category, Long id) {
        if (category == null) {
            return false;
        }
        if (category.getId().equals(id)) {
            return true;
        }
        for (Category child : category.getChildren()) {
            if (isPresent(child, id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверяет существование категории по ID
     * @param id ID категории
     * @return true, если категория существует
     */
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    /**
     * Проверяет существование категории по имени
     * @param name имя категории
     * @return true, если категория существует
     */
    public boolean existsByName(String name) {
        return categoryRepository.findByName(name).isPresent();
    }

    /**
     * Выбрасывает исключение если категория не найдена
     * @param id ID категории
     * @throws CategoryNotFoundException если категория не найдена
     */
    public void orElseThrow(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Category not found with id: " + id);
        }
    }

    /**
     * Возвращает список категорий компонентов для ПК
     * @return список категорий
     */
    public List<CategoryDto> getPcComponentCategories() {
        return categoryRepository.findByIsPcComponent(true).stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }
} 