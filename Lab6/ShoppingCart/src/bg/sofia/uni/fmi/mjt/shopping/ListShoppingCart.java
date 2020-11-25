package bg.sofia.uni.fmi.mjt.shopping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.TreeMap;

import bg.sofia.uni.fmi.mjt.shopping.item.Item;

public class ListShoppingCart implements ShoppingCart {

    ArrayList<Item> items;
    ProductCatalog catalog;

    public ListShoppingCart(ProductCatalog catalog) {
        this.catalog = catalog;
        this.items = new ArrayList<>();
    }

    @Override
    public Collection<Item> getUniqueItems() {
        return new TreeSet<>(items);
    }

    @Override
    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Argument is null!");
        }
        items.add(item);
    }

    @Override
    public void removeItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Argument is null!");
        }
        if (!items.contains(item)) {
            throw new ItemNotFoundException("Item not found!");
        }
        items.remove(item);
    }

    @Override
    public double getTotal() {
        double total = 0;
        for (Item item : items) {
            ProductInfo info = catalog.getProductInfo(item.getId());
            total += info.price();
        }
        return total;
    }

    @Override
    public Collection<Item> getSortedItems() {
        Map<Item, Integer> itemToQuantity = createMap();
        Map<Item, Integer> sortedItems = new TreeMap<>(new Comparator<Item>() {
            public int compare(Item item1, Item item2) {
                return itemToQuantity.get(item2).compareTo(itemToQuantity.get(item1));
            }
        });
        sortedItems.putAll(itemToQuantity);
        return sortedItems.keySet();
    }

    private Map<Item, Integer> createMap() {
        HashMap<Item, Integer> itemToQuantity = new HashMap<>();
        for (Item item : items) {
            boolean condition = itemToQuantity.containsKey(item);
            itemToQuantity.put(item, condition ? itemToQuantity.get(item) + 1 : 1);
        }
        return itemToQuantity;
    }
}