import java.awt.*;


public class Food {
    int radius = 11;
    double x;
    double y;
    Color color;

    public Food(double x, double y, int radius){
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = Color.getHSBColor((float) Math.random(), 0.9f, 0.9f);
    } 
    
    public void draw(Graphics2D g,double cameraX,double cameraY){
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.fillOval((int)(x - cameraX) - radius, (int) (y- cameraY) - radius, radius, radius);

    }

    public boolean isTouching(double otherX, double otherY, int otherRadius){
        double dx = otherX - this.x;
        double dy = otherY - this.y;
        double distance = Math.hypot(dx, dy);
        return distance < (radius + otherRadius);
    }



}
