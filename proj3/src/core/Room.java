package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

public class Room {
    private int width;
    private int height;
    private int ID;
    private int x;
    private int y;

    public Room(int width, int height, int x, int y) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.ID = 0;
    }

    public int[] getCentralPosition(Random rand) {

        int buffer = Math.min(width, height) / 4;

        buffer = Math.min(buffer, width / 2);
        buffer = Math.min(buffer, height / 2);

        int centralX = x + buffer + rand.nextInt(width - 2 * buffer);
        int centralY = y + buffer + rand.nextInt(height - 2 * buffer);

        return new int[]{centralX, centralY};
    }

    public void createRoom(int ranX, int ranY, TETile[][] worldTiles) {
        for (int i = ranY; i < ranY + height; i++) {
            for (int j = ranX; j < ranX + width; j++) {
                if (i == ranY || j == ranX || j == ranX + width - 1 || i == ranY + height - 1) { //wall tiles
                    worldTiles[j][i] = Tileset.WALL;
                } else { //floor tiles
                    worldTiles[j][i] = Tileset.FLOOR;
                }
            }
        }
    }

    public boolean isValidRoom(int ranX, int ranY, int roomWidth, int roomHeight, TETile[][] worldTiles) {
        for (int i = ranY - 1; i < ranY + roomHeight + 1; i++) {
            for (int j = ranX - 1; j < ranX + roomWidth + 1; j++) {
                if (i >= 0 && i < World.WORLDHEIGHT && j >= 0 && j < World.WORLDWIDTH) {
                    if (worldTiles[j][i] != Tileset.NOTHING) {
                        return false;
                    }
                }
            }
        }
        return true; // no overlap
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void updateID(int iD) {
        this.ID = iD;
    }

    public int getID() {
        return this.ID;
    }
}
