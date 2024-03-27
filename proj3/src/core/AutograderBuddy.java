package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

public class AutograderBuddy {
    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */

    public static TETile[][] getWorldFromInput(String input) {
        TETile[][] returnTiles;
        World returnWorld;
        //new world
        if (input.charAt(0) == 'n' || input.charAt(0) == 'N') {
            StringBuilder moves = new StringBuilder();
            long seed = extractSeed(input);
            returnWorld = new World(seed);
            returnTiles = returnWorld.getTiles();
            int seedLength = Long.toString(seed).length();
            Avatar avatar = new Avatar(returnWorld.getAvatar().getInitialX(),
                    returnWorld.getAvatar().getInitialY(), Tileset.AVATAR);

            for (int i = seedLength + 1; i < input.length(); i++) {
                processCommand(input.charAt(i), avatar, returnTiles, false);
                moves.append(input.charAt(i));

                if (input.charAt(i) == ':') {
                    if (i + 1 < input.length() && (input.charAt(i + 1) == 'q' || input.charAt(i + 1) == 'Q')) {
                        saveWorld(moves.toString(), seed);
                    }
                }
            }
            return returnTiles;
        } else if (input.charAt(0) == 'l' || input.charAt(0) == 'L') { //loading world
            returnWorld = loadWorld();
            returnTiles = returnWorld.getTiles();
            StringBuilder moves = new StringBuilder();
            input = input.toLowerCase();
            for (int i = 1; i < input.length(); i++) {
                if (input.charAt(i) == 'w' || input.charAt(i) == 'a'
                        || input.charAt(i) == 's' || input.charAt(i) == 'd') {
                    processCommand(input.charAt(i), returnWorld.getAvatar(), returnTiles, true);
                    moves.append(input.charAt(i));
                }
                if (input.charAt(i) == ':') {
                    if (i + 1 < input.length() && (input.charAt(i + 1) == 'q' || input.charAt(i + 1) == 'Q')) {
                        saveWorld(moves.toString(), returnWorld.getSeedInt());
                    }
                }
            }
            return returnWorld.getTiles();
        }
        return null;
    }

    private static long extractSeed(String input) {
        int i = 1;
        StringBuilder seedBuilder = new StringBuilder();
        while (i < input.length() && input.charAt(i) != 'S' && input.charAt(i) != 's') {
            seedBuilder.append(input.charAt(i));
            i++;
        }
        String returnSeed = seedBuilder.toString();
        return Long.parseLong(returnSeed);
    }

    private static void processCommand(char command, Avatar inputAvatar, TETile[][] inputWorld, boolean isLoaded) {
        if (command == 'w') {
            inputAvatar.move(0, 1, inputWorld, isLoaded);
        } else if (command == 'a') {
            inputAvatar.move(-1, 0, inputWorld, isLoaded);
        } else if (command == 's') {
            inputAvatar.move(0, -1, inputWorld, isLoaded);
        } else if (command == 'd') {
            inputAvatar.move(1, 0, inputWorld, isLoaded);
        }
    }

    private static void saveWorld(String moves, Long seed) {
        StringBuilder sb = new StringBuilder();
        sb.append(seed).append(System.lineSeparator());
        sb.append(moves);
        FileUtils.writeFile("saved-file.txt", sb.toString());
    }

    private static World loadWorld() {
        World returnWorld = new World(0L); ///?
        if (FileUtils.fileExists("saved-file.txt")) {
            String content = FileUtils.readFile("saved-file.txt");
            String[] lines = content.split(System.lineSeparator());
            long seed = Long.parseLong(lines[0]);
            String moves = lines[1];
            returnWorld = new World(seed);
            TETile[][] worldtiles = returnWorld.getTiles();
            Avatar avatar = returnWorld.getAvatar();
            for (int i = 0; i < moves.length(); i++) {
                processCommand(moves.charAt(i), avatar, worldtiles, true);
            }
        }
        return returnWorld;
    }


    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character() || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}

