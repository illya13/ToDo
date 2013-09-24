package toptal.todo.rest.integration;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import toptal.todo.domain.TodoItem;

import java.util.List;

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
    public void testCreateTodoItem() throws Exception {
        TodoItem response = restTemplate.postForObject(getRestEndpoint("item")+"?nickname={nickname}&token={token}",
                expectedItem, TodoItem.class, expectedUser.getNickname(), expectedToken);
        Assert.assertNotNull(response);
    }

    @Test()
    public void testGetTodoItem() throws Exception {
        TodoItem response = restTemplate.getForObject(getRestEndpoint("item", expectedItem.getId(), "?token={token}"),
                TodoItem.class, expectedToken);
        Assert.assertNotNull(response);
        Assert.assertEquals(expectedItem.getId(), response.getId());
        Assert.assertNotNull(response.getTitle());
    }

    @Test()
    public void testUpdateTodoItem() throws Exception {
        TodoItem response = restTemplate.getForObject(getRestEndpoint("item", expectedItem.getId(), "?token={token}"),
                TodoItem.class, expectedToken);
        Assert.assertNotNull(response);
        Assert.assertEquals(expectedItem.getId(), response.getId());
        Assert.assertNotNull(response.getTitle());

        response.setDescription("Updated Description");

        TodoItem updatedResponse = restTemplate.postForObject(getRestEndpoint("item", response.getId(), "?token={token}"),
                response, TodoItem.class, expectedToken);

        Assert.assertNotNull(updatedResponse);
        Assert.assertEquals(response.getId(), updatedResponse.getId());
        Assert.assertEquals("Updated Description", updatedResponse.getDescription());
    }

    @Test()
    public void testGetTodoItems() throws Exception {
        TodoItem[] response = restTemplate.getForObject(getRestEndpoint("item")+"?token={token}",
                TodoItem[].class, expectedToken);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.length > 0);
        Assert.assertEquals(expectedItem.getId(), response[response.length - 1].getId());
        Assert.assertNotNull(response[response.length-1].getTitle());
    }

    @Test
    public void testTitleSuggest() throws Exception {
        String[] response = restTemplate.getForObject(getRestEndpoint("item", "suggest")+"?text={text}&token={token}",
                String[].class, "ti", expectedToken);
        Assert.assertNotNull(response);
    }

    @Test
    public void testFilter() throws Exception {
        TodoItem[] response = restTemplate.getForObject(getRestEndpoint("item", "filter")+
                "?text={text}&start={start}&size={size}&token={token}",
                TodoItem[].class, "update*", 0, 10, expectedToken);
        Assert.assertNotNull(response);
    }

    @Test()
    public void testTodoItemUser() throws Exception {
        restTemplate.delete(getRestEndpoint("item", expectedItem.getId(), "?token={token}"), expectedToken);
    }
}
