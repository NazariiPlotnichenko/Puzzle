package test;

import javax.swing.*;
import java.awt.*;

public class Test {
    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        JPanel panel = new JPanel(new GridLayout());
        panel.setBounds(0,0, 400, 400);
        //panel.setSize(400, 400);
        jFrame.setBounds(0,0, 400, 400);
        jFrame.add(panel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        jFrame.pack();
        jFrame.setVisible(true);
    }
}
