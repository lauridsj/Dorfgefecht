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
			NBTTagCompound tag = StreamHelper.readNBTTagCompound();		
			Team.readFromNBT(tag);
			System.out.printf("Recieved Team Update Packet: %s\n", tag.toString());

			//Minecraft.getMinecraft().renderGlobal.loadRenderers();
		}
		else if(packetID == UPDATE_ATTACKABLE)
		{
			Team team = Team.getTeam(StreamHelper.readString());
			boolean attackable = StreamHelper.readBoolean();
			team.setAttackable(attackable);
			System.out.printf("Recieved Attackable Update Packet: Team %s; %s\n", team.name, attackable);

		}
		else if(packetID == UPDATE_CONFIG)
		{
			System.out.println("Recieved Config update Packet");
			Dorfprojekt.recieveConfig();
		}
	}
	
}
