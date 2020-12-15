package bg.sofia.uni.fmi.mjt.restaurant;

public class Chef extends Thread {
    private final int id;
    private final Restaurant restaurant;
    private int numberOfCookedMeals;

    public Chef(int id, Restaurant restaurant) {
        this.id = id;
        this.restaurant = restaurant;
        this.numberOfCookedMeals = 0;
    }

    @Override
    public void run() {
        setName("Chef-" + id);
        cookMeal();
    }

    public int getTotalCookedMeals() {
        return numberOfCookedMeals;
    }

    private void cookMeal() {
        Order order;
        while ((order = restaurant.nextOrder()) != null) {
            try {
                Thread.sleep(order.meal().getCookingTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            numberOfCookedMeals++;
        }
    }
}
