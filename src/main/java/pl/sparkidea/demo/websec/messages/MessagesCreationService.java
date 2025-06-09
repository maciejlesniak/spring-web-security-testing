package pl.sparkidea.demo.websec.messages;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.sparkidea.demo.websec.domain.MessageEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

class MessagesCreationService {

    private static final Logger LOG = LoggerFactory.getLogger(MessagesCreationService.class);

    public static final int MAX_SIZE = 10;
    private final MessagesRepository messagesRepository;

    public MessagesCreationService(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    @PostConstruct
    void init() {

        var entries = generateEntities();

        LOG.info("Initializing Messages Repository [size: {}]", entries.size());
        messagesRepository.saveAll(entries);
    }

    List<MessageEntity> generateEntities() {
        return Stream.iterate(0, i -> i + 1)
                .limit(MAX_SIZE)
                .map(i -> new MessageEntity(UUID.randomUUID().toString()))
                .toList();
    }

}
