package com.grootan.storeflow.repository;

import com.grootan.storeflow.models.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import com.grootan.storeflow.AbstractRepositoryTest;

public class CategoryRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void saveCategory_savesCorrectly() {
        Category cat = Category.builder()
                .name("Electronics")
                .description("Electronic items")
                .build();
        Category saved = categoryRepository.save(cat);

        assertNotNull(saved.getId());
        assertEquals("Electronics", saved.getName());
    }

    @Test
    public void findByName_returnsCategory() {
        Category cat = Category.builder()
                .name("Books")
                .build();
        categoryRepository.save(cat);

        Optional<Category> found = categoryRepository.findByName("Books");
        assertTrue(found.isPresent());
    }

    @Test
    public void existsByName_returnsTrueIfExists() {
        Category cat = Category.builder()
                .name("Toys")
                .build();
        categoryRepository.save(cat);

        assertTrue(categoryRepository.existsByName("Toys"));
        assertFalse(categoryRepository.existsByName("Fake"));
    }

    @Test
    public void findByParentIsNull_returnsRootCategories() {
        Category root1 = Category.builder().name("Root 1").build();
        Category root2 = Category.builder().name("Root 2").build();
        categoryRepository.save(root1);
        categoryRepository.save(root2);

        Category child1 = Category.builder().name("Child 1").parent(root1).build();
        categoryRepository.save(child1);

        List<Category> roots = categoryRepository.findByParentIsNull();
        assertTrue(roots.size() >= 2);
        assertTrue(roots.stream().anyMatch(c -> c.getName().equals("Root 1")));
        assertTrue(roots.stream().anyMatch(c -> c.getName().equals("Root 2")));
        assertFalse(roots.stream().anyMatch(c -> c.getName().equals("Child 1")));
    }
}
