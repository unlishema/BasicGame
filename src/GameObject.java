
import processing.core.PGraphics;
import processing.core.PVector;

public abstract class GameObject {
	protected static boolean DRAW_BOUNDARIES = false;

	protected PVector position = new PVector(0.0f, 0.0f, 0.0f);
	protected Dimensions size = new Dimensions(0.0f, 0.0f, 0.0f);

	public GameObject() {
	}

	public GameObject(final PVector position, final Dimensions size) {
		this.position.set(position);
		this.size.set(size);
	}

	public boolean hasBoundaryBox() {
		return this.size.width > 0.0f || this.size.height > 0.0f || this.size.depth > 0.0f;
	}

	protected abstract void redraw(final PGraphics g);

	public void draw(final PGraphics g) {
		// Call their internal Draw command
		this.redraw(g);

		this.drawBoundaries(g);
	}

	protected void drawBoundaries(PGraphics g) {
		// Draw Boundary Box
		if (GameObject.DRAW_BOUNDARIES && this.hasBoundaryBox()) {
			g.pushMatrix();

			// Set Colors
			g.stroke(0, 0, 255);
			g.strokeWeight(1);
			g.noFill();

			final PVector min = new PVector(this.position.x - this.size.width / 2,
					this.position.y - this.size.height / 2, this.position.z - this.size.depth / 2);
			final PVector max = new PVector(this.position.x + this.size.width / 2,
					this.position.y + this.size.height / 2, this.position.z + this.size.depth / 2);

			// Draw Entire Outline
			g.beginShape(PGraphics.QUADS);

			// +Z "front" face
			g.vertex(min.x, min.y, max.z, 0, 0);
			g.vertex(max.x, min.y, max.z, 1, 0);
			g.vertex(max.x, max.y, max.z, 1, 1);
			g.vertex(min.x, max.y, max.z, 0, 1);

			// -Z "back" face
			g.vertex(max.x, min.y, min.z, 0, 0);
			g.vertex(min.x, min.y, min.z, 1, 0);
			g.vertex(min.x, max.y, min.z, 1, 1);
			g.vertex(max.x, max.y, min.z, 0, 1);

			// +Y "bottom" face
			g.vertex(min.x, max.y, max.z, 0, 0);
			g.vertex(max.x, max.y, max.z, 1, 0);
			g.vertex(max.x, max.y, min.z, 1, 1);
			g.vertex(min.x, max.y, min.z, 0, 1);

			// -Y "top" face
			g.vertex(min.x, min.y, min.z, 0, 0);
			g.vertex(max.x, min.y, min.z, 1, 0);
			g.vertex(max.x, min.y, max.z, 1, 1);
			g.vertex(min.x, min.y, max.z, 0, 1);

			// +X "right" face
			g.vertex(max.x, min.y, max.z, 0, 0);
			g.vertex(max.x, min.y, min.z, 1, 0);
			g.vertex(max.x, max.y, min.z, 1, 1);
			g.vertex(max.x, max.y, max.z, 0, 1);

			// -X "left" face
			g.vertex(min.x, min.y, min.z, 0, 0);
			g.vertex(min.x, min.y, max.z, 1, 0);
			g.vertex(min.x, max.y, max.z, 1, 1);
			g.vertex(min.x, max.y, min.z, 0, 1);

			g.endShape();

			g.popMatrix();
		}
	}
}
