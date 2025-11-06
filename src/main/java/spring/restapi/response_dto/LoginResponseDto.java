package spring.restapi.response_dto;

import com.google.gson.JsonObject;

public class LoginResponseDto extends BaseResponseDto {
    private String cid;
    private String name;
    private String gender;
    private String birthday;
    private String citizenNumber;
    private String installPlace;
    private String roleType;
    private Integer state;
    private Integer districtId;
    private String districtInfo;
    private String detailInfo;
    private String convDetailInfo;
    private Long deviceId;
    private String propertyInfo;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCitizenNumber() {
        return citizenNumber;
    }

    public void setCitizenNumber(String citizenNumber) {
        this.citizenNumber = citizenNumber;
    }

    public String getInstallPlace() {
        return installPlace;
    }

    public void setInstallPlace(String installPlace) {
        this.installPlace = installPlace;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public String getDistrictInfo() {
        return districtInfo;
    }

    public void setDistrictInfo(String districtInfo) {
        this.districtInfo = districtInfo;
    }

    public String getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(String detailInfo) {
        this.detailInfo = detailInfo;
    }

    public String getConvDetailInfo() {
        return convDetailInfo;
    }

    public void setConvDetailInfo(String convDetailInfo) {
        this.convDetailInfo = convDetailInfo;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getPropertyInfo() {
        return propertyInfo;
    }

    public void setPropertyInfo(String propertyInfo) {
        this.propertyInfo = propertyInfo;
    }
}
