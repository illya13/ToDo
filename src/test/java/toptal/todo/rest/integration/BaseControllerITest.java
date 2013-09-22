package toptal.todo.rest.integration;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.web.client.RestTemplate;
import toptal.todo.domain.User;

public class BaseControllerITest {
    private static int port = 7900;
    private static String contextPath = "/todo";
    private static Server server;

    protected static User expectedUser;
    protected static String expectedToken;

    @BeforeClass
    public static void beforeClass() throws Exception {
        User.Builder builder = new User.Builder();
        expectedUser = builder.newUser("root").setPassword("qwerty").
                setFullname("Full Name").
                build();


        startJetty();

        RestTemplate restTemplate = new RestTemplate();
        expectedToken = restTemplate.getForObject(getRestEndpoint("user", "login", "?nickname={nickname}&password={password}"),
                String.class, expectedUser.getNickname(), expectedUser.getPassword());
    }

    @AfterClass
    public static void afterClass() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForEntity(getRestEndpoint("user", "logout", "?token={token}"),
                String.class, expectedToken);

        stopJetty();
    }

    private static void startJetty() throws Exception {
        server = new Server();

        SocketConnector connector = new SocketConnector();
        connector.setPort(port);

        WebAppContext context = new WebAppContext();
        context.setServer(server);
        context.setContextPath(contextPath);
        context.setWar("src/main/webapp");

        server.setConnectors(new Connector[]{connector});
        server.setHandler(context);
        server.start();
    }

    private static void stopJetty() throws Exception {
        server.stop();
    }

    protected static String getRestEndpoint(String path) {
        return String.format("http://localhost:%1$d%2$s/rest/%3$s", port, contextPath, path);
    }

    protected static String getRestEndpoint(String path, String param) {
        return String.format("http://localhost:%1$d%2$s/rest/%3$s/%4$s", port, contextPath, path, param);
    }

    protected static String getRestEndpoint(String path, String pathParam, String requestParam) {
        return String.format("http://localhost:%1$d%2$s/rest/%3$s/%4$s%5$s", port, contextPath, path, pathParam, requestParam);
    }
}
