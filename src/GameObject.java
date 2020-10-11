
import processing.core.PGraphics;
import processing.core.PVector;

public abstract class GameObject {

	public static interface Collisions {
		public static final int LEFT = 0;
		public static final int RIGHT = 1;
		public static final int FLOOR = 2;
		public static final int CEIL = 3;
		public static final int FRONT = 4;
		public static final int BACK = 5;
	}

	protected static boolean DRAW_BOUNDARIES = false;

	protected PVector position = null;
	protected Dimensions size = null;

	public GameObject() {
	}

	public GameObject(final PVector position, final Dimensions size) {
		this.position = new PVector(0.0f, 0.0f, 0.0f);
		this.position.set(position);
		this.size = size;
	}

	public static final boolean collidesWithAny(final boolean[] collisions) {
		boolean collide = false;
		for (final boolean c : collisions)
			if (!collide && c)
				collide = c;
		return collide;
	}

	public static final boolean collidesWithAll(final boolean[] collisions) {
		boolean collide = true;
		for (final boolean c : collisions)
			if (collide && !c)
				collide = c;
		return collide;
	}

	public boolean[] collidesWith(final GameObject obj, final PVector padding) {
		// If this Object was not properly defined then auto return false
		if (!this.hasBoundaryBox())
			return null;

		// Get the Min and Max Position of First Object (A)
		final PVector minA = new PVector(this.position.x - this.size.width / 2, this.position.y - this.size.height / 2,
				this.position.z - this.size.depth / 2);
		final PVector maxA = new PVector(this.position.x + this.size.width / 2, this.position.y + this.size.height / 2,
				this.position.z + this.size.depth / 2);

		// Get the Min and Max Position of Second Object (B) with padding
		final PVector minB = new PVector(obj.position.x + padding.x - obj.size.width / 2,
				obj.position.y + padding.y - obj.size.height / 2, obj.position.z + padding.z - obj.size.depth / 2);
		final PVector maxB = new PVector(obj.position.x - padding.x + obj.size.width / 2,
				obj.position.y - padding.y + obj.size.height / 2, obj.position.z - padding.z + obj.size.depth / 2);

		// X Collision
		final boolean xPos = this.position.x <= minB.x && minB.x <= maxA.x; // Right
		final boolean xNeg = this.position.x >= maxB.x && maxB.x >= minA.x; // Left
		final boolean xAny = (minA.x < minB.x && minB.x < maxA.x) || (minA.x < maxB.x && maxB.x < maxA.x)
				|| (minB.x < minA.x && minA.x < maxB.x) || (minB.x < maxA.x && maxA.x < maxB.x);

		// Y Collision
		final boolean yNeg = this.position.y >= maxB.y && maxB.y >= minA.y; // Floor
		final boolean yPos = this.position.y < minB.y && minB.y < maxA.y; // Ceiling
		final boolean yAny = (minA.y <= minB.y && minB.y <= maxA.y) || (minA.y <= maxB.y && maxB.y <= maxA.y)
				|| (minB.y <= minA.y && minA.y <= maxB.y) || (minB.y <= maxA.y && maxA.y <= maxB.y);

		// Z Collision
		final boolean zPos = this.position.z <= minB.z && maxA.z >= minB.z; // Front
		final boolean zNeg = this.position.z >= maxB.z && maxB.z >= minA.z; // Back
		final boolean zAny = (minA.z < minB.z && minB.z < maxA.z) || (minA.z < maxB.z && maxB.z < maxA.z)
				|| (minB.z < minA.z && minA.z < maxB.z) || (minB.z < maxA.z && maxA.z < maxB.z);

		// Returns a Boolean[] { Left, Right, Floor, Ceiling, Front, Back }
		boolean[] collisions = new boolean[6];

		collisions[Collisions.LEFT] = xNeg && yAny && zAny;
		collisions[Collisions.RIGHT] = xPos && yAny && zAny;

		collisions[Collisions.FLOOR] = xAny && yNeg && zAny;
		collisions[Collisions.CEIL] = xAny && yPos && zAny;

		collisions[Collisions.FRONT] = xAny && yAny && zPos;
		collisions[Collisions.BACK] = xAny && yAny && zNeg;

		// Returns a boolean for each direction only x, y, z
		return collisions;
	}

	public boolean hasBoundaryBox() {
		return this.position != null && this.size != null;
	}

	protected abstract void redraw(final PGraphics g);

	public void draw(final PGraphics g) {
		// Call their internal Draw command
		this.redraw(g);

		// Draw Boundary Box
		if (GameObject.DRAW_BOUNDARIES && this.hasBoundaryBox()) {
			g.pushMatrix();

			// Set Colors
			g.stroke(0, 0, 255);
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
