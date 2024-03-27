package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

import java.awt.*;
import java.util.*;

public class Main {
    private static TETile[][] world;
    private static Long seed;
    private static Avatar avatar;
    private static final int HUD_HEIGHT = 2;
    private static World wholeWorld;
    private static boolean losEnabled = false;
    private static boolean[][] visibilityMap = new boolean[World.WORLDWIDTH][World.WORLDHEIGHT];
    private static StringBuilder moves = new StringBuilder();


    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        displayMainMenu();

        while (true) {
            while (StdDraw.hasNextKeyTyped()) {
                char choice = StdDraw.nextKeyTyped();
                choice = Character.toUpperCase(choice);

                if (choice == 'N') {
                    seed = getSeedFromUser();
                    wholeWorld = new World(seed);
                    world = wholeWorld.getTiles();
                    ter.initialize(World.WORLDWIDTH, World.WORLDHEIGHT + HUD_HEIGHT);
                    avatar = new Avatar(wholeWorld.getAvatar().getInitialX(),
                            wholeWorld.getAvatar().getInitialY(), Tileset.AVATAR);
                    ter.renderFrame(world);

                    initializeVisibilityMap();
                    runGameLoop(ter);

                } else if (choice == 'L') {
                    loadWorld();
                    ter.initialize(World.WORLDWIDTH, World.WORLDHEIGHT + HUD_HEIGHT);

                    initializeVisibilityMap();
                    runGameLoop(ter);

                } else if (choice == 'Q') {
                    System.exit(0);
                }
            }
        }
    }

    private static void runGameLoop(TERenderer ter) {
        boolean awaitingSecondCharForQuit = false;

        boolean needUpdate = true;
        String currentDescription = "";

        while (true) {

            if (areAllHorsesCleared(world)) {
                displayWinningScreen();
                break;
            }
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 't' || key == 'T') {
                    toggleLOS();
                    needUpdate = true;
                } else if (awaitingSecondCharForQuit) {
                    if (key == 'Q' || key == 'q') {
                        saveWorld(moves.toString(), seed);
                        System.exit(0);
                    }
                    awaitingSecondCharForQuit = false;
                } else if (key == ':') {
                    awaitingSecondCharForQuit = true;
                } else {
                    moveAvatar(key, false);
                    moves.append(key);
                    needUpdate = true;
                }
            }

            int mouseX = (int) StdDraw.mouseX();
            int mouseY = (int) StdDraw.mouseY();
            if (mouseX >= 0 && mouseX < World.WORLDWIDTH && mouseY >= 0 && mouseY < World.WORLDHEIGHT) {
                TETile hoveredTile = world[mouseX][mouseY];
                String description = hoveredTile.description();

                if (!description.equals(currentDescription)) {
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.filledRectangle(World.WORLDWIDTH / 2.0, World.WORLDHEIGHT + HUD_HEIGHT - 0.5, 10, 2);
                    StdDraw.setPenColor(new Color(243, 83, 156));
                    Font currentFont = StdDraw.getFont();
                    Font newFont = new Font(currentFont.getName(), Font.BOLD, currentFont.getSize());
                    StdDraw.setFont(newFont);
                    StdDraw.text(World.WORLDWIDTH / 2.0, World.WORLDHEIGHT + HUD_HEIGHT / 2.0,
                            "What's this?: " + description);
                    StdDraw.show();
                    currentDescription = description;
                }
            }

            if (needUpdate) {
                if (losEnabled) {
                    calculateLOS(avatar.getX(), avatar.getY(), 5, world);
                    drawWorldWithLOS(world);
                } else {
                    clearLOS();
                    ter.drawTiles(world);
                }
                renderHUD();
                StdDraw.show();
                needUpdate = false;
            }
        }
    }


    private static void toggleLOS() {
        losEnabled = !losEnabled;
    }

    private static void clearLOS() {
        for (int x = 0; x < World.WORLDWIDTH; x++) {
            for (int y = 0; y < World.WORLDHEIGHT; y++) {
                visibilityMap[x][y] = true;
            }
        }
    }

    private static void initializeVisibilityMap() {
        for (boolean[] row : visibilityMap) {
            Arrays.fill(row, false);
        }
    }

    private static void drawWorldWithLOS(TETile[][] world1) {
        StdDraw.clear(Color.BLACK);
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world1[0].length; y++) {
                if (visibilityMap[x][y]) {
                    world1[x][y].draw(x, y);
                } else {
                    drawFog(x, y);
                }
            }
        }
        StdDraw.show();
    }

    private static void drawFog(int x, int y) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledSquare(x + 0.5, y + 0.5, 0.5);
    }

    private static void calculateLOS(int avatarX, int avatarY, int radius, TETile[][] world1) {
        for (int x = 0; x < World.WORLDWIDTH; x++) {
            for (int y = 0; y < World.WORLDHEIGHT; y++) {
                visibilityMap[x][y] = false;
            }
        }

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int x = avatarX + dx;
                int y = avatarY + dy;

                if (x >= 0 && x < World.WORLDWIDTH && y >= 0 && y < World.WORLDHEIGHT
                        && dx * dx + dy * dy <= radius * radius) {

                    if (castRay(avatarX, avatarY, x, y, world1)) {
                        visibilityMap[x][y] = true;
                    }
                }
            }
        }
    }


    private static boolean castRay(int x0, int y0, int x1, int y1, TETile[][] world1) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        float xIncrement = dx / (float) steps;
        float yIncrement = dy / (float) steps;
        float currentX = x0;
        float currentY = y0;
        for (int i = 0; i <= steps; i++) {
            int tileX = Math.round(currentX);
            int tileY = Math.round(currentY);

            visibilityMap[tileX][tileY] = true;

            if (isNothingTile(world1[tileX][tileY])) {
                return false;
            }

            if (tileX == x1 && tileY == y1) {
                return true;
            }

            currentX += xIncrement;
            currentY += yIncrement;
        }
        return false;
    }


    private static boolean isNothingTile(TETile tile) {
        return tile.description().equals("Nothing");
    }

    private static void renderHUD() {
        int money = avatar.getMoney();

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(0, World.WORLDHEIGHT + HUD_HEIGHT / 2.0, World.WORLDWIDTH, HUD_HEIGHT - 1);
        StdDraw.setPenColor(new Color(243, 83, 156));

        StdDraw.text(4.75, World.WORLDHEIGHT + HUD_HEIGHT / 2.0, "SHOPPING $$$: " + money);

        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();

        if (mouseX >= 0 && mouseX < World.WORLDWIDTH && mouseY >= 0 && mouseY < World.WORLDHEIGHT) {
            TETile hoveredTile = world[mouseX][mouseY];
            if (hoveredTile != null) {
                String description = hoveredTile.description();
                double width = World.WORLDWIDTH / 2.0;
                double height = World.WORLDHEIGHT + HUD_HEIGHT / 2.0;
                StdDraw.text(width, height, "What's this?: " + description);
            }
        }
    }


    private static void moveAvatar(char key, boolean isLoaded) {
        char key1 = Character.toLowerCase(key);
        if (key1 == 'w') {
            avatar.move(0, 1, world, isLoaded);
        } else if (key1 == 'a') {
            avatar.move(-1, 0, world, isLoaded);
        } else if (key1 == 's') {
            avatar.move(0, -1, world, isLoaded);
        } else if (key1 == 'd') {
            avatar.move(1, 0, world, isLoaded);
        }
        calculateLOS(avatar.getX(), avatar.getY(), 5, world);
    }

    private static void displayMainMenu() {
        //StdDraw.enableDoubleBuffering();
        playWinMusic();
        StdDraw.setCanvasSize(1500, 800); // Increase the canvas size
        StdDraw.setXscale(0, 1500);
        StdDraw.setYscale(0, 800);
        drawMenu();

    }

    private static void drawMenu() {
        StdDraw.clear(StdDraw.WHITE);
        Font bigFont = new Font(null, 0, 40);
        StdDraw.setFont(bigFont);
        StdDraw.setPenColor(new Color(243, 83, 156));
        StdDraw.text(750, 600, "Barbie vs. Ken's Horses: The Game");
        StdDraw.text(750, 450, "New Game (N)");
        StdDraw.text(750, 350, "Load Game (L)");
        StdDraw.text(750, 250, "Quit (Q)");
        StdDraw.show();
        StdDraw.pause(100);
    }


    private static void saveWorld(String moves1, Long seed1) {
        StringBuilder sb = new StringBuilder();
        sb.append(seed1).append(System.lineSeparator());
        sb.append(moves1);
        FileUtils.writeFile("saved-file.txt", sb.toString());
    }


    private static void loadWorld() {
        if (FileUtils.fileExists("saved-file.txt")) {
            String content = FileUtils.readFile("saved-file.txt");
            String[] lines = content.split(System.lineSeparator());
            seed = Long.parseLong(lines[0]);
            moves = new StringBuilder(lines[1]);
            wholeWorld = new World(seed);
            world = wholeWorld.getTiles();
            avatar = new Avatar(wholeWorld.getAvatar().getInitialX(),
                    wholeWorld.getAvatar().getInitialY(), Tileset.AVATAR);
            for (int i = 0; i < moves.length(); i++) {
                processCommand(moves.charAt(i), avatar, wholeWorld.getTiles(), true);
            }
        } else {
            System.exit(0);
        }
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


    private static long getSeedFromUser() {
        Font font = new Font(null, 0, 30);
        StdDraw.setFont(font);
        StdDraw.clear(StdDraw.WHITE);

        StdDraw.setPenColor(new Color(243, 83, 156));

        StdDraw.text(750, 600, "Ken's horses have taken over the Dreamhouse!");
        StdDraw.text(750, 500, "Collect them to sell to Cowgirl Barbie and");
        StdDraw.text(750, 450, "earn $$$ to restore your Barbie DreamHouse!");
        StdDraw.text(750, 300, "Press 'T' at any point during the game to turn");
        StdDraw.text(750, 250, "off the lights and disorient the horses.");
        StdDraw.text(750, 200, "Enter Seed (Press 'S' to enter):");



        StringBuilder inputSeed = new StringBuilder();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 's' || key == 'S') {
                    try {
                        return Long.parseLong(inputSeed.toString());
                    } catch (NumberFormatException e) {
                        inputSeed.setLength(0);
                        StdDraw.clear(StdDraw.WHITE);
                        StdDraw.text(750, 450, "Invalid input! Enter a valid seed:");
                    }
                } else if (Character.isDigit(key)) {
                    inputSeed.append(key);
                    StdDraw.clear(StdDraw.WHITE);
                    StdDraw.text(750, 450, "Enter Seed: " + inputSeed.toString());
                }
            }
            StdDraw.show();
            StdDraw.pause(100);
        }
    }


    private static boolean areAllHorsesCleared(TETile[][] inputWorld) { //inputworld?
        for (int x = 0; x < inputWorld.length; x++) {
            for (int y = 0; y < inputWorld[0].length; y++) {
                if (inputWorld[x][y] == Tileset.HORSE) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void displayWinningScreen() {

        StdDraw.clear(StdDraw.WHITE);
        StdDraw.setPenColor(new Color(243, 83, 156));

        Font bigFont = new Font(null, 0, 40);


        StdDraw.setFont(bigFont);

        double centerX = World.WORLDWIDTH / 2.0;
        double centerY = World.WORLDHEIGHT / 2.0;

        StdDraw.text(centerX, centerY + 10, "You won! Girls Night!!");
        StdDraw.text(centerX, centerY + 5, "You restored Barbie's DreamHouse");
        StdDraw.text(centerX, centerY, "You earned " + avatar.getMoney() + " Dollars!");
        Font smolFont = new Font(null, 0,  30);
        StdDraw.setFont(smolFont);

        StdDraw.show();
    }

    private static void playWinMusic() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("lofi-barbie.wav"))
            );
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(100000);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            System.out.println("Failed to load the sound file.");
            ex.printStackTrace();
        }
    }
}

