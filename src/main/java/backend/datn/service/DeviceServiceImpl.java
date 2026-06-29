package backend.datn.service;


import backend.datn.entity.Alert;
import backend.datn.entity.Device;
import backend.datn.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceRepository;
    private final MqttClient mqttClient;

    public Device getByDeviceId(String id) {
        Optional<Device> device = deviceRepository.findByDeviceId(id);
        if (device.isPresent()) {
            return device.get();
        } else {
            throw new RuntimeException("Device not found with id: " + id);
        }
    }

    @Override
    public String sendDeviceCommand(String deviceId, String command) {
        Device device = getByDeviceId(deviceId);
        if (device == null) {
            throw new RuntimeException("Device not found with id: " + deviceId);
        }
        try {
            mqttClient.publish(
                    "bike/command/" + deviceId,
                    command.getBytes(),
                    0,
                    false
            );
            return "Command sent successfully";
        } catch (MqttException e) {
            throw new RuntimeException("Failed to publish MQTT message", e);
        }
    }

}
