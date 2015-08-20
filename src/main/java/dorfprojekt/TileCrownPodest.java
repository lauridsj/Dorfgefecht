package dorfprojekt;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileCrownPodest extends TileMultiBlock {

	public boolean hasCrown = false;
	public String team;

	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setBoolean("hasCrown", hasCrown);
		if(team != null) tag.setString("team", team);
	}

	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		hasCrown = tag.getBoolean("hasCrown");
		if(tag.hasKey("team")) team = tag.getString("team");
	}

	@Override
	public void replaceBlock(int x, int y, int z) {}

	@Override
	public TileMultiBlock newInstance() {
		return new TileCrownPodest();
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
	}

	public Team getTeam()
	{
		return Team.getTeam(team);
	}


}
