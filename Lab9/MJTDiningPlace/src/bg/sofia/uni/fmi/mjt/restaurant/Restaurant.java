package bg.sofia.uni.fmi.mjt.restaurant;

public interface Restaurant {
    void submitOrder(Order order);

    Order nextOrder();

    int getOrdersCount();

    Chef[] getChefs();

    void close();
}
