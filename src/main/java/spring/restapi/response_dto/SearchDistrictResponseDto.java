package spring.restapi.response_dto;

import spring.restapi.model.District;

import java.util.List;

public class SearchDistrictResponseDto extends BaseResponseDto {
    private int id;
    private String name;
    private int parentId;
    private List<District> childDistricts;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<District> getChildDistricts() {
        return childDistricts;
    }

    public void setChildDistricts(List<District> childDistricts) {
        this.childDistricts = childDistricts;
    }
}
