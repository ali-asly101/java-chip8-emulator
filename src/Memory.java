// Memory.java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;

public class Memory {
    static final int MEMORY_SIZE = 4096;
    static final int PROGRAM_START = 0x200;
    private final int[] FONT_DATA = {
            0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            0x20, 0x60, 0x20, 0x20, 0x70, // 1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            0xF0, 0x10, 0x20, 0x40, 0x40, // 7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            0xF0, 0x80, 0xF0, 0x80, 0x80  // F
    };
    private int delayTimer;
    private int soundTimer;
    int[] ram = new int[MEMORY_SIZE];
    int[] varReg = new int[16];
    int i;
    int PC;
    Stack < Integer > stack = new Stack < > ();

    public Memory(String file) throws IOException {
        loadFont();
        loadROM(file);
        this.PC = PROGRAM_START;
    }

    public void setVarReg(int x, int nn) {
        varReg[x] = nn & 0xFF;
    }

    public void addVarReg(int x, int nn) {
        varReg[x] = (varReg[x] + (nn & 0xFF)) & 0xFF;
    }

    public int getVarReg(int x) {
        return varReg[x] & 0xFF;
    }

    public void setIndexReg(int nnn) {
        this.i = nnn & 0xFFF;
    }

    public int getIndexReg() {
        return this.i & 0xFFF;
    }

    public int getOpCode(int addr) {
        int firstByte = ram[addr] & 0xFF;
        int secondByte = ram[(addr + 1) & 0xFFF] & 0xFF;
        return ((firstByte << 8) | secondByte) & 0xFFFF;
    }

    public void loadROM(String file) throws IOException {
        Path path = Paths.get(file);
        byte[] bytes = Files.readAllBytes(path);
        for (int i = 0; i < bytes.length; i++) {
            ram[PROGRAM_START + i] = bytes[i] & 0xFF;
        }
    }

    public void loadFont() {
        System.arraycopy(FONT_DATA, 0, ram, 0, FONT_DATA.length);
    }

    public void setDelayTimer(int value) {
        delayTimer = value & 0xFF;
    }

    public int getDelayTimer() {
        return delayTimer;
    }

    public void setSoundTimer(int value) {
        soundTimer = value & 0xFF;
    }

    public void updateTimers() {
        if (delayTimer > 0) {
            delayTimer--;
        }
        if (soundTimer > 0) {
            soundTimer--;
        }
    }
}