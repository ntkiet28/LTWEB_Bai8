package BaiTap8.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("admin/categories")
public class CategoryAdminEditController {

	@Autowired
	private ICategoryService categoryService;

	// Đường dẫn tuyệt đối đến thư mục static
	private static final String UPLOAD_DIR = "src/main/resources/static/images/category";

	@GetMapping("add")
	public String add(ModelMap model) {
		CategoryModel cateModel = new CategoryModel();
		cateModel.setIsEdit(false);
		model.addAttribute("category", cateModel);
		return "admin/categories/add";
	}

	@PostMapping("add")
	public ModelAndView addCategory(@ModelAttribute("category") CategoryModel cateModel,
			@RequestParam("iconFile") MultipartFile iconFile,
			ModelMap model) throws Exception {

		if (!iconFile.isEmpty()) {
			// Lấy tên file gốc
			String fileName = StringUtils.cleanPath(iconFile.getOriginalFilename());
			// Tạo đường dẫn đầy đủ
			Path uploadPath = Paths.get(UPLOAD_DIR);

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			try {
				Path filePath = uploadPath.resolve(fileName);
				Files.copy(iconFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				cateModel.setIcon(fileName);
			} catch (Exception e) {
				model.addAttribute("message", "Lưu ảnh thất bại: " + e.getMessage());
				return new ModelAndView("admin/categories/add", model);
			}
		}

		Category entity = new Category();
		BeanUtils.copyProperties(cateModel, entity);

		categoryService.save(entity);
		model.addAttribute("message", "Danh mục đã được lưu!");
		return new ModelAndView("forward:/admin/categories", model);
	}

	@GetMapping("edit/{categoryId}")
	public ModelAndView edit(ModelMap model, @PathVariable("categoryId") Long categoryId) {
		Optional<Category> optCategory = categoryService.findById(categoryId);
		CategoryModel cateModel = new CategoryModel();
		if (optCategory.isPresent()) {
			Category entity = optCategory.get();
			BeanUtils.copyProperties(entity, cateModel);
			cateModel.setIsEdit(true);
			model.addAttribute("category", cateModel);
			return new ModelAndView("admin/categories/edit", model);
		}
		model.addAttribute("message", "Không tìm thấy danh mục!");
		return new ModelAndView("forward:/admin/categories", model);
	}

	@PostMapping("edit")
	public ModelAndView updateCategory(@ModelAttribute("category") CategoryModel cateModel,
			@RequestParam(value = "iconFile", required = false) MultipartFile iconFile,
			ModelMap model) throws Exception {

		Optional<Category> optCategory = categoryService.findById(cateModel.getCategoryId());
		if (optCategory.isPresent()) {
			Category entity = optCategory.get();
			entity.setCategoryName(cateModel.getCategoryName());

			if (iconFile != null && !iconFile.isEmpty()) {
				// Lấy tên file gốc
				String fileName = StringUtils.cleanPath(iconFile.getOriginalFilename());
				// Tạo đường dẫn đầy đủ
				Path uploadPath = Paths.get(UPLOAD_DIR);

				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				try {
					Path filePath = uploadPath.resolve(fileName);
					Files.copy(iconFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
					entity.setIcon(fileName);
				} catch (Exception e) {
					model.addAttribute("message", "Cập nhật ảnh thất bại: " + e.getMessage());
					return new ModelAndView("admin/categories/edit", model);
				}
			}

			categoryService.save(entity);
			model.addAttribute("message", "Danh mục đã được cập nhật!");
		} else {
			model.addAttribute("message", "Không tìm thấy danh mục để cập nhật!");
		}
		return new ModelAndView("forward:/admin/categories", model);
	}

	@GetMapping("delete/{categoryId}")
	public ModelAndView delete(ModelMap model, @PathVariable("categoryId") Long categoryId) {
		categoryService.deleteById(categoryId);
		model.addAttribute("message", "Danh mục đã được xóa!");
		return new ModelAndView("forward:/admin/categories", model);
	}

	@RequestMapping("")
	public String list(ModelMap model) {
		List<Category> list = categoryService.findAll();
		model.addAttribute("categories", list);
		return "admin/categories/list";
	}
}