package dorfprojekt;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class CommandTeams extends CommandBase {

	@Override
	public String getCommandName() {
		return "teams";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/teams <create|delete|listplayers|buildborders|givecrown|settimeout> <team>\n	OR /teams <addplayer|removeplayer> <team> <player>\n	OR /teams setborders <team> <inner|outer> <pos1|pos2>\n	OR /teams setcolor <team> <red> <green> <blue>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) 
	{
		if(args.length <= 1)
		{
			throw new WrongUsageException("Need more arguments!", new Object[0]);
		}

		else if(args.length == 2)
		{
			if(args[0].equalsIgnoreCase("create"))
			{
				Team team = new Team();
				team.name = args[1];
				Team.addTeam(team);
				Util.sendChat(sender, "Successfully created team " + team.name);
			}

			else if(args[0].equalsIgnoreCase("delete"))
			{
				if(Team.getTeam(args[1]) != null)
				{
					Team.teamMap.remove(args[1]);
					Util.sendChat(sender, "Successfully deleted team " + args[1]);
				}
				else
				{
					Util.sendChat(sender, "Team " + args[1] + " does not exist!");
				}
			}

			else if(args[0].equalsIgnoreCase("listplayers"))
			{
				Team team = Team.getTeam(args[1]);
				if(team != null)
				{
					Util.sendChat(sender, "Players in team " + args[1] + ":");
					for(UUID uuid : team.players)
					{
						Util.sendChat(sender, MinecraftServer.getServer().func_152358_ax().func_152652_a(uuid).getName());
					}
				}
				else
				{
					Util.sendChat(sender, "Team " + args[1] + " does not exist!");
				}
			}

			else if(args[0].equalsIgnoreCase("buildborders"))
			{
				Team team = Team.getTeam(args[1]);
				if(team != null)
				{
					if(team.hasOuterBorders()) Util.buildBorders(MinecraftServer.getServer().getEntityWorld(), team.getMinOuterX(), team.getMinOuterZ(), team.getMaxOuterX(), team.getMaxOuterZ(), Dorfprojekt.borderBlock, 0, Blocks.stonebrick, 0);
					if(team.hasInnerBorders()) Util.buildBorders(MinecraftServer.getServer().getEntityWorld(), team.getMinInnerX(), team.getMinInnerZ(), team.getMaxInnerX(), team.getMaxInnerZ(), Dorfprojekt.borderBlock, 1, Blocks.stonebrick, 0);
					Util.sendChat(sender, "Borders for team " + args[1] + " built!");
				}
				else
				{
					Util.sendChat(sender, "Team " + args[1] + " does not exist!");
				}
			}

			else if(args[0].equalsIgnoreCase("givecrown"))
			{
				if(sender instanceof EntityPlayer)
				{
					EntityPlayer player = (EntityPlayer) sender;
					Team team = Team.getTeam(args[1]);
					if(team != null)
					{
						player.inventory.addItemStackToInventory(ItemCrown.getCrownForTeam(team));
					}
					else
					{
						Util.sendChat(sender, "Team " + args[1] + " does not exist!");
					}
				}
			}

		}

		else if(args.length == 3)
		{
			if(args[0].equalsIgnoreCase("addplayer"))
			{
				Team team = Team.getTeam(args[1]);
				if(team != null)
				{
					EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(args[2]);
					if (player == null)
					{
						throw new PlayerNotFoundException();
					}
					Team.setTeamForPlayer(player, team);
					
					player.getWorldScoreboard().func_151392_a(player.getCommandSenderName(), args[1]);
					
					Util.sendChat(sender, "Added player " + args[2] + " to team " + args[1]);
				}
				else
				{
					Util.sendChat(sender, "Team " + args[1] + " does not exist!");
				}
			}

			else if(args[0].equalsIgnoreCase("removeplayer"))
			{
				Team team = Team.getTeam(args[1]);
				if(team != null)
				{
					EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(args[2]);
					if (player == null)
					{
						throw new PlayerNotFoundException();
					}
					if(Team.getTeamForPlayer(player) == team)
					{
						Team.setTeamForPlayer(player, null);
						player.getWorldScoreboard().removePlayerFromTeam(player.getCommandSenderName(), player.getWorldScoreboard().getTeam(team.name));
						Util.sendChat(sender, "Removed player " + args[2] + " for team " + args[1]);
					}
					else
					{
						Util.sendChat(sender, "Player " + args[2] + " is not in that team!");
					}
				}
				else
				{
					Util.sendChat(sender, "Team " + args[1] + " does not exist!");
				}
			}

			else if(args[0].equalsIgnoreCase("setcolor"))
			{
				Team team = Team.getTeam(args[1]);
				if(team != null)
				{

					EnumChatFormatting ecf = EnumChatFormatting.getValueByName(args[2]);
					if(ecf != null)
					{
						team.color = ecf.getFriendlyName();
						Util.sendChat(sender, "Colors updated.");
					}
					else
					{
						Util.sendChat(sender, "This color doesnt exist");
					}


				}
			}

			else if(args[0].equalsIgnoreCase("settimeout"))
			{
				Team team = Team.getTeam(args[1]);
				if(team != null)
				{
					try
					{
						int secs = Integer.parseInt(args[2]);
						team.setBattleTimeout(secs * 20);
					}
					catch(NumberFormatException ex)
					{
						Util.sendChat(sender, "Wrong number format!");
					}
				}
			}
			
			else if(args[0].equalsIgnoreCase("setpoints"))
			{
				Team team = Team.getTeam(args[1]);
				if(team != null)
				{
					try
					{
						int i = Integer.parseInt(args[2]);
						team.crownsStolen = i;
						
						World world = MinecraftServer.getServer().getEntityWorld();
						Collection coll = world.getScoreboard().func_96520_a(Dorfprojekt.crownsStolenCriteria);
						for(Object o : coll)
						{
							ScoreObjective score = (ScoreObjective) o;
							world.getScoreboard().func_96529_a(team.name, score).increseScore(1);;
						}
					}
					catch(NumberFormatException ex)
					{
						Util.sendChat(sender, "Wrong number format!");
					}
				}
			}
		}

		else if(args.length == 4)
		{
			if(args[0].equalsIgnoreCase("setborders"))
			{
				Team team = Team.getTeam(args[1]);
				if(team != null && sender instanceof EntityPlayer)
				{
					EntityPlayer ep = (EntityPlayer) sender;
					MovingObjectPosition mop = Util.playerRayTrace(ep.worldObj, ep, false);

					if(args[2].equalsIgnoreCase("inner"))
					{
						if(args[3].equalsIgnoreCase("pos1"))
						{
							team.innerX1 = mop.blockX;
							team.innerZ1 = mop.blockZ;
						}
						else if(args[3].equalsIgnoreCase("pos2"))
						{
							team.innerX2 = mop.blockX;
							team.innerZ2 = mop.blockZ;
						}
					}
					else if(args[2].equalsIgnoreCase("outer"))
					{
						if(args[3].equalsIgnoreCase("pos1"))
						{
							team.outerX1 = mop.blockX;
							team.outerZ1 = mop.blockZ;
						}
						else if(args[3].equalsIgnoreCase("pos2"))
						{
							team.outerX2 = mop.blockX;
							team.outerZ2 = mop.blockZ;
						}
					}
					Util.sendChat(sender, "Borders updated.");
				}
			}
		}

		Team.sendClientUpdates();
	}

	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if(args.length < 2)
		{
			return getListOfStringsMatchingLastWord(args, "create", "delete", "addplayer", "removeplayer", "listplayers", "setborders", "setcolor", "buildborders", "givecrown", "settimeout", "setpoints");
		}
		else if(args.length < 3)
		{
			if(Util.containsIgnoreCase(args[0], "delete", "addplayer", "removeplayer", "listplayers", "setborders", "setcolor", "buildborders", "givecrown", "settimeout", "setpoints"))
			{
				return getListOfStringsFromIterableMatchingLastWord(args, Team.teamMap.keySet());
			}

		}
		else if(args.length < 4)
		{
			if(Util.containsIgnoreCase(args[0], "addplayer", "removeplayer"))
			{
				return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
			}
			else if(args[0].equalsIgnoreCase("setborders"))
			{
				return getListOfStringsMatchingLastWord(args, "inner", "outer");
			}
			else if(args[0].equalsIgnoreCase("setcolor"))
			{
				return getListOfStringsFromIterableMatchingLastWord(args, EnumChatFormatting.getValidValues(true, false));
			}

		}
		else if(args.length < 5)
		{
			if(args[0].equalsIgnoreCase("setborders"))
			{
				return getListOfStringsMatchingLastWord(args, "pos1", "pos2");
			}
		}
		return null;
	}

	public int getRequiredPermissionLevel()
	{
		return 2;
	}





}
