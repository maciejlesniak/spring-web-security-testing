package pl.sparkidea.demo.websec.api;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sparkidea.demo.websec.domain.MessageEntity;

interface MessagesReaderRepository extends JpaRepository<MessageEntity, Long> {

}
