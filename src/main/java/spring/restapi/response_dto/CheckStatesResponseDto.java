package spring.restapi.response_dto;

import spring.restapi.model.BusinessList;

import java.util.List;

public class CheckStatesResponseDto extends BaseResponseDto {
    private Integer socketConnected;
    private Integer doorConnected;
    private Integer batteryLevel;

    public Integer getSocketConnected() {
        return socketConnected;
    }

    public void setSocketConnected(Integer socketConnected) {
        this.socketConnected = socketConnected;
    }

    public Integer getDoorConnected() {
        return doorConnected;
    }

    public void setDoorConnected(Integer doorConnected) {
        this.doorConnected = doorConnected;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
}
