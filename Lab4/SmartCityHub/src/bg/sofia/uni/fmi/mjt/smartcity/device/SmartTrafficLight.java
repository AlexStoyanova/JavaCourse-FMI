package bg.sofia.uni.fmi.mjt.smartcity.device;

import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.LocalDateTime;
import java.util.Objects;

public class SmartTrafficLight extends GeneralSmartDevice {
    private static int numForId = 0;
    private String id;

    public SmartTrafficLight(String name, double powerConsumption, LocalDateTime installationDateTime) {
        super(name, powerConsumption, installationDateTime);
        id = getType().getShortName() + "-" + name + "-" + numForId;
        numForId++;
    }

    public String getId() {
        return id;
    }

    public DeviceType getType() {
        return DeviceType.TRAFFIC_LIGHT;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof SmartCamera)) {
            return false;
        }

        SmartTrafficLight light = (SmartTrafficLight) obj;
        return this.id.equals(light.id);
    }
}
