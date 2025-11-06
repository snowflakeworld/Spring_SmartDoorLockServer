package spring.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spring.restapi.model.DeviceOperateHistory;
import spring.restapi.model.OperateHistoryProjection;

import java.util.List;

public interface DeviceOperateHistoryRepository extends JpaRepository<DeviceOperateHistory, Long> {
    @Query(
            value = "select t1.id, t2.name, t1.action, t1.mode, date_format(t1.create_at, '%Y-%m-%d %H:%i:%s') as createAt from device_operate_history t1 left join user_list t2 on t1.link_id = t2.id where t1.device_id = :deviceId and t1.action_date_int >= :fromDate and t1.action_date_int <= :toDate order by t1.id desc",
            nativeQuery = true)
    List<OperateHistoryProjection> ntGetOperateHistory(long deviceId, int fromDate, int toDate);
}
