import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class Map extends GameObject implements Collidable {
	private final PApplet app;
	private final PImage tex;

	protected final ArrayList<Region> regions = new ArrayList<Region>();

	private boolean isRegenerating = false;

	public Map(final PApplet app) {
		this.app = app;
		this.tex = this.app.loadImage("terrain2.png");

		// TODO Automate Dungeon Generation using DungonPrefabs and held as DungeonMap
		// which will extend a Map
	}

	public void load(final AssetManager assetManager) {
		assetManager.info = "Generating Test Map";
		assetManager.progress = 0.0f;
		this.generateTestMap(assetManager);

		assetManager.info = "Generating Regions";
		assetManager.progress = 0.0f;
		this.regenerateRegions(assetManager);
	}

	// DEBUG Remove Later Only or testing
	private void generateTestMap(final AssetManager assetManager) {
		for (int x = -5; x < 5; x++) for (int y = 0; y < 1; y++) for (int z = -5; z < 5; z++) {
			final Region regionTest = new Region(this.app, new PVector(x, y, z));
			this.regions.add(regionTest);
		}
	}

	public Node getNode(int x, int y, int z) {
		for (final Region region : this.regions)
			if (region.containsWorldCoors(x, y, z)) return region.getNodeByWorldCoors(x, y, z);
		return null;
	}

	public void regenerateRegions(final AssetManager assetManager) {
		if (this.isRegenerating) return;
		this.isRegenerating = true;

		int index = 0;
		for (final Region region : this.regions) {
			// FIXME Make adjustments to take into account nodes in other regions
			if (region.needsRegen) region.regenerateMesh(this.tex);

			assetManager.progress = ++index / this.regions.size();
			if (assetManager.progress >= 1.0f) assetManager.progress = 1.0f;
		}

		this.isRegenerating = false;
	}

	private boolean collidesWith(final int index, final Node node, final Collidable obj, final PVector velocity) {
		if (node != null && node.isSolid()) {
			final boolean[] nodeCollisions = node.collidesWith(obj, velocity);
			return nodeCollisions[index];
		}
		return false;
	}

	@Override
	public boolean[] collidesWith(final Collidable obj, final PVector velocity) {
		boolean[] mapCollisions = new boolean[] { false, false, false, false, false, false };
		// TODO Adjust this to use region collision checking which should be faster
		// FIXME Check all blocks between player current position and new position after
		// velocity is added (Needed for all sides)

		final float x = obj.getPosition().x;
		final float y = obj.getPosition().y;
		final float z = obj.getPosition().z;

		// Check Node under feet (Floor)
		if (!mapCollisions[Collidable.FLOOR]) mapCollisions[Collidable.FLOOR] = this.collidesWith(Collidable.FLOOR,
				this.getNode(Math.round(x - 0.5f), Math.round(y + 1.0f), Math.round(z)), obj, velocity);
		if (!mapCollisions[Collidable.FLOOR]) mapCollisions[Collidable.FLOOR] = this.collidesWith(Collidable.FLOOR,
				this.getNode(Math.round(x + 0.5f), Math.round(y + 1.0f), Math.round(z)), obj, velocity);
		if (!mapCollisions[Collidable.FLOOR]) mapCollisions[Collidable.FLOOR] = this.collidesWith(Collidable.FLOOR,
				this.getNode(Math.round(x), Math.round(y + 1.0f), Math.round(z - 0.5f)), obj, velocity);
		if (!mapCollisions[Collidable.FLOOR]) mapCollisions[Collidable.FLOOR] = this.collidesWith(Collidable.FLOOR,
				this.getNode(Math.round(x), Math.round(y + 1.0f), Math.round(z + 0.5f)), obj, velocity);

		// Check Node above head (Ceiling)
		if (!mapCollisions[Collidable.CEIL]) mapCollisions[Collidable.CEIL] = this.collidesWith(Collidable.CEIL,
				this.getNode(Math.round(x - 0.5f), Math.round(y - 1.0f), Math.round(z)), obj, velocity);
		if (!mapCollisions[Collidable.CEIL]) mapCollisions[Collidable.CEIL] = this.collidesWith(Collidable.CEIL,
				this.getNode(Math.round(x + 0.5f), Math.round(y - 1.0f), Math.round(z)), obj, velocity);
		if (!mapCollisions[Collidable.CEIL]) mapCollisions[Collidable.CEIL] = this.collidesWith(Collidable.CEIL,
				this.getNode(Math.round(x), Math.round(y - 1.0f), Math.round(z - 0.5f)), obj, velocity);
		if (!mapCollisions[Collidable.CEIL]) mapCollisions[Collidable.CEIL] = this.collidesWith(Collidable.CEIL,
				this.getNode(Math.round(x), Math.round(y - 1.0f), Math.round(z + 0.5f)), obj, velocity);

		// Check Nodes around player at same level (Left)
		if (!mapCollisions[Collidable.LEFT]) mapCollisions[Collidable.LEFT] = this.collidesWith(Collidable.LEFT,
				this.getNode(Math.round(x - 0.5f), Math.round(y + 0.75f), Math.round(z - 0.5f)), obj, velocity);
		if (!mapCollisions[Collidable.LEFT]) mapCollisions[Collidable.LEFT] = this.collidesWith(Collidable.LEFT,
				this.getNode(Math.round(x - 0.5f), Math.round(y - 0.75f), Math.round(z - 0.5f)), obj, velocity);
		if (!mapCollisions[Collidable.LEFT]) mapCollisions[Collidable.LEFT] = this.collidesWith(Collidable.LEFT,
				this.getNode(Math.round(x + 0.5f), Math.round(y + 0.75f), Math.round(z - 0.5f)), obj, velocity);
		if (!mapCollisions[Collidable.LEFT]) mapCollisions[Collidable.LEFT] = this.collidesWith(Collidable.LEFT,
				this.getNode(Math.round(x + 0.5f), Math.round(y - 0.75f), Math.round(z - 0.5f)), obj, velocity);

		// Check Nodes around player at same level (Right)
		if (!mapCollisions[Collidable.RIGHT]) mapCollisions[Collidable.RIGHT] = this.collidesWith(Collidable.RIGHT,
				this.getNode(Math.round(x - 0.5f), Math.round(y + 0.5f), Math.round(z + 0.75f)), obj, velocity);
		if (!mapCollisions[Collidable.RIGHT]) mapCollisions[Collidable.RIGHT] = this.collidesWith(Collidable.RIGHT,
				this.getNode(Math.round(x - 0.5f), Math.round(y - 0.5f), Math.round(z + 0.75f)), obj, velocity);
		if (!mapCollisions[Collidable.RIGHT]) mapCollisions[Collidable.RIGHT] = this.collidesWith(Collidable.RIGHT,
				this.getNode(Math.round(x + 0.5f), Math.round(y + 0.5f), Math.round(z + 0.75f)), obj, velocity);
		if (!mapCollisions[Collidable.RIGHT]) mapCollisions[Collidable.RIGHT] = this.collidesWith(Collidable.RIGHT,
				this.getNode(Math.round(x + 0.5f), Math.round(y - 0.5f), Math.round(z + 0.75f)), obj, velocity);

		// Check Nodes around player at same level (Front)
		if (!mapCollisions[Collidable.FRONT]) mapCollisions[Collidable.FRONT] = this.collidesWith(Collidable.FRONT,
				this.getNode(Math.round(x + 0.5f), Math.round(y + 0.75f), Math.round(z - 0.5f)), obj, velocity);
		if (!mapCollisions[Collidable.FRONT]) mapCollisions[Collidable.FRONT] = this.collidesWith(Collidable.FRONT,
				this.getNode(Math.round(x + 0.5f), Math.round(y - 0.75f), Math.round(z - 0.5f)), obj, velocity);
		if (!mapCollisions[Collidable.FRONT]) mapCollisions[Collidable.FRONT] = this.collidesWith(Collidable.FRONT,
				this.getNode(Math.round(x + 0.5f), Math.round(y + 0.75f), Math.round(z + 0.5f)), obj, velocity);
		if (!mapCollisions[Collidable.FRONT]) mapCollisions[Collidable.FRONT] = this.collidesWith(Collidable.FRONT,
				this.getNode(Math.round(x + 0.5f), Math.round(y - 0.75f), Math.round(z + 0.5f)), obj, velocity);

		// Check Nodes around player at same level (Back)
		if (!mapCollisions[Collidable.BACK]) mapCollisions[Collidable.BACK] = this.collidesWith(Collidable.BACK,
				this.getNode(Math.round(x - 0.5f), Math.round(y + 0.75f), Math.round(z - 0.5f)), obj, velocity);
		if (!mapCollisions[Collidable.BACK]) mapCollisions[Collidable.BACK] = this.collidesWith(Collidable.BACK,
				this.getNode(Math.round(x - 0.5f), Math.round(y - 0.75f), Math.round(z - 0.5f)), obj, velocity);
		if (!mapCollisions[Collidable.BACK]) mapCollisions[Collidable.BACK] = this.collidesWith(Collidable.BACK,
				this.getNode(Math.round(x - 0.5f), Math.round(y + 0.75f), Math.round(z + 0.5f)), obj, velocity);
		if (!mapCollisions[Collidable.BACK]) mapCollisions[Collidable.BACK] = this.collidesWith(Collidable.BACK,
				this.getNode(Math.round(x - 0.5f), Math.round(y - 0.75f), Math.round(z + 0.5f)), obj, velocity);
		return mapCollisions;
	}

	@Override
	public PVector getPosition() {
		return this.position;
	}

	@Override
	public Dimensions getSize() {
		return this.size;
	}

	@Override
	protected void redraw(final PGraphics g) {
		for (final Region region : this.regions) region.draw(g);
	}
}
