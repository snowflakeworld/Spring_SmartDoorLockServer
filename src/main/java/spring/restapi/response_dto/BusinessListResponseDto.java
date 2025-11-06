package spring.restapi.response_dto;

import spring.restapi.model.BusinessList;

import java.util.List;

public class BusinessListResponseDto extends BaseResponseDto {
    private List<BusinessList> businessList;

    public List<BusinessList> getBusinessList() {
        return businessList;
    }

    public void setBusinessList(List<BusinessList> businessList) {
        this.businessList = businessList;
    }
}
