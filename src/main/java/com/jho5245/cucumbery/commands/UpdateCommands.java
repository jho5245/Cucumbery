package com.jho5245.cucumbery.commands;

import com.jho5245.cucumbery.util.MessageUtil;
import com.jho5245.cucumbery.util.Method;
import com.jho5245.cucumbery.util.SelectorUtil;
import com.jho5245.cucumbery.util.storage.data.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UpdateCommands implements CommandExecutor
{
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
	{
		if (!Method.hasPermission(sender, Permission.CMD_UPDATE_COMMANDS, true))
		{
			return true;
		}
		String usage = cmd.getUsage().replace("<command>", label);
		if (args.length <= 2)
		{
			Player player;
			if (args.length == 0)
			{
				if (!(sender instanceof Player))
				{
					MessageUtil.shortArg(sender, 1, args);
					MessageUtil.commandInfo(sender, label, usage);
					return true;
				}
				player = (Player) sender;
			}
			else
			{
				player = SelectorUtil.getPlayer(sender, args[0]);
				if (player == null)
				{
					return true;
				}
			}
			boolean hideOutput = false;
			if (args.length == 2)
			{
				if (!args[1].equals("true") && !args[1].equals("false"))
				{
					MessageUtil.wrongBool(sender, 2, args);
					return true;
				}
				if (args[1].equals("true"))
				{
					hideOutput = true;
				}
			}
			player.updateCommands();
			if (!hideOutput)
			{
				if (!player.equals(sender))
				{
					MessageUtil.info(sender, player, "의 명령어 리스트를 업데이트 하였습니다.");
				}
				MessageUtil.info(player, sender, "이 당신의 명령어 리스트를 업데이트 하였습니다.");
			}
		}
		else
		{
			MessageUtil.longArg(sender, 2, args);
			MessageUtil.commandInfo(sender, label, usage);
			return true;
		}
		return true;
	}
}
