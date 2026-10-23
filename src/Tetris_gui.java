import javax.swing.*;
import java.awt.*;

public class Tetris_gui extends JFrame {
    private static final int CELL_SIZE = 30;
    private static final int SIDE_PANEL_WIDTH = 200;

    private GameBoard gameBoard;

    public Tetris_gui() {
        setTitle("Тетрис");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initUI();
        pack();
        setLocationRelativeTo(null);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(SIDE_PANEL_WIDTH, 0));
        sidePanel.setBackground(new Color(50, 50, 60));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel scoreLabel = new JLabel("Счёт: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel nextPiecePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (gameBoard != null) {
                    gameBoard.drawNextPiece(g, 10, 10, CELL_SIZE);
                }
            }
        };
        nextPiecePanel.setPreferredSize(new Dimension(120, 120));
        nextPiecePanel.setBackground(new Color(30, 30, 40));
        nextPiecePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidePanel.add(Box.createVerticalStrut(20));
        sidePanel.add(scoreLabel);
        sidePanel.add(Box.createVerticalStrut(30));
        sidePanel.add(new JLabel("Следующая:") {{
            setForeground(Color.WHITE);
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }});
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(nextPiecePanel);
        sidePanel.add(Box.createVerticalGlue());

        int boardWidth = 10;
        int boardHeight = 20;
        gameBoard = new GameBoard(boardWidth, boardHeight, CELL_SIZE, scoreLabel, nextPiecePanel);
        gameBoard.setPreferredSize(new Dimension(boardWidth * CELL_SIZE, boardHeight * CELL_SIZE));
        gameBoard.setBackground(Color.BLACK);

        mainPanel.add(gameBoard, BorderLayout.CENTER);
        mainPanel.add(sidePanel, BorderLayout.EAST);

        add(mainPanel);
        gameBoard.setFocusable(true);
        gameBoard.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Tetris_gui().setVisible(true));
    }
}