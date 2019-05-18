package infovis.scatterplot;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class MouseController implements MouseListener, MouseMotionListener {

	private Model model = null;
	private View view = null;
	private double startX = 0;
	private double startY = 0;
	private MatrixCell selectedCell;

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
		startX = arg0.getX();
		startY = arg0.getY();
		view.getMarkerRect().setRect(startX, startY, 0, 0);

		for (MatrixCell cell : view.getCells()) {	// find the marked cell
			if (cell.getRect().contains(new Point2D.Double(startX, startY))) {
				selectedCell = cell;
				break;
			}
		}

		for (Data data : model.getList()) {			// clear old data
			data.setSelected(false);
		}
	}

	public void mouseReleased(MouseEvent arg0) {
		for (PlotPoint point : selectedCell.getPoints()) {
			if (view.getMarkerRect().contains(new Point2D.Double(point.getX(), point.getY()))) {
				this.setSelected(point);
			}
		}
		view.getMarkerRect().setRect(0, 0, 0, 0);
		view.repaint();
	}

	public void mouseDragged(MouseEvent arg0) {
		int x = arg0.getX();
		int y = arg0.getY();

		Rectangle2D marker = view.getMarkerRect();
		double markerX = startX;
		double markerY = startY;
		double w = Math.abs(x - startX);
		double h = Math.abs(y - startY);
		double maxW = 0;
		double maxH = 0;
		Rectangle2D cellRect = selectedCell.getRect();

		// set x and y to top left corner if dragged outside
		if (x < cellRect.getX()) {
			x = (int) Math.round(cellRect.getX());
		}

		if (y < cellRect.getY()) {
			y = (int) Math.round(cellRect.getY());
		}

		if (x > startX) {
			// draw to the right
			maxW = cellRect.getX() + cellRect.getWidth() - startX;
		} else {
			// draw to the left
			markerX = x;
			maxW= startX - cellRect.getX();
		}

		if (y > startY) {
			// draw downwards
			maxH = cellRect.getY() + cellRect.getHeight() - startY;
		} else {
			// draw upwards
			markerY = y;
			maxH = startY - cellRect.getY();
		}

		// keep marker within cell
		if (w > maxW) {
			w = maxW;
		}

		if (h > maxH) {
			h = maxH;
		}

		marker.setRect(markerX, markerY, w, h);
		view.repaint();
	}

	public void mouseMoved(MouseEvent arg0) {
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public void setView(View view) {
		this.view = view;
	}

	private void setSelected(PlotPoint point) {
		for (Data data : model.getList()) {
			if (data.getValue(selectedCell.getColumn()) == point.getHorizontal() 
			&& data.getValue(selectedCell.getRow()) == point.getVertical()) {
				data.setSelected(true);
			}
		}
	}

}
