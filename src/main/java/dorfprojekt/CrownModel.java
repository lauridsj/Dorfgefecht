package dorfprojekt;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class CrownModel extends ModelBiped {

	public static CrownModel instance = new CrownModel();

	public void render(Entity ent, float f1, float f2, float f3, float f4, float f5, float f6)
	{
		if(ent instanceof EntityLivingBase)
		{
			//this.setRotationAngles(f1, f2, f3, f4, f5, f6, ent);
			EntityLivingBase elb = (EntityLivingBase) ent;
			GL11.glPushMatrix();
			//GL11.glTranslated(ent.posX, ent.posY, ent.posZ);
			float posY = this.isSneak ? 1f : 0f;
			GL11.glRotatef(f4, 0f, 1f, 0f);
			GL11.glRotatef(f5, 1f, 0f, 0f);
			GL11.glTranslatef(0.25f, posY - 0.5f, 0.25f);
			GL11.glScalef(-2f, -2f, -2f);
			CrownRenderer.instance.renderCrown();

			GL11.glPopMatrix();
		}
	}
	
	


}
