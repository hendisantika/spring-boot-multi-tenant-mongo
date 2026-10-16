package id.my.hendisantika.mongomultitenantdemo5.controller;

import id.my.hendisantika.mongomultitenantdemo5.config.TenantContext;
import id.my.hendisantika.mongomultitenantdemo5.model.Product;
import id.my.hendisantika.mongomultitenantdemo5.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for Product entities.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    /**
     * Get all products.
     *
     * @return a list of all products
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }
    
    /**
     * Get a product by ID.
     *
     * @param id the ID to search for
     * @return the product if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Optional<Product> product = productService.findById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get products by name.
     *
     * @param name the name to search for
     * @return a list of products with the given name
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> getProductsByName(@RequestParam String name) {
        return ResponseEntity.ok(productService.findByName(name));
    }
    
    /**
     * Get products with stock less than the given value.
     *
     * @param stock the stock threshold
     * @return a list of products with stock less than the given value
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getProductsWithLowStock(@RequestParam Integer stock) {
        return ResponseEntity.ok(productService.findByStockLessThan(stock));
    }
    
    /**
     * Create a new product.
     *
     * @param product the product to create
     * @return the created product
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return new ResponseEntity<>(productService.save(product), HttpStatus.CREATED);
    }
    
    /**
     * Update an existing product.
     *
     * @param id the ID of the product to update
     * @param product the updated product data
     * @return the updated product if found, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        Optional<Product> existingProduct = productService.findById(id);
        if (existingProduct.isPresent()) {
            product.setId(id);
            return ResponseEntity.ok(productService.save(product));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete a product by ID.
     *
     * @param id the ID of the product to delete
     * @return 204 No Content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get the current tenant ID.
     *
     * @return the current tenant ID
     */
    @GetMapping("/tenant-info")
    public ResponseEntity<String> getTenantInfo() {
        return ResponseEntity.ok("Current tenant: " + TenantContext.getTenantId());
    }
}