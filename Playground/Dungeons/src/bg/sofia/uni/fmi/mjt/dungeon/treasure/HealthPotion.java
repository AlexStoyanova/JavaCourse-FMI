package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;

public class HealthPotion extends Potion
{
    public HealthPotion(int healingPoints)
    {
        super(healingPoints);
    }

    public String collect(Hero hero)
    {
        hero.takeHealing(points);
        return "Health potion found! " + points + " health points added to your hero!";
    }
}
