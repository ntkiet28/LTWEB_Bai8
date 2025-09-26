package BaiTap8.service;

import java.util.List;
import java.util.Optional;

import BaiTap8.entity.Product;

public interface IProductService {

    <S extends Product> S save(S entity);

    List<Product> findAll();

    Optional<Product> findById(Long id);

    void deleteById(Long id);

    long count();

    Optional<Long> findCategoryIdByProductId(Long productId);
}