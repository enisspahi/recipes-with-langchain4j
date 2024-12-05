package com.enisspahi.spring_recipes_ai.service;

import com.enisspahi.spring_recipes_ai.model.Recipe;
import com.enisspahi.spring_recipes_ai.repository.RecipesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class RecipesService {

    private final RecipesRepository recipesRepository;


    public RecipesService(RecipesRepository recipesRepository) {
        this.recipesRepository = recipesRepository;
    }

    public List<Recipe> search(List<String> titles, List<String> ingredients, List<String> nutritionFacts) {
        return recipesRepository.findBy(titles, ingredients, nutritionFacts);
    }


}

