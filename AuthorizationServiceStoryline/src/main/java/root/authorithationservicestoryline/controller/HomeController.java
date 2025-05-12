package root.authorithationservicestoryline.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "Hello, home";
    }

    @GetMapping("user/home")
    public String check() {
        return "Hello, home";
    }

    @GetMapping("admin/home")
    public String secured() {
        return "Hello, secured ";
    }
}
