import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper extends JFrame {
    private int tileSize = 70;
    private int numRows;
    private int numCols;
    private int boardWidth;
    private int boardHeight;

    private JFrame frame = new JFrame("Minesweeper");
    private JLabel centralCtrlDisplay = new JLabel();
    private JLabel mineCountDisplay = new JLabel();
    private JPanel textPanelCenter = new JPanel();
    private JPanel boardPanel = new JPanel();

    private int mineCount;
    private MineTile[][] board;
    private ArrayList<MineTile> mineList;
    private Random rand = new Random();

    private int tilesClicked = 0;
    private boolean gameOver = false;

    // Timer
    private Timer timer;
    private int secondsElapsed;
    private JLabel timeLabelDisplay = new JLabel("0");

    public Minesweeper(int rows, int cols, int mines)
    {
        this.numRows = rows;
        this.numCols = cols;
        this.mineCount = mines;

        boardWidth = numCols * tileSize;
        boardHeight = numRows * tileSize;

        initializeFrame();
        initializeBoard();
        setMines();
    }

    private void initializeFrame()
    {
        // Create window frame
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.DARK_GRAY);
        frame.setLayout(new BorderLayout());

        // Display panel - center emoji
        centralCtrlDisplay.setFont(new Font("Roboto", Font.BOLD, 25));
        centralCtrlDisplay.setHorizontalAlignment(JLabel.CENTER);
        centralCtrlDisplay.setText("🙂");
        centralCtrlDisplay.setOpaque(true);

        // Mouse listener to reset game
        centralCtrlDisplay.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON1)
                {
                    resetGame();
                }
            }    
        });

        // Display panel - mine count
        mineCountDisplay.setFont(new Font("Roboto", Font.BOLD, 25));
        mineCountDisplay.setHorizontalAlignment(SwingConstants.LEFT);
        mineCountDisplay.setText(Integer.toString(mineCount));
        mineCountDisplay.setOpaque(true);

        // Display panel - timer
        timeLabelDisplay.setFont(new Font("Roboto", Font.BOLD, 25));
        timeLabelDisplay.setHorizontalAlignment(SwingConstants.RIGHT);
        timeLabelDisplay.setOpaque(true);

        // Add display panels to text panel
        textPanelCenter.setLayout(new BorderLayout());
        textPanelCenter.add(centralCtrlDisplay, BorderLayout.CENTER);
        textPanelCenter.add(mineCountDisplay, BorderLayout.WEST);
        textPanelCenter.add(timeLabelDisplay, BorderLayout.EAST);

        frame.add(textPanelCenter, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        boardPanel.setBackground(Color.gray);
        frame.add(boardPanel);

        // timer
        timer = new Timer(1000, new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                secondsElapsed++;
                timeLabelDisplay.setText("" + secondsElapsed);
            }
        });

        frame.setVisible(true);
    }

    private void initializeBoard()
    {
        board = new MineTile[numRows][numCols];

        for (int r = 0; r < numRows; r++)
        {
            for (int c = 0; c < numCols; c++)
            {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));

                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
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
                            if (tilesClicked == 0)
                            {
                                timer.start();
                            }
                            if (tile.getText().isEmpty())
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
                            if (tilesClicked == 0)
                            {
                                timer.start();
                            }
                            if (tile.getText().isEmpty() && tile.isEnabled())
                            {
                                tile.setText("🚩");
                            }
                            else if (tile.getText().equals("🚩"))
                            {
                                tile.setText("");
                            }

                            mineCountDisplay.setText(Integer.toString(mineCount));
                        }
                    }
                });

                boardPanel.add(tile);
            }
        }
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
        centralCtrlDisplay.setText("😵");
        timer.stop();
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
            // Code to change mine indicator color
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
            centralCtrlDisplay.setText("😎");
            // set all mines to flags
            setFlags();
            timer.stop();
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

    public void resetGame()
    {
        frame.dispose();

        // TEST
        //new Minesweeper();
        new Minesweeper(numRows, numCols, mineCount);
    }
}