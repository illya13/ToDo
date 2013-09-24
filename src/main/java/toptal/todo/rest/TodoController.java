package toptal.todo.rest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import toptal.todo.domain.TodoItem;
import toptal.todo.domain.User;
import toptal.todo.service.SessionService;
import toptal.todo.service.TodoService;
import toptal.todo.service.UserService;

import java.util.List;

@Controller
public class TodoController {
    private static Logger logger = Logger.getLogger(TodoController.class);

    @Autowired
    private TodoService todoService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/item", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public TodoItem createTodoItem(@RequestBody TodoItem todoItem, @RequestParam String nickname, @RequestParam String token) {
        logger.info("createTodoItem");
        sessionService.validateToken(token);
        User user = userService.getUserByNickname(nickname);
        todoItem.setUser(user);
        return todoService.createTodoItem(todoItem);
    }

    @RequestMapping(value = "/item/{id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public TodoItem getTodoItemById(@PathVariable String id, @RequestParam String token) {
        logger.info("getTodoItemById, id=" + id + "token="+token);
        sessionService.validateToken(token);
        return todoService.getTodoItemById(id);
    }

    @RequestMapping(value = "/item/{id}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public TodoItem updateTodoItemById(@RequestBody TodoItem item, @PathVariable String id, @RequestParam String token) {
        logger.info("updateTodoItemById, id=" + id + "token="+token);
        sessionService.validateToken(token);
        return todoService.updateTodoItem(item);
    }

    @RequestMapping(value = "/item", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<TodoItem> getTodoItems(@RequestParam String token) {
        logger.info("getTodoItems, token="+token);
        sessionService.validateToken(token);
        return todoService.getAllTodoItems();
    }

    @RequestMapping(value = "/item/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteTodoItemById(@PathVariable String id, @RequestParam String token) {
        logger.info("deleteTodoItemById, id=" + id + ", token="+token);
        sessionService.validateToken(token);
        todoService.deleteTodoItem(id);
    }

    @RequestMapping(value = "/item/suggest", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<String> suggest(@RequestParam String text, @RequestParam String token) {
        logger.info("suggest, text=" + text + ", token="+token);
        sessionService.validateToken(token);
        return todoService.suggestTitles(text);
    }
}
