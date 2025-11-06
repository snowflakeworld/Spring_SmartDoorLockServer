package spring.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spring.restapi.model.OperateHistoryProjection;
import spring.restapi.model.UserList;
import spring.restapi.model.UserRoleList;
import spring.restapi.model.UserRoleListProjection;

import java.util.List;
import java.util.Optional;

public interface UserRoleListRepository extends JpaRepository<UserRoleList, Long> {
    Optional<UserRoleList> findFirstByUserIdAndState(long userId, int state);
    Optional<UserRoleList> findFirstByUserIdAndDeviceIdAndRoleTypeAndStateNot(long userId, long deviceId, String roleType, int state);
    Optional<UserRoleList> findFirstByDeviceIdAndRoleTypeAndState(long deviceId, String roleType, int state);
    List<UserRoleList> findAllByDeviceIdAndState(long deviceId, int state);

    @Query(
            value = "SELECT\n" +
                    "  t1.id,\n" +
                    "  t2.cid,\n" +
                    "  t2.`name`,\n" +
                    "  t1.role_type AS roleType,\n" +
                    "  t1.state,\n" +
                    "  date_format( t1.update_at, '%Y-%m-%d %H:%i:%s' ) AS updateAt \n" +
                    "FROM\n" +
                    "  user_role_list t1\n" +
                    "  INNER JOIN user_list t2 ON t1.user_id = t2.id \n" +
                    "WHERE\n" +
                    "  t1.device_id = :deviceId \n" +
                    "  AND t1.state != 2 \n" +
                    "  AND t2.state = 3\n" +
                    "ORDER BY\n" +
                    "  t1.update_at DESC",
            nativeQuery = true)
    List<UserRoleListProjection> ntGetRoleListByDeviceId(long deviceId);
}
