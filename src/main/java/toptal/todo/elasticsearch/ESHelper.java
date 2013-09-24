package toptal.todo.elasticsearch;

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
import org.springframework.stereotype.Service;
import toptal.todo.domain.TodoItem;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static toptal.todo.domain.TodoItem.getXContentBuilder;

@Service
public class ESHelper {
    public static final String type = "item";

    @Autowired
    private TransportClient transportClient;

    @Autowired
    private String indexName;

    public void delete(String id) {
        DeleteResponse response = transportClient.prepareDelete(indexName, type, id).
                execute().actionGet();
    }

    public List<String> suggest(String text) {
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

    public List<String> filter(String text, int start, int size) {
        SearchResponse response = transportClient.prepareSearch(indexName).
                setTypes(type).
                setSearchType(SearchType.DFS_QUERY_THEN_FETCH).
                setQuery(QueryBuilders.queryString(text)).
                setFrom(start).setSize(size).setExplain(true).
                execute().actionGet();

        List<String> ids = new LinkedList<String>();
        for (SearchHit hit: response.getHits())
            ids.add(hit.getId());

        return ids;
    }

    public void reindex(TodoItem item) throws IOException {
        IndexResponse response = transportClient.prepareIndex(indexName, type, null).
                setId(item.getId()).
                setSource(getXContentBuilder(item)).
                execute().actionGet();
    }
}
