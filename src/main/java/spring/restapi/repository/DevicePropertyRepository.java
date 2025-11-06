package spring.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.restapi.model.DeviceProperty;

import java.util.List;
import java.util.Optional;

public interface DevicePropertyRepository extends JpaRepository<DeviceProperty, Long> {
    Optional<DeviceProperty> findFirstByDeviceIdAndDeviceProperty(long deviceId, String deviceProperty);
    List<DeviceProperty> findAllByDeviceId(long deviceId);
}
