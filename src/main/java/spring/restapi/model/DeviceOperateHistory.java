package spring.restapi.model;

import javax.persistence.*;

@Entity
@Table(name = "device_operate_history")
public class DeviceOperateHistory {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "link_id")
    private Long linkId;

    @Column(name = "action")
    private String action;

    @Column(name = "mode")
    private String mode;

    @Column(name = "action_date_int")
    private Integer actionDateInt;

    @Column(name = "extra_data")
    private String extraData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getActionDateInt() {
        return actionDateInt;
    }

    public void setActionDateInt(Integer actionDateInt) {
        this.actionDateInt = actionDateInt;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
}
