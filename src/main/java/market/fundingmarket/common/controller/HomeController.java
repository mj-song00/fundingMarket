package market.fundingmarket.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login.html";  // 루트 경로 접근 시 login.html로 리다이렉트
    }
}