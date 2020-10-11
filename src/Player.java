
import org.unlishema.simpleKeyHandler.SimpleKeyHandler;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Player extends GameObject {
	protected static final float GLOBAL_SPEED = 1.15f, GLOBAL_SPEED_MULTIPLIER = 1.5f,
			GLOBAL_FLIGHT_SPEED_MULTIPLIER = 2.5f;

	protected static final PVector STARTING_POSITION = new PVector(0.0f, 0.0f, 0.0f);

	private final World world;
	private final PApplet app;

	private float friction = 0.75f, speed = Player.GLOBAL_SPEED / 60.0f, jumpForce = 6.5f;

	protected final FirstPersonCamera camera;

	protected PVector velocity;
	protected boolean onGround = false, isFlying = false, isClipping = false;

	public Player(final PApplet app, final World world) {
		super(Player.STARTING_POSITION, new Dimensions(0.75f, 1.75f, 0.5f));
		// FIXME SOON!! Find out how to position turn the Camera myself
		this.app = app;
		this.world = world;
		this.camera = new FirstPersonCamera(this.app, this);
		this.velocity = new PVector(0f, 0f, 0f);
	}

	public boolean isFlying() {
		return this.isFlying;
	}

	public void update() {
		final SimpleKeyHandler skh = ((BasicGame) this.app).skh;

		// Movement Keys
		if (skh.isKeyPressed('A')) this.velocity.add(PVector.mult(this.camera.right, this.speed));
		if (skh.isKeyPressed('D')) this.velocity.sub(PVector.mult(this.camera.right, this.speed));
		if (skh.isKeyPressed('W')) this.velocity.add(PVector.mult(this.camera.forward, this.speed));
		if (skh.isKeyPressed('S')) this.velocity.sub(PVector.mult(this.camera.forward, this.speed));

		// Run Key
		if (skh.isKeyPressed(SimpleKeyHandler.ModifierKey.CTRL)) {
			this.speed = (Player.GLOBAL_SPEED * Player.GLOBAL_SPEED_MULTIPLIER);
			if (this.isFlying()) this.speed *= Player.GLOBAL_FLIGHT_SPEED_MULTIPLIER;
			this.speed *= (1.0f / 60.0f);
		} else {
			this.speed = Player.GLOBAL_SPEED;
			if (this.isFlying()) this.speed *= Player.GLOBAL_FLIGHT_SPEED_MULTIPLIER;
			this.speed *= (1.0f / 60.0f);
		}

		// Jump Key
		if (!this.isFlying() && this.onGround && skh.isKeyPressed(' ')) {
			this.onGround = false;
			if (this.velocity.y == 0.0f) this.velocity.y += -this.jumpForce * (1.0f / 60.0f);
		}

		// Crouch Key
		if (!this.isFlying() && this.onGround && skh.isKeyPressed(SimpleKeyHandler.ModifierKey.SHIFT)) {
			// FIXME Add this in.... hmmm how to do this.... lol... just a lot of checking
			// to do i think. need to do same as block collision almost
			// Flip a Boolean here and actual checks will be below when we update player
			// position
		}

		// Fly Up and Down
		if (this.isFlying() && skh.isKeyPressed(SimpleKeyHandler.ModifierKey.SHIFT)) {
			this.velocity.add(PVector.mult(this.camera.up, this.speed));
		} else if (this.isFlying() && skh.isKeyPressed(' ')) {
			this.velocity.sub(PVector.mult(this.camera.up, this.speed));
		} else if (this.isFlying() && !skh.isKeyPressed(SimpleKeyHandler.ModifierKey.SHIFT) && !skh.isKeyPressed(' ')) {
			this.velocity.y = 0.0f;
		}

		// Add friction to movement
		this.velocity.x *= this.friction;
		this.velocity.z *= this.friction;

		// Calculate Collisions
		final boolean[] collisions = this.world.collidesWith(this, new PVector(0.0f, 0.0f, 0.0f));
		if (!this.isClipping) {
			// Calculate When Player Hits a Ceiling
			if (collisions[Collisions.CEIL]) {
				this.position.y = (float) Math.ceil(this.position.y);
				if (this.velocity.y < 0.00001f) this.velocity.y = 0.0f;
			}

			// Calculate When Player Hits a Wall in X Coordinate
			if (collisions[Collisions.LEFT] && this.velocity.x > 0.0f) {
				this.velocity.x = 0.0f;
				this.position.x = Math.round(this.position.x) + (1.0f - this.size.width) / 2;
			} else if (collisions[Collisions.RIGHT] && this.velocity.x < 0.0f) {
				this.velocity.x = 0.0f;
				this.position.x = Math.round(this.position.x) - (1.0f - this.size.width) / 2;
			}

			// Calculate When Player Hits a Wall in Z Coordinate
			if (collisions[Collisions.FRONT] && this.velocity.z < 0.0f) {
				this.velocity.z = 0.0f;
				this.position.z = Math.round(this.position.z) - (1.0f - this.size.depth) / 2;
			} else if (collisions[Collisions.BACK] && this.velocity.z > 0.0f) {
				this.velocity.z = 0.0f;
				this.position.z = Math.round(this.position.z) + (1.0f - this.size.depth) / 2;
			}

			// When Player Hits the Ground
			if (!this.onGround && collisions[Collisions.FLOOR]) {
				// NOTICE If you fall to Far & Fast you should die on impact
				if (this.velocity.y > 0.35f) System.out.println("You should be dead but a Diety saved your life!");
				if (this.velocity.y > -0.0001f) {
					if (!this.isFlying()) this.onGround = true;
					this.velocity.y = 0.0f;
					this.position.y = (float) (Math.floor(this.position.y) + (2.0f - this.size.height) / 2);
				}
			} else if (this.onGround && !collisions[Collisions.FLOOR]) {
				this.onGround = false;
			}
		}

		// Apply Gravity
		if (!this.isFlying() && !this.onGround) { // Deal With Gravity
			this.velocity.add(new PVector(0.0f, this.world.gravity * (1.0f / 60.0f), 0.0f));
		} else { // Friction on
			this.velocity.y *= this.friction;
		}

		// Update the Players Position
		this.position.add(this.velocity);

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
		this.camera.draw(g);
	}
}
