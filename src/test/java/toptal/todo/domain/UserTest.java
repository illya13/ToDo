package toptal.todo.domain;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;


public class UserTest {
    @Test
    public void userBuilderTest() {
        User.Builder builder = new User.Builder();
        User root = builder.newUser("root").setPassword("qwerty").
                setFullname("full name").
                build();

        assertThat(root.getNickname(), is("root"));
        assertThat(root.getPassword(), is("qwerty"));
        assertThat(root.getFullname(), is("full name"));
    }
}
