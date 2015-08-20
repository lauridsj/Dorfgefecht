package dorfprojekt;

import java.io.File;

public class CommonProxy {

	public void preInit() 
	{
		Dorfprojekt.configFile = new File(System.getProperty("user.dir"), "/config/Dorfprojekt.cfg");
	}
	
	public void init() {}
	
	public void postInit() 
	{
		Dorfprojekt.networkChannel.register(new NetworkHandler());
	}
	
}
