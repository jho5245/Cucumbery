package com.jho5245.cucumbery.util.no_groups;

import com.google.common.base.Predicates;
import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectType;
import com.jho5245.cucumbery.util.itemlore.ItemLore;
import com.jho5245.cucumbery.util.itemlore.ItemLoreView;
import com.jho5245.cucumbery.util.josautil.KoreanUtils;
import com.jho5245.cucumbery.util.storage.component.ItemStackComponent;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig;
import com.jho5245.cucumbery.util.storage.no_groups.SoundPlay;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import io.papermc.paper.advancement.AdvancementDisplay.Frame;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.Action;
import net.kyori.adventure.text.event.HoverEvent.ShowItem;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class MessageUtil
{
  private static final Pattern PATTERN_N2S_RGB = Pattern.compile("(([RGBrgb]){1,3})(([0-9]){1,3})(,(([0-9]){1,3}))?(,(([0-9]){1,3}))?;");


  @NotNull
  public static String[] wrapWithQuote(@NotNull String[] args)
  {
    return wrapWithQuote(false, args);
  }

  @NotNull
  public static String[] wrapWithQuote(boolean forTabComplete, @NotNull String[] args)
  {
    String input = listToString(" ", args);
    input = forTabComplete ? PlaceHolderUtil.evalString(PlaceHolderUtil.placeholder(Bukkit.getConsoleSender(), input, null)) : input;
    if (input.equals("'") || input.equals("\""))
    {
      return new String[]{(ComponentUtil.serialize(Component.translatable("command.expected.separator")))};
    }
    List<String> a = new ArrayList<>();
    StringBuilder builder = new StringBuilder();
    boolean isInQuote = false;
    boolean isInDoubleQuote = false;
    for (int i = 0; i < input.length(); i++)
    {
      char c = input.charAt(i);
      final boolean b = i + 1 < input.length() && input.charAt(i + 1) != ' ';
      if (c == '\"' && !isInQuote)
      {
        if (isInDoubleQuote)
        {
          if (i + 1 < input.length() && input.charAt(i + 1) == '\"')
          {
            builder.append("\"");
            i++;
            continue;
          }
          if (b)
          {
            return new String[]{(ComponentUtil.serialize(Component.translatable("command.expected.separator")))};
          }
          if (!builder.isEmpty())
          {
            a.add(builder.toString());
            builder = new StringBuilder();
            isInDoubleQuote = false;
          }
        }
        else
        {
          isInDoubleQuote = true;
        }
      }
      else if (c == '\'' && !isInDoubleQuote)
      {
        if (isInQuote)
        {
          if (i + 1 < input.length() && input.charAt(i + 1) == '\'')
          {
            builder.append("'");
            i++;
            continue;
          }
          if (b)
          {
            return new String[]{(ComponentUtil.serialize(Component.translatable("command.expected.separator")))};
          }
          if (!builder.isEmpty())
          {
            a.add(builder.toString());
            builder = new StringBuilder();
            isInQuote = false;
          }
        }
        else
        {
          isInQuote = true;
        }
      }
      else if (c == ' ' && !isInQuote && !isInDoubleQuote)
      {
        if (!builder.isEmpty() && !builder.toString().equals(" "))
        {
          a.add(builder.toString());
        }
        builder = new StringBuilder();
      }
      else
      {
        builder.append(c);
      }
    }
    if (!builder.isEmpty())
    {
      if (isInDoubleQuote || isInQuote)
      {
        return new String[]{(ComponentUtil.serialize(Component.translatable("parsing.quote.expected.end")))};
      }
      a.add(builder.toString());
    }
    if ((forTabComplete && a.isEmpty()) || input.endsWith(" "))
    {
      a.add("");
    }
    return Method.listToArray(a);
  }

  @Deprecated
  @NotNull
  public static String[] wrapWithQuote2(boolean forTabComplete, @NotNull String[] args)
  {
    String input = listToString(" ", args);
    input = forTabComplete ? PlaceHolderUtil.evalString(PlaceHolderUtil.placeholder(Bukkit.getConsoleSender(), input, null)) : input;
    if (input.equals("'") || input.equals("\""))
    {
      return new String[]{"command.expected.separator"};
    }
    List<String> a = new ArrayList<>();
    StringBuilder builder = new StringBuilder();
    boolean isInQuote = false;
    boolean isInDoubleQuote = false;
    for (int i = 0; i < input.length(); i++)
    {
      char c = input.charAt(i);
      final boolean b = i + 1 < input.length() && input.charAt(i + 1) != ' ';
      if (c == '\"' && !isInQuote)
      {
        if (isInDoubleQuote)
        {
          if (i + 1 < input.length() && input.charAt(i + 1) == '\"')
          {
            builder.append("\"");
            i++;
            continue;
          }
          if (b)
          {
            return new String[]{"command.expected.separator"};
          }
          if (!builder.isEmpty())
          {
            a.add(builder.toString());
            builder = new StringBuilder();
            isInDoubleQuote = false;
          }
        }
        else
        {
          isInDoubleQuote = true;
        }
      }
      else if (c == '\'' && !isInDoubleQuote)
      {
        if (isInQuote)
        {
          if (i + 1 < input.length() && input.charAt(i + 1) == '\'')
          {
            builder.append("'");
            i++;
            continue;
          }
          if (b)
          {
            return new String[]{"command.expected.separator"};
          }
          if (!builder.isEmpty())
          {
            a.add(builder.toString());
            builder = new StringBuilder();
            isInQuote = false;
          }
        }
        else
        {
          isInQuote = true;
        }
      }
      else if (c == ' ' && !isInQuote && !isInDoubleQuote)
      {
        if (!builder.isEmpty() && !builder.toString().equals(" "))
        {
          a.add(builder.toString());
        }
        builder = new StringBuilder();
      }
      else
      {
        builder.append(c);
      }
    }
    if (!builder.isEmpty())
    {
      if (isInDoubleQuote || isInQuote)
      {
        return new String[]{"parsing.quote.expected.end"};
      }
      a.add(builder.toString());
    }
    if ((forTabComplete && a.isEmpty()) || input.endsWith(" "))
    {
      a.add("");
    }
    return Method.listToArray(a);
  }

  @NotNull
  public static String[] splitEscape(@NotNull String str, char token)
  {
    List<String> list = new ArrayList<>();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < str.length(); i++)
    {
      char c = str.charAt(i);
      if (c == token)
      {
        if (i + 1 < str.length() && str.charAt(i + 1) == token)
        {
          builder.append(token);
          i++;
          continue;
        }
        if (!builder.isEmpty())
        {
          list.add(builder.toString());
          builder = new StringBuilder();
        }
      }
      else
      {
        builder.append(c);
      }
    }
    list.add(builder.toString());
    return Method.listToArray(list);
  }

  /**
   * ????????? ????????? ????????? ???????????? ???????????????.
   *
   * @param msg ???????????? ?????? ??????
   * @return ????????? ?????????
   */
  @NotNull
  public static String listToString(@NotNull String... msg) // ????????? ?????? -> ????????? ??????
  {
    return MessageUtil.listToString("", msg);
  }

  /**
   * ????????? ????????? from ?????? to ????????? ????????? ????????? ???????????? ???????????????.
   *
   * @param from ????????? ??????
   * @param to   ????????? ???
   * @param msg  ???????????? ?????? ??????
   * @return ????????? ?????????
   */
  @NotNull
  public static String listToString(int from, int to, @NotNull String... msg) // ????????? ?????? -> ????????? ??????
  {
    return MessageUtil.listToString("", from, to, msg);
  }

  /**
   * ????????? ????????? ????????? ???????????? ???????????????. ??? ?????? ????????? ????????? ???????????????.
   *
   * @param token ?????? ????????? ??????
   * @param msg   ???????????? ?????? ??????
   * @return ????????? ?????????
   */
  @NotNull
  public static String listToString(@NotNull String token, @NotNull String... msg) // ????????? ?????? -> ????????? ??????(?????? ?????? ??????)
  {
    return MessageUtil.listToString(token, 0, msg.length, msg);
  }

  /**
   * ????????? ????????? from ?????? to ????????? ????????? ????????? ???????????? ???????????????. ??? ?????? ????????? ????????? ???????????????.
   * <p>
   * ??? : listToString(" ", 2, 4, args) ??? ??????, args[2] + " " + args[3] ??????
   * </p>
   *
   * @param token ?????? ????????? ??????
   * @param from  ????????? ?????? (??????)
   * @param to    ????????? ??? (?????????)
   * @param msg   ???????????? ?????? ??????
   * @return ????????? ?????????
   */
  @NotNull
  public static String listToString(@NotNull String token, int from, int to, @NotNull String... msg) // ????????? ?????? -> ????????? ??????(?????? ?????? ??????)
  {
    if (from > to)
    {
      throw new IllegalArgumentException("from is bigger than to");
    }
    if (msg.length == 0)
    {
      return "";
    }
    StringBuilder tmp = new StringBuilder();
    for (int i = from; i < to; i++)
    {
      tmp.append(msg[i]).append((i == to - 1) ? "" : token);
    }
    return tmp.toString();
  }

  /**
   * ??????????????? ????????? ???????????????.
   *
   * @param objects ????????????
   * @return ????????????
   */
  @NotNull
  public static Object[] as(Object... objects)
  {
    return objects;
  }

  /**
   * ?????????????????? ??????????????? ????????????(?????? ?????????) ????????? ?????? ??????
   */
  @NotNull
  private static Component translate(@NotNull Player player, @NotNull Component component)
  {
    HoverEvent<?> hoverEvent = component.hoverEvent();
    if (hoverEvent != null && hoverEvent.action() == Action.SHOW_ITEM)
    {
      ShowItem showItem = (ShowItem) hoverEvent.value();
      Material material = Material.valueOf(showItem.item().value().toUpperCase());
      ItemStack itemStack = new ItemStack(material, showItem.count());
      BinaryTagHolder binaryTagHolder = showItem.nbt();
      if (binaryTagHolder != null)
      {
        NBTItem nbtItem = new NBTItem(itemStack, true);
        nbtItem.mergeCompound(new NBTContainer(binaryTagHolder.string()));
      }
      if (material == Material.BUNDLE && "test".equals(new NBTItem(itemStack).getString("test")))
      {
        itemStack = ((BundleMeta) itemStack.getItemMeta()).getItems().get(0);
      }
      if (Method.usingLoreFeature(player))
      {
        ItemLore.setItemLore(itemStack, new ItemLoreView(player));
      }
      else
      {
        ItemLore.removeItemLore(itemStack);
      }
      component = component.hoverEvent(ItemStackComponent.itemStackComponent(itemStack).hoverEvent());
    }
    if (component instanceof TranslatableComponent translatableComponent)
    {
      List<Component> args = new ArrayList<>(translatableComponent.args());
      for (int i = 0; i < args.size(); i++)
      {
        args.set(i, translate(player, args.get(i)));
      }
      component = translatableComponent.args(args);
    }
    if (!component.children().isEmpty())
    {
      List<Component> children = new ArrayList<>(component.children());
      for (int i = 0; i < children.size(); i++)
      {
        children.set(i, translate(player, children.get(i)));
      }
      component = component.children(children);
    }
    return component;
  }

  /**
   * ???????????? ????????????.
   *
   * @param audience ???????????? ?????? ??????
   * @param objects  ?????? ?????????
   */
  @SuppressWarnings("unchecked")
  public static void sendMessage(@NotNull Object audience, @NotNull Object... objects)
  {
    if (Cucumbery.using_CommandAPI && audience instanceof NativeProxyCommandSender proxyCommandSender)
    {
      audience = proxyCommandSender.getCallee();
    }
    if (audience instanceof UUID uuid)
    {
      Entity entity = Bukkit.getEntity(uuid);
      if (entity != null)
      {
        audience = entity;
      }
    }
    if (audience instanceof Collection<?> list)
    {
      for (Object o : list)
      {
        MessageUtil.sendMessage(o, objects);
      }
    }
    if (audience instanceof Audience a)
    {
      Component message;
      if (objects.length == 1 && objects[0] instanceof Component component)
      {
        message = component;
        if (a instanceof Player player)
        {
          message = translate(player, message);
        }
      }
      else
      {
        if (a instanceof Player player)
        {
          message = ComponentUtil.create(player, objects);
        }
        else
        {
          message = ComponentUtil.create(objects);
        }
      }
      if (a instanceof ConsoleCommandSender)
      {
        message = ComponentUtil.create("#52ee52;[Cucumbery] ", message);
      }
      a.sendMessage(message);
      if (a instanceof Entity entity && CustomEffectManager.hasEffect(entity, CustomEffectType.CURSE_OF_BEANS))
      {
        a.sendMessage(message);
      }
    }
  }

  public static void sendMessage(@NotNull Object audience, @NotNull String key)
  {
    sendMessage(audience, (Prefix) null, key, true);
  }

  public static void sendMessage(@NotNull Object audience, @NotNull String key, @NotNull Object... args)
  {
    sendMessage(audience, null, key, args);
  }

  public static void sendMessage(@NotNull Object audience, @NotNull Prefix prefix, @NotNull String key)
  {
    sendMessage(audience, prefix, key, true);
  }

  /**
   * ???????????? ????????????.
   *
   * @param audience ???????????? ?????? ??????
   * @param prefix   ????????? ?????????
   * @param key      ????????? ??? ???
   * @param args     ???????????? ?????? ??????
   */
  public static void sendMessage(@NotNull Object audience, @Nullable Prefix prefix, @NotNull String key, @NotNull Object... args)
  {
    if (audience instanceof List<?> list)
    {
      for (Object o : list)
      {
        sendMessage(o, prefix, key, args);
      }
    }
    else
    {
      Player p = audience instanceof Player player ? player : null;
      Component component = ComponentUtil.translate(p, key, args);
      if (prefix != null)
      {
        component = ComponentUtil.create(prefix, component);
      }
      sendMessage(audience, component);
    }
  }

  public static void sendWarn(@NotNull Object audience, @NotNull String key)
  {
    sendWarn(audience, key, true);
  }

  public static void sendWarn(@NotNull Object audience, @NotNull String key, @NotNull Object... args)
  {
    if (Cucumbery.config.getBoolean("sound-const.warning-sound.enable"))
    {
      SoundPlay.playWarnSound(audience);
    }
    sendMessage(audience, Prefix.INFO_WARN, key, args);
  }

  public static void sendError(@NotNull Object audience, @NotNull String key)
  {
    sendError(audience, key, true);
  }

  public static void sendError(@NotNull Object audience, @NotNull String key, @NotNull Object... args)
  {
    if (Cucumbery.config.getBoolean("sound-const.error-sound.enable"))
    {
      SoundPlay.playErrorSound(audience);
    }
    sendMessage(audience, Prefix.INFO_ERROR, key, args);
  }

  public static void sendWarnOrError(boolean error, @NotNull Object audience, @NotNull String key)
  {
    sendWarnOrError(error, audience, key, true);
  }

  public static void sendWarnOrError(boolean error, @NotNull Object audience, @NotNull String key, @NotNull Object... args)
  {
    if (!error)
    {
      sendWarn(audience, key, args);
    }
    else
    {
      sendError(audience, key, args);
    }
  }

  public static void info(@NotNull Object audience, @NotNull String key)
  {
    info(audience, key, true);
  }

  public static void info(@NotNull Object audience, @NotNull String key, @NotNull Object... args)
  {
    sendMessage(audience, Prefix.INFO, key, args);
  }

  @SuppressWarnings("unchecked")
  public static void sendToast(@NotNull Object audience, @NotNull Component title, @NotNull Material type, @NotNull Frame frame)
  {
    if (audience instanceof Player player)
    {
      NamespacedKey namespacedKey = NamespacedKey.minecraft("z-cucumbery-toast-" + System.currentTimeMillis());
      new ToastMessage(namespacedKey, title, type, frame).showTo(player);
    }
    else if (audience instanceof Collection<?> collection && collection.stream().allMatch(Predicates.instanceOf(Player.class)::apply))
    {
      NamespacedKey namespacedKey = NamespacedKey.minecraft("z-cucumbery-toast-" + System.currentTimeMillis());
      new ToastMessage(namespacedKey, title, type, frame).showTo((Collection<? extends Player>) collection);
    }
  }

  /**
   * ????????? ?????????(?????? ???????????? ??????)??? ???????????????. commandSender??? ????????? ?????? ?????????????????? ???????????????.
   *
   * @param commandSender ?????? ???????????? ????????? ??????
   * @param exception     ????????? ???????????? ???????????? ?????? ??????
   * @param component     ????????? ?????????
   */
  public static void sendAdminMessage(@NotNull Object commandSender, @Nullable List<Permissible> exception, @NotNull Component component)
  {
    Collection<Permissible> collection = new ArrayList<>(Bukkit.getOnlinePlayers());
    if (exception != null)
    {
      collection.removeAll(exception);
    }
    collection.removeIf(c -> !c.hasPermission("minecraft.admin.command_feedback"));
    if (Cucumbery.using_CommandAPI && commandSender instanceof NativeProxyCommandSender proxyCommandSender)
    {
      commandSender = proxyCommandSender.getCallee();
    }
    if (commandSender instanceof Player player)
    {
      collection.remove(player);
    }
    Location location = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
    if (commandSender instanceof BlockCommandSender blockCommandSender)
    {
      location = blockCommandSender.getBlock().getLocation();
    }
    if (commandSender instanceof Entity entity)
    {
      location = entity.getLocation();
    }
    if (Boolean.TRUE.equals(location.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) &&
            (!(commandSender instanceof BlockCommandSender blockCommandSender) || Boolean.TRUE.equals(blockCommandSender.getBlock().getLocation().getWorld().getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT))))
    {
      Component message = setAdminMessage(ComponentUtil.translate("chat.type.admin", commandSender, component));
      sendMessage(collection, message);
      if (!(commandSender instanceof ConsoleCommandSender))
      {
        String worldName = location.getWorld().getName();
        int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
        message = message.append(ComponentUtil.create("&7 - " + worldName + ", " + x + ", " + y + ", " + z));
        consoleSendMessage(message);
      }
    }
  }

  public static void sendAdminMessage(@NotNull Object commandSender, @Nullable List<Permissible> exception, @NotNull String key)
  {
    sendAdminMessage(commandSender, exception, ComponentUtil.translate(key));
  }

  public static void sendAdminMessage(@NotNull Object commandSender, @Nullable List<Permissible> exception, @NotNull String key, @NotNull Object... args)
  {
    sendAdminMessage(commandSender, exception, ComponentUtil.translate(key, args));
  }

  public static void sendAdminMessage(@NotNull Object commandSender, @NotNull String key)
  {
    sendAdminMessage(commandSender, null, ComponentUtil.translate(key));
  }

  public static void sendAdminMessage(@NotNull Object commandSender, @NotNull String key, @NotNull Object... args)
  {
    sendAdminMessage(commandSender, null, ComponentUtil.translate(key, args));
  }

  @NotNull
  private static Component setAdminMessage(@NotNull Component component)
  {
    component = component.decoration(TextDecoration.ITALIC, TextDecoration.State.TRUE);
    component = component.color(NamedTextColor.GRAY);
    List<Component> children = new ArrayList<>(component.children());
    for (int i = 0; i < children.size(); i++)
    {
      Component child = setAdminMessage(children.get(i));
      children.set(i, child);
    }
    if (component instanceof TranslatableComponent translatableComponent)
    {
      List<Component> args = new ArrayList<>(translatableComponent.args());
      for (int i = 0; i < args.size(); i++)
      {
        Component arg = setAdminMessage(args.get(i));
        args.set(i, arg);
      }
      component = translatableComponent.args(args);
    }
    return component.children(children);
  }

  /**
   * ????????? ???????????? ????????????.
   *
   * @param objects ?????? ?????????
   */
  public static void consoleSendMessage(@NotNull Object... objects)
  {
    sendMessage(Bukkit.getServer().getConsoleSender(), objects);
  }

  public static void consoleSendMessage(@NotNull String key)
  {
    consoleSendMessage(null, key);
  }

  public static void consoleSendMessage(@NotNull String key, @NotNull Object... args)
  {
    consoleSendMessage(null, key, args);
  }

  public static void consoleSendMessage(@Nullable Prefix prefix, @NotNull String key)
  {
    consoleSendMessage(prefix, key, true);
  }

  public static void consoleSendMessage(@Nullable Prefix prefix, @NotNull String key, @NotNull Object... args)
  {
    sendMessage(Bukkit.getConsoleSender(), prefix, key, args);
  }

  /**
   * ?????? ?????????????????? ???????????? ????????????.
   *
   * @param objects ?????? ?????????
   */
  public static void broadcastPlayer(@NotNull Object... objects)
  {
    for (Player player : Bukkit.getOnlinePlayers())
    {
      MessageUtil.sendMessage(player, objects);
    }
  }

  public static void broadcastPlayer(@NotNull String key)
  {
    broadcastPlayer(null, key);
  }

  public static void broadcastPlayer(@NotNull String key, @NotNull Object... args)
  {
    broadcastPlayer(null, key, args);
  }

  public static void broadcastPlayer(@Nullable Prefix prefix, @NotNull String key)
  {
    broadcastPlayer(prefix, key, true);
  }

  public static void broadcastPlayer(@Nullable Prefix prefix, @NotNull String key, @NotNull Object... args)
  {
    for (Player player : Bukkit.getOnlinePlayers())
    {
      sendMessage(player, prefix, key, args);
    }
  }

  /**
   * ????????? ?????? ?????????????????? ???????????? ????????????.
   *
   * @param objects ?????? ?????????
   */
  public static void broadcast(@NotNull Object... objects)
  {
    consoleSendMessage(objects);
    broadcastPlayer(objects);
  }

  /**
   * ?????????????????? ????????? ???????????? ????????????.
   *
   * @param player  ????????? ???????????? ?????? ????????????
   * @param objects ?????????
   */
  public static void sendDebug(@NotNull Player player, @NotNull Object... objects)
  {
    if (CustomConfig.UserData.SHOW_PLUGIN_DEV_DEBUG_MESSAGE.getBoolean(player) || CustomEffectManager.hasEffect(player, CustomEffectType.DEBUG_WATCHER))
    {
      Object[] msg = new Object[objects.length + 1];
      msg[0] = Prefix.INFO_DEBUG;
      System.arraycopy(objects, 0, msg, 1, objects.length);
      MessageUtil.sendMessage(player, msg);
    }
  }

  /**
   * ?????? ?????????????????? ????????? ???????????? ????????????.
   *
   * @param objects ?????????
   */
  public static void broadcastDebug(@NotNull Object... objects)
  {
    for (Player player : Bukkit.getServer().getOnlinePlayers())
    {
      MessageUtil.sendDebug(player, objects);
    }
  }

  public static void sendWarn(@NotNull Object audience, @NotNull Object... objects)
  {
    if (Cucumbery.config.getBoolean("sound-const.warning-sound.enable"))
    {
      SoundPlay.playWarnSound(audience);
    }
    Object[] newObjects = new Object[objects.length + 1];
    newObjects[0] = Prefix.INFO_WARN;
    System.arraycopy(objects, 0, newObjects, 1, objects.length);
    sendMessage(audience, newObjects);
  }

  public static void sendError(@NotNull Object audience, @NotNull Object... objects)
  {
    if (Cucumbery.config.getBoolean("sound-const.error-sound.enable"))
    {
      SoundPlay.playErrorSound(audience);
    }
    Object[] newObjects = new Object[objects.length + 1];
    newObjects[0] = Prefix.INFO_ERROR;
    System.arraycopy(objects, 0, newObjects, 1, objects.length);
    sendMessage(audience, newObjects);
  }

  public static void sendWarnOrError(boolean error, @NotNull Object audience, @NotNull Object... objects)
  {
    if (!error)
    {
      sendWarn(audience, objects);
    }
    else
    {
      sendError(audience, objects);
    }
  }

  public static void info(@NotNull Object audience, @NotNull Object... objects)
  {
    Object[] newObjects = new Object[objects.length + 1];
    newObjects[0] = Prefix.INFO;
    System.arraycopy(objects, 0, newObjects, 1, objects.length);
    sendMessage(audience, newObjects);
  }

  public static void commandInfo(@NotNull Object audience, @NotNull String label, @NotNull String usage)
  {
    info(audience, "/" + label + " " + usage);
  }

  public static boolean checkQuoteIsValidInArgs(@NotNull CommandSender sender, @NotNull String[] args)
  {
    return checkQuoteIsValidInArgs(sender, args, false);
  }

  public static boolean checkQuoteIsValidInArgs(@NotNull CommandSender sender, @NotNull String[] args, boolean forTabComplete)
  {
    if (args.length == 1 && args[0].equals(ComponentUtil.serialize(Component.translatable("parsing.quote.expected.end"))))
    {
      if (!forTabComplete)
      {
        MessageUtil.sendError(sender, Component.translatable("parsing.quote.expected.end"));
      }
      return false;
    }
    if (args.length == 1 && args[0].equals(ComponentUtil.serialize(Component.translatable("command.expected.separator"))))
    {
      if (!forTabComplete)
      {
        MessageUtil.sendError(sender, Component.translatable("command.expected.separator"));
      }
      return false;
    }
    return true;
  }

  @Deprecated
  public static boolean checkQuoteIsValidInArgs2(@NotNull CommandSender sender, @NotNull String[] args)
  {
    return (args.length != 1 || !args[0].equals("parsing.quote.expected.end")) && (args.length != 1 || !args[0].equals("command.expected.separator"));
  }

  /**
   * ???????????? ?????? ?????? (??)??? ???????????????.
   *
   * @param msg ?????? ????????? ????????? ?????????
   * @return ?????? ????????? ????????? ?????????
   */
  @NotNull
  public static String stripColor(@NotNull String msg) // ????????? ?????? ??????
  {
    return msg.replaceAll("??(.)", "");
  }

  /**
   * ???????????? ?????? ?????? ?????? (&)??? ?????? ?????? (??)??? ???????????????.
   *
   * @param msg ?????? ????????? ????????? ?????????
   * @return ?????? ????????? ????????? ?????????
   */
  @NotNull
  public static String n2s(@NotNull String msg) // ?????? ?????? ??????
  {
    return MessageUtil.n2s(msg, N2SType.NORMAL);
  }

  /**
   * ???????????? ?????? ?????? ?????? (&)??? ?????? ?????? (??)??? ???????????????.
   *
   * @param input ?????? ????????? ????????? ?????????
   * @param type  SPECIAL??? ?????? && ??? ????????? ?????? ???????????? ????????? ????? ???????????????.
   * @return ?????? ????????? ????????? ?????????
   */
  @NotNull
  public static String n2s(@NotNull String input, @NotNull N2SType type)
  {
    if (type == N2SType.SPECIAL_ONLY)
    {
      return input.replace("&&", "??");
    }
    String tmp = MessageUtil.n2sHex(ChatColor.translateAlternateColorCodes('&', input));
    tmp = ColorUtil.chatEffect(tmp);
    tmp = tmp.replace("&p", "??p").replace("&q", "??q").replace("&i", "??i");
    return type == N2SType.SPECIAL ? tmp.replace("&&", "??") : tmp;
  }

  /**
   * #AAAAAA; #AAA; #AA; #A; ???????????? ?????? ??? ?????? 2???(#AA;, #A;)??? ?????? ?????? rgb100,100,100; rg100,100; rbg100,100,100; r100;
   *
   * @param input ??????????????? ????????? ?????????
   * @return ??????????????? ????????? ?????????
   */
  @NotNull
  private static String n2sHex(@NotNull String input)
  {
    // #ABCDEF; #ABCDEF
    input = input.replaceAll("#([a-fA-F0-9])([a-fA-F0-9])([a-fA-F0-9])([a-fA-F0-9])([a-fA-F0-9])([a-fA-F0-9]);", "??x??$1??$2??$3??$4??$5??$6");
    // #ABC; => #AABBCC
    input = input.replaceAll("#([a-fA-F0-9])([a-fA-F0-9])([a-fA-F0-9]);", "??x??$1??$1??$2??$2??$3??$3");
    // #AB; => #ABABAB
    input = input.replaceAll("#([a-fA-F0-9])([a-fA-F0-9]);", "??x??$1??$2??$1??$2??$1??$2");
    // #A; => #AAAAAA
    input = input.replaceAll("#([a-fA-F0-9]);", "??x??$1??$1??$1??$1??$1??$1");
    // rgbNNN,NNN,NNN; => #XXXXXX
    Matcher matcher_rgb = PATTERN_N2S_RGB.matcher(input);
    ArrayList<ArrayList<String>> matches_rgb = new ArrayList<>();
    while (matcher_rgb.find())
    {
      ArrayList<String> m = new ArrayList<>();
      m.add(matcher_rgb.group(0));
      m.add(matcher_rgb.group(1));
      m.add(matcher_rgb.group(3));
      m.add(matcher_rgb.group(6));
      m.add(matcher_rgb.group(9));
      matches_rgb.add(m);
    }
    for (ArrayList<String> m : matches_rgb)
    {
      String xc = "RXGXBX";
      for (int n = 0; n < 3; n++)
      {
        if (n > m.get(1).length() - 1)
        {
          break;
        }
        if (m.get(n + 2) == null)
        {
          break;
        }
        char c = m.get(1).toLowerCase().charAt(n);
        String cHex = new StringBuffer(Integer.toHexString(0x100 | Integer.parseInt(m.get(n + 2))).substring(1)).insert(1, "??").insert(0, "??").toString();
        switch (c)
        {
          case 'r' -> xc = xc.replace("RX", cHex);
          case 'g' -> xc = xc.replace("GX", cHex);
          case 'b' -> xc = xc.replace("BX", cHex);
        }
      }
      xc = xc.replaceAll("[RGB]X", "??0??0");
      input = input.replaceAll(m.get(0), "??x" + xc);
    }
    return input;
  }

  @NotNull
  public static String s2a(@NotNull String input)
  {

    /// ESC[ 38;2;r???;???g???;???b??? m
    Matcher matcher_hex = Pattern.compile("??x??([0-9a-fA-F])??([0-9a-fA-F])??([0-9a-fA-F])??([0-9a-fA-F])??([0-9a-fA-F])??([0-9a-fA-F])").matcher(input);
    ArrayList<ArrayList<String>> matches_hex = new ArrayList<>();
    while (matcher_hex.find())
    {
      ArrayList<String> m = new ArrayList<>();
      m.add(matcher_hex.group(0));
      m.add(matcher_hex.group(1));
      m.add(matcher_hex.group(2));
      m.add(matcher_hex.group(3));
      m.add(matcher_hex.group(4));
      m.add(matcher_hex.group(5));
      m.add(matcher_hex.group(6));
      matches_hex.add(m);
    }
    for (ArrayList<String> m : matches_hex)
    {
      String xc = "\u001b[38;2;R;G;Bm";
      xc = xc.replace("R", String.valueOf(Integer.parseInt(m.get(1) + m.get(2) + "", 16)));
      xc = xc.replace("G", String.valueOf(Integer.parseInt(m.get(3) + m.get(4) + "", 16)));
      xc = xc.replace("B", String.valueOf(Integer.parseInt(m.get(5) + m.get(6) + "", 16)));
      input = input.replace(m.get(0), xc);
    }

    input = input.replace("??0", "\u001b[30m");
    input = input.replace("??4", "\u001b[31m");
    input = input.replace("??2", "\u001b[32m");
    input = input.replace("??6", "\u001b[33m");
    input = input.replace("??1", "\u001b[34m");
    input = input.replace("??5", "\u001b[35m");
    input = input.replace("??3", "\u001b[36m");
    input = input.replace("??7", "\u001b[37m");
    input = input.replace("??8", "\u001b[90m");
    input = input.replace("??c", "\u001b[91m");
    input = input.replace("??a", "\u001b[92m");
    input = input.replace("??e", "\u001b[93m");
    input = input.replace("??9", "\u001b[94m");
    input = input.replace("??d", "\u001b[95m");
    input = input.replace("??b", "\u001b[96m");
    input = input.replace("??f", "\u001b[97m");

    input = input.replace("??l", "\u001b[1m");
    input = input.replace("??m", "\u001b[2m");
    input = input.replace("??n", "\u001b[4m");
    input = input.replace("??o", "\u001b[3m");
    input = input.replace("??r", "\u001b[0m");
    input = input.replace("??k", "\u001b[5m");
    return input + "\u001b[0m";
  }

  @NotNull
  public static String n2a(@NotNull String input)
  {
    return MessageUtil.s2a(MessageUtil.n2s(input));
  }

  /**
   * ????????? ???????????? ??????????????? ???????????? ???????????????.
   *
   * @param input ??????????????? ????????? ?????????
   * @return ??????????????? ????????? ?????????
   */
  @NotNull
  public static String switchCase(@NotNull String input)
  {
    char[] chars = input.toCharArray();
    for (int i = 0; i < chars.length; i++)
    {
      char c = chars[i];
      if (65 <= c && 97 > c)
      {
        chars[i] = (char) (c + 32);
      }
      else if (96 < c && 123 > c)
      {
        chars[i] = (char) (c - 32);
      }
    }
    return new String(chars);
  }

  public static void noArg(@NotNull Object audience, @NotNull Prefix reason, @NotNull String arg)
  {
    sendError(audience, "%s (%s)", reason, arg);
  }

  public static void shortArg(@NotNull Object audience, int input, @NotNull String[] args)
  {
    sendError(audience, Prefix.ARGS_SHORT + " (&e" + args.length + "???&r ??????, ?????? &e" + input + "???&r)");
  }

  public static void longArg(@NotNull Object audience, int input, @NotNull String[] args)
  {
    sendError(audience, Prefix.ARGS_LONG + " (&e" + args.length + "???&r ??????, ?????? &e" + input + "???&r)");
  }

  public static void wrongArg(@NotNull Object audience, int input, @NotNull String[] args)
  {
    sendError(audience, Prefix.ARGS_WRONG + " (&e" + input + "?????? &r?????? : &e" + args[input - 1] + "&r)");
  }

  public static void wrongBool(@NotNull Object audience, int input, @NotNull String[] args)
  {
    sendError(audience, "????????? ????????????. '&etrue&r' ?????? '&efalse&r'??? ??????????????? &e" + input + "???&r??? ????????? '&e" + args[input - 1] + "&r'" + getFinalConsonant(args[input - 1], ConsonantType.??????) + " ?????????????????????");
  }

  public static void sendTitle(@NotNull Object player, @Nullable Object title, @Nullable Object subTitle, int fadeIn, int stay, int fadeOut)
  {
    @SuppressWarnings("all")
    Title t = Title.title(title != null ? ComponentUtil.create(title) : Component.empty(), subTitle != null ? ComponentUtil.create(subTitle) : Component.empty(),
            Title.Times.of(Duration.ofMillis(fadeIn * 50L), Duration.ofMillis(stay * 50L), Duration.ofMillis(fadeOut * 50L)));
    if (player instanceof Audience audience)
    {
      audience.showTitle(t);
    }

    if (player instanceof Collection<?> collection)
    {
      for (Object o : collection)
      {
        if (o instanceof Audience audience)
        {
          audience.showTitle(t);
        }
      }
    }
  }

  public static void sendTitle(@NotNull Object player, @Nullable Object[] title, @Nullable Object[] subTitle, int fadeIn, int stay, int fadeOut)
  {
    @SuppressWarnings("all")
    Title t = Title.title(ComponentUtil.stripEvent(ComponentUtil.create(title)), subTitle != null ? ComponentUtil.stripEvent(ComponentUtil.create(subTitle)) : Component.empty(),
            Title.Times.of(Duration.ofMillis(fadeIn * 50L), Duration.ofMillis(stay * 50L), Duration.ofMillis(fadeOut * 50L)));
    if (player instanceof Audience audience)
    {
      audience.showTitle(t);
    }
    if (player instanceof Collection<?> collection)
    {
      for (Object o : collection)
      {
        if (o instanceof Audience audience)
        {
          audience.showTitle(t);
        }
      }
    }
  }

  public static void sendActionBar(@NotNull Object player, @NotNull Object... objects)
  {
    if (player instanceof Collection<?> collection)
    {
      for (Object o : collection)
      {
        sendActionBar(o, objects);
      }
    }
    if (player instanceof Audience audience)
    {
      audience.sendActionBar(ComponentUtil.stripEvent(ComponentUtil.create(objects)));
    }
  }

  public static boolean checkNumberSize(CommandSender sender, long value, long min, long max)
  {
    return checkNumberSize(sender, value, min, max, false, false, true);
  }

  public static boolean checkNumberSize(CommandSender sender, long value, long min, long max, boolean excludesMin, boolean excludesMax, boolean notice)
  {
    String valueString = "&e" + Constant.Sosu15.format(value) + "&r";
    valueString += getFinalConsonant(valueString, ConsonantType.??????);
    if (excludesMin && value <= min)
    {
      if (notice)
      {
        sendError(sender, "????????? &e" + Constant.Sosu15.format(min) + "&r ???????????? ?????????, " + valueString + " ????????????");
      }
      return false;
    }
    else if (!excludesMin && value < min)
    {
      if (notice)
      {
        sendError(sender, "????????? &e" + Constant.Sosu15.format(min) + "&r ??????????????? ?????????, " + valueString + " ????????????");
      }
      return false;
    }
    else if (excludesMax && value >= max)
    {
      if (notice)
      {
        sendError(sender, "????????? &e" + Constant.Sosu15.format(max) + "&r ??????????????? ?????????, " + valueString + " ????????????");
      }
      return false;
    }
    else if (!excludesMax && value > max)
    {
      if (notice)
      {
        sendError(sender, "????????? &e" + Constant.Sosu15.format(max) + "&r ???????????? ?????????, " + valueString + " ????????????");
      }
      return false;
    }
    return true;
  }

  public static boolean checkNumberSize(@Nullable CommandSender sender, double value, double min, double max)
  {
    return checkNumberSize(sender, value, min, max, false, false, true);
  }

  public static boolean checkNumberSize(@Nullable CommandSender sender, double value, double min, double max, boolean notice)
  {
    return checkNumberSize(sender, value, min, max, false, false, notice);
  }

  public static boolean checkNumberSize(@Nullable CommandSender sender, double value, double min, double max, boolean excludessMin, boolean excludessMax)
  {
    return checkNumberSize(sender, value, min, max, excludessMin, excludessMax, true);
  }

  public static boolean checkNumberSize(@Nullable CommandSender sender, double value, double min, double max, boolean excludessMin, boolean excludessMax, boolean notice)
  {
    String valueString = "&e" + Constant.Sosu15.format(value) + "&r";
    valueString += getFinalConsonant(valueString, ConsonantType.??????);
    if (excludessMin && value <= min)
    {
      if (notice)
      {
        if (sender != null)
        {
          sendError(sender, "????????? &e" + Constant.Sosu15.format(min) + "&r ???????????? ?????????, " + valueString + " ????????????");
        }
      }
      return false;
    }
    else if (!excludessMin && value < min)
    {
      if (notice)
      {
        if (sender != null)
        {
          sendError(sender, "????????? &e" + Constant.Sosu15.format(min) + "&r ??????????????? ?????????, " + valueString + " ????????????");
        }
      }
      return false;
    }
    else if (excludessMax && value >= max)
    {
      if (notice)
      {
        if (sender != null)
        {
          sendError(sender, "????????? &e" + Constant.Sosu15.format(max) + "&r ??????????????? ?????????, " + valueString + " ????????????");
        }
      }
      return false;
    }
    else if (!excludessMax && value > max)
    {
      if (notice)
      {
        if (sender != null)
        {
          sendError(sender, "????????? &e" + Constant.Sosu15.format(max) + "&r ???????????? ?????????, " + valueString + " ????????????");
        }
      }
      return false;
    }
    return true;
  }

  @NotNull
  public static String getFinalConsonant(@Nullable String word, @NotNull ConsonantType type)
  {
    try
    {
      if (word == null || word.length() < 1)
      {
        return type.toString();
      }
      word = word.replace(" ", "");
      word = stripColor(n2s(word));
      word = word.replaceAll("[&%!?\"'\\\\$_,\\-???????????????????????????????????????????????????????????????+]", "???");
      word = word.replaceAll("[.#@??????????????????????????????????????????????????????]", "???");
      word = word.replaceAll("[*???~]", "???");
      word = word.replace("'", "").replace("\"", "");
      char c;
      boolean b = true;
      String test = KoreanUtils.format("%s???", word);
      if (test.endsWith(")"))
      {
        throw new Exception();
      }
      if (test.endsWith("???"))
      {
        test = KoreanUtils.format("%s???", word);
        if (test.endsWith("??????"))
        {
          c = '???';
        }
        else
        {
          c = '???';
        }
      }
      else
      {
        c = '???';
        b = false;
      }
      switch (type)
      {
        case ??????, ??????, ???, ??? -> {
          if (b)
          {
            return "???";
          }
          return "???";
        }
        case ?????? -> {
          if (b && ((c - 44032) % (21 * 28)) % 28 != 8)
          {
            return "??????";
          }
          return "???";
        }
        case ??????, ??????, ???, ??? -> {
          if (b)
          {
            return "???";
          }
          return "???";
        }
        case ??????, ??????, ???, ??? -> {
          if (b)
          {
            return "???";
          }
          return "???";
        }
        case ??????, ??????, ???, ??? -> {
          if (b)
          {
            return "???";
          }
          return "???";
        }
        case ?????? -> {
          if (b)
          {
            return "??????";
          }
          return "???";
        }
      }
      return type.toString();
    }
    catch (Exception e)
    {
      return type.toString();
    }
  }

  public static boolean isInteger(CommandSender sender, String a, boolean notice)
  {
    try
    {
      try
      {
        Double.parseDouble(a);
      }
      catch (Exception e)
      {
        if (notice)
        {
          MessageUtil.noArg(sender, Prefix.ONLY_NUMBER, a);
        }
        return false;
      }
      Integer.parseInt(a);
      return true;
    }
    catch (Exception e)
    {
      if (notice)
      {
        MessageUtil.noArg(sender, Prefix.ONLY_INTEGER, a);
      }
      return false;
    }
  }

  public static boolean isLong(CommandSender sender, String a, boolean notice)
  {
    try
    {
      try
      {
        Double.parseDouble(a);
      }
      catch (Exception e)
      {
        if (notice)
        {
          MessageUtil.noArg(sender, Prefix.ONLY_NUMBER, a);
        }
        return false;
      }
      Long.parseLong(a);
      return true;
    }
    catch (Exception e)
    {
      if (notice)
      {
        MessageUtil.noArg(sender, Prefix.ONLY_INTEGER, a);
      }
      return false;
    }
  }

  public static boolean isDouble(@Nullable CommandSender sender, @NotNull String a, boolean notice)
  {
    try
    {
      double d = Double.parseDouble(a);
      if (Double.isNaN(d) || Double.isInfinite(d))
      {
        throw new NumberFormatException("Double cannot be NaN or Infinity!");
      }
      return true;
    }
    catch (Exception e)
    {
      if (notice)
      {
        if (sender != null)
        {
          MessageUtil.noArg(sender, Prefix.ONLY_NUMBER, a);
        }
      }
      return false;
    }
  }

  public static boolean isBoolean(@NotNull CommandSender sender, @NotNull String[] args, int input, boolean notice)
  {
    // ???????????? ????????? ???????????? ?????? boolean??? false??? ????????? boolean???
    if (input > args.length)
    {
      return true;
    }
    String arg = args[input - 1];
    if (!arg.equals("true") && !arg.equals("false"))
    {
      if (notice)
      {
        MessageUtil.wrongBool(sender, input, args);
      }
      return false;
    }
    return true;
  }

  /**
   * ????????? ????????? ????????? ?????? ?????? ????????? ?????? ????????? ????????? ???????????? ???????????????.
   *
   * @param time ?????????
   * @return ?????? ????????? ?????? ????????? ????????? ????????? (?????? ?????? : n??? n??? n???, ?????? ?????? : n??? n??? n???)
   */
  @NotNull
  public static String periodRealTimeAndGameTime(long time)
  {
    return MessageUtil.n2s("&2?????? ?????? : &a" + Method.timeFormatMilli(time * 50L) + "&r, &3?????? ?????? : &b" + Method.timeFormatMilli(time * 3600L));
  }

  /**
   * ?????? ????????? ??????????????? ??? ??1??2??3 ?????? ????????? ???????????? ?????? ????????? ??????????????? ?????? ???????????? ?????? ??????
   *
   * @param str ?????????
   * @return ?????????
   */
  @NotNull
  public static String stringEncode(@NotNull String str)
  {
    return str.replace("0", "???").replace("1", "???").replace("2", "???").replace("3", "???").replace("4", "???").replace("5", "???").replace("6", "???").replace("7", "???").replace("8", "???").replace("9", "???")
            .replace("A", "???").replace("B", "???").replace("C", "???").replace("D", "???").replace("E", "???").replace("F", "???").replace("K", "???").replace("L", "???").replace("M", "???").replace("N", "???")
            .replace("O", "???").replace("R", "???").replace("X", "???").replace("a", "???").replace("b", "???").replace("c", "???").replace("d", "???").replace("e", "???").replace("f", "???").replace("k", "???")
            .replace("l", "???").replace("m", "???").replace("n", "???").replace("o", "???").replace("r", "???").replace("x", "???");
  }

  /**
   * ??????????????? ????????? ???????????? ?????? ??????(??1??2??3)??? ????????? ??????
   *
   * @param str ?????????
   * @return ?????????
   */
  @NotNull
  public static String stringDecode(@NotNull String str)
  {
    return str.replace("???", "0").replace("???", "1").replace("???", "2").replace("???", "3").replace("???", "4").replace("???", "5").replace("???", "6").replace("???", "7").replace("???", "8").replace("???", "9")
            .replace("???", "A").replace("???", "B").replace("???", "C").replace("???", "D").replace("???", "E").replace("???", "F").replace("???", "K").replace("???", "L").replace("???", "M").replace("???", "N")
            .replace("???", "O").replace("???", "R").replace("???", "X").replace("???", "a").replace("???", "b").replace("???", "c").replace("???", "d").replace("???", "e").replace("???", "f").replace("???", "k")
            .replace("???", "l").replace("???", "m").replace("???", "n").replace("???", "o").replace("???", "r").replace("???", "x");
  }

  @NotNull
  public static Component boldify(@NotNull Component component)
  {
    List<Component> children = new ArrayList<>(component.children());
    for (int i = 0; i < children.size(); i++)
    {
      Component child = children.get(i);
      children.set(i, boldify(child));
    }
    component = component.decoration(TextDecoration.BOLD, State.TRUE).children(children);
    return component;
  }

  public enum N2SType // ?????? ?????? ?????? ??????
  {
    NORMAL,
    SPECIAL,
    SPECIAL_ONLY
  }

  @SuppressWarnings({"all"})
  public enum ConsonantType
  {
    ??????("???(???)"),
    ??????_2("(???)???"),
    ??????("???(???)"),
    ??????_2("(???)???"),
    ???("???(???)"),
    ???("???(???)"),
    ??????("???(???)"),
    ??????_2("(???)???"),
    ??????("???(???)"),
    ??????_2("(???)???"),
    ???("???(???)"),
    ???("???(???)"),
    ??????("???(???)"),
    ??????_2("(???)???"),
    ??????("???(???)"),
    ??????_2("(???)???"),
    ???("???(???)"),
    ???("???(???)"),
    ??????("???(???)"),
    ??????_2("(???)???"),
    ??????("???(???)"),
    ??????_2("(???)???"),
    ???("???(???)"),
    ???("???(???)"),
    ??????("(???)???"),
    ??????_2("???(???)"),
    ??????("(???)???"),
    ??????_2("???(???)");

    private final String a;

    ConsonantType(String a)
    {
      this.a = a;
    }

    @Override
    public String toString()
    {
      return a;
    }
  }
}
