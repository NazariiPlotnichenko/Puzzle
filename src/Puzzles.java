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
import java.util.Collections;

public class Puzzles extends JFrame {
    private JPanel panel;
    private BufferedImage source;
    private BufferedImage resized;
    private int width, height;
    private final int DESIRED_WIDTH = 400;
    private Image image;
    private JButton btn;
    private JFrame frame;
    private JPanel panelBtn;
    private ArrayList<JLabel> buttons;
    private ArrayList<JLabel> solutionArr;

    private JLabel a = null;
    private JLabel b = null;

    private MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (a == null) {
                a = (JLabel) e.getSource();
                a.setBorder(BorderFactory.createLineBorder(Color.red));
            } else {
                b = (JLabel) e.getSource();
                a.setBorder(null);
                int indexa = buttons.indexOf(a);
                int indexb = buttons.indexOf(b);
                buttons.set(indexa, b);
                buttons.set(indexb, a);
                a = null;
                repaintGrid();
                drawMessage();
            }
        }
    };

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
            }
        });

        frame.add(panel);
        frame.add(panelBtn);

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

    private void cropAndAddImages() {
        int p = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                image = createImage(new FilteredImageSource(resized.getSource(),
                        new CropImageFilter(j * width / 4, i * height / 4, width / 4, height / 4)));
                buttons.add(new JLabel());
                buttons.get(p).setIcon(new ImageIcon(image));
                buttons.get(p).putClientProperty("position", new Point(i, j));
                buttons.get(p).addMouseListener(mouseAdapter);
                solutionArr.add(buttons.get(p));
                p++;
            }
        }
        Collections.shuffle(buttons);
    }

    private void drawMessage() {
        if (checkSolution()) {
            JTextArea text = new JTextArea("The game was successfully solved. Congratulations!!!");
            text.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            panelBtn.add(text);
            repaintGrid();
        }
    }

    private BufferedImage toBufferedImage(int i) {
        ImageIcon imageIcon = (ImageIcon) buttons.get(i).getIcon();
        Image img = imageIcon.getImage();
        BufferedImage bImage = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        bImage.getGraphics().drawImage(img, 0, 0, null);
        return bImage;
    }

    String str = "";

    private void sortButton() {
        ArrayList<JLabel> sortedArray = new ArrayList<>();
        JLabel label;
        BufferedImage bImage;
        BufferedImage bImage1;
        int p = 0;
        for (int i = 0; i < buttons.size(); i++) {
            bImage = toBufferedImage(i);
            for (int j = 0; j < buttons.size(); j++) {
                if (i == j) {
                    continue;
                }
                bImage1 = toBufferedImage(j);
                areNeighbors(bImage, bImage1);
            }
            if (str.equals("32") || str.equals("23")) {
                sortedArray.add(new JLabel());
                sortedArray.get(p).setIcon((new ImageIcon(bImage)));
                p++;
                break;
            }
            str = "";
        }
        str = "";
        for (int i = 0; i < buttons.size(); i++) {
            if (i == 4 || i == 8 || i == 12) {
                bImage = toBufferedImage(i - 4);
                for (int j = 0; j < buttons.size(); j++) {
                    str = "";
                    if (i == j) {
                        continue;
                    }
                    bImage1 = toBufferedImage(j);
                    areNeighbors(bImage, bImage1);
                    if (str.equals("3")) {
                        sortedArray.add(new JLabel());
                        sortedArray.get(p).setIcon(new ImageIcon(bImage1));
                        p++;
                        break;
                    }
                }
            } else {
                bImage = toBufferedImage(i);
                for (int j = 0; j < buttons.size(); j++) {
                    str = "";
                    if (i == j) {
                        continue;
                    }
                    bImage1 = toBufferedImage(j);
                    areNeighbors(bImage, bImage1);
                    if (str.equals("2")) {
                        sortedArray.add(new JLabel());
                        sortedArray.get(p).setIcon(new ImageIcon(bImage1));
                        p++;
                        break;
                    }
                }
            }
        }

        buttons = sortedArray;
        repaintGrid();
    }

    private boolean areNeighbors(BufferedImage piece1, BufferedImage piece2) {
        // Порівнюємо пікселі на межах пазлів
        int height = piece1.getHeight();
        int width = piece1.getWidth();
        int result = 0;

        // Порівнюємо верхню межу пазла piece1 з нижньою межею пазла piece2
        for (int x = 0; x < width; x++) {
            int rgb1 = piece1.getRGB(x, 0);
            int rgb2 = piece2.getRGB(x, height - 1);
            if (areColorsSimilar(rgb1, rgb2)) {
                result++;
            }
        }
        if (result >= (width * 0.7)) {
            str += "1";
            return true;
        }
        result = 0;
        // Порівнюємо нижню межу пазла piece1 з верхньою межею пазла piece2
        for (int x = 0; x < width; x++) {
            int rgb1 = piece1.getRGB(x, height - 1);
            int rgb2 = piece2.getRGB(x, 0);
            if (areColorsSimilar(rgb1, rgb2)) {
                result++;
            }
        }
        if (result >= (width * 0.7)) {
            str += "3";
            return true;
        }
        result = 0;
        // Порівнюємо праву межу пазла piece1 з лівою межею пазла piece2
        for (int y = 0; y < height; y++) {
            int rgb1 = piece1.getRGB(width - 1, y);
            int rgb2 = piece2.getRGB(0, y);
            if (areColorsSimilar(rgb1, rgb2)) {
                result++;
            }
        }
        if (result >= (height * 0.7)) {
            str += "2";
            return true;
        }
        result = 0;
        // Порівнюємо ліву межу пазла piece1 з правою межею пазла piece2
        for (int y = 0; y < height; y++) {
            int rgb1 = piece1.getRGB(0, y);
            int rgb2 = piece2.getRGB(width - 1, y);
            if (areColorsSimilar(rgb1, rgb2)) {
                result++;
            }
        }
        if (result >= (height * 0.7)) {
            str += "4";
            return true;
        } else
            return false;
    }

    private boolean areColorsSimilar(int rgb1, int rgb2) {
        int threshold = 35;

        int redDiff = Math.abs((rgb1 >> 16) & 0xFF - (rgb2 >> 16) & 0xFF);
        int greenDiff = Math.abs((rgb1 >> 8) & 0xFF - (rgb2 >> 8) & 0xFF);
        int blueDiff = Math.abs(rgb1 & 0xFF - rgb2 & 0xFF);
        ;
        return redDiff <= threshold && greenDiff <= threshold && blueDiff <= threshold;
    }

    private boolean checkSolution() {
        int res = 0;
        boolean solution = false;

        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).getClientProperty("position").equals(solutionArr.get(i).getClientProperty("position"))) {
                res++;
            }
            if (res == 16) {
                solution = true;
            }
        }
        return solution;
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
