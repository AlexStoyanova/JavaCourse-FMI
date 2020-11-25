package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Apple;
import bg.sofia.uni.fmi.mjt.shopping.item.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ListShoppingCartTest {

    private static final Item ITEM_1 = new Apple("Apple1");
    private static final Item ITEM_2 = new Apple("Apple2");
    private static final Item ITEM_3 = new Apple("Apple3");

    @Mock
    private ProductCatalog productCatalog;

    @InjectMocks
    private ListShoppingCart listShoppingCart;

    @Test
    public void testGetUniqueItemsSameItems() {
        listShoppingCart.addItem(ITEM_1);
        listShoppingCart.addItem(ITEM_1);

        Collection<Item> uniqueItems = new LinkedList<>();
        uniqueItems.add(ITEM_1);

        Collection<Item> returnedItems = listShoppingCart.getUniqueItems();

        boolean isUnique = uniqueItems.containsAll(returnedItems) && returnedItems.containsAll(uniqueItems);
        assertTrue("Unique items.", isUnique);
    }

    @Test
    public void testGetUniqueItemsDifferentItems() {
        listShoppingCart.addItem(ITEM_1);
        listShoppingCart.addItem(ITEM_2);

        Collection<Item> uniqueItems = new LinkedList<>();
        uniqueItems.add(ITEM_1);
        uniqueItems.add(ITEM_2);

        Collection<Item> returnedItems = listShoppingCart.getUniqueItems();

        boolean isUnique = uniqueItems.containsAll(returnedItems) && returnedItems.containsAll(uniqueItems);
        assertTrue("Unique items.", isUnique);
    }

    @Test
    public void testGetSortedItemsOneItem() {
        listShoppingCart.addItem(ITEM_1);
        Collection<Item> oneItem = new LinkedList<>();
        oneItem.add(ITEM_1);

        Collection<Item> sortedItems = listShoppingCart.getSortedItems();

        boolean isSorted = sortedItems.containsAll(oneItem) && oneItem.containsAll(sortedItems);
        assertTrue("Sorted items.", isSorted);
    }

    @Test
    public void testGetSortedItemsMoreThanOneItemsUnique() {
        listShoppingCart.addItem(ITEM_1);
        listShoppingCart.addItem(ITEM_1);
        listShoppingCart.addItem(ITEM_1);
        listShoppingCart.addItem(ITEM_2);
        listShoppingCart.addItem(ITEM_2);
        listShoppingCart.addItem(ITEM_3);

        Collection<Item> items = new LinkedList<>();
        items.add(ITEM_1);
        items.add(ITEM_2);
        items.add(ITEM_3);

        Collection<Item> sortedItems = listShoppingCart.getSortedItems();
        Iterator<Item> itemsIterator = items.iterator();
        Iterator<Item> sortedItemsIterator = sortedItems.iterator();

        boolean isSorted = true;
        while (itemsIterator.hasNext() && sortedItemsIterator.hasNext()) {
            if (!itemsIterator.next().equals(sortedItemsIterator.next())) {
                isSorted = false;
            }
        }

        assertTrue("Sorted items.", isSorted);
    }

    @Test
    public void testGetSortedItemsMoreThanOneItemsSame() {
        listShoppingCart.addItem(ITEM_1);
        listShoppingCart.addItem(ITEM_2);
        listShoppingCart.addItem(ITEM_3);

        Collection<Item> items = new LinkedList<>();
        items.add(ITEM_1);
        items.add(ITEM_2);
        items.add(ITEM_3);

        Collection<Item> sortedItems = listShoppingCart.getSortedItems();

        boolean isSorted = sortedItems.containsAll(items) && items.containsAll(sortedItems);
        assertTrue("Sorted items.", isSorted);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddItemIllegalArgument() {
        listShoppingCart.addItem(null);
    }

    @Test
    public void testAddItem() {
        listShoppingCart.addItem(ITEM_1);
        assertTrue("Add items.", listShoppingCart.items.contains(ITEM_1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveItemIllegalArgument() {
        listShoppingCart.removeItem(null);
    }

    @Test(expected = ItemNotFoundException.class)
    public void testRemoveItemItemNotFound() {
        listShoppingCart.addItem(ITEM_1);
        listShoppingCart.removeItem(ITEM_2);
    }

    @Test
    public void testRemoveItemSameItems() {
        listShoppingCart.addItem(ITEM_1);
        listShoppingCart.addItem(ITEM_1);
        listShoppingCart.removeItem(ITEM_1);

        Collection<Item> oneItem = new LinkedList<>();
        oneItem.add(ITEM_1);

        boolean isRemoved = oneItem.containsAll(listShoppingCart.items) && listShoppingCart.items.containsAll(oneItem);
        assertTrue("Remove items.", isRemoved);
    }

    @Test
    public void testRemoveItemDifferentItems() {
        listShoppingCart.addItem(ITEM_1);
        listShoppingCart.addItem(ITEM_2);
        listShoppingCart.removeItem(ITEM_1);

        Collection<Item> oneItem = new LinkedList<>();
        oneItem.add(ITEM_2);

        boolean isRemoved = oneItem.containsAll(listShoppingCart.items) && listShoppingCart.items.containsAll(oneItem);
        assertTrue("Remove items.", isRemoved);
    }

    @Test
    public void testRemoveItemOneItemOnly() {
        listShoppingCart.addItem(ITEM_1);
        listShoppingCart.removeItem(ITEM_1);

        assertTrue("Remove items, empty cart.", listShoppingCart.items.isEmpty());
    }


    @Test
    public void testGetTotalOneItem() {
        listShoppingCart.addItem(ITEM_1);

        ProductInfo productInfo = new ProductInfo("Apple1", "apple", 2.00);
        when(productCatalog.getProductInfo("Apple1")).thenReturn(productInfo);

        assertEquals("Total amount.", 2.00, listShoppingCart.getTotal(), 2);
    }

    @Test
    public void testGetTotalMoreThanOneItem() {
        listShoppingCart.addItem(ITEM_1);
        listShoppingCart.addItem(ITEM_2);

        ProductInfo productInfo1 = new ProductInfo("Apple1", "apple", 2.00);
        ProductInfo productInfo2 = new ProductInfo("Apple2", "apple", 3.00);

        when(productCatalog.getProductInfo("Apple1")).thenReturn(productInfo1);
        when(productCatalog.getProductInfo("Apple2")).thenReturn(productInfo2);

        assertEquals("Total amount.", 5.00, listShoppingCart.getTotal(), 2);
    }
}
