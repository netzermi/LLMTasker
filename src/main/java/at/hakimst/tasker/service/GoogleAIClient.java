package at.hakimst.tasker.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleAIClient {

    // model can be overridden via application properties: google.ai.model
    @Value("${google.ai.model:gemini-2.5-flash}")
    private String model;

    private final Client client;

    public GoogleAIClient() {
        // The Client will pick up GEMINI_API_KEY from the environment if present
        this.client = new Client();
    }

    /**
     * Generate text using the configured model.
     * This mirrors the simple usage of the GenAI client:
     *   GenerateContentResponse response = client.models.generateContent(model, prompt, null);
     */
    public String generateText(String prompt) {
        try {
            GenerateContentResponse response = client.models.generateContent(model, prompt, null);
            // response.text() returns the generated text (convenience method)
            return response.text();
        } catch (Exception e) {
            // Wrap so callers get a consistent runtime exception type
            throw new GoogleAIException("Failed to generate content: " + e.getMessage(), e);
        }
    }
}

class GoogleAIException extends RuntimeException {
    public GoogleAIException(String message) {
        super(message);
    }

    public GoogleAIException(String message, Throwable cause) {
        super(message, cause);
    }
}
