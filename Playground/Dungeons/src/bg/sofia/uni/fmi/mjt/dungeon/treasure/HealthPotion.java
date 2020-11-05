package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;

public class HealthPotion extends Potion {
    public HealthPotion(int healingPoints) {
        super(healingPoints);
    }

    public HealthPotion(HealthPotion healthPotion) {
        super(healthPotion);
    }

    public String collect(Hero hero) {
        if (hero.isAlive() && heal() > 0) {
            hero.takeHealing(this.heal());
        }
        return "Health potion found! " + this.heal() + " health points added to your hero!";
    }
}
