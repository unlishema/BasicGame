import processing.core.PVector;

public abstract interface Collidable {
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int FLOOR = 2;
	public static final int CEIL = 3;
	public static final int FRONT = 4;
	public static final int BACK = 5;

	public abstract boolean[] collidesWith(final Collidable obj, final PVector velocity);

	public abstract boolean contains(final PVector vector);

	public abstract PVector getPosition();

	public abstract Dimensions getSize();
}
