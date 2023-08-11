package ru.practicum.mainservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.category.CategoryDto;
import ru.practicum.mainservice.dto.category.NewCategoryDto;
import ru.practicum.mainservice.entity.Category;
import ru.practicum.mainservice.mapper.CategoryMapper;
import ru.practicum.mainservice.repository.CategoryRepository;
import ru.practicum.mainservice.util.PageParams;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("Mapping DTO: {} to entity", newCategoryDto);
        Category category = CategoryMapper.INSTANCE.toEntity(newCategoryDto);
        log.info("Saving entity: {} to DB and map to DTO", category);
        return CategoryMapper.INSTANCE.toDto(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        log.info("Checking if category with id {} exists", catId);
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> {
                    log.warn("Category with id {} does not exists", catId);
                    return new EntityNotFoundException("Category not found");
                }
        );
        log.info("Updating category={} with DTO: {}", category, categoryDto);
        category = CategoryMapper.INSTANCE.partialUpdate(categoryDto, category);
        log.info("Saving updated category={} to DB and map to DTO", category);
        return CategoryMapper.INSTANCE.toDto(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long catId) {
        log.info("Checking if category with id {} exists", catId);
        if (!categoryRepository.existsById(catId)) {
            log.warn("Category with id {} does not exists", catId);
            throw new EntityNotFoundException("Category not found");
        }

        log.info("Deleting category with id {} from DB", catId);
        categoryRepository.deleteById(catId);
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategory(Long catId) {
        log.info("Checking if category with id {} exists", catId);
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> {
                    log.warn("Category with id {} does not exists", catId);
                    return new EntityNotFoundException("Category not found");
                }
        );
        log.info("Mapping category={} to DTO", category);
        return CategoryMapper.INSTANCE.toDto(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(PageParams pageParams) {
        List<Category> categories = categoryRepository.findAll(pageParams.makePageRequest()).getContent();
        log.info("Mapping categories {} to DTO", categories.size());
        return categories.stream().map(CategoryMapper.INSTANCE::toDto).collect(Collectors.toList());
    }
}
