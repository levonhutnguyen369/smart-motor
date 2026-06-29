package backend.datn.controller;


import backend.datn.dto.ApiResponse;
import backend.datn.entity.Device;
import backend.datn.service.DeviceService;
import backend.datn.service.DeviceServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping("/{deviceId}")
    public Device getDeviceById(@PathVariable String deviceId) {
        return deviceService.getByDeviceId(deviceId);
    }

    @PostMapping("{deviceId}/command")
    public ResponseEntity<ApiResponse<String>> sendDeviceCommand(
            @PathVariable String deviceId,
            @RequestBody String command) {
        String result = deviceService.sendDeviceCommand(deviceId, command);

        ApiResponse<String> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Command sent successfully");
        response.setData(result);

        return ResponseEntity.ok(response);
    }
}
