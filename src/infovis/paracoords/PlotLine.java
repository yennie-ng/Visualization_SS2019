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
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public ArrayList<Integer> getyCoordinates() {
		return yCoordinates;
	}

	public void setyCoordinates(ArrayList<Integer> yCoordinates) {
		this.yCoordinates = yCoordinates;
	}

	public ArrayList<Integer> getxCoordinates() {
		return xCoordinates;
	}

	public void setxCoordinates(ArrayList<Integer> xCoordinates) {
		this.xCoordinates = xCoordinates;
	}
}