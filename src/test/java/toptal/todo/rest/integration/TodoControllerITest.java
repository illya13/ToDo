package toptal.todo.rest.integration;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import toptal.todo.domain.TodoItem;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:toptal/todo/rest/integration/IntegrationTest-context.xml")
public class TodoControllerITest extends BaseControllerITest {
    @Autowired
    RestTemplate restTemplate;

    @Test()
    public void testGetRestEndpoint() throws Exception {
        Assert.assertEquals("http://localhost:7900/todo/rest/item", getRestEndpoint("item"));
    }

    @Test
    public void testCreateUser() throws Exception {
        TodoItem response = restTemplate.postForObject(getRestEndpoint("item")+"?nickname={nickname}&token={token}",
                expectedItem, TodoItem.class, expectedUser.getNickname(), expectedToken);
        Assert.assertNotNull(response);
    }
}
