package spring.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.restapi.model.BusinessList;

import java.util.List;

public interface BusinessListRepository extends JpaRepository<BusinessList, Long> {
    List<BusinessList> findAllByStateOrderByDispOrderAsc(int state);
}
