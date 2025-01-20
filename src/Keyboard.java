// Keyboard.java
public class Keyboard {
    public boolean[] keys;

    public Keyboard() {
        keys = new boolean[16];
    }

    public void setKey(int num, boolean pressed) {
        if (num >= 0 && num < 16) {
            keys[num] = pressed;
        }
    }

    public boolean isKeyPressed(int num) {
        return keys[num];
    }

}