package backend.datn.dto;

import lombok.*;

@Getter
@Setter
public class AlertPayload {

    private String deviceId;

    private String event;

    private Double lat;

    private Double lng;
}
