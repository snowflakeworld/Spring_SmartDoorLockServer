package spring.restapi.resquest_dto;

public class MessageSendRequestDto {
    private String cid;
    private String msg;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
