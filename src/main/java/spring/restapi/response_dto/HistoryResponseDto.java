package spring.restapi.response_dto;

import spring.restapi.model.OperateHistoryProjection;

import java.util.List;

public class HistoryResponseDto extends BaseResponseDto {
    private List<OperateHistoryProjection> data;

    public List<OperateHistoryProjection> getData() {
        return data;
    }

    public void setData(List<OperateHistoryProjection> data) {
        this.data = data;
    }
}
