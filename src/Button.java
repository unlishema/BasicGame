

import java.awt.Rectangle;

import processing.core.PGraphics;

public abstract class Button extends GameObject {

	protected final Rectangle rect;

	protected String title;

	public Button(final String title, final int x, final int y, final int width, final int height) {
		this.title = title;
		this.rect = new Rectangle(x, y, width, height);
	}

	public boolean contains(final int x, final int y) {
		return this.rect.contains(x, y);
	}

	public abstract void clicked();

	@Override
	protected void redraw(final PGraphics g) {
		g.pushMatrix();

		// Draw the Button Background
		g.fill(35, 35, 35, 150);
		g.rect(this.rect.x, this.rect.y, this.rect.width, this.rect.height);

		// Draw the Title
		g.fill(155);
		g.textAlign(PGraphics.LEFT, PGraphics.TOP);
		g.textSize(45);
		g.text(this.title, this.rect.x + 3, this.rect.y);

		g.popMatrix();
	}
}
