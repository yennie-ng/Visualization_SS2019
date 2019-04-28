package infovis.example;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import javax.swing.JPanel;

public class View extends JPanel {
	private Model model = null;

	@Override
	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D)g;
		g2D.scale(1, 1);
        
        
        Rectangle2D rect = new Rectangle2D.Double(50, 50, 300, 100);
		g2D.translate(-50, -50);
		g2D.setColor(Color.RED);
		g2D.fill(rect);
		g2D.scale(2, 2);
		g2D.translate(25, 25);
		g2D.setColor(Color.BLUE);
		g2D.fill(rect);
		g2D.scale(0.5, 0.5);
		g2D.setColor(Color.GREEN);
		g2D.fill(rect);
        
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	
}
