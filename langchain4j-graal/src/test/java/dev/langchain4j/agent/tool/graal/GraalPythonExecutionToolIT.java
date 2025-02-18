package dev.langchain4j.agent.tool.graal;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GraalPythonExecutionToolIT {

    interface Assistant {

        String chat(String userMessage);
    }

    @Test
    public void should_execute_tool() {

        GraalPythonExecutionTool tool = spy(new GraalPythonExecutionTool());

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(OpenAiChatModel.withApiKey(System.getenv("OPENAI_API_KEY")))
                .tools(tool)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        String answer = assistant.chat("What is the square root of 485906798473894056 in scientific notation?");

        assertThat(answer).contains("6.97");

        verify(tool).executePythonCode(contains("485906798473894056"));
    }
}