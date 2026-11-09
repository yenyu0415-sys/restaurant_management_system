package com.example.demo.controller;

import com.example.demo.entity.Menu;
import com.example.demo.service.MenuServiceJPA;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menus")
public class MenuControllerJPA {

    private final MenuServiceJPA menuServiceJPA;

    public MenuControllerJPA(MenuServiceJPA menuServiceJPA) {
        this.menuServiceJPA = menuServiceJPA;
    }

    // 新增菜單
    @PostMapping
    public Menu createMenu(@RequestBody Menu menu) {
        return menuServiceJPA.saveMenu(menu);
    }

    // 查詢所有菜單
    @GetMapping
    public List<Menu> getAllMenus() {
        return menuServiceJPA.getAllMenus();
    }
}
