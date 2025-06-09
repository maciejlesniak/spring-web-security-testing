package pl.sparkidea.demo.websec.messages;

import org.springframework.data.repository.CrudRepository;
import pl.sparkidea.demo.websec.domain.MessageEntity;

interface MessagesRepository extends CrudRepository<MessageEntity, Long> {

}
