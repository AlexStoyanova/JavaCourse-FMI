package bg.sofia.uni.fmi.mjt.smartcity.device;

import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.LocalDateTime;

public abstract class GeneralSmartDevice implements SmartDevice {

    private String name;
    private double powerConsumption;
    private LocalDateTime installationDateTime;

    public GeneralSmartDevice(String name, double powerConsumption, LocalDateTime installationDateTime) {
        this.name = name;
        this.powerConsumption = powerConsumption;
        this.installationDateTime = installationDateTime;
    }

    public abstract String getId();

    public String getName() {
        return name;
    }

    public double getPowerConsumption() {
        return powerConsumption;
    }

    public LocalDateTime getInstallationDateTime() {
        return installationDateTime;
    }

    public abstract DeviceType getType();
}
