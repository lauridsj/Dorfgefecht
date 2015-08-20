package dorfprojekt;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	public void preInit()
	{
		Dorfprojekt.configFile = new File(Minecraft.getMinecraft().mcDataDir, "config/Dorfprojekt.cfg");
	}
	
	public void postInit() 
	{
		Dorfprojekt.borderRenderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(Dorfprojekt.borderRenderID, new BorderRenderer());
		
		CrownPodestRenderer cpr = new CrownPodestRenderer();
		Dorfprojekt.crownPodestRenderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(Dorfprojekt.crownPodestRenderID, cpr);
		
		Dorfprojekt.networkChannel.register(new ClientNetworkHandler());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileCrownPodest.class, cpr);
		
		CrownRenderer.instance = new CrownRenderer();
		MinecraftForgeClient.registerItemRenderer(Dorfprojekt.crownItem, CrownRenderer.instance);
		
		RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 0f;
	}
	
}
