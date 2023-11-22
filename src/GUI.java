import javax.swing.*;
import java.util.*;
import java.awt.*;

public class GUI extends JFrame{
    
    public GUI()
    {
        this.setTitle("Minesweeper");
        this.setSize(1286, 829);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(true);
    }
}