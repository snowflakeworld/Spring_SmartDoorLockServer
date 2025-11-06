package spring.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.restapi.model.UserList;

import java.util.Optional;

public interface UserListRepository extends JpaRepository<UserList, Long> {
    Optional<UserList> findFirstByCidAndStateNot(long cid, int state);
    Optional<UserList> findFirstByCidAndState(long cid, int state);
}
