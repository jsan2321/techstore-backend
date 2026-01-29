package com.ecoapi.goodshopping.product.infrastructure.adapter;

import com.ecoapi.goodshopping.product.application.port.in.*;
import com.ecoapi.goodshopping.product.application.port.out.ImageFile;
import com.ecoapi.goodshopping.product.application.service.dto.ProductCommand;
import com.ecoapi.goodshopping.product.application.service.dto.ProductReadModel;
import com.ecoapi.goodshopping.product.domain.model.Product;
import com.ecoapi.goodshopping.product.domain.model.ProductSearchCriteria;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Transactional wrapper for use cases
 * Adds Spring's transaction management at the infrastructure boundary
 * Keeps application layer framework-agnostic
 */
public class TransactionalUseCaseWrapper {
    
    /**
     * Wraps write operations with transactional behavior
     */
    public static class TransactionalAddProductUseCase implements AddProductUseCase {
        private final AddProductUseCase delegate;
        
        public TransactionalAddProductUseCase(AddProductUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public Product addProduct(ProductCommand command) {
            return delegate.addProduct(command);
        }
    }
    
    public static class TransactionalUpdateProductUseCase implements UpdateProductUseCase {
        private final UpdateProductUseCase delegate;
        
        public TransactionalUpdateProductUseCase(UpdateProductUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public Product updateProduct(Long id, ProductCommand command) {
            return delegate.updateProduct(id, command);
        }
    }
    
    public static class TransactionalDeleteProductUseCase implements DeleteProductUseCase {
        private final DeleteProductUseCase delegate;
        
        public TransactionalDeleteProductUseCase(DeleteProductUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public void deleteProduct(Long id) {
            delegate.deleteProduct(id);
        }
    }
    
    public static class TransactionalUploadProductImageUseCase implements UploadProductImageUseCase {
        private final UploadProductImageUseCase delegate;
        
        public TransactionalUploadProductImageUseCase(UploadProductImageUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public Product uploadImage(Long productId, ImageFile imageFile) {
            return delegate.uploadImage(productId, imageFile);
        }
    }
    
    public static class TransactionalDeleteProductImageUseCase implements DeleteProductImageUseCase {
        private final DeleteProductImageUseCase delegate;
        
        public TransactionalDeleteProductImageUseCase(DeleteProductImageUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public Product deleteImage(Long productId) {
            return delegate.deleteImage(productId);
        }
    }
    
    /**
     * Wraps read-only operations with transactional behavior
     */
    public static class TransactionalGetProductByIdUseCase implements GetProductByIdUseCase {
        private final GetProductByIdUseCase delegate;
        
        public TransactionalGetProductByIdUseCase(GetProductByIdUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional(readOnly = true)
        public Product execute(Long id) {
            return delegate.execute(id);
        }
    }
    
    public static class TransactionalGetAllProductsUseCase implements GetAllProductsUseCase {
        private final GetAllProductsUseCase delegate;
        
        public TransactionalGetAllProductsUseCase(GetAllProductsUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional(readOnly = true)
        public List<Product> execute() {
            return delegate.execute();
        }
    }
    
    public static class TransactionalSearchProductsByCriteriaUseCase implements SearchProductsByCriteriaUseCase {
        private final SearchProductsByCriteriaUseCase delegate;
        
        public TransactionalSearchProductsByCriteriaUseCase(SearchProductsByCriteriaUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional(readOnly = true)
        public List<ProductReadModel> search(ProductSearchCriteria criteria) {
            return delegate.search(criteria);
        }
    }
    
    public static class TransactionalGetProductsByCategoryUseCase implements GetProductsByCategoryUseCase {
        private final GetProductsByCategoryUseCase delegate;
        
        public TransactionalGetProductsByCategoryUseCase(GetProductsByCategoryUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional(readOnly = true)
        public List<Product> execute(String categoryName) {
            return delegate.execute(categoryName);
        }
    }
    
    public static class TransactionalGetProductsByBrandUseCase implements GetProductsByBrandUseCase {
        private final GetProductsByBrandUseCase delegate;
        
        public TransactionalGetProductsByBrandUseCase(GetProductsByBrandUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional(readOnly = true)
        public List<Product> execute(String brand) {
            return delegate.execute(brand);
        }
    }
    
    public static class TransactionalSearchProductsUseCase implements SearchProductsUseCase {
        private final SearchProductsUseCase delegate;
        
        public TransactionalSearchProductsUseCase(SearchProductsUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional(readOnly = true)
        public List<Product> byCategoryAndBrand(String categoryName, String brand) {
            return delegate.byCategoryAndBrand(categoryName, brand);
        }
        
        @Override
        @Transactional(readOnly = true)
        public List<Product> byName(String name) {
            return delegate.byName(name);
        }
        
        @Override
        @Transactional(readOnly = true)
        public List<Product> byBrandAndName(String brand, String name) {
            return delegate.byBrandAndName(brand, name);
        }
    }
    
    // ==================== Category Use Case Wrappers ====================
    
    public static class TransactionalAddCategoryUseCase implements AddCategoryUseCase {
        private final AddCategoryUseCase delegate;
        
        public TransactionalAddCategoryUseCase(AddCategoryUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public com.ecoapi.goodshopping.product.domain.model.Category addCategory(String name) {
            return delegate.addCategory(name);
        }
    }
    
    public static class TransactionalGetAllCategoriesUseCase implements GetAllCategoriesUseCase {
        private final GetAllCategoriesUseCase delegate;
        
        public TransactionalGetAllCategoriesUseCase(GetAllCategoriesUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional(readOnly = true)
        public List<com.ecoapi.goodshopping.product.domain.model.Category> execute() {
            return delegate.execute();
        }
    }
    
    public static class TransactionalGetCategoryByIdUseCase implements GetCategoryByIdUseCase {
        private final GetCategoryByIdUseCase delegate;
        
        public TransactionalGetCategoryByIdUseCase(GetCategoryByIdUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional(readOnly = true)
        public com.ecoapi.goodshopping.product.domain.model.Category execute(Long id) {
            return delegate.execute(id);
        }
    }
    
    public static class TransactionalUpdateCategoryUseCase implements UpdateCategoryUseCase {
        private final UpdateCategoryUseCase delegate;
        
        public TransactionalUpdateCategoryUseCase(UpdateCategoryUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public com.ecoapi.goodshopping.product.domain.model.Category updateCategory(Long id, String name) {
            return delegate.updateCategory(id, name);
        }
    }
    
    public static class TransactionalDeleteCategoryUseCase implements DeleteCategoryUseCase {
        private final DeleteCategoryUseCase delegate;
        
        public TransactionalDeleteCategoryUseCase(DeleteCategoryUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public void deleteCategory(Long id) {
            delegate.deleteCategory(id);
        }
    }
    
    // ==================== Brand Use Case Wrappers ====================
    
    public static class TransactionalAddBrandUseCase implements AddBrandUseCase {
        private final AddBrandUseCase delegate;
        
        public TransactionalAddBrandUseCase(AddBrandUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public com.ecoapi.goodshopping.product.domain.model.Brand addBrand(String name) {
            return delegate.addBrand(name);
        }
    }
    
    public static class TransactionalGetAllBrandsUseCase implements GetAllBrandsUseCase {
        private final GetAllBrandsUseCase delegate;
        
        public TransactionalGetAllBrandsUseCase(GetAllBrandsUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional(readOnly = true)
        public List<com.ecoapi.goodshopping.product.domain.model.Brand> execute() {
            return delegate.execute();
        }
    }
    
    public static class TransactionalGetBrandByIdUseCase implements GetBrandByIdUseCase {
        private final GetBrandByIdUseCase delegate;
        
        public TransactionalGetBrandByIdUseCase(GetBrandByIdUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional(readOnly = true)
        public com.ecoapi.goodshopping.product.domain.model.Brand execute(Long id) {
            return delegate.execute(id);
        }
    }
    
    public static class TransactionalUpdateBrandUseCase implements UpdateBrandUseCase {
        private final UpdateBrandUseCase delegate;
        
        public TransactionalUpdateBrandUseCase(UpdateBrandUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public com.ecoapi.goodshopping.product.domain.model.Brand updateBrand(Long id, String name) {
            return delegate.updateBrand(id, name);
        }
    }
    
    public static class TransactionalDeleteBrandUseCase implements DeleteBrandUseCase {
        private final DeleteBrandUseCase delegate;
        
        public TransactionalDeleteBrandUseCase(DeleteBrandUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public void deleteBrand(Long id) {
            delegate.deleteBrand(id);
        }
    }
}
