package backend.datn.dto;

import lombok.*;

@Getter
@Setter
public class TelemetryPayload {

    private String deviceId;

    private Double lat;

    private Double lng;
}
