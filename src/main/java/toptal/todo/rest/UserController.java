package toptal.todo.rest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import toptal.todo.domain.User;
import toptal.todo.service.SessionService;
import toptal.todo.service.UserService;

import java.util.List;

@Controller
public class UserController {
    private static Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/user", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        logger.info("createUser");
        return userService.createUser(user);
    }

    @RequestMapping(value = "/user/login", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String login(@RequestParam String nickname, @RequestParam String password) {
        logger.info("login, nickname=" + nickname + ", password="+password);
        User user = userService.auth(nickname, password);
        return sessionService.generateToken(user.getNickname());
    }

    @RequestMapping(value = "/user/logout", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public void logout(@RequestParam String token) {
        logger.info("logout, token="+token);
        sessionService.invalidateToken(token);
    }

    @RequestMapping(value = "/user/{nickname}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public User getUserById(@PathVariable String nickname, @RequestParam String token) {
        logger.info("getUserById, nickname=" + nickname + "token="+token);
        sessionService.validateToken(token);
        return userService.getUserById(nickname);
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<User> getUsers(@RequestParam String token) {
        logger.info("getUsers, token="+token);
        sessionService.validateToken(token);
        return userService.getAllUsers();
    }

    @RequestMapping(value = "/user/{nickname}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteUserById(@PathVariable String nickname, @RequestParam String token) {
        logger.info("deleteUserById, nickname=" + nickname + ", token="+token);
        sessionService.validateToken(token);
        userService.deleteUserById(nickname);
    }
}
