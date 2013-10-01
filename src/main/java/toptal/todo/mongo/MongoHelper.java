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

    public List<TodoItem> findItemInSorted(List<String> ids, Boolean completed, String sort, String sortBy) {
        Sort.Direction direction = Sort.Direction.fromString(sort);
        Criteria criteria = Criteria.where("_id").in(ids);
        if (completed != null)
            criteria = criteria.and("completed").is(completed);
        Query query = new Query(criteria);
        query.with(new Sort(direction, sortBy));
        return mongoTemplate.find(query, TodoItem.class);
    }
}
