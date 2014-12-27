import java.util.Random;

public class Labyrinth implements LabyrinthChangeable{

    private final int numberOfCells;

    private Cell[][] cells;

    public int getNumberOfCells() {
        return numberOfCells;
    }

    private Labyrinth(Cell[][] cells) {
        this.cells = cells;
        this.numberOfCells = cells.length;
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
            for (int j = 0; j < cells[0].length; j++){
                if (cells[i][j].isLeft()){
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

    public void printOutWithValues(){}  //todo

    public void setRight(int x, int y, boolean right) {
        if (x >= getNumberOfCells() || y >= getNumberOfCells() || x < 0 || y < 0) {
            throw new IndexOutOfBoundsException();
        }
        cells[x+1][y+1].setLeft(right);
    }

    public void setLeft(int x, int y, boolean left) {
        if (x >= getNumberOfCells() || y >= getNumberOfCells() || x < 0 || y < 0) {
            throw new IndexOutOfBoundsException();
        }
        cells[x+1][y].setLeft(left);
    }

    public void setUp(int x, int y, boolean up) {
        if (x >= getNumberOfCells() || y >= getNumberOfCells() || x < 0 || y < 0) {
            throw new IndexOutOfBoundsException();
        }
        cells[x][y].setDown(up);
    }

    public void setDown(int x, int y, boolean down) {
        if (x >= getNumberOfCells() || y >= getNumberOfCells() || x < 0 || y < 0) {
            throw new IndexOutOfBoundsException();
        }
        cells[x+1][y].setDown(down);
    }

    public void setValue(int x, int y, char value){
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

    public static Labyrinth generateLabyrinthPrim(int numberOfCells){  // алгоритм Прима
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
        System.out.println(xRand + " " + yRand);
        int[][] borders = hasBorder(cellTypes);
        while (borders != null) {
            res.printOut();

            int randNumber = random.nextInt(borders[0].length);
            changeBorder(cellTypes, borders[0][randNumber], borders[1][randNumber]);
            int chooseValue = changeRealBorder(cellTypes, borders[0][randNumber], borders[1][randNumber]);
            switch (chooseValue){
                case 1:
                    res.setGate(borders[0][randNumber], borders[1][randNumber], borders[0][randNumber] - 1, borders[1][randNumber]);
                    System.out.println(borders[0][randNumber] + " " + borders[1][randNumber] + " 1");
                    break;
                case 2:
                    res.setGate(borders[0][randNumber], borders[1][randNumber], borders[0][randNumber], borders[1][randNumber] - 1);
                    System.out.println(borders[0][randNumber] + " " + borders[1][randNumber] + " 2");
                    break;
                case 3:
                    res.setGate(borders[0][randNumber], borders[1][randNumber], borders[0][randNumber], borders[1][randNumber] + 1);
                    System.out.println(borders[0][randNumber] + " " + borders[1][randNumber] + " 3");
                    break;
                case 4:
                    res.setGate(borders[0][randNumber], borders[1][randNumber], borders[0][randNumber] + 1, borders[1][randNumber]);
                    System.out.println(borders[0][randNumber] + " " + borders[1][randNumber] + " 4");
                    break;
            }
            borders = hasBorder(cellTypes);
        }
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

    public static void main(String[] args) {
        generateLabyrinthPrim(5).printOut();
    }
}
