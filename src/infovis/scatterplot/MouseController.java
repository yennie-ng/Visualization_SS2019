package infovis.scatterplot;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

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
		// Iterator<Data> iter = model.iterator();
		// view.getMarkerRectangle().setRect(x,y,w,h);
		// view.repaint();
		//TODO:
	}

	public void mouseReleased(MouseEvent arg0) {
		//TODO:
	}

	public void mouseDragged(MouseEvent arg0) {
		// view.repaint();
		//TODO:
	}

	public void mouseMoved(MouseEvent arg0) {
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public void setView(View view) {
		this.view = view;
	}

}
