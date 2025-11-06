package spring.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.restapi.model.District;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, Integer> {
    List<District> findAllByParentIdAndStateOrderByDispOrder(int parentId, int state);
}
