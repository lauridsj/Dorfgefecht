package dorfprojekt;

import java.io.File;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class Util {

	public static int[] colorCode = new int[32];
	public static final String FORMAT_CHAR = "\u00a7";
	
	static
	{
		 for (int i = 0; i < 32; ++i)
	        {
	            int j = (i >> 3 & 1) * 85;
	            int k = (i >> 2 & 1) * 170 + j;
	            int l = (i >> 1 & 1) * 170 + j;
	            int i1 = (i >> 0 & 1) * 170 + j;

	            if (i == 6)
	            {
	                k += 85;
	            }

	    	    if (i >= 16)
	            {
	                k /= 4;
	                l /= 4;
	                i1 /= 4;
	            }

	            colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
	        }
	}
	
	public static File getSaveDirectory(String worldName)
	{
		File parent = getBaseDirectory();

		if(FMLCommonHandler.instance().getSide().isClient())
		{
			parent = new File(getBaseDirectory(), "saves" + File.separator);
		}

		return new File(parent, worldName + File.separator);
	}

	public static File getBaseDirectory()
	{
		if(FMLCommonHandler.instance().getSide().isClient())
		{
			return Minecraft.getMinecraft().mcDataDir;
		}
		else
		{
			return new File(".");
		}
	}
	
	public static MovingObjectPosition playerRayTrace(World world, EntityPlayer player, boolean par3)
    {
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double)f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double)f + (double)(world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        if (player instanceof EntityPlayerMP)
        {
            d3 = ((EntityPlayerMP)player).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
        return world.func_147447_a(vec3, vec31, par3, !par3, false);
    }
	
	public static void buildBorders(World world, int x1, int z1, int x2, int z2, Block block, int meta, Block replaceBlock, int replaceMeta)
	{
		System.out.println("Building borders: " + x1 + "|" + z1 + " to " + x2 + "|" + z2);
		int height = world.getHeight();
		for(int j = 0; j < world.getHeight(); j++)
		{
			for(int i = x1; i <= x2; i++)
			{
				setIfAir(world, i, j, z1, block, meta, replaceBlock, replaceMeta);
				setIfAir(world, i, j, z2, block, meta, replaceBlock, replaceMeta);
			}
			for(int k = z1; k <= z2; k++)
			{
				setIfAir(world, x1, j, k, block, meta, replaceBlock, replaceMeta);
				setIfAir(world, x2, j, k, block, meta, replaceBlock, replaceMeta);
			}
		}
		
		for(int i = x1; i <= x2; i++)
		{
			for(int k = z1; k <= z2; k++)
			{
				//System.out.println(i + "   " + height + "   " + k);
				setIfAir(world, i, height - 10, k, block, meta, replaceBlock, replaceMeta);
			}
		}
	}
	
	public static void setIfAir(World world, int x, int y, int z, Block block, int meta, Block replaceBlock, int replaceMeta)
	{
		Block currentBlock = world.getBlock(x, y, z);
		if(currentBlock instanceof BlockLiquid || currentBlock instanceof IFluidBlock)
		{
			world.setBlock(x, y, z, replaceBlock, replaceMeta, 3);
		}
		else if(currentBlock == Blocks.air || !currentBlock.renderAsNormalBlock())
		{
			world.setBlock(x, y, z, block, meta, 3);
		}
	}

	public static void sendChat(ICommandSender sender, String text)
	{
		sender.addChatMessage(new ChatComponentText("[Dorfprojekt] " + text));
	
	}
	
	public static void sendTranslatedChat(ICommandSender sender, String text)
	{
		sender.addChatMessage(new ChatComponentTranslation(text));
	
	}
	
	public static void sendTranslatedChatToAll(String text, Object... obj)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation(text, obj));
		}
	}
	
	public static int getMinimalArg(double... args)
	{
		int argnumber = 0;
		double minvalue = args[0];
		for(int i = 1; i < args.length; i++)
		{
			if(args[i] < minvalue)
			{
				argnumber = i;
				minvalue = args[i];
			}
		}
		return argnumber;
	}
	
	public static String deleteDoubleSpaces(String str)
	{
		while(str.contains("  "))
		{
			str = str.replace("  ", " ");
		}
		str.replace("	", "");
		while(str.endsWith(" "))
		{
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}
	
	public boolean isItemValid(ItemStack stack)
	{
		return ItemCrown.isValid(this, stack);
	}
	
	public static void sendTileEntityUpdates(TileEntity te)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(te.getDescriptionPacket());
		}
	}
	
	public static int getColor(EnumChatFormatting ecf)
	{
		int j = "0123456789abcdef".indexOf(ecf.getFormattingCode());
		return colorCode[j];
	}
	
	public static int getColorFromRGB(int r, int g, int b)
	{
		int var1 = r << 16;
		int var2 = g << 8;
		int var3 = b;
		return var1 | var2 | var3;
	}
	
	public static int getRed(int color)
	{
		return (color >> 16) & 255;
	}
	
	public static int getGreen(int color)
	{
		return (color >> 8) & 255;
	}
	
	public static int getBlue(int color)
	{
		return color & 255;
	}
	
	public static boolean containsIgnoreCase(String str, String... astrs)
	{
		for(String str2 : astrs)
		{
			if(str.equalsIgnoreCase(str2)) return true;
		}
		return false;
	}
	
}
