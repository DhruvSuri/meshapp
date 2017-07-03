package in.meshworks.Redis;

/**
 * Created by dhruv.suri on 07/05/17.
 */
public class Jedis extends redis.clients.jedis.Jedis {
    public Jedis(String host) {
        super(host);
    }

    public String lpop(String key) {
        try {
            this.checkIsInMultiOrPipeline();
            this.client.lpop(key);
            return this.client.getBulkReply();
        } catch (ClassCastException e) {

        }
        return null;
    }

    public Long rpush(String key, String... strings) {
        try {
            this.checkIsInMultiOrPipeline();
            this.client.rpush(key, strings);
            return this.client.getIntegerReply();
        } catch (ClassCastException e) {

        }
        return null;
    }
}