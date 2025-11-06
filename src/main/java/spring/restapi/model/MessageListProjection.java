package spring.restapi.model;

public interface MessageListProjection {

    long getId();

    int getIsUser();

    String getMsg();

    String getCreateAt();
}
