package com.jho5245.cucumbery.util.no_groups;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent.Completion;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectType;
import com.jho5245.cucumbery.util.nbt.CucumberyTag;
import com.jho5245.cucumbery.util.nbt.NBTAPI;
import com.jho5245.cucumbery.util.no_groups.CommandArgumentUtil.LocationTooltip;
import com.jho5245.cucumbery.util.storage.component.LocationComponent;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.component.util.ItemNameUtil;
import com.jho5245.cucumbery.util.storage.component.util.sendercomponent.SenderComponentUtil;
import com.jho5245.cucumbery.util.storage.data.*;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommandTabUtil
{
  public static final Completion ARGS_LONG = Completion.completion(Prefix.ARGS_LONG.toString(), Component.translatable(Prefix.ARGS_LONG.toString()));
  private static final List<Completion> SELECTORS = List.of(Completion.completion("@p", Component.translatable("argument.entity.selector.nearestPlayer")),
          Completion.completion("@r", Component.translatable("argument.entity.selector.randomPlayer")),
          Completion.completion("@a", Component.translatable("argument.entity.selector.allPlayers")),
          Completion.completion("@s", Component.translatable("argument.entity.selector.self")));

  @Deprecated
  public static List<String> customItemTabCompleter(Player player, String[] args)
  {
    ItemStack item = player.getInventory().getItemInMainHand();
    int length = args.length;
    if (!ItemStackUtil.itemExists(item))
    {
      return Collections.singletonList(Prefix.NO_HOLDING_ITEM.toString());
    }
    NBTCompound itemTag = NBTAPI.getMainCompound(item);
    NBTCompound customItemTag = NBTAPI.getCompound(itemTag, CucumberyTag.CUSTOM_ITEM_TAG_KEY);
    String customItemType = NBTAPI.getString(customItemTag, CucumberyTag.ID_KEY);
    if (customItemType == null)
    {
      return Collections.singletonList("?????? ???????????? ?????? ?????? ?????? ???????????? ????????? ????????? ????????? ????????????");
    }
    switch (customItemType)
    {
      case CucumberyTag.CUSTOM_ITEM_RAILGUN_ID:
        if (length == 3)
        {
          return Method.tabCompleterList(args, "<??????>", "range", "sortparticle", "ignoreinvincible", "density", "damage", "blockpenetrate", "fireworkrocketrequired", "cooldown", "cooldowntag", "piercing",
                  "laserwidth", "fireworktype", "reverse", "suicide");
        }
        else if (length == 4)
        {
          switch (args[2])
          {
            case "range":
              return Method.tabCompleterIntegerRadius(args, 1, 256, "<?????? ??????(m)>");
            case "sortparticle":
              return Method.tabCompleterBoolean(args, "<?????? ??????>");
            case "ignoreinvincible":
              return Method.tabCompleterBoolean(args, "<?????? ?????? ??????>");
            case "blockpenetrate":
              return Method.tabCompleterBoolean(args, "<?????? ??????>");
            case "fireworkrocketrequired":
              return Method.tabCompleterBoolean(args, "<????????? ?????? ??????>");
            case "reverse":
              return Method.tabCompleterBoolean(args, "<?????? ??????>");
            case "suicide":
              return Method.tabCompleterBoolean(args, "<?????? ??????>");
            case "density":
              return Method.tabCompleterIntegerRadius(args, 0, 10, "<????????? ?????? ?????? ??????>");
            case "piercing":
              return Method.tabCompleterIntegerRadius(args, 0, 100, "<?????? ??????>");
            case "fireworktype":
              return Method.tabCompleterIntegerRadius(args, 1, 10, "<?????? ??????>");
            case "cooldown":
              return Method.tabCompleterDoubleRadius(args, 0, 3600, "<????????? ?????? ??????(???)>");
            case "cooldowntag":
              return Method.tabCompleterList(args, "<????????? ?????? ?????? ??????>", true);
            case "damage":
              return Method.tabCompleterDoubleRadius(args, 0, Double.MAX_VALUE, "<?????????>");
            case "laserwidth":
              return Method.tabCompleterDoubleRadius(args, 0, 100, "<????????? ??????>");
          }
        }
        break;
      case CucumberyTag.CUSTOM_ITEM_FISHING_LOD_ID:
        if (item.getType() != Material.FISHING_ROD)
        {
          return Collections.singletonList("?????? ????????? ??????????????? ????????? ??? ????????????");
        }
        if (length == 3)
        {
          return Method.tabCompleterList(args, "<??????>", "multiplier", "x", "y", "z", "allow-on-air");
        }
        else if (length == 4)
        {
          switch (args[2])
          {
            case "multiplier":
              return Method.tabCompleterDoubleRadius(args, 0d, 5d, "<?????? ??????>");
            case "x":
            case "y":
            case "z":
              return Method.tabCompleterDoubleRadius(args, 0d, 5d, "<" + args[2] + "??? ?????? ?????????>");
            case "allow-on-air":
              return Method.tabCompleterBoolean(args, "<???????????? ?????? ?????? ??????>");
          }
        }
        break;
    }
    return Collections.singletonList(Prefix.ARGS_LONG.toString());
  }

  @Deprecated
  public static List<String> getCommandsTabCompleter(CommandSender sender, String[] args, int length, boolean forSudo)
  {
    if (args.length == length)
    {
      if (!forSudo)
      {
        return Method.tabCompleterList(args, Method.getAllServerCommands(), "<?????????>", true);
      }
      List<String> cmds = Method.getAllServerCommands();
      List<String> newCmds = new ArrayList<>();
      for (String cmd2 : cmds)
      {
        newCmds.add(cmd2);
        newCmds.add("op:" + cmd2);
      }
      return Method.tabCompleterList(args, newCmds, "<?????????>", true);
    }
    else
    {
      String cmdLabel = args[length - 1];
      if (forSudo)
      {
        if (cmdLabel.startsWith("op:"))
        {
          cmdLabel = cmdLabel.substring(3);
        }
      }
      if (args.length == length + 1 && Method.equals(cmdLabel, "?", "bukkit:?", "bukkit:help"))
      {
        return Method.tabCompleterList(args, Method.getAllServerCommands(), "<?????????>", true);
      }
      PluginCommand command = Bukkit.getServer().getPluginCommand(cmdLabel);
      String[] args2 = new String[args.length - length];
      System.arraycopy(args, length, args2, 0, args.length - length);
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

  @NotNull
  public static List<Completion> getCommandsTabCompleter2(@NotNull CommandSender sender, @NotNull String[] args, int length, boolean forSudo)
  {
    if (args.length == length)
    {
      if (!forSudo)
      {
        return tabCompleterList(args, Method.getAllServerCommands(), "<?????????>", true);
      }
      List<String> cmds = Method.getAllServerCommands();
      List<String> newCmds = new ArrayList<>();
      for (String cmd2 : cmds)
      {
        newCmds.add(cmd2);
        newCmds.add("op:" + cmd2);
      }
      return tabCompleterList(args, newCmds, "<?????????>", true);
    }
    else
    {
      String cmdLabel = args[length - 1];
      if (forSudo)
      {
        if (cmdLabel.startsWith("op:"))
        {
          cmdLabel = cmdLabel.substring(3);
        }
      }
      if (args.length == length + 1 && Method.equals(cmdLabel, "?", "bukkit:?", "bukkit:help"))
      {
        return tabCompleterList(args, Method.getAllServerCommands(), "<?????????>", true);
      }
      PluginCommand command = Bukkit.getPluginCommand(cmdLabel);
      String[] args2 = new String[args.length - length];
      System.arraycopy(args, length, args2, 0, args.length - length);
      if (command != null)
      {
        CommandExecutor executor = command.getExecutor();
        if (executor instanceof AsyncTabCompleter asyncTabCompleter)
        {
          return asyncTabCompleter.completion(sender, command, command.getLabel(), args2, CommandArgumentUtil.senderLocation(sender));
        }
        else
        {
          TabCompleter completer = command.getTabCompleter();
          if (completer != null)
          {
            List<String> list = completer.onTabComplete(sender, command, command.getLabel(), args2);
            if (list != null)
            {
              List<Completion> completions = new ArrayList<>();
              list.forEach(s -> completions.add(Completion.completion(s)));
              return completions;
            }
          }

        }
      }
      return Collections.singletonList(Completion.completion("[<??????>]"));
    }
  }

  @NotNull
  public static Completion completion(@NotNull Component component)
  {
    return Completion.completion(ComponentUtil.serialize(component), component);
  }

  @NotNull
  public static List<Completion> completions(@NotNull Component component)
  {
    return Collections.singletonList(completion(component));
  }

  @NotNull
  public static List<Completion> errorMessage(@NotNull String key)
  {
    return errorMessage(key, true);
  }

  @NotNull
  public static List<Completion> errorMessage(@NotNull String key, @NotNull Object... args)
  {
    Component component = ComponentUtil.translate("%s", ComponentUtil.translate(key, args), "error-text");
    return completions(component);
  }

  public static boolean isErrorMessage(@NotNull List<Completion> completions)
  {
    return completions.size() == 1 && completions.get(0).tooltip() instanceof TranslatableComponent translatableComponent && (translatableComponent.args().size() == 2 &&
            translatableComponent.args().get(1) instanceof TextComponent t && t.content().equals("error-text"));
  }

  /**
   * ?????? ?????????, ???????????? ??????, ???????????? ?????? ????????? UUID??? ???????????????
   *
   * @param sender                 ???????????? ???????????? ??????
   * @param lastArg                ???????????? ????????? ??????
   * @param excludeNonPlayerEntity ???????????? ?????? ????????? UUID ??? ??????????????? ?????? ????????? UUID ?????? ??????
   * @return ???????????? ???????????? ?????????
   */
  @NotNull
  private static List<Completion> tabCompleterEntity(@NotNull CommandSender sender, @NotNull String lastArg, boolean excludeNonPlayerEntity)
  {
    Player exactPlayer = Bukkit.getServer().getPlayerExact(lastArg);
    if (exactPlayer != null)
    {
      Component hover = SenderComponentUtil.senderComponent(exactPlayer, Constant.THE_COLOR, true);
      return Collections.singletonList(Completion.completion(lastArg, hover));
    }
    List<Completion> list = sender.hasPermission("minecraft.command.selector") ? new ArrayList<>(SELECTORS) : new ArrayList<>();
    for (Player online : Bukkit.getServer().getOnlinePlayers())
    {
      Component hover = SenderComponentUtil.senderComponent(online, Constant.THE_COLOR, true);
      list.add(Completion.completion(online.getName(), hover));
      String displayName = MessageUtil.stripColor(ComponentUtil.serialize(online.displayName()));
      String playerListName = MessageUtil.stripColor(ComponentUtil.serialize(online.playerListName()));
      list.add(Completion.completion(displayName, hover));
      list.add(Completion.completion(playerListName, hover));
    }
    if (!excludeNonPlayerEntity && sender instanceof Player player)
    {
      Location location = player.getLocation();
      location.add(location.getDirection().multiply(2d));
      List<Entity> entities = new ArrayList<>(Arrays.asList(location.getChunk().getEntities()));
      entities.addAll(Arrays.asList(location.clone().add(16, 0, 0).getChunk().getEntities()));
      entities.addAll(Arrays.asList(location.clone().add(0, 0, 16).getChunk().getEntities()));
      entities.addAll(Arrays.asList(location.clone().add(-16, 0, 0).getChunk().getEntities()));
      entities.addAll(Arrays.asList(location.clone().add(0, 0, -16).getChunk().getEntities()));
      entities.addAll(Arrays.asList(location.clone().add(16, 0, 16).getChunk().getEntities()));
      entities.addAll(Arrays.asList(location.clone().add(-16, 0, 16).getChunk().getEntities()));
      entities.addAll(Arrays.asList(location.clone().add(16, 0, -16).getChunk().getEntities()));
      entities.addAll(Arrays.asList(location.clone().add(-16, 0, -16).getChunk().getEntities()));
      entities.removeIf(entity ->
      {
        double distance = entity.getLocation().distance(location);
        return distance > 3d || entity instanceof Player;
      });
      for (Entity entity : entities)
      {
        Component hover = SenderComponentUtil.senderComponent(entity, Constant.THE_COLOR, true);
        String uuid = entity.getUniqueId().toString();
        list.add(Completion.completion(uuid, hover));
      }
    }
    if (sender instanceof Player player && player.getGameMode() == GameMode.SPECTATOR)
    {
      Entity entity = player.getSpectatorTarget();
      if (entity != null && (!excludeNonPlayerEntity || entity instanceof Player))
      {
        Component hover = ComponentUtil.translate("?????? ?????? ?????? (%s)", SenderComponentUtil.senderComponent(entity, Constant.THE_COLOR, true));
        String uuid = entity.getUniqueId().toString();
        list.add(Completion.completion(uuid, hover));
      }
    }
    if (lastArg.equals(""))
    {
      list.add(Completion.completion("#", Component.translatable("?????? ????????? ?????? ?????? (??? : #foo)")));
    }
    return list;
  }

  /**
   * ????????? ???????????????
   *
   * @param sender ???????????? ???????????? ??????
   * @param args   ???????????? ??????
   * @param key    ????????? ??????
   * @return ?????? ?????????
   */
  @NotNull
  public static List<Completion> tabCompleterEntity(@NotNull CommandSender sender, @NotNull String[] args, @NotNull Object key)
  {
    String arg = args[args.length - 1];
    if (Method.equals(arg, "@a", "@e", "@p", "@r", "@s", "@A", "@E", "@R", "@S") && !sender.hasPermission("minecraft.command.selector"))
    {
      return errorMessage("argument.entity.selector.not_allowed");
    }
    List<Completion> list = new ArrayList<>(tabCompleterEntity(sender, arg, false));
    if (sender.hasPermission("minecraft.command.selector"))
    {
      list.add(Completion.completion("@e", Component.translatable("argument.entity.selector.allEntities")));
      if (arg.startsWith("@"))
      {
        list.add(Completion.completion("@A", Component.translatable("????????? ????????? ?????? ????????????")));
        list.add(Completion.completion("@E", Component.translatable("??????????????? ????????? ?????? ??????")));
        list.add(Completion.completion("@R", Component.translatable("????????? ??????")));
        if (arg.toLowerCase().startsWith("@r"))
        {
          list.add(Completion.completion("@rR", Component.translatable("????????? ????????? ????????? ??????")));
        }
        list.add(Completion.completion("@S", Component.translatable("????????? ????????? ?????? ??????")));
        if (arg.toLowerCase().startsWith("@p"))
        {
          list.add(Completion.completion("@P", Component.translatable("????????? ????????? ?????? ????????? ????????????")));
          list.add(Completion.completion("@pp", Component.translatable("????????? ????????? ?????? ????????? ??????")));
        }
      }
      if (arg.startsWith("#"))
      {
        for (World world : Bukkit.getWorlds())
        {
          for (Chunk chunk : world.getLoadedChunks())
          {
            for (Entity entity : chunk.getEntities())
            {
              for (String tag : entity.getScoreboardTags())
              {
                list.add(Completion.completion("#" + tag, ComponentUtil.translate("%s ????????? ?????? ??????", tag)));
              }
            }
          }
        }
      }
    }
    return tabCompleterList(args, list, key, arg.startsWith("@") || arg.startsWith("#"));
  }

  @NotNull
  public static List<Completion> tabCompleterPlayer(@NotNull CommandSender sender, @NotNull String[] args, @NotNull Object key)
  {
    String arg = args[args.length - 1];
    if (Method.equals(arg, "@a", "@e", "@p", "@r", "@s", "@A", "@E") && !sender.hasPermission("minecraft.command.selector"))
    {
      return errorMessage("argument.entity.selector.not_allowed");
    }
    List<Completion> list = new ArrayList<>(tabCompleterEntity(sender, arg, true));
    if (sender.hasPermission("minecraft.command.selector"))
    {
      if (arg.startsWith("@"))
      {
        list.add(Completion.completion("@A", Component.translatable("????????? ????????? ?????? ????????????")));
        if (arg.toLowerCase().startsWith("@r"))
        {
          list.add(Completion.completion("@rr", Component.translatable("????????? ????????? ????????? ????????????")));
        }
        if (arg.toLowerCase().startsWith("@p"))
        {
          list.add(Completion.completion("@P", Component.translatable("????????? ????????? ?????? ????????? ????????????")));
        }
      }
      if (arg.startsWith("#"))
      {
        for (World world : Bukkit.getWorlds())
        {
          for (Chunk chunk : world.getLoadedChunks())
          {
            for (Entity entity : chunk.getEntities())
            {
              for (String tag : entity.getScoreboardTags())
              {
                list.add(Completion.completion("#" + tag, ComponentUtil.translate("%s ????????? ?????? ??????", tag)));
              }
            }
          }
        }
      }
    }
    return tabCompleterList(args, list, key, arg.startsWith("@") || arg.startsWith("#"));
  }

  @NotNull
  public static List<Completion> tabCompleterOfflinePlayer(@NotNull CommandSender sender, @NotNull String[] args, @NotNull Object key)
  {
    String arg = args[args.length - 1];
    if (Method.equals(arg, "@a", "@e", "@p", "@r", "@s", "@A", "@E") && !sender.hasPermission("minecraft.command.selector"))
    {
      return errorMessage("argument.entity.selector.not_allowed");
    }
    List<Completion> list = new ArrayList<>(tabCompleterEntity(sender, arg, true));
    if (sender.hasPermission("minecraft.command.selector"))
    {
      if (arg.startsWith("@"))
      {
        list.add(Completion.completion("@A", Component.translatable("????????? ????????? ?????? ????????????")));
        if (arg.toLowerCase().startsWith("@r"))
        {
          list.add(Completion.completion("@rr", Component.translatable("????????? ????????? ????????? ????????????")));
        }
        if (arg.toLowerCase().startsWith("@p"))
        {
          list.add(Completion.completion("@P", Component.translatable("????????? ????????? ?????? ????????? ????????????")));
        }
      }
      if (arg.startsWith("#"))
      {
        for (World world : Bukkit.getWorlds())
        {
          for (Chunk chunk : world.getLoadedChunks())
          {
            for (Entity entity : chunk.getEntities())
            {
              for (String tag : entity.getScoreboardTags())
              {
                list.add(Completion.completion("#" + tag, ComponentUtil.translate("%s ????????? ?????? ??????", tag)));
              }
            }
          }
        }
      }
    }
    for (String nickName : Variable.nickNames)
    {
      if (!Method.isUUID(nickName) || Bukkit.getOfflinePlayer(UUID.fromString(nickName)).getName() == null)
      {
        nickName = MessageUtil.stripColor(nickName);
        OfflinePlayer offlinePlayer = Method.getOfflinePlayer(sender, nickName, false);
        Component hover = offlinePlayer == null ? null :
                offlinePlayer.getPlayer() != null ? SenderComponentUtil.senderComponent(offlinePlayer.getPlayer(), Constant.THE_COLOR, true) :
                        Component.text(offlinePlayer.getUniqueId().toString());
        list.add(Completion.completion(nickName, hover));
      }
    }
    return tabCompleterList(args, list, key, arg.startsWith("@") || arg.startsWith("#"));
  }

  @NotNull
  public static List<Completion> tabCompleterList(@NotNull String[] args, @NotNull List<?> list, @NotNull Object key)
  {
    return tabCompleterList(args, list, key, false);
  }

  @NotNull
  public static List<Completion> tabCompleterList(@NotNull String[] args, @NotNull List<?> list, @NotNull Object key, boolean ignoreEmpty)
  {
    String tabArg = args[args.length - 1];
    Completion returnKey = null;
    String suggestion;
    if (key instanceof Completion completion)
    {
      suggestion = completion.suggestion();
      if (completion.tooltip() == null)
      {
        returnKey = Completion.completion(completion.suggestion(), Component.translatable(suggestion.substring(1, suggestion.length() - 1)));
      }
    }
    else
    {
      suggestion = key.toString();
      returnKey = Completion.completion(suggestion, Component.translatable(suggestion.substring(1, suggestion.length() - 1)));
    }
    if (!ignoreEmpty && tabArg.equals("") && list.isEmpty() || ignoreEmpty && list.isEmpty())
    {
      return Collections.singletonList(returnKey);
    }
    list = new ArrayList<>(list);
    list.removeIf(e -> e == null || e.equals("") || (e instanceof Completion completion && completion.suggestion().equals("")));
    int length = tabArg.length();
    if (!tabArg.equals(""))
    {
      if (length == 1 && tabArg.charAt(0) >= '???' && tabArg.charAt(0) <= '???')
      {
        length = 3;
      }
      else
      {
        for (char c : tabArg.toCharArray())
        {
          if (c >= '???' && c <= '???')
          {
            length++;
          }
        }
      }
      List<Completion> returnValue = new ArrayList<>();
      String replace = tabArg.replace(" ", "").replace("_", "").toLowerCase();
      for (Object o : list)
      {
        String str = o instanceof Completion completion ? completion.suggestion() : o.toString();
        final boolean contains = str.toLowerCase().replace(" ", "").replace("_", "").contains(replace);
        if ((length <= 2 && str.replace(Constant.TAB_COMPLETER_QUOTE_ESCAPE, "").toLowerCase().startsWith(replace)) || (length >= 3 && contains))
        {
          returnValue.add(o instanceof Completion completion ? completion : Completion.completion(o.toString()));
        }
      }
      if (returnValue.size() == 1)
      {
        String s = returnValue.get(0).suggestion();
        if (s.contains(" ") && s.endsWith(tabArg))
        {
          return Collections.singletonList(returnKey);
        }
      }
      if (returnValue.isEmpty() && !(key instanceof Completion completion ? completion.suggestion() : key.toString()).equals("") && !ignoreEmpty)
      {
        String key2 = (key instanceof Completion completion ? completion.suggestion() :
                key.toString()).replace("<", "").replace(">", "").replace("[", "").replace("]", "");
        String msg;
        switch (key2)
        {
          case "?????????" -> msg = "argument.item.id.invalid";
          case "??????" -> msg = "argument.block.id.invalid";
          case "??????" -> msg = "???????????? ????????? ????????? ????????????. (%s)";
          default -> msg = "'%s'???(???) ??????????????? ??? ??? ?????? %s?????????";
        }
        if (key2.contains("??????"))
        {
          msg = "????????? ?????? ??? ???????????? (%s)";
        }
        if (key2.contains("????????????") || key2.contains("?????????"))
        {
          msg = "??????????????? ?????? ??? ???????????? (%s)";
        }
        return errorMessage(msg, tabArg, Component.translatable(key2));
      }
      returnValue = sort(returnValue);
      if ((ignoreEmpty && returnValue.isEmpty()) || (!(key instanceof Completion completion ? completion.suggestion() : key.toString())
              .equals("") && returnValue.size() == 1 && returnValue.get(0).suggestion().equalsIgnoreCase(tabArg)))
      {
        return Collections.singletonList(returnKey);
      }
      return returnValue;
    }
    List<Completion> completions = new ArrayList<>();
    for (Object o : list)
    {
      completions.add(o instanceof Completion completion ? completion : Completion.completion(o.toString()));
    }
    return sort(completions);
  }

  @NotNull
  public static List<Completion> tabCompleterList(@NotNull String[] args, @NotNull Enum<?>[] array, @NotNull Object key)
  {
    return tabCompleterList(args, array, key, null);
  }

  @NotNull
  public static List<Completion> tabCompleterList(@NotNull String[] args, @NotNull Enum<?>[] array, @NotNull Object key, @Nullable Predicate<Enum<?>> exclude)
  {
    List<Object> list = new ArrayList<>();
    String arg = args[args.length - 1];
    if (arg.equals(arg.toLowerCase()))
    {
      for (Enum<?> e : array)
      {
        if (exclude != null && exclude.test(e))
        {
          continue;
        }
        if (!(e instanceof EnumHideable enumHideable) || !enumHideable.isHiddenEnum())
        {
          Component hover = null;
          if (e instanceof Material material)
          {
            hover = ItemNameUtil.itemName(material);
          }
          else if (e instanceof Statistic statistic)
          {
            hover = switch (statistic)
                    {
                      case ENTITY_KILLED_BY -> Component.translatable("???????????? ?????? ??????");
                      case KILL_ENTITY -> Component.translatable("????????? ?????? ??????");
                      default -> Component.translatable(TranslatableKeyParser.getKey(statistic));
                    };
          }
          else if (e instanceof Translatable translatable)
          {
            hover = Component.translatable(translatable.translationKey());
          }
          list.add(Completion.completion(e.toString().toLowerCase(), hover));
        }
      }
    }
    return tabCompleterList(args, list, key);
  }

  @NotNull
  public static List<Completion> tabCompleterList(@NotNull String[] args, @NotNull Map<?, ?> map, @NotNull Object key)
  {
    return tabCompleterList(args, map, key, null);
  }

  @NotNull
  public static List<Completion> tabCompleterList(@NotNull String[] args, @NotNull Map<?, ?> map, @NotNull Object key, Predicate<Object> exclude)
  {
    List<Object> list = new ArrayList<>();
    for (Object k : map.keySet())
    {
      if (exclude != null && exclude.test(k))
      {
        continue;
      }
      Object v = map.get(k);
      if (v instanceof CustomEffectType customEffectType && customEffectType.isEnumHidden())
      {
        continue;
      }
      String s = k.toString();
      Component hover = null;
      if (v instanceof CustomEffectType customEffectType)
      {
        hover = Component.translatable(customEffectType.translationKey()).color(customEffectType.isNegative() ? NamedTextColor.RED : NamedTextColor.GREEN);
      }
      list.add(Completion.completion(s, hover));
    }
    return tabCompleterList(args, list, key);
  }

  @NotNull
  public static List<Completion> tabCompleterList(@NotNull String[] args, @NotNull Object key, boolean ignoreEmpty, @NotNull Object... list)
  {
    return tabCompleterList(args, Arrays.asList(list), key, ignoreEmpty);
  }

  @NotNull
  public static List<Completion> tabCompleterBoolean(@NotNull String[] args, @NotNull Object key)
  {
    String tag = args[args.length - 1];
    if (!Method.startsWith(tag, true, "true", "false"))
    {
      return errorMessage("parsing.bool.invalid", tag);
    }
    if (tag.equals("true"))
    {
      return Collections.singletonList(key instanceof Completion completion ? completion : Completion.completion(key.toString()));
    }
    String hover = key instanceof Completion completion ? completion.suggestion() : key.toString();
    // '[????????? ?????? ?????? ??????]' ??? ?????? ?????? ?????? '['??? ']' ??????
    hover = hover.substring(1, hover.length() - 1);
    return CommandTabUtil.tabCompleterList(args, key, false, Completion.completion("true", Component.translatable(hover)), Completion.completion("false", Component.translatable(hover)));
  }

  @NotNull
  public static List<Completion> tabCompleterDoubleRadius(
          @NotNull String[] args, double from, double to, @NotNull Object key)
  {
    return tabCompleterDoubleRadius(args, from, false, to, false, key);
  }

  @NotNull
  public static List<Completion> tabCompleterDoubleRadius(
          @NotNull String[] args, double from, boolean excludeFrom, double to, boolean excludeTo, @NotNull Object key)
  {
    String tabArg = args[args.length - 1];
    Completion completion = key instanceof Completion c ? c : Completion.completion(key.toString(), ComponentUtil.translate("?????? ?????? : %s %s %s %s",
            Constant.Sosu2.format(from), excludeFrom ? "??????" : "??????", Constant.Sosu2.format(to), excludeTo ? "??????" : "??????"));
    if (tabArg.equals(""))
    {
      return Collections.singletonList(completion);
    }
    if (from == -Double.MAX_VALUE && to == Double.MAX_VALUE)
    {
      return Collections.singletonList(objectToCompletion(key));
    }
    if (!MessageUtil.isDouble(null, tabArg, false))
    {
      return errorMessage("parsing.double.invalid", tabArg);
    }
    double argDouble = Double.parseDouble(tabArg);
    tabArg = Constant.Sosu15.format(argDouble);
    if (!MessageUtil.checkNumberSize(null, argDouble, from, Double.MAX_VALUE, excludeFrom, excludeTo, false))
    {
      return errorMessage(excludeFrom ? "double??? %s ??????????????? ?????????, %s???(???) ????????????" : "argument.double.low", Constant.Sosu15.format(from), tabArg);
    }
    if (!MessageUtil.checkNumberSize(null, argDouble, -Double.MAX_VALUE, to, excludeFrom, excludeTo, false))
    {
      return errorMessage(excludeTo ? "double??? %s ??????????????? ?????????, %s???(???) ????????????" : "argument.double.big", Constant.Sosu15.format(to), tabArg);
    }
    String keyString = completion.suggestion();
    char keyLast = keyString.charAt(keyString.length() - 1);
    keyString = keyString.substring(0, keyString.length() - 1) + "=" + Constant.Sosu15.format(argDouble) + keyLast;
    return Collections.singletonList(Completion.completion(keyString, completion.tooltip()));
  }

  @NotNull
  public static List<Completion> tabCompleterLongRadius(@NotNull String[] args, long from, long to, @NotNull Object key)
  {
    String tabArg = args[args.length - 1];
    Completion completion = key instanceof Completion c ? c : Completion.completion(key.toString(), ComponentUtil.translate("?????? ?????? : %s ?????? %s ??????", Constant.Sosu2.format(from), Constant.Sosu2.format(to)));
    if (tabArg.equals(""))
    {
      return Collections.singletonList(completion);
    }
    if (!MessageUtil.isDouble(null, tabArg, false))
    {
      return errorMessage("parsing.long.invalid", tabArg);
    }
    if (!MessageUtil.isLong(null, tabArg, false))
    {
      return errorMessage("parsing.long.invalid", tabArg);
    }
    long argLong = Long.parseLong(tabArg);
    tabArg = Constant.Sosu15.format(argLong);
    if (!MessageUtil.checkNumberSize(null, argLong, from, Long.MAX_VALUE))
    {
      return errorMessage("argument.long.low", Constant.Sosu15.format(from), tabArg);
    }
    if (!MessageUtil.checkNumberSize(null, argLong, Long.MIN_VALUE, to))
    {
      return errorMessage("argument.long.big", Constant.Sosu15.format(from), tabArg);
    }
    String keyString = completion.suggestion();
    char keyLast = keyString.charAt(keyString.length() - 1);
    keyString = keyString.substring(0, keyString.length() - 1) + "=" + Constant.Sosu15.format(argLong) + keyLast;
    return Collections.singletonList(Completion.completion(keyString, completion.tooltip()));
  }

  @NotNull
  public static List<Completion> tabCompleterIntegerRadius(@NotNull String[] args, int from, int to, @NotNull Object key)
  {
    String tabArg = args[args.length - 1];
    Completion completion = key instanceof Completion c ? c : Completion.completion(key.toString(), ComponentUtil.translate("?????? ?????? : %s ?????? %s ??????", Constant.Sosu2.format(from), Constant.Sosu2.format(to)));
    if (tabArg.equals(""))
    {
      return Collections.singletonList(completion);
    }
    if (!MessageUtil.isDouble(null, tabArg, false))
    {
      return errorMessage("parsing.int.invalid", tabArg);
    }
    if (!MessageUtil.isInteger(null, tabArg, false))
    {
      return errorMessage("parsing.int.invalid", tabArg);
    }
    int argInteger = Integer.parseInt(tabArg);
    tabArg = Constant.Sosu15.format(argInteger);
    if (!MessageUtil.checkNumberSize(null, argInteger, from, Integer.MAX_VALUE))
    {
      return errorMessage("argument.integer.low", Constant.Sosu15.format(from), tabArg);
    }
    if (!MessageUtil.checkNumberSize(null, argInteger, Integer.MIN_VALUE, to))
    {
      return errorMessage("argument.integer.big", Constant.Sosu15.format(to), tabArg);
    }

    String keyString = completion.suggestion();
    char keyLast = keyString.charAt(keyString.length() - 1);
    keyString = keyString.substring(0, keyString.length() - 1) + "=" + Constant.Sosu15.format(argInteger) + keyLast;
    return Collections.singletonList(Completion.completion(keyString, completion.tooltip()));
  }

  @NotNull
  public static List<Completion> locationArgument(@NotNull CommandSender sender, @NotNull String[] args, @NotNull Object key, @Nullable Location location, boolean isInteger)
  {
    return locationArgument(sender, args, key, location, isInteger, null);
  }

  @NotNull
  public static List<Completion> locationArgument(@NotNull CommandSender sender, @NotNull String[] args, @NotNull Object key, @Nullable Location location, boolean isInteger, @Nullable List<LocationTooltip> extraSuggestions)
  {
    if (location == null)
    {
      location = CommandArgumentUtil.senderLocation(sender);
    }
    if (isInteger)
    {
      location.setX(location.getBlockX());
      location.setY(location.getBlockY());
      location.setZ(location.getBlockZ());
    }
    String x = isInteger ? location.getBlockX() + "" : Constant.Sosu2rawFormat.format(location.getX());
    String y = isInteger ? location.getBlockY() + "" : Constant.Sosu2rawFormat.format(location.getY());
    String z = isInteger ? location.getBlockZ() + "" : Constant.Sosu2rawFormat.format(location.getZ());
    Component hover = ComponentUtil.translate("?????? ?????? (%s)", location);
    @Nullable Block targetBlock = null;
    if (sender instanceof Player player)
    {
      targetBlock = player.getTargetBlockExact(6);
    }
    List<Object> list = new ArrayList<>(Arrays.asList(
            Completion.completion("~ ~ ~"),
            Completion.completion("~ ~"),
            Completion.completion("~"),
            Completion.completion(x + " " + y + " " + z, hover)
    ));
    if (extraSuggestions != null)
    {
      for (LocationTooltip tooltip : extraSuggestions)
      {
        Location loc = tooltip.location();
        Component h = tooltip.hover();
        if (h == null)
        {
          h = LocationComponent.locationComponent(loc);
        }
        String X = isInteger ? loc.getBlockX() + "" : Constant.Sosu2rawFormat.format(loc.getX());
        String Y = isInteger ? loc.getBlockY() + "" : Constant.Sosu2rawFormat.format(loc.getY());
        String Z = isInteger ? loc.getBlockZ() + "" : Constant.Sosu2rawFormat.format(loc.getZ());
        list.add(Completion.completion(X + " " + Y + " " + Z, h));
      }
    }
    if (targetBlock != null)
    {
      hover = ComponentUtil.translate("???????????? ?????? ?????? (%s)", targetBlock.getType());
      int x2 = targetBlock.getX(), y2 = targetBlock.getY(), z2 = targetBlock.getZ();
      list.add(Completion.completion(x2 + " " + y2 + " " + z2, hover));
    }
    String arg = args[args.length - 1];
    if (!arg.equals(""))
    {
      if (arg.equals("~ ~ ~") || arg.equals("^ ^ ^"))
      {
        return Collections.singletonList(objectToCompletion(key));
      }
      if (arg.contains("~") && arg.contains("^") || (arg.contains("^") && !Method.allStartsWith("^", true, arg.split(" "))))
      {
        return errorMessage("argument.pos.mixed");
      }
      else if (arg.startsWith("^"))
      {
        list.removeIf(c -> c instanceof Completion completion && completion.suggestion().startsWith("~"));
        list.addAll(Arrays.asList(
                Completion.completion("^ ^ ^"),
                Completion.completion("^ ^"),
                Completion.completion("^")));
      }
      String[] split = arg.split(" ");
      x = split[0];
      y = split.length > 1 ? split[1] : y;
      z = split.length > 2 ? split[2] : z;
      if (isInteger)
      {
        if (!MessageUtil.isInteger(sender, x, false))
        {
          if (x.startsWith("~") || x.startsWith("^"))
          {
            if (!x.equals("~") && !x.equals("^"))
            {
              x = x.substring(1);
              if (!MessageUtil.isInteger(sender, x, false))
              {
                return errorMessage("argument.pos.missing.int");
              }
            }
          }
          else
          {
            return errorMessage("argument.pos.missing.int");
          }
        }
        if (!MessageUtil.isInteger(sender, y, false))
        {
          if (y.startsWith("~") || y.startsWith("^"))
          {
            if (!y.equals("~") && !y.equals("^"))
            {
              y = y.substring(1);
              if (!MessageUtil.isInteger(sender, y, false))
              {
                return errorMessage("argument.pos.missing.int");
              }
            }
          }
          else
          {
            return errorMessage("argument.pos.missing.int");
          }
        }
        if (!MessageUtil.isInteger(sender, z, false))
        {
          if (z.startsWith("~") || z.startsWith("^"))
          {
            if (!z.equals("~") && !z.equals("^"))
            {
              z = z.substring(1);
              if (!MessageUtil.isInteger(sender, z, false))
              {
                return errorMessage("argument.pos.missing.int");
              }
            }
          }
          else
          {
            return errorMessage("argument.pos.missing.int");
          }
        }
      }
      else
      {
        if (!MessageUtil.isDouble(sender, x, false))
        {
          if (x.startsWith("~") || x.startsWith("^"))
          {
            if (!x.equals("~") && !x.equals("^"))
            {
              x = x.substring(1);
              if (!MessageUtil.isDouble(sender, x, false))
              {
                return errorMessage("argument.pos.missing.double");
              }
            }
          }
          else
          {
            return errorMessage("argument.pos.missing.double");
          }
        }
        if (!MessageUtil.isDouble(sender, y, false))
        {
          if (y.startsWith("~") || y.startsWith("^"))
          {
            if (!y.equals("~") && !y.equals("^"))
            {
              y = y.substring(1);
              if (!MessageUtil.isDouble(sender, y, false))
              {
                return errorMessage("argument.pos.missing.double");
              }
            }
          }
          else
          {
            return errorMessage("argument.pos.missing.double");
          }
        }
        if (!MessageUtil.isDouble(sender, z, false))
        {
          if (z.startsWith("~") || z.startsWith("^"))
          {
            if (!z.equals("~") && !z.equals("^"))
            {
              z = z.substring(1);
              if (!MessageUtil.isDouble(sender, z, false))
              {
                return errorMessage("argument.pos.missing.double");
              }
            }
          }
          else
          {
            return errorMessage("argument.pos.missing.double");
          }
        }
      }

      if (split.length > 3)
      {
        return errorMessage("???????????? 3???????????? ?????????");
      }
      split = arg.split(" ");
      if (arg.startsWith("^"))
      {
        y = split.length > 1 ? split[1] : y;
        z = split.length > 2 ? split[2] : z;
        if (y.startsWith("^"))
        {
          if (z.startsWith("^"))
          {
            list.add(Completion.completion(split[0] + " " + y + " " + z));
          }
          else
          {
            list.add(Completion.completion(split[0] + " " + y + " ^"));
          }
          list.add(Completion.completion(split[0] + " " + y));
        }
        else
        {
          list.add(Completion.completion(split[0] + " ^ ^"));
          list.add(Completion.completion(split[0] + " ^"));
        }
      }
      else
      {
        list.add(Completion.completion(split[0] + " " + (split.length > 1 ? split[1] : y) + " " + (split.length > 2 ? split[2] : z)));
        list.add(Completion.completion(split[0] + " " + (split.length > 1 ? split[1] : y)));
        list.add(Completion.completion(split[0] + " ~ ~"));
        list.add(Completion.completion(split[0] + " ~"));
      }
      list.add(Completion.completion(split[0]));
      if (isErrorMessage(tabCompleterList(args, list, key)) && split.length < 3)
      {
        return errorMessage("argument.pos3d.incomplete");
      }
      list.removeIf(c -> c instanceof Completion completion && completion.suggestion().equals(arg));
    }
    return tabCompleterList(args, list, key, true);
  }

  @NotNull
  public static List<Completion> rotationArgument(@NotNull CommandSender sender, @NotNull String[] args, @NotNull Object key, @Nullable Location location)
  {
    if (location == null)
    {
      location = CommandArgumentUtil.senderLocation(sender);
    }
    String yaw = Constant.Sosu2rawFormat.format(location.getYaw()), pitch = Constant.Sosu2rawFormat.format(location.getPitch());
    List<Completion> list = new ArrayList<>(Arrays.asList(
            Completion.completion("~ ~"),
            Completion.completion("~"),
            Completion.completion(yaw + " " + pitch, ComponentUtil.translate("?????? ???????????? ?????? ??????"))
    ));
    String arg = args[args.length - 1];
    if (!arg.equals(""))
    {
      if (arg.equals("~ ~"))
      {
        return Collections.singletonList(objectToCompletion(key));
      }
      String[] split = arg.split(" ");
      yaw = split[0];
      pitch = split.length > 1 ? split[1] : pitch;
      if (!MessageUtil.isDouble(sender, yaw, false))
      {
        if (yaw.startsWith("~"))
        {
          if (!yaw.equals("~"))
          {
            yaw = yaw.substring(1);
            if (!MessageUtil.isDouble(sender, yaw, false))
            {
              return errorMessage("argument.pos.missing.double");
            }
          }
        }
        else
        {
          return errorMessage("argument.pos.missing.double");
        }
      }
      if (!MessageUtil.isDouble(sender, pitch, false))
      {
        if (pitch.startsWith("~"))
        {
          if (!pitch.equals("~"))
          {
            pitch = pitch.substring(1);
            if (!MessageUtil.isDouble(sender, pitch, false))
            {
              return errorMessage("argument.pos.missing.double");
            }
          }
        }
        else
        {
          return errorMessage("argument.pos.missing.double");
        }
      }
      if (split.length > 2)
      {
        return errorMessage("???????????? 2???????????? ?????????");
      }
      list.add(Completion.completion(split[0] + " " + (split.length > 1 ? split[1] : pitch)));
      list.add(Completion.completion(split[0] + " ~"));
      list.add(Completion.completion(split[0]));
      if (isErrorMessage(tabCompleterList(args, list, key)) && split.length < 2)
      {
        return errorMessage("argument.pos2d.incomplete");
      }
      list.removeIf(c -> c.suggestion().equals(arg));
    }
    return tabCompleterList(args, list, key, true);
  }

  @NotNull
  public static List<Completion> worldArgument(@NotNull CommandSender sender, @NotNull String[] args, @NotNull Object key)
  {
    List<World> worlds = Bukkit.getWorlds();
    List<Completion> list = new ArrayList<>();
    for (World world : worlds)
    {
      String tooltipString = Method.getWorldDisplayName(world);
      Component tooltip = tooltipString.equals(world.getName()) ? null : ComponentUtil.create(tooltipString);
      list.add(Completion.completion(world.getName(), tooltip));
    }
    World currentWorld = CommandArgumentUtil.senderLocation(sender).getWorld();
    String tooltipString = Method.getWorldDisplayName(currentWorld);
    Component tooltip = tooltipString.equals(currentWorld.getName()) ? ComponentUtil.translate("?????? ?????? (%s)", currentWorld.getName())
            : ComponentUtil.translate("?????? ?????? (%s, %s)", currentWorld.getName(), ComponentUtil.create(tooltipString));
    list.add(Completion.completion("~", tooltip));
    return tabCompleterList(args, list, key);
  }

  @NotNull
  public static List<Completion> itemStackArgument(@NotNull CommandSender sender, @NotNull String[] args, @NotNull Object key)
  {
    List<Completion> list = new ArrayList<>(tabCompleterList(args, Material.values(), key, e -> e instanceof Material material && (material.isAir() || !material.isItem())));
    String arg = args[args.length - 1];
    boolean regex = arg.equals("") || (arg.matches("(.*[a-z0-9_-])") && !arg.contains("{"));
    if (regex)
    {
      try
      {
        if (arg.toLowerCase().equals(arg))
        {
          Material material = Material.valueOf(arg.toUpperCase());
          if (!(material.isAir() || !material.isItem()))
          {
            return List.of(Completion.completion(arg + "{"));
          }
        }
      }
      catch (Exception ignored)
      {

      }
      return list;
    }
    try
    {
      Bukkit.getItemFactory().createItemStack(args[args.length - 1]);
      return tabCompleterList(args, key, true);
    }
    catch (Exception e)
    {
      TranslatableComponent component = ItemStackUtil.getErrorCreateItemStack(e.getCause());
      return completions(ComponentUtil.translate("%s", component, "error-text"));
    }
  }

  @NotNull
  public static Completion objectToCompletion(@NotNull Object object)
  {
    return object instanceof Completion completion ? completion : Completion.completion(object.toString());
  }

  @NotNull
  public static List<Completion> sort(@NotNull List<Completion> list)
  {
    list = new ArrayList<>(list);
    for (int i = 0; i < list.size(); i++)
    {
      String s = list.get(i).suggestion();
      // for legacy
      try
      {
        if (s.startsWith("{\""))
        {
          s = ComponentUtil.serialize(GsonComponentSerializer.gson().deserialize(s));
        }
      }
      catch (Exception ignored)
      {

      }
      s = MessageUtil.stripColor(s);
      if (s.length() > 123)
      {
        s = "???????????????! ???????????? ?????? ???????????????! : " + s.substring(0, 100) + "...";
      }
      boolean escaped = s.startsWith(Constant.TAB_COMPLETER_QUOTE_ESCAPE);
      if (escaped)
      {
        s = s.replace(Constant.TAB_COMPLETER_QUOTE_ESCAPE, "");
      }
      if (s.contains(" ") &&
              (!(s.startsWith("(") || s.endsWith(")") ||
                      s.startsWith("<") || s.endsWith(">") ||
                      s.startsWith("[") || s.endsWith("]")
              ) || !escaped)
      )
      {
        if (s.contains("'"))
        {
          s = s.replace("'", "''");
        }
        s = "'" + s + "'";
      }
      list.set(i, Completion.completion(s, list.get(i).tooltip()));
    }
    return list.stream().distinct().collect(Collectors.toList());
  }

  @NotNull
  public static List<Completion> removeDupe(@NotNull List<Completion> list)
  {
    return list.stream().distinct().collect(Collectors.toList());
  }

  /**
   * ?????? ?????? ????????? ?????? ??????????????? ????????? ????????? ????????? ?????? ????????? ?????????
   *
   * @param completions ?????? ????????? ?????? ?????? ????????????
   * @return ?????? ???????????? ????????? ???????????? ?????? ????????? ???????????? ?????? ?????? ????????? ?????? ???????????? ??????
   */
  @SafeVarargs
  @NotNull
  public static List<Completion> sortError(@NotNull List<Completion>... completions)
  {
    List<Completion> list = new ArrayList<>();
    boolean[] errors = new boolean[completions.length];
    boolean allIsError = true;
    for (int i = 0; i < errors.length; i++)
    {
      boolean b = isErrorMessage(completions[i]);
      errors[i] = b;
      if (!b)
      {
        allIsError = false;
      }
    }
    if (allIsError)
    {
      for (int i = 0; i < errors.length; i++)
      {
        list.addAll(completions[i]);
      }
    }
    else
    {
      for (int i = 0; i < errors.length; i++)
      {
        if (!errors[i])
        {
          list.addAll(completions[i]);
        }
      }
    }

    return list.stream().distinct().collect(Collectors.toList());
  }
}
