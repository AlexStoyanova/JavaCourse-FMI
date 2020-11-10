package bg.sofia.uni.fmi.mjt.smartcity.device;

import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.LocalDateTime;
import java.util.Objects;

public class SmartLamp extends GeneralSmartDevice {
    private static int numForId = 0;
    private String id;

    public SmartLamp(String name, double powerConsumption, LocalDateTime installationDateTime) {
        super(name, powerConsumption, installationDateTime);
        id = getType().getShortName() + "-" + name + "-" + numForId;
        numForId++;
    }

    public String getId() {
        return id;
    }

    public DeviceType getType() {
        return DeviceType.LAMP;
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

        SmartLamp lamp = (SmartLamp) obj;
        return this.id.equals(lamp.id);
    }
}
