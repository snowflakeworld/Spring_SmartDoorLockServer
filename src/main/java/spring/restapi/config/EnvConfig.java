package spring.restapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {
    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.port}")
    private String redisPort;
    @Value("${redis.server_id}")
    private String serverId;
    @Value("${redis.socket_server_id}")
    private String socketServerId;
    @Value("${config.socket_timeout}")
    private Long socketTimeout;
    @Value("${config.bluetooth_timeout}")
    private Long bluetoothTimeout;

    public String getRedisHost() {
        return redisHost;
    }

    public String getRedisPort() {
        return redisPort;
    }

    public String getServerId() {
        return serverId;
    }

    public String getSocketServerId() {
        return socketServerId;
    }

    public Long getSocketTimeout() {
        return socketTimeout;
    }

    public Long getBluetoothTimeout() {
        return bluetoothTimeout;
    }
}
