import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        showMenu(frame);
    }

    static void showMenu(JFrame frame) {
        JPanel menu = new JPanel(null);

        JButton button = new JButton(" Start snake.io");
        int widthButton = 250;
        int heightButton = 100;
        button.setBounds(
              (frame.getWidth() - widthButton) / 2,
              (frame.getHeight() - heightButton) / 2,
              widthButton,
              heightButton
        );
        button.setFont(new Font("SansSerif", Font.BOLD, 25));
        button.setBackground(Color.lightGray);
        button.setBorder(BorderFactory.createEtchedBorder());
        button.setFocusable(false);

        JTextField textField = new JTextField();
        int textWidth = 200;
        int textHeight = 40;
        textField.setBounds(
            (frame.getWidth() - textWidth) / 2,
            ((frame.getHeight() - textHeight) / 2) - 100,
            textWidth,
            textHeight
        );
        textField.setFont(new Font("SansSerif", Font.BOLD, 25));

        JLabel usernamelabel = new JLabel("Username");
        usernamelabel.setBounds(frame.getWidth() / 2 - 100, frame.getHeight() / 2 - 155, 250, 40);
        usernamelabel.setFont(new Font("SansSerif", Font.BOLD, 25));

        JLabel highScoreLabel = new JLabel("Highscore: " + GamePanel.loadHighScore());
        highScoreLabel.setBounds(frame.getWidth() / 2 - 100, frame.getHeight() / 2 + 80, 300, 40);
        highScoreLabel.setFont(new Font("SansSerif", Font.BOLD, 25));

        menu.add(usernamelabel);
        menu.add(textField);
        menu.add(button);
        menu.add(highScoreLabel);

        button.addActionListener(e -> {
            String username = textField.getText().trim();
            if (username.isEmpty()) {
                username = "Player";
            }
            startGame(frame, username);
        });

        frame.setContentPane(menu);
        frame.revalidate();
        frame.repaint();
    }

    static void startGame(JFrame frame, String username) {
        GamePanel game = new GamePanel(username, frame);
        frame.setContentPane(game);
        frame.revalidate();
        frame.repaint();
        game.StartGameThread();
    }
}