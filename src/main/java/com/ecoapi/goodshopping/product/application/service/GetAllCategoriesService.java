package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.GetAllCategoriesUseCase;
import com.ecoapi.goodshopping.product.domain.model.Category;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.repository.JpaCategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for getting all categories
 */
public class GetAllCategoriesService implements GetAllCategoriesUseCase {
    
    private final JpaCategoryRepository jpaCategoryRepository;
    private final com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.mapper.ProductPersistenceMapper mapper;
    
    public GetAllCategoriesService(JpaCategoryRepository jpaCategoryRepository,
                                   com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.mapper.ProductPersistenceMapper mapper) {
        this.jpaCategoryRepository = jpaCategoryRepository;
        this.mapper = mapper;
    }
    
    @Override
    public List<Category> execute() {
        return jpaCategoryRepository.findAll().stream()
                .map(mapper::toCategoryDomain)
                .collect(Collectors.toList());
    }
}
