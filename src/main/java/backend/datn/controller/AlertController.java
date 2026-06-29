package backend.datn.controller;

import backend.datn.entity.Alert;
import backend.datn.service.AlertService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService service;

    @GetMapping
    public List<Alert> getAll() {
        return service.getAll();
    }

    @GetMapping("/{deviceId}")
    public List<Alert> getByDevice(
            @PathVariable String deviceId
    ) {
        return service.getByDeviceId(deviceId);
    }
}
