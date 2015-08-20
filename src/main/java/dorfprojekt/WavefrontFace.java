package dorfprojekt;

import java.util.Comparator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class WavefrontFace
{

	public static Comparator<WavefrontFace> comparator = new Comparator<WavefrontFace>()
			{
				@Override
				public int compare(WavefrontFace o1, WavefrontFace o2)
				{
					return Integer.compare(o1.material.hashCode(), o2.material.hashCode());
				}
		
			};
	
	public double[] verticesX;
	public double[] verticesY;
	public double[] verticesZ;
	
	public double[] texCoordsX;
	public double[] texCoordsY;
	
	
	public double[] normalsX;
	public double[] normalsY;
	public double[] normalsZ;
	
	public boolean useNormals;
	public boolean useTextures = false;
	public WavefrontMaterial material;
	public int edges;
	
	public static boolean isTessellating = false;
	
	public WavefrontFace(int edges, boolean useNormals)
	{
		this.edges = edges;
		this.useNormals = useNormals;
		this.verticesX = new double[this.edges];
		this.verticesY = new double[this.edges];
		this.verticesZ = new double[this.edges];
		this.texCoordsX = new double[this.edges];
		this.texCoordsY = new double[this.edges];
		this.normalsX = new double[this.edges];
		this.normalsY = new double[this.edges];
		this.normalsZ = new double[this.edges];

	}
	
	public WavefrontFace(int edges)
	{
		this(edges, false);
	}
	
	public void setVertex(int index, double vx, double vy, double vz, double texx, double texy, double nx, double ny, double nz)
	{
		this.verticesX[index] = vx;
		this.verticesY[index] = vy;
		this.verticesZ[index] = vz;
		this.texCoordsX[index] = texx;
		this.texCoordsY[index] = texy;
		this.normalsX[index] = nx;
		this.normalsY[index] = ny;
		this.normalsZ[index] = nz;
	}
	
	public void setVertex(int index, Vector3f vert)
	{
		this.verticesX[index] = vert.x;
		this.verticesY[index] = vert.y;
		this.verticesZ[index] = vert.z;
	}
	
	public void setVertex(int index, double x, double y, double z)
	{
		this.verticesX[index] = x;
		this.verticesY[index] = y;
		this.verticesZ[index] = z;
	}
	
	public void setNormal(int index, Vector3f vert)
	{
		this.normalsX[index] = vert.x;
		this.normalsY[index] = vert.y;
		this.normalsZ[index] = vert.z;
	}
	
	public void setTexCoord(int index, Vector2f vert)
	{
		this.texCoordsX[index] = vert.x;
		this.texCoordsY[index] = vert.y;
		this.useTextures = true;
	}
	
	public void setTexCoord(int index, double u, double v)
	{
		this.texCoordsX[index] = u;
		this.texCoordsY[index] = v;
		this.useTextures = true;
	}
	
	public void tessellate(Tessellator tess)
	{
		//tess.setColorRGBA_F(material.red, material.green, material.blue, material.alpha);
		for(int i = 0; i < edges; i++)			
		{
			if(useNormals) 
			{
				tess.setNormal((float)normalsX[i], (float)normalsY[i], (float)normalsZ[i]);
			}
			if(useTextures)
			{
				tess.addVertexWithUV(verticesX[i], verticesY[i], verticesZ[i], texCoordsX[i], texCoordsY[i]);
			}
			else
			{
				tess.addVertex(verticesX[i], verticesY[i], verticesZ[i]);
			}
		}
	}
	
	public void renderRaw()
	{
		Tessellator tess = Tessellator.instance;
		if(useTextures && material.texture != null) 
		{
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			Minecraft.getMinecraft().getTextureManager().bindTexture(material.texture);
		}
		else
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);	
		}
		
		
		if(useNormals)
		{
			GL11.glEnable(GL11.GL_NORMALIZE);
		}
		else
		{
			GL11.glDisable(GL11.GL_NORMALIZE);	
		}

		tess.startDrawing(GL11.GL_POLYGON);
		
		tess.setColorRGBA_F(material.red, material.green, material.blue, material.alpha);
		
		for(int i = 0; i < edges; i++)			
		{
			if(useNormals) 
			{
				tess.setNormal((float)normalsX[i], (float)normalsY[i], (float)normalsZ[i]);
			}
			if(useTextures)
			{
				tess.addVertexWithUV(verticesX[i], verticesY[i], verticesZ[i], texCoordsX[i], texCoordsY[i]);
			}
			else
			{
				tess.addVertex(verticesX[i], verticesY[i], verticesZ[i]);
			}
		}
		
		tess.draw();	
	}
	
	
}
