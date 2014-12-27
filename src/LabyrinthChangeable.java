
public interface LabyrinthChangeable {
    public void setLeft(int x, int y, boolean left);
    public void setRight(int x, int y, boolean right);
    public void setUp(int x, int y, boolean up);
    public void setDown(int x, int y, boolean down);
    public void setValue(int x, int y, char value);
    public int getNumberOfCells();
}
