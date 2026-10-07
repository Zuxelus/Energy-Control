package com.zuxelus.energycontrol.renderers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.renderers.CubeRenderer.PositionTextureVertex;
import com.zuxelus.energycontrol.renderers.CubeRenderer.TexturedQuad;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;

// PanelModel bakes it, so the screen borders match findTexture()
@Environment(EnvType.CLIENT)
public class PanelCube {
	public static final PanelCube MODEL = new PanelCube(0, 0, new RotationOffset(), false);
	// chunk meshes are built on several threads
	private static final Map<ModelKey, PanelCube> LIBRARY = new ConcurrentHashMap<>();
	private static final Map<ModelKey, PanelCube> LIBRARY_FACE = new ConcurrentHashMap<>();
	public static final int FACE = 4;

	private final ModelBox cube;

	public PanelCube(int faceTexU, int faceTexV, RotationOffset offset, boolean faceOnly) {
		cube = new ModelBox(faceTexU, faceTexV, 0.0F, 0.0F, 0.0F, 32, 32, 32, 128, 128, offset.leftTop, offset.leftBottom, offset.rightTop, offset.rightBottom, faceOnly);
	}

	// Hands every quad to the visitor in block coordinates (0..1), transformed by matrixStack,
	// so a baked model can use the same geometry. u and v are relative to the texture (0..1).
	public void visitQuads(PoseStack matrixStack, QuadVisitor visitor) {
		matrixStack.pushPose();
		matrixStack.scale(0.5F, 0.5F, 0.5F);
		Matrix4f matrix4f = matrixStack.last().pose();
		Matrix3f matrix3f = matrixStack.last().normal();
		for (int n = 0; n < cube.quads.length; n++) {
			TexturedQuad quad = cube.quads[n];
			Vector3f[] positions = new Vector3f[4];
			float[] u = new float[4];
			float[] v = new float[4];
			for (int i = 0; i < 4; i++) {
				PositionTextureVertex vertex = quad.vertexPositions[i];
				Vector4f pos = matrix4f.transform(new Vector4f(vertex.position.x() / 16.0F, vertex.position.y() / 16.0F, vertex.position.z() / 16.0F, 1.0F));
				positions[i] = new Vector3f(pos.x(), pos.y(), pos.z());
				u[i] = vertex.textureU;
				v[i] = vertex.textureV;
			}
			visitor.accept(n, positions, u, v, matrix3f.transform(new Vector3f(quad.normal)));
		}
		matrixStack.popPose();
	}

	public interface QuadVisitor {
		void accept(int index, Vector3f[] positions, float[] u, float[] v, Vector3f normal);
	}

	private static class ModelBox {
		private final TexturedQuad[] quads;

		public ModelBox(int texOffX, int texOffY, float x, float y, float z, float dx, float dy, float dz, float texWidth, float texHeight, float leftTop, float leftBottom, float rightTop, float rightBottom, boolean faceOnly) {
			quads = faceOnly ? new TexturedQuad[1] : new TexturedQuad[6];
			float f = x + dx;
			float f1 = y + dy;
			float f2 = z + dz;
			PositionTextureVertex v7 = new PositionTextureVertex(x, y, z + rightBottom, 0.0F, 0.0F);
			PositionTextureVertex v = new PositionTextureVertex(f, y, z + leftBottom, 0.0F, 8.0F);
			PositionTextureVertex v1 = new PositionTextureVertex(f, f1, z + leftTop, 8.0F, 8.0F);
			PositionTextureVertex v2 = new PositionTextureVertex(x, f1, z + rightTop, 8.0F, 0.0F);
			PositionTextureVertex v3 = new PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
			PositionTextureVertex v4 = new PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
			PositionTextureVertex v5 = new PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
			PositionTextureVertex v6 = new PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);
			if (faceOnly) {
				quads[0] = new TexturedQuad(new PositionTextureVertex[] { v2, v1, v, v7 }, texOffX, texOffY, texOffX + dx , texOffY + dx, texWidth, texHeight, Direction.NORTH);
				return;
			}
			quads[0] = new TexturedQuad(new PositionTextureVertex[] { v1, v5, v4, v }, 0, dz, dz, dz + dy, texWidth, texHeight, Direction.EAST); // left
			quads[1] = new TexturedQuad(new PositionTextureVertex[] { v6, v2, v7, v3 }, dz + dx, dz, dz + dx + dz, dz + dy, texWidth, texHeight, Direction.WEST); // right
			quads[2] = new TexturedQuad(new PositionTextureVertex[] { v7, v, v4, v3 }, dz, dz + dz, dz + dx, dz + dz + dz, texWidth, texHeight, Direction.DOWN); // bottom
			quads[3] = new TexturedQuad(new PositionTextureVertex[] { v6, v5, v1, v2 }, dz, 0, dz + dx, dz, texWidth, texHeight, Direction.UP); // top
			quads[4] = new TexturedQuad(new PositionTextureVertex[] { v2, v1, v, v7 }, dz, dz, dz + dx, dz + dy, texWidth, texHeight, Direction.NORTH); // face
			quads[5] = new TexturedQuad(new PositionTextureVertex[] { v5, v6, v3, v4 }, dz + dx + dz, dz, dz + dx + dz + dx, dz + dy, texWidth, texHeight, Direction.SOUTH); // back
		}
	}

	public static void rotateBlock(PoseStack matrixStack, Direction facing, Direction rotation) {
		if (rotation == null)
			rotation = Direction.NORTH;

		switch (facing) {
		case UP:
			switch(rotation) {
			case NORTH:
				matrixStack.rotate(Axis.XP.rotationDegrees(90));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case SOUTH:
				matrixStack.rotate(Axis.XP.rotationDegrees(90));
				matrixStack.rotate(Axis.ZP.rotationDegrees(180));
				matrixStack.translate(-1.0F, -1.0F, -1.0F);
				break;
			case WEST:
				matrixStack.rotate(Axis.XP.rotationDegrees(90));
				matrixStack.rotate(Axis.ZP.rotationDegrees(-90));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case EAST:
				matrixStack.rotate(Axis.XP.rotationDegrees(90));
				matrixStack.rotate(Axis.ZP.rotationDegrees(90));
				matrixStack.translate(0.0F, -1.0F, -1.0F);
				break;
			default:
				break;
			}
			break;
		case DOWN:
			switch(rotation) {
			case NORTH:
				matrixStack.rotate(Axis.XP.rotationDegrees(-90));
				matrixStack.rotate(Axis.ZP.rotationDegrees(180));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			case SOUTH:
				matrixStack.rotate(Axis.XP.rotationDegrees(-90));
				matrixStack.translate(0.0F, -1.0F, 0.0F);
				break;
			case WEST:
				matrixStack.rotate(Axis.XP.rotationDegrees(-90));
				matrixStack.rotate(Axis.ZP.rotationDegrees(-90));
				matrixStack.translate(0.0F, 0.0F, 0.0F);
				break;
			case EAST:
				matrixStack.rotate(Axis.XP.rotationDegrees(-90));
				matrixStack.rotate(Axis.ZP.rotationDegrees(90));
				matrixStack.translate(-1.0F, -1.0F, 0.0F);
				break;
			default:
				break;
			}
			break;
		case NORTH:
			break;
		case SOUTH:
			matrixStack.rotate(Axis.YP.rotationDegrees(180)); // 180 by Y
			matrixStack.translate(-1.0F, 0.0F, -1.0F);
			break;
		case WEST:
			matrixStack.rotate(Axis.YP.rotationDegrees(90));
			matrixStack.translate(-1.0F, 0.0F, 0.0F);
			break;
		case EAST:
			matrixStack.rotate(Axis.YP.rotationDegrees(-90));
			matrixStack.translate(0.0F, 0.0F, -1.0F);
			break;
		}
	}

	public static void rotateBlockText(PoseStack matrixStack, Direction facing, Direction rotation) {
		if (rotation == null)
			rotation = Direction.NORTH;

		switch (facing) {
		case UP:
			switch(rotation) {
			case NORTH:
				matrixStack.rotate(Axis.XP.rotationDegrees(-90));
				matrixStack.translate(0.0F, -1.0F, 1.0F);
				break;
			case SOUTH:
				matrixStack.rotate(Axis.XP.rotationDegrees(-90));
				matrixStack.rotate(Axis.YP.rotationDegrees(180));
				matrixStack.translate(-1.0F, -1.0F, 0.0F);
				break;
			case WEST:
				matrixStack.rotate(Axis.XP.rotationDegrees(-90));
				matrixStack.rotate(Axis.YP.rotationDegrees(-90));
				matrixStack.translate(0.0F, -1.0F, 0.0F);
				break;
			case EAST:
				matrixStack.rotate(Axis.XP.rotationDegrees(-90));
				matrixStack.rotate(Axis.YP.rotationDegrees(90));
				matrixStack.translate(-1.0F, -1.0F, 1.0F);
				break;
			default:
				break;
			}
			break;
		case DOWN:
			switch(rotation) {
			case NORTH:
				matrixStack.rotate(Axis.XP.rotationDegrees(-90));
				matrixStack.translate(0.0F, -1.0F, 0.0F);
				break;
			case SOUTH:
				matrixStack.rotate(Axis.XP.rotationDegrees(-90));
				matrixStack.rotate(Axis.YP.rotationDegrees(180));
				matrixStack.translate(-1.0F, -1.0F, -1.0F);
				break;
			case WEST:
				matrixStack.rotate(Axis.XP.rotationDegrees(-90));
				matrixStack.rotate(Axis.YP.rotationDegrees(-90));
				matrixStack.translate(0.0F, -1.0F, -1.0F);
				break;
			case EAST:
				matrixStack.rotate(Axis.XP.rotationDegrees(-90));
				matrixStack.rotate(Axis.YP.rotationDegrees(90));
				matrixStack.translate(-1.0F, -1.0F, 0.0F);
				break;
			default:
				break;
			}
			break;
		case NORTH:
			matrixStack.rotate(Axis.XP.rotationDegrees(-90));
			matrixStack.rotate(Axis.YP.rotationDegrees(180));
			matrixStack.translate(-1.0F, -1.0F, 0.0F);
			break;
		case SOUTH:
			matrixStack.rotate(Axis.XP.rotationDegrees(90));
			matrixStack.rotate(Axis.ZP.rotationDegrees(180));
			matrixStack.translate(-1.0F, -1.0F, 0.0F);
			break;
		case WEST:
			matrixStack.rotate(Axis.XP.rotationDegrees(-90));
			matrixStack.rotate(Axis.YP.rotationDegrees(180));
			matrixStack.translate(-1.0F, -1.0F, 0.0F);
			break;
		case EAST:
			matrixStack.rotate(Axis.ZP.rotationDegrees(180));
			matrixStack.rotate(Axis.XP.rotationDegrees(-90));
			matrixStack.translate(-1.0F, -1.0F, 0.0F);
			break;
		}
	}

	public static PanelCube getModel(RotationOffset offset) {
		return LIBRARY.computeIfAbsent(new ModelKey(0, offset), key -> new PanelCube(0, 0, offset, false));
	}

	public static PanelCube getFaceModel(RotationOffset offset, int textureId) {
		return LIBRARY_FACE.computeIfAbsent(new ModelKey(textureId, offset), key -> new PanelCube(textureId / 4 * 32, textureId % 4 * 32, offset, true));
	}

	// offsets are fractional, so they can't be packed into a number without collisions
	private record ModelKey(int textureId, float leftTop, float leftBottom, float rightTop, float rightBottom) {
		ModelKey(int textureId, RotationOffset offset) {
			this(textureId, offset.leftTop, offset.leftBottom, offset.rightTop, offset.rightBottom);
		}
	}
}
