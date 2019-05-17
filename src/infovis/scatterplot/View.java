package infovis.scatterplot;

import infovis.debug.Debug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class View extends JPanel {
	private static final long serialVersionUID = 7332626899583634667L;
	private Model model = null;
	private Rectangle2D markerRect = new Rectangle2D.Double(0, 0, 0, 0);
	private int cellSize = 40;
	private int pointSize = 5;
	private int padding = 20;
	private ArrayList<MatrixCell> cells = new ArrayList<MatrixCell>();

	// ------------------------ getter setter -----------------------------
	public Rectangle2D getMarkerRect() {
		return markerRect;
	}

	public ArrayList<MatrixCell> getCells() {
		return this.cells;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	// ---------------------------------------------------------------------
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING, 
			RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
		debuggingPrint();
		this.cellSize = ((getHeight() <= getWidth() ? getHeight() : getWidth()) - 50) / model.getLabels().size();	// resize based on Frame
		g2d.setStroke(new BasicStroke(2.f));
		drawScatterPlot(g2d);
		drawMarker(g2d);
	}

	private void drawScatterPlot(Graphics2D g2d) {
		int size = model.getLabels().size();
		Font font = new Font("Helvetica Neue", Font.PLAIN, 10);
		g2d.setFont(font);

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {		// for each cell...
				Rectangle2D rect = new Rectangle2D.Double(cellSize * i + 5, cellSize * j + 5, cellSize, cellSize);
				MatrixCell cell;
				if (this.cells.size() < Math.pow(size, 2)) {	// create new cell to the list in the first time
					cell = new MatrixCell(rect, j, i);
					cells.add(cell);
				} else {										// get from ecurrent list
					cell = cells.get(i * size + j);
					cell.setRect(rect);
				}

				g2d.setPaint(Color.BLACK);
				g2d.draw(rect);
				g2d.setColor(Color.white);
				g2d.fill(rect);
				g2d.setPaint(Color.BLACK);

				if (i == j)	// diagonal cell
					drawDiagonalCell(g2d, cell, model.getLabels().get(i), font);
				else
					drawPlotCells(g2d, cell, i, j);
			}
		}
	}

	private void drawPlotCells(Graphics2D g2d, MatrixCell cell, int columnIndex, int rowIndex) {
		// ------------ scale the data 
		Range rowRange = model.getRanges().get(rowIndex);
		double minVertical = rowRange.getMin();
		double maxVertical = rowRange.getMax();

		Range columnRange = model.getRanges().get(columnIndex);
		double minHorizontal = columnRange.getMin();
		double maxHorizontal = columnRange.getMax();

		double vertivalSteps = (maxVertical - minVertical) / (cellSize - padding);
		double horizontalSteps = (maxHorizontal - minHorizontal) / (cellSize - padding);
		int dataSize = model.getList().size();

		for (int index = 0; index < dataSize; index++) {
			Data data = model.getList().get(index);
			double vertical = data.getValue(rowIndex);
			double horizontal = data.getValue(columnIndex);
			Rectangle2D rect = cell.getRect();
			// set point's coordinate
			double x = rect.getX() + 5 + (horizontal - minHorizontal) / horizontalSteps;
			double y = rect.getY() + 5 + (vertical - minVertical) / vertivalSteps;

			g2d.setColor((data.getSelected()) ? Color.RED : Color.BLACK);

			// draw point
			g2d.fillOval((int) Math.round(x), (int) Math.round(y), pointSize, pointSize);
			updatePointList(cell, dataSize, horizontal, vertical, x, y, index);
		}
	}

	private void drawDiagonalCell(Graphics2D g2d, MatrixCell cell, String label, Font font) {
		FontMetrics fontMetrics = g2d.getFontMetrics(font);
		Rectangle2D rect = cell.getRect();
		int x = (int) Math.round(rect.getX()) + (cellSize - fontMetrics.stringWidth(label)) / 2;
		int y = (int) Math.round(rect.getY()) + ((cellSize - fontMetrics.getHeight()) / 2) + fontMetrics.getAscent();
		g2d.drawString(label, x, y);
	}

	private void updatePointList(
		MatrixCell cell, int dataSize, double horizontal, 
		double vertical, double x, double y, int index) {
		if (cell.getPoints().size() < dataSize) {	// first time
			// ad point
			cell.getPoints().add(new PlotPoint(horizontal, vertical, x, y));
		} else {
			// update point
			PlotPoint point = cell.getPoints().get(index);
			point.setX(x);
			point.setY(y);
			point.setHorizontal(horizontal);
			point.setVertical(vertical);
		}
	}

	private void drawMarker(Graphics2D g2d) {
		g2d.setPaint(Color.RED);
		g2d.draw(markerRect);
	}

	private void debuggingPrint() {
		for (String l : model.getLabels()) {
			Debug.print(l);
			Debug.print(",  ");
			Debug.println("");
		}
		for (Range range : model.getRanges()) {
			Debug.print(range.toString());
			Debug.print(",  ");
			Debug.println("");
		}
		for (Data d : model.getList()) {
			Debug.print(d.toString());
			Debug.println("");
		}
	}
}
