
import processing.core.PApplet;
import processing.core.PGraphics;

public class AssetManager extends GameObject {

	private final PApplet app;

	protected String info = "Loading";
	protected float progress = 0.0f;
	protected boolean loading = false;

	public AssetManager(final PApplet app) {
		this.app = app;
	}

	public boolean isLoading() {
		return this.loading;
	}

	@Override
	protected void redraw(final PGraphics g) {
		if (!this.isLoading()) return;

		// Set Perspective and Background for Loading Screen
		g.ortho();
		g.background(0);
		// FIXME !!!NOW!!! Load in a background image

		// Setup Style
		g.stroke(0);
		g.strokeWeight(1);

		// Setup Font
		g.textSize(41);
		g.textAlign(PGraphics.CENTER, PGraphics.TOP);
		g.fill(0, 100, 255);

		// Draw Loading Text
		g.text(info, this.app.width / 2, this.app.height / 2);

		// Draw Progress Bar
		g.noStroke();
		g.fill(50, 255, 50);
		g.rect(this.app.width / 3, this.app.height / 3 * 2, this.progress * (this.app.width / 3), 50);

		// Draw Progress Percentage
		g.fill(155);
		final int percentage = (int) Math.floor(this.progress * 100);
		g.text(percentage + "%", this.app.width / 2, this.app.height / 3 * 2 + 10);

		// Draw Progress Bar Outline
		g.noFill();
		g.stroke(255);
		g.strokeWeight(6);
		g.rect(this.app.width / 3, this.app.height / 3 * 2, (this.app.width / 3), 53, 0);
	}
}
