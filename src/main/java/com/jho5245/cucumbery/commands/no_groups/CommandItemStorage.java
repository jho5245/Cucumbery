package com.jho5245.cucumbery.commands.no_groups;

import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.util.no_groups.*;
import com.jho5245.cucumbery.util.no_groups.MessageUtil.ConsonantType;
import com.jho5245.cucumbery.util.additemmanager.AddItemUtil;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig;
import com.jho5245.cucumbery.util.storage.no_groups.ItemInfo;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Permission;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import com.jho5245.cucumbery.util.storage.data.Variable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandItemStorage implements CommandExecutor, TabCompleter
{
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
  {
    if (!Method.hasPermission(sender, Permission.CMD_ITEMSTORAGE, true))
    {
      return true;
    }
    if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(args)))
    {
      return !(sender instanceof BlockCommandSender);
    }
    String usage = cmd.getUsage().replace("/<command> ", "");
    if (args.length == 0)
    {
      MessageUtil.shortArg(sender, 1, args);
      MessageUtil.commandInfo(sender, label, usage);
      return true;
    }
    else if (args.length <= 8)
    {
      if (args[0].equalsIgnoreCase("list"))
      {
        if (args.length == 1)
        {
          MessageUtil.sendMessage(sender, Prefix.INFO_ITEMSTORAGE, "????????? ?????? ?????? : &e" + Variable.itemStorage.size() + "???");
          if (Variable.itemStorage.size() == 0)
          {
            return true;
          }
          List<Component> txt = new ArrayList<>();
          txt.add(ComponentUtil.create(Prefix.INFO_ITEMSTORAGE));
          for (String fileName : Variable.itemStorage.keySet())
          {
            ConfigurationSection itemList = Variable.itemStorage.get(fileName).getConfigurationSection("items");
            StringBuilder itemListText = new StringBuilder();
            if (itemList == null || itemList.getKeys(false).size() == 0)
            {
              itemListText = new StringBuilder("&c????????? ??????\n");
            }
            else
            {
              for (String key : itemList.getKeys(false))
              {
                itemListText.append(key).append("\n");
              }
            }
            txt.add(ComponentUtil.create("&e" + fileName, itemListText.substring(0, itemListText.length() - 1), ClickEvent.Action.SUGGEST_COMMAND, "/cis list " + fileName));
            txt.add(ComponentUtil.create("&r, "));
          }
          txt.remove(txt.size() - 1);
          Component[] txt2 = new Component[txt.size()];
          MessageUtil.sendMessage(sender, txt);
        }
        else if (args.length == 2)
        {
          if (Variable.itemStorage.size() == 0)
          {
            MessageUtil.sendError(sender, "????????? ????????? ????????? ???????????? ????????????");
            return true;
          }
          if (!Variable.itemStorage.containsKey(args[1]))
          {
            MessageUtil.sendError(sender, "????????? ?????? &e" + args[1] + "&r" + MessageUtil.getFinalConsonant(args[1], ConsonantType.??????) + " ?????? ??? ????????????");
            return true;
          }
          YamlConfiguration config = Variable.itemStorage.get(args[1]);
          ConfigurationSection itemList = config.getConfigurationSection("items");
          if (itemList == null || itemList.getKeys(false).size() == 0)
          {
            MessageUtil.sendError(sender, "???????????? ???????????? ?????? ???????????????");
            return true;
          }
          MessageUtil.sendMessage(sender, Prefix.INFO_ITEMSTORAGE, "????????? ?????? &e" + args[1] + "&r??? ????????? ?????? : &e" + itemList.getKeys(false).size() + "&e???");
          List<Component> txt = new ArrayList<>();
          txt.add(ComponentUtil.create(Prefix.INFO_ITEMSTORAGE));
          for (String key : itemList.getKeys(false))
          {
            ItemStack item = ItemSerializer.deserialize(config.getString("items." + key));
            txt.add(ComponentUtil.create("&e" + key, item, ClickEvent.Action.SUGGEST_COMMAND, "/cis get " + args[1] + " " + key));
            txt.add(ComponentUtil.create("&r, "));
          }
          txt.remove(txt.size() - 1);
          Component[] txt2 = new Component[txt.size()];
          MessageUtil.sendMessage(sender, (Object) txt.toArray(txt2));
        }
        else
        {
          MessageUtil.longArg(sender, 2, args);
          MessageUtil.commandInfo(sender, label, "list [????????? ?????? ??????]");
          return true;
        }
      }
      else if (args[0].equalsIgnoreCase("store"))
      {
        if (!(sender instanceof Player))
        {
          MessageUtil.sendError(sender, Prefix.ONLY_PLAYER);
          return true;
        }
        if (args.length < 3)
        {
          MessageUtil.shortArg(sender, 3, args);
          MessageUtil.commandInfo(sender, label, "store <????????? ?????? ??????> <????????? ????????? ??????> [?????? ????????? ????????????] [????????? ?????? ?????? ??????]");
          return true;
        }
        else if (args.length <= 5)
        {
          Player player = (Player) sender;
          ItemStack item = player.getInventory().getItemInMainHand();
          if (!ItemStackUtil.itemExists(item))
          {
            MessageUtil.sendError(sender, Prefix.NO_HOLDING_ITEM);
            return true;
          }
          if (args[2].equals("--all") || Method2.isInvalidFileName(args[2]))
          {
            MessageUtil.sendError(player, "???????????? ??????????????? ???????????? ????????? ??? ????????????");
            return true;
          }
          File folder = new File(Cucumbery.getPlugin().getDataFolder() + "/data/ItemStorage");
          if (!folder.exists())
          {
            boolean success = folder.mkdirs();
            if (!success)
            {
              System.err.println("[Cucumbery] could not create ItemStorage folder!");
            }
          }
          File file = new File(Cucumbery.getPlugin().getDataFolder() + "/data/ItemStorage/" + args[1] + ".yml");
          if (!file.exists())
          {
            try
            {
              boolean success = file.createNewFile();
              if (!success)
              {
                throw new IOException();
              }
            }
            catch (IOException e)
            {
              e.printStackTrace();
              return true;
            }
          }
          CustomConfig itemListConfig = CustomConfig.getCustomConfig("data/ItemStorage/" + args[1] + ".yml");
          boolean override = args.length >= 4 && args[3].equals("true");
          YamlConfiguration config = itemListConfig.getConfig();
          boolean hideOutput = false;
          if (args.length == 5)
          {
            if (!args[4].equals("true") && !args[4].equals("false"))
            {
              MessageUtil.wrongBool(sender, 5, args);
              return true;
            }
            if (args[4].equals("true"))
            {
              hideOutput = true;
            }
          }
          ConfigurationSection configurationSection = config.getConfigurationSection("items");
          if (configurationSection != null && configurationSection.contains(args[2]))
          {
            if (!hideOutput && override)
            {
              MessageUtil.sendWarn(sender, "????????? ?????? &e" + args[1] + "&r??? ?????? ?????? ???????????? ????????? ?????????(&e" + args[2] + "&r)??? ???????????? ?????????????????????");
            }
            else
            {
              MessageUtil.sendError(sender, "????????? ?????? &e" + args[1] + "&r??? ?????? ?????? ???????????? ????????? ?????????(&e" + args[2] + "&r)??? ???????????? ????????? ??? ????????????");
              return true;
            }
          }
          config.set("items." + args[2], ItemSerializer.serialize(item));
          itemListConfig.saveConfig();
          Variable.itemStorage.put(args[1], config);
          MessageUtil.sendMessage(player, Prefix.INFO_ITEMSTORAGE, "?????? ???????????? ?????? ?????? ?????? ???????????? ????????? ?????? &e" + args[1] + "&r??? &e" + args[2] + "&r" + MessageUtil.getFinalConsonant(args[2], ConsonantType.??????) + "??? ???????????? ??????????????????");
        }
        else
        {
          MessageUtil.longArg(sender, 5, args);
          MessageUtil.commandInfo(sender, label, "store <????????? ?????? ??????> <????????? ????????? ??????> [?????? ????????? ????????????] [????????? ?????? ?????? ??????]");
          return true;
        }
      }
      else if (args[0].equalsIgnoreCase("remove"))
      {
        if (args.length < 3)
        {
          MessageUtil.shortArg(sender, 3, args);
          MessageUtil.commandInfo(sender, label, "remove <????????? ?????? ??????> <????????? ????????? ??????|--all> [????????? ?????? ?????? ??????]");
          return true;
        }
        else if (args.length <= 4)
        {
          if (!Variable.itemStorage.containsKey(args[1]))
          {
            MessageUtil.sendError(sender, "????????? ?????? &e" + args[1] + "&r" + MessageUtil.getFinalConsonant(args[1], ConsonantType.??????) + " ?????? ??? ????????????");
            return true;
          }
          YamlConfiguration config = Variable.itemStorage.get(args[1]);
          boolean hideOutput = false;
          if (args.length == 4)
          {
            if (!args[3].equals("true") && !args[3].equals("false"))
            {
              MessageUtil.wrongBool(sender, 2, args);
              return true;
            }
            if (args[3].equals("true"))
            {
              hideOutput = true;
            }
          }
          File file = new File(Cucumbery.getPlugin().getDataFolder() + "/data/ItemStorage/" + args[1] + ".yml");
          if (args[2].equalsIgnoreCase("--all"))
          {
            boolean success = file.delete();
            if (!success)
            {
              System.err.println("[Cucumbery] could not delete " + args[1] + ".yml file!");
            }
            Variable.itemStorage.remove(args[1]);
            if (!hideOutput)
            {
              MessageUtil.sendMessage(sender, Prefix.INFO_ITEMSTORAGE, "????????? ?????? &e" + args[1] + "&r??? ?????? ?????? ???????????? ?????????????????????");
            }
            return true;
          }
          ConfigurationSection configurationSection = config.getConfigurationSection("items");
          if (configurationSection == null || !configurationSection.contains(args[2]))
          {
            MessageUtil.sendError(sender, "????????? ?????? &e" + args[1] + "&r?????? ?????? ?????????(&e" + args[2] + "&r)??? ?????? ??? ????????????");
            return true;
          }
          CustomConfig itemListConfig = CustomConfig.getCustomConfig("data/ItemStorage/" + args[1] + ".yml");
          itemListConfig.getConfig().set("items." + args[2], null);
          config.set("items." + args[2], null);
          if (configurationSection.getKeys(false).size() == 0)
          {
            boolean success = file.delete();
            if (!success)
            {
              System.err.println("[Cucumbery] could not delete " + args[1] + ".yml file!");
            }
            Variable.itemStorage.remove(args[1]);
          }
          else
          {
            itemListConfig.saveConfig();
            Variable.itemStorage.put(args[1], config);
          }
          if (!hideOutput)
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_ITEMSTORAGE, "????????? ?????? &e" + args[1] + "&r?????? &e" + args[2] + "&r ???????????? ?????????????????????");
          }
        }
        else
        {
          MessageUtil.longArg(sender, 4, args);
          MessageUtil.commandInfo(sender, label, "remove <????????? ?????? ??????> <????????? ????????? ??????|--all> [????????? ?????? ?????? ??????]");
          return true;
        }
      }
      else if (args[0].equalsIgnoreCase("get"))
      {
        if (!(sender instanceof Player))
        {
          MessageUtil.sendError(sender, Prefix.ONLY_PLAYER);
          return true;
        }
        if (args.length < 3)
        {
          MessageUtil.shortArg(sender, 3, args);
          MessageUtil.commandInfo(sender, label, "get <????????? ?????? ??????> <????????? ??????> [??????]");
          return true;
        }
        else if (args.length <= 4)
        {
          Player player = (Player) sender;
          if (!Variable.itemStorage.containsKey(args[1]))
          {
            MessageUtil.sendError(sender, "????????? ?????? &e" + args[1] + "&r" + MessageUtil.getFinalConsonant(args[1], ConsonantType.??????) + " ?????? ??? ????????????");
            return true;
          }
          YamlConfiguration config = Variable.itemStorage.get(args[1]);
          ConfigurationSection itemsSection = config == null ? null : config.getConfigurationSection("items");
          if (itemsSection != null && args[2].equals("--all"))
          {
            for (String key : itemsSection.getKeys(false))
            {
              Bukkit.dispatchCommand(sender, "cis get '" + args[1].replace("'", "''") + "' '" + key.replace("'", "''") + "'" + (args.length >= 4 ? (" " + args[3]) : ""));
            }
            return true;
          }
          boolean validItem = true;
          ItemStack item = null;
          if (config == null || !config.contains("items." + args[2]))
          {
            validItem = false;
          }
          else
          {
            item = ItemSerializer.deserialize(config.getString("items." + args[2]));
          }
          if (!ItemStackUtil.itemExists(item))
          {
            item = config != null ? config.getItemStack("items." + args[2]) : null;
          }
          validItem = validItem && ItemStackUtil.itemExists(item);
          if (!validItem)
          {
            MessageUtil.sendError(sender, "????????? ?????? &e" + args[1] + "&r?????? ?????? ?????????(&e" + args[2] + "&r)??? ?????? ??? ????????? ????????? ??????????????????");
            return true;
          }
          item = item.clone();
          int amount = item.getAmount();
          if (args.length == 4)
          {
            if (!MessageUtil.isInteger(sender, args[3], true))
            {
              return true;
            }
            int inputAmount = Integer.parseInt(args[3]);
            if (!MessageUtil.checkNumberSize(sender, inputAmount, 1, 2304))
            {
              return true;
            }
            item.setAmount(inputAmount);
            amount = inputAmount;
          }
          AddItemUtil.addItemResult2(sender, player, item, amount).sendFeedback(false);
        }
        else
        {
          MessageUtil.longArg(sender, 4, args);
          MessageUtil.commandInfo(sender, label, "get <????????? ?????? ??????> <????????? ??????> [??????]");
          return true;
        }
      }
      else if (args[0].equalsIgnoreCase("give"))
      {
        if (args.length < 4)
        {
          MessageUtil.shortArg(sender, 4, args);
          MessageUtil.commandInfo(sender, label, "give <????????????> <????????? ?????? ??????> <????????? ??????> [??????] [????????? ?????? ?????? ??????]");
          return true;
        }
        else if (args.length <= 6)
        {
          Player target = SelectorUtil.getPlayer(sender, args[1]);
          if (target == null)
          {
            return true;
          }
          if (!Variable.itemStorage.containsKey(args[2]))
          {
            MessageUtil.sendError(sender, "????????? ?????? &e" + args[2] + "&r" + MessageUtil.getFinalConsonant(args[2], ConsonantType.??????) + " ?????? ??? ????????????");
            return true;
          }
          YamlConfiguration config = Variable.itemStorage.get(args[2]);
          ConfigurationSection itemsSection = config == null ? null : config.getConfigurationSection("items");
          if (itemsSection != null && args[3].equals("--all"))
          {
            for (String key : itemsSection.getKeys(false))
            {
              Bukkit.dispatchCommand(sender, "cis give " + args[1] + " " + args[2] + " " + key + (args.length >= 5 ? (" " + args[4]) : "") + (args.length >= 6 ? (" " + args[5]) : ""));
            }
            return true;
          }
          boolean validItem = true;
          ItemStack item = null;
          if (config == null || !config.contains("items." + args[3]))
          {
            validItem = false;
          }
          else
          {
            item = ItemSerializer.deserialize(config.getString("items." + args[3]));
          }
          if (!ItemStackUtil.itemExists(item))
          {
            item = config != null ? config.getItemStack("items." + args[3]) : null;
          }
          validItem = validItem && ItemStackUtil.itemExists(item);
          if (!validItem)
          {
            MessageUtil.sendError(sender, "????????? ?????? &e" + args[2] + "&r?????? ?????? ?????????(&e" + args[3] + "&r)??? ?????? ??? ????????? ????????? ??????????????????");
            return true;
          }
          item = item.clone();
          int amount = item.getAmount();
          if (args.length >= 5)
          {
            if (!MessageUtil.isInteger(sender, args[4], true))
            {
              return true;
            }
            int inputAmount = Integer.parseInt(args[4]);
            if (inputAmount != -1 && !MessageUtil.checkNumberSize(sender, inputAmount, 1, 2304))
            {
              return true;
            }
            item.setAmount(inputAmount == -1 ? amount : inputAmount);
          }
          ItemStack lostItem = null;
          int lostAmount = 0;
          boolean hideOutput = false;
          if (args.length == 6)
          {
            if (!args[5].equals("true") && !args[5].equals("false"))
            {
              MessageUtil.wrongBool(sender, 6, args);
              return true;
            }
            if (args[5].equals("true"))
            {
              hideOutput = true;
            }
          }
          AddItemUtil.addItemResult2(sender, target, item, amount).sendFeedback(hideOutput);
        }
        else
        {
          MessageUtil.longArg(sender, 6, args);
          MessageUtil.commandInfo(sender, label, "give <????????????> <????????? ?????? ??????> <????????? ??????> [??????] [????????? ?????? ?????? ??????]");
          return true;
        }
      }
      else if (args[0].equalsIgnoreCase("setitem"))
      {
        if (args.length < 5)
        {
          MessageUtil.shortArg(sender, 5, args);
          MessageUtil.commandInfo(sender, label, "setitem <????????????> <??????> <????????? ?????? ??????> <????????? ??????> [??????] [?????? ????????? ????????????] [????????? ?????? ?????? ??????]");
          return true;
        }
        else
        {
          Player target = SelectorUtil.getPlayer(sender, args[1]);
          if (target == null)
          {
            return true;
          }
          int slot;
          int heldItemSlot = target.getInventory().getHeldItemSlot();
          switch (args[2])
          {
            case "weapon":
            case "weapon.mainhand":
              slot = heldItemSlot + 1;
              break;
            case "weapon.offhand":
              slot = 41;
              break;
            case "armor.head":
              slot = 37;
              break;
            case "armor.chest":
              slot = 38;
              break;
            case "armor.legs":
              slot = 39;
              break;
            case "armor.feet":
              slot = 40;
              break;
            default:
              if (args[2].startsWith("inventory."))
              {
                try
                {
                  slot = Integer.parseInt(args[2].replace("inventory.", "")) + 9;
                }
                catch (Exception e)
                {
                  MessageUtil.wrongArg(sender, 3, args);
                  return true;
                }
                if (slot < 10 || slot > 36)
                {
                  MessageUtil.wrongArg(sender, 3, args);
                  return true;
                }
              }
              else if (args[2].startsWith("hotbar."))
              {
                try
                {
                  slot = Integer.parseInt(args[2].replace("hotbar.", ""));
                }
                catch (Exception e)
                {
                  MessageUtil.wrongArg(sender, 3, args);
                  return true;
                }
                if (slot < 1 || slot > 9)
                {
                  MessageUtil.wrongArg(sender, 3, args);
                  return true;
                }
              }
              else
              {
                MessageUtil.wrongArg(sender, 3, args);
                return true;
              }
          }
          if (!Variable.itemStorage.containsKey(args[3]))
          {
            MessageUtil.sendError(sender, "????????? ?????? &e" + args[3] + "&r" + MessageUtil.getFinalConsonant(args[3], ConsonantType.??????) + " ?????? ??? ????????????");
            return true;
          }
          YamlConfiguration config = Variable.itemStorage.get(args[3]);
          boolean validItem = true;
          ItemStack item = null;
          if (config == null || !config.contains("items." + args[4]))
          {
            validItem = false;
          }
          else
          {
            item = ItemSerializer.deserialize(config.getString("items." + args[4]));
          }
          if (!ItemStackUtil.itemExists(item))
          {
            item = config != null ? config.getItemStack("items." + args[4]) : null;
          }
          validItem = validItem && ItemStackUtil.itemExists(item);
          if (!validItem)
          {
            MessageUtil.sendError(sender, "????????? ?????? &e" + args[3] + "&r?????? ?????? ?????????(&e" + args[4] + "&r)??? ?????? ??? ????????? ????????? ??????????????????");
            return true;
          }
          item = item.clone();
          int amount = item.getAmount();
          if (args.length >= 6)
          {
            if (!MessageUtil.isInteger(sender, args[5], true))
            {
              return true;
            }
            int inputAmount = Integer.parseInt(args[5]);
            if (!MessageUtil.checkNumberSize(sender, inputAmount, 1, 127))
            {
              return true;
            }
            if (inputAmount == 0)
            {
              MessageUtil.sendError(sender, Prefix.ONLY_NATURALNUMBER);
              return true;
            }
            item.setAmount(inputAmount == -1 ? amount : inputAmount);
          }
          if (!MessageUtil.isBoolean(sender, args, 7, true))
          {
            return true;
          }
          boolean override = args.length >= 7 && args[6].equals("true");
          if (!MessageUtil.isBoolean(sender, args, 8, true))
          {
            return true;
          }
          boolean hideOutput = args.length == 8 && args[7].equals("true");
          ItemStack origin = target.getInventory().getItem(slot - 1);
          boolean itemExists = ItemStackUtil.itemExists(origin);
          if (itemExists)
          {
            origin = origin.clone();
          }
          if (!override && itemExists)
          {
            if (!hideOutput)
            {
              MessageUtil.sendMessage(sender, Prefix.INFO_ERROR, "???????????? ????????? ??? ????????????. %s???(???) ?????? %s ????????? ???????????? ????????? ????????????", target, ComponentUtil.create(args[2]).hoverEvent(origin.asHoverEvent()));
              if (sender instanceof Player)
              {
                Method.playSound(sender, Constant.ERROR_SOUND, Constant.ERROR_SOUND_VOLUME, Constant.ERROR_SOUND_PITCH);
              }
            }
            return true;
          }
          target.getInventory().setItem(slot - 1, item);
          if (!hideOutput)
          {
            if (override && itemExists)
            {
              MessageUtil.sendMessage(sender, Prefix.INFO_WARN, "?????? %s??? %s ????????? ???????????? ???????????? ?????????????????????", target, ComponentUtil.create(args[2]).hoverEvent(origin.asHoverEvent()));
              if (sender instanceof Player)
              {
                Method.playSound(sender, Constant.WARNING_SOUND, Constant.WARNING_SOUND_VOLUME, Constant.WARNING_SOUND_PITCH);
              }
            }
            String itemName = args[3];
            String finalConsonant = MessageUtil.getFinalConsonant(itemName, ConsonantType.??????);
            Component txt = ComponentUtil.create(MessageUtil.as(Prefix.INFO_ITEMSTORAGE, sender, "??? ????????? &e" + args[2] + "&r ????????? &e" + itemName + "&r" + finalConsonant + "&e " + amount + "???&r ?????????????????????"), item);
            if (!target.equals(sender))
            {
              MessageUtil.sendMessage(target, txt);
            }
            txt = ComponentUtil.create(MessageUtil.as(Prefix.INFO_ITEMSTORAGE, target, "??? &e" + args[2] + "&r ????????? &e" + itemName + "&r" + finalConsonant + "&e " + amount + "???&r ?????????????????????"), item);
            MessageUtil.sendMessage(sender, txt);
          }
        }
      }
      else if (args[0].equalsIgnoreCase("info"))
      {
        if (args.length < 3)
        {
          MessageUtil.shortArg(sender, 3, args);
          MessageUtil.commandInfo(sender, label, "info <????????? ?????? ??????> <????????? ??????>");
          return true;
        }
        else if (args.length == 3)
        {
          File folder = new File(Cucumbery.getPlugin().getDataFolder() + "/data/ItemStorage");
          if (!folder.exists())
          {
            boolean success = folder.mkdirs();
            if (!success)
            {
              System.err.println("[Cucumbery] could not create ItemStorage folder!");
            }
          }
          File file = new File(Cucumbery.getPlugin().getDataFolder() + "/data/ItemStorage/" + args[1] + ".yml");
          if (!file.exists())
          {
            MessageUtil.sendError(sender, "????????? ?????? &e" + args[1] + "&r" + MessageUtil.getFinalConsonant(args[1], ConsonantType.??????) + " ?????? ??? ????????????");
            return true;
          }
          CustomConfig itemListConfig = CustomConfig.getCustomConfig("data/ItemStorage/" + args[1] + ".yml");
          FileConfiguration config = itemListConfig.getConfig();
          if (config.getItemStack("items." + args[2]) == null)
          {
            MessageUtil.sendError(sender, "????????? ?????? &e" + args[1] + "&r?????? ?????? ?????????(&e" + args[2] + "&r)??? ?????? ??? ????????????");
            return true;
          }
          ItemStack item = config.getItemStack("items." + args[2]);
          ItemInfo.sendInfo(sender, item);
        }
        else
        {
          MessageUtil.longArg(sender, 3, args);
          MessageUtil.commandInfo(sender, label, "info <????????? ?????? ??????> <????????? ??????>");
          return true;
        }
      }
      else
      {
        MessageUtil.wrongArg(sender, 1, args);
        MessageUtil.commandInfo(sender, label, usage);
        return true;
      }
    }
    else
    {
      if (args[0].equalsIgnoreCase("list"))
      {
        MessageUtil.longArg(sender, 2, args);
        MessageUtil.commandInfo(sender, label, "list [????????? ?????? ??????]");
        return true;
      }
      else if (args[0].equalsIgnoreCase("store"))
      {
        MessageUtil.longArg(sender, 3, args);
        MessageUtil.commandInfo(sender, label, "store <????????? ?????? ??????> <????????? ????????? ??????> [?????? ????????? ????????????] [????????? ?????? ?????? ??????]");
        return true;
      }
      else if (args[0].equalsIgnoreCase("remove"))
      {
        MessageUtil.longArg(sender, 3, args);
        MessageUtil.commandInfo(sender, label, "remove <????????? ?????? ??????> <????????? ????????? ??????|--all> [????????? ?????? ?????? ??????]");
        return true;
      }
      else if (args[0].equalsIgnoreCase("get"))
      {
        MessageUtil.longArg(sender, 4, args);
        MessageUtil.commandInfo(sender, label, "get <????????? ?????? ??????> <????????? ??????> [??????]");
        return true;
      }
      else if (args[0].equalsIgnoreCase("give"))
      {
        MessageUtil.longArg(sender, 6, args);
        MessageUtil.commandInfo(sender, label, "give <????????????> <????????? ?????? ??????> <????????? ??????> [??????] [????????? ?????? ?????? ??????]");
        return true;
      }
      else if (args[0].equalsIgnoreCase("setitem"))
      {
        MessageUtil.longArg(sender, 7, args);
        MessageUtil.commandInfo(sender, label, "setitem <????????????> <??????> <????????? ?????? ??????> <????????? ??????> [??????] [????????? ?????? ?????? ??????]");
        return true;
      }
      else if (args[0].equalsIgnoreCase("checkamount"))
      {
        MessageUtil.longArg(sender, 6, args);
        MessageUtil.commandInfo(sender, label, "checkamount <????????????> <????????? ?????? ??????> <????????? ??????> [?????????|*]");
        return true;
      }
      else if (args[0].equalsIgnoreCase("info"))
      {
        MessageUtil.longArg(sender, 3, args);
        MessageUtil.commandInfo(sender, label, "info <????????? ?????? ??????> <????????? ??????>");
        return true;
      }
      else
      {
        MessageUtil.longArg(sender, 7, args);
        MessageUtil.commandInfo(sender, label, usage);
        return true;
      }
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
    String lastArg = args[length - 1];
    if (length == 1)
    {
      return Method.tabCompleterList(args, "<??????>", "list", "store", "remove", "get", "info", "give", "setitem");
    }
    else if (length == 2)
    {
      if (Method.equals(args[0], "store") && sender instanceof Player player)
      {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!ItemStackUtil.itemExists(item))
        {
          return Collections.singletonList(Prefix.NO_HOLDING_ITEM.toString());
        }
        if (Variable.itemStorage.isEmpty())
        {
          return Method.tabCompleterList(args, "<????????? ????????? ??????>", true);
        }
        List<String> list = new ArrayList<>(Method.tabCompleterList(args, Variable.itemStorage.keySet(), "<????????? ??????>"));
        if (!Variable.itemStorage.containsKey(lastArg))
        {
          list.addAll(Method.tabCompleterList(args, "<????????? ????????? ??????>", true));
        }
        return list;
      }
      else if (args[0].equals("list"))
      {
        if (Variable.itemStorage.isEmpty())
        {
          return Collections.singletonList("????????? ????????? ????????? ???????????? ????????????");
        }
        return Method.tabCompleterList(args, Variable.itemStorage.keySet(), "[????????? ??????]");
      }
      else if (Method.equals(args[0], "remove", "get", "info"))
      {
        if (Variable.itemStorage.isEmpty())
        {
          return Collections.singletonList("????????? ????????? ????????? ???????????? ????????????");
        }
        return Method.tabCompleterList(args, Variable.itemStorage.keySet(), "<????????? ??????>");
      }
      else if (Method.equals(args[0], "give", "checkamount", "setitem"))
      {
        return Method.tabCompleterPlayer(sender, args);
      }
    }
    else if (length == 3)
    {
      switch (args[0])
      {
        case "remove":
        case "info":
        {
          FileConfiguration config = Variable.itemStorage.get(args[1]);
          if (config == null)
          {
            return Collections.singletonList("'" + args[1] + "'" + MessageUtil.getFinalConsonant(args[1], MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ????????? ???????????????");
          }
          ConfigurationSection itemList = config.getConfigurationSection("items");
          if (itemList == null)
          {
            return Collections.singletonList(args[1] + " ????????? ???????????? ????????? ???????????? ????????????");
          }
          return Method.tabCompleterList(args, itemList.getKeys(false), "<?????????>");

        }
        case "get":
        {
          FileConfiguration config = Variable.itemStorage.get(args[1]);
          if (config == null)
          {
            return Collections.singletonList("'" + args[1] + "'"  + MessageUtil.getFinalConsonant(args[1], MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ????????? ???????????????");
          }
          ConfigurationSection itemList = config.getConfigurationSection("items");
          if (itemList == null)
          {
            return Collections.singletonList(args[1] + " ????????? ???????????? ????????? ???????????? ????????????");
          }
          List<String> list = new ArrayList<>(Method.tabCompleterList(args, Method.addAll(itemList.getKeys(false), "--all"), "<?????????>"));
          if (itemList.getKeys(false).size() > 1)
          {
            if (lastArg.equals("--all"))
            {
              list.clear();
              list.addAll(Method.tabCompleterList(args,  "<" + args[1] + " ????????? ????????? ?????? ?????????>", "--all"));
            }
          }
          return list;
        }
        case "store":
          Player player = (Player) sender;
          ItemStack item = player.getInventory().getItemInMainHand();
          if (!ItemStackUtil.itemExists(item))
          {
            return Collections.singletonList(Prefix.NO_HOLDING_ITEM.toString());
          }
          FileConfiguration config = Variable.itemStorage.get(args[1]);
          if (config == null)
          {
            return Collections.singletonList("<????????? ?????????>");
          }
          ConfigurationSection itemList = config.getConfigurationSection("items");
          if (itemList == null)
          {
            return Collections.singletonList("<????????? ?????????>");
          }
          return Method.tabCompleterList(args, itemList.getKeys(false), itemList.getKeys(false).contains(args[2]) ? "<???????????? ?????????>" : "<????????? ?????????>", true);
        case "give":
        case "checkamount":
          if (Variable.itemStorage.size() == 0)
          {
            return Collections.singletonList("????????? ????????? ????????? ???????????? ????????????");
          }
          return Method.tabCompleterList(args, Variable.itemStorage.keySet(), "<????????? ??????>");
        case "setitem":
          List<String> list = new ArrayList<>();
          for (int i = 1; i <= 27; i++)
          {
            list.add("inventory." + i);
          }
          for (int i = 1; i <= 9; i++)
          {
            list.add("hotbar." + i);
          }
          list.addAll(Arrays.asList("weapon", "weapon.mainhand", "weapon.offhand", "armor.head", "armor.chest", "armor.legs", "armor.feet"));
          return Method.tabCompleterList(args, list, "<??????>");
        default:
          if (!args[length - 1].equals(""))
          {
            return Collections.singletonList(Prefix.ARGS_LONG.toString());
          }
      }
    }
    else if (length == 4)
    {
      switch (args[0])
      {
        case "get":
          return Method.tabCompleterIntegerRadius(args, 1, 2304, "[??????]");
        case "checkamount":
        {
          FileConfiguration config = Variable.itemStorage.get(args[2]);
          if (config == null)
          {
            return Collections.singletonList(args[2] + MessageUtil.getFinalConsonant(args[2], MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ????????? ???????????????");
          }
          ConfigurationSection itemList = config.getConfigurationSection("items");
          if (itemList == null)
          {
            return Collections.singletonList(args[2] + " ????????? ???????????? ????????? ???????????? ????????????");
          }
          return Method.tabCompleterList(args, itemList.getKeys(false), "<?????????>");
        }
        case "give":
        {
          FileConfiguration config = Variable.itemStorage.get(args[2]);
          if (config == null)
          {
            return Collections.singletonList(args[2] + MessageUtil.getFinalConsonant(args[2], MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ????????? ???????????????");
          }
          ConfigurationSection itemList = config.getConfigurationSection("items");
          if (itemList == null)
          {
            return Collections.singletonList(args[2] + " ????????? ???????????? ????????? ???????????? ????????????");
          }
          return Method.tabCompleterList(args, Method.addAll(itemList.getKeys(false), "--all"), "<?????????>");
        }
        case "setitem":
          if (Variable.itemStorage.size() == 0)
          {
            return Collections.singletonList("????????? ????????? ????????? ???????????? ????????????");
          }
          return Method.tabCompleterList(args, Variable.itemStorage.keySet(), "<????????? ??????>");
        case "store":
          return Method.tabCompleterBoolean(args, "[?????? ????????? ????????????]");
        case "remove":
          return Method.tabCompleterBoolean(args, "[????????? ?????? ?????? ??????]");
        default:
          if (!args[length - 1].equals(""))
          {
            return Collections.singletonList(Prefix.ARGS_LONG.toString());
          }
      }
    }
    else if (length == 5)
    {
      switch (args[0])
      {
        case "give":
          return Method.tabCompleterIntegerRadius(args, 1, 2304, "[??????]", "-1");
        case "store":
          return Method.tabCompleterBoolean(args, "[????????? ?????? ?????? ??????]");
        case "setitem":
          FileConfiguration config = Variable.itemStorage.get(args[3]);
          if (config == null)
          {
            return Collections.singletonList(args[3] + MessageUtil.getFinalConsonant(args[3], MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ????????? ???????????????");
          }
          ConfigurationSection itemList = config.getConfigurationSection("items");
          if (itemList == null)
          {
            return Collections.singletonList(args[3] + " ????????? ???????????? ????????? ???????????? ????????????");
          }
          return Method.tabCompleterList(args, itemList.getKeys(false), "<?????????>");
        case "checkamount":
          return Method.tabCompleterIntegerRadius(args, 0, 32767, "[????????? ??????]", "-1");
        default:
          if (!args[length - 1].equals(""))
          {
            return Collections.singletonList(Prefix.ARGS_LONG.toString());
          }
      }
    }
    else if (length == 6)
    {
      if (args[0].equals("setitem"))
      {
        return Method.tabCompleterIntegerRadius(args, 1, 127, "[??????]");
      }
      else if (args[0].equals("give"))
      {
        return Method.tabCompleterBoolean(args, "[????????? ?????? ?????? ??????]");
      }
    }
    else if (length == 7)
    {
      if (args[0].equals("setitem"))
      {
        return Method.tabCompleterBoolean(args, "[?????? ????????? ????????????]");
      }
    }
    else if (length == 8)
    {
      if (args[0].equals("setitem"))
      {
        return Method.tabCompleterBoolean(args, "[????????? ?????? ?????? ??????]");
      }
    }

    return Collections.singletonList(Prefix.ARGS_LONG.toString());
  }
}