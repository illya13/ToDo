package toptal.todo.rest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import toptal.todo.domain.User;
import toptal.todo.service.SessionService;
import toptal.todo.service.TodoService;

import java.util.List;

@Controller
public class TodoController {
    private static Logger logger = Logger.getLogger(TodoController.class);

    @Autowired
    private TodoService todoService;

    @Autowired
    private SessionService sessionService;
}
