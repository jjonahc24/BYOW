package core;

import tileengine.TETile;
import tileengine.Tileset;


public class Hall {
    public Hall() {
    }

    public void createHallway(Room room1, Room room2, TETile[][] worldTiles) {
        int x1 = room1.getX() + room1.getWidth() / 2;
        int y1 = room1.getY() + room1.getHeight() / 2;
        int x2 = room2.getX() + room2.getWidth() / 2;
        int y2 = room2.getY() + room2.getHeight() / 2;

        if (x1 == x2) { // vertical hall
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                worldTiles[x1][y] = Tileset.FLOOR;
                addWall(worldTiles, x1 - 1, y);
                addWall(worldTiles, x1 + 1, y);
            }
        } else if (y1 == y2) { // horizontal hall
            for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                worldTiles[x][y1] = Tileset.FLOOR;
                addWall(worldTiles, x, y1 - 1);
                addWall(worldTiles, x, y1 + 1);
            }
        } else { // L hall
            int cornerX = x2;
            int cornerY = y1;

            for (int x = Math.min(x1, cornerX); x <= Math.max(x1, cornerX); x++) {
                worldTiles[x][y1] = Tileset.FLOOR;
                addWall(worldTiles, x, y1 - 1);
                addWall(worldTiles, x, y1 + 1);
            }
            for (int y = Math.min(cornerY, y2); y <= Math.max(cornerY, y2); y++) {
                worldTiles[cornerX][y] = Tileset.FLOOR;
                addWall(worldTiles, cornerX - 1, y);
                addWall(worldTiles, cornerX + 1, y);
            }
        }
    }


    private void addWall(TETile[][] worldTiles, int x, int y) {
        if (x >= 0 && x < World.WORLDWIDTH && y >= 0 && y < World.WORLDHEIGHT) {
            if (worldTiles[x][y] == Tileset.NOTHING) {
                worldTiles[x][y] = Tileset.WALL;
            }
        }
    }

}
