package dorfprojekt;

import io.netty.buffer.Unpooled;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class StreamHelper
{
	public static DataInputStream dataIS;
	public static ByteArrayInputStream byteArrayIS;

	public static DataOutputStream dataOS;
	public static ByteArrayOutputStream byteArrayOS;

	public static void bindInputStream(byte[] arr)
	{
		byteArrayIS = new ByteArrayInputStream(arr);
		dataIS = new DataInputStream(byteArrayIS);
	}

	public static byte[] getByteArray()
	{
		return byteArrayOS.toByteArray();
	}

	public static void newOutputStream()
	{
		byteArrayOS = new ByteArrayOutputStream();
		dataOS = new DataOutputStream(byteArrayOS);
	}

	public static boolean readBoolean()
	{
		try
		{
			return dataIS.readBoolean();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public static byte readByte()
	{
		try
		{
			return dataIS.readByte();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}

	public static double readDouble()
	{
		try
		{
			return dataIS.readDouble();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}

	public static float readFloat()
	{
		try
		{
			return dataIS.readFloat();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}

	public static int readInt()
	{
		try
		{
			return dataIS.readInt();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}

	public static NBTTagCompound readNBTTagCompound()
	{
		try
		{
			return (NBTTagCompound) CompressedStreamTools.read(dataIS);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static short readShort()
	{
		try
		{
			return dataIS.readShort();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}

	public static String readString()
	{
		try
		{
			return dataIS.readUTF();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static void writeBoolean(boolean par0)
	{
		try
		{
			dataOS.writeBoolean(par0);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public static void writeByte(byte par0)
	{
		try
		{
			dataOS.writeByte(par0);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public static void writeDouble(double par0)
	{
		try
		{
			dataOS.writeDouble(par0);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public static void writeFloat(float par0)
	{
		try
		{
			dataOS.writeFloat(par0);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public static void writeInt(int par0)
	{
		try
		{
			dataOS.writeInt(par0);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public static void writeNBTTagCompound(NBTTagCompound par0)
	{
		try
		{
			CompressedStreamTools.write(par0, dataOS);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public static void writeShort(short par0)
	{
		try
		{
			dataOS.writeShort(par0);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public static void writeString(String par0)
	{
		try
		{
			dataOS.writeUTF(par0);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static FMLProxyPacket getPacket()
	{
		return new FMLProxyPacket(Unpooled.wrappedBuffer(getByteArray()), Dorfprojekt.MODID);
	}
}
