package toptal.todo.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import toptal.todo.domain.User;
import toptal.todo.service.SessionService;
import toptal.todo.service.UserService;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static toptal.todo.service.UserServiceTest.isUserEquals;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    @Mock
    private SessionService sessionService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userConstroller;

    private User expectedUser;
    private List<User> expectedUsers;
    private String expectedToken;

    @Before
    public void before() {
        User.Builder builder = new User.Builder();
        expectedUser = builder.newUser("user").setPassword("qwerty").
                setFullname("Full Name").
                build();

        expectedToken = "token";

        expectedUsers = new LinkedList<User>();
        expectedUsers.add(expectedUser);
    }

    @Test
    public void loginOkTest() {
        given(userService.auth(expectedUser.getNickname(), expectedUser.getPassword())).willReturn(expectedUser);
        given(sessionService.generateToken(expectedUser.getNickname())).willReturn(expectedToken);

        //when
        String token = userConstroller.login(expectedUser.getNickname(), expectedUser.getPassword());

        // then
        assertThat(token, is(expectedToken));
    }

    @Test(expected = IllegalArgumentException.class)
    public void loginFailedTest() {
        given(userService.auth(expectedUser.getNickname(), expectedUser.getPassword())).willThrow(new IllegalArgumentException());
        given(sessionService.generateToken(expectedUser.getNickname())).willReturn(expectedToken);

        //when
        userConstroller.login(expectedUser.getNickname(), expectedUser.getPassword());

        // then exception
    }

    @Test
    public void logoutTest() {
        willDoNothing().given(sessionService).invalidateToken(expectedToken);

        //when
        userConstroller.logout(expectedToken);

        // then no exception
    }


    @Test
    public void getUserByNicknameOkTest() {
        given(userService.getUserByNickname(expectedUser.getNickname())).willReturn(expectedUser);
        willDoNothing().given(sessionService).validateToken(expectedToken);

        // when
        User actualUser = userConstroller.getUserByNickname(expectedUser.getNickname(), expectedToken);

        // then
        assertThat(actualUser, isUserEquals(expectedUser));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUserByNicknameFailed1Test() {
        given(userService.getUserByNickname(expectedUser.getNickname())).willReturn(expectedUser);
        willThrow(new IllegalArgumentException()).given(sessionService).validateToken(expectedToken);

        // when
        userConstroller.getUserByNickname(expectedUser.getNickname(), expectedToken);

        // then exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUserByNicknameFailed2Test() {
        given(userService.getUserByNickname(expectedUser.getNickname())).willThrow(new IllegalArgumentException());
        willDoNothing().given(sessionService).validateToken(expectedToken);

        // when
        userConstroller.getUserByNickname(expectedUser.getNickname(), expectedToken);

        // then exception
    }

    @Test
    public void createUserTest() {
        given(userService.createUser(expectedUser)).willReturn(expectedUser);

        // when
        User actualUser = userConstroller.createUser(expectedUser);

        // then
        assertThat(actualUser, isUserEquals(expectedUser));
    }

    @Test
    public void deleteUserByNicknameTest() {
        willDoNothing().given(userService).deleteUserByNickname(expectedUser.getNickname());
        willDoNothing().given(sessionService).validateToken(expectedToken);

        // when
        userConstroller.deleteUserByNickname(expectedUser.getNickname(), expectedToken);

        // then no exception
    }

    @Test
    public void getUsersTest() {
        given(userService.getAllUsers()).willReturn(expectedUsers);
        willDoNothing().given(sessionService).validateToken(expectedToken);

        // when
        List<User> actualUsers = userConstroller.getUsers(expectedToken);

        // then
        assertThat(actualUsers, is(hasSize(1)));
        assertThat(actualUsers.get(0), isUserEquals(expectedUser));
    }
}
