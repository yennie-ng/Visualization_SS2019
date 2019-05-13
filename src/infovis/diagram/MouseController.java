package infovis.diagram;

import infovis.debug.Debug;
import infovis.diagram.elements.DrawingEdge;
import infovis.diagram.elements.Edge;
import infovis.diagram.elements.Element;
import infovis.diagram.elements.GroupingRectangle;
import infovis.diagram.elements.None;
import infovis.diagram.elements.Vertex;
import infovis.diagram.layout.Fisheye;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MouseController implements MouseListener, MouseMotionListener {
	private Model model;
	private View view;
	private Element selectedElement = new None();
	private double mouseOffsetX;
	private double mouseOffsetY;
	private boolean edgeDrawMode = false;
	private DrawingEdge drawingEdge = null;
	private boolean fisheyeMode;
	private GroupingRectangle groupRectangle;
	private boolean selectingMarker = false;
	private boolean selectingOverviewTopBorder = false;
	private double currentX = 0;
	private double currentY = 0;

	/*
	 * Getter And Setter
	 */
	public Element getSelectedElement() {
		return selectedElement;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model diagramModel) {
		this.model = diagramModel;
	}

	public View getView() {
		return view;
	}

	public void setView(View diagramView) {
		this.view = diagramView;
	}

	/*
	 * Implements MouseListener
	 */
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		double scale = view.getScale();

		if (e.getButton() == MouseEvent.BUTTON3) {
			/*
			 * add grouped elements to the model
			 */
			Vertex groupVertex = (Vertex) getElementContainingPosition(x / scale, y / scale);
			for (Iterator<Vertex> iter = groupVertex.getGroupedElements().iteratorVertices(); iter.hasNext();) {
				model.addVertex(iter.next());
			}
			for (Iterator<Edge> iter = groupVertex.getGroupedElements().iteratorEdges(); iter.hasNext();) {
				model.addEdge(iter.next());
			}
			/*
			 * remove elements
			 */
			List<Edge> edgesToRemove = new ArrayList<Edge>();
			for (Iterator<Edge> iter = model.iteratorEdges(); iter.hasNext();) {
				Edge edge = iter.next();
				if (edge.getSource() == groupVertex || edge.getTarget() == groupVertex) {
					edgesToRemove.add(edge);
				}
			}
			model.removeEdges(edgesToRemove);
			model.removeElement(groupVertex);
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		double scale = view.getScale();

		if (edgeDrawMode) {
			if (drawingEdge != null) {
				drawingEdge = new DrawingEdge((Vertex) getElementContainingPosition(x / scale, y / scale));
				model.addElement(drawingEdge);
			}
		} else {
			selectingMarker = isInRect(view.getMarker(), scale, x, y);
			//TODO: selectingOverviewTopBorder = ...;
			if (selectingMarker) {
				currentX = x;
				currentY = y;
			} else {
				selectedElement = getElementContainingPosition(x / scale, y / scale);
				/*
				 * calculate offset
				 */
				mouseOffsetX = x - selectedElement.getX() * scale;
				mouseOffsetY = y - selectedElement.getY() * scale;
				currentX = 0;
				currentY = 0;
			}
		}
	}

	private boolean isInRect(Rectangle2D rect, double scale, int currentX, int currentY) {
		double x = (rect.getX() - view.getTranslateX()) * scale;
		double y = (rect.getY() - view.getTranslateY()) * scale;
		double w = rect.getWidth() * scale;
		double h = rect.getHeight() * scale;
		return currentX >= x && currentX <= x + w * scale && currentY >= y && currentY <= y + h * scale;
	}

	public void mouseReleased(MouseEvent arg0) {
		selectingMarker = false;
		selectingOverviewTopBorder = false;
		int x = arg0.getX();
		int y = arg0.getY();

		if (drawingEdge != null) {
			Element to = getElementContainingPosition(x, y);
			model.addEdge(new Edge(drawingEdge.getFrom(), (Vertex) to));
			model.removeElement(drawingEdge);
			drawingEdge = null;
		}
		if (groupRectangle != null) {
			Model groupedElements = new Model();
			for (Iterator<Vertex> iter = model.iteratorVertices(); iter.hasNext();) {
				Vertex vertex = iter.next();
				if (groupRectangle.contains(vertex.getShape().getBounds2D())) {
					Debug.p("Vertex found");
					groupedElements.addVertex(vertex);
				}
			}
			if (!groupedElements.isEmpty()) {
				model.removeVertices(groupedElements.getVertices());

				Vertex groupVertex = new Vertex(groupRectangle.getCenterX(), groupRectangle.getCenterX());
				groupVertex.setColor(Color.ORANGE);
				groupVertex.setGroupedElements(groupedElements);
				model.addVertex(groupVertex);

				List<Edge> newEdges = new ArrayList<Edge>();
				for (Iterator<Edge> iter = model.iteratorEdges(); iter.hasNext();) {
					Edge edge = iter.next();
					if (groupRectangle.contains(edge.getSource().getShape().getBounds2D())
							&& groupRectangle.contains(edge.getTarget().getShape().getBounds2D())) {
						groupVertex.getGroupedElements().addEdge(edge);
						Debug.p("add Edge to groupedElements");
						// iter.remove(); // Warum geht das nicht!
					} else if (groupRectangle.contains(edge.getSource().getShape().getBounds2D())) {
						groupVertex.getGroupedElements().addEdge(edge);
						newEdges.add(new Edge(groupVertex, edge.getTarget()));
					} else if (groupRectangle.contains(edge.getTarget().getShape().getBounds2D())) {
						groupVertex.getGroupedElements().addEdge(edge);
						newEdges.add(new Edge(edge.getSource(), groupVertex));
					}
				}
				model.addEdges(newEdges);
				model.removeEdges(groupedElements.getEdges());
			}
			model.removeElement(groupRectangle);
			groupRectangle = null;
		}
		view.repaint();
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		double scale = view.getScale();
		if (selectingMarker) {
			double overviewScale = view.overviewScaleValue;
			double transX = (x - currentX) * overviewScale + view.getTranslateX();
			double transY = (y - currentY) * overviewScale + view.getTranslateY();
			view.updateTranslation(transX, transY);
			//TODO: updateFisheyeFocus(...);
			mouseMoved(e);
		}
		currentX = x;
		currentY = y;
		if (fisheyeMode) {
			// TODO: 4.1

			view.repaint();
		} else if (edgeDrawMode) {
			drawingEdge.setX(e.getX());
			drawingEdge.setY(e.getY());
		} else if (selectedElement != null) {
			selectedElement.updatePosition((e.getX() - mouseOffsetX) / scale, (e.getY() - mouseOffsetY) / scale);
		}
		view.repaint();
	}

	public void mouseMoved(MouseEvent e) {
		if (fisheyeMode) {
			//TODO: 4.1
		}
	}

	public boolean isDrawingEdges() {
		return edgeDrawMode;
	}

	public void setDrawingEdges(boolean drawingEdges) {
		this.edgeDrawMode = drawingEdges;
	}

	public void setFisheyeMode(boolean b) {
		fisheyeMode = b;
		if (b) {
			Debug.p("new Fisheye Layout");
			/*
			 * handle fish eye initial call
			 */
			view.repaint();
		} else {
			Debug.p("new Normal Layout");
			view.setModel(model);
			view.repaint();
		}
	}

	/*
	 * private Methods
	 */
	private Element getElementContainingPosition(double x, double y) {
		Element currentElement = new None();
		Iterator<Element> iter = getModel().iterator();
		while (iter.hasNext()) {
			Element element = iter.next();
			if (element.contains(x, y))
				currentElement = element;
		}
		return currentElement;
	}

}
