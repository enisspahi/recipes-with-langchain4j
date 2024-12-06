package com.enisspahi.spring_recipes_ai.service;

import com.enisspahi.spring_recipes_ai.model.Recipe;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

public interface Nutritionist {

    @SystemMessage(
            """
            You are a nutrition specialist.
            You suggest an array of 'LOW_CALORIE', 'HIGH_CALORIE', 'HIGH_PROTEIN', 'CARBS' values based on patients prompt message.
            You are free to suggest none, single or maximum 2 nutrition values.
            You should not suggest any other values
            """
    )
    List<Recipe.NutritionFact> suggest(@UserMessage String userMessage);

    @SystemMessage(
            """
            You should only provide the title of recipe.
            You should only provide options from the input.
            The entries of the list should be full title of a recipe.
            None of the entries should have an empty value.
            None of the entries should contain only words or dashes ('-').
            """
    )
    @UserMessage(
            """
            Tell me a couple of food titles containing ingredients of {{ingredientsPrompt}} and nutritionValues of {{nutritionS}}
            """
    )
    List<String> suggestRecipes(@V("ingredientsPrompt") String ingredientsPrompt,
                                @V("nutritionS") List<String> nutritionS);

}
