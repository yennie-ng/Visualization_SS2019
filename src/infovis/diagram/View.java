package infovis.diagram;

import infovis.diagram.elements.Element;
import infovis.diagram.elements.Vertex;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JPanel;

public class View extends JPanel {
	private static final long serialVersionUID = 1L;
	private Model model = null;
	private Color color = Color.BLUE;
	private double scale = 1;
	private double translateX = 0;
	private double translateY = 0;
	private Rectangle2D marker = new Rectangle2D.Double();
	private Rectangle2D overviewRect = new Rectangle2D.Double();
	private final double OverviewScaleValue = 3.5; 

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void paint(Graphics g) {

		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.clearRect(0, 0, getWidth(), getHeight());
		paintDiagram(model, g2D, this.scale);
		paintOverview(g2D);
	}

	private void paintDiagram(Model model, Graphics2D g2D, double scaling) {
		g2D.scale(scaling, scaling);
		g2D.translate(-this.translateX, -this.translateY);
		paintDiagram(model, g2D);
	}

	private void paintDiagram(Model model, Graphics2D g2D) {
		for (Element element : model.getElements()) {
			element.paint(g2D);
		}
	}

	private void paintOverview(Graphics2D g2D) {
		g2D.scale(1, 1);
		g2D.setStroke(new BasicStroke((float)(1 / scale)));
		
		double w = getWidth() / OverviewScaleValue / scale;
		double h = getHeight() / OverviewScaleValue / scale;
		double x = getWidth() / scale - w - 5;
		double y = 5;
		this.overviewRect = new Rectangle2D.Double(x, y, w, h);

		g2D.draw(overviewRect);
		g2D.setPaint(Color.WHITE);
		g2D.fill(overviewRect);

		g2D.translate(x, y);	// translate to coordinate system of overview rectangle to draw small diagram
		paintDiagram(scaledModel(model, 
								OverviewScaleValue * this.scale, 
								overviewRect), g2D);
		g2D.translate(-x, -y);	// translate back to root coordinate system
		makeMarker(g2D);		// draw marker window after main diagram and it scaled version are drawed
	}

	private void makeMarker(Graphics2D g2D) {
		if (overviewRect != null) {
			double difference = (translateX / scale) / OverviewScaleValue;
			double w = overviewRect.getWidth() / this.scale;
			double h = overviewRect.getHeight() / this.scale;
			double x = overviewRect.getX() + difference;
			double y = overviewRect.getY() + difference;
			marker = new Rectangle2D.Double(x, y, w, h);

			g2D.setColor(Color.RED);
			g2D.draw(marker);
		}
	}

	private Model scaledModel(Model model, double scaleValue, Rectangle2D frame) {
		Model newModel = new Model();
		for (Element element: model.getElements()) {
			try { 
				Vertex vertex = (Vertex) element;
				Vertex newElement = new Vertex(vertex.getX() / scaleValue, 
												vertex.getY() / scaleValue, 
												vertex.getWidth() / scaleValue, 
												vertex.getHeight() / scaleValue);
				// if the element doesn't completely stay inside the current overview frame, won't add
				if (newElement.getX() + newElement.getWidth() < frame.getWidth() && 
					newElement.getY() + newElement.getHeight() < frame.getHeight())
					newModel.addElement((Element) newElement);
			} catch (Exception e) {}
		}
		return newModel;
	}


	public void setScale(double scale) {
		this.scale = scale;
	}

	public double getScale() {
		return scale;
	}

	public double getTranslateX() {
		return translateX;
	}

	public void setTranslateX(double translateX) {
		this.translateX = translateX;
	}

	public double getTranslateY() {
		return translateY;
	}

	public void setTranslateY(double tansslateY) {
		this.translateY = tansslateY;
	}

	public void updateTranslation(double x, double y) {
		setTranslateX(x);
		setTranslateY(y);
	}

	public void updateMarker(int x, int y) {
		marker.setRect(x, y, 16, 10);
	}

	public Rectangle2D getMarker() {
		return marker;
	}

	public Rectangle2D getOverview() {
		return overviewRect;
	}

	public boolean markerContains(int x, int y) {
		return marker.contains(x, y);
	}
}
