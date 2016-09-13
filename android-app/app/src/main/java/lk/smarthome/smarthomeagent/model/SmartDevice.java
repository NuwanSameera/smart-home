package lk.smarthome.smarthomeagent.model;

/**
 * Created by charitha on 9/13/16.
 */
public class SmartDevice {

    private Integer id;
    private String name;
    private Integer regionId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }
}
