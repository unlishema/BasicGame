
import org.unlishema.simpleKeyHandler.SimpleKeyHandler;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

public class Player extends Entity {
	protected static final float GLOBAL_FLIGHT_SPEED_MULTIPLIER = 2.5f;
	private final PApplet app;

	protected final FirstPersonCamera camera;

	public Player(final PApplet app, final World world) {
		super(world);
		this.app = app;
		this.camera = new FirstPersonCamera(this.app, this);
	}

	@Override
	public void update() {
		final SimpleKeyHandler skh = ((BasicGame) this.app).skh;

		// Movement Keys
		if (skh.isKeyPressed('A')) this.moveLeft();
		if (skh.isKeyPressed('D')) this.moveRight();
		if (skh.isKeyPressed('W')) this.moveForward();
		if (skh.isKeyPressed('S')) this.moveBackward();

		// Run Key
		this.toggleRun(skh.isKeyPressed(SimpleKeyHandler.ModifierKey.CTRL));

		// Jump Key
		if (!this.isFlying && this.onGround && skh.isKeyPressed(' ')) this.jump();

		// Crouch Key
		if (!this.isFlying && this.onGround && skh.isKeyPressed(SimpleKeyHandler.ModifierKey.SHIFT)) {
			// TODO Add in Crouching at some point...
		}

		// Fly Up and Down
		if (this.isFlying && skh.isKeyPressed(SimpleKeyHandler.ModifierKey.SHIFT) && !skh.isKeyPressed(' '))
			this.flyDown();
		else if (this.isFlying && !skh.isKeyPressed(SimpleKeyHandler.ModifierKey.SHIFT) && skh.isKeyPressed(' '))
			this.flyUp();

		super.update();

		// Update the Camera
		this.camera.update();
	}

	public void mousePressed() {
		if (camera.isMouseFocused) {
			System.out.println("Mouse Clicked!");
		}

		if (this.camera.isMouseFocused == false) {
			this.camera.centerMouse();
			this.camera.isMouseFocused = true;
		}
	}

	@Override
	protected void redraw(final PGraphics g) {
		final PVector min = new PVector(this.getPosition().x - this.getSize().width / 2,
				this.getPosition().y - this.getSize().height / 2, this.getPosition().z - this.getSize().depth / 2);
		final PVector max = new PVector(this.getPosition().x + this.getSize().width / 2,
				this.getPosition().y + this.getSize().height / 2, this.getPosition().z + this.getSize().depth / 2);

		g.beginShape(PShape.QUADS);

		// Setup Colored Face
		g.fill(75, 25, 25);
		g.stroke(1);

		// +Z "front" face
		g.vertex(min.x, min.y, max.z, 0, 0);
		g.vertex(max.x, min.y, max.z, 0, 1);
		g.vertex(max.x, max.y, max.z, 1, 1);
		g.vertex(min.x, max.y, max.z, 1, 0);

		g.endShape();
	}
}
