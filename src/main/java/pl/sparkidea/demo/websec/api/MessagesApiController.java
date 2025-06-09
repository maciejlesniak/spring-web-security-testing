package pl.sparkidea.demo.websec.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessagesApiController {

    private static final Logger LOG = LoggerFactory.getLogger(MessagesApiController.class);

    private final MessagesReaderService messagesReaderService;

    MessagesApiController(MessagesReaderService messagesReaderService) {
        this.messagesReaderService = messagesReaderService;
    }

    @GetMapping("/v1/messages")
    ResponseEntity<List<MessageResponseDto>> allMessages(Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() != null) {
            LOG.info("Request authenticated on behalf of: [{}]", authentication.getPrincipal().toString());
        }

        var dtos = messagesReaderService.getMessages();
        return ResponseEntity.ok(dtos);
    }

}
