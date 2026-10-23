import java.util.Random;

class WanderSeekStrategy implements BotStrategy {
    private static final double SIGHT = 300;
    private static final int WANDER_FRAMES = 180;
    private static final double SNAP_DISTANCE = 80;
    private static final double WANDER_REACHED = 40;

    private final Random random = new Random();
    private Food target;
    private double targetX;
    private double targetY;
    private int framesLeft = 0;

    @Override
    public double[] decide(Snake self, Iterable<Food> pellets) {
        double headX = self.getX();
        double headY = self.getY();

        boolean targetAlive = false;
        Food nearest = null;
        double best = SIGHT;
        for (Food f : pellets) {
            if (f == target) {
                targetAlive = true;
            }
            double d = Math.hypot(f.x - headX, f.y - headY);
            if (d < best) {
                best = d;
                nearest = f;
            }
        }

        boolean chasingPellet = true;
        if (targetAlive) {
            targetX = target.x;
            targetY = target.y;
        } else if (nearest != null) {
            target = nearest;
            targetX = nearest.x;
            targetY = nearest.y;
        } else {
            chasingPellet = false;
            target = null;
            double distToWander = Math.hypot(targetX - headX, targetY - headY);
            if (framesLeft <= 0 || distToWander < WANDER_REACHED) {
                targetX = 100 + random.nextDouble() * (GamePanel.WORLD_WIDTH - 200);
                targetY = 100 + random.nextDouble() * (GamePanel.WORLD_HEIGHT - 200);
                framesLeft = WANDER_FRAMES;
            }
            framesLeft--;
        }

        double dx = targetX - headX;
        double dy = targetY - headY;
        if (chasingPellet && Math.hypot(dx, dy) < SNAP_DISTANCE) {
            self.setAngle(Math.atan2(dy, dx));
        }

        return new double[]{targetX, targetY};
    }
}