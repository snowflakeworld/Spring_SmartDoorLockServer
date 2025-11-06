package spring.restapi.model;

import javax.persistence.*;

@Entity
@Table(name = "device_property")
public class DeviceProperty {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "device_property")
    private String deviceProperty;

    @Column(name = "property_value")
    private String propertyValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceProperty() {
        return deviceProperty;
    }

    public void setDeviceProperty(String deviceProperty) {
        this.deviceProperty = deviceProperty;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
