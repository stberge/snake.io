import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class GamePanel extends JPanel implements Runnable {

    public final int Width = 1280;
    final int Height = 720;

    private final Snake snake;
    private volatile int mouseX = 640;
    private volatile int mouseY = 360;

    Thread gameThread;

    public GamePanel() {
        setPreferredSize(new Dimension(Width, Height));
        this.setBackground(new Color(0, 0, 0));
        this.setDoubleBuffered(true);

        snake = new Snake(Width / 2, Height / 2);

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
    }


    

    public void StartGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    // game loop, about 60 frames per second
    @Override
    public void run() {
        final long frameTime = 16; // milliseconds per frame
        double LastMouseX = mouseX;
        double LastMouseY = mouseY;

        while (gameThread != null) {
            long start = System.currentTimeMillis();

            snake.update(mouseX, mouseY, LastMouseX, LastMouseY);
            repaint();
            LastMouseX = mouseX;
            LastMouseY = mouseY;

            long elapsed = System.currentTimeMillis() - start;
            if (elapsed < frameTime) {
                try {
                    Thread.sleep(frameTime - elapsed);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        snake.draw((Graphics2D) g);
    }



    
}
