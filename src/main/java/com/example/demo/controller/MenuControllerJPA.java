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
    @GetMapping("/{id}")
    public Menu getMenuById(@PathVariable Long id){
        return menuServiceJPA.getById(id);
    }
    @GetMapping("/active")
    public List<Menu> getActiveMenus() {
        return menuServiceJPA.getActiveMenus();
    }


    @PutMapping("/{id}")
    public Menu updateMenu(
            @PathVariable Long id,
            @RequestBody Menu menu) {

        return menuServiceJPA.updateMenu(
                id,
                menu.getMenuName(),
                menu.getPrice(),
                menu.getCategory()
        );
    }

    @DeleteMapping("/{id}")
    public void deleteMenu(@PathVariable Long id) {
        menuServiceJPA.deleteMenu(id);
    }

    @PatchMapping("/{id}/activate")
    public Menu activateMenu(@PathVariable Long id) {
      return   menuServiceJPA.activateMenu(id);
    }

}




