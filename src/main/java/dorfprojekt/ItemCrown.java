package dorfprojekt;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemCrown extends ItemArmor {


	public ItemCrown()
	{
		super(ItemArmor.ArmorMaterial.GOLD, 4, 0);
		this.setMaxDamage(0);
		this.setUnlocalizedName("crown");
		this.setTextureName("minecraft:gold_helmet");
	}
	
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
        return CrownModel.instance;
    }
    
    public void addInformation(ItemStack item, EntityPlayer player, List list, boolean b)
    {
    	Team team = getTeam(item);
    	if(team != null)
    	{
    		list.add("Team " + team.getColoredName());
    	}
    }
    
    public static Team getTeam(ItemStack item)
    {
    	if(item.hasTagCompound() && item.getTagCompound().hasKey("dorfprojekt_team"))
    	{
    		return Team.getTeam(item.getTagCompound().getString("dorfprojekt_team"));
    	}
    	else
    	{
    		return null;
    	}
    }
    
    public static ItemStack getCrownForTeam(Team team)
    {
    	ItemStack crown = new ItemStack(Dorfprojekt.crownItem);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("dorfprojekt_team", team.name);
		crown.setTagCompound(tag);
		return crown;
    }
    
    public void onUpdate(ItemStack stack, World world, Entity ent, int slot, boolean isInHand)
    {
    	Team team = getTeam(stack);
    	if(ent instanceof EntityPlayer)
    	{
			EntityPlayer player = (EntityPlayer) ent;
			if(team != null && !team.isAttackable() && team != Team.getTeamForPlayer(player))
    		{
    			player.inventory.setInventorySlotContents(slot, null);
    			System.out.println("Deleting crown from inventory and respawning");
    			team.respawnCrown();
    		}
    	}
    }
    
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack)
    {
    	Team team = getTeam(stack);
    	if(team != null && !team.isAttackable() && team != Team.getTeamForPlayer(player))
    	{
    		player.setCurrentItemOrArmor(4, null);
    		System.out.println("Deleting armor - respawning crown");
    		team.respawnCrown();
    	}
    }


    public static boolean isValid(Slot slot, ItemStack stack)
    {
    	//System.out.printf("isValid has been called! Args: %s %s \n", slot.inventory, stack);
    	if(stack != null && stack.getItem() == Dorfprojekt.crownItem && ItemCrown.getTeam(stack) != null)
    	{
    		if(!(slot.inventory instanceof InventoryPlayer))
    		{
    			return false;
    		}
    	}
    	return true;
    }
    
    public static boolean isValid(Util slot, ItemStack stack)
    {
    	//System.out.printf("isValid has been called! Args: %s %s \n", slot, stack);
    	return !(stack != null && stack.getItem() == Dorfprojekt.crownItem && ItemCrown.getTeam(stack) != null);
    }
	
}
