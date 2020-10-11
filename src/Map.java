import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class Map extends GameObject {
	private final PApplet app;
	private final PImage tex;

	private final int size = 16;

	protected final ArrayList<Node> nodes = new ArrayList<Node>();

	public Map(final PApplet app) {
		this.app = app;
		this.tex = this.app.loadImage("grass_old.jpg");
		for (int x = -this.size / 2; x < this.size / 2; x++) for (int z = -this.size / 2; z < this.size / 2; z++) {
			if (x == -this.size / 2 || x == this.size / 2 - 1 || z == -this.size / 2 || z == this.size / 2 - 1) {
				this.nodes.add(new Node(this.tex, new PVector(x, 0.5f, z), new Dimensions(1.0f, 1.0f, 1.0f)));
				this.nodes.add(new Node(this.tex, new PVector(x, -0.5f, z), new Dimensions(1.0f, 1.0f, 1.0f)));
				this.nodes.add(new Node(this.tex, new PVector(x, -1.5f, z), new Dimensions(1.0f, 1.0f, 1.0f)));
			}
			this.nodes.add(new Node(this.tex, new PVector(x, 1.5f, z), new Dimensions(1.0f, 1.0f, 1.0f)));
			//this.nodes.add(new Node(this.tex, new PVector(x, -2.5f, z), new Dimensions(1.0f, 1.0f, 1.0f)));
		}
	}

	// FIXME Change to use GameObject so we can determine the top most Node using
	// the Player Size also this should fix jumping too I hope
	public float calculateGroundLevel(final PVector position) {
		float closest = Float.MAX_VALUE;
		for (final Node node : this.nodes)
			if (node.position.x - node.size.width / 2 < position.x && position.x < node.position.x + node.size.width / 2
					&& node.position.z - node.size.depth / 2 < position.z
					&& position.z < node.position.z + node.size.depth / 2) {
						final float distance = position.y + 1.5f - (node.position.y - node.size.height / 2);
						if (distance > 0.0f && position.y + 1.5f - closest < distance)
							closest = node.position.y - node.size.height / 2;
					}
		return closest == Float.MAX_VALUE ? 0.0f : closest;
	}

	@Override
	public boolean[] collidesWith(final GameObject obj, final PVector padding) {
		boolean[] mapCollisions = new boolean[] { false, false, false, false, false, false };
		for (final Node node : this.nodes) {
			if (node != null) {
				final float xDist = node.position.x - obj.position.x;
				final float yDist = node.position.y - obj.position.y;
				final float zDist = node.position.z - obj.position.z;

				// FIXME Make it so it can collide with entity
				// Make sure we take velocity into account so we can fix not hitting walls and
				// floor at high speeds notice this needs to be added to GameObject not here

				if ((-1.5f <= yDist && yDist <= 1.5f) && (-1.5f <= xDist && xDist <= 1.5f)
						&& (-1.5f <= zDist && zDist <= 1.5f)) {
					boolean[] nodeCollisions = node.collidesWith(obj, padding);
					if (GameObject.collidesWithAny(nodeCollisions)) {

						// If Node is underneath our feet
						if (0.5f <= yDist && yDist <= 1.5f && node.position.x - node.size.width / 2 <= obj.position.x
								&& obj.position.x <= node.position.x + node.size.width / 2
								&& node.position.z - node.size.depth / 2 <= obj.position.z
								&& obj.position.z <= node.position.z + node.size.depth / 2) {
							mapCollisions[Collisions.FLOOR] = nodeCollisions[Collisions.FLOOR];
						}

						// If Node is same level as us
						if (-1.0f <= yDist && yDist <= 1.0f) {
							if (node.position.x - node.size.width / 2 <= obj.position.x
									&& obj.position.x <= node.position.x + node.size.width / 2) {
								if (nodeCollisions[Collisions.FRONT])
									mapCollisions[Collisions.FRONT] = nodeCollisions[Collisions.FRONT];
								if (nodeCollisions[Collisions.BACK])
									mapCollisions[Collisions.BACK] = nodeCollisions[Collisions.BACK];
							}
							if (node.position.z - node.size.depth / 2 <= obj.position.z
									&& obj.position.z <= node.position.z + node.size.depth / 2) {
								if (nodeCollisions[Collisions.LEFT])
									mapCollisions[Collisions.LEFT] = nodeCollisions[Collisions.LEFT];
								if (nodeCollisions[Collisions.RIGHT])
									mapCollisions[Collisions.RIGHT] = nodeCollisions[Collisions.RIGHT];
							}
						}

						// If Node is above our head
						if (-0.5f <= yDist && yDist <= -1.5f && node.position.x - node.size.width / 2 <= obj.position.x
								&& obj.position.x <= node.position.x + node.size.width / 2
								&& node.position.z - node.size.depth / 2 <= obj.position.z
								&& obj.position.z <= node.position.z + node.size.depth / 2) {
							mapCollisions[Collisions.CEIL] = nodeCollisions[Collisions.CEIL];
						}
					}
				}
			}
		}
		return mapCollisions;
	}

	@Override
	protected void redraw(final PGraphics g) {
		g.pushMatrix();
		for (final Node node : this.nodes) if (node != null) node.draw(g);
		g.popMatrix();
	}
}
