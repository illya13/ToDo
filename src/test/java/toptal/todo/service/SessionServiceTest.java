package toptal.todo.service;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class SessionServiceTest {
    private SessionService sessionService;

    @Before
    public void before() {
        sessionService = new SessionService();
    }
    @Test
    public void generateTokenTest() {
        assertThat(sessionService.generateToken("root"), is(notNullValue()));
    }

    @Test
    public void validateTokenOkTest() {
        String token = sessionService.generateToken("root");
        assertThat(token, is(notNullValue()));
        sessionService.validateToken(token);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateTokenFailedTest() {
        sessionService.validateToken("bla-bla");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidateTokenTest() {
        String token = sessionService.generateToken("root");
        assertThat(token, is(notNullValue()));
        sessionService.validateToken(token);
        sessionService.invalidateToken(token);
        sessionService.validateToken(token);
    }
}
