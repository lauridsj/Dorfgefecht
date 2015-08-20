package dorfprojekt;

import java.io.InputStream;
import java.util.HashMap;

import net.minecraft.util.ResourceLocation;

public class WavefrontMaterial
{

	public String name;
	public float red;
	public float green;
	public float blue;
	public float alpha = 1f;
	public ResourceLocation texture;
	
	public static HashMap<String, WavefrontMaterial> materialMap = new HashMap<String, WavefrontMaterial>();
	
	public static void parse(InputStream stream)
	{
		System.out.println("Parsing Materials");
		try
		{
			WavefrontMaterial currentMat = null;
			

			while(stream.available() > 0)
			{
				String line = WavefrontModel.readLine(stream);
				line = Util.deleteDoubleSpaces(line).trim();
				if(line.equals("") || line.startsWith("#")) continue;
				if(line.startsWith("newmtl "))
				{
					String name = line.split(" ")[1];
					currentMat = new WavefrontMaterial();
					currentMat.name = name;
					materialMap.put(currentMat.name, currentMat);
					System.out.println("Adding material: " + name);
				}
				if(line.startsWith("Kd "))
				{
					String[] astr2 = line.split(" ");
					currentMat.red = Float.parseFloat(astr2[1]);
					currentMat.green = Float.parseFloat(astr2[2]);
					currentMat.blue = Float.parseFloat(astr2[3]);
				}
				if(line.startsWith("map_Kd "))
				{
					currentMat.texture = new ResourceLocation(line.substring(7));
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(alpha);
		result = prime * result + Float.floatToIntBits(blue);
		result = prime * result + Float.floatToIntBits(green);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Float.floatToIntBits(red);
		result = prime * result + ((texture == null) ? 0 : texture.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WavefrontMaterial other = (WavefrontMaterial) obj;
		if (Float.floatToIntBits(alpha) != Float.floatToIntBits(other.alpha))
			return false;
		if (Float.floatToIntBits(blue) != Float.floatToIntBits(other.blue))
			return false;
		if (Float.floatToIntBits(green) != Float.floatToIntBits(other.green))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Float.floatToIntBits(red) != Float.floatToIntBits(other.red))
			return false;
		if (texture == null) {
			if (other.texture != null)
				return false;
		} else if (!texture.equals(other.texture))
			return false;
		return true;
	}

	public static WavefrontMaterial getMaterial(String name)
	{
		if(materialMap.containsKey(name)) return materialMap.get(name);
		else throw new RuntimeException("Could not find Material " + name + "!");
	}
	
	public void setColors(float r, float g, float b)
	{
		red = r;
		green = g;
		blue = b;
	}
	

}
