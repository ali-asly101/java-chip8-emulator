import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;

public class TimerSound {
    private Clip soundClip;

    public TimerSound() {
        try {
            initializeSound();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void initializeSound() throws Exception {
        AudioFormat audioFormat = new AudioFormat(44100, 8, 1, true, true);
        byte[] waveData = generateWave(audioFormat);

        AudioInputStream audioStream = new AudioInputStream(new ByteArrayInputStream(waveData), audioFormat, waveData.length);

        soundClip = AudioSystem.getClip();
        soundClip.open(audioStream);
    }

    private byte[] generateWave(AudioFormat format) {
        int samples = (int) format.getSampleRate();
        byte[] wave = new byte[samples];
        double period = format.getSampleRate() / 1200;

        for (int i = 0; i < samples; i++) {
            wave[i] = (byte)((i % period < period / 2) ? 127 : -128);
        }
        return wave;
    }

    public void startSound() {
        if (!soundClip.isRunning()) {
            soundClip.setFramePosition(0);
            soundClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopSound() {
        if (soundClip.isRunning()) {
            soundClip.stop();
        }
    }
}