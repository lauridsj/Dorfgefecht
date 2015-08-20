package dorfprojekt;

import java.io.File;
import java.util.ArrayList;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreDummyCriteria;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Dorfprojekt.MODID, version = Dorfprojekt.VERSION)
public class Dorfprojekt
{
    public static final String MODID = "dorfprojekt";
    public static final String VERSION = "07";
        
    public static int borderRenderID;
    public static BlockBorder borderBlock;
    
    public static int crownPodestRenderID;
    public static BlockCrownPodest crownPodestBlock;
    
    public static ItemCrown crownItem;
    
    @SidedProxy(clientSide = "dorfprojekt.ClientProxy", serverSide = "dorfprojekt.CommonProxy")
    public static CommonProxy sidedProxy;
    
    public static FMLEventChannel networkChannel;
    
    public static File configFile;
    public static Configuration config;
    
    public static int minAttackablePlayers;
    public static int attackableDelay;
    public static int maxPlaceableBlocks;
    public static double breakSpeedMultiplierEnemy;
    public static double breakSpeedMultiplierFriendly;
    public static boolean slowdownInOuterBorders;
    public static int timeoutAfterCrownStolen;
    public static int crownLifespan;
    
    public static IScoreObjectiveCriteria crownsStolenCriteria = new ScoreDummyCriteria("crownsStolen");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	sidedProxy.preInit();
    	config = new Configuration(configFile);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	sidedProxy.init();
    	
      	loadConfig();
    	
    	borderBlock = new BlockBorder();
    	crownPodestBlock = new BlockCrownPodest();
    	
    	crownItem = new ItemCrown();
    	
    	GameRegistry.registerBlock(borderBlock, "border");
    	GameRegistry.registerBlock(crownPodestBlock, "crown_podest");
    	
    	GameRegistry.registerItem(crownItem, "crown");
    	
    	GameRegistry.registerTileEntity(TileCrownPodest.class, "CrownPodest");
    	
    	MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
    	
    	networkChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(MODID);
    	
    	FMLCommonHandler.instance().bus().register(new FMLEventHandler());
    	
    	GameRegistry.addRecipe(new ItemStack(crownPodestBlock), " C ", " W ", "SSS", 'C', Blocks.carpet, 'W', Blocks.planks, 'S', Blocks.stone);

    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	sidedProxy.postInit();
    	config.save();
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
    	event.registerServerCommand(new CommandTeams());
    }
    
    public static int getColorFromRBG(int r, int b, int g)
    {
    	int var1 = r << 16;
		int var2 = g << 8;
		int var3 = b;
		return var1 | var2 | var3;
    }
    
    public static void loadConfig()
    {
    	minAttackablePlayers = config.get("General", "MinAttackablePlayers", 4, "Minimum online players required for a team to be attackable").getInt();
    	attackableDelay = config.get("General", "AttackableDelay", 60, "The time in seconds in which a team is still attackable after players leave").getInt() * 20;
    	maxPlaceableBlocks = config.get("General", "MaxPlaceableBlocks", 32, "Maximum blocks placeable per team in an enemy team's base whilst attacking").getInt();   	
    	breakSpeedMultiplierEnemy = config.get("General", "BreakSpeedMultiplierEnemy", 0.2, "The multiplier applied to the break speed in enemy territory").getDouble();
    	breakSpeedMultiplierFriendly = config.get("General", "BreakSpeedMultiplierFriendly", 0.5, "The multiplier applied to the break speed in friendly territory").getDouble();
    	slowdownInOuterBorders = config.get("General", "SlowdownInOuterBorders", false, "Should break speed slowdown also be applied in outer borders?").getBoolean();
    	timeoutAfterCrownStolen = config.get("General", "TimeoutAfterCrownStolen", 43200, "The time in seconds in which a team is unattackable after its crown has been stolen").getInt() * 20;
    	crownLifespan = config.get("General", "crownLifespan", 30, "The time in seconds it takes for the crown to respawn after being dropped").getInt() * 20;

    }
    
    public static FMLProxyPacket getConfigPacket()
    {
    	StreamHelper.newOutputStream();
    	StreamHelper.writeInt(NetworkHandler.UPDATE_CONFIG);
    	StreamHelper.writeInt(minAttackablePlayers);
    	StreamHelper.writeInt(attackableDelay);
    	StreamHelper.writeInt(maxPlaceableBlocks);
    	StreamHelper.writeDouble(breakSpeedMultiplierEnemy);
    	StreamHelper.writeDouble(breakSpeedMultiplierFriendly);
    	StreamHelper.writeBoolean(slowdownInOuterBorders);
    	StreamHelper.writeInt(timeoutAfterCrownStolen);
    	StreamHelper.writeInt(crownLifespan);
    	
    	return StreamHelper.getPacket();
    }
    
    public static void recieveConfig()
    {
    	minAttackablePlayers = StreamHelper.readInt();
    	attackableDelay = StreamHelper.readInt();
    	maxPlaceableBlocks = StreamHelper.readInt();
    	breakSpeedMultiplierEnemy = StreamHelper.readDouble();
    	breakSpeedMultiplierFriendly = StreamHelper.readDouble();
    	slowdownInOuterBorders = StreamHelper.readBoolean();
    	timeoutAfterCrownStolen = StreamHelper.readInt();
    	crownLifespan = StreamHelper.readInt();
    	
    }
}
