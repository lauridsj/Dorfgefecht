package dorfprojekt;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class FMLEventHandler {
/*
	@SubscribeEvent
	public void onServerConnectionFromClient(FMLNetworkEvent.ServerConnectionFromClientEvent event)
	{
		if(FMLCommonHandler.instance().getSide().isServer())
		{			
			NetHandlerPlayServer handler = (NetHandlerPlayServer) event.handler;
			System.out.println(handler.playerEntity);
				Dorfprojekt.networkChannel.sendToAll(Team.getUpdatePacket());
			
		}
	}*/
	
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			EntityPlayerMP emp = (EntityPlayerMP) event.player;
			Dorfprojekt.networkChannel.sendTo(Team.getUpdatePacket(), emp);
			Dorfprojekt.networkChannel.sendTo(Dorfprojekt.getConfigPacket(), emp);
		}
	}
	
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event)
	{
		if(event.phase == TickEvent.Phase.START)
		{
			for(Coords4 coords : BlockBorder.scheduledBorders.keySet())
			{
				coords.setBlock(Dorfprojekt.borderBlock, BlockBorder.scheduledBorders.get(coords));
			}
			BlockBorder.scheduledBorders.clear();
			
			
			for(Team team : Team.teamMap.values())
			{
				if(team.isAttackable())
				{
					if(team.getOnlinePlayers().size() < Dorfprojekt.minAttackablePlayers)
					{
						if(team.attackableDelayLeft > 0)
						{
							if(team.attackableDelayLeft % 200 == 0)
							{
								MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("dorfprojekt.delayLeft", team.getColoredName(), team.attackableDelayLeft / 20));
							}
							team.attackableDelayLeft--;
						}
						else
						{
							team.setAttackable(false);
							MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("dorfprojekt.notAttackable", team.name));
						}
					}
					else
					{
						team.attackableDelayLeft = Dorfprojekt.attackableDelay;
					}
				}
				else
				{
					if(team.getOnlinePlayers().size() >= Dorfprojekt.minAttackablePlayers && team.battleTimeoutLeft <= 0)
					{
						team.setAttackable(true);
						System.out.println("Team is now attackable");
						Util.sendTranslatedChatToAll("dorfprojekt.attackable", team.name);
					}
				}
				
				if(team.battleTimeoutLeft > 0)
				{
					team.battleTimeoutLeft--;
					if(team.battleTimeoutLeft == 0)
					{
						Util.sendTranslatedChatToAll("dorfprojekt.battleTimeoutOver", team.name);
						if(team.getOnlinePlayers().size() >= Dorfprojekt.minAttackablePlayers)
						{
							team.setAttackable(true);
							Util.sendTranslatedChatToAll("dorfprojekt.attackable", team.name);
						}
						Team.sendClientUpdates();
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
	{
		for(int i = 0; i < event.player.inventory.getSizeInventory(); i++)
		{
			ItemStack item = event.player.inventory.getStackInSlot(i);
			if(item != null && item.getItem() == Dorfprojekt.crownItem && ItemCrown.getTeam(item) != null)
			{
				event.player.inventory.setInventorySlotContents(i, null);
				event.player.entityDropItem(item, 1);
			}
		}
	}
	
	@SubscribeEvent
	public void onClientDisconnectionFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
	{
		Dorfprojekt.loadConfig();
	}
	
	@SubscribeEvent
	public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
	{
		Team team = Team.getTeamForPlayer(event.player);
		if(team != null)
		{
			if(team.getCrownPodest() != null)
			{
				TileEntity te = team.getCrownPodest();
				event.player.setPosition(te.xCoord + 0.5, te.yCoord + 1, te.zCoord + 0.5);
			}
			else
			{
				World w = MinecraftServer.getServer().getEntityWorld();
				ChunkCoordinates cc = w.getSpawnPoint();
				event.player.setPosition(cc.posX, cc.posY, cc.posZ);
			}
		}
	}
	
}
