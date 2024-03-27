package core;
import tileengine.TETile;
import tileengine.Tileset;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

import java.awt.*;
import java.util.*;


public class Avatar {
    private int x, y;
    private int initialX, initialY;
    private int money;
    private TETile avatarTile;
    private Set<Point> collectedHorses;
    private boolean isWorldLoaded = false;

    public Avatar(int startX, int startY, TETile avatarTile) {
        this.initialX = startX;
        this.initialY = startY;
        this.x = initialX;
        this.y = initialY;
        this.avatarTile = avatarTile;
        this.collectedHorses = new HashSet<>();
    }

    public void move(int dx, int dy, TETile[][] world, boolean isLoaded) {
        int newX = x + dx;
        int newY = y + dy;

        if (isValidMove(newX, newY, world)) {
            if (world[newX][newY] == Tileset.HORSE && !collectedHorses.contains(new Point(newX, newY))) {
                collectedHorses.add(new Point(newX, newY));
                this.money += 1000;
                if (!isLoaded) {
                    playNeighSound();
                }
            }
            world[x][y] = Tileset.FLOOR;
            x = newX;
            y = newY;
            world[x][y] = avatarTile;

            this.x = newX;
            this.y = newY;
        }
    }

    private void playNeighSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("horse-neigh1.wav")));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            System.out.println("Failed to load the sound file.");
            ex.printStackTrace();
        }
    }

    private boolean isValidMove(int newX, int newY, TETile[][] world) {
        if (newX < 0 || newY < 0 || newX >= world.length || newY >= world[0].length) {
            return false;
        }
        return !world[newX][newY].equals(Tileset.WALL);
    }

    public int getX() {
        return this.x;
    }
    public int getInitialX() {
        return this.initialX;
    }

    public int getY() {
        return this.y;
    }
    public int getInitialY() {
        return this.initialY;
    }

    public int getMoney() {
        return this.money;
    }

}

