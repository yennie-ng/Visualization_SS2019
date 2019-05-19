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
	private Rectangle2D markerRectangle = new Rectangle2D.Double(0, 0, 0, 0);
	private int paddingX = 100;
	private int paddingY = 50;
	private int axisHeight = 0;
	private final int countValue = 5;
	private final int lengthValue = 5;
	private final int paddingValue = 5;
	private ArrayList<Axis> axisList = new ArrayList<Axis>();
	private ArrayList<PlotLine> plotLineList = new ArrayList<PlotLine>();
	private ArrayList<Integer> labelIndexList = new ArrayList<>();

	public Rectangle2D getMarkerRectangle() {
		return markerRectangle;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
		for (int i = 0; i < model.getDim(); i++) {
			labelIndexList.add(i);
		}
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

	@Override
	public void paint(Graphics g) {
		plotLineList.clear();
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setBackground(Color.WHITE);
		g2D.clearRect(0, 0, getWidth(), getHeight());
		Font font = new Font("Helvetica Neue", Font.PLAIN, 12);
		g2D.setFont(font);
		FontMetrics metrics = g2D.getFontMetrics(font);
		paintAxis(g2D, metrics);
		paintData(g2D, metrics);
		drawMarker(g2D);
		updateLabelIndexList();
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	private void paintAxis(Graphics2D g2D, FontMetrics metrics) {
		axisHeight = getHeight() - paddingY * 3;
		int gap = (getWidth() - paddingY) / model.getDim();
		int axisCount = model.getLabels().size();

		for (int index = 0; index < axisCount; index++) {
			int labelIndex = index;

			if (axisList.size() == axisCount)
				labelIndex = axisList.get(index).getLabelIndex();

			int axisX = paddingX + gap * index;
			int originalX = axisX;

			if (axisList.size() == axisCount)
				axisX += axisList.get(index).getTranslate();

			g2D.drawLine(axisX, paddingY, axisX, paddingY + axisHeight);
			Rectangle2D rectangle = drawedAxisLabel(g2D, metrics, labelIndex, axisX);
			updateAxisList(originalX, index, labelIndex, rectangle);
		}
	}

	private Rectangle2D drawedAxisLabel(Graphics2D g2D, FontMetrics metrics, int labelIndex, int axisX) {
		String label = model.getLabels().get(labelIndex);
		// ------------------ set center
		int stringWidth = metrics.stringWidth(label);
		int x = axisX - stringWidth / 2;
		int y = paddingY * 2 + axisHeight;
		g2D.setColor(Color.CYAN);
		g2D.drawString(label, x, y);
		g2D.setColor(Color.BLACK);
		// ------------------ label container
		Rectangle2D rect = new Rectangle2D.Double(x - paddingValue, 
														y - metrics.getHeight(),
														stringWidth + paddingValue * 2, 
														metrics.getHeight() * 2);
		g2D.draw(rect);
		return rect;
	}

	private void updateAxisList(int axisX, int axisIndex, int labelIndex, Rectangle2D rect) {
		Range range = model.getRanges().get(labelIndex);
		double min = range.getMin();
		double step = (range.getMax() - min) / axisHeight;
		if (axisList.size() < model.getLabels().size()) {	// add axis in the first time
			axisList.add(new Axis(axisX, labelIndex, min, range.getMax(), step, rect));
		} else {	// update existed axis
			Axis axis = axisList.get(axisIndex);
			axis.setX(axisX);
			axis.setStep(step);
			axis.setRect(rect);
		}
	}

	private void paintData(Graphics2D g2D, FontMetrics metrics) {
		for (int i = 0; i < labelIndexList.size(); i++) {
			int labelIndex = labelIndexList.get(i);
			Axis currentAxis = axisList.get(labelIndex);
			Range range = model.getRanges().get(labelIndex);

			paintValue(g2D, currentAxis, paddingY, metrics, range.getMax());	// draw top value
			paintValue(g2D, currentAxis, paddingY + axisHeight, metrics, range.getMin());	// draw bottom value

			double step = axisHeight / (countValue + 1);

			for (int j = 0; j < countValue; ++j) {
				int y = (int) Math.round(paddingY + axisHeight - step * (j + 1));
				paintValue(g2D, 
							currentAxis, 
							y, 
							metrics,
							currentAxis.getMin() + (paddingY + axisHeight - y) * currentAxis.getStep());
			}
			if (i < axisList.size() - 1) {
				Axis nextAxis = findNextAxis(i + 1);
				paintLines(g2D, currentAxis, nextAxis);
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

	private void paintValue(Graphics2D g2D, Axis axis, int y, FontMetrics metrics, double value) {
		int axisX = (int) Math.round(axis.getX() + axis.getTranslate());
		g2D.drawLine(axisX - lengthValue, y, axisX, y);
		String label = String.format("%.1f", value);
		int x = axisX - (metrics.stringWidth(label) + paddingValue);
		g2D.setColor(Color.ORANGE);
		g2D.drawString(label, x, y);
		g2D.setColor(Color.BLACK);
	}

	private void paintLines(Graphics2D g2D, Axis currentAxis, Axis nextAxis) {
		for (int index = 0; index < model.getList().size(); index++) {
			Data data = model.getList().get(index);
			int startX = (int) Math.round(currentAxis.getX() + currentAxis.getTranslate());
			int endX = (int) Math.round(nextAxis.getX() + nextAxis.getTranslate());
			int startY = (int) Math.round(paddingY + axisHeight
					- (data.getValue(currentAxis.getLabelIndex()) - currentAxis.getMin()) / currentAxis.getStep());
			int endY = (int) Math.round(paddingY + axisHeight
					- (data.getValue(nextAxis.getLabelIndex()) - nextAxis.getMin()) / nextAxis.getStep());

			if (data.getSelected()) {
				g2D.setColor(Color.MAGENTA);
			} else {
				g2D.setColor(Color.BLACK);
			}
			g2D.drawLine(startX, startY, endX, endY);
			updatePlotLineList(data, index, startX, endX, startY, endY);
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

		line.getxCoordinates().add(startX);
		line.getxCoordinates().add(endX);
		line.getyCoordinates().add(startY);
		line.getyCoordinates().add(endY);
	}

	private void drawMarker(Graphics2D g2D) {
		g2D.setPaint(Color.RED);
		g2D.draw(markerRectangle);
	}

	private void updateLabelIndexList() {
		ArrayList<Axis> tempArray = new ArrayList<Axis>();
		tempArray.addAll(axisList);
		tempArray.sort(new Comparator<Axis>() {
			@Override
			public int compare(Axis o1, Axis o2) {
				return (int) Math.round(o1.getX() + o1.getTranslate() - (o2.getX() + o2.getTranslate()));
			}
		});
		labelIndexList.clear();
		for (Axis axis : tempArray) {
			labelIndexList.add(axis.getLabelIndex());
		}
	}
}
