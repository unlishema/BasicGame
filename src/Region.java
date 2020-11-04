import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class Region extends GameObject implements Collidable {
	protected static final int WIDTH = 10, HEIGHT = 10, DEPTH = 10;

	protected final PShape mesh;
	protected final Node[][][] nodes = new Node[Region.WIDTH][Region.HEIGHT][Region.DEPTH];

	protected boolean needsRegen = true;

	protected Region(final PApplet app, final PVector position) {
		super(position, new Dimensions(0.0f, 0.0f, 0.0f));
		this.mesh = app.createShape();

		// Create a Blank Chunk
		for (int x = 0; x < Region.WIDTH; x++) {
			for (int y = 0; y < Region.HEIGHT; y++) {
				for (int z = 0; z < Region.DEPTH; z++) {
					final PVector nodePos = new PVector(x + this.position.x * Region.WIDTH,
							y + this.position.y * Region.HEIGHT, z + this.position.z * Region.DEPTH);
					this.nodes[x][y][z] = new Node(nodePos, new Dimensions(1.0f, 1.0f, 1.0f));

					// DEBUG Remove Later
					if (y == 0) this.nodes[x][y][z].textureMap = new GenericTextureMap();
				}
			}
		}
	}

	/**
	 * Check and see if this Region contains the Coordinates in question
	 * 
	 * @param x The X coordinate inside the region
	 * @param y The Y coordinate inside the region
	 * @param z The Z coordinate inside the region
	 * @return True if node exists in this Region, otherwise false
	 */
	public boolean containsCoors(final int x, final int y, final int z) {
		return 0 <= x && x < Region.WIDTH && 0 <= y && y < Region.HEIGHT && 0 <= z && z < Region.DEPTH;
	}

	/**
	 * Check and see if this Region contains the World Coordinates in question
	 * 
	 * @param x The X coordinate from the world
	 * @param y The Y coordinate from the world
	 * @param z The Z coordinate from the world
	 * @return True if node exists in this Region, otherwise false
	 */
	public boolean containsWorldCoors(final int x, final int y, final int z) {
		return this.containsCoors((int) (x - this.position.x * Region.WIDTH),
				(int) (y - this.position.y * Region.HEIGHT), (int) (z - this.position.z * Region.DEPTH));
	}

	/**
	 * Get a Node from this region based on coordinates inside of region
	 * 
	 * @param x The X coordinate inside the region
	 * @param y The Y coordinate inside the region
	 * @param z The Z coordinate inside the region
	 * @return The Node you requested or null if not found
	 */
	public Node getNode(final int x, final int y, final int z) {
		if (this.containsCoors(x, y, z)) return this.nodes[x][y][z];
		return null;
	}

	/**
	 * Get a Node from this region based on coordinates from the world
	 * 
	 * @param x The X coordinate from the world
	 * @param y The Y coordinate from the world
	 * @param z The Z coordinate from the world
	 * @return The Node you requested or null if not found
	 */
	public Node getNodeByWorldCoors(final int x, final int y, final int z) {
		return this.getNode((int) (x - this.position.x * Region.WIDTH), (int) (y - this.position.y * Region.HEIGHT),
				(int) (z - this.position.z * Region.DEPTH));
	}

	/**
	 * Regenerate the Mesh for this Region if it needs Regenerated
	 * 
	 * @param texture The Texture for this region to use
	 */
	public void regenerateMesh(final PImage texture) {
		if (!this.needsRegen) return;

		this.mesh.beginShape(PGraphics.QUADS);
		this.mesh.textureMode(PGraphics.NORMAL);
		this.mesh.texture(texture);
		this.mesh.noStroke();
		this.mesh.noFill();

		for (int x = 0; x < Region.WIDTH; x++) {
			for (int y = 0; y < Region.HEIGHT; y++) {
				for (int z = 0; z < Region.DEPTH; z++) {
					final Node node = this.nodes[x][y][z];
					if (node != null && node.isSolid()) {
						Node tempNode = null;
						final boolean[] n = new boolean[6];
						// FIXME Make adjustments to take into account nodes in other regions
						n[Collidable.LEFT] = (tempNode = this.getNode(x, y, z + 1)) != null && tempNode.isSolid();
						n[Collidable.RIGHT] = (tempNode = this.getNode(x, y, z - 1)) != null && tempNode.isSolid();
						n[Collidable.FLOOR] = (tempNode = this.getNode(x, y - 1, z)) != null && tempNode.isSolid();
						n[Collidable.CEIL] = (tempNode = this.getNode(x, y + 1, z)) != null && tempNode.isSolid();
						n[Collidable.FRONT] = (tempNode = this.getNode(x + 1, y, z)) != null && tempNode.isSolid();
						n[Collidable.BACK] = (tempNode = this.getNode(x - 1, y, z)) != null && tempNode.isSolid();
						node.regenerateMesh(this.mesh, n);
					}
				}
			}
		}

		this.mesh.endShape(PGraphics.QUADS);
		this.needsRegen = false;
	}

	/*
	 * (non-Javadoc)
	 * @see Collidable#collidesWith(Collidable, processing.core.PVector)
	 */
	@Override
	public boolean[] collidesWith(final Collidable obj, final PVector velocity) {
		// FIXME Collision of Regions
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see Collidable#getPosition()
	 */
	@Override
	public PVector getPosition() {
		return this.position;
	}

	/*
	 * (non-Javadoc)
	 * @see Collidable#getSize()
	 */
	@Override
	public Dimensions getSize() {
		return this.size;
	}

	/*
	 * (non-Javadoc)
	 * @see GameObject#redraw(processing.core.PGraphics)
	 */
	@Override
	protected void redraw(final PGraphics g) {
		if (!this.needsRegen) g.shape(this.mesh);
	}
}
