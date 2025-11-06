package spring.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.restapi.model.DeviceList;

import java.util.Optional;

public interface DeviceListRepository extends JpaRepository<DeviceList, Long> {
    Optional<DeviceList> findFirstByInstallPlaceAndDistrictIdAndDetailInfoAndState(String installPlace, int districtId, String detailInfo, int state);
}
