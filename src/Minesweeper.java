import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.plaf.basic.BasicBorders;

public class Minesweeper {

    int tileSize = 70;
    int numRows = 8;
    int numCols = 8;
    int boardWidth = numCols * tileSize;
    int boardHeight = numRows * tileSize;

    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabelCenter = new JLabel();
    //JLabel textLabelLeft = new JLabel();

    JPanel textPanelCenter = new JPanel();
    //JPanel textPanelLeft = new JPanel();
    JPanel boardPanel = new JPanel();

    int mineCount = 10;

    MineTile[][] board = new MineTile[numRows][numCols];
    ArrayList<MineTile> mineList;
    Random rand = new Random();

    int tilesClicked = 0;
    boolean gameOver = false;

    Minesweeper()
    {
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabelCenter.setFont(new Font("Roboto", Font.BOLD, 25));
        textLabelCenter.setHorizontalAlignment(JLabel.CENTER);
        textLabelCenter.setText("🙂");
        textLabelCenter.setOpaque(true);

        //textLabelLeft.setFont(new Font("Roboto", Font.BOLD, 25));
        //textLabelLeft.setHorizontalAlignment(JLabel.WEST);
        //textLabelLeft.setText(Integer.toString(mineCount));
        //textLabelLeft.setOpaque(true);

        textPanelCenter.setLayout(new BorderLayout());
        textPanelCenter.add(textLabelCenter);

        //textPanelLeft.setLayout(new BorderLayout());
        //textPanelLeft.add(textLabelLeft);

        frame.add(textPanelCenter, BorderLayout.NORTH);
        //frame.add(textPanelLeft, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        boardPanel.setBackground(Color.gray);
        frame.add(boardPanel);

        for (int r = 0; r < numRows; r++)
        {
            for (int c = 0; c < numCols; c++)
            {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));

                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                //tile.setText("💣");
                tile.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mousePressed(MouseEvent e)
                    {
                        if (gameOver)
                        {
                            return;
                        }

                        MineTile tile = (MineTile) e.getSource();
                        
                        // left click
                        if (e.getButton() == MouseEvent.BUTTON1)
                        {
                            if (tile.getText() == "")
                            {
                                if (mineList.contains(tile))
                                {
                                    revealMines();
                                }
                                else
                                {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        }
                        // right click
                        else if (e.getButton() == MouseEvent.BUTTON3)
                        {
                            if (tile.getText() == "" && tile.isEnabled())
                            {
                                tile.setText("🚩");
                            }
                            else if (tile.getText() == "🚩")
                            {
                                tile.setText("");
                            }
                        }
                    }
                });

                boardPanel.add(tile);
            }
        }

        frame.setVisible(true);

        setMines();
    }

    private class MineTile extends JButton
    {
        int r, c;

        public MineTile(int r, int c)
        {
            this.r = r;
            this.c = c;
        }
    }

    public void setMines()
    {
        mineList = new ArrayList<MineTile>();
        int minesLeft = mineCount;

        while (minesLeft > 0)
        {
            int r = rand.nextInt(numRows);
            int c = rand.nextInt(numCols);

            MineTile tile = board[r][c];

            if (!mineList.contains(tile))
            {
                mineList.add(tile);
                minesLeft -= 1;
            }
        }
    }

    public void revealMines()
    {
        for (int i = 0; i < mineList.size(); i++)
        {
            MineTile tile = mineList.get(i);
            tile.setText("💣");
        }

        gameOver = true;
        textLabelCenter.setText("😵");
    }

    public void checkMine(int r, int c)
    {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols)
        {
            return;
        }

        MineTile tile = board[r][c];

        if (!tile.isEnabled())
        {
            return;
        }
        tile.setEnabled(false);
        tilesClicked += 1;
        int minesFound = 0;

        // top left
        minesFound += countMines(r-1, c-1);
        // top center
        minesFound += countMines(r-1, c);
        // top right
        minesFound += countMines(r-1, c+1);

        // left
        minesFound += countMines(r, c-1);
        // right
        minesFound += countMines(r, c+1);

        // bottom left
        minesFound += countMines(r+1, c-1);
        // bottom center
        minesFound += countMines(r+1, c);
        // bottom right
        minesFound += countMines(r+1, c+1);

        if (minesFound > 0)
        {
            tile.setText(Integer.toString(minesFound));
        }
        else
        {
            tile.setText("");

            // top left
            checkMine(r-1, c-1);
            // top center
            checkMine(r-1, c);
            // top right
            checkMine(r-1, c+1);

            // left
            checkMine(r, c-1);
            // right
            checkMine(r, c+1);

            // bottom left
            checkMine(r+1, c-1);
            // bottom center
            checkMine(r+1, c);
            // bottom right
            checkMine(r+1, c+1);
        }

        if (tilesClicked == numRows * numCols - mineList.size())
        {
            gameOver = true;
            textLabelCenter.setText("😎");
            // set all mines to flags
            setFlags();
        }
    }

    public int countMines(int r, int c)
    {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols)
        {
            return 0;
        }
        if (mineList.contains(board[r][c]))
        {
            return 1;
        }
        return 0;
    }

    public void setFlags()
    {
        for (int i = 0; i < mineList.size(); i++)
        {
            MineTile tile = mineList.get(i);
            tile.setText("🚩");
        }
    }
}