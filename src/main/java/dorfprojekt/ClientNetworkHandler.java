package dorfprojekt;

import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class ClientNetworkHandler extends NetworkHandler {

	@SubscribeEvent
	public void onClientCustomPacket(FMLNetworkEvent.ClientCustomPacketEvent event)
	{
		
		StreamHelper.bindInputStream(event.packet.payload().array());
		int packetID = StreamHelper.readInt();
		
		if(packetID == UPDATE_TEAMS)
		{
			System.out.println("Recieved Team Update Packet");
			NBTTagCompound tag = StreamHelper.readNBTTagCompound();		
			Team.readFromNBT(tag);
			//Minecraft.getMinecraft().renderGlobal.loadRenderers();
		}
		else if(packetID == UPDATE_ATTACKABLE)
		{
			System.out.println("Recieved Attackable Update Packet");
			Team team = Team.getTeam(StreamHelper.readString());
			team.setAttackable(StreamHelper.readBoolean());
		}
		else if(packetID == UPDATE_CONFIG)
		{
			System.out.println("Recieved Config update Packet");
			Dorfprojekt.recieveConfig();
		}
	}
	
}
