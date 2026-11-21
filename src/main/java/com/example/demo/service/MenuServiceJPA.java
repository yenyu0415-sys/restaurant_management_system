package com.example.demo.service;

import com.example.demo.entity.Menu;
import com.example.demo.exception.InvalidMenuStateException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.MenuRepositoryJPA;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.awt.SystemColor.menu;

@Service
public class MenuServiceJPA {

    private final MenuRepositoryJPA menuRepositoryJPA;

    public MenuServiceJPA(MenuRepositoryJPA menuRepositoryJPA) {

        this.menuRepositoryJPA = menuRepositoryJPA;
    }

    public Menu saveMenu(Menu menu) {

        return menuRepositoryJPA.save(menu);
    }

    public List<Menu> getActiveMenus() {
        return menuRepositoryJPA.findByIsActiveTrue();
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
        return menuRepositoryJPA.save(menu);
    }
    public void deleteMenu(Long id) {
        Menu menu = getById(id);

        if (!menu.isActive()) {
            throw new InvalidMenuStateException(
                    "Menu is already inactive: " + id
            );
        }

        menu.setActive(false);
        menuRepositoryJPA.save(menu);
    }

    public Menu activateMenu(Long id) {
        Menu menu = getById(id);

        if (menu.isActive()) {
            throw new InvalidMenuStateException(
                    "Menu is already active: " + id
            );
        }

        menu.setActive(true);

        return menuRepositoryJPA.save(menu);
    }
}

