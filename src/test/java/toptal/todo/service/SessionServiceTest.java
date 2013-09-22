package toptal.todo.service;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import toptal.todo.domain.Session;
import toptal.todo.domain.User;
import toptal.todo.mongo.SessionRepository;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Matchers.any;

@RunWith(MockitoJUnitRunner.class)
public class SessionServiceTest {
    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

    private User expectedUser;
    private Session expectedSession;
    private String expectedToken;

    @Before
    public void before() {
        User.Builder builder = new User.Builder();
        expectedUser = builder.newUser("root").setPassword("qwerty").setFullname("Full Name").
                build();

        expectedToken = "523ee81644942699ab81ebf4";
        expectedSession = new Session.Builder().newSession(expectedUser, expectedToken);
    }

    @Test
    public void generateTokenTest() {
        given(sessionRepository.save(any(Session.class))).willReturn(expectedSession);

        // when
        String token = sessionService.generateToken(expectedUser);

        // then
        assertThat(token, is(expectedToken));
    }

    @Test
    public void validateTokenOkTest() {
        given(sessionRepository.findOne(new ObjectId(expectedToken))).willReturn(expectedSession);

        // when
        sessionService.validateToken(expectedToken);

        // then no exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateTokenFailedTest() {
        sessionService.validateToken("bla-bla");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidateTokenTest() {
        willDoNothing().given(sessionRepository).delete(new ObjectId(expectedToken));

        // when
        sessionService.invalidateToken(expectedToken);
        sessionService.validateToken(expectedToken);

        // then throw exception
    }
}
