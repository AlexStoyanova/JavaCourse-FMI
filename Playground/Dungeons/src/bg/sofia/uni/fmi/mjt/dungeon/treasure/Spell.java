package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;

public class Spell extends AbstractWeapons {
    private int manaCost;

    public Spell(String name, int damage, int manaCost) {
        super(name, damage);
        this.manaCost = manaCost;
    }

    public Spell(Spell spell) {
        super(spell);
        this.manaCost = spell.manaCost;
    }

    public int getManaCost() {
        return manaCost;
    }

    public String collect(Hero hero) {
        hero.learn(this);
        return "Spell found! Damage points: "
                + this.getDamage()
                + ", Mana cost: "
                + this.getManaCost();
    }
}
