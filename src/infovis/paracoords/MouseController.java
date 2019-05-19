package infovis.paracoords;

import infovis.scatterplot.Data;
import infovis.scatterplot.Model;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class MouseController implements MouseListener, MouseMotionListener {
	private View view = null;
	private Model model = null;
	private double startX = 0;
	private double startY = 0;
	private Axis selectedAxis = null;

	public void mouseClicked(MouseEvent e) { }

	public void mouseEntered(MouseEvent e) { }

	public void mouseExited(MouseEvent e) { }

	public void mousePressed(MouseEvent e) {
		startX = e.getX();
		int y = e.getY();

		for (int i = 0; i < view.getAxisList().size(); ++i) {
			Axis container = view.getAxisList().get(i);
			if (container.getRect().contains(new Point2D.Double(startX, y))) {
				selectedAxis = container;
				break;
			}
		}
		if (selectedAxis == null) {
			startY = y;
			view.getMarkerRectangle().setRect(startX, startY, 0, 0);
			for (Data data : model.getList()) {		// clear selected
				data.setSelected(false);
			}
		}
	}

	public void mouseReleased(MouseEvent arg0) {
		if (selectedAxis == null) {
			for (PlotLine line : view.getPlotLineList()) {
				for (int i = 0; i < line.getxCoordinates().size() - 1; ++i) {
					int startX = line.getxCoordinates().get(i);
					int endX = line.getxCoordinates().get(i + 1);
					int startY = line.getyCoordinates().get(i);
					int endY = line.getyCoordinates().get(i + 1);
					Line2D line2D = new Line2D.Double(startX, startY, endX, endY);
					if (line2D.intersects(view.getMarkerRectangle())) {
						line.getData().setSelected(true);
						break;
					}
				}
			}
			view.getMarkerRectangle().setRect(0, 0, 0, 0);
		}
		selectedAxis = null;
		startX = 0;
		startY = 0;
		view.repaint();
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		if (selectedAxis != null) {
			updateAxis(x);
		} else {
			updateMarker(x, y);
		}
		view.repaint();
	}

	private void updateAxis(int x) {
		double translation = x - startX;
		startX = x;
		selectedAxis.setTranslate(selectedAxis.getTranslate() + translation);
	}

	private void updateMarker(int x, int y) {
		Rectangle2D marker = view.getMarkerRectangle();
		double markerX = startX;
		double markerY = startY;
		double width = Math.abs(x - startX);
		double height = Math.abs(y - startY);

		if (x < startX) {
			markerX = x;
		}

		if (y < startY) {
			markerY = y;
		}
		marker.setRect(markerX, markerY, width, height);
	}

	public void mouseMoved(MouseEvent e) { }

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
}
