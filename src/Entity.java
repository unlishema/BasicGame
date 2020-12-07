
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public abstract class Entity extends GameObject implements Collidable {
	protected static final float GLOBAL_SPEED = 1.15f, GLOBAL_SPEED_MULTIPLIER = 1.5f;

	private final World world;

	private float friction = 0.75f, speed = Entity.GLOBAL_SPEED / 60.0f, jumpForce = 6.5f;

	protected PVector look = new PVector(0f, -0.5f, 1f);
	protected PVector up = new PVector(0f, 1f, 0f);
	protected PVector right = new PVector(1f, 0f, 0f);
	protected PVector forward = new PVector(0f, 0f, 1f);
	protected PVector velocity = new PVector(0f, 0f, 0f);
	protected float pan = 0.0f, tilt = 0.0f;
	protected boolean onGround = false, isClipping = false, isFlying = false;

	public Entity(final World world) {
		super(new PVector(0.0f, 0.0f, 0.0f), new Dimensions(0.5f, 1.75f, 0.75f));
		this.world = world;
	}

  @Override
  public boolean[] collidesWith(final Collidable obj, final PVector velocity) {
    // Get the Min and Max Position of First Object (A)
    final PVector minA = new PVector(this.getPosition().x - this.getSize().width / 2,
        this.getPosition().y - this.getSize().height / 2, this.getPosition().z - this.getSize().depth / 2);
    final PVector maxA = new PVector(this.getPosition().x + this.getSize().width / 2,
        this.getPosition().y + this.getSize().height / 2, this.getPosition().z + this.getSize().depth / 2);

    // Get the Min and Max Position of Second Object (B)
    final PVector minB = new PVector(obj.getPosition().x - obj.getSize().width / 2,
        obj.getPosition().y - obj.getSize().height / 2, obj.getPosition().z - obj.getSize().depth / 2);
    final PVector maxB = new PVector(obj.getPosition().x + obj.getSize().width / 2,
        obj.getPosition().y + obj.getSize().height / 2, obj.getPosition().z + obj.getSize().depth / 2);

    // X Collision
    final boolean xPos = this.getPosition().x <= minB.x && minB.x <= maxA.x; // Front
    final boolean xNeg = this.getPosition().x >= maxB.x && maxB.x >= minA.x; // Back
    final boolean xAny = (minA.x < minB.x && minB.x < maxA.x) || (minA.x < maxB.x && maxB.x < maxA.x)
        || (minB.x < minA.x && minA.x < maxB.x) || (minB.x < maxA.x && maxA.x < maxB.x);

    // Y Collision
    final boolean yNeg = this.getPosition().y >= maxB.y && maxB.y >= minA.y; // Floor
    final boolean yPos = this.getPosition().y <= minB.y && minB.y <= maxA.y; // Ceiling
    final boolean yAny = (minA.y <= minB.y && minB.y <= maxA.y) || (minA.y <= maxB.y && maxB.y <= maxA.y)
        || (minB.y <= minA.y && minA.y <= maxB.y) || (minB.y <= maxA.y && maxA.y <= maxB.y);

    // Z Collision
    final boolean zPos = this.getPosition().z <= minB.z && maxA.z >= minB.z; // Left
    final boolean zNeg = this.getPosition().z >= maxB.z && maxB.z >= minA.z; // Right
    final boolean zAny = (minA.z < minB.z && minB.z < maxA.z) || (minA.z < maxB.z && maxB.z < maxA.z)
        || (minB.z < minA.z && minA.z < maxB.z) || (minB.z < maxA.z && maxA.z < maxB.z);

    if (velocity.x > 1.0f || velocity.y > 1.0f || velocity.z > 1.0f) {}

    // Returns a Boolean[] { Left, Right, Floor, Ceiling, Front, Back }
    boolean[] collisions = new boolean[6];

    collisions[FRONT] = xNeg && yAny && zAny;
    collisions[BACK] = xPos && yAny && zAny;

    collisions[FLOOR] = xAny && yNeg && zAny;
    collisions[CEIL] = xAny && yPos && zAny;

    collisions[LEFT] = xAny && yAny && zPos;
    collisions[RIGHT] = xAny && yAny && zNeg;

    // Returns a boolean for each direction only x, y, z
    return collisions;
  }

  @Override
  public boolean contains(final PVector vector) {
    return false;
  }

	protected void flyUp() {
		this.velocity.sub(PVector.mult(this.up, this.speed));
	}

	protected void flyDown() {
		if (!this.onGround) this.velocity.add(PVector.mult(this.up, this.speed));
	}

	protected void jump() {
		this.onGround = false;
		if (this.velocity.y == 0.0f) this.velocity.y += -this.jumpForce * (1.0f / 60.0f);
	}

	protected void toggleRun(final boolean toggle) {
		this.speed = (Entity.GLOBAL_SPEED * (toggle ? Entity.GLOBAL_SPEED_MULTIPLIER : 1)) * (1.0f / 60.0f);
	}

	protected void moveForward() {
		this.velocity.add(PVector.mult(this.forward, this.speed));
	}

	protected void moveBackward() {
		this.velocity.sub(PVector.mult(this.forward, this.speed));
	}

	protected void moveLeft() {
		this.velocity.add(PVector.mult(this.right, this.speed));
	}

	protected void moveRight() {
		this.velocity.sub(PVector.mult(this.right, this.speed));
	}

	protected void turnLeft() {
		// FIXME Adjust these so they use a speed multiplier
		this.pan -= Math.toRadians(2.5f);
	}

	protected void turnRight() {
		this.pan += Math.toRadians(2.5f);
	}

	protected void lookUp() {
		this.tilt -= Math.toRadians(1.0f);
	}

	protected void lookDown() {
		this.tilt += Math.toRadians(1.0f);
	}

	private void adjustLookVectors() {
		// Lets make sure we only Tilt so far
		this.tilt = this.clamp(this.tilt, -PConstants.PI / 2.01f, PConstants.PI / 2.01f);

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
	}

	private void checkCollisions() {
		final boolean[] collisions = this.world.map.collidesWith(this, new PVector(0.0f, 0.0f, 0.0f));

		// Calculate When Entity Hits a Wall in X Coordinate
		if (collisions[Collidable.FRONT] && this.velocity.x > 0.0f) {
			this.velocity.x = 0.0f;
			this.position.x = Math.round(this.position.x) + (1.0f - this.size.width) / 2.0f;
		}
		if (collisions[Collidable.BACK] && this.velocity.x < 0.0f) {
			this.velocity.x = 0.0f;
			this.position.x = Math.round(this.position.x) - (1.0f - this.size.width) / 2.0f;
		}

		// Calculate When Entity Hits a Wall in Z Coordinate
		if (collisions[Collidable.LEFT] && this.velocity.z < 0.0f) {
			this.velocity.z = 0.0f;
			this.position.z = Math.round(this.position.z) - (1.0f - this.size.depth) / 2.0f;
		}
		if (collisions[Collidable.RIGHT] && this.velocity.z > 0.0f) {
			this.velocity.z = 0.0f;
			this.position.z = Math.round(this.position.z) + (1.0f - this.size.depth) / 2.0f;
		}

		// Calculate When Entity Hits a Ceiling
		if (!this.onGround && collisions[Collidable.CEIL] && this.velocity.y < -0.00001f) {
			this.velocity.y = 0.0f;
			this.position.y = (float) (Math.ceil(this.position.y - 1.5f) + this.size.height / 2.0f) + 0.5f;
		}

		// When Entity Hits the Ground
		if (!this.onGround && collisions[Collidable.FLOOR] && this.velocity.y > 0.0001f) {
			// NOTICE If you fall to Far & Fast you should die on impact
			if (this.velocity.y > 0.35f) System.out.println("You should be dead but a Diety saved your life!");
			this.onGround = true;
			this.velocity.y = 0.0f;
			this.position.y = (float) (Math.floor(this.position.y + 1.5f) - this.size.height / 2.0f) - 0.5f;
		} else if (this.onGround && !collisions[Collidable.FLOOR]) {
			this.onGround = false;
		}
	}

	private float clamp(float x, float min, float max) {
		if (x > max) return max;
		if (x < min) return min;
		return x;
	}

	public void update() {
		// Add friction to movement
		this.velocity.x *= this.friction;
		this.velocity.z *= this.friction;

		// Check Collisions
		if (!this.isClipping) this.checkCollisions();

		// Apply Gravity
		if (!this.onGround && !this.isFlying) { // Deal With Gravity
			this.velocity.add(new PVector(0.0f, this.world.gravity * (1.0f / 60.0f), 0.0f));
		} else { // Friction on
			this.velocity.y *= this.friction;
		}

		this.adjustLookVectors();

		// Update the Players Position
		this.position.add(this.velocity);
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
	public void draw(final PGraphics g) {
		if (GameObject.DRAW_BOUNDARIES && !this.world.player.equals(this)) {
			// Setup Colored Face
			g.stroke(0, 0, 255);
			g.strokeWeight(1.0f);

			// Draw Look Ray Vector
			g.line(this.position.x, this.position.y - this.size.height * 0.375f, this.position.z,
					this.position.x + this.look.x * 10.0f, this.position.y + this.look.y * 10.0f,
					this.position.z + this.look.z * 10.0f);
		}

		// Adjust Rotation of Entity
		g.translate(this.getPosition().x, this.getPosition().y, this.getPosition().z);
		g.rotateY(-this.pan);// Pan of Entity
		// g.rotateZ(this.tilt); // Tilt of Entity Head
		g.translate(-this.getPosition().x, -this.getPosition().y, -this.getPosition().z);

		super.draw(g);

		// Adjust Rotation to normal
		g.translate(this.getPosition().x, this.getPosition().y, this.getPosition().z);
		g.rotateY(this.pan);// Pan of Entity
		// g.rotateZ(this.tilt); // Tilt of Entity Head
		g.translate(-this.getPosition().x, -this.getPosition().y, -this.getPosition().z);
	}
}
