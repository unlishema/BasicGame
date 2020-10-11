import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class Node extends GameObject {

	private final PImage tex;

	public Node(final PImage tex, final PVector position, final Dimensions size) {
		super(position, size);
		this.tex = tex;

	}

	@Override
	protected void redraw(final PGraphics g) {
		g.pushMatrix();
		g.noStroke();
		g.fill(35, 180, 35);

		final PVector min = new PVector(this.position.x - this.size.width / 2, this.position.y - this.size.height / 2,
				this.position.z - this.size.depth / 2);
		final PVector max = new PVector(this.position.x + this.size.width / 2, this.position.y + this.size.height / 2,
				this.position.z + this.size.depth / 2);

		g.beginShape(PGraphics.QUADS);
		g.texture(tex);

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
