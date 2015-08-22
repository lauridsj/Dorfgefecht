package dorfprojekt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class Team {

	public static HashMap<String, Team> teamMap = new HashMap<String, Team>();

	public int outerX1 = Integer.MIN_VALUE;
	public int outerZ1 = Integer.MIN_VALUE;
	public int outerX2 = Integer.MIN_VALUE;
	public int outerZ2 = Integer.MIN_VALUE;

	public int innerX1 = Integer.MIN_VALUE;
	public int innerZ1 = Integer.MIN_VALUE;
	public int innerX2 = Integer.MIN_VALUE;
	public int innerZ2 = Integer.MIN_VALUE;

	public String color = EnumChatFormatting.BLACK.getFriendlyName();

	public String name;

	public ArrayList<UUID> players = new ArrayList<UUID>();

	public Coords4 crownPodest;
	public int crownsStolen = 0;
	public int battleTimeoutLeft = 0;

	private boolean currentlyAttackable = false;
	public int attackableDelayLeft = 0;
	public ArrayList<Coords4> blocksPlacedInArea = new ArrayList<Coords4>();

	public static void addTeam(Team team)
	{
		teamMap.put(team.name, team);
	}

	public static Team getTeam(String name)
	{
		return teamMap.get(name);
	}

	public int getMaxInnerX() { return Math.max(innerX1, innerX2); }
	public int getMaxInnerZ() { return Math.max(innerZ1, innerZ2); }

	public int getMinInnerX() { return Math.min(innerX1, innerX2); }
	public int getMinInnerZ() { return Math.min(innerZ1, innerZ2); }

	public int getMaxOuterX() { return Math.max(outerX1, outerX2); }
	public int getMaxOuterZ() { return Math.max(outerZ1, outerZ2); }

	public int getMinOuterX() { return Math.min(outerX1, outerX2); }
	public int getMinOuterZ() { return Math.min(outerZ1, outerZ2); }


	public boolean isInOuterBorders(int x, int z)
	{
		return x >= getMinOuterX() && x <= getMaxOuterX() && z >= getMinOuterZ() && z <= getMaxOuterZ();
	}

	public boolean isInInnerBorders(int x, int z)
	{
		return x >= getMinInnerX() && x <= getMaxInnerX() && z >= getMinInnerZ() && z <= getMaxInnerZ();
	}

	public static Team getTeamForCoords(int x, int z)
	{
		for(Team team : teamMap.values())
		{
			if(team.isInOuterBorders(x, z))
				return team;
		}
		return null;
	}

	public static Team getTeamForCoordsWithInnerBorders(int x, int z)
	{
		for(Team team : teamMap.values())
		{
			if(team.isInInnerBorders(x, z))
				return team;
		}
		return null;
	}

	public boolean isOnOuterBorder(int x, int z)
	{
		return hasOuterBorders() && (((x == getMinOuterX() || x == getMaxOuterX()) && z >= getMinOuterZ() && z <= getMaxOuterZ()) || ((z == getMinOuterZ() || z == getMaxOuterZ()) && x >= getMinOuterX() && x <= getMaxOuterX()));
	}

	public boolean isOnInnerBorder(int x, int z)
	{
		return hasInnerBorders() && (((x == getMinInnerX() || x == getMaxInnerX()) && z >= getMinInnerZ() && z <= getMaxInnerZ()) || ((z == getMinInnerZ() || z == getMaxInnerZ()) && x >= getMinInnerX() && x <= getMaxInnerX()));
	}


	public static void writeToNBT(NBTTagCompound tag)
	{
		for(String name : teamMap.keySet())
		{
			Team team = teamMap.get(name);
			NBTTagCompound teamTag = new NBTTagCompound();
			if(team.color != null) teamTag.setString("color", team.color);

			teamTag.setInteger("innerX1", team.innerX1);
			teamTag.setInteger("innerX2", team.innerX2);
			teamTag.setInteger("innerZ1", team.innerZ1);
			teamTag.setInteger("innerZ2", team.innerZ2);

			teamTag.setInteger("outerX1", team.outerX1);
			teamTag.setInteger("outerX2", team.outerX2);
			teamTag.setInteger("outerZ1", team.outerZ1);
			teamTag.setInteger("outerZ2", team.outerZ2);

			NBTTagList uuids = new NBTTagList();
			for(UUID uuid : team.players)
			{
				uuids.appendTag(new NBTTagString(uuid.toString()));
			}
			teamTag.setTag("players", uuids);

			teamTag.setTag("crownPodest", Coords4.toNBT(team.crownPodest));
			teamTag.setInteger("crownsStolen", team.crownsStolen);
			teamTag.setInteger("battleTimeoutLeft", team.battleTimeoutLeft);
			teamTag.setBoolean("currentlyAttackable", team.currentlyAttackable);

			tag.setTag(name, teamTag);
		}
	}

	public static void readFromNBT(NBTTagCompound tag)
	{
		teamMap.clear();

		for(Object o : tag.func_150296_c())
		{
			String name = (String) o;
			NBTTagCompound teamTag = tag.getCompoundTag(name);

			Team team = new Team();
			team.name = name;

			team.color = teamTag.getString("color");

			team.innerX1 = teamTag.getInteger("innerX1");
			team.innerX2 = teamTag.getInteger("innerX2");
			team.innerZ1 = teamTag.getInteger("innerZ1");
			team.innerZ2 = teamTag.getInteger("innerZ2");

			team.outerX1 = teamTag.getInteger("outerX1");
			team.outerX2 = teamTag.getInteger("outerX2");
			team.outerZ1 = teamTag.getInteger("outerZ1");
			team.outerZ2 = teamTag.getInteger("outerZ2");

			NBTTagList uuids = teamTag.getTagList("players", 8);
			for(int i = 0; i < uuids.tagCount(); i++)
			{
				team.players.add(UUID.fromString(uuids.getStringTagAt(i)));
			}

			team.crownPodest = Coords4.fromNBT(teamTag.getCompoundTag("crownPodest"));
			team.crownsStolen = teamTag.getInteger("crownsStolen");
			team.battleTimeoutLeft = teamTag.getInteger("battleTimeoutLeft");
			team.currentlyAttackable = teamTag.getBoolean("currentlyAttackable");

			teamMap.put(name, team);
		}
	}

	public static Team getTeamForPlayer(EntityPlayer player)
	{
		UUID uuid = player.getUniqueID();
		for(Team team : teamMap.values())
		{
			if(team.players.contains(uuid))
				return team;
		}
		return null;
	}

	public static void setTeamForPlayer(EntityPlayer player, Team team)
	{
		UUID uuid = player.getUniqueID();
		for(Team t : teamMap.values())
		{
			if(t.players.contains(uuid))
				t.players.remove(uuid);
		}
		if(team != null)
		{
			team.players.add(uuid);
		}
	}

	public static FMLProxyPacket getUpdatePacket() 
	{
		NBTTagCompound tag = new NBTTagCompound();
		Team.writeToNBT(tag);

		StreamHelper.newOutputStream();
		StreamHelper.writeInt(NetworkHandler.UPDATE_TEAMS);
		StreamHelper.writeNBTTagCompound(tag);

		return StreamHelper.getPacket();
	}

	public static void sendClientUpdates()
	{
		if(FMLCommonHandler.instance().getSide().isServer())
		{
			MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(getUpdatePacket());
		}
	}

	public ArrayList<EntityPlayer> getOnlinePlayers()
	{
		ArrayList<EntityPlayer> onlinePlayers = new ArrayList<EntityPlayer>();
		for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList)
		{
			EntityPlayer ep = (EntityPlayer) o;
			if(players.contains(ep.getUniqueID()))
			{
				onlinePlayers.add(ep);
			}
		}
		return onlinePlayers;
	}

	public boolean isAttackable() 
	{
		return this.currentlyAttackable;
	}

	public boolean isPlayerInTeam(EntityPlayer player)
	{
		return players.contains(player.getUniqueID());
	}

	public void setAttackable(boolean attackable)
	{
		this.currentlyAttackable = attackable;
		if(!attackable)
		{
			blocksPlacedInArea.clear();
		}
		if(FMLCommonHandler.instance().getSide().isServer())
		{
			if(!attackable)
			{
				World world = MinecraftServer.getServer().getEntityWorld();
				for(Object o : world.getEntitiesWithinAABB(EntityPlayer.class, getOuterBB()))
				{
					EntityPlayer player = (EntityPlayer) o;
					if(!this.isPlayerInTeam(player) && ! player.capabilities.isCreativeMode)
					{
						this.teleportEntityToBorders(player);
					}
				}
			}
			System.out.println("Sending attackable Packet");
			sendClientUpdates();
		}
	}

	/*public FMLProxyPacket getAttackablePacket()
	{
		StreamHelper.newOutputStream();
		StreamHelper.writeInt(NetworkHandler.UPDATE_ATTACKABLE);
		StreamHelper.writeString(name);
		StreamHelper.writeBoolean(currentlyAttackable);
		return StreamHelper.getPacket();
	}*/

	public boolean isCurrentlyAttacked(World world)
	{
		if(this.isAttackable())
		{
			List list;
			if(Dorfprojekt.slowdownInOuterBorders)
			{
				list = world.getEntitiesWithinAABB(EntityPlayer.class, this.getOuterBB());
			}
			else
			{
				list = world.getEntitiesWithinAABB(EntityPlayer.class, this.getInnerBB());
			}
			for(Object o : list)
			{
				EntityPlayer player = (EntityPlayer) o;
				if(!this.isPlayerInTeam(player))
				{
					return true;
				}
			}
		}
		return false;
	}

	public AxisAlignedBB getInnerBB()
	{
		return AxisAlignedBB.getBoundingBox(getMinInnerX(), 0, getMinInnerZ(), getMaxInnerX(), 256, getMaxInnerZ());
	}

	public AxisAlignedBB getOuterBB()
	{
		return AxisAlignedBB.getBoundingBox(getMinOuterX(), 0, getMinOuterZ(), getMaxOuterX(), 256, getMaxOuterZ());
	}

	public void teleportEntityToBorders(EntityPlayer ent)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			EntityPlayerMP emp = (EntityPlayerMP) ent;
			double x = ent.posX;
			double z = ent.posZ;
			int i = Util.getMinimalArg(getMaxOuterX() - x, getMaxOuterZ() - z, x - getMinOuterX(), z - getMinOuterZ());

			switch(i)
			{
			case 0:
				x = getMaxOuterX() + 2;
				break;
			case 1:
				z = getMaxOuterZ() + 2;
				break;
			case 2:
				x = getMinOuterX() - 2;
				break;
			case 3:
				z = getMinOuterZ() - 2;
				break;
			}

			int y = Util.getTopBlock(ent.worldObj, MathHelper.floor_double(x), MathHelper.floor_double(z)) + 1;
			System.out.printf("Teleporting to: %s %s %s\n", x, y, z);

			emp.playerNetServerHandler.setPlayerLocation(x, y, z, emp.rotationPitch, emp.rotationYaw);
		}
	}

	public void setBattleTimeout(int ticks)
	{
		this.battleTimeoutLeft = ticks;
		String s = ticks + " ticks";
		if(ticks > 1728000)
		{
			s = ((double)ticks / 1728000d) + " d";
		}
		else if(ticks > 72000)
		{
			s = ((double)ticks / 72000d) + " h";
		}
		else if(ticks > 1200)
		{
			s = ((double)ticks / 1200d) + " min";
		}
		else if(ticks > 20)
		{
			s = ((double)ticks / 20d) + " s";
		}

		if(ticks > 0)
		{
			this.setAttackable(false);
		}
		Util.sendTranslatedChatToAll("dorfprojekt.battleTimeout", this.getColoredName(), s);
		Team.sendClientUpdates();
	}

	public boolean hasOuterBorders()
	{
		return outerX1 != Integer.MIN_VALUE && outerX2 != Integer.MIN_VALUE && outerZ1 != Integer.MIN_VALUE && outerZ2 != Integer.MIN_VALUE;
	}

	public boolean hasInnerBorders()
	{
		return innerX1 != Integer.MIN_VALUE && innerX2 != Integer.MIN_VALUE && innerZ1 != Integer.MIN_VALUE && innerZ2 != Integer.MIN_VALUE;
	}

	public void respawnCrown()
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			if(crownPodest != null && crownPodest.getBlock() == Dorfprojekt.crownPodestBlock && crownPodest.getTileEntity() != null)
			{
				System.out.println("Crown respawned on Podest");
				TileCrownPodest tcp2 = (TileCrownPodest) crownPodest.getTileEntity();
				tcp2.hasCrown = true;
				tcp2.markDirty();
				Util.sendTileEntityUpdates(tcp2);


			}
			else if(FMLCommonHandler.instance().getEffectiveSide().isServer())
			{

				int spawnx = (getMaxInnerX() + getMinInnerX()) / 2;
				int spawnz = (getMaxInnerZ() + getMinInnerZ()) / 2;

				System.out.println("Crown respawned at " + spawnx + "; " + spawnz);			

				World world = MinecraftServer.getServer().getEntityWorld();
				EntityItem eitem = new EntityItem(world, spawnx, 200, spawnz, ItemCrown.getCrownForTeam(this));
				world.spawnEntityInWorld(eitem);
				eitem.lifespan = Integer.MAX_VALUE;
			}
			Util.sendTranslatedChatToAll("dorfprojekt.crownRespawned", this.getColoredName());
		}


	}

	public int getColor()
	{
		EnumChatFormatting ecf = EnumChatFormatting.getValueByName(color);
		if(ecf == null)
		{
			return 0;
		}
		else
		{
			return Util.getColor(ecf);
		}
	}

	public String getColoredName()
	{
		if(color == null)
		{
			return name;
		}
		else
		{
			return Util.FORMAT_CHAR + EnumChatFormatting.getValueByName(color).getFormattingCode() + name + Util.FORMAT_CHAR + EnumChatFormatting.RESET.getFormattingCode();
		}
	}

	public TileCrownPodest getCrownPodest()
	{
		if(this.crownPodest != null && this.crownPodest.getTileEntity() != null && this.crownPodest.getTileEntity() instanceof TileCrownPodest)
		{
			return (TileCrownPodest) this.crownPodest.getTileEntity();
		}
		else
		{
			return null;
		}
	}

}
