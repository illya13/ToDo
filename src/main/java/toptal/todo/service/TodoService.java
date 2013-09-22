package toptal.todo.service;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import toptal.todo.domain.TodoItem;
import toptal.todo.mongo.TodoItemRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static toptal.todo.domain.TodoItem.getXContentBuilder;

@Service
public class TodoService {
    public static final String type = "item";

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private TransportClient transportClient;

    @Autowired
    private String indexName;

    public TodoItem createTodoItem(TodoItem item) {
        item = todoItemRepository.save(item);
        try {
            reindex(item);
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

    public void updateTodoItem(TodoItem item) {
        item = todoItemRepository.save(item);
        try {
            reindex(item);
        } catch (IOException ioe) {
            // do nothing
            throw new IllegalStateException(ioe);
        }
    }

    public void deleteTodoItem(String id) {
        todoItemRepository.delete(id);

        DeleteResponse response = transportClient.prepareDelete(indexName, type, id).
                execute().actionGet();
    }

    public List<String> suggestTitles(String text) {
        return Collections.EMPTY_LIST;
    }

    public List<TodoItem> filter(String text) {
        return Collections.EMPTY_LIST;
    }

    private void reindex(TodoItem item) throws IOException {
        IndexResponse response = transportClient.prepareIndex(indexName, type, null).
                setId(item.getId()).
                setSource(getXContentBuilder(item)).
                execute().actionGet();
    }

    private IllegalArgumentException notFound(String id) {
        return new IllegalArgumentException(String.format("Todo item with id=%1$s was not found", id));
    }
}
