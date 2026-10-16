package id.my.hendisantika.mongomultitenantdemo5.service;

import id.my.hendisantika.mongomultitenantdemo5.model.Product;
import id.my.hendisantika.mongomultitenantdemo5.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-multi-tenant-mongo
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 16/10/26
 * Time: 13.21
 */
/**
 * Service class for Product entities.
 */
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    /**
     * Find all products.
     *
     * @return a list of all products
     */
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    /**
     * Find a product by ID.
     *
     * @param id the ID to search for
     * @return an Optional containing the product if found, or empty if not found
     */
    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }
    
    /**
     * Find products by name.
     *
     * @param name the name to search for
     * @return a list of products with the given name
     */
    public List<Product> findByName(String name) {
        return productRepository.findByName(name);
    }
    
    /**
     * Find products with stock less than the given value.
     *
     * @param stock the stock threshold
     * @return a list of products with stock less than the given value
     */
    public List<Product> findByStockLessThan(Integer stock) {
        return productRepository.findByStockLessThan(stock);
    }
    
    /**
     * Save a product.
     *
     * @param product the product to save
     * @return the saved product
     */
    public Product save(Product product) {
        return productRepository.save(product);
    }
    
    /**
     * Delete a product by ID.
     *
     * @param id the ID of the product to delete
     */
    public void deleteById(String id) {
        productRepository.deleteById(id);
    }
}