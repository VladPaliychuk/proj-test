package edu.pzks.projtest;

import edu.pzks.projtest.model.Product;
import edu.pzks.projtest.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
public class ProjRepositoryTest {

    @Autowired
    ProductRepository underTest;

    @BeforeEach
    void setUp() {
        Product p1 = new Product("Name1", "Code1", "Desc1", 100);
        Product p2 = new Product("Name2", "Code2", "Desc2", 200);
        Product p3 = new Product("Name3", "Code3", "Desc3", 300);
        underTest.saveAll(List.of(p1, p2, p3));
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll(underTest.findAll());
    }

    @Test
    void shouldSaveNewProduct() {
        // given
        Product newProduct = new Product("Name4", "Code4", "Desc4", 400);

        // when
        underTest.save(newProduct);

        // then
        Product productFromDb = underTest.findById(newProduct.getId()).orElse(null);
        assertNotNull(productFromDb);
        assertEquals(newProduct.getName(), productFromDb.getName());
        assertEquals(newProduct.getCode(), productFromDb.getCode());
    }

    @Test
    void shouldDeleteProductById() {
        // given
        Product productToDelete = new Product("Name5", "Code5", "Desc5", 500);
        underTest.save(productToDelete);

        // when
        underTest.deleteById(productToDelete.getId());

        // then
        assertFalse(underTest.findById(productToDelete.getId()).isPresent());
    }

    @Test
    void shouldFindProductByName() {
        // given
        Product productToFind = new Product("Name6", "Code6", "Desc6", 600);
        underTest.save(productToFind);

        // when
        Product foundProduct = underTest.findAll().stream()
                .filter(product -> product.getName().equals("Name6"))
                .findFirst().orElse(null);

        // then
        assertNotNull(foundProduct);
        assertEquals(productToFind.getName(), foundProduct.getName());
    }

    @Test
    void shouldReturnAllProducts() {
        // when
        List<Product> allProducts = underTest.findAll();

        // then
        assertEquals(3, allProducts.size()); // Має бути 3, включаючи продукти з setUp()
    }

    @Test
    void shouldFindProductsByDescription() {
        // given
        Product product1 = new Product("Name7", "Code7", "###test1", 700);
        Product product2 = new Product("Name8", "Code8", "###test2", 800);
        underTest.saveAll(List.of(product1, product2));

        // when
        List<Product> foundProducts = underTest.findAll().stream()
                .filter(product -> product.getDescription().contains("###test"))
                .toList();

        // then
        assertEquals(2, foundProducts.size()); // Має бути 2, оскільки ми додали два продукти з ###test у опис
    }
}
