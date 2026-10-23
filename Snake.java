import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;

public class Snake {
    private static final int SEGMENTS = 3;
    private static final double SPEED = 3.0;
    private static final double SEGMENT_GAP = 12;
    private static final double BASETURN_RATE = 0.07;
    private static final double MINTURN_RATE = 0.03;
    private static final int CAP_LENGTH = 60;
    private static final double BOOST_SPEED = 6.0;
    private static final int MIN_BOOST_LENGTH = 15;

    private final CopyOnWriteArrayList<double[]> body = new CopyOnWriteArrayList<>();
    private double angle = 0;
    private volatile boolean boostHeld = false;
    private double distanceSinceLastDrop = 0;
    private BufferedImage faceDerpImg;
    private final Color color;
    private boolean alive = true;

    private static final double BOOST_DROP_DISTANCE = 70;
    private static final int BOOST_PELLET_RADIUS = 10;

    public Snake(double x, double y, boolean withFace, Color color) {
        this.color = color;
        for (int i = 0; i < SEGMENTS; i++) {
            body.add(new double[]{x, y});
        }

        if (withFace) {
            try {
                faceDerpImg = ImageIO.read(new File(assets/"face_derp.png"));
            } catch (IOException e) {
                System.err.println("Could not load face_derp.png: " + e.getMessage());
            }
        }
    }

    public static Color randomColor() {
        float hue = (float) Math.random();
        float saturation = 0.7f + (float) Math.random() * 0.3f;
        float brightness = 0.8f + (float) Math.random() * 0.2f;
        return Color.getHSBColor(hue, saturation, brightness);
    }

    public void setBoosting(boolean held) {
        this.boostHeld = held;
    }
    public void setAngle(double newAngle) {
        this.angle = newAngle;
    }

    public int getLength() {
        return body.size();
    }

    public boolean isAlive() {
        return alive;
    }
    
    public void die() {
        alive = false;
    }
    
    public double[][] getBodyPositions() {
        return body.toArray(new double[0][]);
    }

    public Food updateAndDrop(double targetX, double targetY) {
        double[] head = body.get(0);

        double dx = targetX - head[0];
        double dy = targetY - head[1];
        double t = Math.min(1.0, (body.size() - SEGMENTS) / (double) (CAP_LENGTH - SEGMENTS));
        double turnRate = BASETURN_RATE - (BASETURN_RATE - MINTURN_RATE) * Math.pow(t, 1);
        boolean boosting = boostHeld && body.size() > MIN_BOOST_LENGTH;
        double currentSpeed = boosting ? BOOST_SPEED : SPEED;

        if (Math.hypot(dx, dy) > 5) {
            double targetAngle = Math.atan2(dy, dx);
            double diff = targetAngle - angle;

            while (diff > Math.PI) {
                diff -= 2 * Math.PI;
            }
            while (diff < -Math.PI) {
                diff += 2 * Math.PI;
            }

            if (Math.abs(diff) < turnRate) {
                angle = targetAngle;
            } else {
                angle += turnRate * Math.signum(diff);
            }
        }

        head[0] += Math.cos(angle) * currentSpeed;
        head[1] += Math.sin(angle) * currentSpeed;

        for (int i = 1; i < body.size(); i++) {
            double[] front = body.get(i - 1);
            double[] seg = body.get(i);

            double segDx = front[0] - seg[0];
            double segDy = front[1] - seg[1];
            double dist = Math.hypot(segDx, segDy);

            if (dist > SEGMENT_GAP) {
                seg[0] = front[0] - segDx / dist * SEGMENT_GAP;
                seg[1] = front[1] - segDy / dist * SEGMENT_GAP;
            }
        }

        Food droppedFood = null;
        if (boosting) {
            distanceSinceLastDrop += currentSpeed;
            if (distanceSinceLastDrop >= BOOST_DROP_DISTANCE) {
                distanceSinceLastDrop = 0;
                double[] tail = body.remove(body.size() - 1);
                droppedFood = new Food(tail[0], tail[1], BOOST_PELLET_RADIUS);
            }
        } else {
            distanceSinceLastDrop = 0;
        }
        return droppedFood;
    }

    public double getX() {
        return body.get(0)[0];
    }

    public double getY() {
        return body.get(0)[1];
    }

    public int getRadius() {
        int baseRadius = 12;
        int sizeIncrease = body.size() / 15;
        return Math.min(baseRadius + sizeIncrease, 100);
    }

    public void grow(int amount) {
        for (int i = 0; i < amount; i++) {
            double[] last = body.get(body.size() - 1);
            body.add(new double[]{last[0], last[1]});
        }
    }

    public void draw(Graphics2D g, double cameraX, double cameraY) {
        if (!alive) {
            return;
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        double[][] segs = body.toArray(new double[0][]);

        int currentRadius = getRadius();
        for (int i = segs.length - 1; i >= 0; i--) {
            double[] seg = segs[i];

            if (i == 0) {
                int x = (int) (seg[0] - cameraX) - currentRadius;
                int y = (int) (seg[1] - cameraY) - currentRadius;
                int diameter = currentRadius * 2;

                g.setColor(color);
                g.fillOval(x, y, diameter, diameter);

                if (faceDerpImg != null) {
                    g.drawImage(faceDerpImg, x, y, diameter, diameter, null);
                }
            } else {
                int r = currentRadius - 4;
                g.setColor(color);
                g.fillOval(
                    (int) (seg[0] - cameraX) - r,
                    (int) (seg[1] - cameraY) - r,
                    r * 2,
                    r * 2
                );
            }
        }
    }
}
