import java.util.Map;

public interface UserPostsEndpoints {
    String GET_POSTS = "/posts";

    Map<Integer, Integer> getUserPostsCountMap();
}