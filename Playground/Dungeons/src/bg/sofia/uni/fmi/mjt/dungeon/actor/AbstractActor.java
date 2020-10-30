package bg.sofia.uni.fmi.mjt.dungeon.actor;

import bg.sofia.uni.fmi.mjt.dungeon.treasure.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Weapon;

public abstract class AbstractActor implements Actor
{

    protected String name;
    protected int health;
    protected int mana;
    protected Weapon weapon;
    protected Spell spell;

    public AbstractActor(String name, int health, int mana)
    {
        this.name = name;
        this.health = health;
        this.mana = mana;
    }

    public String getName()
    {
        return name;
    }

    public int getHealth()
    {
        return health;
    }

    public int getMana()
    {
        return mana;
    }

    public boolean isAlive()
    {
        return health > 0;
    }

    public Weapon getWeapon()
    {
        return weapon;
    }

    public Spell getSpell()
    {
        return spell;
    }

    public void takeDamage(int damagePoints)
    {
        health = ((damagePoints < health) ? (health - damagePoints) : 0);
    }

    public int attack()
    {
        //In progress
        return 0;
    }
}
