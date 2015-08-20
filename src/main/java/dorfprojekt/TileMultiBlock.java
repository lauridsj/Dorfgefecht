package dorfprojekt;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public abstract class TileMultiBlock extends TileEntity {

	public boolean isSource = true;
	public boolean replaceOthers = false;
	public int sourcex = 0;
	public int sourcey = 0;
	public int sourcez = 0;

	public void onPartRemoved(int partx, int party, int partz)
	{
		if(replaceOthers)
		{
			TileMultiBlock te = this.newInstance();
			te.setSourcePosition(partx, party, partz);
			this.replaceBlock(partx, party, partz);
			this.worldObj.setTileEntity(partx, party, partz, te);
		}
		else
		{
			this.clearBlock(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public void updateEntity()
	{
		if(this.isSource && this.blockMetadata == 1)
		{
			this.isSource = false;
		}
		if(!isSource)
		{
			if(this.worldObj.getTileEntity(sourcex, sourcey, sourcez) == null || !(this.worldObj.getTileEntity(sourcex, sourcey, sourcez) instanceof TileMultiBlock))
			{
				this.clearBlock(xCoord, yCoord, zCoord);
			}
		}
	}

	public abstract void replaceBlock(int x, int y, int z);

	public abstract TileMultiBlock newInstance();

	public void setSourcePosition(int x, int y, int z)
	{
		this.sourcex = x;
		this.sourcey = y;
		this.sourcez = z;
		this.isSource = false;
	}

	@Override
	public void invalidate()
	{
		super.invalidate();
		if(!isSource && this.getSource() != null)
		{
			this.getSource().onPartRemoved(xCoord, yCoord, zCoord);
		}
	}

	public TileMultiBlock getSource()
	{
		if(isSource)
		{
			return this;
		}
		else
		{
			TileEntity te = this.worldObj.getTileEntity(sourcex, sourcey, sourcez);
			if(te instanceof TileMultiBlock)
			{
				return (TileMultiBlock) te;
			}
			else
			{
				return null;
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setBoolean("multi_isSource", this.isSource);
		par1NBTTagCompound.setInteger("multi_sourcex", this.sourcex);
		par1NBTTagCompound.setInteger("multi_sourcey", this.sourcey);
		par1NBTTagCompound.setInteger("multi_sourcez", this.sourcez);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		this.isSource = par1NBTTagCompound.getBoolean("multi_isSource");
		this.sourcex = par1NBTTagCompound.getInteger("multi_sourcex");
		this.sourcey = par1NBTTagCompound.getInteger("multi_sourcey");
		this.sourcez = par1NBTTagCompound.getInteger("multi_sourcez");
		
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbttagcompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		this.readFromNBT(pkt.func_148857_g());

	}

	public void clearBlock(int x, int y, int z)
	{
		this.worldObj.setBlockToAir(x, y, z);
	}
	
	public static void placeMultiBlock(World world, int x, int y, int z, AxisAlignedBB bounds, Block block)
	{
		world.setBlock(x, y, z, block);
		
		for(int i = (int)bounds.minX; i <= (int)bounds.maxX; i++)
		{
			for(int j = (int)bounds.minY; j <= (int)bounds.maxY; j++)
			{
				for(int k = (int)bounds.minZ; k <= (int)bounds.maxZ; k++)
				{
					if(i != 0 || j != 0 || k != 0)
					{
						world.setBlock(x + i, y + j, z + k, block);
						TileMultiBlock tmb = (TileMultiBlock) world.getTileEntity(x + i, y + j, z + k);
						tmb.setSourcePosition(x, y, z);
					}
				}
			}
		}
		
	}
	
	public static boolean canPlaceAt(World world, int x, int y, int z, AxisAlignedBB bounds)
	{
		for(int i = (int)bounds.minX; i <= (int)bounds.maxX; i++)
		{
			for(int j = (int)bounds.minY; j <= (int)bounds.maxY; j++)
			{
				for(int k = (int)bounds.minZ; k <= (int)bounds.maxZ; k++)
				{
					if(!world.getBlock(i + x, j + y, k + z).isReplaceable(world, i + x, j + y, k + z)) return false;
				}
			}
		}
		
		return true;
	}

}
