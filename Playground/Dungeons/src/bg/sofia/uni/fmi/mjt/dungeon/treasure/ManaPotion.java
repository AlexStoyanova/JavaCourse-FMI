package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;

public class ManaPotion extends Potion
{
    public ManaPotion(int manaPoints)
    {
        super(manaPoints);
    }

    public String collect(Hero hero)
    {
        hero.takeMana(points);
        return "Mana potion found! " + points + " mana points added to your hero!";
    }
}
