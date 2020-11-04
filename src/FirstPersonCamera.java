
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
	private PVector center;
	private Point mouse;
	private float sensitivity = 0.15f;
	protected boolean isMouseFocused = true;

	public FirstPersonCamera(final PApplet app, final Player player) {
		super(new PVector(0.0f, 0.0f, 0.0f), new Dimensions(0.0f, 0.0f, 0.0f));
		this.app = app;
		this.player = player;
		try {
			this.robot = new Robot();
		} catch (final AWTException e) {}

		this.app.perspective(PConstants.PI / 3f, (float) this.app.width / (float) this.app.height, 0.01f, 1000f);

		this.centerMouse();
		this.update();
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
		// Update Camera Position based on Player Position
		this.position.set(this.player.getPosition().x,
				this.player.getPosition().y - this.player.getSize().height * 0.375f, this.player.getPosition().z);

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
			this.player.pan += PApplet.map(deltaX, 0, this.app.width, 0, PConstants.TWO_PI) * this.sensitivity;
			this.player.tilt += PApplet.map(deltaY, 0, this.app.height, 0, PConstants.PI) * this.sensitivity;
		}

		// Checks to Make sure Player cannot break the camera
		if (this.position.x > Float.MAX_VALUE) this.position.set(Float.MAX_VALUE, this.position.y, this.position.z);
		if (this.position.x < -Float.MAX_VALUE) this.position.set(-Float.MAX_VALUE, this.position.y, this.position.z);
		if (this.position.y > Float.MAX_VALUE) this.position.set(this.position.x, Float.MAX_VALUE, this.position.z);
		if (this.position.y < -Float.MAX_VALUE) this.position.set(this.position.x, -Float.MAX_VALUE, this.position.z);
		if (this.position.z > Float.MAX_VALUE) this.position.set(this.position.x, this.position.y, Float.MAX_VALUE);
		if (this.position.z < -Float.MAX_VALUE) this.position.set(this.position.x, this.position.y, -Float.MAX_VALUE);

		// Adjust the Center
		this.center = PVector.add(this.position, this.player.look);

	}

	@Override
	protected void redraw(final PGraphics g) {
		g.camera(this.position.x, this.position.y, this.position.z, this.center.x, this.center.y, this.center.z,
				this.player.up.x, this.player.up.y, this.player.up.z);
	}
}
