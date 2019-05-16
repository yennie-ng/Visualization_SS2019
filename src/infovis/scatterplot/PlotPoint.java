package infovis.scatterplot;

public class PlotPoint {
    private double horizontal;
    private double vertical;
    private double x;
    private double y;

    public PlotPoint(double horizontal, double vertical, double x, double y) {
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.x = x;
        this.y = y;
    }

    public double getHorizontal() {
        return this.horizontal;
    }

    public void setHorizontal(double horizontal) {
        this.horizontal = horizontal;
    }

    public double getVertical() {
        return this.vertical;
    }

    public void setVertical(double vertical) {
        this.vertical = vertical;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }
}