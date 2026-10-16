package id.my.hendisantika.mongomultitenantdemo5.repository;

import id.my.hendisantika.mongomultitenantdemo5.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
 * Repository interface for Product entities.
 */
@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    /**
     * Find products by name.
     *
     * @param name the name to search for
     * @return a list of products with the given name
     */
    List<Product> findByName(String name);
    
    /**
     * Find products with stock less than the given value.
     *
     * @param stock the stock threshold
     * @return a list of products with stock less than the given value
     */
    List<Product> findByStockLessThan(Integer stock);
}