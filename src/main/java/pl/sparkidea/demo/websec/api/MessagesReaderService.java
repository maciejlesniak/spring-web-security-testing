package pl.sparkidea.demo.websec.api;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
class MessagesReaderService {

    private final MessagesReaderRepository readerRepository;

    public MessagesReaderService(MessagesReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    List<MessageResponseDto> getMessages() {
        return readerRepository.findAll().stream()
                .map(entity -> new MessageResponseDto(entity.getMessage()))
                .toList();

    }

}
