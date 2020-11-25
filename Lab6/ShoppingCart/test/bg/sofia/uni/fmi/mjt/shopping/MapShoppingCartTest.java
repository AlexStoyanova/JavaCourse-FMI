package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Chocolate;
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
public class MapShoppingCartTest {

    private static final Item ITEM_1 = new Chocolate("Chocolate1");
    private static final Item ITEM_2 = new Chocolate("Chocolate2");
    private static final Item ITEM_3 = new Chocolate("Chocolate3");

    @Mock
    private ProductCatalog productCatalog;

    @InjectMocks
    private MapShoppingCart mapShoppingCart;

    @Test
    public void testGetUniqueItemsSameItems() {
        mapShoppingCart.addItem(ITEM_1);
        mapShoppingCart.addItem(ITEM_1);

        Collection<Item> uniqueItems = new LinkedList<>();
        uniqueItems.add(ITEM_1);

        Collection<Item> returnedItems = mapShoppingCart.getUniqueItems();

        boolean isUnique = uniqueItems.containsAll(returnedItems) && returnedItems.containsAll(uniqueItems);
        assertTrue("Unique items.", isUnique);
    }

    @Test
    public void testGetUniqueItemsDifferentItems() {
        mapShoppingCart.addItem(ITEM_1);
        mapShoppingCart.addItem(ITEM_2);

        Collection<Item> uniqueItems = new LinkedList<>();
        uniqueItems.add(ITEM_1);
        uniqueItems.add(ITEM_2);

        Collection<Item> returnedItems = mapShoppingCart.getUniqueItems();

        boolean isUnique = uniqueItems.containsAll(returnedItems) && returnedItems.containsAll(uniqueItems);
        assertTrue("Unique items.", isUnique);
    }


    @Test
    public void testGetSortedItemsOneItem() {
        mapShoppingCart.addItem(ITEM_1);

        Collection<Item> oneItem = new LinkedList<>();
        oneItem.add(ITEM_1);

        Collection<Item> sortedItems = mapShoppingCart.getSortedItems();

        boolean isSorted = sortedItems.containsAll(oneItem) && oneItem.containsAll(sortedItems);
        assertTrue("Sorted items.", isSorted);
    }

    @Test
    public void testGetSortedItemsMoreThanOneItemsUnique() {
        mapShoppingCart.addItem(ITEM_1);
        mapShoppingCart.addItem(ITEM_1);
        mapShoppingCart.addItem(ITEM_1);
        mapShoppingCart.addItem(ITEM_2);
        mapShoppingCart.addItem(ITEM_2);
        mapShoppingCart.addItem(ITEM_3);

        Collection<Item> items = new LinkedList<>();
        items.add(ITEM_1);
        items.add(ITEM_2);
        items.add(ITEM_3);

        ProductInfo productInfo1 = new ProductInfo("Chocolate1", "chocolate", 2.00);
        ProductInfo productInfo2 = new ProductInfo("Chocolate2", "chocolate", 3.00);
        ProductInfo productInfo3 = new ProductInfo("Chocolate3", "chocolate", 4.00);
        when(productCatalog.getProductInfo("Chocolate1")).thenReturn(productInfo1);
        when(productCatalog.getProductInfo("Chocolate2")).thenReturn(productInfo2);
        when(productCatalog.getProductInfo("Chocolate3")).thenReturn(productInfo3);

        Collection<Item> sortedItems = mapShoppingCart.getSortedItems();
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
        mapShoppingCart.addItem(ITEM_1);
        mapShoppingCart.addItem(ITEM_2);
        mapShoppingCart.addItem(ITEM_3);

        Collection<Item> items = new LinkedList<>();
        items.add(ITEM_1);
        items.add(ITEM_2);
        items.add(ITEM_3);

        ProductInfo productInfo1 = new ProductInfo("Chocolate1", "chocolate", 2.00);
        ProductInfo productInfo2 = new ProductInfo("Chocolate2", "chocolate", 2.00);
        ProductInfo productInfo3 = new ProductInfo("Chocolate3", "chocolate", 2.00);
        when(productCatalog.getProductInfo("Chocolate1")).thenReturn(productInfo1);
        when(productCatalog.getProductInfo("Chocolate2")).thenReturn(productInfo2);
        when(productCatalog.getProductInfo("Chocolate3")).thenReturn(productInfo3);

        Collection<Item> sortedItems = mapShoppingCart.getSortedItems();

        boolean isSorted = sortedItems.containsAll(items) && items.containsAll(sortedItems);
        assertTrue("Sorted items.", isSorted);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddItemIllegalArgument() {
        mapShoppingCart.addItem(null);
    }

    @Test
    public void testAddItem() {
        mapShoppingCart.addItem(ITEM_1);
        assertTrue("Add item.", mapShoppingCart.items.containsKey(ITEM_1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveItemIllegalArgument() {
        mapShoppingCart.removeItem(null);
    }

    @Test(expected = ItemNotFoundException.class)
    public void testRemoveItemItemNotFound() {
        mapShoppingCart.addItem(ITEM_1);
        mapShoppingCart.removeItem(ITEM_2);
    }

    @Test
    public void testRemoveItemSameItems() {
        mapShoppingCart.addItem(ITEM_1);
        mapShoppingCart.addItem(ITEM_1);

        mapShoppingCart.removeItem(ITEM_1);

        Collection<Item> oneItem = new LinkedList<>();
        oneItem.add(ITEM_1);

        boolean isRemoved = oneItem.containsAll(mapShoppingCart.items.keySet())
                && mapShoppingCart.items.keySet().containsAll(oneItem);
        assertTrue("Remove items.", isRemoved);
    }

    @Test
    public void testRemoveItemDifferentItems() {
        mapShoppingCart.addItem(ITEM_1);
        mapShoppingCart.addItem(ITEM_2);
        mapShoppingCart.removeItem(ITEM_1);

        Collection<Item> oneItem = new LinkedList<>();
        oneItem.add(ITEM_2);

        boolean isRemoved = oneItem.containsAll(mapShoppingCart.items.keySet())
                && mapShoppingCart.items.keySet().containsAll(oneItem);
        assertTrue("Remove items.", isRemoved);
    }

    @Test
    public void testRemoveItemOneItemOnly() {
        mapShoppingCart.addItem(ITEM_1);
        mapShoppingCart.removeItem(ITEM_1);

        assertTrue("Remove item, empty cart.", mapShoppingCart.items.isEmpty());
    }


    @Test
    public void testGetTotalOneItem() {
        mapShoppingCart.addItem(ITEM_1);

        ProductInfo productInfo = new ProductInfo("Chocolate1", "chocolate", 2.00);
        when(productCatalog.getProductInfo("Chocolate1")).thenReturn(productInfo);

        assertEquals("Total amount.", 2.00, mapShoppingCart.getTotal(), 2);
    }

    @Test
    public void testGetTotalMoreThanOneItem() {
        mapShoppingCart.addItem(ITEM_1);
        mapShoppingCart.addItem(ITEM_2);

        ProductInfo productInfo1 = new ProductInfo("Chocolate1", "chocolate", 2.00);
        ProductInfo productInfo2 = new ProductInfo("Chocolate2", "chocolate", 4.00);

        when(productCatalog.getProductInfo("Chocolate1")).thenReturn(productInfo1);
        when(productCatalog.getProductInfo("Chocolate2")).thenReturn(productInfo2);

        assertEquals("Total amount.", 6.00, mapShoppingCart.getTotal(), 0.001);
    }
}
