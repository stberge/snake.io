import java.awt.*;
import java.util.ArrayList;

public class Snake {
    private static final int SEGMENTS = 15;
    private static final double SPEED = 3.0;          // pixels per frame
    private static final double SEGMENT_GAP = 12;     // distance between segments
    private static final int RADIUS = 12;

    //body [ [500, 300],
    // [ ... ]]
    private final ArrayList<double[]> body = new ArrayList<>(); 
    private double angle = 0; // direction the head is facing, in radians

    public Snake(double x, double y) {
        for (int i = 0; i < SEGMENTS; i++) {
            body.add(new double[]{x, y});
        }
    }

    public void update(double targetX, double targetY, double LastMouseX, double LastMouseY) {
        double[] head = body.get(0);

        if (targetX == LastMouseX && targetY == LastMouseY) {
            // If the mouse hasn't moved, keep moving in the same direction
            head[0] += Math.cos(angle) * SPEED;
            head[1] += Math.sin(angle) * SPEED;
        }else {
            // If the mouse has moved, update the angle to point towards it
            angle = Math.atan2(targetY - head[1], targetX - head[0]);
            // Always move forward
            head[0] += Math.cos(angle) * SPEED;
            head[1] += Math.sin(angle) * SPEED;
        }
        
        
        

        // Each segment follows the one in front of it
        for (int i = 1; i < body.size(); i++) {
            double[] front = body.get(i - 1);
            double[] seg = body.get(i);

            double dx = front[0] - seg[0];
            double dy = front[1] - seg[1];
            double dist = Math.hypot(dx, dy);
            
            if (dist > SEGMENT_GAP) {
                seg[0] = front[0] - dx / dist * SEGMENT_GAP;
                seg[1] = front[1] - dy / dist * SEGMENT_GAP;
            }
        }
    }

    public void draw(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(34, 139, 34)); 

        // Draw from tail to head so the head sits on top
        for (int i = body.size() - 1; i >= 0; i--) {
            double[] seg = body.get(i);
            int r;
            if (i == 0) {
                r = RADIUS;
            } else {
                r = RADIUS - 4;
}
            g.fillOval(
                (int) seg[0] - r,
                (int) seg[1] - r,
                r * 2,
                r * 2
            );
        }
    }
}