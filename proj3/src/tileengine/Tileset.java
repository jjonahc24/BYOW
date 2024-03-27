package tileengine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {

    public static final TETile NOTHING = new TETile(' ', Color.BLACK, Color.BLACK, "Nothing");
    public static final TETile GRASS = new TETile('"', new Color(152, 251, 152), Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black, "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black, "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");
    public static final TETile HORSE = new TETile('♘', new Color(107, 45, 0), new Color(255, 150, 207), "Ken's horse");
    public static final TETile LIGHT_OFF = new TETile('○', Color.WHITE, new Color(255, 150, 207), "Light Bulb Off!");
    public static final TETile LIGHT_ON = new TETile('●', Color.WHITE, new Color(255, 150, 207), "Light Bulb On!");
    public static final TETile AVATAR = new TETile('❤', new Color(243, 83, 156), Color.WHITE, "Barbie");
    public static final TETile WALL = new TETile(' ', new Color(239, 177, 204), new Color(243, 83, 156), "Wall");
    public static final TETile FLOOR = new TETile('.', Color.white, new Color(255, 150, 207), "Dance floor");
}


