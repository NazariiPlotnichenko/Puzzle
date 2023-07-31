package src;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Mapper {

    int[][] fromVisualState(ArrayList<JLabel> but){
        int a[][] = new int[4][4];
        for (int i = 0; i < but.size(); i++) {
            Point point = (Point) but.get(i).getClientProperty("position");
            if (point.x == 3 && point.y == 3) {
                a[i / 4][i % 4] = 0;
            } else {
                a[i / 4][i % 4] = (point.x * 4 + point.y) + 1;
            }
        }
        return a;
    }

    ArrayList<JLabel> toVisualState(int[][] a, JLabel lastButton, ArrayList<JLabel> solutionArr){
        ArrayList<JLabel> t = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int w = a[i][j];
                if (w == 0) {
                    t.add(lastButton);
                } else {
                    t.add(solutionArr.get(w - 1));
                }
            }
        }
        return t;
    }
}