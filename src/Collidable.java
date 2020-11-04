import processing.core.PVector;

public abstract interface Collidable {
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int FLOOR = 2;
	public static final int CEIL = 3;
	public static final int FRONT = 4;
	public static final int BACK = 5;

	public static boolean collidesWithAny(final boolean[] collisions) {
		boolean collide = false;
		for (final boolean c : collisions) if (!collide && c) collide = c;
		return collide;
	}

	public static boolean collidesWithAll(final boolean[] collisions) {
		boolean collide = true;
		for (final boolean c : collisions) if (collide && !c) collide = c;
		return collide;
	}

	public default boolean[] collidesWith(final Collidable obj, final PVector velocity) {
		// Get the Min and Max Position of First Object (A)
		final PVector minA = new PVector(this.getPosition().x - this.getSize().width / 2,
				this.getPosition().y - this.getSize().height / 2, this.getPosition().z - this.getSize().depth / 2);
		final PVector maxA = new PVector(this.getPosition().x + this.getSize().width / 2,
				this.getPosition().y + this.getSize().height / 2, this.getPosition().z + this.getSize().depth / 2);

		// Get the Min and Max Position of Second Object (B)
		final PVector minB = new PVector(obj.getPosition().x - obj.getSize().width / 2,
				obj.getPosition().y - obj.getSize().height / 2, obj.getPosition().z - obj.getSize().depth / 2);
		final PVector maxB = new PVector(obj.getPosition().x + obj.getSize().width / 2,
				obj.getPosition().y + obj.getSize().height / 2, obj.getPosition().z + obj.getSize().depth / 2);

		// X Collision
		final boolean xPos = this.getPosition().x <= minB.x && minB.x <= maxA.x; // Front
		final boolean xNeg = this.getPosition().x >= maxB.x && maxB.x >= minA.x; // Back
		final boolean xAny = (minA.x < minB.x && minB.x < maxA.x) || (minA.x < maxB.x && maxB.x < maxA.x)
				|| (minB.x < minA.x && minA.x < maxB.x) || (minB.x < maxA.x && maxA.x < maxB.x);

		// Y Collision
		final boolean yNeg = this.getPosition().y >= maxB.y && maxB.y >= minA.y; // Floor
		final boolean yPos = this.getPosition().y <= minB.y && minB.y <= maxA.y; // Ceiling
		final boolean yAny = (minA.y <= minB.y && minB.y <= maxA.y) || (minA.y <= maxB.y && maxB.y <= maxA.y)
				|| (minB.y <= minA.y && minA.y <= maxB.y) || (minB.y <= maxA.y && maxA.y <= maxB.y);

		// Z Collision
		final boolean zPos = this.getPosition().z <= minB.z && maxA.z >= minB.z; // Left
		final boolean zNeg = this.getPosition().z >= maxB.z && maxB.z >= minA.z; // Right
		final boolean zAny = (minA.z < minB.z && minB.z < maxA.z) || (minA.z < maxB.z && maxB.z < maxA.z)
				|| (minB.z < minA.z && minA.z < maxB.z) || (minB.z < maxA.z && maxA.z < maxB.z);

		if (velocity.x > 1.0f || velocity.y > 1.0f || velocity.z > 1.0f) {}

		// Returns a Boolean[] { Left, Right, Floor, Ceiling, Front, Back }
		boolean[] collisions = new boolean[6];

		collisions[FRONT] = xNeg && yAny && zAny;
		collisions[BACK] = xPos && yAny && zAny;

		collisions[FLOOR] = xAny && yNeg && zAny;
		collisions[CEIL] = xAny && yPos && zAny;

		collisions[LEFT] = xAny && yAny && zPos;
		collisions[RIGHT] = xAny && yAny && zNeg;

		// Returns a boolean for each direction only x, y, z
		return collisions;
	}

	public default boolean contains(final PVector vector) {
		// Get the Min and Max Position of this Collidable
		final PVector min = new PVector(this.getPosition().x - this.getSize().width / 2,
				this.getPosition().y - this.getSize().height / 2, this.getPosition().z - this.getSize().depth / 2);
		final PVector max = new PVector(this.getPosition().x + this.getSize().width / 2,
				this.getPosition().y + this.getSize().height / 2, this.getPosition().z + this.getSize().depth / 2);

		final boolean xAny = min.x <= vector.x && vector.x <= max.x;
		final boolean yAny = min.y <= vector.y && vector.y <= max.y;
		final boolean zAny = min.z <= vector.x && vector.x <= max.z;
		return xAny && yAny && zAny;
	}

	public abstract PVector getPosition();

	public abstract Dimensions getSize();
}
