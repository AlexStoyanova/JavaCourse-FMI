package bg.sofia.uni.fmi.mjt.dungeon.actor;

import bg.sofia.uni.fmi.mjt.dungeon.treasure.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Weapon;

public abstract class AbstractActor implements Actor {

    protected String name;
    protected int health;
    protected int mana;
    protected Weapon weapon;
    protected Spell spell;

    public AbstractActor(String name, int health, int mana) {
        this.name = name;
        this.health = health;
        this.mana = mana;
    }

    public AbstractActor(AbstractActor abstractActor) {
        this.name = abstractActor.name;
        this.health = abstractActor.health;
        this.mana = abstractActor.mana;
        this.weapon = new Weapon(abstractActor.weapon);
        this.spell = new Spell(abstractActor.spell);
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getMana() {
        return mana;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public Spell getSpell() {
        return spell;
    }

    public void takeDamage(int damagePoints) {
        health = ((damagePoints < health) ? (health - damagePoints) : 0);
    }

    public int attack() {
        if (weapon == null && spell == null) {
            throw new NullPointerException();
        }

        if (weapon == null) {
            if (spell.getManaCost() <= mana) {
                mana -= spell.getManaCost();
                return spell.getDamage();
            }
            return 0;
        }

        if (spell == null) {
            return weapon.getDamage();
        }

        if (spell.getDamage() > weapon.getDamage() && spell.getManaCost() <= mana) {
            mana -= spell.getManaCost();
            return spell.getDamage();
        }

        return weapon.getDamage();
    }
}
