package backend.datn.mqtt;

import backend.datn.dto.AlertPayload;
import backend.datn.dto.TelemetryPayload;
import backend.datn.entity.Device;
import backend.datn.repository.DeviceRepository;
import backend.datn.service.AlertService;
import backend.datn.service.TelemetryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;


@Component
public class MqttMessageHandler {

    private final ObjectMapper mapper;

    private final TelemetryService telemetryService;

    private final AlertService alertService;

    private final MqttPublisherService mqttPublisherService;

    @Autowired
    private DeviceRepository deviceRepository;

    public MqttMessageHandler(
            ObjectMapper mapper,
            TelemetryService telemetryService,
            AlertService alertService, MqttPublisherService mqttPublisherService)
    {
        this.mqttPublisherService = mqttPublisherService;
        System.out.println("MqttMessageHandler CREATED");

        this.mapper = mapper;
        this.telemetryService = telemetryService;
        this.alertService = alertService;
    }

    @ServiceActivator(
            inputChannel = "mqttInputChannel"
    )
    public void receive(Message<?> message) throws Exception {

        String topic =
                (String) message.getHeaders()
                        .get("mqtt_receivedTopic");

        String payload =
                (String) message.getPayload();

        System.out.println("RECEIVED MQTT");
        System.out.println(payload);

        if(topic.equals("bike/telemetry"))
        {
            TelemetryPayload dto =
                    mapper.readValue(
                            payload,
                            TelemetryPayload.class
                    );
            if (deviceRepository.findByDeviceId(dto.getDeviceId()).isEmpty()) {
                System.out.println("Device not found: " + dto.getDeviceId());
                deviceRepository.save(
                        backend.datn.entity.Device.builder()
                                .deviceId(dto.getDeviceId())
                                .name("Device " + dto.getDeviceId())
                                .build()
                );
            }

            telemetryService.save(
                    dto.getDeviceId(),
                    dto.getLat(),
                    dto.getLng()
            );
        }

        if(topic.equals("bike/alert"))
        {
            AlertPayload dto =
                    mapper.readValue(
                            payload,
                            AlertPayload.class
                    );

            alertService.save(
                    dto.getDeviceId(),
                    dto.getEvent(),
                    dto.getLat(),
                    dto.getLng()
            );
        }
        if (topic.equals("bike/battery/bike001")) {
            Device device = deviceRepository.findByDeviceId("bike001").orElse(null);
            if (device != null) {
                String[] parts = payload.split(",");
                if (parts.length <= 7) {
                    try {
                        double batteryVoltage = Double.parseDouble(parts[0]);
                        int batteryPercent = Integer.parseInt(parts[1]);
                        device.setBatteryVoltage(batteryVoltage);
                        device.setBatteryPercent(batteryPercent);
                        deviceRepository.save(device);
                        System.out.println("Updated battery info for device bike001: " + batteryVoltage + "V, " + batteryPercent + "%");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid battery data format: " + payload);
                    }
                }
            } else {
                System.out.println("Device not found");
            }
        }

        if (topic.startsWith("bike/balance/response/")) {
            System.out.println("MATCH");
        }

        if (topic.equals("bike/balance/response/bike001")) {
            Device device = deviceRepository.findByDeviceId("bike001").orElse(null);
            if (device != null) {
                mqttPublisherService.publish("app/balance/response/bike001",  payload);
            } else {
                System.out.println("Device not found");
            }
        }


    }
}