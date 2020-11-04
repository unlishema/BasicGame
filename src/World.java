
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class World extends GameObject {

	private final PApplet app;

	protected final Player player;
	protected final Npc npc;
	protected Map map;
	protected final float gravity = 0.26f;

	public World(final PApplet app) {
		this.app = app;

		// Create Player and Position them
		this.player = new Player(this.app, this);
		this.player.position.set(0.0f, -2.0f, 0.0f);

		// Create Npc and Position them
		this.npc = new Npc(this.app, this);
		this.npc.position.set(0.0f, -2.0f, 0.0f);

		// Create Map
		this.map = new Map(this.app);
	}

	public PVector getLookingAt(final Entity entity) {
		final float maxDist = 20.0f;

		// Determine Steps for Look Vector
		final PVector startPos = new PVector(entity.getPosition().x,
				entity.getPosition().y - entity.size.height * 0.375f, entity.getPosition().z);
		final PVector lookPos = new PVector(entity.getPosition().x,
				entity.getPosition().y - entity.size.height * 0.375f, entity.getPosition().z);
		final PVector lookVector = new PVector(entity.look.x / 15.0f, entity.look.y / 15.0f, entity.look.z / 15.0f);

		// Build a list of nodes in ray path
		final ArrayList<Node> nodes = new ArrayList<Node>();
		while (startPos.dist(lookPos) <= maxDist) {
			lookPos.add(lookVector);
			final Node node = this.map.getNode((int) Math.round(lookPos.x), (int) Math.round(lookPos.y),
					(int) Math.round(lookPos.z));
			if (node != null && node.isSolid() && !(nodes.size() >= 1 && nodes.get(nodes.size() - 1).equals(node)))
				nodes.add(node);
		}

		// Pick the Closest Node to us.
		PVector pos = null;
		if (nodes.size() > 0) for (final Node node : nodes) {
			if (pos == null) pos = new PVector(node.getPosition().x, node.getPosition().y, node.getPosition().z);
			if (pos != null && startPos.dist(pos) > startPos.dist(node.getPosition()))
				pos.set(node.getPosition().x, node.getPosition().y, node.getPosition().z);
		}
		return pos;
	}

	public void load(final AssetManager assetManager) {
		this.map.load(assetManager);
	}

	@Override
	protected void redraw(final PGraphics g) {
		// Set Sky Background Color and Set Perspective
		g.background(98, 144, 219);
		g.perspective(PApplet.radians(90), (float) this.app.width / (float) this.app.height, 0.1f, 400);

		if (this.map != null) {
			this.map.draw(g);
			this.player.draw(g);
			this.player.camera.draw(g);
			this.npc.draw(g);
		}
	}
}
