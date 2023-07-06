package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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
    //private JFrame frame;

//    private ArrayList<Image> normalArray;
//    private ArrayList<Image> shuffledArray;

    public Game() {
        initUI();
    }

    public void initUI() {
        buttons = new ArrayList<>();

        panel = new JPanel();
        //panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(4, 4, 1, 1));

        try {
            source = loadImage();
            int h = getNewHeight(source.getWidth(), source.getHeight());
            resized = resizeImage(source, DESIRED_WIDTH, h, BufferedImage.TYPE_INT_ARGB);

        } catch (IOException e) {
            System.err.println("Problems with the source image" + e);
        }

        width = resized.getWidth();
        height = resized.getHeight();

        cropAndAddImages();

        Collections.shuffle(buttons);

//        System.out.println("Similar arrays: " + (shuffledArray.equals(normalArray)));
//        Collections.shuffle(shuffledArray);
//
//        image = new BufferedImage(width / 4, height / 4, BufferedImage.TYPE_INT_ARGB);
//        shuffledArray.add((Image) image);
//        normalArray.add((Image) image);

//        System.out.println("Shuffeled array + normal: " + (shuffledArray.equals(normalArray)));

        buttons.add(lastButton);

        panel.setPreferredSize(new Dimension(buttons.get(0).getIcon().getIconWidth() * 4, buttons.get(0).getIcon().getIconHeight() * 4));

        for (int i = 0; i < 16; i++) {
            panel.add(buttons.get(i));
        }

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Mouse pressed");
//                int mouseX;
//                int mouseY;
//
//                int lastButtonX;
//                int lastButtonY;
//
//                int checkX;
//                int checkY;
//
//                int puzzlePositionInArray;
//                int lastButtonPositionInArray;
//
//                PuzzleButton tmp;
//
//                mouseX = e.getX() % 4;
//                mouseY = e.getY() % 4;
//
//                lastButtonX = lastButton.getX() % 4;
//                lastButtonY = lastButton.getY() % 4;
//
//                checkX = Math.abs(mouseX - lastButtonX);
//                checkY = Math.abs(mouseY - lastButtonY);
//
//                if ((checkX == 1 && checkY == 0) || (checkX == 0 && checkY == 1)) {
//
//                    puzzlePositionInArray = mouseX * 4 + mouseY;
//                    lastButtonPositionInArray = lastButtonX * 4 + lastButtonY;
//                    tmp = buttons.get(puzzlePositionInArray);
//
//                    buttons.set(puzzlePositionInArray, buttons.get(lastButtonPositionInArray));
//                    buttons.set(lastButtonPositionInArray, tmp);
//
//                    repaint();
//                }
            }
        });

        add(panel, BorderLayout.CENTER);
    }

    private void cropAndAddImages() {
//        normalArray = new ArrayList<>();
//        shuffledArray = new ArrayList<>();
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
//                    normalArray.add(image);
//                    shuffledArray.add(image);
                }
            }
        }
    }

    private void checkSolution() {
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

//        System.out.println("Check solution:");
//        for (int i = 0; i < solution.size(); i++) {
//            System.out.println(i + ": " + (solution.get(i)).equals(buttons.get(i).getClientProperty("position")));
//        }
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
        game.setResizable(false);
        game.pack();
        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }

}
