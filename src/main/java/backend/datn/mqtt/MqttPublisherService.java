package backend.datn.mqtt;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MqttPublisherService {

    private final MqttClient mqttClient;

    public void publish(String topic, String payload) {

        try {

            mqttClient.publish(
                    topic,
                    payload.getBytes(StandardCharsets.UTF_8),
                    1,
                    false
            );

        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

    }

}
