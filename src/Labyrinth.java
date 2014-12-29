import java.util.InputMismatchException;
import java.util.Random;

public class Labyrinth implements LabyrinthChangeable {

    private final int numberOfCells;

    private Cell[][] cells;                  //    cells.length != numberOfCells, use getNumberOfCells()  !!!!

    public int getNumberOfCells() {
        return numberOfCells;
    }

    private Labyrinth(Cell[][] cells) {
        this.cells = cells;
        this.numberOfCells = cells.length - 1;
    }

    public static Labyrinth generateFullLabyrinth(int numberOfCells) {
        Cell[][] cells = new Cell[numberOfCells + 1][numberOfCells + 1];  // +1 - границы
        for (int j = 0; j < numberOfCells; j++) {
            for (int i = 1; i < numberOfCells + 1; i++) {
                cells[i][j] = new Cell(true, true);
            }
            cells[j][numberOfCells] = new Cell(false, true);
            cells[0][j] = new Cell(true, false);
        }
        cells[0][numberOfCells].setLeft(false);
        cells[numberOfCells][numberOfCells] = new Cell(false, true);
        return new Labyrinth(cells);
    }

    public void printOut() {
        char symbolToPrint;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                if (cells[i][j].isLeft()) {
                    System.out.print("|");
                } else {
                    System.out.print(" ");
                }
                symbolToPrint = cells[i][j].isDown() ? '_' : ' ';
                System.out.print(symbolToPrint);
            }
            System.out.println("");
        }
    }

    public void printOutWithValues() {
        char symbolToPrint;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                if (cells[i][j].isLeft()) {
                    System.out.print("|");
                } else {
                    System.out.print(" ");
                }
                System.out.print(cells[i][j].getValue());

            }
            System.out.println("");
            for (int j = 0; j < cells[0].length; j++) {
                symbolToPrint = cells[i][j].isDown() ? '-' : ' ';
                System.out.print(" " + symbolToPrint);
            }
            System.out.println("");
        }
    }

    public void setRight(int x, int y, boolean right) {
        if (x >= getNumberOfCells() || y >= getNumberOfCells() || x < 0 || y < 0) {
            throw new IndexOutOfBoundsException();
        }
        cells[x+1][y+1].setLeft(right);
    }

    public boolean getRight(int x, int y) {
        if (x >= getNumberOfCells() || y >= getNumberOfCells() || x < 0 || y < 0) {
            throw new IndexOutOfBoundsException();
        }
        return cells[x+1][y+1].isLeft();
    }

    public void setLeft(int x, int y, boolean left) {
        if (x >= getNumberOfCells() || y >= getNumberOfCells() || x < 0 || y < 0) {
            throw new IndexOutOfBoundsException();
        }
        cells[x+1][y].setLeft(left);
    }

    public boolean getLeft(int x, int y) {
        if (x >= getNumberOfCells() || y >= getNumberOfCells() || x < 0 || y < 0) {
            throw new IndexOutOfBoundsException();
        }
        return cells[x+1][y].isLeft();
    }

    public void setUp(int x, int y, boolean up) {
        if (x >= getNumberOfCells() || y >= getNumberOfCells() || x < 0 || y < 0) {
            throw new IndexOutOfBoundsException();
        }
        cells[x][y].setDown(up);
    }

    public boolean getUp(int x, int y, boolean up) {
        if (x >= getNumberOfCells() || y >= getNumberOfCells() || x < 0 || y < 0) {
            throw new IndexOutOfBoundsException();
        }
        return cells[x][y].isDown();
    }

    public void setDown(int x, int y, boolean down) {
        if (x >= getNumberOfCells() || y >= getNumberOfCells() || x < 0 || y < 0) {
            throw new IndexOutOfBoundsException();
        }
        cells[x+1][y].setDown(down);
    }

    public boolean getDown(int x, int y) {
        if (x >= getNumberOfCells() || y >= getNumberOfCells() || x < 0 || y < 0) {
            throw new IndexOutOfBoundsException();
        }
        return cells[x+1][y].isDown();
    }

    public void setValue(int x, int y, char value) {
        cells[x+1][y].setValue(value);
    }

    public void setGate(int x1, int y1, int x2, int y2) {
        if (x1 >= numberOfCells || x2 >= numberOfCells || y1 >= numberOfCells || y2 >= numberOfCells) {
            System.err.println(x1 + " " + x2 + " " + y1 + " " + y2);
            throw new IndexOutOfBoundsException();
        }
        if (Math.abs(x1 - x2) == 1 && y1 == y2) {
            if (x1 > x2) {
                setDown(x2, y2, false);
            } else {
                setDown(x1, y2, false);
            }
        } else if (Math.abs(y1 - y2) == 1 && x1 == x2) {
            if (y1 > y2) {
                setLeft(x2, y2 + 1, false);
            } else {
                setLeft(x1, y1 + 1, false);
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public static Labyrinth generateLabyrinthPrim(int numberOfCells) {  // алгоритм Прима
        Labyrinth res = generateFullLabyrinth(numberOfCells);
        CellType[][] cellTypes = new CellType[numberOfCells][numberOfCells];
        for (int i = 0; i < cellTypes.length; i++) {
            for (int j = 0; j < cellTypes[0].length; j++) {
                cellTypes[i][j] = CellType.Outside;
            }
        }
        Random random = new Random();
        int xRand = random.nextInt(numberOfCells);
        int yRand = random.nextInt(numberOfCells);
        changeBorder(cellTypes, xRand, yRand);
        int[][] borders = hasBorder(cellTypes);
        while (borders != null) {
            int randNumber = random.nextInt(borders[0].length);
            changeBorder(cellTypes, borders[0][randNumber], borders[1][randNumber]);
            int chooseValue = changeRealBorder(cellTypes, borders[0][randNumber], borders[1][randNumber]);
            switch (chooseValue) {
                case 1:
                    res.setGate(borders[0][randNumber], borders[1][randNumber], borders[0][randNumber] - 1, borders[1][randNumber]);
                    break;
                case 2:
                    res.setGate(borders[0][randNumber], borders[1][randNumber], borders[0][randNumber], borders[1][randNumber] - 1);
                    break;
                case 3:
                    res.setGate(borders[0][randNumber], borders[1][randNumber], borders[0][randNumber], borders[1][randNumber] + 1);
                    break;
                case 4:
                    res.setGate(borders[0][randNumber], borders[1][randNumber], borders[0][randNumber] + 1, borders[1][randNumber]);
                    break;
            }
            borders = hasBorder(cellTypes);
        }
        return res;
    }

    public static Labyrinth generateLabyrinthKrascal(int numberOfCells) {  // алгоритм Краскала
        Labyrinth res = generateFullLabyrinth(numberOfCells);
        Random random = new Random();
        int numberOfLocations = numberOfCells * numberOfCells;
        int randX, randY, randNumber;
        int[][] randomSequenceOfCells = res.getRandomSequenceOfCells();
        while (numberOfLocations > 1) {
            randX = random.nextInt(numberOfCells);
            randY = random.nextInt(numberOfCells);
            while (true) {
                randNumber = random.nextInt(4);
                if (randNumber == 0 && randX > 0) {
                    if (!res.isConnected(randX, randY, randX - 1, randY)) {
                        res.setGate(randX, randY, randX - 1, randY);
                    }
                    break;
                }
                if (randNumber == 1 && randX < numberOfCells - 1) {
                    if (!res.isConnected(randX, randY, randX + 1, randY)) {
                        res.setGate(randX, randY, randX + 1, randY);
                    }
                    break;
                }
                if (randNumber == 2 && randY > 0) {
                    if (!res.isConnected(randX, randY, randX, randY - 1)) {
                        res.setGate(randX, randY, randX, randY - 1);
                    }
                    break;
                }
                if (randNumber == 3 && randY < numberOfCells - 1) {
                    if (!res.isConnected(randX, randY, randX, randY + 1)) {
                        res.setGate(randX, randY, randX, randY + 1);
                    }
                    break;
                }
            }
            numberOfLocations--;
        }
        return null;
    }

    private boolean isConnected(int x1, int y1, int x2, int y2) {
        return solveRecursive(x1, y1, x2, y2) != null;
    }

    private int[][] getRandomSequenceOfCells() {       // для generateLabyrinthKrascal
        int[] temp = new int[getNumberOfCells() * getNumberOfCells()];
        int[][] res = new int[2][getNumberOfCells() * getNumberOfCells()];
        Random random = new Random();
        for (int i = 0; i < getNumberOfCells(); i++) {
            for (int j = 0; j < getNumberOfCells(); j++) {
                temp[i + getNumberOfCells() * j] = random.nextInt();
                res[0][i + getNumberOfCells() * j] = i;
                res[1][i + getNumberOfCells() * j] = j;
            }
        }
        //todo
        return res;
    }

    private static int[][] hasBorder(CellType[][] cellTypes) {       // [x, y] или null, если не нашел
        int resAmount = 0;                                           // for generateLabyrinthPrim
        for (int i = 0; i < cellTypes.length; i++) {
            for (int j = 0; j < cellTypes[0].length; j++) {
                if (cellTypes[i][j] == CellType.Border) {
                    resAmount++;
                }
            }
        }
        if (resAmount > 0) {
            int[][] res = new int[2][resAmount];
            int n = 0;
            for (int i = 0; i < cellTypes.length; i++) {
                for (int j = 0; j < cellTypes[0].length; j++) {
                    if (cellTypes[i][j] == CellType.Border) {
                        res[0][n] = i;
                        res[1][n] = j;
                        n++;
                    }
                }
            }
            return res;
        } else {
            return null;
        }
    }

    private static void changeBorder(CellType[][] cellTypes, int x, int y) {        // for generateLabyrinthPrim
        cellTypes[x][y] = CellType.Inside;
        if (x > 0) {
            cellTypes[x - 1][y] = cellTypes[x - 1][y] == CellType.Outside ? CellType.Border : cellTypes[x - 1][y];
        }
        if (y > 0) {
            cellTypes[x][y - 1] = cellTypes[x][y - 1] == CellType.Outside ? CellType.Border : cellTypes[x][y - 1];
        }
        if (x < cellTypes.length - 1) {
            cellTypes[x+1][y] = cellTypes[x+1][y] == CellType.Outside ? CellType.Border : cellTypes[x+1][y];
        }
        if (y < cellTypes.length - 1) {
            cellTypes[x][y + 1] = cellTypes[x][y + 1] == CellType.Outside ? CellType.Border : cellTypes[x][y + 1];
        }
    }

    private static int changeRealBorder(CellType[][] cellTypes, int x, int y) {         // for generateLabyrinthPrim
        int[] chooseFrom = new int[4];                                                  //    1
        int res = 0;                                                                    //   2  3
        Random random = new Random();                                                   //    4
        if (x > 0 && cellTypes[x - 1][y] == CellType.Inside) {
            chooseFrom[0] = 1;
        } else {
            chooseFrom[0] = 0;
        }
        if (y > 0 && cellTypes[x][y - 1] == CellType.Inside) {
            chooseFrom[1] = 2;
        } else {
            chooseFrom[1] = 0;
        }
        if (x < cellTypes.length - 1 && cellTypes[x+1][y] == CellType.Inside) {
            chooseFrom[2] = 4;
        } else {
            chooseFrom[2] = 0;
        }
        if (y < cellTypes.length - 1 && cellTypes[x][y + 1] == CellType.Inside) {
            chooseFrom[3] = 3;
        } else {
            chooseFrom[3] = 0;
        }
        while (res == 0) {
            res = chooseFrom[random.nextInt(4)];
        }
        return res;
    }

    private int[][] solveRecursive(int x1, int y1, int x2, int y2) { // null, усли пути нет
        //todo
        return null;
    }

    private boolean canGo(int x, int y, int dx, int dy) {   // dx, dy = +-1
        if (x >= getNumberOfCells() || y >= getNumberOfCells() || x < 0 || y < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (dx == 1 && dy == 0) {
            return getDown(x, y);
        } else if (dx == -1 && dy == 0) {
            if (x == 0) {
                throw new IndexOutOfBoundsException();
            }
            return getDown(x - 1, y);
        } else if (dx == 0 && dy == 1) {
            return getRight(x, y);
        } else if (dx == 0 && dy == -1) {
            return getLeft(x, y);
        } else {
            throw new InputMismatchException();
        }
    }

    private int[] whereCanGo(int x, int y) {            //  [вверх, вправо, вниз, налево]  1 - да, -1 - нет
        int[] res = new int[4];
        res[0] = x > 0 && canGo(x, y, -1, 0) ? 1 : -1;
        res[1] = y < getNumberOfCells() - 1 && canGo(x, y, 0, 1) ? 1 : -1;
        res[2] = x < getNumberOfCells() - 1 && canGo(x, y, 1, 0) ? 1 : -1;
        res[3] = y > 0 && canGo(x, y, 0, -1) ? 1 : -1;
        return res;
    }

    public static void main(String[] args) {
        Labyrinth labyrinth = generateLabyrinthPrim(5);
        labyrinth.setValue(2, 2, '*');
        labyrinth.printOutWithValues();
    }
}
