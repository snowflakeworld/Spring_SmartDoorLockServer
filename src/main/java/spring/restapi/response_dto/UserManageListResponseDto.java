package spring.restapi.response_dto;

import spring.restapi.model.UserRoleListProjection;

import java.util.List;

public class UserManageListResponseDto extends BaseResponseDto {
    private List<UserRoleListProjection> data;

    public List<UserRoleListProjection> getData() {
        return data;
    }

    public void setData(List<UserRoleListProjection> data) {
        this.data = data;
    }
}
