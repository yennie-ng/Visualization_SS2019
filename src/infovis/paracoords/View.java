package infovis.paracoords;

import infovis.scatterplot.Data;
import infovis.scatterplot.Model;
import infovis.scatterplot.Range;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JPanel;

import com.sun.istack.internal.Nullable;

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
		plotLineList.clear();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
		Font font = new Font("Helvetica Neue", Font.PLAIN, 12);
		g2d.setFont(font);
		FontMetrics fontMetric = g2d.getFontMetrics(font);
		this.paintAxis(g2d, fontMetric);
		this.paintData(g2d, fontMetric);
		this.drawMarker(g2d);
		this.updateLabelIndexs();
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	private void paintAxis(Graphics2D g2d, FontMetrics fontMetric) {
		this.axisHeight =  this.getHeight() - paddingY * 3;
		int gap = (this.getWidth() - paddingY) / model.getDim();
		int axisCount = model.getLabels().size();

		for (int index = 0; index < axisCount; index ++) {
			int labelIndex = index;

			if (axisList.size() == axisCount)
				labelIndex = axisList.get(index).getLabelIndex();
			
			int axisX = paddingX + gap * index;
			int originalX = axisX;

			if (axisList.size() == axisCount)
				axisX += axisList.get(index).getTranslate();
			
			g2d.drawLine(axisX, paddingY, axisX, paddingY + axisHeight);
			Rectangle2D rect = drawedAxisLabel(g2d, fontMetric, labelIndex, axisX);
			this.updateAxisList(originalX, index, labelIndex, rect);
		}
	}

	private Rectangle2D drawedAxisLabel(Graphics2D g2d, FontMetrics fontMetric, int labelIndex, int axisX) {
		String label = this.model.getLabels().get(labelIndex);
		// ------------------ set center
		int width = fontMetric.stringWidth(label);
		int x = axisX - width / 2;
		int y = paddingY * 2 + axisHeight;
		g2d.setColor(Color.CYAN);
		g2d.drawString(label, x, y);
		g2d.setColor(Color.BLACK);
		// ------------------ label container
		Rectangle2D rect = new Rectangle2D.Double(x - padding, 
												y - fontMetric.getHeight(), 
												width + padding * 2, 
												fontMetric.getHeight() * 2);
		g2d.draw(rect);
		return rect;
	}

	private void updateAxisList(int axisX, int axisIndex, int labelIndex, Rectangle2D rect) {
		Range range = model.getRanges().get(labelIndex);
		double min = range.getMin();
		double step = (range.getMax() - min) / axisHeight;
		if (axisList.size() < model.getLabels().size()) {	// add axis in the first time
			Axis newAxis = new Axis(axisX, labelIndex, min, range.getMax(), step, rect);
			axisList.add(newAxis);
		} else {											// update existed axis
			Axis axis = axisList.get(axisIndex);
			axis.setX(axisX);
			axis.setStep(step);
			axis.setRect(rect);
		}
	}

	private void paintData(Graphics2D g2d, FontMetrics fontMetric) {
		for (int i = 0; i < this.labelIndexList.size(); i++) {
			int labelIndex = labelIndexList.get(i);
			Axis currentAxis = this.axisList.get(labelIndex);
			Range range = model.getRanges().get(labelIndex);

			this.paintValue(g2d, currentAxis, this.paddingY, 
							fontMetric, range.getMax());	// draw top value
			this.paintValue(g2d, currentAxis, this.paddingY + this.axisHeight, 
							fontMetric, range.getMin());	// draw bottom value
			double step = this.axisHeight / (this.count + 1);

			for (int j = 0; j < this.count; j++) {
				int y = (int) Math.round(this.paddingY + this.axisHeight - step * (j + 1));
				paintValue(g2d, currentAxis, y, 
							fontMetric,
							currentAxis.getMin() + (this.paddingY + this.axisHeight - y) * currentAxis.getStep());
				if (i < this.axisList.size() - 1) {
					Axis nextAxis = this.findNextAxis(i + 1);
					this.paintLines(g2d, currentAxis, nextAxis);
				}
			}
		}
	}

	@Nullable
	private Axis findNextAxis(int index) {
		for (Axis axis : axisList) {
			if (axis.getLabelIndex() == labelIndexList.get(index)) {
				return axis;
			}
		}
		return null;
	}

	private void paintValue(Graphics2D g2d, Axis axis, int y, FontMetrics fontMetric, double value) {
		int axisX = (int) Math.round(axis.getX() + axis.getTranslate());
		g2d.drawLine(axisX - this.length, y, axisX, y);
		String label = String.format("%.1f", value);
		int x = axisX - (fontMetric.stringWidth(label) + this.padding);
		g2d.setColor(Color.ORANGE);
		g2d.drawString(label, x, y);
		g2d.setColor(Color.BLACK);
	}

	private void paintLines(Graphics2D g2d, Axis currentAxis, Axis nextAxis) {
		for (int index = 0; index < model.getList().size(); index++) {
			Data data = model.getList().get(index);
			int startX = (int) Math.round(currentAxis.getX() + currentAxis.getTranslate());
			int endX = (int) Math.round(nextAxis.getX() + nextAxis.getTranslate());
			int startY = (int) Math.round(paddingY + axisHeight
					- (data.getValue(currentAxis.getLabelIndex()) - currentAxis.getMin()) / currentAxis.getStep());
			int endY = (int) Math.round(paddingY + axisHeight
					- (data.getValue(nextAxis.getLabelIndex()) - nextAxis.getMin()) / nextAxis.getStep());

			if (data.getSelected()) {
				g2d.setColor(Color.RED);
			} else {
				g2d.setColor(Color.BLACK);
			}

			g2d.drawLine(startX, startY, endX, endY);
			this.updatePlotLineList(data, index, startX, endX, startY, endY);
		}
	}

	private void updatePlotLineList(Data data, int index, int startX, int endX, int startY, int endY) {
		PlotLine line;

		if (plotLineList.size() < model.getList().size()) {
			line = new PlotLine(data);
			plotLineList.add(line);
		} else {
			line = plotLineList.get(index);
		}

		line.getXCoordinates().add(startX);
		line.getXCoordinates().add(endX);
		line.getXCoordinates().add(startY);
		line.getXCoordinates().add(endY);
	}

	private void drawMarker(Graphics2D g2d) {
		g2d.setPaint(Color.RED);
		g2d.draw(this.markerRect);
	}

	private void updateLabelIndexs() {
		ArrayList<Axis> tempArray = new ArrayList<Axis>();
		tempArray.addAll(this.axisList);
		tempArray.sort(new Comparator<Axis>() {

			@Override
			public int compare(Axis o1, Axis o2) {
				return (int) Math.round(o1.getX() + o1.getTranslate() - o2.getX() - o2.getTranslate());
			}
		});
		this.labelIndexList.clear();
		for (Axis axis: tempArray) {
			this.labelIndexList.add(axis.getLabelIndex());
		}
	}
}
