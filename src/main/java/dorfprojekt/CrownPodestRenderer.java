package dorfprojekt;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class CrownPodestRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {

	private final ResourceLocation modelLoc = new ResourceLocation("dorfprojekt:models/crown_podest.obj");

	private WavefrontModel model;

	public CrownPodestRenderer()
	{
		model = WavefrontModel.getModel(modelLoc);

	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		BlockCrownPodest bcp = Dorfprojekt.crownPodestBlock;
		TextureManager texmgr = Minecraft.getMinecraft().getTextureManager();

		WavefrontMaterial.getMaterial("wool").setColors(1, 1, 1);
		
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LIGHTING);
		
		model.render();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);


	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return Dorfprojekt.crownPodestRenderID;
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partTicks) {
		BlockCrownPodest bcp = Dorfprojekt.crownPodestBlock;
		TileCrownPodest tcp = (TileCrownPodest) te;
		TextureManager texmgr = Minecraft.getMinecraft().getTextureManager();
		Tessellator tess = Tessellator.instance;

		if(tcp.isSource)
		{
			if(tcp.team != null)
			{
				Team team = Team.getTeam(tcp.team);
				//Tessellator.instance.setColorOpaque(team.colorR, team.colorG, team.colorB);
				//GL11.glColor3f((float) team.colorR / 255f, (float) team.colorG / 255f, (float) team.colorB / 255f);
				int color = team.getColor();
				WavefrontMaterial.getMaterial("wool").setColors((float) Util.getRed(color) / 255f, (float) Util.getGreen(color) / 255f, (float) Util.getBlue(color) / 255f);
			}
			else
			{
				WavefrontMaterial.getMaterial("wool").setColors(1, 1, 1);
			}
			
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_LIGHTING);
			
			GL11.glTranslated(x, y, z);
			model.render();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
						
			
			if(tcp.hasCrown)
			{
				GL11.glPushMatrix();
				GL11.glTranslated(x, y, z);
				GL11.glTranslated(0.1, 0.75, 0.1);
				GL11.glScalef(3f, 3f, 3f);
				CrownRenderer.instance.renderCrown();
				GL11.glPopMatrix();
			}

		
			
		}


		
	}

}
