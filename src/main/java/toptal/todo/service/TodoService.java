package toptal.todo.service;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionFuzzyBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import toptal.todo.domain.TodoItem;
import toptal.todo.mongo.TodoItemRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
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

    @Autowired
    private MongoTemplate mongoTemplate;

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

    public TodoItem updateTodoItem(TodoItem item) {
        item = todoItemRepository.save(item);
        try {
            reindex(item);
        } catch (IOException ioe) {
            // do nothing
            throw new IllegalStateException(ioe);
        }
        return item;
    }

    public void deleteTodoItem(String id) {
        todoItemRepository.delete(id);

        DeleteResponse response = transportClient.prepareDelete(indexName, type, id).
                execute().actionGet();
    }

    public List<String> suggestTitles(String text) {
        SuggestResponse response = transportClient.prepareSuggest(indexName).
                addSuggestion(new CompletionSuggestionFuzzyBuilder("title").
                        field("title.completion").text(text).size(5)).execute().actionGet();

        List<String> strings = new LinkedList<String>();
        for(Suggest.Suggestion.Entry entry: response.getSuggest().getSuggestion("title").getEntries())
            for(Object object: entry.getOptions()) {
                Suggest.Suggestion.Entry.Option option = (Suggest.Suggestion.Entry.Option)object;
                strings.add(option.getText().string());
            }

        return strings;
    }

    public List<TodoItem> filter(String text, int start, int size) {
        SearchResponse response = transportClient.prepareSearch(indexName).
                setTypes(type).
                setSearchType(SearchType.DFS_QUERY_THEN_FETCH).
                setQuery(QueryBuilders.queryString(text)).
                setFrom(start).setSize(size).setExplain(true).
                execute().actionGet();

        List<String> ids = new LinkedList<String>();
        for (SearchHit hit: response.getHits())
            ids.add(hit.getId());

        return findItemInSorted(ids);
    }

    private void reindex(TodoItem item) throws IOException {
        IndexResponse response = transportClient.prepareIndex(indexName, type, null).
                setId(item.getId()).
                setSource(getXContentBuilder(item)).
                execute().actionGet();
    }

    private List<TodoItem> findItemInSorted(List<String> ids) {
        Query query = new Query(Criteria.where("_id").in(ids));
        query.with(new Sort(Sort.Direction.ASC, "date").and(new Sort(Sort.Direction.ASC, "priority")));
        return mongoTemplate.find(query, TodoItem.class);
    }

    private IllegalArgumentException notFound(String id) {
        return new IllegalArgumentException(String.format("Todo item with id=%1$s was not found", id));
    }
}
