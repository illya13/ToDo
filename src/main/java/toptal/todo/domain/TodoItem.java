package toptal.todo.domain;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Document
@CompoundIndex(def = "{'user':1, 'priority':1, 'date':1}", unique = false)
public class TodoItem {
    @Id
    private String id;

    @DBRef
    private User user;

    private String title;

    private String description;

    private int priority;

    private Date date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public static XContentBuilder getXContentBuilder(TodoItem item) throws IOException {
        return jsonBuilder().
                startObject().
                field("user", item.getUser().getNickname()).
                field("title", item.getTitle()).
                field("description", item.getDescription()).
                endObject();
    }
}
