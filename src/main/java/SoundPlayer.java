import javax.sound.sampled.*;
import java.io.File;

public class SoundPlayer {
    public static void playClick() {
        play("click.wav");
    }

    public static void playConfirm() {
        play("confirm.wav");
    }

    public static void playCancel() {
        play("cancel.wav");
    }

    public static void play(String fileName) {
        new Thread(() -> {
            try{
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(fileName));
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();

                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                        try {
                            audioIn.close();
                        } catch (Exception ignored) {}
                    }
                });
            }
            catch (Exception e) {
                System.out.println("sound error" + fileName);
            }
        }).start();
    }
}
