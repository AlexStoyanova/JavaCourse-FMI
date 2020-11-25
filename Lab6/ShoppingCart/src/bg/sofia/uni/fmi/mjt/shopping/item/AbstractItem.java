package bg.sofia.uni.fmi.mjt.shopping.item;

import java.util.Objects;

public abstract class AbstractItem implements Item, Comparable {
    private String id;

    public AbstractItem(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractItem abstractItem = (AbstractItem) o;
        return Objects.equals(id, abstractItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Object o) {
        AbstractItem abstractItem = (AbstractItem) o;
        return id.compareTo(abstractItem.id);
    }
}
