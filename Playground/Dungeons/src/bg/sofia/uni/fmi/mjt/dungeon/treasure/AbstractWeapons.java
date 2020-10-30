package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;

public abstract class AbstractWeapons implements Treasure
{
    protected String name;
    protected int damage;

    public AbstractWeapons(String name, int damage)
    {
        this.name = name;
        this.damage = damage;
    }

    public String getName()
    {
        return name;
    }

    public int getDamage()
    {
        return damage;
    }

    public abstract String collect(Hero hero);
}
