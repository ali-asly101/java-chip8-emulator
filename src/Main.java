import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
public class Main {
    static File file;
    static int currentTime;
    static int originalTime = 100;
    static int timeSleep = 3;

    public static void main(String[] args) throws IOException {

        chooseFiles();
        Memory memory = new Memory(file.getAbsolutePath());
        TimerSound timerSound = new TimerSound();
        Screen screen = new Screen();
        JFrame frame = new JFrame("Chip-8 Emulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel memoryVariables = new JLabel();
        JPanel emulatorPanel = new JPanel();
        JPanel memoryDataPanel = new JPanel();

        emulatorPanel.setPreferredSize(new Dimension(768, 384));
        emulatorPanel.add(screen);
        emulatorPanel.setBackground(Color.black);

        memoryDataPanel.setPreferredSize(new Dimension(300, 360));
        memoryDataPanel.add(memoryVariables);
        memoryDataPanel.setBackground(Color.white);

        JSplitPane finalPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, emulatorPanel, memoryDataPanel);
        finalPane.setResizeWeight(0.7);
        frame.add(finalPane);
        frame.pack();
        frame.setVisible(true);
        screen.setFocusable(true);
        screen.requestFocusInWindow();

        while (true) {
            memoryVariables = buildString(memory, memoryVariables);

            currentTime = originalTime;
            currentTime = currentTime - 5;

            if (currentTime >= 0) {
                memory.updateTimers();
                if (memory.getDelayTimer() > 0) {
                    timerSound.startSound();
                } else {
                    timerSound.stopSound();
                }
                originalTime = currentTime;
            } else {
                originalTime = 100;
            }

            int opCode = memory.getOpCode(memory.PC);
            int operator = (opCode >> 12) & 0x0F;
            int x, y, n, nn, nnn;
            x = (opCode >> 8) & 0x0F;
            y = (opCode >> 4) & 0x0F;
            n = opCode & 0x0F;
            nn = opCode & 0xFF;
            nnn = opCode & 0x0FFF;
            boolean incrementPC = true;

            switch (operator) {
                case 0x0:
                    switch (nnn) {
                        case 0x0E0:
                            screen.clearScreen();
                            break;
                        case 0x0EE:
                            memory.PC = memory.stack.pop();
                            break;
                    }
                    break;
                case 0x1:
                    memory.PC = nnn & 0xFFF;
                    incrementPC = false;
                    break;
                case 0x2:
                    memory.stack.push(memory.PC);
                    memory.PC = nnn & 0xFFF;
                    incrementPC = false;
                    break;
                case 0x3:
                    if (memory.getVarReg(x) == nn) {
                        memory.PC += 2;
                    }
                    break;
                case 0x4:
                    if (memory.getVarReg(x) != nn) {
                        memory.PC += 2;
                    }
                    break;
                case 0x5:
                    if (memory.getVarReg(x) == memory.getVarReg(y)) {
                        memory.PC += 2;
                    }
                    break;
                case 0x9:
                    if (memory.getVarReg(x) != memory.getVarReg(y)) {
                        memory.PC += 2;
                    }
                    break;
                case 0x6:
                    memory.setVarReg(x, nn);
                    break;
                case 0x7:
                    memory.addVarReg(x, nn);
                    break;
                case 0xA:
                    memory.setIndexReg(nnn);
                    break;
                case 0xD:
                    int xLoc = memory.getVarReg(x);
                    int yLoc = memory.getVarReg(y);
                    int address = memory.getIndexReg();
                    memory.setVarReg(0xF, 0);
                    for (int i = 0; i < n; i++) {
                        int yCord = (yLoc + i) % 32;
                        int currentByte = memory.ram[address + i] & 0xFF;
                        for (int j = 0; j < 8; j++) {

                            int xCord = (xLoc + j) % 64;
                            if (xLoc < 64) {
                                int bit = (currentByte & (0x80 >> j)) != 0 ? 1 : 0;
                                if (screen.getPixel(xCord, yCord) == 0x1 && bit == 0x1) {
                                    memory.setVarReg(0xF, 1);

                                }
                                screen.setPixel(xCord, yCord, bit);

                            } else {
                                break;
                            }

                        }
                    }
                    screen.repaint();
                    break;
                case 0x8:
                    switch (n) {
                        case 0:

                            memory.setVarReg(x, memory.getVarReg(y));
                            break;
                        case 1:
                            memory.setVarReg(x, memory.getVarReg(x) | memory.getVarReg(y));
                            break;
                        case 2:
                            memory.setVarReg(x, memory.getVarReg(x) & memory.getVarReg(y));
                            break;
                        case 3:
                            memory.setVarReg(x, memory.getVarReg(x) ^ memory.getVarReg(y));
                            break;
                        case 4:
                            if (memory.getVarReg(x) + memory.getVarReg(y) > 255) {
                                memory.setVarReg(0xF, 1);
                            }
                            memory.setVarReg(x, memory.getVarReg(x) + memory.getVarReg(y));
                            break;
                        case 5:
                            if (memory.getVarReg(x) > memory.getVarReg(y)) {
                                memory.setVarReg(0xF, 1);
                            } else {
                                memory.setVarReg(0xF, 0);
                            }
                            memory.setVarReg(x, memory.getVarReg(x) - memory.getVarReg(y));
                            break;
                        case 7:
                            if (memory.getVarReg(y) > memory.getVarReg(x)) {
                                memory.setVarReg(0xF, 1);
                            } else {
                                memory.setVarReg(0xF, 0);
                            }
                            memory.setVarReg(x, memory.getVarReg(y) - memory.getVarReg(x));
                            break;
                        case 6:
                            memory.setVarReg(x, memory.getVarReg(y));
                            int bitShifted = memory.getVarReg(x) & 0x01;
                            memory.setVarReg(x, (memory.getVarReg(x) >> 1) & 0xFF);
                            if (bitShifted == 1) {
                                memory.setVarReg(0xF, 1);
                            } else {
                                memory.setVarReg(0xF, 0);
                            }
                            break;
                        case 0xE:
                            memory.setVarReg(x, memory.getVarReg(y));
                            bitShifted = memory.getVarReg(x) & 0x80;
                            memory.setVarReg(x, (memory.getVarReg(x) << 1) & 0xFF);
                            memory.setVarReg(0xF, 0);
                            break;

                    }
                    break;

                case 0xB:
                    memory.PC = nnn + memory.getVarReg(0);
                    incrementPC = false;
                    break;
                case 0xC:
                    int randomInt = (int)(Math.random() * 256);
                    memory.setVarReg(x, randomInt & nn);
                    break;
                case 0xE:
                    switch (nn) {
                        case 0x9E:
                            if (screen.keyboard.isKeyPressed(memory.getVarReg(x))) {
                                memory.PC += 2;
                            }
                            break;
                        case 0xA1:
                            if (!screen.keyboard.isKeyPressed(memory.getVarReg(x))) {
                                memory.PC += 2;
                            }
                            break;

                    };
                    break;
                case 0xF:
                    switch (nn) {
                        case 0x1E:
                            int newI = memory.getIndexReg() + memory.getVarReg(x);

                            memory.setVarReg(0xF, newI > 0xFFF ? 1 : 0);

                            memory.setIndexReg(newI & 0xFFF);
                            break;
                        case 0x0A:
                            while (true) {
                                for (int i = 0; i < 16; i++) {
                                    if (screen.keyboard.isKeyPressed(i)) {
                                        break;
                                    }
                                }
                                break;

                            }
                            break;
                        case 0x29:
                            memory.setIndexReg(memory.getVarReg(x) * 5);
                            break;
                        case 0x33:
                            int regNum = memory.getVarReg(x);
                            memory.ram[memory.getIndexReg()] = regNum / 100;
                            memory.ram[memory.getIndexReg() + 1] = (regNum / 10) % 10;
                            memory.ram[memory.getIndexReg() + 2] = regNum % 10;
                            break;

                        case 0x55:
                            for (int i = 0; i <= x; i++) {
                                memory.ram[memory.getIndexReg() + i] = memory.getVarReg(i);
                            }
                            break;

                        case 0x65:
                            for (int i = 0; i <= x; i++) {
                                memory.setVarReg(i, memory.ram[memory.getIndexReg() + i]);
                            }
                            break;
                        case 0x07:
                            memory.setVarReg(x, memory.getDelayTimer());
                            break;
                        case 0x15:
                            memory.setDelayTimer(memory.getVarReg(x));
                            break;
                        case 0x18:
                            memory.setSoundTimer(memory.getVarReg(x));
                            break;

                    }
                    break;

            }
            if (incrementPC) {
                memory.PC += 2;
            }
            try {
                Thread.sleep(timeSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    public static void chooseFiles() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Chip8 File", "ch8"));

        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            System.out.println(result);

        };

    };

    public static JLabel buildString(Memory memory, JLabel emuData) {
        StringBuilder displayText = new StringBuilder("<html>");
        displayText.append("<b>Delay Timer:</b><br>");
        displayText.append(String.format("<font color='green'>%02d</font>", currentTime)).append("<br><br>");

        displayText.append("<b>Program Counter:</b><br>");
        displayText.append(String.format("<font color='blue'>0x%04X</font>", memory.PC)).append("<br><br>");

        displayText.append("<b>Index Register (I):</b><br>");
        displayText.append(String.format("<font color='red'>0x%04X</font>", memory.i)).append("<br><br>");

        displayText.append("<b>Variable Registers (V0-VF):</b><br>");
        for (int i = 0; i < 16; i++) {
            displayText.append(String.format("<b>V%X:</b> ", i));
            displayText.append(String.format("<font color='purple'>0x%02X</font>", memory.varReg[i]));
            displayText.append("<br>");
        }

        displayText.append("</div></html>");
        emuData.setText(displayText.toString());
        return emuData;
    }

}