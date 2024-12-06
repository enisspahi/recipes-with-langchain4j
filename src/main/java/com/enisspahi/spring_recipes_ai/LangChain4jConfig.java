package com.enisspahi.spring_recipes_ai;

import com.enisspahi.spring_recipes_ai.model.Recipe;
import com.enisspahi.spring_recipes_ai.repository.RecipesRepository;
import com.enisspahi.spring_recipes_ai.service.Nutritionist;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
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
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    @Bean
    ContentRetriever createContentRetriever(RecipesRepository recipesRepository) {

        var formattedRecipeSegment = "\"%s\" is made of the following ingredients: [%s]";
        var segments = recipesRepository.findAll().stream()
                .map(recipe -> formattedRecipeSegment.formatted(recipe.title(), recipe.ingredients().stream().map(Recipe.Ingredient::name).collect(Collectors.joining(", "))))
                .map(TextSegment::from)
                .toList();

        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddings, segments);

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5) // on each interaction we will retrieve the 5 most relevant segments
                .minScore(0.75) // we want to retrieve segments at least somewhat similar to user query
                .build();
    }

}
