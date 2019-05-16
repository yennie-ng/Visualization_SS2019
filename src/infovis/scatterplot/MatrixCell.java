package infovis.scatterplot;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class MatrixCell {
    private Rectangle2D rect;
    private ArrayList<PlotPoint> points = new ArrayList<PlotPoint>();
    private int row;
    private int column;

    public MatrixCell(Rectangle2D rect, int row, int column) {
        this.rect = rect;
        this.row = row;
        this.column = column;
    }
    public Rectangle2D getRect() {
        return rect;
    }

    public void setRect(Rectangle2D rect) {
        this.rect = rect;
    }

    public ArrayList<PlotPoint> getPoints() {
        return this.points;
    }

    public void setPoints(ArrayList<PlotPoint> points) {
        this.points = points;
    }

    public int getRow() {
        return this.row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return this.column;
    }

    public void setColumn(int column) {
        this.column = column;
    } 

}