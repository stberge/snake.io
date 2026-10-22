import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;


// Main class
class Main {

    // Main driver method
    public static void main(String[] args)
    {
        // Setup frame
        JFrame frame = new JFrame();
        //frame.setSize(1280, 720);     changed to full screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // Creates button
        JButton button = new JButton(" Start snake.io");
        int widthButton = 250;
        int heightButton= 100;
        //button.setBounds((1280 - widthButton) / 2, (720 - heightButton) / 2, widthButton, heightButton);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        button.setBounds(
            (screen.width - widthButton) / 2,
            (screen.height - heightButton) / 2,
            widthButton,
            heightButton
        );
        button.setFont(new Font("SansSerif", Font.BOLD, 25));
        button.setBackground(Color.lightGray);
        button.setBorder(BorderFactory.createEtchedBorder());
        

        frame.add(button);

        // create username
        JTextField textField =  new JTextField();
        int textWidth = 200;
        int textHeight = 40;

        textField.setBounds(
            (frame.getWidth() - textWidth) / 2,
            ((frame.getHeight() - textHeight) / 2) -100,
            textWidth,
            textHeight
        );
        textField.setFont(new Font("SansSerif", Font.BOLD, 25));

        JLabel usernamelabel = new JLabel("Username");
        usernamelabel.setBounds(frame.getWidth()/2 - 100 , frame.getHeight()/2 - 155 ,250,40 );
        usernamelabel.setFont(new Font("SansSerif",Font.BOLD,25));
        frame.add(usernamelabel);

        frame.add(textField);
        frame.revalidate();
        frame.repaint();



        // handlles button and username input
        button.addActionListener(e -> {
            String username = textField.getText().trim();
            if(username.isEmpty()) {
                username = "Player";
            }


            GamePanel game = new GamePanel(username);
            frame.setContentPane(game);
            frame.revalidate();
            frame.repaint();
            game.StartGameThread();
        });

        button.setFocusable(false);


        

    
        
    }
}    