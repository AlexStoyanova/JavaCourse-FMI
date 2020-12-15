package bg.sofia.uni.fmi.mjt.restaurant;

import bg.sofia.uni.fmi.mjt.restaurant.customer.Customer;
import bg.sofia.uni.fmi.mjt.restaurant.customer.VipCustomer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MJTDiningPlaceTest {
    private static MJTDiningPlace restaurant;
    private static Thread[] customers;
    private static final int NUMBER_OF_CHEFS = 10;
    private static final int NUMBER_OF_CUSTOMERS = 30;

    @BeforeClass
    public static void setRestaurant() {
        restaurant = new MJTDiningPlace(NUMBER_OF_CHEFS);
        customers = new Thread[NUMBER_OF_CUSTOMERS];
        for (int i = 0; i < NUMBER_OF_CUSTOMERS; ++i) {
            if (i % 2 == 0) {
                customers[i] = new Customer(restaurant);
            } else {
                customers[i] = new VipCustomer(restaurant);
            }
        }

        for (int i = 0; i < NUMBER_OF_CUSTOMERS; ++i) {
            customers[i].start();
        }
    }


    @Before
    public void set() throws InterruptedException {
        for (int i = 0; i < NUMBER_OF_CUSTOMERS; ++i) {
            customers[i].join();
        }
        Thread.sleep(1000);
        restaurant.close();
    }

    @Test
    public void testGetChefsTotalCookedMeals() {
        int count = 0;
        Chef[] chefs = restaurant.getChefs();
        for (Chef chef : chefs) {
            count += chef.getTotalCookedMeals();
        }
        int expected = 30;
        assertEquals("Total cooked meals. ", expected, count);
    }

    @Test
    public void testGetOrdersCount() {
        int expected = 30;
        int actual = restaurant.getOrdersCount();
        assertEquals("Orders count.", expected, actual);
    }
}


