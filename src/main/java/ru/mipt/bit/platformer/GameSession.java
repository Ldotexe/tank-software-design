package ru.mipt.bit.platformer;

public final class GameSession {
    private static final int PLAYER_START_TILE_X = 1;
    private static final int PLAYER_START_TILE_Y = 1;
    private static final int TREE_TILE_X = 1;
    private static final int TREE_TILE_Y = 3;

    private final GameField field = new GameField();
    private final Tank player = new Tank(field, PLAYER_START_TILE_X, PLAYER_START_TILE_Y);

    public GameSession() {
        field.addTree(TREE_TILE_X, TREE_TILE_Y);
    }

    public GameField getField() {
        return field;
    }

    public Tank getPlayer() {
        return player;
    }

    public void update(float elapsedSeconds) {
        player.updateMovement(elapsedSeconds);
    }
}
