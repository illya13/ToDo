package toptal.todo.service;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import toptal.todo.domain.User;
import toptal.todo.mongo.UserRepository;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User expectedUser;
    private User newUser;

    private List<User> expectedUsers;
    private List<User> newUsers;

    @Before
    public void before() {
        User.Builder builder = new User.Builder();
        expectedUser = builder.newUser("root").setPassword("qwerty").setFullname("Full Name").
                build();

        expectedUsers = new LinkedList<User>();
        expectedUsers.add(expectedUser);

        newUser = builder.newUser("user").setPassword("qwerty").setFullname("Full Name").
                build();

        newUsers = new LinkedList<User>();
        newUsers.add(newUser);
    }

    @Test
    public void authOkTest() {
        given(userRepository.findOne(expectedUser.getNickname())).willReturn(expectedUser);

        //when
        User actualUser = userService.auth(expectedUser.getNickname(), expectedUser.getPassword());

        // then
        assertThat(actualUser, isUserEquals(expectedUser));
    }

    @Test(expected = IllegalArgumentException.class)
    public void authFailed1Test() {
        // given no users

        // when
        userService.auth("root", "qwerty1");

        // then exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void authFailed2Test() {
        // given no users

        // when
        userService.auth("root1", "qwerty");

        // then exception
    }

    @Test
    public void getUserByNicknameOkTest() {
        given(userRepository.findOne(expectedUser.getNickname())).willReturn(expectedUser);

        //when
        User actualUser = userService.getUserByNickname(expectedUser.getNickname());

        // then
        assertThat(actualUser, isUserEquals(expectedUser));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUserByNicknameFailedTest() {
        // given no users

        // when
        userService.getUserByNickname("root1");

        // then exception
    }

    @Test
    public void getAllUsersTest() {
        given(userRepository.findAll()).willReturn(expectedUsers);

        List<User> actualUsers = userService.getAllUsers();

        // then
        assertThat(actualUsers, is(hasSize(1)));
        assertThat(actualUsers.get(0), isUserEquals(expectedUser));
    }

    @Test
    public void createDeleteUserTest() {
        given(userRepository.save(newUser)).willReturn(newUser);
        willDoNothing().given(userRepository).delete(newUser);

        // when
        User actual = userService.createUser(newUser);
        assertThat(actual, isUserEquals(newUser));

        userService.deleteUserByNickname(newUser.getNickname());
    }

    public static Matcher<User> isUserEquals(final User excepted) {
        return new TypeSafeMatcher<User>() {
            @Override
            protected boolean matchesSafely(User user) {
                return user.getNickname().equals(excepted.getNickname()) &&
                        user.getPassword().equals(excepted.getPassword()) &&
                        user.getNickname().equals(excepted.getNickname()) &&
                        user.getFullname().equals(excepted.getFullname());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(excepted.toString());
            }
        };
    }
}
