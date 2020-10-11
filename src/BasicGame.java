
import org.unlishema.simpleKeyHandler.SimpleKeyHandler;
import org.unlishema.simpleKeyHandler.SimpleKeyListener;

import processing.core.PApplet;

public class BasicGame extends PApplet implements SimpleKeyListener {
	protected SimpleKeyHandler skh = new SimpleKeyHandler(this);
	protected World world;
	protected AssetLoader assetLoader;
	protected GameGUI gui;

	public void settings() {
		size(800, 600, P3D);
		noSmooth();
	}

	public void setup() {
		background(0);
		textureMode(NORMAL);
		this.surface.setResizable(true);
		this.skh.overrideEscape(true);

		this.assetLoader = new AssetLoader(this);
		this.world = new World(this);
		this.skh.addListener(this);

		this.gui = new GameGUI(this, this.world);
	}

	public void draw() {
		// Draw the Loading Screen
		if (this.assetLoader.isLoading()) {
			this.assetLoader.draw(this.getGraphics());
			return;
		}

		// Update Player
		this.world.player.update();

		// Draw the World
		this.world.draw(this.getGraphics());

		// Draw the GUI and Debug
		this.gui.draw(this.getGraphics());
	}

	public void mousePressed() {
		if (!this.gui.menuOpen)
			this.world.player.mousePressed();
		else
			this.gui.mousePressed(this.mouseX, this.mouseY);
	}

	@Override
	public void onKeyPressed() {
		// Escape Key for Menu and Exiting
		if (this.skh.isKeyPressed(SimpleKeyHandler.ControlKey.ESCAPE)) {
			this.world.player.camera.isMouseFocused = !this.world.player.camera.isMouseFocused;
			this.gui.menuOpen = !this.gui.menuOpen;
		}

		// Toggle Flying Key
		if (this.skh.isKeyPressed('F')) {
			this.world.player.isFlying = !this.world.player.isFlying;
			if (this.world.player.isFlying && this.world.player.onGround)
				this.world.player.onGround = !this.world.player.onGround;
			System.out.println("Flying has been Toggled!");
		}

		// Toggle Clipping Key
		if (this.skh.isKeyPressed('C')) {
			this.world.player.isClipping = !this.world.player.isClipping;
			if (this.world.player.isClipping && this.world.player.onGround)
				this.world.player.onGround = !this.world.player.onGround;
			System.out.println("Clipping has been Toggled!");
		}

		// Toggle Draw Boundaries Key
		if (this.skh.isKeyPressed('B')) {
			Player.DRAW_BOUNDARIES = !Player.DRAW_BOUNDARIES;
			System.out.println("Drawing Boundaries has been Toggled!");
		}

		// Reset Player Position
		if (skh.isKeyPressed('R')) {
			this.world.player.velocity.set(0.0f, 0.0f, 0.0f);
			this.world.player.position.set(Player.STARTING_POSITION);
			System.out.println("Reset Player to Starting Position!");
		}
	}

	@Override
	public void onKeyReleased() {
	}

	@Override
	public void onKeyTyped() {
	}

	public static void main(String[] args) {
		PApplet.main("BasicGame");
	}
}
