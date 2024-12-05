package com.enisspahi.spring_recipes_ai.repository;

import com.enisspahi.spring_recipes_ai.model.Recipe;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
class RecipesYmlRepository implements RecipesRepository {

    private final List<Recipe> recipes = YamlSourceReader.readFromYaml();


    @Override
    public List<Recipe> findBy(List<String> title, List<String> ingredients, List<String> nutritionFacts) {
        return recipes.stream()
                .filter(filterByTitle(title))
                .filter(filterByIngredients(ingredients))
                .filter(filterByNutritionFacts(nutritionFacts))
                .collect(Collectors.toList());
    }

    @Override
    public List<Recipe> findAll() {
        return findBy(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }


    private static Predicate<Recipe> filterByTitle(List<String> titleSearch) {
        return recipe -> {
            if (titleSearch == null || titleSearch.isEmpty()) { return true; }
            return titleSearch.stream()
                    .map(String::toLowerCase)
                    .anyMatch(searchedTitle -> recipe.title().toLowerCase().contains(searchedTitle.toLowerCase()));
        };
    }

    private static Predicate<Recipe> filterByIngredients(List<String> ingredients) {
        return recipe -> ingredients.stream()
                .map(String::toLowerCase)
                .allMatch(searchedIngredient ->
                        recipe.ingredients().stream()
                                .map(Recipe.Ingredient::name)
                                .map(String::toLowerCase)
                                .anyMatch(ingredientOfRecipe -> ingredientOfRecipe.contains(searchedIngredient))
                );
    }

    private static Predicate<Recipe> filterByNutritionFacts(List<String> nutritionFacts) {
        return recipe -> nutritionFacts.stream()
                .allMatch(searchedNutritionFact ->
                        recipe.nutritionFacts().stream()
                                .anyMatch(nutritionFactOfRecipe -> nutritionFactOfRecipe.name().equals(searchedNutritionFact))
                );
    }

}
