package toptal.todo.service;

import org.springframework.stereotype.Service;
import toptal.todo.domain.TodoItem;

import java.util.Collections;
import java.util.List;

@Service
public class TodoService {
    public String createTodoItem(String nickname, TodoItem item) {
        return "";
    }

    public TodoItem getTodoItemById(String id) {
        return new TodoItem();
    }

    public List<TodoItem> getAllTodoItems() {
        return Collections.EMPTY_LIST;
    }

    public void updateTodoItem(TodoItem item) {
    }

    public void deleteTodoItem(String id) {
    }

    public List<String> suggestTitles(String text) {
        return Collections.EMPTY_LIST;
    }

    public List<TodoItem> filter(String text) {
        return Collections.EMPTY_LIST;
    }
}
