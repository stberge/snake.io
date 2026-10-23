import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class Leaderboard extends JPanel {

    public interface Listener {
        void onScoresChanged(List<Map.Entry<String, Integer>> ranking);
    }

    private final Map<String, Integer> scores = new HashMap<>();
    private final List<Listener> listeners = new ArrayList<>();
    private List<Map.Entry<String, Integer>> top = new ArrayList<>();

    public Leaderboard() {
        setOpaque(false);
        setPreferredSize(new Dimension(200, 150));
        addListener(this::redraw);
    }

    public void addListener(Listener l) {
        listeners.add(l);
    }

    public synchronized void setScore(String name, int value) {
        scores.put(name, value);

        List<Map.Entry<String, Integer>> ranking = new ArrayList<>();
        for (Map.Entry<String, Integer> e : scores.entrySet()) {
            ranking.add(new AbstractMap.SimpleEntry<>(e));
        }
        ranking.sort((a, b) -> b.getValue() - a.getValue());

        for (Listener l : listeners) {
            l.onScoresChanged(ranking);
        }
    }

    private void redraw(List<Map.Entry<String, Integer>> ranking) {
        SwingUtilities.invokeLater(() -> {
            top = ranking.subList(0, Math.min(5, ranking.size()));
            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString("Leaderboard", 12, 22);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        int y = 44;
        int rank = 1;
        for (Map.Entry<String, Integer> e : top) {
            g2.drawString(rank + ". " + e.getKey(), 12, y);
            String score = String.valueOf(e.getValue());
            g2.drawString(score, getWidth() - 12 - g2.getFontMetrics().stringWidth(score), y);
            y += 20;
            rank++;
        }
    }
}