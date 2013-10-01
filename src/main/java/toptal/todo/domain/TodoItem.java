package toptal.todo.domain;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Document
public class TodoItem {
    @Id
    private String id;

    @DBRef
    private User user;

    private String title;

    private String description;

    @Indexed
    private int priority;

    @Indexed
    private Date date;

    private boolean completed = false;

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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
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
