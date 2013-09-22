package toptal.todo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import toptal.todo.domain.User;
import toptal.todo.mongo.UserRepository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User auth(String nickname, String password) {
        User user = userRepository.findOne(nickname);

        if (user == null)
            throw notFound(nickname);
        if (!user.getPassword().equals(password))
            throw badUserPassword(nickname);
        return user;
    }

    public User getUserByNickname(String nickname) {
        User user = userRepository.findOne(nickname);
        if (user == null)
            throw notFound(nickname);
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        if(userRepository.findOne(user.getNickname()) != null)
            throw alreadyExists(user.getNickname());
        return userRepository.save(user);
    }

    public void deleteUserByNickname(String nickname) {
        userRepository.delete(nickname);
    }

    private IllegalArgumentException notFound(String nickname) {
        return new IllegalArgumentException(String.format("User with nickname=%1$s was not found", nickname));
    }

    private IllegalArgumentException alreadyExists(String nickname) {
        return new IllegalArgumentException(String.format("User with nickname=%1$s already exists", nickname));
    }

    private IllegalArgumentException badUserPassword(String nickname) {
        return new IllegalArgumentException(String.format("Bad user nickname=%1$s or/and password", nickname));
    }
}
