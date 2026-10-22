import java.util.Random;

// eats the nearest food in sight, otherwise wanders around
class Bot {
    private static final double SIGHT = 300;
    private static final int WANDER_FRAMES = 180;

    private final Snake snake;
    private final Random random = new Random();
    private double targetX;
    private double targetY;
    private int framesLeft = 0;

    Bot(double x, double y) {
        snake = new Snake(x, y, true); 
    }

    Snake getSnake() {
        return snake;
    }

    void update(Iterable<Food> pellets) {
        double headX = snake.getX();
        double headY = snake.getY();

        Food nearest = null;
        double best = SIGHT;
        for (Food f : pellets) {
            double d = Math.hypot(f.x - headX, f.y - headY);
            if (d < best) {
                best = d;
                nearest = f;
            }
        }

        if (nearest != null) {
            targetX = nearest.x;
            targetY = nearest.y;
        } else {
            if (framesLeft <= 0) {
                targetX = 100 + random.nextDouble() * (GamePanel.WORLD_WIDTH - 200);
                targetY = 100 + random.nextDouble() * (GamePanel.WORLD_HEIGHT - 200);
                framesLeft = WANDER_FRAMES;
            }
            framesLeft--;
        }

        snake.updateAndDrop(targetX, targetY);
    }
}