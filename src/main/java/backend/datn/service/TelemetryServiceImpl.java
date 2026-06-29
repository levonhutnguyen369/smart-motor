package backend.datn.service;


import backend.datn.repository.TelemetryRepository;
import backend.datn.entity.Telemetry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelemetryServiceImpl implements TelemetryService {

    private final TelemetryRepository repository;

    @Override
    public Telemetry save(
            String deviceId,
            Double latitude,
            Double longitude
    ) {

        Telemetry telemetry =
                Telemetry.builder()
                        .deviceId(deviceId)
                        .latitude(latitude)
                        .longitude(longitude)
                        .createdAt(LocalDateTime.now())
                        .build();

        return repository.save(telemetry);
    }

    @Override
    public List<Telemetry> findByDevice(
            String deviceId
    ) {
        return repository
                .findByDeviceIdOrderByCreatedAtDesc(
                        deviceId
                );
    }

    @Override
    public Telemetry latest(
            String deviceId
    ) {
        List<Telemetry> list =
                repository.findByDeviceIdOrderByCreatedAtDesc(
                        deviceId
                );

        return list.isEmpty()
                ? null
                : list.get(0);
    }
}
