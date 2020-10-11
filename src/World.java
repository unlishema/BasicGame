
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class World extends GameObject {

	private final PApplet app;
	protected Player player;
	private final Map map;
	// FIXME Change from using a node to using a map full of nodes

	protected final float gravity = 0.33f;

	public World(final PApplet app) {
		this.app = app;
		this.map = new Map(app);
		this.player = new Player(this.app, this);
	}

	public float getGroundLevel(final PVector position) {
		return this.map.calculateGroundLevel(position);
	}

	@Override
	public boolean[] collidesWith(final GameObject obj, final PVector padding) {
		return this.map.collidesWith(obj, padding);
	}

	@Override
	protected void redraw(final PGraphics g) {
		// Set Sky Background Color and Set Perspective
		g.background(98, 144, 219);
		g.perspective(PApplet.radians(90), (float) this.app.width / (float) this.app.height, 0.1f, 400);

		this.map.draw(g);
		this.player.draw(g);
	}
}
