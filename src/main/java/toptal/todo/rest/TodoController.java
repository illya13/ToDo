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
}
