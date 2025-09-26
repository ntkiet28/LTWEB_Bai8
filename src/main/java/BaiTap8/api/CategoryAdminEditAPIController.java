package BaiTap8.api;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import BaiTap8.entity.Category;
import BaiTap8.model.CategoryModel;
import BaiTap8.service.ICategoryService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/admin/categories")
public class CategoryAdminEditAPIController {

	@Autowired
	private ICategoryService categoryService;

	// Đường dẫn tuyệt đối đến thư mục static
	private static final String UPLOAD_DIR = "src/main/resources/static/images/category";

	@GetMapping
	public ResponseEntity<List<Category>> getAllCategories() {
		List<Category> list = categoryService.findAll();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Object> addCategory(@ModelAttribute("category") CategoryModel cateModel,
			@RequestParam("iconFile") MultipartFile iconFile) {
		try {
			if (!iconFile.isEmpty()) {
				String fileName = StringUtils.cleanPath(iconFile.getOriginalFilename());
				Path uploadPath = Paths.get(UPLOAD_DIR);

				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				Path filePath = uploadPath.resolve(fileName);
				Files.copy(iconFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				cateModel.setIcon(fileName);
			}

			Category entity = new Category();
			BeanUtils.copyProperties(cateModel, entity);

			categoryService.save(entity);
			return new ResponseEntity<>(entity, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>("Lưu ảnh thất bại: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{categoryId}")
	public ResponseEntity<Object> getCategoryById(@PathVariable("categoryId") Long categoryId) {
		Optional<Category> optCategory = categoryService.findById(categoryId);
		if (optCategory.isPresent()) {
			return new ResponseEntity<>(optCategory.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>("Không tìm thấy danh mục!", HttpStatus.NOT_FOUND);
	}

	@PutMapping("/{categoryId}")
	public ResponseEntity<Object> updateCategory(@PathVariable("categoryId") Long categoryId,
			@ModelAttribute("category") CategoryModel cateModel,
			@RequestParam(value = "iconFile", required = false) MultipartFile iconFile) {
		Optional<Category> optCategory = categoryService.findById(categoryId);
		if (!optCategory.isPresent()) {
			return new ResponseEntity<>("Không tìm thấy danh mục để cập nhật!", HttpStatus.NOT_FOUND);
		}

		Category entity = optCategory.get();
		try {
			entity.setCategoryName(cateModel.getCategoryName());
			if (iconFile != null && !iconFile.isEmpty()) {
				String fileName = StringUtils.cleanPath(iconFile.getOriginalFilename());
				Path uploadPath = Paths.get(UPLOAD_DIR);

				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				Path filePath = uploadPath.resolve(fileName);
				Files.copy(iconFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				entity.setIcon(fileName);
			}

			categoryService.save(entity);
			return new ResponseEntity<>(entity, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Cập nhật ảnh thất bại: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{categoryId}")
	public ResponseEntity<String> deleteCategory(@PathVariable("categoryId") Long categoryId) {
		Optional<Category> optCategory = categoryService.findById(categoryId);
		if (!optCategory.isPresent()) {
			return new ResponseEntity<>("Không tìm thấy danh mục để xóa!", HttpStatus.NOT_FOUND);
		}
		categoryService.deleteById(categoryId);
		return new ResponseEntity<>("Danh mục đã được xóa!", HttpStatus.OK);
	}
}