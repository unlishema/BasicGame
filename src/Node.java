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
    // Get the Min and Max Position of this Collidable
    final PVector min = new PVector(this.getPosition().x - this.getSize().width / 2,
        this.getPosition().y - this.getSize().height / 2, this.getPosition().z - this.getSize().depth / 2);
    final PVector max = new PVector(this.getPosition().x + this.getSize().width / 2,
        this.getPosition().y + this.getSize().height / 2, this.getPosition().z + this.getSize().depth / 2);

    final boolean xAny = min.x <= vector.x && vector.x <= max.x;
    final boolean yAny = min.y <= vector.y && vector.y <= max.y;
    final boolean zAny = min.z <= vector.x && vector.x <= max.z;
    return xAny && yAny && zAny;
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
