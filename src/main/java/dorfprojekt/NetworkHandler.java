package dorfprojekt;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class NetworkHandler {

	public static final int UPDATE_TEAMS = 0;
	public static final int UPDATE_ATTACKABLE = 1;
	public static final int UPDATE_CONFIG = 2;

	@SubscribeEvent
	public void onServerCustomPacket(FMLNetworkEvent.ServerCustomPacketEvent event)
	{
		
	}
	
}
