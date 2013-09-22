package toptal.todo.mongo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import toptal.todo.domain.Session;

public interface SessionRepository extends MongoRepository<Session, ObjectId> {
}
