package BaiTap8.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("view/api/admin")
public class HomeAdminAPI {

	@GetMapping
	public String list() {
		return "admin-ajax/homeadmin/home";
	}

	@GetMapping("/products")
	public String listproduct() {
		return "admin-ajax/products/list-add-edit-product";
	}

	@GetMapping("/categories")
	public String listcategory() {
		return "admin-ajax/categories/list-add-edit";
	}

}
