
import org.unlishema.simpleKeyHandler.SimpleKeyEvent;
import org.unlishema.simpleKeyHandler.SimpleKeyHandler;
import org.unlishema.simpleKeyHandler.SimpleKeyListener;

import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;

public class BasicGame extends PApplet {
	protected SimpleKeyHandler skh = new SimpleKeyHandler(this);
	protected World world;
	protected AssetManager assetManager;
	protected GameGUI gui;

	private boolean f3Pressed = false;

	public void settings() {
		this.size(800, 600, P3D);
		this.noSmooth();
	}

	public void setup() {
		this.frameRate(60);
		this.background(0);
		this.textureMode(PApplet.NORMAL);
		this.hint(PApplet.DISABLE_TEXTURE_MIPMAPS);
		((PGraphicsOpenGL) this.g).textureSampling(3);
		this.surface.setResizable(true);
		this.skh.overrideEscape(true);

		// Load up basic World and AssetManager
		this.assetManager = new AssetManager(this);
		this.world = new World(this);

		// Add Key Listener
		// TODO Eventually Move this into an object probably World
		this.skh.addListener(new SimpleKeyListener() {

			@Override
			public void onKeyPressed(final SimpleKeyEvent event) {
				// Escape Key for Menu and Exiting
				if (event.isKeyPressed(SimpleKeyEvent.ControlKey.ESCAPE)) {
					world.player.camera.isMouseFocused = !world.player.camera.isMouseFocused;
					gui.menuOpen = !gui.menuOpen;
					cursor();
				}

				// F3 Key for Debug
				// FIXME SimpleKeyEvent.FunctionKey.F3 instead of 99
				if (event.isKeyPressed(99)) {
					f3Pressed = true;
					if (f3Pressed && !gui.drawDebug) {
						gui.drawDebug = !gui.drawDebug;
						System.out.println("Draw Debug has been Toggled!");
						f3Pressed = false;
					}
				}

				// Toggle Draw Boundaries Key
				// FIXME SimpleKeyEvent.FunctionKey.F3 instead of 99
				if (event.isKeysPressed(99, 'B')) {
					GameObject.DRAW_BOUNDARIES = !GameObject.DRAW_BOUNDARIES;
					System.out.println("Drawing Boundaries has been Toggled!");
					f3Pressed = false;
				}

				// Toggle Flying Key
				if (event.isKeyPressed('F')) {
					world.player.isFlying = !world.player.isFlying;
					if (world.player.isFlying && world.player.onGround) world.player.onGround = !world.player.onGround;
					System.out.println("Flying has been Toggled!");
				}

				// Toggle Clipping Key
				// FIXME SimpleKeyEvent.FunctionKey.F3 instead of 99
				if (event.isKeysPressed(99, 'C')) {
					world.player.isClipping = !world.player.isClipping;
					if (world.player.isClipping && world.player.onGround)
						world.player.onGround = !world.player.onGround;
					System.out.println("Clipping has been Toggled!");
				}

				// Reset Player Position
				// FIXME SimpleKeyEvent.FunctionKey.F3 instead of 99
				if (event.isKeysPressed(99, 'R')) {
					world.player.velocity.set(0.0f, 0.0f, 0.0f);
					world.player.position.set(new PVector(0.0f, -2.0f, 0.0f));
					System.out.println("Reset Player to Starting Position!");
				}
			}

			@Override
			public void onKeyReleased(final SimpleKeyEvent event) {

				// F3 Key for Debug
				// FIXME SimpleKeyEvent.FunctionKey.F3 instead of 99
				if (!event.isKeyPressed(99) && f3Pressed) {
					gui.drawDebug = !gui.drawDebug;
					System.out.println("Draw Debug has been Toggled!");
					f3Pressed = false;
				}
			}

			@Override
			public void onKeyTyped(final SimpleKeyEvent event) {
			}
		});

		// Create a GUI Graphics and GUI
		this.gui = new GameGUI(this, this.world);

		this.thread("load");
	}

	public void load() {
		// Start Loading
		this.assetManager.loading = true;

		this.world.load(this.assetManager);

		// Finished Loading
		this.assetManager.loading = false;
	}

	public void draw() {
		// Draw the Loading Screen
		if (this.assetManager.isLoading()) {
			this.assetManager.draw(this.getGraphics());
			return;
		}

		// Update Player
		if (!this.gui.menuOpen && this.world.map != null) {
			this.world.player.update();
			this.world.npc.update();
		}

		// Draw the World
		this.world.draw(this.getGraphics());

		// FIXME Change to only draw while editMode is on
		// Draw Node we are looking at
		if (this.gui.drawDebug && GameObject.DRAW_BOUNDARIES) {
			final PVector lookingAtPos = this.world.getLookingAt(this.world.player);
			if (lookingAtPos != null) {
				final Node node = this.world.map.getNode((int) Math.round(lookingAtPos.x),
						(int) Math.round(lookingAtPos.y), (int) Math.round(lookingAtPos.z));
				if (node != null) {
					g.pushStyle();
					g.noFill();
					g.stroke(255);
					g.strokeWeight(3.0f);
					g.translate(node.getPosition().x, node.getPosition().y, node.getPosition().z);
					g.box(1.01f);
					g.popStyle();
				}
			}
		}

		// Draw the GUI & Debug
		this.gui.draw(this.getGraphics());
	}

	public void exit() {
		// TODO this.assetManager.close();
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
