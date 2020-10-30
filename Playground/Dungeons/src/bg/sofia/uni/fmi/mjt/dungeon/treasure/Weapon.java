package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;

public class Weapon extends AbstractWeapons
{
    public Weapon(String name, int damage, int manaCost)
    {
        super(name, damage);
    }

    public Weapon(Weapon weapon)
    {
        super(weapon);
    }

    public String collect(Hero hero)
    {
        hero.equip(this);
        return "Weapon found! Damage points: " + damage;
    }
}
