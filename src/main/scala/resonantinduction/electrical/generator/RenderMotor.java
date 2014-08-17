package resonantinduction.electrical.generator;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import resonant.content.prefab.scala.render.ISimpleItemRenderer;
import resonant.lib.render.RenderUtility;
import resonantinduction.core.Reference;

/**
 * @author Calclavia
 * 
 */
public class RenderMotor extends TileEntitySpecialRenderer implements ISimpleItemRenderer
{
	public static final IModelCustom MODEL = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain(), Reference.modelPath() + "generator.tcn"));
	public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.domain(), Reference.modelPath() + "generator.png");

	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
	{
		doRender(t.getBlockMetadata(), x, y, z, f);
	}

	private void doRender(int facingDirection, double x, double y, double z, float f)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);
		GL11.glRotatef(90, 0, 1, 0);
		RenderUtility.rotateBlockBasedOnDirection(ForgeDirection.getOrientation(facingDirection));
		bindTexture(TEXTURE);
		MODEL.renderAll();
		GL11.glPopMatrix();
	}

	@Override
	public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
	{
		doRender(2, 0, 0, 0, 0);
	}
}
