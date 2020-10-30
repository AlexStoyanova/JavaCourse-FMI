package bg.sofia.uni.fmi.mjt.dungeon.actor;

import bg.sofia.uni.fmi.mjt.dungeon.treasure.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Weapon;

public interface Actor
{

    String getName();

    int getHealth();

    int getMana();

    boolean isAlive();

    Weapon getWeapon();

    Spell getSpell();

    void takeDamage(int damagePoints);

    int attack();
}
