package toptal.todo.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import toptal.todo.domain.TodoItem;

import java.util.List;

@Service
public class MongoHelper {
    @Autowired
    MongoTemplate mongoTemplate;

    public List<TodoItem> findItemInSorted(List<String> ids) {
        Query query = new Query(Criteria.where("_id").in(ids));
        query.with(new Sort(Sort.Direction.ASC, "date").and(new Sort(Sort.Direction.ASC, "priority")));
        return mongoTemplate.find(query, TodoItem.class);
    }
}