package bg.sofia.uni.fmi.mjt.restaurant;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class MJTDiningPlace implements Restaurant {

    private final int numberOfChefs;
    private int numberOfSubmittedOrders = 0;
    private volatile boolean isClosed = false;
    private Chef[] chefs;
    private Queue<Order> orders;


    public MJTDiningPlace(int numberOfChefs) {
        this.numberOfChefs = numberOfChefs;
        this.orders = new PriorityQueue<>(Comparator.comparing((Order o) -> o.customer().hasVipCard())
                .thenComparing(o -> o.meal().getCookingTime()));
        createChefs();
    }

    public synchronized void submitOrder(Order order) {
        orders.add(order);
        ++numberOfSubmittedOrders;
        this.notify();
    }

    public synchronized Order nextOrder() {
        if (isClosed && orders.isEmpty()) {
            return null;
        }
        while (orders.isEmpty()) {
            try {
                this.wait();
                if (isClosed) {
                    return null;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return orders.poll();
    }


    public int getOrdersCount() {
        return numberOfSubmittedOrders;
    }

    public Chef[] getChefs() {
        return chefs;
    }

    public synchronized void close() {
        isClosed = true;
        this.notifyAll();
    }

    private void createChefs() {
        this.chefs = new Chef[this.numberOfChefs];

        for (int i = 0; i < numberOfChefs; ++i) {
            chefs[i] = new Chef(i, this);
            chefs[i].start();
        }
    }
}
