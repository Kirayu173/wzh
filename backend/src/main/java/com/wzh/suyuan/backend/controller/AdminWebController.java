package com.wzh.suyuan.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminWebController {

    @GetMapping({"/admin-web", "/admin-web/"})
    public String adminWebIndex() {
        return "forward:/admin-web/index.html";
    }

    @GetMapping({"/admin-web/{path:^(?!.*\\.).*$}", "/admin-web/**/{path:^(?!.*\\.).*$}"})
    public String adminWebSpa() {
        return "forward:/admin-web/index.html";
    }
}
