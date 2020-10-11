
import com.jogamp.newt.opengl.GLWindow;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

import java.awt.*;

public class FirstPersonCamera extends GameObject {
	private final PApplet app;
	private final Player player;

	private Robot robot = null;
	protected PVector look = new PVector(0f, 0.5f, 1f);
	private PVector center;
	private Point mouse;
	private float sensitivity = 0.15f, tilt, pan;

	protected PVector up = new PVector(0f, 1f, 0f);
	protected PVector right = new PVector(1f, 0f, 0f);
	protected PVector forward = new PVector(0f, 0f, 1f);
	protected boolean isMouseFocused = true;

	public FirstPersonCamera(final PApplet app, final Player player) {
		this.app = app;
		this.player = player;
		try {
			this.robot = new Robot();
		} catch (final AWTException e) {}

		this.pan = 0;
		this.tilt = 0f;

		this.app.perspective(PConstants.PI / 3f, (float) this.app.width / (float) this.app.height, 0.01f, 1000f);

		this.update();
	}

	private float clamp(float x, float min, float max) {
		if (x > max) return max;
		if (x < min) return min;
		return x;
	}

	public void centerMouse() {
		// Get the Real Mouse Location
		int x = ((GLWindow) this.app.getSurface().getNative()).getX();
		int y = ((GLWindow) this.app.getSurface().getNative()).getY();

		// Move Real Mouse to Center of Screen
		this.robot.mouseMove(this.app.width / 2 + x, this.app.height / 2 + y);
	}

	// Update the Camera Position
	public void update() {
		// Get Fake (Java) Mouse Position
		this.mouse = MouseInfo.getPointerInfo().getLocation();

		// If app and mouse are both focused on
		if (this.isMouseFocused && this.app.focused) {
			// Set to No Mouse On Screen
			this.app.noCursor();

			// Get Real Mouse Position
			int x = ((GLWindow) this.app.getSurface().getNative()).getX();
			int y = ((GLWindow) this.app.getSurface().getNative()).getY();

			// Compare Real Mouse to Fake Mouse
			int deltaX = this.mouse.x - (this.app.width / 2 + x);
			int deltaY = this.mouse.y - (this.app.height / 2 + y);

			// Center the Mouse Again
			this.centerMouse();

			// Adjust the Pan & Tilt of the Screen
			this.pan += PApplet.map(deltaX, 0, this.app.width, 0, PConstants.TWO_PI) * this.sensitivity;
			this.tilt += PApplet.map(deltaY, 0, this.app.height, 0, PConstants.PI) * this.sensitivity;
		} else {
			// Set to Mouse on Screen
			this.app.cursor();
		}

		// Lets make sure we only Tilt so far
		this.tilt = this.clamp(this.tilt, -PConstants.PI / 2.01f, PConstants.PI / 2.01f);

		// Hmm what is this for???
		if (this.tilt == PConstants.PI / 2) this.tilt += 0.001f;

		// Adjust Forward Vector
		this.forward = new PVector(PApplet.cos(this.pan), 0, PApplet.sin(this.pan));
		this.forward.normalize();

		// Adjust the Look Vector
		this.look = new PVector(PApplet.cos(this.pan), PApplet.tan(this.tilt), PApplet.sin(this.pan));
		this.look.normalize();

		// Adjust the Right Vector
		this.right = new PVector(PApplet.cos(this.pan - PConstants.PI / 2), 0,
				PApplet.sin(this.pan - PConstants.PI / 2));
		this.right.normalize();

		// Adjust the Center
		this.center = PVector.add(this.player.position, this.look);

	}

	@Override
	protected void redraw(final PGraphics g) {
		g.camera(this.player.position.x, this.player.position.y - this.player.size.height * 0.375f,
				this.player.position.z, this.center.x, this.center.y, this.center.z, this.up.x, this.up.y, this.up.z);
	}
}
