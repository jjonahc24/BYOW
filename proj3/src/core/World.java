
package core;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;
import java.util.List;


public class World {
    private long seedInt;
    private TETile[][] worldTiles;
    private List<Room> roomsList = new ArrayList<>();
    private List<Horse> horses = new ArrayList<>();
    public static final int WORLDWIDTH = 80;
    public static final int WORLDHEIGHT = 40;
    private TreeMap<Integer, Room> rooms;
    private TreeMap<Integer, ArrayList<Integer>> connections;
    private int numRooms;
    private Avatar avatar;

    public World(long seedInt) { //constructor
        this.seedInt = seedInt;
        worldTiles = new TETile[WORLDWIDTH][WORLDHEIGHT];
        rooms = new TreeMap<>();
        connections = new TreeMap<>();

        for (int j = 0; j < WORLDHEIGHT; j++) { //fill tiles with nothing
            for (int i = 0; i < WORLDWIDTH; i++) {
                worldTiles[i][j] = Tileset.NOTHING;
            }
        }
        placeRooms(); //place rooms
        placeHalls(); //place halls
        ensureAllConnected();
        placeAvatar();
        placeHorses();

    }
    public void placeAvatar() {
        Random rand = new Random(seedInt);
        boolean avatarPlaced = false;

        while (!avatarPlaced) {
            int x = rand.nextInt(WORLDWIDTH);
            int y = rand.nextInt(WORLDHEIGHT);

            if (worldTiles[x][y] == Tileset.FLOOR) {
                avatar = new Avatar(x, y, Tileset.AVATAR);
                worldTiles[x][y] = Tileset.AVATAR;
                avatarPlaced = true;
            }
        }
    }


    public void placeHorses() {
        Random rand = new Random(seedInt);
        for (Room room : roomsList) {
            int[] position = room.getCentralPosition(rand);
            Horse horse1 = new Horse(position[0], position[1], worldTiles);
            horses.add(horse1);
            if (!worldTiles[position[0]][position[1]].description().equals("Wall")) {
                worldTiles[position[0]][position[1]] = Tileset.HORSE;
            }
        }
    }


    public void placeRooms() {
        Random random = new Random(seedInt);
        numRooms = random.nextInt(150) + 8;

        for (int i = 0; i < numRooms; i++) {
            int roomWidth = random.nextInt(15) + 4; // generate random widths and heights
            int roomHeight = random.nextInt(15) + 4;
            int x = random.nextInt(WORLDWIDTH - roomWidth); // for random x and ys
            int y = random.nextInt(WORLDHEIGHT - roomHeight);
            Room room = new Room(roomWidth, roomHeight, x, y);
            if (room.isValidRoom(x, y, roomWidth, roomHeight, worldTiles)) {
                room.createRoom(x, y, worldTiles);
                room.updateID(i);
                rooms.put(room.getID(), room);
                roomsList.add(room);
                connections.put(room.getID(), new ArrayList<>(room.getID())); //each room starts connected to itself
            }
        }
    }

    public void placeHalls() {
        Random random = new Random(seedInt);
        for (int i = 0; i < numRooms; i++) {
            int roomAID = random.nextInt(numRooms);
            int roomBID = random.nextInt(numRooms);

            if (roomAID != roomBID) {
                Room roomA = rooms.get(roomAID);
                Room roomB = rooms.get(roomBID);
                if (roomA != null & roomB != null) {
                    Hall hall = new Hall();
                    hall.createHallway(roomA, roomB, worldTiles); //make hall
                    connections.get(roomAID).add(roomBID); // update connections for both room IDs
                    connections.get(roomBID).add(roomAID);
                }
            }
        }
    }

    public void ensureAllConnected() {
        boolean allConnected = false;
        Integer currentRoomKey = rooms.firstKey();

        while (!allConnected && currentRoomKey != null) {
            Integer nextRoomKey = rooms.higherKey(currentRoomKey);
            if (nextRoomKey != null) {
                Room connectedRooms = rooms.get(currentRoomKey);
                Room roomToConnect = rooms.get(nextRoomKey);
                if (connectedRooms != null && roomToConnect != null
                        && !connections.get(connectedRooms.getID()).contains(roomToConnect.getID())
                        && !connections.get(roomToConnect.getID()).contains(connectedRooms.getID())) {
                    Hall hall = new Hall();
                    hall.createHallway(connectedRooms, roomToConnect, worldTiles);
                    connections.get(connectedRooms.getID()).add(roomToConnect.getID());
                    if (connections.get(connectedRooms.getID()).size() == numRooms) {
                        allConnected = true;
                    }
                }
                currentRoomKey = nextRoomKey;
            } else {
                allConnected = true;
            }
        }
    }


    public TETile[][] getTiles() {
        return worldTiles;
    }

    public long getSeedInt() {
        return this.seedInt;
    }

    public Avatar getAvatar() {
        return this.avatar;
    }
}
