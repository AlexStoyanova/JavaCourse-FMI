package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;

public abstract class Potion implements Treasure
{
    protected int points;

    public Potion(int points)
    {
        this.points = points;
    }

    public Potion(Potion potion)
    {
        this.points = potion.points;
    }

    public int heal()
    {
        return points;
    }

    public abstract String collect(Hero hero);
}
