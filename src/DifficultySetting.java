import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DifficultySetting {

    JFrame difficultyFrame = new JFrame("Select Difficulty");
    JButton easyButton = new JButton("Easy");
    JButton intermediateButton = new JButton("Intermediate");
    JButton expertButton = new JButton("Expert");

    DifficultySetting()
    {
        difficultyFrame.setSize(300, 200);
        difficultyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        difficultyFrame.setLayout(new GridLayout(3, 1));

        easyButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                startMinesweeper(10, 10, 10);
            }
        });

        intermediateButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                startMinesweeper(16, 16, 40);
            }
        });

        expertButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                startMinesweeper(16, 30, 99);
            }
        });

        difficultyFrame.add(easyButton);
        difficultyFrame.add(intermediateButton);
        difficultyFrame.add(expertButton);

        difficultyFrame.setVisible(true);
    }

    private void startMinesweeper(int rows, int cols, int mines)
    {
        difficultyFrame.dispose(); // Close the difficulty selection window

        // Start Minesweeper with chosen difficulty
        Minesweeper game = new Minesweeper(rows, cols, mines);
    }

    public static void main(String[] args)
    {
        new DifficultySetting();
    }
}