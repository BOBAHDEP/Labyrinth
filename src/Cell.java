
public class Cell{
    private boolean down; //true = есть граница
    private boolean left;
    private char value;

    public Cell(boolean down, boolean left) {
        this.down = down;
        this.left = left;
        this.value = ' ';
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value){
        this.value = value;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }


}
