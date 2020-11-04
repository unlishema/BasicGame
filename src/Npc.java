import org.unlishema.simpleKeyHandler.SimpleKeyHandler;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

public class Npc extends Entity {

	private final PApplet app;

	public Npc(final PApplet app, final World world) {
		super(world);
		this.app = app;
	}

	@Override
	public void update() {
		final SimpleKeyHandler skh = ((BasicGame) this.app).skh;

		// Movement Keys
		if (skh.isKeyPressed('J')) this.moveLeft();
		if (skh.isKeyPressed('L')) this.moveRight();
		if (skh.isKeyPressed('I')) this.moveForward();
		if (skh.isKeyPressed('K')) this.moveBackward();
		
		// Turn Keys
		if (skh.isKeyPressed('U')) this.turnLeft();
		if (skh.isKeyPressed('O')) this.turnRight();
		if (skh.isKeyPressed('Y')) this.lookUp();
		if (skh.isKeyPressed('H')) this.lookDown();

		// Run Key
		this.toggleRun(skh.isKeyPressed(SimpleKeyHandler.ModifierKey.CTRL));

		// Jump Key
		if (!this.isFlying && this.onGround && skh.isKeyPressed('P')) this.jump();

		super.update();
	}

	@Override
	protected void redraw(final PGraphics g) {
		final PVector min = new PVector(this.getPosition().x - this.getSize().width / 2,
				this.getPosition().y - this.getSize().height / 2, this.getPosition().z - this.getSize().depth / 2);
		final PVector max = new PVector(this.getPosition().x + this.getSize().width / 2,
				this.getPosition().y + this.getSize().height / 2, this.getPosition().z + this.getSize().depth / 2);

		g.beginShape(PShape.QUADS);

		// Setup Colored Face
		g.fill(115, 10, 10);
		g.noStroke();

		// +X "front" face
		g.vertex(max.x, min.y, max.z, 0, 0);
		g.vertex(max.x, min.y, min.z, 0, 1);
		g.vertex(max.x, max.y, min.z, 1, 1);
		g.vertex(max.x, max.y, max.z, 1, 0);

		g.endShape();
	}
}
