package dorfprojekt;

import org.lwjgl.opengl.GL11;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class CrownRenderer implements IItemRenderer {

	public static CrownRenderer instance;
	
	private final ResourceLocation crownLoc = new ResourceLocation("dorfprojekt:models/crown.obj");	
	private WavefrontModel crownModel;
	
	public CrownRenderer()
	{
		crownModel = WavefrontModel.getModel(crownLoc);
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return type != ItemRenderType.FIRST_PERSON_MAP;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if(item.getItem() instanceof ItemCrown)
		{
			if(type == ItemRenderType.ENTITY)
			{
				//GL11.glTranslatef(0f, 0f, 0f);
				GL11.glScalef(2f, 2f, 2f);
			}
			else if(type == ItemRenderType.INVENTORY)
			{
				//GL11.glRotatef(20f, 1f, 0f, 0f);
				//GL11.glRotatef(-20f, 0f, 0f, 1f);
				GL11.glScalef(1.5f, 1.5f, 1.5f);
				GL11.glTranslatef(0.5f, 0.5875f, 0.5f);	
				
			}
			else
			{
				GL11.glTranslatef(0.5f, 0.5f, 0.5f);
			}
			GL11.glScalef(3f, 3f, 3f);
			this.renderCrown();
		}

	}
	
	public void renderCrown()
	{
		GL11.glScaled(0.038, 0.038, 0.038);
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LIGHTING);
		
		crownModel.render();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
