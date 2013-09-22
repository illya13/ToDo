package toptal.todo.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import toptal.todo.domain.User;

public interface UserRepository extends MongoRepository<User, String> {
}
