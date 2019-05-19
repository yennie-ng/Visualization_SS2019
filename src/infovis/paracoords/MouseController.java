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
		this.startX = e.getX();
		int y = e.getY();
		
		for (int i = 0; i < view.getAxisList().size(); i++) {
			Axis container = view.getAxisList().get(i);
			if (container.getRect().contains(new Point2D.Double(startX, y))) {
				this.selectedAxis = container;
				break;
			}
		}
		if (this.selectedAxis == null) {
			this.startY = y;
			view.getMarkerRectangle().setRect(startX, startY, 0, 0);
			for (Data data : this.model.getList()) {
				data.setSelected(false);
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (this.selectedAxis == null) {
			for (PlotLine line : view.getPlotLineList()) {
				for (int index = 0; index < line.getXCoordinates().size() - 1; index++) {
					int startX = line.getXCoordinates().get(index);
					int endX = line.getXCoordinates().get(index + 1);
					int startY = line.getYCoordinates().get(index);
					int endY = line.getYCoordinates().get(index + 1);
					Line2D line2D = new Line2D.Double(startX, startY, endX, endY);

					if (line2D.intersects(view.getMarkerRectangle())) {
						line.getData().setSelected(true);
						break;
					}
				}
			}
			view.getMarkerRectangle().setRect(0, 0, 0, 0);
		}
		this.selectedAxis = null;
		this.startX = 0;
		this.startY = 0;
		view.repaint();
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		if (selectedAxis != null) {
			this.updateAxis(x);
		} else {
			this.updateMarker(x, y);
		}
		view.repaint();
	}

	private void updateAxis(int x) {
		double translation = x - this.startX;
		this.startX = x;
		this.selectedAxis.setTranslate(this.selectedAxis.getTranslate() + translation);
	}

	private void updateMarker(int x, int y) {
		Rectangle2D marker = view.getMarkerRectangle();
		double markerX = this.startX;
		double markerY = this.startY;
		double width = Math.abs(x - this.startX);
		double height = Math.abs(y - this.startY);
		
		if (x < this.startX) {
			markerX = x;
		}
		if (y < this.startY) {
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
