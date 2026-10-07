import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;


// Main class
class GFG {

    // Main driver method
    public static void main(String[] args)
    {
        // Setup frame
        JFrame frame = new JFrame();
        frame.setSize(1280, 720);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // Creates button
        JButton button = new JButton(" Start snake.io");
        int widthButton = 250;
        int heightButton= 100;
        button.setBounds((1280 - widthButton) / 2, (720 - heightButton) / 2, widthButton, heightButton);
        button.setFont(new Font("SansSerif", Font.BOLD, 25));
        button.setBackground(Color.lightGray);
        button.setBorder(BorderFactory.createEtchedBorder());



        frame.add(button);
        button.addActionListener(e -> {
            GamePanel game = new GamePanel();
            frame.setContentPane(game);
            frame.revalidate();
            frame.repaint();
            game.StartGameThread();
        });

        button.setFocusable(false);

    
        
    }
}