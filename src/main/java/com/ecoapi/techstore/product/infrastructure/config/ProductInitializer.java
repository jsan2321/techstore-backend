package com.ecoapi.techstore.product.infrastructure.config;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.product.application.port.out.BrandRepositoryPort;
import com.ecoapi.techstore.product.application.port.out.CategoryRepositoryPort;
import com.ecoapi.techstore.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.techstore.product.domain.model.Brand;
import com.ecoapi.techstore.product.domain.model.Category;
import com.ecoapi.techstore.product.domain.model.ImageUrl;
import com.ecoapi.techstore.product.domain.model.Product;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ProductInitializer {

    @Bean
    public CommandLineRunner initProducts(
            ProductRepositoryPort productRepository,
            CategoryRepositoryPort categoryRepository,
            BrandRepositoryPort brandRepository) {
        return args -> {
            log.info("Starting database seeding for categories, brands, and products...");

            // 1. Seed Categories
            String[] categoryNames = {
                "Smartphones", "Headphones", "Wearables", "Cameras",
                "Gaming Consoles", "TVs", "Computer Accessories", "Smart Home", "Storage & Memory"
            };
            Map<String, Category> categoryMap = new HashMap<>();
            for (String catName : categoryNames) {
                Category category = categoryRepository.findByName(catName)
                    .orElseGet(() -> {
                        Category newCat = categoryRepository.save(new Category(catName));
                        log.info("✅ Data Seeding: Created category -> {}", catName);
                        return newCat;
                    });
                categoryMap.put(catName, category);
            }

            // 2. Seed Brands
            String[] brandNames = {
                "Samsung", "Apple", "Google", "Xiaomi", "Sony", "Logitech", "Dell"
            };
            Map<String, Brand> brandMap = new HashMap<>();
            for (String brName : brandNames) {
                Brand brand = brandRepository.findByName(brName)
                    .orElseGet(() -> {
                        Brand newBrand = brandRepository.save(new Brand(brName));
                        log.info("✅ Data Seeding: Created brand -> {}", brName);
                        return newBrand;
                    });
                brandMap.put(brName, brand);
            }

            // 3. Seed Featured Products if they don't already exist
            seedFeaturedProduct(
                productRepository,
                "Samsung Ultra 4K Gaming Monitor",
                brandMap.get("Samsung"),
                categoryMap.get("Computer Accessories"),
                new BigDecimal("649.00"),
                30, // 30% discount
                "http://localhost:4200/images/gaming_monitor.png",
                "Experience immersive gaming with the Samsung Ultra 4K Gaming Monitor, featuring a high refresh rate and vibrant color display."
            );

            seedFeaturedProduct(
                productRepository,
                "Samsung Galaxy S21 Phone",
                brandMap.get("Samsung"),
                categoryMap.get("Smartphones"),
                new BigDecimal("999.00"),
                20, // 20% discount
                "http://localhost:4200/images/galaxy_s21.png",
                "The Samsung Galaxy S21 Phone offers a brilliant display, pro-grade camera, and powerful performance."
            );

            seedFeaturedProduct(
                productRepository,
                "Google Pixel 6 Pro",
                brandMap.get("Google"),
                categoryMap.get("Smartphones"),
                new BigDecimal("1099.00"),
                20, // 20% discount
                "http://localhost:4200/images/pixel_6_pro.png",
                "Experience the best of Google with the Pixel 6 Pro, featuring a revolutionary camera and advanced AI features."
            );

            seedFeaturedProduct(
                productRepository,
                "Apple MacBook Air M2",
                brandMap.get("Apple"),
                categoryMap.get("Computer Accessories"),
                new BigDecimal("1399.00"),
                20, // 20% discount
                "http://localhost:4200/images/macbook_air_m2.png",
                "Supercharged by the next-generation M2 chip, the Apple MacBook Air M2 combines incredible performance with silent operation."
            );

            log.info("Database seeding for products completed.");
        };
    }

    private void seedFeaturedProduct(
            ProductRepositoryPort productRepository,
            String name,
            Brand brand,
            Category category,
            BigDecimal price,
            int discountPercentage,
            String imageUrl,
            String description) {
        
        if (productRepository.existsByName(name)) {
            log.info("Featured product already exists: {}", name);
            return;
        }

        Product product = Product.create(
            name,
            brand,
            Money.of(price),
            10, // Stock
            description,
            category
        );

        product.setFeatured(true);
        product.activate();
        if (discountPercentage > 0) {
            product.applyDiscount(discountPercentage);
        }
        product.updateImageUrl(ImageUrl.of(imageUrl));

        productRepository.save(product);
        log.info("✅ Data Seeding: Created featured product -> {}", name);
    }
}
