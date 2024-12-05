package com.enisspahi.spring_recipes_ai.controller;

import com.enisspahi.spring_recipes_ai.service.RecipesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class ClientController {

    private final RecipesService recipesService;

    public ClientController(RecipesService recipesService) {
        this.recipesService = recipesService;
    }

    @GetMapping("/")
    public String defaultPage(Model model) {
        model.addAttribute("step", "input");
        return "search";
    }

    @PostMapping("/search")
    public String handleSearch(@RequestParam Optional<String> title,
                               @RequestParam Optional<List<String>> ingredients,
                               @RequestParam Optional<List<String>> nutritionFacts,
                               Model model) {
        var recipes = recipesService.search(
                title.stream().toList(),
                ingredients.orElse(Collections.emptyList()),
                nutritionFacts.orElse(Collections.emptyList()));
        model.addAttribute("recipes", recipes);
        return "result";
    }

}
