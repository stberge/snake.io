import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class GamePanel extends JPanel implements Runnable {

    
    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    public int Width = SCREEN_SIZE.width;
    public int Height = SCREEN_SIZE.height;

    public final int WORLD_WIDTH = 4000;
    public final int WORLD_HEIGHT = 4000;


    private double cameraX = WORLD_WIDTH / 2.0 - Width / 2.0;
    private double cameraY = WORLD_HEIGHT / 2.0 - Height / 2.0;

    private final Snake snake;
    private volatile int mouseX = 640;
    private volatile int mouseY = 360;

    Thread gameThread;

    public GamePanel() {
        setPreferredSize(new Dimension(Width, Height));
        this.setBackground(new Color(0, 0, 0));
        this.setDoubleBuffered(true);

        snake = new Snake(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);

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

    private void drawGrid(Graphics2D g) {
        g.setColor(new Color(50, 50, 50));
        for (int x = 0; x < WORLD_WIDTH; x += 100) {
            g.drawLine(x - (int) cameraX, 0 - (int) cameraY, x - (int) cameraX, WORLD_HEIGHT - (int) cameraY);
        }
        for (int y = 0; y < WORLD_HEIGHT; y += 100) {
            g.drawLine(0 - (int) cameraX, y - (int) cameraY, WORLD_WIDTH - (int) cameraX, y - (int) cameraY);
        }
    }
    


    private void updateCamera() {
        cameraX = snake.getX() - Width / 2.0;
        cameraY = snake.getY() - Height / 2.0;

        cameraX = Math.max(0, Math.min(cameraX, WORLD_WIDTH - Width));
        cameraY = Math.max(0, Math.min(cameraY, WORLD_HEIGHT - Height));
    }




    public void StartGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    // game loop, about 60 frames per second
    @Override
    public void run() {
        final long frameTime = 16; // milliseconds per frame
        double LastMouseX = mouseX + cameraX;
        double LastMouseY = mouseY + cameraY;

        while (gameThread != null) {
            long start = System.currentTimeMillis();
            double worldMouseX = mouseX + cameraX;
            double worldMouseY = mouseY + cameraY;

            snake.update(worldMouseX, worldMouseY, worldMouseX != LastMouseX || worldMouseY != LastMouseY);
            repaint();
            updateCamera();

            LastMouseX = worldMouseX;
            LastMouseY = worldMouseY;

            

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
        drawGrid((Graphics2D) g);
        snake.draw((Graphics2D) g, cameraX, cameraY);
    }

    private void setExtendedState(int MAXIMIZED_BOTH) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



    
}
