package in.meshworks.redis;



/**
 * Created by dhruv.suri on 12/04/17.
 */
public class RedisFactory {
    private static final String host = "52.66.66.36";

    public static void image(String imageUrl) {
        getRedisConnection().rpush("IMAGE", imageUrl);
    }

    public static String image() {
        return getRedisConnection().lpop("IMAGE");
    }

    public static void video(String videoUrl) {
        getRedisConnection().rpush("VIDEO", videoUrl);
    }

    public static String video() {
        return getRedisConnection().lpop("VIDEO");
    }

    public static void proxy(String proxyResponse) {
        getRedisConnection().rpush("PROXY", proxyResponse);
    }

    public static String proxy() {
        return getRedisConnection().lpop("PROXY");
    }

    public static void mail(String mail) {
        getRedisConnection().rpush("MAIL", mail);
    }

    public static String mail() {
        return getRedisConnection().lpop("MAIL");
    }

    public static void keyword(String imageUrl, String keywords) {
        getRedisConnection().rpush("KEY", imageUrl, keywords);
    }

    public static String keyword() {
        return getRedisConnection().lpop("KEY");
    }

    public static Jedis getRedisConnection(){
        return new Jedis(host);
    }
}
