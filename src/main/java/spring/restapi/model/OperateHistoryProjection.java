package spring.restapi.model;

public interface OperateHistoryProjection {

    long getId();

    String getName();

    String getAction();

    String getMode();

    String getCreateAt();
}
