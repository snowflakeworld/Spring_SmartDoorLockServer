package spring.restapi.utils;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spring.restapi.service.DoorService;

@Component
public class RedisSubscriber {

    @Autowired
    private DoorService doorService;

    public void start(String redisHost, String redisPort, String redisServerId) {
        RedisClient redisClient = RedisClient.create("redis://" + redisHost + ":" + redisPort);
        StatefulRedisPubSubConnection<String, String> connection = redisClient.connectPubSub();

        connection.addListener(new RedisPubSubListener<String, String>() {
            @Override
            public void message(String channel, String message) {
                String[] parts = message.split("\\|", 2);
                String userId = parts[0];
                String text = parts[1];

                System.out.println("Subscribe: " + userId + "@@" + text);

                doorService.processSocketMessage(userId, text);
            }

            @Override
            public void message(String pattern, String channel, String message) {
            }

            @Override
            public void subscribed(String channel, long count) {
            }

            @Override
            public void psubscribed(String pattern, long count) {
            }

            @Override
            public void unsubscribed(String channel, long count) {
            }

            @Override
            public void punsubscribed(String pattern, long count) {
            }
        });

        connection.sync().subscribe("server:" + redisServerId);
    }
}