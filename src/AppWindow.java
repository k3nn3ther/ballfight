import javax.swing.*;
import java.awt.*;

public class AppWindow extends JFrame {

    // used youtube tutorial to set up window/learn Java Swing

    public AppWindow(){
        makeWindow();
    }

    public void makeWindow(){
        setTitle("Weapon Ball Tournament");
        setSize(new Dimension(480,800));
        setLayout(new BorderLayout());
        UI ui = new UI();
        add(ui, BorderLayout.CENTER);
        setResizable(false);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}