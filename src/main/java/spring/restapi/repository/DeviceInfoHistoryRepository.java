package spring.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.restapi.model.DeviceInfoHistory;

import java.util.Optional;

public interface DeviceInfoHistoryRepository extends JpaRepository<DeviceInfoHistory, Long> {
    Optional<DeviceInfoHistory> findFirstByDeviceIdAndType(Long deviceId, String type);
}
