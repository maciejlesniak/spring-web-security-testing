package pl.sparkidea.demo.websec.messages;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app.messages", name = "creation", havingValue = "auto")
class DataProviderConfig {

    @Bean
    MessagesCreationService messagesCreationService(MessagesRepository messagesRepository) {
        return new MessagesCreationService(messagesRepository);
    }

}
