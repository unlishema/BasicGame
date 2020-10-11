
import processing.core.PApplet;
import processing.core.PGraphics;

public class AssetLoader extends GameObject {

	private final PApplet app;

	private float progress = 0.5f;
	private boolean loading = false;

	public AssetLoader(final PApplet app) {
		this.app = app;
		// TODO Load Assets Here
	}

	public void close() {
		// TODO Unload Assets Here
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
		// TODO Load in a background image

		// Setup Font
		g.textSize(41);
		g.textAlign(PGraphics.LEFT, PGraphics.TOP);
		g.fill(0, 100, 255);

		// Draw Loading Text
		final StringBuilder sb = new StringBuilder();
		sb.append("Loading");
		for (int i = 0; i <= (this.app.frameCount / (this.app.frameRate / 3)) % 3; i++) sb.append(".");
		g.text(sb.toString(), this.app.width / 2 - this.app.width / 12, this.app.height / 2);

		// Draw the Asset we are Loading
		// TODO Draw Asset we are Loading

		// Draw Progress Bar
		g.noStroke();
		g.fill(50, 255, 50);
		g.rect(this.app.width / 3, this.app.height / 3 * 2, this.progress * (this.app.width / 3), 50);

		// Draw Progress Percentage
		g.fill(155);
		final int percentage = (int) (this.progress * 100);
		g.text(percentage + "%", this.app.width / 3 * 2 + 5, this.app.height / 3 * 2 + 10);

		// Draw Progress Bar Outline
		g.noFill();
		g.stroke(255);
		g.strokeWeight(6);
		g.rect(this.app.width / 3, this.app.height / 3 * 2, (this.app.width / 3), 53, 0);
	}
}
