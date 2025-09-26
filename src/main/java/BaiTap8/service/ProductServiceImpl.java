package BaiTap8.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import BaiTap8.entity.Product;
import BaiTap8.repo.ProductRepository;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public <S extends Product> S save(S entity) {
        return productRepository.save(entity);
        // <S extends Product> tức là định nghĩa S là mở rộng của Product (các lớp con
        // của Product cũng nhận luôn
        // Sau đó thì public S save(S entity) cũng giống như các hàm khác trả về kiểu S
        // nhận đầu vào là S luôn
        // Khi đó thì ở các tầng khác ta có thể linh thoạt truyền vào Product hoặc con
        // của Product (kế thừa từ Product)
        // Cách viết này là viết theo Generics để tận dụng code tốt hơn
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public long count() {
        return productRepository.count();
    }

    @Override
    public Optional<Long> findCategoryIdByProductId(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        return product.map(p -> p.getCategory().getCategoryId());
    }
}