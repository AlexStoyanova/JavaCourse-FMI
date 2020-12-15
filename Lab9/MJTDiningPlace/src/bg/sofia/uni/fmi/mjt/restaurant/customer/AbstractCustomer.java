package bg.sofia.uni.fmi.mjt.restaurant.customer;

import bg.sofia.uni.fmi.mjt.restaurant.Meal;
import bg.sofia.uni.fmi.mjt.restaurant.Order;
import bg.sofia.uni.fmi.mjt.restaurant.Restaurant;

import java.util.Random;

public abstract class AbstractCustomer extends Thread {
    private static final int MAX_TIME = 3000;
    private static Random random = new Random();

    Restaurant workshop;

    public AbstractCustomer(Restaurant workshop) {
        this.workshop = workshop;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(random.nextInt(MAX_TIME));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        makeOrder();
    }

    public abstract boolean hasVipCard();

    private void makeOrder() {
        Order newOrder = new Order(Meal.chooseFromMenu(), this);
        this.workshop.submitOrder(newOrder);
    }
}
