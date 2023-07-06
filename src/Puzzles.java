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

public class Puzzles extends JFrame {
    private JPanel panel;
    private BufferedImage source;
    private BufferedImage resized;
    private int width, height;
    private final int DESIRED_WIDTH = 400;
    private Image image;
    private JLabel lastButton;

    private ArrayList<JLabel> buttons;
    private ArrayList<Image> startArray;
    private ArrayList<Image> shuffledArray;

    public Puzzles() {
        initUI();
    }

    public void initUI() {
        buttons = new ArrayList<>();
        startArray = new ArrayList<>();
        shuffledArray = new ArrayList<>();

        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(4, 4, 2, 2));

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

        //Collections.shuffle(buttons);

        //System.out.println("Similar arrays: " + (shuffledArray.equals(startArray)));
//        Collections.shuffle(shuffledArray);
//
//        image = new BufferedImage(width / 4, height / 4, BufferedImage.TYPE_INT_ARGB);
//        shuffledArray.add((Image) image);
//        buttons.add(new JLabel());
//        buttons.get(15).setIcon(new ImageIcon(image));
        shuffleAndSetArray();

        //System.out.println("array + normal: " + (shuffledArray.equals(startArray)));

        //buttons.add(lastButton);

        panel.setPreferredSize(new Dimension(width, height));

        for (int i = 0; i < 16; i++) {
            panel.add(buttons.get(i));
        }

        add(panel, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mouseX;
                int mouseY;

                int lastButtonX;
                int lastButtonY;

                int checkX;
                int checkY;

                int puzzlePositionInArray;
                int lastButtonPositionInArray;

                JLabel tmp;

                mouseX = e.getX() / buttons.get(0).getWidth();
                mouseY = e.getY() / buttons.get(0).getHeight();

                lastButtonX = lastButton.getX() / buttons.get(0).getWidth();
                lastButtonY = lastButton.getY() / buttons.get(0).getHeight();

                checkX = Math.abs(mouseX - lastButtonX);
                checkY = Math.abs(mouseY - lastButtonY);

                if ((checkX == 1 && checkY == 0) || (checkX == 0 && checkY == 1)) {

                    puzzlePositionInArray = mouseY * 4 + mouseX;
                    lastButtonPositionInArray = lastButtonY * 4 + lastButtonX;
                    tmp = buttons.get(puzzlePositionInArray);

                    buttons.set(puzzlePositionInArray, buttons.get(lastButtonPositionInArray));
                    buttons.set(lastButtonPositionInArray, tmp);
                }
                repaintGrid();
            }
        });
        
    }

    private void repaintGrid() {
        remove(panel);
        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(4, 4, 2, 2));
        for (int i = 0; i < 16; i++) {
            panel.add(buttons.get(i));
        }
        add(panel);
        repaint();
        setVisible(true);
    }

    private void shuffleAndSetArray() {
        Collections.shuffle(shuffledArray);
        for (int i = 0; i < 16; i++) {
            if (i == 15) {
                image = new BufferedImage(width / 4, height / 4, BufferedImage.TYPE_INT_ARGB);
                lastButton = new JLabel();
                buttons.add(lastButton);
                buttons.get(i).setIcon(new ImageIcon(image));
            } else {
                buttons.add(new JLabel());
                buttons.get(i).setIcon(new ImageIcon(shuffledArray.get(i)));
                //buttons.get(i).putClientProperty("position", );
            }
        }
    }

    private void cropAndAddImages() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                image = createImage(new FilteredImageSource(resized.getSource(),
                        new CropImageFilter(j * width / 4, i * height / 4, width / 4, height / 4)));
//                PuzzleButton button = new PuzzleButton(image);
//                button.putClientProperty("position", new Point(i, j));
                if (j == 3 && i == 3) {
                    return;
                } else {
                    startArray.add(image);
                    shuffledArray.add(image);
                }
            }
        }
    }

//    private void checkSolution() {
//        for (:
//             ) {
//
//        }
//    }

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
        Puzzles puzzles = new Puzzles();
        puzzles.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        puzzles.setTitle("Puzzle game");
        puzzles.setResizable(false);
        puzzles.pack();
        puzzles.setLocationRelativeTo(null);
        puzzles.setVisible(true);
    }

}
