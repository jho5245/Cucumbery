package com.jho5245.cucumbery.commands.no_groups;

import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.util.no_groups.*;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Permission;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CommandVirtualChest implements CommandExecutor, TabCompleter
{
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
  {
    if (!(sender instanceof Player))
    {
      MessageUtil.sendError(sender, Prefix.ONLY_PLAYER);
      return true;
    }
    String usage = Method.getUsage(cmd);
    switch (cmd.getName())
    {
      case "virtualchest" -> {
        if (!Method.hasPermission(sender, Permission.CMD_VIRTUAL_CHEST, true))
        {
          return true;
        }
        if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(args)))
        {
          return true;
        }
        if (args.length > 1)
        {
          MessageUtil.longArg(sender, 1, args);
          MessageUtil.commandInfo(sender, label, usage);
          return true;
        }
        Player player = (Player) sender;
        if (args.length == 1 && !args[0].equals("default") && !Method.hasPermission(player, Permission.CMD_VIRTUAL_CHEST_UNLIMITED, false)
                && !Method.hasPermission(player, Permission.CMD_VIRTUAL_CHEST + "." + args[0], false))
        {
          MessageUtil.sendError(player, "?????? ??????????????? ????????? ????????? ????????????");
          return true;
        }
        String chestName = args.length == 1 ? args[0] : "default";
        if (Method2.isInvalidFileName(chestName))
        {
          MessageUtil.sendError(player, "???????????? ??????????????? ??????????????? ????????? ??? ????????????");
          return true;
        }
        UUID uuid = player.getUniqueId();
        CustomConfig customConfig = CustomConfig.getCustomConfig("data/VirtualChest/" + uuid.toString() + "/" + chestName + ".yml");
        YamlConfiguration config = customConfig.getConfig();
        Inventory chest = Bukkit.createInventory(null, 54, Constant.VIRTUAL_CHEST_MENU_PREFIX + "??6???????????? - ??e" + chestName);
        ConfigurationSection items = config.getConfigurationSection("items");
        if (items != null && !items.getKeys(false).isEmpty())
        {
          for (int i = 1; i <= 54; i++)
          {
            String itemString = config.getString("items." + i);
            if (itemString != null)
            {
              ItemStack item = ItemSerializer.deserialize(itemString);
              chest.setItem(i - 1, item);
            }
          }
        }
        if (Method.inventoryEmpty(chest))
        {
          customConfig.delete();
        }
        player.openInventory(chest);
        MessageUtil.sendMessage(player, Prefix.INFO_VIRTUAL_CHEST, "&e" + chestName + "&r ??????????????? ?????????");
      }
      case "virtualchestadd" -> {
        if (!Method.hasPermission(sender, Permission.CMD_VIRTUAL_CHEST_ADMIN, true))
        {
          return true;
        }

        if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(args)))
        {
          return true;
        }
        if (args.length > 1)
        {
          MessageUtil.longArg(sender, 1, args);
          MessageUtil.commandInfo(sender, label, usage);
          return true;
        }
        Player player = (Player) sender;
        if (args.length == 1 && !args[0].equals("default") && !Method.hasPermission(player, Permission.CMD_VIRTUAL_CHEST_UNLIMITED, false)
                && !Method.hasPermission(player, Permission.CMD_VIRTUAL_CHEST + "." + args[0], false))
        {
          MessageUtil.sendError(player, "?????? ??????????????? ????????? ????????? ????????????");
          return true;
        }
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (!ItemStackUtil.itemExists(handItem))
        {
          MessageUtil.sendError(player, Prefix.NO_HOLDING_ITEM);
          return true;
        }
        String chestName = args.length == 1 ? args[0] : "default";
        if (Method2.isInvalidFileName(chestName))
        {
          MessageUtil.sendError(player, "???????????? ??????????????? ??????????????? ????????? ??? ????????????");
          return true;
        }
        UUID uuid = player.getUniqueId();
        CustomConfig customConfig = CustomConfig.getCustomConfig("data/VirtualChest/" + uuid.toString() + "/" + chestName + ".yml");
        YamlConfiguration config = customConfig.getConfig();
        Inventory chest = Bukkit.createInventory(null, 54, Constant.VIRTUAL_CHEST_MENU_PREFIX + "??6???????????? - ??e" + chestName);
        ConfigurationSection items = config.getConfigurationSection("items");
        if (items != null && !items.getKeys(false).isEmpty())
        {
          for (int i = 1; i <= 54; i++)
          {
            String itemString = config.getString("items." + i);
            if (itemString != null)
            {
              ItemStack item = ItemSerializer.deserialize(itemString);
              chest.setItem(i - 1, item);
            }
          }
        }
        if (chest.firstEmpty() == -1)
        {
          MessageUtil.sendError(player, "&e" + chestName + "&r ?????????????????? ???????????? ?????? ????????????");
          return true;
        }
        chest.addItem(handItem);
        ItemStack[] contents = chest.getContents();
        for (int i = 1; i <= contents.length; i++)
        {
          config.set("items." + i, ItemSerializer.serialize(contents[i - 1]));
        }
        customConfig.saveConfig();
        MessageUtil.sendMessage(player, Prefix.INFO_VIRTUAL_CHEST, "%s ??????????????? %s???(???) ?????????????????????", "&e" + chestName, handItem);
      }
      case "virtualchestadmin" -> {
        args = MessageUtil.wrapWithQuote(args);
        if (!Method.hasPermission(sender, Permission.CMD_VIRTUAL_CHEST_ADMIN, true))
        {
          return true;
        }
        if (args.length < 1)
        {
          MessageUtil.shortArg(sender, 1, args);
          MessageUtil.commandInfo(sender, label, usage);
          return true;
        }
        if (args.length > 2)
        {
          MessageUtil.longArg(sender, 2, args);
          MessageUtil.commandInfo(sender, label, usage);
          return true;
        }
        Player player = (Player) sender;
        OfflinePlayer target = SelectorUtil.getOfflinePlayer(sender, args[0], true);
        if (target == null)
        {
          return true;
        }
        UUID uuid = target.getUniqueId();
        String chestName = args.length == 2 ? args[1] : "default";
        if (Method2.isInvalidFileName(chestName))
        {
          MessageUtil.sendError(player, "???????????? ??????????????? ??????????????? ????????? ??? ????????????");
          return true;
        }
        @Nullable YamlConfiguration config;
        File file = new File(Cucumbery.getPlugin().getDataFolder() + "/data/VirtualChest/" + uuid.toString() + "/" + chestName + ".yml");
        if (!file.exists())
        {
          config = null;
        }
        else
        {
          config = CustomConfig.getCustomConfig(file).getConfig();
        }
        if (config == null)
        {
          MessageUtil.sendError(player, "&e" + Method.getDisplayName(target) + "&r??? ??e" + chestName + "&r ??????????????? ???????????? ????????????");
          return true;
        }
        Inventory chest = Bukkit.createInventory(null, 54,
                Constant.VIRTUAL_CHEST_ADMIN_MENU_PREFIX + "??6???????????? - ??e" + chestName + Method.format("owneruuid:" + target.getUniqueId(), "??"));
        ConfigurationSection items = config.getConfigurationSection("items");
        if (items != null && !items.getKeys(false).isEmpty())
        {
          for (int i = 1; i <= 54; i++)
          {
            String itemString = config.getString("items." + i);
            if (itemString != null)
            {
              ItemStack item = ItemSerializer.deserialize(itemString);
              chest.setItem(i - 1, item);
            }
          }
        }
        player.openInventory(chest);
        MessageUtil.sendMessage(player, Prefix.INFO_VIRTUAL_CHEST, "&e" + Method.getDisplayName(target) + "&r??? &e" + chestName + "&r ??????????????? ?????????");
      }
    }
    return true;
  }

  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
  {
    if (!(sender instanceof Player player))
    {
      return Collections.emptyList();
    }
    if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(true, args), true))
    {
      return Collections.singletonList(args[0]);
    }
    int length = args.length;
    String name = cmd.getName();
    if (name.equals("virtualchest") || name.equals("virtualchestadd"))
    {
      if (length == 1)
      {
        if (Method2.isInvalidFileName(args[0]))
        {
          return Collections.singletonList(args[0] + MessageUtil.getFinalConsonant(args[0], MessageUtil.ConsonantType.??????) + " ???????????? ?????? ?????? ???????????????");
        }
        File folder = new File(Cucumbery.getPlugin().getDataFolder() + "/data/VirtualChest/" + player.getUniqueId());
        if (folder.exists())
        {
          List<String> list = new ArrayList<>();
          File[] files = folder.listFiles();
          if (files != null)
          {
            for (File file : files)
            {
              String fileName = file.getName();
              if (fileName.endsWith(".yml"))
              {
                fileName = fileName.substring(0, fileName.length() - 4);
                list.add(fileName);
              }
            }
          }
          return Method.tabCompleterList(args, list, "[?????? ??????]", true);
        }
        return Method.tabCompleterList(args, "[?????? ??????]", true, "default");
      }

    }
    else if (name.equals("virtualchestadmin"))
    {
      if (length == 1)
      {
        if (label.equals("chestadmin"))
        {
          return Method.tabCompleterPlayer(sender, args);
        }
        return Method.tabCompleterOfflinePlayer(sender, args);
      }
      else if (length == 2)
      {
        OfflinePlayer target = SelectorUtil.getOfflinePlayer(sender, args[0], false);
        if (target != null)
        {
          if (Method2.isInvalidFileName(args[1]))
          {
            return Collections.singletonList(args[1] + MessageUtil.getFinalConsonant(args[1], MessageUtil.ConsonantType.??????) + " ???????????? ?????? ?????? ???????????????");
          }
          File folder = new File(Cucumbery.getPlugin().getDataFolder() + "/data/VirtualChest/" + target.getUniqueId());
          if (folder.exists())
          {
            List<String> list = new ArrayList<>();
            File[] files = folder.listFiles();
            if (files != null)
            {
              for (File file : files)
              {
                String fileName = file.getName();
                if (fileName.endsWith(".yml"))
                {
                  fileName = fileName.substring(0, fileName.length() - 4);
                  list.add(fileName);
                }
              }
            }
            return Method.tabCompleterList(args, list, "[?????? ??????]");
          }
          return Method.tabCompleterList(args, "[?????? ??????]");
        }
      }
    }
    return Collections.singletonList(Prefix.ARGS_LONG.toString());
  }
}
