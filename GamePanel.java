import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.*;


public class GamePanel extends JPanel implements Runnable {

    
    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    public int Width = SCREEN_SIZE.width;
    public int Height = SCREEN_SIZE.height;

    public static final int WORLD_WIDTH = 4000;
    public static final int WORLD_HEIGHT = 4000;

    private static final String HIGHSCORE_FILE = "highscore.txt";


    private volatile double cameraX = WORLD_WIDTH / 2.0 - Width / 2.0;
    private volatile double cameraY = WORLD_HEIGHT / 2.0 - Height / 2.0;

    private final Snake snake;
    private volatile int mouseX = 640;
    private volatile int mouseY = 360;
    private static final int FOOD_COUNT = 200;
    private static final int BOT_COUNT = 10;
    private static final int DEATH_FOOD_RADIUS = 11;
    private final String playerName;
    private final JFrame frame;

    CopyOnWriteArrayList<Food> pellets = new CopyOnWriteArrayList<>();
    private final ArrayList<Bot> bots = new ArrayList<>();

    private final Leaderboard leaderboard = new Leaderboard();
    private final JButton playAgainButton = new JButton("Play Again");
    private final JButton menuButton = new JButton("Menu");
    private volatile boolean gameOver = false;
    private int highScore;

    volatile Thread gameThread;

    public GamePanel(String playerName, JFrame frame) {
        this.playerName = playerName;
        this.frame = frame;
        this.highScore = loadHighScore();
        setPreferredSize(new Dimension(Width, Height));
        this.setBackground(new Color(0, 0, 0));
        this.setDoubleBuffered(true);
        setFocusable(true);
        requestFocusInWindow();

        setLayout(null);
        leaderboard.setBounds(Width - 220, 10, 200, 150);
        add(leaderboard);

        playAgainButton.setBounds((Width - 220) / 2, Height / 2 + 30, 220, 50);
        playAgainButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        playAgainButton.setFocusable(false);
        playAgainButton.setVisible(false);
        playAgainButton.addActionListener(e -> {
            stopGame();
            Main.startGame(frame, playerName);
        });
        add(playAgainButton);

        menuButton.setBounds((Width - 220) / 2, Height / 2 + 95, 220, 50);
        menuButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        menuButton.setFocusable(false);
        menuButton.setVisible(false);
        menuButton.addActionListener(e -> {
            stopGame();
            Main.showMenu(frame);
        });
        add(menuButton);

        snake = new Snake(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, true, Snake.randomColor());

        for (int i = 0; i < FOOD_COUNT; i++) {
            spawnFood();
        }

        for (int i = 0; i < BOT_COUNT; i++) {
            double x = 100 + Math.random() * (WORLD_WIDTH - 200);
            double y = 100 + Math.random() * (WORLD_HEIGHT - 200);
            bots.add(new Bot("Bot " + (i + 1), x, y, new WanderSeekStrategy()));
        }

        updateLeaderboard();

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


        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "boostOn");
        inputMap.put(KeyStroke.getKeyStroke("released SPACE"), "boostOff");

        actionMap.put("boostOn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                snake.setBoosting(true);
            }
        });

        actionMap.put("boostOff", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                snake.setBoosting(false);
            }
        });
    }


    static int loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
            return Integer.parseInt(reader.readLine().trim());
        } catch (IOException | RuntimeException e) {
            return 0;
        }
    }

    static void saveHighScore(int score) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(HIGHSCORE_FILE))) {
            writer.println(score);
        } catch (IOException e) {
            System.err.println("Could not save highscore: " + e.getMessage());
        }
    }


    // makes Grid
    private void drawGrid(Graphics2D g) {
        g.setColor(new Color(50, 50, 50));
        for (int x = 0; x < WORLD_WIDTH; x += 100) {
            g.drawLine(x - (int) cameraX, 0 - (int) cameraY, x - (int) cameraX, WORLD_HEIGHT - (int) cameraY);
        }
        for (int y = 0; y < WORLD_HEIGHT; y += 100) {
            g.drawLine(0 - (int) cameraX, y - (int) cameraY, WORLD_WIDTH - (int) cameraX, y - (int) cameraY);
        }
    }

    // draws the arena border at the world edges
    private void drawBorder(Graphics2D g) {
        g.setColor(new Color(220, 60, 60));
        g.setStroke(new BasicStroke(6));
        g.drawRect((int) (-cameraX), (int) (-cameraY), WORLD_WIDTH, WORLD_HEIGHT);
        g.setStroke(new BasicStroke(1));
    }
    

    // camera
    private void updateCamera() {
        cameraX = snake.getX() - getWidth() / 2.0;
        cameraY = snake.getY() - getHeight() / 2.0;

        cameraX = Math.max(0, Math.min(cameraX, WORLD_WIDTH - getWidth()));
        cameraY = Math.max(0, Math.min(cameraY, WORLD_HEIGHT - getHeight()));
    }

    // spawn one pellet of food
    private void spawnFood() {
        int maxX = GamePanel.WORLD_WIDTH;
        int maxY = GamePanel.WORLD_HEIGHT;
        int marg = 30; // so it does not spawn on the border

        int ranX = (int) (Math.random() * (maxX - marg)) + marg;
        int ranY = (int) (Math.random() * (maxY - marg)) + marg;
        int radius = (int) (Math.random() * 10) + 9;

        pellets.add(new Food(ranX, ranY, radius));
    }

    // pushes the current length of the player and every bot to the leaderboard
    private void updateLeaderboard() {
        leaderboard.setScore(playerName, snake.isAlive() ? snake.getLength() : 0);
        for (Bot bot : bots) {
            leaderboard.setScore(bot.getName(), bot.getSnake().getLength());
        }
    }

    // true if the head is outside the arena
    private boolean outsideWorld(Snake s) {
        double r = s.getRadius();
        return s.getX() < r || s.getY() < r
            || s.getX() > WORLD_WIDTH - r || s.getY() > WORLD_HEIGHT - r;
    }

    // true if the head of a touches a body segment of b (the head of b is not counted here)
    private boolean headHitsBody(Snake a, Snake b) {
        double[][] segs = b.getBodyPositions();
        double headR = a.getRadius();
        double bodyR = b.getRadius() - 4;
        for (int i = 1; i < segs.length; i++) {
            if (Math.hypot(a.getX() - segs[i][0], a.getY() - segs[i][1]) < headR + bodyR) {
                return true;
            }
        }
        return false;
    }

    // true if two heads touch each other
    private boolean headsTouch(Snake a, Snake b) {
        return Math.hypot(a.getX() - b.getX(), a.getY() - b.getY()) < a.getRadius() + b.getRadius();
    }

    // drops food along the body of a dead snake, one pellet per two segments
    private void dropFood(Snake s) {
        double[][] segs = s.getBodyPositions();
        for (int i = 0; i < segs.length; i += 2) {
            pellets.add(new Food(segs[i][0], segs[i][1], DEATH_FOOD_RADIUS));
        }
    }

    // finds all dead snakes, drops their food, kills the player or respawns the bot
    private void handleDeaths() {
        List<Snake> all = new ArrayList<>();
        if (snake.isAlive()) {
            all.add(snake);
        }
        for (Bot bot : bots) {
            all.add(bot.getSnake());
        }

        Set<Snake> dead = new HashSet<>();

        for (Snake a : all) {
            if (outsideWorld(a)) {
                dead.add(a);
            }
        }

        for (Snake a : all) {
            for (Snake b : all) {
                if (a != b && headHitsBody(a, b)) {
                    dead.add(a);
                }
            }
        }

        for (int i = 0; i < all.size(); i++) {
            for (int j = i + 1; j < all.size(); j++) {
                Snake a = all.get(i);
                Snake b = all.get(j);
                if (headsTouch(a, b)) {
                    if (a.getLength() < b.getLength()) {
                        dead.add(a);
                    } else if (b.getLength() < a.getLength()) {
                        dead.add(b);
                    } else {
                        dead.add(a);
                        dead.add(b);
                    }
                }
            }
        }

        for (int i = 0; i < bots.size(); i++) {
            Bot bot = bots.get(i);
            if (dead.contains(bot.getSnake())) {
                dropFood(bot.getSnake());
                double x = 100 + Math.random() * (WORLD_WIDTH - 200);
                double y = 100 + Math.random() * (WORLD_HEIGHT - 200);
                bots.set(i, new Bot(bot.getName(), x, y, new WanderSeekStrategy()));
            }
        }

        if (snake.isAlive() && dead.contains(snake)) {
            dropFood(snake);
            snake.die();
        }
    }

    // saves a new highscore and shows the game over buttons
    private void endGame() {
        int score = snake.getLength();
        if (score > highScore) {
            highScore = score;
            saveHighScore(score);
        }
        SwingUtilities.invokeLater(() -> {
            playAgainButton.setVisible(true);
            menuButton.setVisible(true);
            repaint();
        });
    }

    void stopGame() {
        gameThread = null;
    }


    public void StartGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    // game loop, about 60 frames per second
    @Override
    public void run() {
        final long frameTime = 16; // milliseconds per frame

        while (gameThread != null) {
            long start = System.currentTimeMillis();

            if (snake.isAlive()) {
                double worldMouseX = mouseX + cameraX;
                double worldMouseY = mouseY + cameraY;

                Food droppedFood = snake.updateAndDrop(worldMouseX, worldMouseY);
                if (droppedFood != null) {
                    pellets.add(droppedFood);
                }

                // Food disappearing mechanism
                for (Food pellet : pellets) {
                    if (pellet.isTouching(snake.getX(), snake.getY(), snake.getRadius())) {
                        pellets.remove(pellet);
                        snake.grow(pellet.segmentValue());
                        spawnFood();
                    }
                }
            }

            for (Bot bot : bots) {
                Snake s = bot.getSnake();
                bot.update(pellets);
                for (Food pellet : pellets) {
                    if (pellet.isTouching(s.getX(), s.getY(), s.getRadius()) && pellets.remove(pellet)) {
                        s.grow(pellet.segmentValue());
                        spawnFood();
                    }
                }
            }

            handleDeaths();

            if (!snake.isAlive() && !gameOver) {
                gameOver = true;
                endGame();
            }

            updateLeaderboard();
            updateCamera();
            repaint();


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

    private void drawCentered(Graphics2D g2, String text, int cx, int y, Font font) {
        g2.setFont(font);
        int w = g2.getFontMetrics().stringWidth(text);
        g2.drawString(text, cx - w / 2, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid((Graphics2D) g);
        drawBorder((Graphics2D) g);
        for (Food pellet : pellets) {
            pellet.draw((Graphics2D) g, cameraX, cameraY);
        }
        for (Bot bot : bots) {
            bot.getSnake().draw((Graphics2D) g, cameraX, cameraY);
        }
        snake.draw((Graphics2D) g, cameraX, cameraY);

        if (gameOver) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 170));
            g2.fillRect(0, 0, getWidth(), getHeight());

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            g2.setColor(Color.WHITE);
            drawCentered(g2, "GAME OVER", cx, cy - 80, new Font("SansSerif", Font.BOLD, 48));
            drawCentered(g2, "Score: " + snake.getLength(), cx, cy - 30, new Font("SansSerif", Font.PLAIN, 28));
            drawCentered(g2, "Highscore: " + highScore, cx, cy + 10, new Font("SansSerif", Font.PLAIN, 28));
        }
    }
   
}