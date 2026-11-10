package com.guivasconcelos.finance_api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guivasconcelos.finance_api.dto.CategoryRequest;
import com.guivasconcelos.finance_api.dto.CategoryUpdateRequest;
import com.guivasconcelos.finance_api.exception.ConflictException;
import com.guivasconcelos.finance_api.exception.ResourcesNotFoundException;
import com.guivasconcelos.finance_api.model.Category;
import com.guivasconcelos.finance_api.model.User;
import com.guivasconcelos.finance_api.repository.CategoryRepository;
import com.guivasconcelos.finance_api.repository.TransactionRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final TransactionRepository transactionRepository;

    public CategoryService(CategoryRepository categoryRepository, UserService userService, TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Category create(UUID userId, CategoryRequest req) {
        String name = req.name().trim();
        if (categoryRepository.existsByUserIdAndNameAndType(userId, name, req.type())) {
            throw new ConflictException("Já existe uma categoria com esse nome e tipo");
        }
        User user = userService.getById(userId);
        return categoryRepository.save(new Category(user, name, req.type()));
    }

    @Transactional(readOnly = true)
    public List<Category> list(UUID userId) {
        return categoryRepository.findAllByUserIdOrderByNameAsc(userId);
    }

    @Transactional(readOnly = true)
    public Category get(UUID userId, UUID id) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourcesNotFoundException("Categoria não encontrada"));
    }

    @Transactional
    public Category rename(UUID userId, UUID id, CategoryUpdateRequest req) {
        Category category = get(userId, id);
        String name = req.name().trim();
        if (!name.equals(category.getName())
                && categoryRepository.existsByUserIdAndNameAndType(userId, name, category.getType())) {
            throw new ConflictException("Já existe uma categoria com esse nome e tipo");
        }
        category.rename(name);
        return category;
    }

    @Transactional
    public void delete(UUID userId, UUID id) {
        Category category = get(userId, id);
        if (transactionRepository.existsByCategoryId(id)) {
            throw new ConflictException("A categoria possui transações e não pode ser apagada");
        }
        categoryRepository.delete(category);
    }
}
