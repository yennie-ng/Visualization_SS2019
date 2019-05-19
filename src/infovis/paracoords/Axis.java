package infovis.paracoords;

import java.awt.geom.Rectangle2D;

public class Axis {
	private int x;
	private int labelIndex;
	private double translate;
	private double min;
	private double max;
	private double step;
	private Rectangle2D rect;

    public Axis(int x, int labelIndex, 
                double min, double max, 
                double step, Rectangle2D rect) {
		this.x = x;
		this.labelIndex = labelIndex;
		this.min = min;
		this.max = max;
		this.step = step;
		this.rect = rect;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getLabelIndex() {
		return labelIndex;
	}

	public void setLabelIndex(int labelIndex) {
		this.labelIndex = labelIndex;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getStep() {
		return step;
	}

	public void setStep(double step) {
		this.step = step;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getTranslate() {
		return translate;
	}

	public void setTranslate(double offset) {
		this.translate = offset;
	}

	public Rectangle2D getRect() {
		return rect;
	}

	public void setRect(Rectangle2D rect) {
		this.rect = rect;
	}
}