package infovis.paracoords;

import java.util.ArrayList;

import infovis.scatterplot.Data;

public class PlotLine {
    private Data data;
    private ArrayList<Integer> xCoordinates = new ArrayList<Integer>();
    private ArrayList<Integer> yCoordinates = new ArrayList<Integer>();

    public PlotLine(Data data) {
        this.data = data;
    }

    public Data getData() {
        return this.data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public ArrayList<Integer> getXCoordinates() {
        return this.xCoordinates;
    }

    public void setXcoordinates(ArrayList<Integer> xCoordinates) {
        this.xCoordinates = xCoordinates;
    }

    public ArrayList<Integer> getYCoordinates() {
        return this.yCoordinates;
    }

    public void setYCoordinates(ArrayList<Integer> yCoordinates) {
        this.yCoordinates = yCoordinates;
    }
}