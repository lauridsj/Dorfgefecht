package dorfprojekt;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class DorfprojektLoadingPlugin implements IFMLLoadingPlugin {

	public static boolean IN_MCP = false;
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[] {DorfprojektTransformer.class.getName()};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	    IN_MCP = !((Boolean)data.get("runtimeDeobfuscationEnabled")).booleanValue();
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
