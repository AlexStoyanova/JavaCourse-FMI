package bg.sofia.uni.fmi.mjt.warehouse;

import bg.sofia.uni.fmi.mjt.warehouse.exceptions.CapacityExceededException;
import bg.sofia.uni.fmi.mjt.warehouse.exceptions.ParcelNotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MJTExpressWarehouse<L, P> implements DeliveryServiceWarehouse<L, P> {

    private int capacity;
    private int size;
    private int retentionPeriod;
    private HashMap<L, Pair<P>> parcels;
    private LocalDateTime oldestSubmissionDate;

    public MJTExpressWarehouse(int capacity, int retentionPeriod) {
        this.capacity = capacity;
        this.retentionPeriod = retentionPeriod;
        parcels = new HashMap<>();
        size = 0;
        oldestSubmissionDate = null;
    }

    public void submitParcel(L label, P parcel, LocalDateTime submissionDate) throws CapacityExceededException {
        if (size == capacity) {
            long daysLeft = Duration.between(oldestSubmissionDate, LocalDateTime.now()).toDays();
            if (daysLeft > (long) retentionPeriod) {
                removeOldestSubmissionDate(submissionDate);
            } else {
                throw new CapacityExceededException("No more space in warehouse!");
            }
        }
        if (label == null || parcel == null || submissionDate == null || submissionDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException();
        }

        if (oldestSubmissionDate == null || oldestSubmissionDate.isAfter(submissionDate)) {
            oldestSubmissionDate = submissionDate;
        }
        parcels.put(label, new Pair(parcel, submissionDate));
        size++;
    }

    private void removeOldestSubmissionDate(LocalDateTime newSubmissionDate) {
        LocalDateTime newOldestSubmissionDate = newSubmissionDate;
        Set<Map.Entry<L, Pair<P>>> entries = parcels.entrySet();
        Iterator<Map.Entry<L, Pair<P>>> iterator = entries.iterator();

        while (iterator.hasNext()) {
            Map.Entry<L, Pair<P>> next = iterator.next();
            if (next.getValue().submissionDate().equals(oldestSubmissionDate)) {
                iterator.remove();
            } else if (next.getValue().submissionDate().isBefore(newOldestSubmissionDate)) {
                newOldestSubmissionDate = next.getValue().submissionDate();
            }
        }

        oldestSubmissionDate = newOldestSubmissionDate;
        size--;
    }

    public P getParcel(L label) {
        if (label == null) {
            throw new IllegalArgumentException();
        }
        if (parcels.containsKey(label)) {
            return parcels.get(label).parcel();
        }
        return null;
    }

    public P deliverParcel(L label) throws ParcelNotFoundException {
        if (label == null) {
            throw new IllegalArgumentException();
        }
        if (!parcels.containsKey(label)) {
            throw new ParcelNotFoundException("Parcel not found!");
        }

        P deliveredParcel = parcels.get(label).parcel();
        parcels.remove(label);
        size--;
        return deliveredParcel;
    }

    public double getWarehouseSpaceLeft() {
        double scale = Math.pow(10, 2);
        return Math.round((capacity - size) / (double) capacity * scale) / scale;
    }

    public Map<L, P> getWarehouseItems() {
        Map<L, P> items = new HashMap<>();
        Iterator<Map.Entry<L, Pair<P>>> iterator = parcels.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<L, Pair<P>> next = iterator.next();
            items.put(next.getKey(), next.getValue().parcel());
        }
        return items;
    }

    public Map<L, P> deliverParcelsSubmittedBefore(LocalDateTime before) {
        if (before == null) {
            throw new IllegalArgumentException();
        }

        Map<L, P> items = new HashMap<>();
        Iterator<Map.Entry<L, Pair<P>>> iterator = parcels.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<L, Pair<P>> next = iterator.next();
            if (next.getValue().submissionDate().isBefore(before)) {
                items.put(next.getKey(), next.getValue().parcel());
            }
        }
        return items;
    }

    public Map<L, P> deliverParcelsSubmittedAfter(LocalDateTime after) {
        if (after == null) {
            throw new IllegalArgumentException();
        }

        Map<L, P> items = new HashMap<>();
        Iterator<Map.Entry<L, Pair<P>>> iterator = parcels.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<L, Pair<P>> next = iterator.next();
            if (next.getValue().submissionDate().isAfter(after)) {
                items.put(next.getKey(), next.getValue().parcel());
            }
        }
        return items;
    }
    
}
