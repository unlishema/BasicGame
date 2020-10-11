
import processing.core.PApplet;
import processing.core.PGraphics;

public class GameGUI extends GameObject {

	private final PApplet app;
	private final World world;
	private final Button resumeBtn, exitBtn;

	protected boolean menuOpen = false, drawDebug = true;

	public GameGUI(final PApplet app, final World world) {
		this.app = app;
		this.world = world;
		final int w = this.app.width / 2, h = this.app.height / 2;
		this.resumeBtn = new Button("Resume", w - 90, h, 180, 55) {

			@Override
			public void clicked() {
				world.player.camera.isMouseFocused = true;
				menuOpen = false;
			}
		};
		this.exitBtn = new Button("Exit Game", w - 115, h + 100, 230, 55) {

			@Override
			public void clicked() {
				app.exit();
			}
		};
	}

	private void toggleRedGreen(final PGraphics g, final boolean toggle) {
		if (toggle)
			g.fill(0, 255, 0);
		else
			g.fill(255, 0, 0);
	}

	@Override
	protected void redraw(final PGraphics g) {
		// Create a new Matrix to Draw to
		g.pushMatrix();
		g.resetMatrix();

		// Reset Back to original perspective and position
		g.ortho();
		g.translate(-this.app.width / 2, -this.app.height / 2);

		// Create a new Style just for menu
		g.pushStyle();

		// Draw Crosshairs
		if (!this.menuOpen) {
			g.noStroke();
			g.fill(255);
			g.rect(this.app.width / 2 - 2, this.app.height / 2 - 15, 4, 30);
			g.rect(this.app.width / 2 - 15, this.app.height / 2 - 2, 30, 4);
		}

		// Draw Debug
		if (!this.menuOpen && this.drawDebug) {
			int y = -8;
			g.stroke(0);
			g.textAlign(PGraphics.LEFT, PGraphics.TOP);

			// Draw Shortcuts
			g.fill(255);
			g.text("Esc: Open Menu (Simple But Efficent)", 10, y += 18);
			g.text("R: Reset Player Position", 10, y += 18);
			this.toggleRedGreen(g, this.world.player.isFlying());
			g.text("F: Toggle Flying (" + this.world.player.isFlying() + ")", 10, y += 18);
			this.toggleRedGreen(g, this.world.player.isClipping);
			g.text("C: Toggle Clipping (" + this.world.player.isClipping + ")", 10, y += 18);
			this.toggleRedGreen(g, Player.DRAW_BOUNDARIES);
			g.text("B: Toggle Draw Boundaries (" + Player.DRAW_BOUNDARIES + ")", 10, y += 18);
			this.toggleRedGreen(g, this.world.player.onGround);
			g.text("On Ground: (" + this.world.player.onGround + ")", 10, y += 18);

			// Draw Debug Info
			g.fill(0, 0, 255);
			g.text("Debug:", 10, y += 18);
			g.fill(255);
			g.text("FPS: " + (int) this.app.frameRate, 20, y += 18);
			final StringBuilder sb = new StringBuilder();

			// Build X Position
			sb.append("X: ");
			sb.append(String.format("%.02f", this.world.player.position.x));
			sb.append(" (V:");
			sb.append(String.format("%.02f", this.world.player.velocity.x));
			sb.append(")");
			g.text(sb.toString(), 20, y += 18);

			// Build Y Position
			sb.setLength(0);
			sb.append("Y: ");
			sb.append(String.format("%.02f", this.world.player.position.y));
			sb.append(" (V:");
			sb.append(String.format("%.02f", this.world.player.velocity.y));
			sb.append(")");
			g.text(sb.toString(), 20, y += 18);

			// Build Z Position
			sb.setLength(0);
			sb.append("Z: ");
			sb.append(String.format("%.02f", this.world.player.position.z));
			sb.append(" (V:");
			sb.append(String.format("%.02f", this.world.player.velocity.z));
			sb.append(")");
			g.text(sb.toString(), 20, y += 18);

			// Build Look Vector
			sb.setLength(0);
			sb.append("Look: ");
			sb.append(String.format("%.02f", this.world.player.camera.look.x));
			sb.append(",");
			sb.append(String.format("%.02f", this.world.player.camera.look.y));
			sb.append(",");
			sb.append(String.format("%.02f", this.world.player.camera.look.z));
			g.text(sb.toString(), 20, y += 18);
		} else if (this.menuOpen) { // Draw Menu
			this.resumeBtn.draw(g);
			this.exitBtn.draw(g);
			// TODO Make a Better GUI Menu
		}

		// Reset the Style and Matrix
		g.popStyle();
		g.popMatrix();
	}

	public void mousePressed(final int x, final int y) {
		if (this.resumeBtn.contains(x, y)) this.resumeBtn.clicked();
		if (this.exitBtn.contains(x, y)) this.exitBtn.clicked();
	}
}
