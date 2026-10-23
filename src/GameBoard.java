import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GameBoard extends JPanel implements ActionListener, KeyListener {
    private int width, height, cellSize;
    private int[][] board;
    private int currentX, currentY;
    private int score;
    private boolean isGameOver;
    private Timer timer;
    private Random random = new Random();

    private JLabel scoreLabel;
    private JPanel nextPiecePanel;

    private int[][] currentPiece;
    private int currentColor;
    private int[][] nextPiece;
    private int nextColor;

    private static final int[][][] SHAPES = {
            {{0,0,0,0},{1,1,1,1},{0,0,0,0},{0,0,0,0}},
            {{0,0,0,0},{0,1,1,0},{0,1,1,0},{0,0,0,0}},
            {{0,0,0,0},{0,1,0,0},{1,1,1,0},{0,0,0,0}},
            {{0,0,0,0},{0,1,1,0},{1,1,0,0},{0,0,0,0}},
            {{0,0,0,0},{1,1,0,0},{0,1,1,0},{0,0,0,0}},
            {{0,0,0,0},{1,0,0,0},{1,1,1,0},{0,0,0,0}},
            {{0,0,0,0},{0,0,1,0},{1,1,1,0},{0,0,0,0}}
    };

    public GameBoard(int width, int height, int cellSize, JLabel scoreLabel, JPanel nextPiecePanel) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
        this.scoreLabel = scoreLabel;
        this.nextPiecePanel = nextPiecePanel;

        board = new int[height][width];
        initGame();

        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(400, this);
        timer.start();
    }

    private void initGame() {
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                board[i][j] = 0;
        score = 0;
        isGameOver = false;
        updateScore();

        nextPiece = randomShape();
        nextColor = randomColor();
        spawnNewPiece();
    }

    private int[][] randomShape() {
        return SHAPES[random.nextInt(SHAPES.length)];
    }

    private int randomColor() {
        return 1 + random.nextInt(7);
    }

    private void spawnNewPiece() {
        currentPiece = copyShape(nextPiece);
        currentColor = nextColor;
        nextPiece = randomShape();
        nextColor = randomColor();
        nextPiecePanel.repaint();

        currentX = width / 2 - currentPiece[0].length / 2;
        currentY = 0;

        if (collision(currentPiece, currentX, currentY)) {
            isGameOver = true;
            timer.stop();
        }
        repaint();
    }

    private int[][] copyShape(int[][] shape) {
        int[][] copy = new int[shape.length][shape[0].length];
        for (int i = 0; i < shape.length; i++)
            copy[i] = shape[i].clone();
        return copy;
    }

    private boolean collision(int[][] piece, int x, int y) {
        for (int row = 0; row < piece.length; row++) {
            for (int col = 0; col < piece[0].length; col++) {
                if (piece[row][col] != 0) {
                    int nx = x + col;
                    int ny = y + row;
                    if (nx < 0 || nx >= width || ny >= height || ny < 0)
                        return true;
                    if (ny >= 0 && board[ny][nx] != 0)
                        return true;
                }
            }
        }
        return false;
    }

    private void mergePiece() {
        for (int row = 0; row < currentPiece.length; row++) {
            for (int col = 0; col < currentPiece[0].length; col++) {
                if (currentPiece[row][col] != 0) {
                    int ny = currentY + row;
                    int nx = currentX + col;
                    if (ny >= 0 && ny < height && nx >= 0 && nx < width)
                        board[ny][nx] = currentColor;
                }
            }
        }
        clearLines();
        spawnNewPiece();
        repaint();
        if (isGameOver) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Игра окончена! Счёт: " + score, "Тетрис", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearLines() {
        int linesCleared = 0;
        for (int y = height - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < width; x++) {
                if (board[y][x] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                for (int row = y; row > 0; row--)
                    board[row] = board[row - 1].clone();
                board[0] = new int[width];
                linesCleared++;
                y++;
            }
        }
        if (linesCleared > 0) {
            int[] points = {0, 100, 300, 600, 1000};
            score += points[Math.min(linesCleared, 4)];
            updateScore();
            int delay = Math.max(100, 400 - (score / 500) * 30);
            timer.setDelay(delay);
        }
    }

    private void updateScore() {
        scoreLabel.setText("Счёт: " + score);
    }

    private void movePiece(int dx, int dy) {
        if (isGameOver) return;
        int nx = currentX + dx;
        int ny = currentY + dy;
        if (!collision(currentPiece, nx, ny)) {
            currentX = nx;
            currentY = ny;
            repaint();
        } else if (dy == 1) {
            mergePiece();
        }
    }

    private void rotatePiece() {
        if (isGameOver) return;
        int size = currentPiece.length;
        int[][] rotated = new int[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                rotated[j][size - 1 - i] = currentPiece[i][j];
        if (!collision(rotated, currentX, currentY)) {
            currentPiece = rotated;
            repaint();
        } else {
            if (!collision(rotated, currentX - 1, currentY)) {
                currentPiece = rotated;
                currentX--;
                repaint();
            } else if (!collision(rotated, currentX + 1, currentY)) {
                currentPiece = rotated;
                currentX++;
                repaint();
            }
        }
    }

    private void hardDrop() {
        if (isGameOver) return;
        while (!collision(currentPiece, currentX, currentY + 1))
            currentY++;
        mergePiece();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (board[y][x] != 0)
                    drawBlock(g, x, y, board[y][x]);
                else {
                    g.setColor(new Color(40, 40, 50));
                    g.drawRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }
        for (int row = 0; row < currentPiece.length; row++) {
            for (int col = 0; col < currentPiece[0].length; col++) {
                if (currentPiece[row][col] != 0) {
                    drawBlock(g, currentX + col, currentY + row, currentColor);
                }
            }
        }
        if (isGameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String msg = "GAME OVER";
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g.drawString(msg, x, y);
        }
    }

    private void drawBlock(Graphics g, int x, int y, int colorCode) {
        Color color;
        switch (colorCode) {
            case 1: color = new Color(0, 255, 255); break;
            case 2: color = new Color(255, 255, 0); break;
            case 3: color = new Color(128, 0, 128); break;
            case 4: color = new Color(0, 255, 0); break;
            case 5: color = new Color(255, 0, 0); break;
            case 6: color = new Color(255, 165, 0); break;
            case 7: color = new Color(0, 0, 255); break;
            default: color = Color.GRAY;
        }
        g.setColor(color);
        g.fillRect(x * cellSize, y * cellSize, cellSize - 1, cellSize - 1);
        g.setColor(color.brighter());
        g.drawRect(x * cellSize, y * cellSize, cellSize - 1, cellSize - 1);
    }

    public void drawNextPiece(Graphics g, int x, int y, int blockSize) {
        for (int row = 0; row < nextPiece.length; row++) {
            for (int col = 0; col < nextPiece[0].length; col++) {
                if (nextPiece[row][col] != 0) {
                    Color color;
                    switch (nextColor) {
                        case 1: color = new Color(0, 255, 255); break;
                        case 2: color = new Color(255, 255, 0); break;
                        case 3: color = new Color(128, 0, 128); break;
                        case 4: color = new Color(0, 255, 0); break;
                        case 5: color = new Color(255, 0, 0); break;
                        case 6: color = new Color(255, 165, 0); break;
                        case 7: color = new Color(0, 0, 255); break;
                        default: color = Color.GRAY;
                    }
                    g.setColor(color);
                    g.fillRect(x + col * blockSize, y + row * blockSize, blockSize - 1, blockSize - 1);
                    g.setColor(color.brighter());
                    g.drawRect(x + col * blockSize, y + row * blockSize, blockSize - 1, blockSize - 1);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) movePiece(0, 1);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isGameOver) return;
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_LEFT: movePiece(-1, 0); break;
            case KeyEvent.VK_RIGHT: movePiece(1, 0); break;
            case KeyEvent.VK_DOWN: movePiece(0, 1); break;
            case KeyEvent.VK_UP: rotatePiece(); break;
            case KeyEvent.VK_SPACE: hardDrop(); break;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}