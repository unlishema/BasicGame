
public class Dimensions {

	protected float width, height, depth;

	public Dimensions(final float width, final float height, final float depth) {
		this.set(width, height, depth);
	}

	public void set(final Dimensions dimensions) {
		this.set(dimensions.width, dimensions.height, dimensions.depth);
	}

	public void set(final float width, final float height, final float depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
	}
}
