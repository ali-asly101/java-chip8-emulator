import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class Screen extends JPanel implements KeyListener {
        private static final int WIDTH = 64;
        private static final int HEIGHT = 32;
        private final BufferedImage screenImage;
        public final Keyboard keyboard;

        public Screen() {
                this.setPreferredSize(new Dimension(WIDTH * 10, HEIGHT * 10));
                this.setFocusable(true);
                this.requestFocusInWindow();
                this.addKeyListener(this);
                keyboard = new Keyboard();
                screenImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
                clearScreen();
        }

        @Override
        public void keyPressed(KeyEvent e) {
                char key = Character.toLowerCase(e.getKeyChar());
                switch (key) {
                        case '1':
                                keyboard.setKey(0x1, true);
                                break;
                        case '2':
                                keyboard.setKey(0x2, true);
                                break;
                        case '3':
                                keyboard.setKey(0x3, true);
                                break;
                        case '4':
                                keyboard.setKey(0xC, true);
                                break;
                        case 'q':
                                keyboard.setKey(0x4, true);
                                break;
                        case 'w':
                                keyboard.setKey(0x5, true);
                                break;
                        case 'e':
                                keyboard.setKey(0x6, true);
                                break;
                        case 'r':
                                keyboard.setKey(0xD, true);
                                break;
                        case 'a':
                                keyboard.setKey(0x7, true);
                                break;
                        case 's':
                                keyboard.setKey(0x8, true);
                                break;
                        case 'd':
                                keyboard.setKey(0x9, true);
                                break;
                        case 'f':
                                keyboard.setKey(0xE, true);
                                break;
                        case 'z':
                                keyboard.setKey(0xA, true);
                                break;
                        case 'x':
                                keyboard.setKey(0x0, true);
                                break;
                        case 'c':
                                keyboard.setKey(0xB, true);
                                break;
                        case 'v':
                                keyboard.setKey(0xF, true);
                                break;
                }
        }

        @Override
        public void keyReleased(KeyEvent e) {
                char key = Character.toLowerCase(e.getKeyChar());
                switch (key) {
                        case '1':
                                keyboard.setKey(0x1, false);
                                break;
                        case '2':
                                keyboard.setKey(0x2, false);
                                break;
                        case '3':
                                keyboard.setKey(0x3, false);
                                break;
                        case '4':
                                keyboard.setKey(0xC, false);
                                break;
                        case 'q':
                                keyboard.setKey(0x4, false);
                                break;
                        case 'w':
                                keyboard.setKey(0x5, false);
                                break;
                        case 'e':
                                keyboard.setKey(0x6, false);
                                break;
                        case 'r':
                                keyboard.setKey(0xD, false);
                                break;
                        case 'a':
                                keyboard.setKey(0x7, false);
                                break;
                        case 's':
                                keyboard.setKey(0x8, false);
                                break;
                        case 'd':
                                keyboard.setKey(0x9, false);
                                break;
                        case 'f':
                                keyboard.setKey(0xE, false);
                                break;
                        case 'z':
                                keyboard.setKey(0xA, false);
                                break;
                        case 'x':
                                keyboard.setKey(0x0, false);
                                break;
                        case 'c':
                                keyboard.setKey(0xB, false);
                                break;
                        case 'v':
                                keyboard.setKey(0xF, false);
                                break;
                }
        }

        @Override
        public void keyTyped(KeyEvent e) {}

        public void setPixel(int x, int y, int value)
        {
                int currentColor = getPixel(x,y);
                int xoredColor = currentColor ^ value;
                int color = (xoredColor == 1) ? Color.WHITE.getRGB() : Color.BLACK.getRGB();
                screenImage.setRGB(x,y,color);
        }

        public int getPixel(int x, int y) {
                return (screenImage.getRGB(x, y) == Color.WHITE.getRGB()) ? 1 : 0;
        }

        public void clearScreen() {
                Graphics2D g = screenImage.createGraphics();
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, WIDTH, HEIGHT);
                g.dispose();
                repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(screenImage, 0, 0, getWidth(), getHeight(), null);
        }
}