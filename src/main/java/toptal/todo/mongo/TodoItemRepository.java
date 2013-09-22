package toptal.todo.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import toptal.todo.domain.TodoItem;

public interface TodoItemRepository extends MongoRepository<TodoItem, String> {
}
