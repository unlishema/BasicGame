
import org.unlishema.simpleKeyHandler.SimpleKeyHandler;
import org.unlishema.simpleKeyHandler.SimpleKeyListener;

import processing.core.PApplet;

public class BasicGame extends PApplet {
	protected SimpleKeyHandler skh = new SimpleKeyHandler(this);
	protected World world;
	protected AssetLoader assetLoader;
	protected GameGUI gui;

	public void settings() {
		size(800, 600, P3D);
		noSmooth();
	}

	public void setup() {
		frameRate(61);
		background(0);
		textureMode(NORMAL);
		this.surface.setResizable(true);
		this.skh.overrideEscape(true);

		this.assetLoader = new AssetLoader(this);
		this.world = new World(this);

		this.skh.addListener(new SimpleKeyListener() {

			@Override
			public void onKeyPressed() {
				// Escape Key for Menu and Exiting
				if (skh.isKeyPressed(SimpleKeyHandler.ControlKey.ESCAPE)) {
					world.player.camera.isMouseFocused = !world.player.camera.isMouseFocused;
					gui.menuOpen = !gui.menuOpen;
				}

				// Toggle Flying Key
				if (skh.isKeyPressed('F')) {
					world.player.isFlying = !world.player.isFlying;
					if (world.player.isFlying && world.player.onGround) world.player.onGround = !world.player.onGround;
					System.out.println("Flying has been Toggled!");
				}

				// Toggle Clipping Key
				if (skh.isKeyPressed('C')) {
					world.player.isClipping = !world.player.isClipping;
					if (world.player.isClipping && world.player.onGround)
						world.player.onGround = !world.player.onGround;
					System.out.println("Clipping has been Toggled!");
				}

				// Toggle Draw Boundaries Key
				if (skh.isKeyPressed('B')) {
					Player.DRAW_BOUNDARIES = !Player.DRAW_BOUNDARIES;
					System.out.println("Drawing Boundaries has been Toggled!");
				}

				// Reset Player Position
				if (skh.isKeyPressed('R')) {
					world.player.velocity.set(0.0f, 0.0f, 0.0f);
					world.player.position.set(Player.STARTING_POSITION);
					System.out.println("Reset Player to Starting Position!");
				}
			}

			@Override
			public void onKeyReleased() {
			}

			@Override
			public void onKeyTyped() {
			}
		});

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

	public void exit() {
		this.assetLoader.close();
		super.exit();
	}

	public void mousePressed() {
		if (!this.gui.menuOpen)
			this.world.player.mousePressed();
		else
			this.gui.mousePressed(this.mouseX, this.mouseY);
	}

	public static void main(String[] args) {
		PApplet.main("BasicGame");
	}
}
