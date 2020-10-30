package bg.sofia.uni.fmi.mjt.dungeon.actor;

import bg.sofia.uni.fmi.mjt.dungeon.treasure.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Weapon;

public class Hero extends AbstractActor
{
    private final int initialHealth;
    private final int initialMana;

    public Hero(String name, int health, int mana)
    {
        super(name, health, mana);
        initialHealth = health;
        initialMana = mana;
    }

    public void takeHealing(int healingPoints)
    {
        if(this.isAlive())
        {
            health = Math.min(healingPoints + health, initialHealth);
        }
    }

    public void takeMana(int manaPoints)
    {
        if(this.isAlive())
        {
            mana = Math.min(manaPoints + mana, initialMana);
        }
    }

    public void equip(Weapon weapon)
    {
        if(this.weapon == null || this.weapon.getDamage() < weapon.getDamage())
        {
            this.weapon = weapon;
        }
    }

    public void learn(Spell spell)
    {
        if(this.spell == null || this.spell.getDamage() < spell.getDamage())
        {
            this.spell = spell;
        }
    }
}
