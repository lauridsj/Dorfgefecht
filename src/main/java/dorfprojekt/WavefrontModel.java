package dorfprojekt;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelFormatException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class WavefrontModel
{


	public static HashMap<String, WavefrontModel> modelMap = new HashMap<String, WavefrontModel>();

	public ArrayList<WavefrontFace> faces = new ArrayList<WavefrontFace>();
	public boolean onlyTriangles = false;


	public static WavefrontModel parseWithMaterials(String name)
	{
		WavefrontMaterial.parse(WavefrontModel.class.getResourceAsStream("/resources/models/" + name.replace('.', '/') + ".obj.mtl"));
		return parse(WavefrontModel.class.getResourceAsStream("/resources/models/" + name.replace('.', '/') + ".obj"));
	}

	public static WavefrontModel parse(InputStream stream)
	{
		long time = System.currentTimeMillis();
		System.out.println("Parsing model");
		int lineCounter = 0;
		String line = "";
		try
		{			
			ArrayList<Vector3f> vertexList = new ArrayList<Vector3f>();
			ArrayList<Vector2f> texCoordList = new ArrayList<Vector2f>();
			ArrayList<Vector3f> normalList = new ArrayList<Vector3f>();
			WavefrontModel obj = new WavefrontModel();
			float minX = Float.MAX_VALUE;
			float minY = Float.MAX_VALUE;
			float minZ = Float.MAX_VALUE;
			float maxX = Float.MAX_VALUE;
			float maxY = Float.MAX_VALUE;
			float maxZ = Float.MAX_VALUE;

			WavefrontMaterial currentMat = null;

			while(stream.available() > 0)
			{
				lineCounter++;
				line = readLine(stream);
				line = Util.deleteDoubleSpaces(line).trim();
				if(line.equals("") || line.startsWith("#")) continue;
				if(line.startsWith("v "))
				{
					String[] aline = line.substring(2).split(" ");
					float x = Float.parseFloat(aline[0]);
					float y = Float.parseFloat(aline[1]);
					float z = Float.parseFloat(aline[2]);
					vertexList.add(new Vector3f(x, y, z));
					if(minX == Float.MAX_VALUE)
					{
						minX = x;
						minY = y;
						minZ = z;
						maxX = x;
						maxY = y;
						maxZ = z;
					}
					else
					{
						if(x < minX) minX = x;
						if(y < minY) minY = y;
						if(z < minZ) minZ = z;
						if(x > maxX) maxX = x;
						if(y > maxY) maxY = y;
						if(z > maxZ) maxZ = z;
					}
					continue;
				}
				if(line.startsWith("vt "))
				{
					String[] aline = line.split(" ");
					float x = Float.parseFloat(aline[1]);
					float y = Float.parseFloat(aline[2]);
					texCoordList.add(new Vector2f(x, y));
					continue;
				}
				if(line.startsWith("vn "))
				{
					String[] aline = line.split(" ");
					float x = Float.parseFloat(aline[1]);
					float y = Float.parseFloat(aline[2]);
					float z = Float.parseFloat(aline[3]);
					normalList.add(new Vector3f(x, y, z));
					continue;
				}
				if(line.startsWith("f "))
				{				
					String[] aline = line.split(" ");
					WavefrontFace f = new WavefrontFace(aline.length - 1);
					for(int i = 1; i < aline.length; i++)
					{
						String[] indices = aline[i].split("/");
						f.setVertex(i - 1, vertexList.get(Integer.parseInt(indices[0]) - 1));
						if(indices.length == 2)
						{
							f.setTexCoord(i - 1, texCoordList.get(Integer.parseInt(indices[1]) - 1));
						}
						else
						{
							if(!indices[1].equals(""))
							{
								f.setTexCoord(i - 1, texCoordList.get(Integer.parseInt(indices[1]) - 1));	
							}
							f.setNormal(i - 1, normalList.get(Integer.parseInt(indices[2]) - 1));
							f.useNormals = true;
						}
					}
					if(currentMat != null)
					{
						f.material = currentMat;
					}
					if(f.verticesX.length > 3)
					{
						obj.onlyTriangles = false;
					}
					obj.faces.add(f);
				}
				if(line.startsWith("usemtl "))
				{
					String material = line.substring(7);
					if(WavefrontMaterial.materialMap.containsKey(material))
					{
						System.out.println("Using material: " + material);
						currentMat = WavefrontMaterial.getMaterial(material);
					}
					else
					{
						System.out.println("Could not find material: " + material);
					}
				}

			}

//			obj.bounds = new BoundingBox3D(minX, minY, minZ, maxX, maxY, maxZ);
			for(WavefrontFace f : obj.faces)
			{
				for(int i = 0; i < f.edges; i++)
				{
					f.verticesX[i] -= minX;
					f.verticesY[i] -= minY;
					f.verticesZ[i] -= minZ;
				}
			}
			Collections.sort(obj.faces, WavefrontFace.comparator);
			System.out.println("Took " + (System.currentTimeMillis() - time) + " milliseconds");
			return obj;
		}
		catch(Exception ex)
		{
			System.out.println("Could not load model; error in line " + lineCounter + ": " + line);
			ex.printStackTrace();
			return null;
		}
	}

	public void render()
	{
		if(onlyTriangles)
		{
			Tessellator tess = Tessellator.instance;
			WavefrontMaterial currentMat = null;
			GL11.glEnable(GL11.GL_NORMALIZE);
			tess.startDrawing(GL11.GL_TRIANGLES);
			for(WavefrontFace f : this.faces)
			{
				if(f.material != currentMat)
				{
					currentMat = f.material;
					tess.setColorRGBA_F(currentMat.red, currentMat.green, currentMat.blue, currentMat.alpha);
					if(currentMat.texture != null)
					{
						//GL11.glEnable(GL11.GL_TEXTURE_2D);
						Minecraft.getMinecraft().getTextureManager().bindTexture(currentMat.texture);
					}
					else
					{
						//GL11.glDisable(GL11.GL_TEXTURE_2D);
					}
				}
				f.tessellate(tess);
				
			}
			tess.draw();
		}
		else
		{
			for(WavefrontFace f : this.faces)
			{
				f.renderRaw();	
			}
		}

	}

	public static WavefrontModel getModel(ResourceLocation loc)
	{
		try
        {
			ResourceLocation material = new ResourceLocation(loc.toString() + ".mtl");
			IResource matRes = Minecraft.getMinecraft().getResourceManager().getResource(material);
			WavefrontMaterial.parse(matRes.getInputStream());
						
            IResource res = Minecraft.getMinecraft().getResourceManager().getResource(loc);
            return parse(res.getInputStream());
        }
        catch (IOException e)
        {
            throw new ModelFormatException("IO Exception reading model format", e);
        }
	}
	
	public static WavefrontModel getModel(String name)
	{
		if(!modelMap.containsKey(name))
		{
			try
			{
				modelMap.put(name, parseWithMaterials(name));
			}
			catch(Exception ex)
			{
				return null;
			}
		}
		return modelMap.get(name);
	}
	
	

	public static String readLine(InputStream stream) throws IOException
	{		
		String str = "";
		while(stream.available() > 0)
		{
			char c = (char) stream.read();
			if(c == '\n' || c == '\r') break;
			else str = str + c;
		}
		return str;
	}

}