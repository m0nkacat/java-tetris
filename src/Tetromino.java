public class Tetromino {
    public static final Tetromino[] ALL_PIECES = {
            new Tetromino(new int[][]{{0,0,0,0},{1,1,1,1},{0,0,0,0},{0,0,0,0}}, 1),
            new Tetromino(new int[][]{{0,0,0,0},{0,1,1,0},{0,1,1,0},{0,0,0,0}}, 2),
            new Tetromino(new int[][]{{0,0,0,0},{0,1,0,0},{1,1,1,0},{0,0,0,0}}, 3),
            new Tetromino(new int[][]{{0,0,0,0},{0,1,1,0},{1,1,0,0},{0,0,0,0}}, 4),
            new Tetromino(new int[][]{{0,0,0,0},{1,1,0,0},{0,1,1,0},{0,0,0,0}}, 5),
            new Tetromino(new int[][]{{0,0,0,0},{1,0,0,0},{1,1,1,0},{0,0,0,0}}, 6),
            new Tetromino(new int[][]{{0,0,0,0},{0,0,1,0},{1,1,1,0},{0,0,0,0}}, 7)
    };

    private final int[][] shape;
    private final int colorCode;

    private Tetromino(int[][] shape, int colorCode) {
        this.shape = shape;
        this.colorCode = colorCode;
    }

    public int[][] getShape() {
        return shape;
    }

    public int getColorCode() {
        return colorCode;
    }

    public int getWidth() {
        return shape[0].length;
    }

    public Tetromino rotate() {
        int size = shape.length;
        int[][] rotated = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                rotated[j][size - 1 - i] = shape[i][j];
            }
        }
        return new Tetromino(rotated, colorCode);
    }
}