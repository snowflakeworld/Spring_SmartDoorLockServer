package spring.restapi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import spring.restapi.config.EnvConfig;
import spring.restapi.utils.Global;
import spring.restapi.utils.RedisSubscriber;

@SpringBootApplication
@EnableAsync
public class RestapiApplication implements CommandLineRunner {

    public final EnvConfig env;
    public final RedisSubscriber redisSubscriber;

    public RestapiApplication(EnvConfig env, RedisSubscriber redisSubscriber) {
        this.env = env;
        this.redisSubscriber = redisSubscriber;
    }

    public static void main(String[] args) {
        SpringApplication.run(RestapiApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        Global.redisHost = env.getRedisHost();
        Global.redisPort = env.getRedisPort();
        Global.redisServerId = env.getServerId();
        Global.redisSocketServerId = env.getSocketServerId();
        Global.socketTimeout = env.getSocketTimeout();
        Global.bluetoothTimeout = env.getBluetoothTimeout();

        redisSubscriber.start(Global.redisHost, Global.redisPort, Global.redisServerId);
    }
}
