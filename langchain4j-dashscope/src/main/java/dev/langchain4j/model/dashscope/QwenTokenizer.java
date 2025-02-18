package dev.langchain4j.model.dashscope;

import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.tokenizers.Tokenization;
import com.alibaba.dashscope.tokenizers.TokenizationResult;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.Tokenizer;

import java.util.Collections;

import static dev.langchain4j.model.dashscope.QwenHelper.toQwenMessages;

public class QwenTokenizer implements Tokenizer {
    private final String apiKey;
    private final String modelName;
    private final Tokenization tokenizer;

    public QwenTokenizer(String apiKey, String modelName) {
        if (Utils.isNullOrBlank(apiKey)) {
            throw new IllegalArgumentException("DashScope api key must be defined. It can be generated here: https://dashscope.console.aliyun.com/apiKey");
        }
        this.modelName = Utils.isNullOrBlank(modelName) ? QwenModelName.QWEN_PLUS : modelName;
        this.apiKey = apiKey;
        this.tokenizer = new Tokenization();
    }

    @Override
    public int estimateTokenCountInText(String text) {
        try {
            QwenParam param = QwenParam.builder()
                    .apiKey(apiKey)
                    .model(modelName)
                    .prompt(text)
                    .build();

            TokenizationResult result = tokenizer.call(param);
            return result.getUsage().getInputTokens();
        } catch (NoApiKeyException | InputRequiredException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int estimateTokenCountInMessage(ChatMessage message) {
        return estimateTokenCountInMessages(Collections.singleton(message));
    }

    @Override
    public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
        try {
            QwenParam param = QwenParam.builder()
                    .apiKey(apiKey)
                    .model(modelName)
                    .messages(toQwenMessages(messages))
                    .build();

            TokenizationResult result = tokenizer.call(param);
            return result.getUsage().getInputTokens();
        } catch (NoApiKeyException | InputRequiredException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int estimateTokenCountInToolSpecifications(Iterable<ToolSpecification> toolSpecifications) {
        throw new IllegalArgumentException("Tools are currently not supported by this tokenizer");
    }
}
