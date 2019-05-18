package infovis.paracoords;

import infovis.scatterplot.Model;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class View extends JPanel {
	private static final long serialVersionUID = 1L;
	private Model model = null;
	private Rectangle2D markerRect = new Rectangle2D.Double(0, 0, 0, 0);
	private int paddingX = 100;
	private int paddingY = 50;
	private int axisHeight = 0;
	private final int count = 5;
	private final int length = 5;
	private final int padding = 5;
	private ArrayList<Axis> axisList = new ArrayList<Axis>();
	private ArrayList<PlotLine> plotLineList = new ArrayList<PlotLine>();
	private ArrayList<Integer> labelIndexList = new ArrayList<Integer>();

	public Rectangle2D getMarkerRectangle() {
		return this.markerRect;
	}

	public ArrayList<Axis> getAxisList() {
		return axisList;
	}

	public ArrayList<PlotLine> getPlotLineList() {
		return plotLineList;
	}

	public ArrayList<Integer> getLabelIndexList() {
		return labelIndexList;
	}

	public void setLabelIndexList(ArrayList<Integer> labelIndexList) {
		this.labelIndexList = labelIndexList;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
		for (int i = 0; i < model.getDim(); ++i) {
			labelIndexList.add(i);
		}
	}

	@Override
	public void paint(Graphics g) {
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}
}
