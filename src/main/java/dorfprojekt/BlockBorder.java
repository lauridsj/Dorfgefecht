package dorfprojekt;

import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBorder extends Block {

	//meta 0 = closed
	//meta 1 = open

	public static HashMap<Coords4, Integer> scheduledBorders = new HashMap<Coords4, Integer>();
	
	private IIcon crossIcon;


	public static IIcon getCrossIcon() {
		return Dorfprojekt.borderBlock.crossIcon;
	}

	public static IIcon getLineIcon() {
		return Dorfprojekt.borderBlock.lineIcon;
	}

	private IIcon lineIcon;

	public BlockBorder()
	{
		super(Material.portal);
		this.setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f);
		this.disableStats();
		this.setBlockName("border");
		this.setBlockUnbreakable();
	}

	public boolean isOpaqueCube()
	{
		return false;
	}

	public void dropBlockAsItemWithChance(World p_149690_1_, int p_149690_2_, int p_149690_3_, int p_149690_4_, int p_149690_5_, float p_149690_6_, int p_149690_7_)
	{

	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		Team team = Team.getTeamForCoords(x, z);
		if(team != null && !team.isAttackable() && world.getBlockMetadata(x, y, z) == 0)
		{
			return super.getCollisionBoundingBoxFromPool(world, x, y, z);
		}
		else
		{
			return null;
		}
	}

	public static boolean isBorder(IBlockAccess world, int x, int y, int z, int side)
	{
		Team team = Team.getTeamForCoords(x, z);
		return team != null && (team.isOnOuterBorder(x, z) || team.isOnInnerBorder(x, z));
	}

	public void registerBlockIcons(IIconRegister reg)
	{
		crossIcon = reg.registerIcon("dorfprojekt:border_cross");
		lineIcon = reg.registerIcon("dorfprojekt:border_line");
	}

	public int getRenderType() 
	{
		return Dorfprojekt.borderRenderID;
	}

	public boolean canCollideCheck(int p_149678_1_, boolean p_149678_2_)
	{
		return false;
	}

	public IIcon getIcon(int p_149691_1_, int p_149691_2_)
	{
		return this.lineIcon;
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		Team team = Team.getTeamForCoords(x, z);
		if(team == null || (!team.isOnOuterBorder(x, z) && !team.isOnInnerBorder(x, z)))
		{
			world.setBlockToAir(x, y, z);
		}
	}
	
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity)
    {
		if(entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entity;
			Team team = Team.getTeamForCoords(x, z);
			if(team.isPlayerInTeam(player) || player.capabilities.isCreativeMode)
			{
				return;
			}
		}
		super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
    }
	
	/**
     * Determines if a new block can be replace the space occupied by this one,
     * Used in the player's placement code to make the block act like water, and lava.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return True if the block is replaceable by another block
     */
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z)
    {
    	return true;
    }

}
