package com.jho5245.cucumbery.commands.itemtag;

import com.jho5245.cucumbery.custom.customeffect.CustomEffect.DisplayType;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectType;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.no_groups.Method;
import com.jho5245.cucumbery.util.no_groups.CommandTabUtil;
import com.jho5245.cucumbery.util.nbt.CucumberyTag;
import com.jho5245.cucumbery.util.nbt.NBTAPI;
import com.jho5245.cucumbery.util.storage.no_groups.ItemCategory;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.component.util.ItemNameUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Constant.ItemUsageType;
import com.jho5245.cucumbery.util.storage.data.Constant.RestrictionType;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import com.jho5245.cucumbery.util.storage.data.Variable;
import de.tr7zw.changeme.nbtapi.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandItemTagTabCompleter implements TabCompleter
{
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
    String lastArg = args[length - 1];
    ItemStack item = player.getInventory().getItemInMainHand();
    if (!ItemStackUtil.itemExists(item))
    {
      return Collections.singletonList(Prefix.NO_HOLDING_ITEM.toString());
    }
    Material material = item.getType();
    final boolean isPotionType = material == Material.POTION || material == Material.SPLASH_POTION || material == Material.LINGERING_POTION || material == Material.TIPPED_ARROW || ItemStackUtil.isEdible(material);
    switch (length)
    {
      case 1:
      {
        List<String> list = Method.tabCompleterList(args, "<??????>", "restriction", "customlore", "extratag", Constant.TAB_COMPLETER_QUOTE_ESCAPE + "customdurability" + (!Constant.DURABLE_ITEMS.contains(material) ? "(???????????? ?????? ????????? ??????)" : ""),
                "customitemtype", "hideflag", "customrarity", "usage", "expiredate", Constant.TAB_COMPLETER_QUOTE_ESCAPE + "tnt" + (material != Material.TNT ? "(TNT ??????)" : ""), "abovecustomlore",
                "customitem", Constant.TAB_COMPLETER_QUOTE_ESCAPE + "food" + (ItemStackUtil.isEdible(material) ? "" : "(?????? ??? ?????? ????????? ??????)"), "id", "nbt", "customtag", Constant.TAB_COMPLETER_QUOTE_ESCAPE + "potion" +
                        (isPotionType ? "" : "(?????? ?????? ?????? ????????? ????????? ??????)"), "customdisplayname");
        if (args[0].equals("tnt") && material != Material.TNT)
        {
          return Collections.singletonList("?????? ????????? TNT?????? ????????? ??? ????????????");
        }
        if (args[0].equals("customdurability") && !Constant.DURABLE_ITEMS.contains(material))
        {
          return Collections.singletonList("?????? ????????? ???????????? ?????? ??????????????? ????????? ??? ????????????");
        }
        if (args[0].equals("food") && !ItemStackUtil.isEdible(material))
        {
          return Collections.singletonList("?????? ????????? ?????? ??? ?????? ??????????????? ????????? ??? ????????????");
        }
        if (args[0].equals("potion") && !isPotionType)
        {
          return Collections.singletonList("?????? ????????? ?????? ?????? ?????? ????????? ??????????????? ????????? ??? ????????????");
        }
        return list;
      }
      case 2:
        switch (args[0])
        {
          case "restriction":
            return Method.tabCompleterList(args, "<??????>", "add", "remove", "modify");
          case "customlore":
          case "abovecustomlore":
            return Method.tabCompleterList(args, "<??????>", "add", "remove", "set", "insert", "list");
          case "customdurability":
            if (!Constant.DURABLE_ITEMS.contains(material))
            {
              return Collections.singletonList("?????? ????????? ???????????? ?????? ??????????????? ????????? ??? ????????????");
            }
            return Method.tabCompleterList(args, "<??????>", "durability", "chance");
          case "hideflag":
          case "extratag":
            return Method.tabCompleterList(args, "<??????>", "add", "remove");
          case "customrarity":
            return Method.tabCompleterList(args, "<??????>", "base", "value", "set");
          case "usage":
            return Method.tabCompleterList(args, "<??????>", "disposable", "command", "equip", "cooldown", "permission");
          case "tnt":
            if (material != Material.TNT)
            {
              return Collections.singletonList("?????? ????????? TNT?????? ????????? ??? ????????????");
            }
            return Method.tabCompleterList(args, "<??????>", "unstable", "ignite", "fuse", "fire", "explode-power");
          case "customitem":
            return Method.tabCompleterList(args, "<??????>", "setid", "modify");
          case "food":
            if (!ItemStackUtil.isEdible(material))
            {
              return Collections.singletonList("?????? ????????? ?????? ??? ?????? ??????????????? ????????? ??? ????????????");
            }
            boolean hasEffects = ItemStackUtil.hasStatusEffect(material);
            return Method.tabCompleterList(args, "<??????>", "disable-status-effect" + (hasEffects ? "" : "(?????? ????????? ????????? ??? ??? ?????? ????????? ??????)"), "food-level", "saturation", "nourishment");
          case "nbt":
            return Method.tabCompleterList(args, "<??????>", "set", "remove", "merge");
          case "potion":
            if (!isPotionType)
            {
              return Collections.singletonList("?????? ????????? ?????? ?????? ?????? ????????? ??????????????? ????????? ??? ????????????");
            }
            return Method.tabCompleterList(args, "<??????>", "list", "add", "remove", "set");
          case "customdisplayname":
            return Method.tabCompleterList(args, "<??????>", "prefix", "suffix", "name", "remove");
        }
        break;
      case 3:
        switch (args[0])
        {
          case "restriction":
            switch (args[1])
            {
              case "add":
              case "remove":
              case "modify":
                return Method.tabCompleterList(args, RestrictionType.values(), "<?????? ?????? ??????>");
            }
            break;
          case "extratag":
            switch (args[1])
            {
              case "add", "remove" -> {
                boolean shulkerBoxExclusive = !Constant.SHULKER_BOXES.contains(material);
                boolean enderPearlExclusive = material != Material.ENDER_PEARL;
                List<String> list = new ArrayList<>(Method.tabCompleterList(args, Constant.ExtraTag.values(), "<??????>"));
                for (int i = 0; i < list.size(); i++)
                {
                  if (shulkerBoxExclusive && list.get(i).equalsIgnoreCase(Constant.ExtraTag.PORTABLE_SHULKER_BOX.toString()))
                  {
                    list.set(i, Constant.ExtraTag.PORTABLE_SHULKER_BOX.toString().toLowerCase() + "(?????? ?????? ??????)");
                    break;
                  }
                  if (enderPearlExclusive && list.get(i).equalsIgnoreCase(Constant.ExtraTag.NO_COOLDOWN_ENDER_PEARL.toString()))
                  {
                    list.set(i, Constant.ExtraTag.NO_COOLDOWN_ENDER_PEARL.toString().toLowerCase() + "(?????? ?????? ??????)");
                    break;
                  }
                }
                if (shulkerBoxExclusive && args[2].equalsIgnoreCase(Constant.ExtraTag.PORTABLE_SHULKER_BOX.toString()))
                {
                  return Collections.singletonList("?????? ????????? ?????? ???????????? ????????? ??? ????????????");
                }
                if (enderPearlExclusive && args[2].equalsIgnoreCase(Constant.ExtraTag.NO_COOLDOWN_ENDER_PEARL.toString()))
                {
                  return Collections.singletonList("?????? ????????? ?????? ???????????? ????????? ??? ????????????");
                }
                return list;
              }
            }
          case "hideflag":
            switch (args[1])
            {
              case "add":
              case "remove":
                return Method.tabCompleterList(args, Method.addAll(Constant.CucumberyHideFlag.values()
                        , "--all", "--??????", "--ables", "--?????????", "--dura", "--?????????"), "<??????>");
            }
            break;
          case "customrarity":
            switch (args[1])
            {
              case "base":
              case "set":
                return Method.tabCompleterList(args, Method.addAll(ItemCategory.Rarity.values(), "--remove"), "<????????? ??????>");
              case "value":
                return Method.tabCompleterIntegerRadius(args, -2_000_000_000, 2_000_000_000, "<??????>");
            }
            break;
          case "usage":
            switch (args[1])
            {
              case "command":
              case "cooldown":
              case "permission":
                return Method.tabCompleterList(args, Constant.ItemUsageType.values(), "<?????? ??????>");
              case "disposable":
                List<String> usageTypes = Method.enumToList(Constant.ItemUsageType.values());
                if (args[2].contains("attack") && usageTypes.contains(args[2]))
                {
                  return Collections.singletonList(ItemUsageType.valueOf(args[2].toUpperCase()).getDisplay() + " ???????????? ?????? ????????? ????????? ??? ????????????");
                }
                usageTypes.removeIf(s -> s.contains("attack"));
                return Method.tabCompleterList(args, usageTypes, "<?????? ??????>");
              case "equip":
                return Method.tabCompleterList(args, "<??????>", "--remove", "helmet", "chestplate", "leggings", "boots");
            }
            break;
          case "tnt":
            switch (args[1])
            {
              case "unstable":
                return Method.tabCompleterBoolean(args, "<???????????? ?????? ??????>");
              case "ignite":
                return Method.tabCompleterBoolean(args, "<?????? ?????? ?????? ??????>");
              case "fuse":
                return Method.tabCompleterIntegerRadius(args, 0, Integer.MAX_VALUE, "<?????? ??????(???)>", "-1");
              case "fire":
                return Method.tabCompleterBoolean(args, "<?????? ??? ??? ?????? ??????>");
              case "explode-power":
                return Method.tabCompleterDoubleRadius(args, 0, 500, "<?????? ??????>", "-1", "-1(?????????)");
            }
            break;
          case "customitem":
            switch (args[1])
            {
              case "setid":
                List<String> list = Method.tabCompleterList(args, "<?????????>", "railgun", "fishingrod" + (material != Material.FISHING_ROD ? "(????????? ??????)" : ""), "--remove");
                if (args[2].equals("fishingrod") && material != Material.FISHING_ROD)
                {
                  return Collections.singletonList("?????? ????????? ??????????????? ????????? ??? ????????????");
                }
                return list;
              case "modify":
                return CommandTabUtil.customItemTabCompleter(player, args);
            }
            break;
          case "customdurability":
            if (!Constant.DURABLE_ITEMS.contains(material))
            {
              return Collections.singletonList("?????? ????????? ???????????? ?????? ??????????????? ????????? ??? ????????????");
            }
            switch (args[1])
            {
              case "chance":
                return Method.tabCompleterDoubleRadius(args, 0, 100, "<????????? ?????? ?????? ??????(%)>");
              case "durability":
                return Method.tabCompleterLongRadius(args, 0, Long.MAX_VALUE, "(<?????????>|<?????? ?????????> <?????? ?????????>)", "0", "0 (??????)");
            }
            break;
          case "food":
            if (!ItemStackUtil.isEdible(material))
            {
              return Collections.singletonList("?????? ????????? ?????? ??? ?????? ??????????????? ????????? ??? ????????????");
            }
            switch (args[1])
            {
              case "disable-status-effect":
                boolean hasEffects = ItemStackUtil.hasStatusEffect(material);
                if (!hasEffects)
                {
                  return Collections.singletonList("?????? ????????? ?????? ????????? ????????? ??? ??? ?????? ??????????????? ????????? ??? ????????????");
                }
                return Method.tabCompleterBoolean(args, "<?????? ??? ?????? ?????? ????????? ??????>");
              case "food-level":
                return Method.tabCompleterIntegerRadius(args, -20, 20, "<?????? ?????????>", "--remove");
              case "saturation":
                return Method.tabCompleterDoubleRadius(args, -20, 20, "<?????????>", "--remove");
            }
            break;
          case "nbt":
            switch (args[1])
            {
              case "set":
                return Method.tabCompleterList(args, "<?????????>",
                        "boolean", "byte", "byte-array", "short", "int", "int-list", "int-array", "long", "long-list",
                        "float", "float-list", "double", "double-list", "uuid", "string", "string-list",
                        "compound", "compound-list");
              case "remove":
                NBTItem nbtItem = new NBTItem(item);
                List<String> returnValue = new ArrayList<>(nbtItem.getKeys());
                for (String key1 : nbtItem.getKeys())
                {
                  NBTCompound compound2 = nbtItem.getCompound(key1);
                  if (compound2 != null)
                  {
                    for (String key2 : compound2.getKeys())
                    {
                      returnValue.add(key1 + "." + key2);
                      NBTCompound compound3 = compound2.getCompound(key2);
                      if (compound3 != null)
                      {
                        for (String key3 : compound3.getKeys())
                        {
                          returnValue.add(key1 + "." + key2 + "." + key3);
                          NBTCompound compound4 = compound3.getCompound(key3);
                          if (compound4 != null)
                          {
                            for (String key4 : compound4.getKeys())
                            {
                              returnValue.add(key1 + "." + key2 + "." + key3 + "." + key4);
                              NBTCompound compound5 = compound4.getCompound(key4);
                              if (compound5 != null)
                              {
                                for (String key5 : compound5.getKeys())
                                {
                                  returnValue.add(key1 + "." + key2 + "." + key3 + "." + key4 + "." + key5);
                                  NBTCompound compound6 = compound5.getCompound(key5);
                                  if (compound6 != null)
                                  {
                                    for (String key6 : compound6.getKeys())
                                    {
                                      returnValue.add(key1 + "." + key2 + "." + key3 + "." + key4 + "." + key5 + "." + key6);
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
                return Method.tabCompleterList(args, returnValue, "<??????>");
            }
            break;
          case "potion":
            if (!isPotionType)
            {
              return Collections.singletonList("?????? ????????? ?????? ?????? ?????? ????????? ??????????????? ????????? ??? ????????????");
            }
            switch (args[1])
            {
              case "remove" -> {
                NBTCompoundList nbtCompoundList = NBTAPI.getCompoundList(NBTAPI.getMainCompound(item), CucumberyTag.CUSTOM_EFFECTS);
                if (nbtCompoundList == null || nbtCompoundList.isEmpty())
                {
                  return Collections.singletonList(MessageUtil.stripColor(ComponentUtil.serialize(ItemNameUtil.itemName(item))) + "?????? ????????? ????????????");
                }
                return Method.tabCompleterIntegerRadius(args, 1, nbtCompoundList.size(), "[???]");
              }
              case "set" -> {
                NBTCompoundList nbtCompoundList = NBTAPI.getCompoundList(NBTAPI.getMainCompound(item), CucumberyTag.CUSTOM_EFFECTS);
                if (nbtCompoundList == null || nbtCompoundList.isEmpty())
                {
                  return Collections.singletonList(MessageUtil.stripColor(ComponentUtil.serialize(ItemNameUtil.itemName(item))) + "?????? ????????? ????????????");
                }
                return Method.tabCompleterIntegerRadius(args, 1, nbtCompoundList.size(), "<???>");
              }
              case "add" -> {
                return Method.tabCompleterList(args, CustomEffectType.getEffectTypeMap(), "<??????>");
              }
            }
            break;
          case "customdisplayname":
            switch (args[1])
            {
              case "prefix":
              case "suffix":
                return Method.tabCompleterList(args, "<??????>", "add", "remove", "list", "set");
              case "name":
                return Method.tabCompleterList(args, "<??????|--remove>", true, "--remove");
            }
            break;
        }
        break;
      case 4:
        switch (args[0])
        {
          case "restriction":
            switch (args[1])
            {
              case "add":
                return Method.tabCompleterBoolean(args, "[?????? ?????? ??????]");
              case "modify":
                return Method.tabCompleterList(args, "<??????>", "hide", "permission");
            }
            break;
          case "customitem":
            if ("modify".equals(args[1]))
            {
              return CommandTabUtil.customItemTabCompleter(player, args);
            }
            break;
          case "customenchant":
            switch (args[1])
            {
              case "add":
              case "remove":
                return Method.tabCompleterIntegerRadius(args, 1, Integer.MAX_VALUE, "[??????]");
            }
            break;
          case "customdurability":
            if (!Constant.DURABLE_ITEMS.contains(material))
            {
              return Collections.singletonList("?????? ????????? ???????????? ?????? ??????????????? ????????? ??? ????????????");
            }
            if ("durability".equals(args[1]))
            {
              return Method.tabCompleterLongRadius(args, 1, Long.MAX_VALUE, "[?????? ?????????]");
            }
            break;
          case "usage":
            switch (args[1])
            {
              case "command" -> {
                try
                {
                  ItemUsageType.valueOf(args[2].toUpperCase());
                }
                catch (Exception e)
                {
                  return Collections.singletonList(args[2] + MessageUtil.getFinalConsonant(args[2], MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ?????? ???????????????");
                }
                return Method.tabCompleterList(args, "<??????>", "add", "remove", "list", "set", "insert");
              }
              case "cooldown" -> {
                try
                {
                  ItemUsageType.valueOf(args[2].toUpperCase());
                }
                catch (Exception e)
                {
                  return Collections.singletonList(args[2] + MessageUtil.getFinalConsonant(args[2], MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ?????? ???????????????");
                }
                return Method.tabCompleterList(args, "<??????>", "tag", "time");
              }
              case "disposable" -> {
                String display;
                try
                {
                  ItemUsageType itemUsageType = ItemUsageType.valueOf(args[2].toUpperCase());
                  display = itemUsageType.getDisplay();
                }
                catch (Exception e)
                {
                  return Collections.singletonList(args[2] + MessageUtil.getFinalConsonant(args[2], MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ?????? ???????????????");
                }
                if (args[2].contains("attack"))
                {
                  return Collections.singletonList(display + " ???????????? ?????? ????????? ????????? ??? ????????????");
                }
                return Method.tabCompleterDoubleRadius(args, 0, 100, "<" + display + " ??? ?????? ??????(%)>", "-1");
              }
              case "permission" -> {
                try
                {
                  ItemUsageType.valueOf(args[2].toUpperCase());
                }
                catch (Exception e)
                {
                  return Collections.singletonList(args[2] + MessageUtil.getFinalConsonant(args[2], MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ?????? ???????????????");
                }
                return Method.tabCompleterList(args, "<????????? ??????>", true, "--remove", "<????????? ??????>");
              }
            }
            break;
          case "nbt":
            if (args[1].equals("set"))
            {
              switch (args[2])
              {
                case "boolean":
                case "byte":
                case "byte-array":
                case "short":
                case "int":
                case "int-array":
                case "int-list":
                case "long":
                case "long-list":
                case "float":
                case "float-list":
                case "double":
                case "double-list":
                case "uuid":
                case "string":
                case "string-list":
                case "compound":
                case "compound-list":
                  NBTItem nbtItem = new NBTItem(item);
                  Set<String> keys = nbtItem.getKeys();
                  if (keys.contains(args[3]))
                  {
                    return Method.tabCompleterList(args, Method.addAll(keys, "<????????? ???>"), "<????????? ???>", true);
                  }
                  return Method.tabCompleterList(args, Method.addAll(keys, "<???|????????? ???>"), "<???>", true);
                default:
                  return Collections.singletonList(args[2] + MessageUtil.getFinalConsonant(args[2], MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ??????????????????");
              }
            }
            break;
          case "potion":
            if (!isPotionType)
            {
              return Collections.singletonList("?????? ????????? ?????? ?????? ?????? ????????? ??????????????? ????????? ??? ????????????");
            }
            switch (args[1])
            {
              case "set" -> {
                return Method.tabCompleterList(args, CustomEffectType.getEffectTypeMap(), "<??????>");
              }
              case "add" -> {
                return Method.tabCompleterDoubleRadius(args, 0.05, Integer.MAX_VALUE / 20d, "[?????? ??????(???)]", "infinite", "default");
              }
            }
            break;
          case "customdisplayname":
            switch (args[1])
            {
              case "prefix", "suffix" -> {
                NBTCompound displayCompound = NBTAPI.getCompound(NBTAPI.getMainCompound(item), CucumberyTag.ITEMSTACK_DISPLAY_KEY);
                boolean isPrefix = args[1].equals("prefix");
                switch (args[2])
                {
                  case "add" -> {
                    return Method.tabCompleterList(args, isPrefix ? "<?????????>" : "<?????????>", true);
                  }
                  case "remove" -> {
                    if (isPrefix)
                    {
                      NBTCompoundList prefixList = NBTAPI.getCompoundList(displayCompound, CucumberyTag.ITEMSTACK_DISPLAY_PREFIX);
                      if (prefixList == null || prefixList.isEmpty())
                      {
                        return Collections.singletonList(MessageUtil.stripColor(ComponentUtil.serialize(ComponentUtil.translate("%s?????? ???????????? ????????????", item))));
                      }
                      return Method.tabCompleterIntegerRadius(args, 1, prefixList.size(), "[???(1~" + prefixList.size() + ")|--all]", "--all");
                    }
                    else
                    {
                      NBTCompoundList suffixList = NBTAPI.getCompoundList(displayCompound, CucumberyTag.ITEMSTACK_DISPLAY_SUFFIX);
                      if (suffixList == null || suffixList.isEmpty())
                      {
                        return Collections.singletonList(MessageUtil.stripColor(ComponentUtil.serialize(ComponentUtil.translate("%s?????? ???????????? ????????????", item))));
                      }
                      return Method.tabCompleterIntegerRadius(args, 1, suffixList.size(), "[???(1~" + suffixList.size() + ")|--all]", "--all");
                    }
                  }
                  case "set" -> {
                    return Method.tabCompleterIntegerRadius(args, 1, Integer.MAX_VALUE, "<???>");
                  }
                }
              }
            }
            break;
        }
        break;
      case 5:
        switch (args[0])
        {
          case "restriction":
            switch (args[1])
            {
              case "add":
                return Method.tabCompleterList(args, "[?????? ????????? ??????]", true);
              case "modify":
                if ("hide".equals(args[3]))
                {
                  return Method.tabCompleterBoolean(args, "<?????? ?????? ??????>");
                }
                else if ("permission".equals(args[3]))
                {
                  return Method.tabCompleterList(args, "<?????? ????????? ??????>", true, "--remove", "<?????? ????????? ??????>");
                }
            }
            break;
          case "usage":
            if (args[1].equals("cooldown"))
            {
              try
              {
                ItemUsageType.valueOf(args[2].toUpperCase());
              }
              catch (Exception e)
              {
                return Collections.singletonList(args[2] + MessageUtil.getFinalConsonant(args[2], MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ?????? ???????????????");
              }
              switch (args[3])
              {
                case "tag":
                  return Method.tabCompleterList(args, "<??????>", true, "<??????>", "default-" + args[2].toLowerCase());
                case "time":
                  return Method.tabCompleterDoubleRadius(args, 0, Double.MAX_VALUE, "<????????? ?????? ??????(???)>", "0");
              }
            }
            break;
          case "nbt":
            if (args[1].equals("set"))
            {
              String key = args[3];
              String input = args[4];
              NBTItem nbtItem = new NBTItem(item);
              switch (args[2])
              {
                case "boolean":
                  boolean exists = nbtItem.hasKey(key) && nbtItem.getType(key) == NBTType.NBTTagByte;
                  byte b = nbtItem.getByte(key);
                  exists = exists && (b == 0 || b == 1);
                  boolean bool = nbtItem.getBoolean(key);
                  return Method.tabCompleterBoolean(args, "<???>", exists ? bool + "(?????????)" : null);
                case "byte":
                  exists = nbtItem.hasKey(key) && nbtItem.getType(key) == NBTType.NBTTagByte;
                  b = nbtItem.getByte(key);
                  return Method.tabCompleterIntegerRadius(args, Byte.MIN_VALUE, Byte.MAX_VALUE, "<???>", exists ? b + "(?????????)" : "");
                case "byte-array":
                  if (input.startsWith(","))
                  {
                    return Collections.singletonList("????????? ??????????????? (" + input + ")");
                  }
                  String[] split = input.split(",");
                  if (!input.equals(""))
                  {
                    for (String s : split)
                    {
                      if (!MessageUtil.isInteger(sender, s, false))
                      {
                        return Collections.singletonList("????????? ??????????????? (" + s + ")");
                      }
                      int j = Integer.parseInt(s);
                      if (j > Byte.MAX_VALUE)
                      {
                        String valueString = Constant.Sosu15.format(j);
                        valueString += MessageUtil.getFinalConsonant(valueString, MessageUtil.ConsonantType.??????);
                        return Collections.singletonList("????????? " + Constant.Sosu15.format(Byte.MAX_VALUE) + " ???????????? ?????????, " + valueString + " ????????????");
                      }
                      else if (j < Byte.MIN_VALUE)
                      {
                        String valueString = Constant.Sosu15.format(j);
                        valueString += MessageUtil.getFinalConsonant(valueString, MessageUtil.ConsonantType.??????);
                        return Collections.singletonList("????????? " + Constant.Sosu15.format(Byte.MIN_VALUE) + " ??????????????? ?????????, " + valueString + " ????????????");
                      }
                    }
                  }
                  exists = nbtItem.hasKey(key) && nbtItem.getType(key) == NBTType.NBTTagByteArray;
                  if (exists)
                  {
                    byte[] originValue = nbtItem.getByteArray(key);
                    StringBuilder originValueStringBuilder = new StringBuilder();
                    for (byte originValue2 : originValue)
                    {
                      originValueStringBuilder.append(originValue2).append(",");
                    }
                    String originValueString = originValueStringBuilder.toString();
                    originValueString = originValueString.substring(0, originValueString.length() - 1);
                    return Method.tabCompleterList(args, "<???>", true, "<???>", "?????? : 1,2,3", originValueString, originValueString + "(?????????)");
                  }
                  return Method.tabCompleterList(args, "<???>", true, "<???>", "?????? : 1,2,3");
                case "short":
                  exists = nbtItem.hasKey(key) && nbtItem.getType(key) == NBTType.NBTTagShort;
                  short s = nbtItem.getShort(key);
                  return Method.tabCompleterIntegerRadius(args, Short.MIN_VALUE, Short.MAX_VALUE, "<???>", exists ? s + "(?????????)" : "");
                case "int":
                  exists = nbtItem.hasKey(key) && nbtItem.getType(key) == NBTType.NBTTagInt;
                  int i = nbtItem.getInteger(key);
                  return Method.tabCompleterIntegerRadius(args, Integer.MIN_VALUE, Integer.MAX_VALUE, "<???>", exists ? i + "(?????????)" : "");
                case "int-array":
                  if (input.startsWith(","))
                  {
                    return Collections.singletonList("????????? ??????????????? (" + input + ")");
                  }
                  split = input.split(",");
                  if (!input.equals(""))
                  {
                    for (String s2 : split)
                    {
                      if (!MessageUtil.isInteger(sender, s2, false))
                      {
                        return Collections.singletonList("????????? ??????????????? (" + s2 + ")");
                      }
                    }
                  }
                  exists = nbtItem.hasKey(key) && nbtItem.getType(key) == NBTType.NBTTagIntArray;
                  if (exists)
                  {
                    int[] originValue = nbtItem.getIntArray(key);
                    StringBuilder originValueStringBuilder = new StringBuilder();
                    for (int originValue2 : originValue)
                    {
                      originValueStringBuilder.append(originValue2).append(",");
                    }
                    String originValueString = originValueStringBuilder.toString();
                    originValueString = originValueString.substring(0, originValueString.length() - 1);
                    return Method.tabCompleterList(args, "<???>", true, "<???>", "?????? : 1,2,3", originValueString, originValueString + "(?????????)");
                  }
                  return Method.tabCompleterList(args, "<???>", true, "<???>", "?????? : 1,2,3");
                case "int-list":
                  if (input.startsWith(","))
                  {
                    return Collections.singletonList("????????? ??????????????? (" + input + ")");
                  }
                  split = input.split(",");
                  if (!input.equals(""))
                  {
                    for (String s2 : split)
                    {
                      if (!MessageUtil.isInteger(sender, s2, false))
                      {
                        return Collections.singletonList("????????? ??????????????? (" + s2 + ")");
                      }
                    }
                  }
                  NBTList<Integer> nbtIntegerList = nbtItem.getIntegerList(key);
                  if (nbtIntegerList != null && nbtIntegerList.size() > 0)
                  {
                    StringBuilder originValueStringBuilder = new StringBuilder();
                    for (int originValue2 : nbtIntegerList)
                    {
                      originValueStringBuilder.append(originValue2).append(",");
                    }
                    String originValueString = originValueStringBuilder.toString();
                    originValueString = originValueString.substring(0, originValueString.length() - 1);
                    return Method.tabCompleterList(args, "<???>", true, "<???>", "?????? : 1,2,3", originValueString, originValueString + "(?????????)");
                  }
                  return Method.tabCompleterList(args, "<???>", true, "<???>", "?????? : 1,2,3");
                case "long":
                  if (input.startsWith(","))
                  {
                    return Collections.singletonList("????????? ??????????????? (" + input + ")");
                  }
                  split = input.split(",");
                  if (!input.equals(""))
                  {
                    for (String s2 : split)
                    {
                      if (!MessageUtil.isLong(sender, s2, false))
                      {
                        return Collections.singletonList("????????? ??????????????? (" + s2 + ")");
                      }
                    }
                  }
                  exists = nbtItem.hasKey(key) && nbtItem.getType(key) == NBTType.NBTTagLong;
                  long l = nbtItem.getLong(key);
                  return Method.tabCompleterLongRadius(args, Long.MIN_VALUE, Long.MAX_VALUE, "<???>", exists ? l + "(?????????)" : "");
                case "long-list":
                  if (input.startsWith(","))
                  {
                    return Collections.singletonList("????????? ??????????????? (" + input + ")");
                  }
                  split = input.split(",");
                  if (!input.equals(""))
                  {
                    for (String s2 : split)
                    {
                      if (!MessageUtil.isLong(sender, s2, false))
                      {
                        return Collections.singletonList("????????? ??????????????? (" + s2 + ")");
                      }
                    }
                  }
                  NBTList<Long> nbtLongList = nbtItem.getLongList(key);
                  if (nbtLongList != null && nbtLongList.size() > 0)
                  {
                    StringBuilder originValueStringBuilder = new StringBuilder();
                    for (long originValue2 : nbtLongList)
                    {
                      originValueStringBuilder.append(originValue2).append(",");
                    }
                    String originValueString = originValueStringBuilder.toString();
                    originValueString = originValueString.substring(0, originValueString.length() - 1);
                    return Method.tabCompleterList(args, "<???>", true, "<???>", "?????? : 1,2,3", originValueString, originValueString + "(?????????)");
                  }
                  return Method.tabCompleterList(args, "<???>", true, "<???>", "?????? : 1,2,3");
                case "float":
                  exists = nbtItem.hasKey(key) && nbtItem.getType(key) == NBTType.NBTTagFloat;
                  float f = nbtItem.getFloat(key);
                  return Method.tabCompleterDoubleRadius(args, -Float.MAX_VALUE, Float.MAX_VALUE, "<???>", exists ? f + "(?????????)" : "");
                case "float-list":
                  if (input.startsWith(","))
                  {
                    return Collections.singletonList("????????? ??????????????? (" + input + ")");
                  }
                  split = input.split(",");
                  if (!input.equals(""))
                  {
                    for (String s2 : split)
                    {
                      if (!MessageUtil.isDouble(sender, s2, false))
                      {
                        return Collections.singletonList("????????? ??????????????? (" + s2 + ")");
                      }
                      double j = Double.parseDouble(s2);
                      if (j > Float.MAX_VALUE)
                      {
                        String valueString = Constant.Sosu15.format(j);
                        valueString += MessageUtil.getFinalConsonant(valueString, MessageUtil.ConsonantType.??????);
                        return Collections.singletonList("????????? " + Constant.Sosu15.format(Byte.MAX_VALUE) + " ???????????? ?????????, " + valueString + " ????????????");
                      }
                      else if (j < -Float.MAX_VALUE)
                      {
                        String valueString = Constant.Sosu15.format(j);
                        valueString += MessageUtil.getFinalConsonant(valueString, MessageUtil.ConsonantType.??????);
                        return Collections.singletonList("????????? " + Constant.Sosu15.format(Byte.MIN_VALUE) + " ??????????????? ?????????, " + valueString + " ????????????");
                      }
                    }
                  }
                  NBTList<Float> nbtFloatList = nbtItem.getFloatList(key);
                  if (nbtFloatList != null && nbtFloatList.size() > 0)
                  {
                    StringBuilder originValueStringBuilder = new StringBuilder();
                    for (float originValue2 : nbtFloatList)
                    {
                      originValueStringBuilder.append(originValue2).append(",");
                    }
                    String originValueString = originValueStringBuilder.toString();
                    originValueString = originValueString.substring(0, originValueString.length() - 1);
                    return Method.tabCompleterList(args, "<???>", true, "<???>", "?????? : 1.5,2.5,3.5", originValueString, originValueString + "(?????????)");
                  }
                  return Method.tabCompleterList(args, "<???>", true, "<???>", "?????? : 1.5,2.5,3.5");
                case "double":
                  exists = nbtItem.hasKey(key) && nbtItem.getType(key) == NBTType.NBTTagDouble;
                  double d = nbtItem.getDouble(key);
                  return Method.tabCompleterDoubleRadius(args, -Double.MAX_VALUE, Double.MAX_VALUE, "<???>", exists ? d + "(?????????)" : "");
                case "double-list":
                  if (input.startsWith(","))
                  {
                    return Collections.singletonList("????????? ??????????????? (" + input + ")");
                  }
                  split = input.split(",");
                  if (!input.equals(""))
                  {
                    for (String s2 : split)
                    {
                      if (!MessageUtil.isDouble(sender, s2, false))
                      {
                        return Collections.singletonList("????????? ??????????????? (" + s2 + ")");
                      }
                    }
                  }
                  NBTList<Double> nbtDoubleList = nbtItem.getDoubleList(key);
                  if (nbtDoubleList != null && nbtDoubleList.size() > 0)
                  {
                    StringBuilder originValueStringBuilder = new StringBuilder();
                    for (double originValue2 : nbtDoubleList)
                    {
                      originValueStringBuilder.append(originValue2).append(",");
                    }
                    String originValueString = originValueStringBuilder.toString();
                    originValueString = originValueString.substring(0, originValueString.length() - 1);
                    return Method.tabCompleterList(args, "<???>", true, "<???>", "?????? : 1.5,2.5,3.5", originValueString, originValueString + "(?????????)");
                  }
                  return Method.tabCompleterList(args, "<???>", true, "<???>", "?????? : 1.5,2.5,3.5");
                case "uuid":
                  exists = nbtItem.hasKey(key) && nbtItem.getType(key) == NBTType.NBTTagIntArray;
                  try
                  {
                    UUID uuid = nbtItem.getUUID(key);
                    return Method.tabCompleterList(args, "<???>", true, exists ?
                            new String[]{uuid.toString(), uuid.toString() + "(?????????)", "<???>", player.getUniqueId().toString(), player.getUniqueId().toString() + "(???????????? UUID)"}
                            : new String[]{"<???>", player.getUniqueId().toString() + "(???????????? UUID)"});
                  }
                  catch (Exception e)
                  {
                    return Method.tabCompleterList(args, "<???>", true, "<???>", player.getUniqueId().toString() + "(???????????? UUID)");
                  }
                case "string":
                  exists = nbtItem.hasKey(key) && nbtItem.getType(key) == NBTType.NBTTagString;
                  String str = nbtItem.getString(key);
                  return Method.tabCompleterList(args, "<???>", true, exists ? new String[]{str.replace("??", "&"), "<???>"} : new String[]{"<???>"});
                case "string-list":
                  NBTList<String> nbtStringList = nbtItem.getStringList(key);
                  if (nbtStringList != null && nbtStringList.size() > 0)
                  {
                    StringBuilder originValueStringBuilder = new StringBuilder();
                    for (String originValue2 : nbtStringList)
                    {
                      originValueStringBuilder.append(originValue2).append(";;");
                    }
                    String originValueString = originValueStringBuilder.toString();
                    originValueString = originValueString.substring(0, originValueString.length() - 2);
                    return Method.tabCompleterList(args, "<???>", true, "<???>", "?????? : ??????1;;??????2;;??????3", originValueString, originValueString + "(?????????)");
                  }
                  return Method.tabCompleterList(args, "<???>", true, "<???>", "?????? : ??????1;;??????2;;??????3");
                case "compound":
                case "compound-list":
                  break;
                default:
                  return Collections.singletonList(args[2] + MessageUtil.getFinalConsonant(args[2], MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ??????????????????");
              }
            }
            break;
          case "potion":
            if (!isPotionType)
            {
              return Collections.singletonList("?????? ????????? ?????? ?????? ?????? ????????? ??????????????? ????????? ??? ????????????");
            }
            switch (args[1])
            {
              case "set" -> {
                return Method.tabCompleterDoubleRadius(args, 0.05, Integer.MAX_VALUE / 20d, "[?????? ??????(???)]", "infinite", "default");
              }
              case "add" -> {
                String effect = args[2];
                CustomEffectType customEffectType;
                try
                {
                  customEffectType = CustomEffectType.valueOf(effect);
                }
                catch (Exception e)
                {
                  return Collections.singletonList(effect + MessageUtil.getFinalConsonant(effect, MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ???????????????");
                }
                return Method.tabCompleterIntegerRadius(args, 0, customEffectType.getMaxAmplifier(), "[?????? ??????]", "max");
              }
            }
            break;

          case "customdisplayname":
            switch (args[1])
            {
              case "prefix", "suffix" -> {
                boolean isPrefix = args[1].equals("prefix");
                switch (args[2])
                {
                  case "add" -> {
                    String nbt = args[4];
                    if (!nbt.equals(""))
                    try
                    {
                      new NBTContainer(nbt);
                    }
                    catch (Exception exception)
                    {
                      return Collections.singletonList("????????? NBT?????????:" + nbt);
                    }
                    return Method.tabCompleterList(args, "[nbt]", true);
                  }
                  case "set" -> {
                    return Method.tabCompleterList(args, isPrefix ? "<?????????>" : "<?????????>", true);
                  }
                }
              }
            }
            break;
        }
        break;
      case 6:
      {
        if (args[0].equals("potion"))
        {
          if (!isPotionType)
          {
            return Collections.singletonList("?????? ????????? ?????? ?????? ?????? ????????? ??????????????? ????????? ??? ????????????");
          }
          switch (args[1])
          {
            case "set" -> {
              String effect = args[3];
              CustomEffectType customEffectType;
              try
              {
                customEffectType = CustomEffectType.valueOf(effect.toUpperCase());
              }
              catch (Exception e)
              {
                return Collections.singletonList(effect + MessageUtil.getFinalConsonant(effect, MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ???????????????");
              }
              return Method.tabCompleterIntegerRadius(args, 0, customEffectType.getMaxAmplifier(), "[?????? ??????]", "max");
            }
            case "add" -> {
              String effect = args[2];
              CustomEffectType customEffectType;
              try
              {
                customEffectType = CustomEffectType.valueOf(effect.toUpperCase());
              }
              catch (Exception e)
              {
                return Collections.singletonList(effect + MessageUtil.getFinalConsonant(effect, MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ???????????????");
              }
              List<String> list = new ArrayList<>(Method.enumToList(DisplayType.values()));
              list.add(customEffectType.getDefaultDisplayType().toString().toLowerCase() + "(?????????)");
              return Method.tabCompleterList(args, list, "[?????? ??????]");
            }
          }
        }
        if (args[0].equals("customdisplayname"))
        {
          switch (args[1])
          {
            case "prefix", "suffix" -> {
              if ("set".equals(args[2]))
              {
                String nbt = args[5];
                if (!nbt.equals(""))
                  try
                  {
                    new NBTContainer(nbt);
                  }
                  catch (Exception exception)
                  {
                    return Collections.singletonList("????????? NBT?????????:" + nbt);
                  }
                return Method.tabCompleterList(args, "[nbt]", true);
              }
            }
          }
        }
        break;
      }
      case 7:
      {
        if (args[0].equals("potion"))
        {
          if (!isPotionType)
          {
            return Collections.singletonList("?????? ????????? ?????? ?????? ?????? ????????? ??????????????? ????????? ??? ????????????");
          }
          if (args[1].equals("set"))
          {
            String effect = args[3];
            CustomEffectType customEffectType;
            try
            {
              customEffectType = CustomEffectType.valueOf(effect.toUpperCase());
            }
            catch (Exception e)
            {
              return Collections.singletonList(effect + MessageUtil.getFinalConsonant(effect, MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ???????????????");
            }
            List<String> list = new ArrayList<>(Method.enumToList(DisplayType.values()));
            list.add(customEffectType.getDefaultDisplayType().toString().toLowerCase() + "(?????????)");
            return Method.tabCompleterList(args, list, "[?????? ??????]");
          }
        }
      }
    }

    switch (args[0])
    {
      case "customitemtype":
        if (length == 2)
        {
          return Method.tabCompleterList(args, "<????????? ??????>", true, "<????????? ??????>", "--remove");
        }
        return Method.tabCompleterList(args, "[????????? ??????]", true);
      case "customlore":
        switch (args[1])
        {
          case "add" -> {
            if (length == 3)
            {
              return Method.tabCompleterList(args, "<????????? ??????>", true, "<????????? ??????>", "--empty");
            }
            return Method.tabCompleterList(args, "[????????? ??????]", true);
          }
          case "set" -> {
            if (length == 3)
            {
              return Method.tabCompleterIntegerRadius(args, 1, Integer.MAX_VALUE, "<???>");
            }
            NBTList<String> customLore = NBTAPI.getStringList(NBTAPI.getMainCompound(item), CucumberyTag.CUSTOM_LORE_KEY);
            try
            {
              int input = Integer.parseInt(args[2]);
              String lore = customLore.get(input - 1);
              if (args[3].equals("") && customLore.size() >= input && input > 0)
              {
                return Method.tabCompleterList(args, "<????????? ??????>", true, "<????????? ??????>", "--empty", lore.replace("??", "&"));
              }
            }
            catch (Exception ignored)
            {

            }
            if (length == 4)
            {
              return Method.tabCompleterList(args, "<????????? ??????>", true, "<????????? ??????>", "--empty");
            }
            return Method.tabCompleterList(args, "[????????? ??????]", true);
          }
          case "remove" -> {
            {
              if (length == 3)
              {
                NBTList<String> customLore = NBTAPI.getStringList(NBTAPI.getMainCompound(item), CucumberyTag.CUSTOM_LORE_KEY);
                if (customLore == null || customLore.size() == 0)
                {
                  return Collections.singletonList("??? ?????? ????????? ??? ?????? ????????? ????????? ????????????");
                }
                return Method.tabCompleterIntegerRadius(args, 1, customLore.size(), "[???]", "--all");
              }
            }
          }
          case "insert" -> {
            NBTList<String> customLore = NBTAPI.getStringList(NBTAPI.getMainCompound(item), CucumberyTag.CUSTOM_LORE_KEY);
            if (customLore == null || customLore.size() == 0)
            {
              return Collections.singletonList("????????? ????????? ????????? ??? ????????????");
            }
            if (length == 3)
            {
              return Method.tabCompleterIntegerRadius(args, 1, customLore.size(), "<???>");
            }
            if (length == 4)
            {
              return Method.tabCompleterList(args, "<????????? ??????>", true, "<????????? ??????>", "--empty");
            }
            return Method.tabCompleterList(args, "[????????? ??????]", true);
          }
        }
        break;
      case "abovecustomlore":
        switch (args[1])
        {
          case "add" -> {
            if (length == 3)
            {
              return Method.tabCompleterList(args, "<?????? ????????? ??????>", true, "<?????? ????????? ??????>", "--empty");
            }
            return Method.tabCompleterList(args, "[?????? ????????? ??????]", true);
          }
          case "set" -> {
            if (length == 3)
            {
              return Method.tabCompleterIntegerRadius(args, 1, Integer.MAX_VALUE, "<???>");
            }
            NBTList<String> customLore = NBTAPI.getStringList(NBTAPI.getMainCompound(item), CucumberyTag.ABOVE_CUSTOM_LORE_KEY);
            try
            {
              int input = Integer.parseInt(args[2]);
              String lore = customLore.get(input - 1);
              if (args[3].equals("") && customLore.size() >= input && input > 0)
              {
                return Method.tabCompleterList(args, "<?????? ????????? ??????>", true, "<?????? ????????? ??????>", "--empty", lore.replace("??", "&"));
              }
            }
            catch (Exception ignored)
            {

            }
            if (length == 4)
            {
              return Method.tabCompleterList(args, "<?????? ????????? ??????>", true, "<?????? ????????? ??????>", "--empty");
            }
            return Method.tabCompleterList(args, "[?????? ????????? ??????]", true);
          }
          case "remove" -> {
            {
              if (length == 3)
              {
                NBTList<String> customLore = NBTAPI.getStringList(NBTAPI.getMainCompound(item), CucumberyTag.ABOVE_CUSTOM_LORE_KEY);
                if (customLore == null || customLore.size() == 0)
                {
                  return Collections.singletonList("??? ?????? ????????? ??? ?????? ?????? ????????? ????????? ????????????");
                }
                return Method.tabCompleterIntegerRadius(args, 1, customLore.size(), "[???]", "--all");
              }
            }
            break;
          }
          case "insert" -> {
            NBTList<String> customLore = NBTAPI.getStringList(NBTAPI.getMainCompound(item), CucumberyTag.ABOVE_CUSTOM_LORE_KEY);
            if (customLore == null || customLore.size() == 0)
            {
              return Collections.singletonList("?????? ????????? ????????? ????????? ??? ????????????");
            }
            if (length == 3)
            {
              return Method.tabCompleterIntegerRadius(args, 1, customLore.size(), "<???>");
            }
            if (length == 4)
            {
              return Method.tabCompleterList(args, "<?????? ????????? ??????>", true, "<?????? ????????? ??????>", "--empty");
            }
            return Method.tabCompleterList(args, "[?????? ????????? ??????]", true);
          }
        }
        break;
      case "expiredate":
        if (args.length == 2)
        {
          Calendar calendar = Calendar.getInstance();
          List<String> list = new ArrayList<>();
          list.add("--remove");
          list.addAll(Arrays.asList("~1???", "~10???", "~1??????", "~1???", "~7???", "~14???", "~21???", "~30???", "~1???"));
          calendar.add(Calendar.MINUTE, 1);
          list.add(Method.getCurrentTime(calendar, true, false));
          calendar.add(Calendar.MINUTE, 10);
          list.add(Method.getCurrentTime(calendar, true, false));
          calendar.add(Calendar.HOUR, 1);
          list.add(Method.getCurrentTime(calendar, true, false));
          calendar.add(Calendar.DATE, 1);
          list.add(Method.getCurrentTime(calendar, true, false));
          calendar.add(Calendar.DATE, 7);
          list.add(Method.getCurrentTime(calendar, true, false));
          calendar.add(Calendar.DATE, 14);
          list.add(Method.getCurrentTime(calendar, true, false));
          calendar.add(Calendar.DATE, 21);
          list.add(Method.getCurrentTime(calendar, true, false));
          calendar.add(Calendar.DATE, 30);
          list.add(Method.getCurrentTime(calendar, true, false));
          calendar.add(Calendar.DATE, 365);
          list.add(Method.getCurrentTime(calendar, true, false));
          return Method.tabCompleterList(args, list, "<??????>", true);
        }
        return Method.tabCompleterList(args, "[<??????>]", true);
      case "usage":
      {
        if ("command".equals(args[1]))
        {
          ItemUsageType itemUsageType;
          try
          {
            itemUsageType = ItemUsageType.valueOf(args[2].toUpperCase());
          }
          catch (Exception e)
          {
            return Collections.singletonList(args[2] + MessageUtil.getFinalConsonant(args[2], MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ?????? ???????????????");
          }
          NBTCompound usageTag = NBTAPI.getCompound(NBTAPI.getMainCompound(item), CucumberyTag.USAGE_KEY);
          NBTList<String> commands = NBTAPI.getStringList(NBTAPI.getCompound(usageTag, itemUsageType.getKey()), CucumberyTag.USAGE_COMMANDS_KEY);
          switch (args[3])
          {
            case "set":
              if (length == 5)
              {
                return Method.tabCompleterIntegerRadius(args, 1, Integer.MAX_VALUE, "<???>");
              }
              break;
            case "remove":
            {
              if (length == 5)
              {
                if (commands == null || commands.size() == 0)
                {
                  return Collections.singletonList("???????????? " + itemUsageType.getDisplay() + " ??? ????????? ?????? ?????? ?????? ????????????");
                }
                return Method.tabCompleterIntegerRadius(args, 1, commands.size(), "[???]", "--all");
              }
              break;
            }
            case "insert":
            {
              if (commands == null || commands.size() == 0)
              {
                return Collections.singletonList("???????????? " + itemUsageType.getDisplay() + " ??? ????????? ?????? ?????? ?????? ????????????");
              }
              if (length == 5)
              {
                return Method.tabCompleterIntegerRadius(args, 1, commands.size(), "<???>");
              }
            }
            break;
          }
          break;
        }
        break;
      }
      case "nbt":
        if (args[1].equals("merge"))
        {
          String input = MessageUtil.listToString(" ", 2, args.length, args);
          if (!input.equals(""))
          {
            try
            {
              new NBTContainer("{" + input + "}");
            }
            catch (Exception e)
            {
              return Collections.singletonList(input + MessageUtil.getFinalConsonant(input, MessageUtil.ConsonantType.??????) + " ????????? nbt?????????");
            }
          }
          if (args.length == 3)
          {
            return Method.tabCompleterList(args, "<nbt>", true, "<nbt>", "?????? : foo:bar,nbt:{extra:\"nbts\"}");
          }
          return Method.tabCompleterList(args, "[nbt]", true);
        }
        if (args.length >= 5 && args[1].equals("set"))
        {
          switch (args[2])
          {
            case "string":
            case "string-list":
              return Method.tabCompleterList(args, "[???]", true);
            case "compound":
              String input = MessageUtil.listToString(" ", 4, args.length, args);
              if (!input.equals(""))
              {
                try
                {
                  new NBTContainer(input);
                }
                catch (Exception e)
                {
                  return Collections.singletonList(input + MessageUtil.getFinalConsonant(input, MessageUtil.ConsonantType.??????) + " ????????? nbt?????????");
                }
              }
              String key = args[3];
              NBTItem nbtItem = new NBTItem(item);
              boolean exists = nbtItem.hasKey(key) && nbtItem.getType(key) == NBTType.NBTTagCompound;
              NBTCompound nbtCompound = nbtItem.getCompound(key);
              if (exists && nbtCompound.toString().length() < 100)
              {
                return Method.tabCompleterList(args, "<nbt>", true, "<nbt>", "?????? : {foo:bar,nbt:{extra:\"nbts\"}}", nbtCompound.toString(), nbtCompound.toString() + "(?????????)");
              }
              if (args.length == 5)
              {
                return Method.tabCompleterList(args, "<nbt>", true, "<nbt>", "?????? : {foo:bar,nbt:{extra:\"nbts\"}}");
              }
              return Method.tabCompleterList(args, "[nbt]", true);
            case "compound-list":
              input = MessageUtil.listToString(" ", 4, args.length, args);
              String[] split = input.split(";;");
              if (!input.equals(""))
              {
                for (String compoundString : split)
                {
                  try
                  {
                    new NBTContainer(compoundString);
                  }
                  catch (Exception e)
                  {
                    return Collections.singletonList(compoundString + MessageUtil.getFinalConsonant(compoundString, MessageUtil.ConsonantType.??????) + " ????????? nbt?????????");
                  }
                }
              }
              key = args[3];
              nbtItem = new NBTItem(item);
              NBTCompoundList nbtCompoundList = nbtItem.getCompoundList(key);
              if (nbtCompoundList != null && nbtCompoundList.size() > 0)
              {
                StringBuilder originValueStringBuilder = new StringBuilder();
                for (NBTCompound originValue2 : nbtCompoundList)
                {
                  originValueStringBuilder.append(originValue2.toString()).append(";;");
                }
                String originValueString = originValueStringBuilder.toString();
                originValueString = originValueString.substring(0, originValueString.length() - 2);
                return Method.tabCompleterList(args, "<nbt>", true, "<nbt>", "?????? : {foo:bar};;{wa:sans};;{third:list}", originValueString, originValueString + "(?????????)");
              }
              if (args.length == 5)
              {
                return Method.tabCompleterList(args, "<nbt>", true, "<nbt>", "?????? : {foo:bar};;{wa:sans};;{third:list}");
              }
              return Method.tabCompleterList(args, "[nbt]", true);
          }
        }
        break;
      case "customtag":
        if (length == 2)
        {
          return Method.tabCompleterList(args, "args", "list", "add", "remove");
        }
        else if (length == 3)
        {
          List<String> keys = new ArrayList<>();
          NBTItem nbtItem = new NBTItem(item);
          NBTCompound customTags = NBTAPI.getCompound(NBTAPI.getCompound(nbtItem, CucumberyTag.KEY_TMI), CucumberyTag.TMI_CUSTOM_TAGS);
          if (customTags != null)
          {
            for (String key : customTags.getKeys())
            {
              if (customTags.getBoolean(key))
              {
                keys.add(key);
              }
            }
          }
          switch (args[1])
          {
            case "add" -> {
              return Method.tabCompleterList(args, "new key", true);
            }
            case "remove" -> {
              return Method.tabCompleterList(args, keys, "key");
            }
          }
        }
        break;
    }

    if (length >= 3 && args[0].equals("food") && args[1].equals("nourishment"))
    {
      if (args.length == 3)
      {
        return Method.tabCompleterList(args, "<?????????>", true, "<?????????>", "--remove", "#57B6F0;??????", "#47B6F0;???-???", "#16F06C;??????", "#F0CA4F;??????", "#F05C48;??????", "#E553F0;???-???");
      }
      return Method.tabCompleterList(args, "[?????????]", true);
    }

    if (length >= 5 && args[0].equals("usage") && args[1].equals("command"))
    {
      if (args[3].equals("add") || args[3].equals("insert") || args[3].equals("set"))
      {
        int argLength = 5;
        boolean insertOrSet = args[3].equals("insert") || args[3].equals("set");
        if (insertOrSet)
        {
          argLength++;
        }
        if (insertOrSet)
        {
          if (!MessageUtil.isInteger(sender, args[4], false))
          {
            return Collections.singletonList(args[4] + MessageUtil.getFinalConsonant(args[4], MessageUtil.ConsonantType.??????) + " ????????? ????????????");
          }
          else if (!MessageUtil.checkNumberSize(sender, Integer.parseInt(args[4]), 1, Integer.MAX_VALUE, false))
          {
            return Collections.singletonList("????????? 1 ??????????????? ?????????, " + args[4] + MessageUtil.getFinalConsonant(args[4], MessageUtil.ConsonantType.??????) + " ????????????");
          }
        }
        if (length == argLength)
        {
          List<String> cmds = Method.getAllServerCommands();
          List<String> newCmds = new ArrayList<>();
          ItemUsageType itemUsageType;
          try
          {
            itemUsageType = ItemUsageType.valueOf(args[2].toUpperCase());
            NBTCompound usageTag = NBTAPI.getCompound(NBTAPI.getMainCompound(item), CucumberyTag.USAGE_KEY);
            NBTList<String> commands = NBTAPI.getStringList(NBTAPI.getCompound(usageTag, itemUsageType.getKey()), CucumberyTag.USAGE_COMMANDS_KEY);
            int input = Integer.parseInt(args[4]);
            String command = commands.get(input - 1);
            if (args[5].equals("") && commands.size() >= input && input > 0)
            {
              newCmds.add(command);
              return Method.tabCompleterList(args, newCmds, "<?????????>", true);
            }
          }
          catch (Exception ignored)
          {

          }
          newCmds.add("chat:" + "<?????? ?????????>");
          newCmds.add("opchat:" + "<?????? ???????????? ?????? ?????????>");
          newCmds.add("chat:/" + "<?????? ?????????>");
          newCmds.add("opchat:/" + "<?????? ???????????? ?????? ?????????>");
          for (String cmd2 : cmds)
          {
            newCmds.add(cmd2);
            newCmds.add("chat:/" + cmd2);
            newCmds.add("op:" + cmd2);
            newCmds.add("opchat:/" + cmd2);
            newCmds.add("console:" + cmd2);
          }
          List<String> list = new ArrayList<>(Method.tabCompleterList(args, Variable.commandPacks.keySet(), "<????????? ??? ??????>", true));
          for (int i = 0; i < list.size(); i++)
          {
            String fileName = list.get(i);
            newCmds.add("commandpack:" + fileName);
            list.set(i, "commandpack:" + fileName);
          }
          if (lastArg.startsWith("commandpack:"))
          {
            if (Variable.commandPacks.size() == 0)
            {
              return Collections.singletonList("????????? ????????? ??? ????????? ???????????? ????????????");
            }
            args[args.length - 1] = lastArg.substring(12);
            return Method.tabCompleterList(args, list, "<????????? ??? ??????>");
          }
          return Method.tabCompleterList(args, newCmds, "<?????????>", true);
        }
        else
        {
          String cmdLabel = args[argLength - 1];
          if (cmdLabel.startsWith("commandpack:"))
          {
            cmdLabel = cmdLabel.substring(12);
            if (Variable.commandPacks.size() == 0)
            {
              return Collections.singletonList("????????? ????????? ??? ????????? ???????????? ????????????");
            }
            YamlConfiguration config = Variable.commandPacks.get(cmdLabel);
            if (config == null)
            {
              return Collections.singletonList(cmdLabel + MessageUtil.getFinalConsonant(cmdLabel, MessageUtil.ConsonantType.??????) + " ??????????????? ??? ??? ?????? ????????? ??? ???????????????");
            }
            return Method.tabCompleterList(args, config.getKeys(false), "<????????? ???>");
          }
          if (cmdLabel.startsWith("op:"))
          {
            cmdLabel = cmdLabel.substring(3);
          }
          if (cmdLabel.startsWith("chat:/"))
          {
            cmdLabel = cmdLabel.substring(6);
          }
          else if (cmdLabel.startsWith("chat:"))
          {
            return Collections.singletonList("[?????????]");
          }
          if (cmdLabel.startsWith("opchat:/"))
          {
            cmdLabel = cmdLabel.substring(8);
          }
          else if (cmdLabel.startsWith("opchat:"))
          {
            return Collections.singletonList("[?????????]");
          }
          if (cmdLabel.startsWith("console:"))
          {
            cmdLabel = cmdLabel.substring(8);
          }
          if (length == argLength + 1 && (cmdLabel.equals("?") || cmdLabel.equals("bukkit:?") || cmdLabel.equals("bukkit:help")))
          {
            return Method.tabCompleterList(args, Method.getAllServerCommands(), "<?????????>");
          }
          PluginCommand command = Bukkit.getServer().getPluginCommand(cmdLabel);
          String[] args2 = new String[length - argLength];
          System.arraycopy(args, argLength, args2, 0, length - argLength);
          if (command != null)
          {
            org.bukkit.command.TabCompleter completer = command.getTabCompleter();
            if (completer != null)
            {
              return completer.onTabComplete(sender, command, command.getLabel(), args2);
            }
          }
          return Collections.singletonList("[<??????>]");
        }
      }
    }
    return Collections.singletonList(Prefix.ARGS_LONG.toString());
  }
}
