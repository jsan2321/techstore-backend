package com.ecoapi.techstore.product.application.service;

import com.ecoapi.techstore.product.application.port.in.GetAllBrandsUseCase;
import com.ecoapi.techstore.product.domain.model.Brand;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.repository.JpaBrandRepository;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.mapper.ProductPersistenceMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for getting all brands
 */
public class GetAllBrandsService implements GetAllBrandsUseCase {
    
    private final JpaBrandRepository jpaBrandRepository;
    private final ProductPersistenceMapper mapper;
    
    public GetAllBrandsService(JpaBrandRepository jpaBrandRepository,
                               ProductPersistenceMapper mapper) {
        this.jpaBrandRepository = jpaBrandRepository;
        this.mapper = mapper;
    }
    
    @Override
    public List<Brand> execute() {
        return jpaBrandRepository.findAll().stream()
                .map(mapper::toBrandDomain)
                .collect(Collectors.toList());
    }
}
