package infovis.diagram.layout;

import infovis.diagram.Model;
import infovis.diagram.View;
import infovis.diagram.elements.Element;
import infovis.diagram.elements.Vertex;

public class Fisheye implements Layout{
	protected final int magnificationFactor = 4;

	public void setMouseCoords(int x, int y, View view) { }

	public Model transform(Model model, View view) {
		Model fishEyeModel = new Model();
		for (Element element : model.getElements()) {
			Vertex current = (Vertex) element;
			double ratio = current.getWidth() / current.getHeight();
			double boundsX = current.getX() + current.getWidth();
			double topLeftX = getFisheyeCoordinate(current.getX(), view.getWidth(), view.getFocusX());
			double rightX = getFisheyeCoordinate(boundsX, view.getWidth(), view.getFocusX());
			double boundsY = current.getY() + current.getHeight();
			double topLeftY = getFisheyeCoordinate(current.getY(), view.getHeight(), view.getFocusY());
			double bottomY = getFisheyeCoordinate(boundsY, view.getHeight(), view.getFocusY());
			double width = rightX - topLeftX;
			double height = bottomY - topLeftY;
			double heightByWidth = width / ratio;
			double widthByHeight = height * ratio;
			// Select minimum size and preserve ratio
			if (width < widthByHeight)
				height = heightByWidth;
			else
				width = widthByHeight;
			Vertex newVertex = new Vertex(topLeftX, topLeftY, width, height);
			fishEyeModel.addVertex(newVertex);
		}
		return fishEyeModel;
	}

	private double getFisheyeCoordinate(double original, double boundary, double focus) {
		double dNorm = original - focus;
		double dMax = (dNorm >= 0) ? boundary - focus : 0 - focus;
		double temp = dNorm / dMax;
		return focus + (((magnificationFactor + 1) * temp) / ((magnificationFactor * temp) + 1) * dMax);
	}
	
}
