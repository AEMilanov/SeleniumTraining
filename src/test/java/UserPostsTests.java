import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserPostsTests implements UserPostsEndpoints {
    private CloseableHttpClient httpClient;

    public UserPostsTests() {
        this.httpClient = HttpClients.createDefault();
    }

    public Map<Integer, Integer> getUserPostsCountMap() {
        Map<Integer, Integer> userPostsCountMap = new HashMap<>();
        for (int user : new int[]{5, 7, 9}) {
            String url = UserPostsConfig.BASE_URL + GET_POSTS + "?userId=" + user;
            HttpGet request = new HttpGet(url);
            try {
                CloseableHttpResponse response = httpClient.execute(request);
                String responseBody = responseToString(response);
                JSONArray posts = new JSONArray(responseBody);
                userPostsCountMap.put(user, posts.length());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userPostsCountMap;
    }

    private String responseToString(CloseableHttpResponse response) throws IOException {
        return new String(response.getEntity().getContent().readAllBytes());
    }

    public static void main(String[] args) {
        UserPostsTests tests = new UserPostsTests();
        Map<Integer, Integer> userPostsCountMap = tests.getUserPostsCountMap();
        for (Map.Entry<Integer, Integer> entry : userPostsCountMap.entrySet()) {
            System.out.println("User " + entry.getKey() + " has " + entry.getValue() + " posts.");
        }
    }
}