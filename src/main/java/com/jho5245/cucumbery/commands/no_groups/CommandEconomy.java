package com.jho5245.cucumbery.commands.no_groups;

import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.no_groups.Method;
import com.jho5245.cucumbery.util.no_groups.SelectorUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Permission;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class CommandEconomy implements CommandExecutor, TabCompleter
{
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
  {
    if (!Method.hasPermission(sender, Permission.CMD_ECONOMY, true))
    {
      return true;
    }
    if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(args)))
    {
      return !(sender instanceof BlockCommandSender);
    }
    if (!Cucumbery.using_Vault_Economy)
    {
      MessageUtil.sendError(sender, "&eVault&r 플러그인을 사용하고 있지 않습니다");
      return true;
    }
    if (args.length < 3)
    {
      MessageUtil.shortArg(sender, 3, args);
      MessageUtil.commandInfo(sender, label, Method.getUsage(cmd));
      return true;
    }
    else if (args.length <= 4)
    {
      if (!MessageUtil.isDouble(sender, args[2], true))
      {
        return true;
      }
      boolean hideOuput = false;
      if (args.length == 4)
      {
        if (!MessageUtil.isBoolean(sender, args, 4, true))
        {
          return true;
        }
        hideOuput = Boolean.parseBoolean(args[3]);
      }
      if (!MessageUtil.isDouble(sender, args[2], true))
      {
        return true;
      }
      double input = Double.parseDouble(args[2]);
      if (args[0].equals("set") && !MessageUtil.checkNumberSize(sender, input, 0, 10_000_000_000_000d))
      {
        return true;
      }
      if (Method.equals(args[0], "take", "give") && !MessageUtil.checkNumberSize(sender, input, 0, 10_000_000_000_000d, true, true))
      {
        return true;
      }
      OfflinePlayer offlinePlayer = SelectorUtil.getOfflinePlayer(sender, args[1]);
      if (offlinePlayer == null)
      {
        return true;
      }
      Player player = offlinePlayer.getPlayer();
      double balance = Cucumbery.eco.getBalance(offlinePlayer);
      String inputDisplay = Constant.Sosu2.format(input);
      switch (args[0])
      {
        case "give" -> {
          Cucumbery.eco.depositPlayer(offlinePlayer, input);
          if (!hideOuput)
          {
            String balanceDisplay = Constant.Sosu2.format(Cucumbery.eco.getBalance(offlinePlayer));
            if (player != null && !player.equals(sender))
            {
              MessageUtil.sendMessage(player, Prefix.INFO_ECONOMY, sender, "이 당신에게 &e" + inputDisplay + "원&r을 지급하였습니다");
              MessageUtil.sendMessage(player, Prefix.INFO_ECONOMY, "현재 소지 금액 : &e" + balanceDisplay + "원");
            }
            MessageUtil.sendMessage(sender, Prefix.INFO_ECONOMY, player != null ? player : offlinePlayer, "에게 &e" + inputDisplay + "원&r을 지급하였습니다");
            MessageUtil.sendMessage(sender, Prefix.INFO_ECONOMY, player != null ? player : offlinePlayer, "의 소지 금액 : &e" + balanceDisplay + "원");
          }
        }
        case "take" -> {
          Cucumbery.eco.withdrawPlayer(offlinePlayer, input);
          if (!hideOuput)
          {
            String balanceDisplay = Constant.Sosu2.format(Cucumbery.eco.getBalance(offlinePlayer));
            if (player != null && !player.equals(sender))
            {
              MessageUtil.sendMessage(player, Prefix.INFO_ECONOMY, sender, "이 당신에게서 &e" + inputDisplay + "원&r을 차감하였습니다");
              MessageUtil.sendMessage(player, Prefix.INFO_ECONOMY, "현재 소지 금액 : &e" + balanceDisplay + "원");
            }
            MessageUtil.sendMessage(sender, Prefix.INFO_ECONOMY, player != null ? player : offlinePlayer, "에게서 &e" + inputDisplay + "원&r을 차감하였습니다");
            MessageUtil.sendMessage(sender, Prefix.INFO_ECONOMY, player != null ? player : offlinePlayer, "의 소지 금액 : &e" + balanceDisplay + "원");
          }
        }
        case "set" -> {
          Cucumbery.eco.withdrawPlayer(offlinePlayer, balance);
          Cucumbery.eco.depositPlayer(offlinePlayer, input);
          if (!hideOuput)
          {
            if (player != null && !player.equals(sender))
            {
              MessageUtil.sendMessage(player, Prefix.INFO_ECONOMY, sender, "이 당신의 소지 금액을 &e" + inputDisplay + "원&r으로 설정하였습니다");
            }
            MessageUtil.sendMessage(sender, Prefix.INFO_ECONOMY, player != null ? player : offlinePlayer, "의 소지 금액을 &e" + inputDisplay + "원&r으로 설정하였습니다");
          }
        }
        default -> {
          MessageUtil.wrongArg(sender, 2, args);
          MessageUtil.commandInfo(sender, label, Method.getUsage(cmd));
          return true;
        }
      }
    }
    else
    {
      MessageUtil.longArg(sender, 4, args);
      MessageUtil.commandInfo(sender, label, Method.getUsage(cmd));
      return true;
    }
    return true;
  }

  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
  {
    if (!Cucumbery.using_Vault_Economy)
    {
      return Collections.singletonList("Vault 플러그인을 사용하고 있지 않습니다");
    }
    if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(true, args), true))
    {
      return Collections.singletonList(args[0]);
    }
    int length = args.length;

    if (length == 1)
    {
      return Method.tabCompleterList(args, "<인수>", "give", "set", "take");
    }
    else if (length == 2)
    {
      if (label.equals("ceco"))
      {
        return Method.tabCompleterPlayer(sender, args);
      }
      return Method.tabCompleterOfflinePlayer(sender, args);
    }
    else if (length == 3)
    {
      switch (args[0])
      {
        case "set":
          return Method.tabCompleterDoubleRadius(args, 0, 10_000_000_000_000d, "<금액>");
        case "give":
        case "take":
          return Method.tabCompleterDoubleRadius(args, 0, true, 10_000_000_000_000d, false, "<금액>");
      }
    }
    else if (length == 4)
    {
      return Method.tabCompleterBoolean(args, "[명령어 출력 숨김 여부]");
    }

    return Collections.singletonList(Prefix.ARGS_LONG.toString());
  }
}
