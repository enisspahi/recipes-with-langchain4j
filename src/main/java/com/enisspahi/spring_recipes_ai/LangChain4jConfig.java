package com.enisspahi.spring_recipes_ai;

import com.enisspahi.spring_recipes_ai.model.Recipe;
import com.enisspahi.spring_recipes_ai.repository.RecipesRepository;
import com.enisspahi.spring_recipes_ai.service.Nutritionist;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

@Configuration
public class LangChain4jConfig {

    @Bean
    Nutritionist nutritionist(@Value("${openAIKey:demo}") String apiKey, ContentRetriever contentRetriever) {
        var model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(GPT_4_O_MINI)
                .logRequests(true)
                .logResponses(true)
                .build();

        return AiServices.builder(Nutritionist.class)
                .chatLanguageModel(model)
                .contentRetriever(contentRetriever)
//                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    @Bean
    ContentRetriever createContentRetriever(RecipesRepository recipesRepository) {
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        var documents = recipesRepository.findAll().stream()
                .map(recipe -> "\"" + recipe.title() + "\" is made of the following ingredients: " + recipe.ingredients().stream().map(Recipe.Ingredient::name).collect(Collectors.joining(", ")))
                .map(Document::from)
                .toList();

        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

        return EmbeddingStoreContentRetriever.from(embeddingStore);
    }

}
