package toptal.todo.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import toptal.todo.domain.Session;

public interface SessionRepository extends MongoRepository<Session, String> {
}
