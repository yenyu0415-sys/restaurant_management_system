package com.example.demo.service;

import com.example.demo.entity.Menu;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.MenuRepositoryJPA;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceJPA {

    private final MenuRepositoryJPA menuRepositoryJPA;

    public MenuServiceJPA(MenuRepositoryJPA menuRepositoryJPA) {

        this.menuRepositoryJPA = menuRepositoryJPA;
    }

    public Menu saveMenu(Menu menu) {

        return menuRepositoryJPA.save(menu);
    }

    public List<Menu> getAllMenus() {

        return menuRepositoryJPA.findAll();
    }

    public Menu getById(Long id) {
        return menuRepositoryJPA.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found:" + id));

    }

    public Menu updateMenu(Long id, String name, int price, String category) {
        Menu menu = getById(id);
        menu.setMenuName(name);
        menu.setPrice(price);
        menu.setCategory(category);
        menuRepositoryJPA.save(menu);
        return menu;
    }

    public void deleteMenu(Long id) {
        menuRepositoryJPA.deleteById(id);
    }
}

