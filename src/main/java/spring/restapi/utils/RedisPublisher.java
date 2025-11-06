package spring.restapi.utils;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisPublisher {
    private static final RedisClient redisClient = RedisClient.create("redis://" + Global.redisHost + ":" + Global.redisPort);
    private static final StatefulRedisConnection<String, String> connection = redisClient.connect();
    public static final RedisCommands<String, String> redis = connection.sync();

    public static void publish(String toUserId, String message) {
        redis.publish("server:" + Global.redisSocketServerId, toUserId + "|" + message);
    }
}
