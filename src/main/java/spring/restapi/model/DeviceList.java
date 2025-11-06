package spring.restapi.model;

import javax.persistence.*;

@Entity
@Table(name = "device_list")
public class DeviceList {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "install_place")
    private String installPlace;

    @Column(name = "district_id")
    private Integer districtId;

    @Column(name = "district_info")
    private String districtInfo;

    @Column(name = "detail_info")
    private String detailInfo;

    @Column(name = "state")
    private Integer state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getInstallPlace() {
        return installPlace;
    }

    public void setInstallPlace(String installPlace) {
        this.installPlace = installPlace;
    }

    public Integer getDistrictId() {
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
