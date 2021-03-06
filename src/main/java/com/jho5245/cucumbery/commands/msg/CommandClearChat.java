package com.jho5245.cucumbery.commands.msg;

import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.no_groups.Method;
import com.jho5245.cucumbery.util.no_groups.SelectorUtil;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Permission;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class CommandClearChat implements CommandExecutor, TabCompleter
{
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
  {
    if (!Method.hasPermission(sender, Permission.CMD_CLEARCHAT, true))
    {
      return true;
    }
    if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(args)))
    {
      return !(sender instanceof BlockCommandSender);
    }
    String usage = cmd.getUsage().replace("/<command> ", "");
    int random = Method.random(1, 20);
    String easterEgg = switch (random)
            {
              case 1 -> "청소창을 채팅하였습니다...?";
              case 2 -> "청소창을 채팅하였습니다..";
              case 3 -> "청소창을 채팅.. 아니 채팅창을 청소하였습니다";
              default -> "채팅창을 청소하였습니다";
            };
    if (args.length == 0)
    {
      for (int i = 0; i < 500; i++)
      {
        MessageUtil.broadcastPlayer("§c§l§e§a§r§c§h§a§t");
      }
      MessageUtil.broadcastPlayer(Prefix.INFO_CLEARCHAT, ComponentUtil.translate("%s이(가) " + easterEgg, sender));
      if (!(sender instanceof Player))
      {
        MessageUtil.sendMessage(sender, Prefix.INFO_CLEARCHAT, "채팅창을 청소하였습니다");
      }
    }
    else if (args.length <= 2)
    {
      Player target = SelectorUtil.getPlayer(sender, args[0]);
      if (target == null)
      {
        return true;
      }
      for (int i = 0; i < 500; i++)
      {
        MessageUtil.sendMessage(target, "§c§l§e§a§r§c§h§a§t");
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
      if (!hideOutput)
      {
        if (!target.equals(sender))
        {
          MessageUtil.sendMessage(target, Prefix.INFO_CLEARCHAT, sender, "이(가) 당신의 " + easterEgg);
        }
        MessageUtil.sendMessage(sender, Prefix.INFO_CLEARCHAT, target, "의 " + easterEgg);
      }
    }
    else
    {
      MessageUtil.longArg(sender, 2, args);
      MessageUtil.commandInfo(sender, label, usage);
    }
    return true;
  }


  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
  {
    if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(true, args), true))
    {
      return Collections.singletonList(args[0]);
    }
    int length = args.length;

    if (length == 1)
    {
      return Method.tabCompleterPlayer(sender, args);
    }
    else if (length == 2)
    {
      return Method.tabCompleterBoolean(args, "[명령어 출력 숨김 여부]");
    }
    return Collections.singletonList(Prefix.ARGS_LONG.toString());
  }
}
