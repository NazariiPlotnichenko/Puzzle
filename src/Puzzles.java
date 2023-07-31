package src;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Puzzles extends JFrame {
    private JPanel panel;
    private BufferedImage source;
    private BufferedImage resized;
    private int width, height;
    private final int DESIRED_WIDTH = 400;
    private Image image;
    private JLabel lastButton;
    private JButton btn;
    private JFrame frame;
    private JPanel panelBtn;
    private ArrayList<JLabel> buttons;
    private ArrayList<JLabel> solutionArr;
    private Mapper mapper = new Mapper();

    public Puzzles() {
        initUI();
    }

    public void initUI() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Puzzle game");
        frame.setResizable(false);
        frame.setLayout(new GridLayout(0, 2));

        btn = new JButton();
        panelBtn = new JPanel();

        buttons = new ArrayList<>();
        solutionArr = new ArrayList<>();

        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(4, 4, 2, 2));

        btn.setSize(50, 50);
        btn.setText("Press to sort puzzles");

        try {
            source = loadImage();
            int h = getNewHeight(source.getWidth(), source.getHeight());
            resized = resizeImage(source, DESIRED_WIDTH, h, BufferedImage.TYPE_INT_ARGB);
        } catch (IOException e) {
            System.err.println("Problems with the source image" + e);
        }

        width = resized.getWidth();
        height = resized.getHeight();
        panel.setPreferredSize(new Dimension(width, height));

        cropAndAddImages();

        for (int i = 0; i < buttons.size(); i++) {
            panel.add(buttons.get(i));
        }

        panelBtn.add(btn);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortButton();
                checkSolution();
            }
        });

        frame.add(panel);
        frame.add(panelBtn);

        frame.addMouseListener(new MouseAdapter() {
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
                checkSolution();
                repaintGrid();
            }
        });
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void repaintGrid() {
        frame.remove(panel);
        frame.remove(panelBtn);
        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(4, 4, 2, 2));
        for (int i = 0; i < 16; i++) {
            panel.add(buttons.get(i));
        }
        frame.add(panel);
        frame.add(panelBtn);
        frame.repaint();
        frame.setVisible(true);
    }

    FifteenPuzzle a;

    private void cropAndAddImages() {
        int p = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                image = createImage(new FilteredImageSource(resized.getSource(),
                        new CropImageFilter(j * width / 4, i * height / 4, width / 4, height / 4)));
                if (j == 3 && i == 3) {
                    image = new BufferedImage(width / 4, height / 4, BufferedImage.TYPE_INT_ARGB);
                    lastButton = new JLabel();
                    lastButton.setIcon(new ImageIcon(image));
                    lastButton.putClientProperty("position", new Point(i, j));
                    solutionArr.add(new JLabel());
                    solutionArr.get(p).setIcon(new ImageIcon(image));
                    solutionArr.get(p).putClientProperty("position", new Point(i, j));
                } else {
                    buttons.add(new JLabel());
                    buttons.get(p).setIcon(new ImageIcon(image));
                    buttons.get(p).putClientProperty("position", new Point(i, j));
                    solutionArr.add(buttons.get(p));
                }
                p++;
            }
        }

        a = new FifteenPuzzle();
        a.shuffle(70);

        buttons = mapper.toVisualState(a.tiles, lastButton, solutionArr);
        repaintGrid();
    }

    private void drawMessage() {
        JTextArea text = new JTextArea("The game was successfully solved. Congratulations!!!");
        text.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelBtn.add(text);
    }

    int i = 0;
    Timer timer;
    int count = 0;

    private void sortButton() {
        FifteenPuzzle resolver = new FifteenPuzzle(mapper.fromVisualState(buttons));
        List<FifteenPuzzle> solutions;
        solutions = resolver.aStarSolve();
        FifteenPuzzle.showSolution(solutions);

        ActionListener actionListener = actionEvent -> {
            buttons = mapper.toVisualState(solutions.get(count).tiles, lastButton, solutionArr);
            checkSolution();
            repaintGrid();
            count++;
            if (count == solutions.size()) {
                timer.stop();
            }
        };

        timer = new Timer(500, actionListener);
        timer.start();
    }

    private void checkSolution() {
        int res = 0;
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).getClientProperty("position").equals(solutionArr.get(i).getClientProperty("position"))) {
                res++;
            }
            if (res == 15) {
                drawMessage();
            }
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
        Puzzles puzzles = new Puzzles();
    }

}
