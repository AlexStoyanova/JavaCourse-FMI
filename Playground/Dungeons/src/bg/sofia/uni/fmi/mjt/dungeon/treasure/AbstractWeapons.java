package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;

public abstract class AbstractWeapons implements Treasure {
    private String name;
    private int damage;

    public AbstractWeapons(String name, int damage) {
        this.name = name;
        this.damage = damage;
    }

    public AbstractWeapons(AbstractWeapons weapons) {
        if (weapons == null) {
            throw new NullPointerException();
        }
        this.name = weapons.name;
        this.damage = weapons.damage;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public abstract String collect(Hero hero);
}
