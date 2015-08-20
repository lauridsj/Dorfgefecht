package dorfprojekt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCrownPodest extends BlockContainer {

	private final AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(-1, 0, -1, 1, 2, 1);

	public BlockCrownPodest()
	{
		super(Material.circuits);
		this.setBlockName("crown_podest");
		this.setBlockTextureName("minecraft:stone");
		this.setBlockBounds(0, 0, 0, 1, 0.75f, 1);
		this.setHardness(4f);
		this.setResistance(20000f);
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileCrownPodest();
	}

	public boolean isOpaqueCube()
	{
		return false;
	}


	public int getRenderType()
	{
		return Dorfprojekt.crownPodestRenderID;
	}


	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item)
	{
		TileMultiBlock.placeMultiBlock(world, x, y, z, bounds, this);

		TileEntity te = world.getTileEntity(x, y, z);
		System.out.println(te);
		if(world.provider.dimensionId == 0 && te != null && te instanceof TileCrownPodest && entity != null && entity instanceof EntityPlayer)
		{
			System.out.println("lol sie nerven");
			EntityPlayer player = (EntityPlayer) entity;
			Team team = Team.getTeamForPlayer(player);
			if(team != null && team.isInInnerBorders(x, z))
			{


				TileCrownPodest tcp = (TileCrownPodest) te;
				tcp.team = team.name;
				tcp.markDirty();

				team.crownPodest = Coords4.fromTileEntity(tcp);
				Team.sendClientUpdates();
			}
		}

	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		this.setBlockBoundsBasedOnState(world, x, y, z);
		TileCrownPodest tcp = (TileCrownPodest) world.getTileEntity(x, y, z);
		if(tcp == null || tcp.isSource)
		{
			return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 0.75, z + 1);
		}
		else
		{
			return null;
		}
	}


	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		return TileMultiBlock.canPlaceAt(world, x, y, z, bounds);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		TileCrownPodest tcp = (TileCrownPodest) world.getTileEntity(x, y, z);
		if(tcp == null || tcp.isSource)
		{
			this.setBlockBounds(0, 0, 0, 1, 0.75f, 1);
		}
		else
		{
			this.setBlockBounds(0f, 0f, 0f, 0f, 0f, 0f);
		}
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float partX, float partY, float partZ)
	{
		TileCrownPodest tcp = (TileCrownPodest) world.getTileEntity(x, y, z);
		if(tcp != null)
		{
			Team podestTeam = tcp.getTeam();
			if(podestTeam != null)
			{
				System.out.println("Has crown: " + tcp.hasCrown + "; Team: " + tcp.team);
				if(player.getHeldItem() != null && player.getHeldItem().getItem() == Dorfprojekt.crownItem && ItemCrown.getTeam(player.getHeldItem()) != null)
				{
					Team crownTeam = ItemCrown.getTeam(player.getHeldItem());
					if(crownTeam != podestTeam)
					{
						System.out.println("Stealing crown");
						player.inventory.setInventorySlotContents(player.inventory.currentItem, null);

						Util.sendTranslatedChatToAll("dorfprojekt.crownStolen", podestTeam.getColoredName(), crownTeam.getColoredName());
						podestTeam.crownsStolen++;

						Collection coll = world.getScoreboard().func_96520_a(Dorfprojekt.crownsStolenCriteria);
						for(Object o : coll)
						{
							ScoreObjective score = (ScoreObjective) o;
							world.getScoreboard().func_96529_a(podestTeam.name, score).setScorePoints(podestTeam.crownsStolen);
						}

						crownTeam.setBattleTimeout(Dorfprojekt.timeoutAfterCrownStolen);

						crownTeam.respawnCrown();
						Team.sendClientUpdates();
					}
					else if(!tcp.hasCrown)
					{
						player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
						tcp.hasCrown = true;
						tcp.markDirty();
						Util.sendTileEntityUpdates(tcp);
						
						podestTeam.crownPodest = Coords4.fromTileEntity(tcp);
						
						Util.sendTranslatedChatToAll("dorfprojekt.crownPlaced", podestTeam.getColoredName());
					}
				}
				else if(tcp.hasCrown)
				{
					if(player.inventory.addItemStackToInventory(ItemCrown.getCrownForTeam(podestTeam)))
					{
						tcp.hasCrown = false;
						tcp.markDirty();
						Util.sendTileEntityUpdates(tcp);
						Util.sendTranslatedChatToAll("dorfprojekt.crownTaken", podestTeam.getColoredName());

					}
				}
			}
		}
		return false;
	}

	
	
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileCrownPodest)
		{
			TileCrownPodest tcp = (TileCrownPodest) te;
			if(tcp.hasCrown && tcp.team != null)
			{
				ItemStack itemstack = ItemCrown.getCrownForTeam(tcp.getTeam());
                EntityItem entityitem = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, itemstack);
                world.spawnEntityInWorld(entityitem);
                
                if (itemstack.hasTagCompound())
                {
                    entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                }
			}
		}
    }

}
