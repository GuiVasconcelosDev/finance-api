package com.guivasconcelos.finance_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.guivasconcelos.finance_api.dto.CategoryRequest;
import com.guivasconcelos.finance_api.dto.CategoryResponse;
import com.guivasconcelos.finance_api.dto.CategoryUpdateRequest;
import com.guivasconcelos.finance_api.exception.CurrentUser;
import com.guivasconcelos.finance_api.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CategoryRequest request) {
        return CategoryResponse.fromEntity(categoryService.create(CurrentUser.id(jwt), request));
    }

     @GetMapping
    public List<CategoryResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return categoryService.list(CurrentUser.id(jwt)).stream().map(CategoryResponse::fromEntity).toList();
    }

    @GetMapping("/{id}")
    public CategoryResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        return CategoryResponse.fromEntity(categoryService.get(CurrentUser.id(jwt), id));
    }

    @PutMapping("/{id}")
    public CategoryResponse rename(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id,
                                   @Valid @RequestBody CategoryUpdateRequest request) {
        return CategoryResponse.fromEntity(categoryService.rename(CurrentUser.id(jwt), id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        categoryService.delete(CurrentUser.id(jwt), id);
    }
}
