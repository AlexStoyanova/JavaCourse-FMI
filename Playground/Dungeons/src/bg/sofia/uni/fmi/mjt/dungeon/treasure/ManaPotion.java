package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;

public class ManaPotion extends Potion {
    public ManaPotion(int manaPoints) {
        super(manaPoints);
    }

    public ManaPotion(ManaPotion manaPotion) {
        super(manaPotion);
    }

    public String collect(Hero hero) {
        if (hero.isAlive() && heal() > 0) {
            hero.takeMana(this.heal());
        }
        return "Mana potion found! " + this.heal() + " mana points added to your hero!";
    }
}
