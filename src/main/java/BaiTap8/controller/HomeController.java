package BaiTap8.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/view/api/admin";
    }
    
    @GetMapping("/home")
    public String publicHome(Model model) {
        model.addAttribute("title", "Welcome to BaiTap8");
        return "public/home";
    }
    
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About Us");
        return "public/about";
    }
}