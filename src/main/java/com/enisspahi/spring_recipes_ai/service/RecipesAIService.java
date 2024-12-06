package com.enisspahi.spring_recipes_ai.service;

import com.enisspahi.spring_recipes_ai.model.Recipe;
import com.enisspahi.spring_recipes_ai.repository.RecipesRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RecipesAIService {

    private final RecipesRepository recipesRepository;

    private final Nutritionist nutritionist;

    public RecipesAIService(RecipesRepository recipesRepository, Nutritionist nutritionist) {
        this.recipesRepository = recipesRepository;
        this.nutritionist = nutritionist;
    }

    public List<Recipe.NutritionFact> suggestNutritionFacts(String prompt) {
        return nutritionist.suggest(prompt);
    }

    public List<Recipe> search(String ingredientsPrompt, List<String> nutritionFacts) {
        var suggestedRecipes = nutritionist.suggestRecipes(ingredientsPrompt, nutritionFacts);
        var sanitized = suggestedRecipes.stream().map(entry -> entry.replaceAll("-", "").strip().stripIndent().stripLeading().stripTrailing()).toList();
        System.out.println("suggestedRecipes : " + sanitized + " for nutrition facts " + nutritionFacts + " prompt " + ingredientsPrompt);
        return recipesRepository.findBy(sanitized, Collections.emptyList(), nutritionFacts);
    }

}

