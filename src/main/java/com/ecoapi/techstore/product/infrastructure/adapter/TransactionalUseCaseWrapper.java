package com.ecoapi.techstore.product.infrastructure.adapter;

import com.ecoapi.techstore.product.application.port.in.*;
import com.ecoapi.techstore.product.application.port.out.ImageFile;
import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.product.application.service.dto.AdminProductListReadModel;
import com.ecoapi.techstore.product.application.service.dto.AdminProductSearchCriteria;
import com.ecoapi.techstore.product.application.service.dto.ProductCommand;
import com.ecoapi.techstore.product.application.service.dto.ProductReadModel;
import com.ecoapi.techstore.product.domain.model.Product;
import com.ecoapi.techstore.product.application.service.dto.ProductSearchCriteria;
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

    public static class TransactionalActivateProductUseCase implements ActivateProductUseCase {
        private final ActivateProductUseCase delegate;

        public TransactionalActivateProductUseCase(ActivateProductUseCase delegate) {
            this.delegate = delegate;
        }

        @Override
        @Transactional
        public Product activateProduct(Long productId) {
            return delegate.activateProduct(productId);
        }
    }

    public static class TransactionalDeactivateProductUseCase implements DeactivateProductUseCase {
        private final DeactivateProductUseCase delegate;

        public TransactionalDeactivateProductUseCase(DeactivateProductUseCase delegate) {
            this.delegate = delegate;
        }

        @Override
        @Transactional
        public Product deactivateProduct(Long productId) {
            return delegate.deactivateProduct(productId);
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

    public static class TransactionalSearchProductsByCriteriaUseCase implements SearchProductsByCriteriaUseCase {
        private final SearchProductsByCriteriaUseCase delegate;

        public TransactionalSearchProductsByCriteriaUseCase(SearchProductsByCriteriaUseCase delegate) {
            this.delegate = delegate;
        }

        @Override
        @Transactional(readOnly = true)
        public PagedResult<ProductReadModel> search(ProductSearchCriteria criteria) {
            return delegate.search(criteria);
        }
    }

    public static class TransactionalAdminSearchProductsUseCase implements AdminSearchProductsUseCase {
        private final AdminSearchProductsUseCase delegate;

        public TransactionalAdminSearchProductsUseCase(AdminSearchProductsUseCase delegate) {
            this.delegate = delegate;
        }

        @Override
        @Transactional(readOnly = true)
        public PagedResult<AdminProductListReadModel> search(AdminProductSearchCriteria criteria) {
            return delegate.search(criteria);
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
        public com.ecoapi.techstore.product.domain.model.Category addCategory(String name) {
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
        public List<com.ecoapi.techstore.product.domain.model.Category> execute() {
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
        public com.ecoapi.techstore.product.domain.model.Category execute(Long id) {
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
        public com.ecoapi.techstore.product.domain.model.Category updateCategory(Long id, String name) {
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
        public com.ecoapi.techstore.product.domain.model.Brand addBrand(String name) {
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
        public List<com.ecoapi.techstore.product.domain.model.Brand> execute() {
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
        public com.ecoapi.techstore.product.domain.model.Brand execute(Long id) {
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
        public com.ecoapi.techstore.product.domain.model.Brand updateBrand(Long id, String name) {
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
