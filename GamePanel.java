import java.awt.*;
import javax.swing.*;


public class GamePanel extends JPanel implements Runnable {

    public final int Width = 1280;
    final int Height = 720;

    public GamePanel() {
        setPreferredSize(new Dimension(Width, Height));
        this.setSize(1280, 720);
        this.setBackground(new Color(0, 0, 0));
        this.setDoubleBuffered(true);
    }


    Thread gameThread;

    public void StartGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    // game loop
    @Override 
    public void run(){
        while (gameThread != null){
            System.out.println("dit is de gameloop");

        }

    }



    
}
