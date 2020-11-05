package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;

public abstract class Potion implements Treasure {
    private int points;

    public Potion(int points) {
        if (points < 0) {
            this.points = 0;
        }
        this.points = points;
    }

    public Potion(Potion potion) {
        this.points = potion.points;
    }

    public int heal() {
        return points;
    }

    public abstract String collect(Hero hero);
}
