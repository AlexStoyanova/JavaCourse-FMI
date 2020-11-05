package bg.sofia.uni.fmi.mjt.dungeon.actor;

import bg.sofia.uni.fmi.mjt.dungeon.treasure.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Weapon;

public class Enemy extends AbstractActor {
    public Enemy(String name, int health, int mana, Weapon weapon, Spell spell) {
        super(name, health, mana);
        this.weapon = weapon;
        this.spell = spell;
    }

    public Enemy(Enemy enemy) {
        super(enemy);
    }
}
