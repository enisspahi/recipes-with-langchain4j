package com.enisspahi.spring_recipes_ai.repository;

import com.enisspahi.spring_recipes_ai.model.Recipe;

import java.util.List;

public interface RecipesRepository {
    List<Recipe> findBy(List<String> title, List<String> ingredients, List<String> nutritionFacts);
    List<Recipe> findAll();
}
