package BaiTap8.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import BaiTap8.entity.Category;
import BaiTap8.entity.Product;
import BaiTap8.model.ProductModel;
import BaiTap8.service.ICategoryService;
import BaiTap8.service.IProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/admin/products")
public class ProductAdminAPIController {

    @Autowired
    private IProductService productService;

    @Autowired
    private ICategoryService categoryService;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/product";

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Object> getProductById(@PathVariable Long productId) {
        Optional<Product> opt = productService.findById(productId);
        return opt.<ResponseEntity<Object>>map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>("Không tìm thấy sản phẩm!", HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Object> createProduct(@ModelAttribute ProductModel productModel,
            @RequestParam(value = "images", required = false) MultipartFile images) {
        try {
            Product entity = new Product();
            BeanUtils.copyProperties(productModel, entity);
            entity.setCreateDate(new Date());
            entity.setStatus((short) 1);

            Optional<Category> category = categoryService.findById(productModel.getCategoryId());
            if (category.isEmpty()) {
                return new ResponseEntity<>("Không tìm thấy danh mục!", HttpStatus.BAD_REQUEST);
            }
            entity.setCategory(category.get());

            // Xử lý upload file
            if (images != null && !images.isEmpty()) {
                String fileName = StringUtils.cleanPath(images.getOriginalFilename());
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);

                try (var inputStream = images.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                entity.setImages(fileName);
            }

            Product saved = productService.save(entity);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Lỗi khi xử lý file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Lỗi khi tạo sản phẩm: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Object> updateProduct(@PathVariable Long productId,
            @ModelAttribute ProductModel productModel,
            @RequestParam(value = "images", required = false) MultipartFile images) {
        try {
            Optional<Product> opt = productService.findById(productId);
            if (opt.isEmpty()) {
                return new ResponseEntity<>("Không tìm thấy sản phẩm!", HttpStatus.NOT_FOUND);
            }

            Product entity = opt.get();
            BeanUtils.copyProperties(productModel, entity, "productId", "createDate", "status", "images");

            Optional<Category> category = categoryService.findById(productModel.getCategoryId());
            if (category.isEmpty()) {
                return new ResponseEntity<>("Không tìm thấy danh mục!", HttpStatus.BAD_REQUEST);
            }
            entity.setCategory(category.get());

            if (images != null && !images.isEmpty()) {
                String fileName = StringUtils.cleanPath(images.getOriginalFilename());
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);

                try (var inputStream = images.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                entity.setImages(fileName);
            }

            Product updated = productService.save(entity);
            return new ResponseEntity<>(updated, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Lỗi khi xử lý file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Lỗi khi cập nhật sản phẩm: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        Optional<Product> opt = productService.findById(productId);
        if (opt.isPresent()) {
            productService.deleteById(productId);
            return new ResponseEntity<>("Đã xóa sản phẩm thành công.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Không tìm thấy sản phẩm để xóa!", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{productId}/categoryId")
    public ResponseEntity<Object> getCategoryIdByProductId(@PathVariable Long productId) {
        Optional<Long> categoryId = productService.findCategoryIdByProductId(productId);
        if (categoryId.isPresent()) {
            return new ResponseEntity<>(categoryId.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Không tìm thấy sản phẩm!", HttpStatus.NOT_FOUND);
    }
}