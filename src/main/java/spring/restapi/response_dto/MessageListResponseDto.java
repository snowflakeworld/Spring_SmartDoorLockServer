package spring.restapi.response_dto;

import spring.restapi.model.MessageListProjection;

import java.util.List;

public class MessageListResponseDto extends BaseResponseDto {
    long totalCount;
    int totalPage;
    boolean hasNextPage;
    boolean hasPrevPage;
    private List<MessageListProjection> data;

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public boolean isHasPrevPage() {
        return hasPrevPage;
    }

    public void setHasPrevPage(boolean hasPrevPage) {
        this.hasPrevPage = hasPrevPage;
    }

    public List<MessageListProjection> getData() {
        return data;
    }

    public void setData(List<MessageListProjection> data) {
        this.data = data;
    }
}
