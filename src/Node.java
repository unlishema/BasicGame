import processing.core.PShape;
import processing.core.PVector;

public class Node implements Collidable {

	private final PVector position = new PVector(0.0f, 0.0f, 0.0f);
	private final Dimensions size = new Dimensions(0.0f, 0.0f, 0.0f);

	protected GenericTextureMap textureMap = null;

	public Node(final PVector position, final Dimensions size) {
		this.position.set(position);
		this.size.set(size);
	}

	public boolean isAir() {
		return false; // DEBUG Temp only
	}

	public boolean isSolid() {
		return this.textureMap != null; // DEBUG Temp only
	}

	public boolean isGas() {
		return false; // DEBUG Temp only
	}

	public void regenerateMesh(final PShape mesh, final boolean[] neighbor) {
		if (!this.isSolid()) return;

		final PVector min = new PVector(this.getPosition().x - this.getSize().width / 2,
				this.getPosition().y - this.getSize().height / 2, this.getPosition().z - this.getSize().depth / 2);
		final PVector max = new PVector(this.getPosition().x + this.getSize().width / 2,
				this.getPosition().y + this.getSize().height / 2, this.getPosition().z + this.getSize().depth / 2);

		// FIXME Make a Texture Manager that will be used to retrieve the texture and
		// the U & V Coordinates

		// +Z "left" face
		if (!neighbor[Collidable.LEFT]) {
			mesh.vertex(min.x, min.y, max.z, this.textureMap.front % 16 * 0.0625f,
					(float) (Math.floor(this.textureMap.front / 16) * 0.0625f));
			mesh.vertex(max.x, min.y, max.z, this.textureMap.front % 16 * 0.0625f + 0.0625f,
					(float) (Math.floor(this.textureMap.front / 16) * 0.0625f));
			mesh.vertex(max.x, max.y, max.z, this.textureMap.front % 16 * 0.0625f + 0.0625f,
					(float) (Math.floor(this.textureMap.front / 16) * 0.0625f + 0.0625f));
			mesh.vertex(min.x, max.y, max.z, this.textureMap.front % 16 * 0.0625f,
					(float) (Math.floor(this.textureMap.front / 16) * 0.0625f + 0.0625f));
		}

		// -Z "right" face
		if (!neighbor[Collidable.RIGHT]) {
			mesh.vertex(max.x, min.y, min.z, this.textureMap.back % 16 * 0.0625f,
					(float) (Math.floor(this.textureMap.back / 16) * 0.0625f));
			mesh.vertex(min.x, min.y, min.z, this.textureMap.back % 16 * 0.0625f + 0.0625f,
					(float) (Math.floor(this.textureMap.back / 16) * 0.0625f));
			mesh.vertex(min.x, max.y, min.z, this.textureMap.back % 16 * 0.0625f + 0.0625f,
					(float) (Math.floor(this.textureMap.back / 16) * 0.0625f + 0.0625f));
			mesh.vertex(max.x, max.y, min.z, this.textureMap.back % 16 * 0.0625f,
					(float) (Math.floor(this.textureMap.back / 16) * 0.0625f + 0.0625f));
		}

		// +Y "bottom" face
		if (!neighbor[Collidable.CEIL]) {
			mesh.vertex(min.x, max.y, max.z, this.textureMap.bottom % 16 * 0.0625f,
					(float) (Math.floor(this.textureMap.bottom / 16) * 0.0625f));
			mesh.vertex(max.x, max.y, max.z, this.textureMap.bottom % 16 * 0.0625f + 0.0625f,
					(float) (Math.floor(this.textureMap.bottom / 16) * 0.0625f));
			mesh.vertex(max.x, max.y, min.z, this.textureMap.bottom % 16 * 0.0625f + 0.0625f,
					(float) (Math.floor(this.textureMap.bottom / 16) * 0.0625f + 0.0625f));
			mesh.vertex(min.x, max.y, min.z, this.textureMap.bottom % 16 * 0.0625f,
					(float) (Math.floor(this.textureMap.bottom / 16) * 0.0625f + 0.0625f));
		}

		// -Y "top" face
		if (!neighbor[Collidable.FLOOR]) {
			mesh.vertex(min.x, min.y, min.z, this.textureMap.top % 16 * 0.0625f,
					(float) (Math.floor(this.textureMap.top / 16) * 0.0625f));
			mesh.vertex(max.x, min.y, min.z, this.textureMap.top % 16 * 0.0625f + 0.0625f,
					(float) (Math.floor(this.textureMap.top / 16) * 0.0625f));
			mesh.vertex(max.x, min.y, max.z, this.textureMap.top % 16 * 0.0625f + 0.0625f,
					(float) (Math.floor(this.textureMap.top / 16) * 0.0625f + 0.0625f));
			mesh.vertex(min.x, min.y, max.z, this.textureMap.top % 16 * 0.0625f,
					(float) (Math.floor(this.textureMap.top / 16) * 0.0625f + 0.0625f));
		}

		// +X "front" face
		if (!neighbor[Collidable.FRONT]) {
			mesh.vertex(max.x, min.y, max.z, this.textureMap.right % 16 * 0.0625f,
					(float) (Math.floor(this.textureMap.right / 16) * 0.0625f));
			mesh.vertex(max.x, min.y, min.z, this.textureMap.right % 16 * 0.0625f + 0.0625f,
					(float) (Math.floor(this.textureMap.right / 16) * 0.0625f));
			mesh.vertex(max.x, max.y, min.z, this.textureMap.right % 16 * 0.0625f + 0.0625f,
					(float) (Math.floor(this.textureMap.right / 16) * 0.0625f + 0.0625f));
			mesh.vertex(max.x, max.y, max.z, this.textureMap.right % 16 * 0.0625f,
					(float) (Math.floor(this.textureMap.right / 16) * 0.0625f + 0.0625f));
		}

		// -X "back" face
		if (!neighbor[Collidable.BACK]) {
			mesh.vertex(min.x, min.y, min.z, this.textureMap.left % 16 * 0.0625f,
					(float) (Math.floor(this.textureMap.left / 16) * 0.0625f));
			mesh.vertex(min.x, min.y, max.z, this.textureMap.left % 16 * 0.0625f + 0.0625f,
					(float) (Math.floor(this.textureMap.left / 16) * 0.0625f));
			mesh.vertex(min.x, max.y, max.z, this.textureMap.left % 16 * 0.0625f + 0.0625f,
					(float) (Math.floor(this.textureMap.left / 16) * 0.0625f + 0.0625f));
			mesh.vertex(min.x, max.y, min.z, this.textureMap.left % 16 * 0.0625f,
					(float) (Math.floor(this.textureMap.left / 16) * 0.0625f + 0.0625f));
		}
	}

	@Override
	public PVector getPosition() {
		return this.position;
	}

	@Override
	public Dimensions getSize() {
		return this.size;
	}
}
