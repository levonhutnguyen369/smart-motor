package backend.datn.repository;


import backend.datn.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository
        extends JpaRepository<Alert, Long> {

    List<Alert> findAlertByDeviceId(String deviceId);
}
