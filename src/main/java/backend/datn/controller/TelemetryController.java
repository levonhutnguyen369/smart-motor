package backend.datn.controller;

import backend.datn.entity.Telemetry;
import backend.datn.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryService service;

    @GetMapping("/{deviceId}")
    public List<Telemetry> history(
            @PathVariable String deviceId
    ) {
        return service.findByDevice(
                deviceId
        );
    }

    @GetMapping("/{deviceId}/latest")
    public Telemetry latest(
            @PathVariable String deviceId
    ) {
        return service.latest(
                deviceId
        );
    }
}
