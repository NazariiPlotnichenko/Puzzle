package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Game extends JFrame {
    private ArrayList<Point> solution;
    private ArrayList<PuzzleButton> buttons;
    private JPanel panel;
    private BufferedImage source;
    private BufferedImage resized;
    private int width, height;
    private final int DESIRED_WIDTH = 400;
    private Image image;
    private PuzzleButton lastButton;

    private JLabel label;

    public Game() {
        initUI();
    }

    public void initUI() {
        solution = new ArrayList<>();
        solution.add(new Point(0, 0));
        solution.add(new Point(0, 1));
        solution.add(new Point(0, 2));
        solution.add(new Point(0, 3));
        solution.add(new Point(1, 0));
        solution.add(new Point(1, 1));
        solution.add(new Point(1, 2));
        solution.add(new Point(1, 3));
        solution.add(new Point(2, 0));
        solution.add(new Point(2, 1));
        solution.add(new Point(2, 2));
        solution.add(new Point(2, 3));
        solution.add(new Point(3, 0));
        solution.add(new Point(3, 1));
        solution.add(new Point(3, 2));

//        System.out.println("Solution:");
//        for (Point a : solution
//        ) {
//            System.out.println(a.toString());
//        }

        buttons = new ArrayList<>();

        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(4, 4));


        try {

            source = loadImage();
            int h = getNewHeight(source.getWidth(), source.getHeight());
            resized = resizeImage(source, DESIRED_WIDTH, h, BufferedImage.TYPE_INT_ARGB);

        } catch (IOException e) {
            System.err.println("Problems with source image" + e);
        }

        width = resized.getWidth();
        height = resized.getHeight();

        add(panel, BorderLayout.CENTER);

        //System.out.println("Buttons:");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                image = createImage(new FilteredImageSource(resized.getSource(),
                        new CropImageFilter(j * width / 4, i * height / 4, width / 4, height / 4)));

                PuzzleButton button = new PuzzleButton(image);
                button.putClientProperty("position", new Point(i, j));

                if (j == 3 && i == 3) {
                    lastButton = new PuzzleButton();
                    lastButton.setBorderPainted(false);
                    lastButton.setContentAreaFilled(false);
                    lastButton.setLastButton(true);
                } else {
                    buttons.add(button);
                    //System.out.println(buttons.get(buttons.size() - 1).getClientProperty("position"));
                }
            }

        }

        for (int i = 0; i < 16; i++) {
            if (i == 15) {
                panel.add(lastButton);
            } else
                panel.add(buttons.get(i));
        }



        panel.setPreferredSize(new Dimension(buttons.get(0).getIcon().getIconWidth()*4, buttons.get(0).getIcon().getIconHeight()*4));
//        panel.setPreferredSize(buttons.get(0).getWidth()*4);
//        System.out.println(lastButton);
//        System.out.println(buttons.get(0).getIcon());
//        System.out.println(buttons.get(1).getIcon());
//        System.out.println(buttons.get(2).getIcon());
//        System.out.println(buttons.get(3).getIcon());
//        checkSolution();
    }

    //private void

    private void checkSolution() {
        System.out.println("Check solution:");
        for (int i = 0; i < solution.size(); i++) {
            System.out.println(i + ": " + (solution.get(i)).equals(buttons.get(i).getClientProperty("position")));
        }
    }

    private BufferedImage resizeImage(BufferedImage originImage, int width, int height, int type) {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private BufferedImage loadImage() throws IOException {
        BufferedImage image = ImageIO.read(new File("image.jpg"));
        return image;
    }

    private int getNewHeight(int w, int h) {
        double ratio = DESIRED_WIDTH / (double) w;
        int newHeight = (int) (h * ratio);
        return newHeight;
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setTitle("Puzzle game");
        game.getContentPane().add(game.panel);
        game.pack();
        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }

}
