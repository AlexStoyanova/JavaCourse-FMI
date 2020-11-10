package bg.sofia.uni.fmi.mjt.smartcity.hub;

import bg.sofia.uni.fmi.mjt.smartcity.device.SmartDevice;
import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class SmartCityHub {

    private LinkedHashMap<String, SmartDevice> smartDevices;

    public SmartCityHub() {
        smartDevices = new LinkedHashMap<>();
    }

    public void register(SmartDevice device) throws DeviceAlreadyRegisteredException {
        if (device == null) {
            throw new IllegalArgumentException();
        }

        if (smartDevices.containsKey(device.getId())) {
            throw new DeviceAlreadyRegisteredException("Device is already registered!");
        }

        smartDevices.put(device.getId(), device);
    }

    public void unregister(SmartDevice device) throws DeviceNotFoundException {
        if (device == null) {
            throw new IllegalArgumentException();
        }

        if (!smartDevices.containsKey(device.getId())) {
            throw new DeviceNotFoundException("Device is not found!");
        }

        smartDevices.remove(device.getId(), device);
    }

    public SmartDevice getDeviceById(String id) throws DeviceNotFoundException {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        if (!smartDevices.containsKey(id)) {
            throw new DeviceNotFoundException("Device is not found!");
        }

        return smartDevices.get(id);
    }

    public int getDeviceQuantityPerType(DeviceType type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }

        return countDevicesByType(type);
    }

    public Collection<String> getTopNDevicesByPowerConsumption(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }

        int smartDevicesSize = smartDevices.size();
        int minSize = Math.min(n, smartDevicesSize);

        List<SmartDevice> sortedSmartDevices = new ArrayList<>(smartDevices.values());
        sortedSmartDevices.sort(smartDeviceComparator);

        Collection<String> topNDevicesByPowerConsumption = new ArrayList<>(minSize);

        for (int i = 0; i < minSize; ++i) {
            topNDevicesByPowerConsumption.add(sortedSmartDevices.get(i).getId());
        }
        return topNDevicesByPowerConsumption;
    }

    public Collection<SmartDevice> getFirstNDevicesByRegistration(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }

        int smartDevicesSize = smartDevices.size();
        int minSize = Math.min(n, smartDevicesSize);

        Collection<SmartDevice> firstNSmartDevices = new ArrayList<>(minSize);

        Collection<SmartDevice> values = smartDevices.values();
        Iterator<SmartDevice> iterator = values.iterator();

        for (int i = 0; i < minSize; ++i) {
            firstNSmartDevices.add(iterator.next());
        }

        return firstNSmartDevices;
    }

    private int countDevicesByType(DeviceType type) {
        int count = 0;
        Collection<SmartDevice> values = smartDevices.values();

        for (SmartDevice value : values) {
            if (value.getType() == type) {
                count++;
            }
        }

        return count;
    }

    private static Comparator<SmartDevice> smartDeviceComparator = new Comparator<>() {
        @Override
        public int compare(SmartDevice first, SmartDevice second) {
            long firstDuration = Duration.between(first.getInstallationDateTime(), LocalDateTime.now()).toHours();
            long secondDuration = Duration.between(second.getInstallationDateTime(), LocalDateTime.now()).toHours();

            return (int) (firstDuration * first.getPowerConsumption() - secondDuration * second.getPowerConsumption());
        }
    };

    public static void main(String[] args) {

    }
}
