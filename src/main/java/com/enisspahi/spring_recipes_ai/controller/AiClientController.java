package com.enisspahi.spring_recipes_ai.controller;

import com.enisspahi.spring_recipes_ai.service.RecipesAIService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AiClientController {

    private final RecipesAIService recipesAIService;

    public AiClientController(RecipesAIService recipesAIService) {
        this.recipesAIService = recipesAIService;
    }

    @GetMapping("/searchAI")
    public String showForm(Model model) {
        if (!model.containsAttribute("nutritionFactsPrompt")) {
            model.addAttribute("nutritionFactsPrompt", null);
        }
        if (!model.containsAttribute("suggestedNutritionFacts")) {
            model.addAttribute("suggestedNutritionFacts", null);
        }
        return "searchAI"; // Thymeleaf view
    }

    @PostMapping("/handle-nutrition-prompt")
    public String handleNutritionFactsPrompt(
            @RequestParam("nutritionFactsPrompt") String nutritionFactsPrompt,
            RedirectAttributes redirectAttributes) {

        var suggestedNutritionFacts = recipesAIService.suggestNutritionFacts(nutritionFactsPrompt);
        redirectAttributes.addFlashAttribute("suggestedNutritionFacts", suggestedNutritionFacts);

        return "redirect:/searchAI";
    }

    @PostMapping("/ai-search")
    public String handleAiSearch(@RequestParam("intermediateResult") List<String> intermediateResult,
                                 @RequestParam("ingredientsPrompt") String ingredientsPrompt,
                                 Model model) {

        var recipes = recipesAIService.search(ingredientsPrompt, intermediateResult);

        model.addAttribute("recipes", recipes);
        return "result";
    }

}
