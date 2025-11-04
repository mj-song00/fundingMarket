package market.fundingmarket.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/home.html";  // 루트 경로 접근 시 home.html로 리다이렉트
    }

    @GetMapping("/success")
    public String success(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam int amount, Model model
    ) {
        System.out.println("paymentKey = " + paymentKey);
        System.out.println("orderId = " + orderId);
        System.out.println("amount = " + amount);
        model.addAttribute("paymentKey",  paymentKey);
        model.addAttribute("amount", amount);
        model.addAttribute("orderId", orderId);
        return "/success";
    }


    @GetMapping("/fail")
    public String fail() {
        return "/fail";
    }
}