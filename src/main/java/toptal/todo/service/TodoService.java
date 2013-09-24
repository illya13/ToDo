package toptal.todo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import toptal.todo.domain.TodoItem;
import toptal.todo.elasticsearch.ESHelper;
import toptal.todo.mongo.MongoHelper;
import toptal.todo.mongo.TodoItemRepository;

import java.io.IOException;
import java.util.List;

@Service
public class TodoService {
    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private MongoHelper mongoHelper;

    @Autowired
    private ESHelper esHelper;

    public TodoItem createTodoItem(TodoItem item) {
        item = todoItemRepository.save(item);
        try {
            esHelper.reindex(item);
        } catch (IOException ioe) {
            // simulate rollback
            todoItemRepository.delete(item);
            throw new IllegalStateException(ioe);
        }
        return item;
    }

    public TodoItem getTodoItemById(String id) {
        TodoItem todoItem = todoItemRepository.findOne(id);
        if (todoItem == null)
            throw notFound(id);
        return todoItem;
    }

    public List<TodoItem> getAllTodoItems() {
        return todoItemRepository.findAll();
    }

    public TodoItem updateTodoItem(TodoItem item) {
        item = todoItemRepository.save(item);
        try {
            esHelper.reindex(item);
        } catch (IOException ioe) {
            // do nothing
            throw new IllegalStateException(ioe);
        }
        return item;
    }

    public void deleteTodoItem(String id) {
        todoItemRepository.delete(id);
        esHelper.delete(id);
    }

    public List<String> suggestTitles(String text) {
        return esHelper.suggest(text);
    }

    public List<TodoItem> filter(String text, int start, int size) {
        List<String> ids = esHelper.filter(text, start, size);
        return mongoHelper.findItemInSorted(ids);
    }

    private IllegalArgumentException notFound(String id) {
        return new IllegalArgumentException(String.format("Todo item with id=%1$s was not found", id));
    }
}
