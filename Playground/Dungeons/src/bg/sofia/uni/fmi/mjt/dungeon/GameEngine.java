package bg.sofia.uni.fmi.mjt.dungeon;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Enemy;
import bg.sofia.uni.fmi.mjt.dungeon.actor.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.actor.Position;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;

public class GameEngine {
    private final char HERO_SYMBOL = 'H';
    private final char ENEMY_SYMBOL = 'E';
    private final char START_SYMBOL = 'S';
    private final char END_SYMBOL = 'G';
    private final char TREASURE_SYMBOL = 'T';
    private final char BARRIER_SYMBOL = '#';
    private final char PATH_SYMBOL = '.';

    private char[][] map;
    private Hero hero;
    private Enemy[] enemies;
    private Treasure[] treasures;
    private boolean[] isTakenTreasure;
    private Position positionHero;

    public GameEngine(char[][] map, Hero hero, Enemy[] enemies, Treasure[] treasures) {
        this.map = map;
        this.hero = hero;

        this.enemies = new Enemy[enemies.length];
        for (int i = 0; i < enemies.length; ++i) {
            this.enemies[i] = enemies[i];
        }

        this.treasures = new Treasure[treasures.length];
        for (int i = 0; i < treasures.length; ++i) {
            this.treasures[i] = treasures[i];
        }

        this.isTakenTreasure = new boolean[treasures.length];

        positionHero = findStartPosition();
        map[positionHero.getX()][positionHero.getY()] = HERO_SYMBOL;
    }

    public char[][] getMap() {
        return map;
    }

    public Hero getHero() {
        return hero;
    }

    public Position getHeroPosition() {
        return positionHero;
    }

    public String makeMove(Direction direction) {
        if (direction != Direction.DOWN
                && direction != Direction.UP
                && direction != Direction.LEFT
                && direction != Direction.RIGHT) {
            return "Unknown command entered.";
        }

        Position newPosition = getNewPosition(direction);

        if (checkPositionIsValid(newPosition)) {

            if (checkSymbolAtPosition(newPosition, PATH_SYMBOL)) {
                moveHero(newPosition);
                return "You moved successfully to the next position.";
            }

            if (checkSymbolAtPosition(newPosition, BARRIER_SYMBOL)) {
                return "Wrong move. There is an obstacle and you cannot bypass it.";
            }

            if (checkSymbolAtPosition(newPosition, TREASURE_SYMBOL)) {
                moveHero(newPosition);
                return getTreasure();
            }

            if (checkSymbolAtPosition(newPosition, ENEMY_SYMBOL)) {
                return attack(newPosition);
            }

            if (checkSymbolAtPosition(newPosition, END_SYMBOL)) {
                return "You have successfully passed through the dungeon. Congrats!";
            }

        }

        return "Wrong move! Position is not valid.";
    }

    private Position findStartPosition() {
        for (int i = 0; i < map.length; ++i) {
            for (int j = 0; j < map[i].length; ++j) {
                if (map[i][j] == START_SYMBOL) {
                    return new Position(i, j);
                }
            }
        }
        return new Position(0, 0);
    }


    private Position getNewPosition(Direction direction) {
        Position newPosition;

        if (direction == Direction.UP) {
            newPosition = new Position(positionHero.getX() - 1, positionHero.getY());
        } else if (direction == Direction.DOWN) {
            newPosition = new Position(positionHero.getX() + 1, positionHero.getY());
        } else if (direction == Direction.LEFT) {
            newPosition = new Position(positionHero.getX(), positionHero.getY() - 1);
        } else {
            newPosition = new Position(positionHero.getX(), positionHero.getY() + 1);
        }

        return newPosition;
    }

    private boolean checkPositionIsValid(final Position position) {
        return position.getX() >= 0 && position.getX() < map.length
                && position.getY() >= 0 && position.getY() < map[0].length;
    }

    private boolean checkSymbolAtPosition(final Position position, final char symbol) {
        return map[position.getX()][position.getY()] == symbol;
    }

    private void moveHero(final Position position) {
        map[positionHero.getX()][positionHero.getY()] = PATH_SYMBOL;
        positionHero = position;
        map[positionHero.getX()][positionHero.getY()] = HERO_SYMBOL;
    }

    private String getTreasure() {
        int index = 0;
        for (int i = 0; i < isTakenTreasure.length; ++i) {
            if (!isTakenTreasure[i]) {
                isTakenTreasure[i] = true;
                index = i;
                break;
            }
        }
        return treasures[index].collect(hero);
    }


    private String attack(final Position position) {
        int index = 0;
        boolean hasAliveEnemy = false;

        for (int i = 0; i < enemies.length; ++i) {
            if (enemies[i].isAlive()) {
                index = i;
                hasAliveEnemy = true;
                break;
            }
        }

        if (!hasAliveEnemy) {
            moveHero(position);
            return "Enemy died.";
        }

        while (enemies[index].isAlive() && hero.isAlive()) {
            enemies[index].takeDamage(hero.attack());
            if (!enemies[index].isAlive()) {
                moveHero(position);
                return "Enemy died.";
            }
            hero.takeDamage(enemies[index].attack());
        }
        return "Hero is dead! Game over!";
    }

    public static void main(String[] args) {
        /*Hero hero;
        char[][] map;
        Enemy[] enemies;
        Treasure[] treasures;
        GameEngine gameEngine;
        hero = new Hero("hero", 100, 100);
        map = new char[][]{"S##".toCharArray(),
                "TE.".toCharArray(),
                "##G".toCharArray()};
        enemies = new Enemy[]{new Enemy("enemy", 100, 0, new Weapon("enemy weapon", 30), null)};
        treasures = new Treasure[]{new Weapon("strong weapon", 50)};
        gameEngine = new GameEngine(map, hero, enemies, treasures);

        gameEngine.makeMove(Direction.DOWN);
        gameEngine.makeMove(Direction.RIGHT);
        gameEngine.makeMove(Direction.RIGHT);
        gameEngine.makeMove(Direction.DOWN);*/

    }
}
