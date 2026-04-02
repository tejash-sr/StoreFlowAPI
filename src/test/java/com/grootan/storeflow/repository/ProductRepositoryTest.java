package com.grootan.storeflow.repository;

import com.grootan.storeflow.enums.ProductStatus;
import com.grootan.storeflow.models.Category;
import com.grootan.storeflow.models.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void saveProduct_savesCorrectly() {
        Category cat = categoryRepository.save(Category.builder().name("Cat").build());
        Product product = Product.builder()
                .name("Laptop")
                .description("Good laptop")
                .sku("SKU-123")
                .price(new BigDecimal("1000.00"))
                .stock(10)
                .category(cat)
                .status(ProductStatus.ACTIVE)
                .build();
        Product saved = productRepository.save(product);

        assertNotNull(saved.getId());
        assertEquals("SKU-123", saved.getSku());
    }

    @Test
    public void findBySku_returnsProduct() {
        Category cat = categoryRepository.save(Category.builder().name("Cat2").build());
        Product product = Product.builder()
                .name("Phone")
                .description("Good phone")
                .sku("SKU-456")
                .price(new BigDecimal("500.00"))
                .stock(5)
                .category(cat)
                .build();
        productRepository.save(product);

        Optional<Product> found = productRepository.findBySku("SKU-456");
        assertTrue(found.isPresent());
        assertEquals("Phone", found.get().getName());
    }

    @Test
    public void existsBySku_returnsTrueIfExists() {
        Category cat = categoryRepository.save(Category.builder().name("Cat3").build());
        Product product = Product.builder()
                .name("Watch")
                .description("Good watch")
                .sku("SKU-789")
                .price(new BigDecimal("200.00"))
                .stock(2)
                .category(cat)
                .build();
        productRepository.save(product);

        assertTrue(productRepository.existsBySku("SKU-789"));
        assertFalse(productRepository.existsBySku("FAKE-SKU"));
    }
}
