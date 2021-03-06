package com.jho5245.cucumbery.listeners.player.no_groups;

import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.custom.customeffect.CustomEffect;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectType;
import com.jho5245.cucumbery.custom.customeffect.children.group.ItemStackCustomEffect;
import com.jho5245.cucumbery.events.entity.EntityCustomEffectRemoveEvent.RemoveReason;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.no_groups.Method;
import com.jho5245.cucumbery.util.no_groups.PlaceHolderUtil;
import com.jho5245.cucumbery.util.storage.component.ItemStackComponent;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.component.util.sendercomponent.SenderComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Permission;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import com.jho5245.cucumbery.util.storage.data.Variable;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig.UserData;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import com.jho5245.cucumbery.util.storage.no_groups.SoundPlay;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public class PlayerChat implements Listener
{
  @EventHandler
  public void onPlayerChat(AsyncChatEvent event)
  {
    if (event.isCancelled())
    {
      return;
    }
    Player player = event.getPlayer();
    if (CustomEffectManager.hasEffect(player, CustomEffectType.MUTE))
    {
      event.setCancelled(true);
      MessageUtil.sendMessage(player, Prefix.INFO, "????????? ??? ??? ?????? ???????????????");
      return;
    }
    UUID uuid = player.getUniqueId();
    String message = ComponentUtil.serialize(event.message());
    if (message.startsWith("\\cmd "))
    {
      try
      {
        String cmd = message.substring(5);
        Bukkit.getServer().getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> player.performCommand(cmd), 0L);
        if (UserData.LISTEN_COMMAND.getBoolean(player.getUniqueId()))
        {
          SoundPlay.playSound(player, Sound.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS);
        }
        event.setCancelled(true);
        return;
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    if (event.isCancelled())
    {
      return;
    }
    switch (message.toLowerCase())
    {
      case "cucumbery-????????????" -> {
        event.setCancelled(true);
        Bukkit.getScheduler().callSyncMethod(Cucumbery.getPlugin(), () -> Bukkit.getServer().dispatchCommand(player, "?????? quit"));
        return;
      }
      case "cucumbery-????????????" -> {
        event.setCancelled(true);
        Bukkit.getScheduler().callSyncMethod(Cucumbery.getPlugin(), () -> Bukkit.getServer().dispatchCommand(player, "?????? ??????????????????"));
        return;
      }
      case "cucumbery-???????????????" -> {
        event.setCancelled(true);
        Bukkit.getScheduler().callSyncMethod(Cucumbery.getPlugin(), () -> Bukkit.getServer().dispatchCommand(player, "?????? ?????????????????????"));
        return;
      }
      case "cucumbery-??????" -> {
        event.setCancelled(true);
        Bukkit.getScheduler().callSyncMethod(Cucumbery.getPlugin(), () -> Bukkit.getServer().dispatchCommand(player, "?????? realstart"));
        return;
      }
      case "cucumbery-????????????????????????" -> {
        event.setCancelled(true);
        Bukkit.getScheduler().callSyncMethod(Cucumbery.getPlugin(), () -> Bukkit.getServer().dispatchCommand(player, "?????? ????????????????????????"));
        return;
      }
      case "cucumbery-???????????????????????????" -> {
        event.setCancelled(true);
        Bukkit.getScheduler().callSyncMethod(Cucumbery.getPlugin(), () -> Bukkit.getServer().dispatchCommand(player, "?????? ???????????????????????????"));
        return;
      }
      case Constant.DROP_UNTRADABLE_ITEM -> {
        if (CustomEffectManager.hasEffect(player, CustomEffectType.NOTIFY_NO_TRADE_ITEM_DROP))
        {
          CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectType.NOTIFY_NO_TRADE_ITEM_DROP);
          if (customEffect instanceof ItemStackCustomEffect itemStackCustomEffect)
          {
            ItemStack itemStack = itemStackCustomEffect.getItemStack();
            Component itemComponent = ItemStackComponent.itemStackComponent(itemStack, Constant.THE_COLOR);
            if (ItemStackUtil.countItem(player.getInventory(), itemStack) == 0)
            {
              MessageUtil.sendWarn(player, "??????????????? %s???(???) ????????? ?????? ????????? ??? ???????????????", itemComponent);
              Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () ->
                      CustomEffectManager.removeEffect(player, CustomEffectType.NOTIFY_NO_TRADE_ITEM_DROP, RemoveReason.TIME_OUT), 0L);
              event.setCancelled(true);
              return;
            }
            player.getInventory().removeItem(itemStack);
            MessageUtil.info(player, "%s???(???) ?????????????????? ?????????????????????", itemComponent);
            Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () ->
                    CustomEffectManager.removeEffect(player, CustomEffectType.NOTIFY_NO_TRADE_ITEM_DROP), 0L);
          }
        }
        event.setCancelled(true);
        return;
      }
    }
    if (!Cucumbery.config.getBoolean("grant-default-permission-to-players") && !Permission.EVENT_CHAT.has(player))
    {
      event.setCancelled(true);
      if (!Permission.EVENT_ERROR_HIDE.has(player) && !Variable.playerChatAlertCooldown.contains(uuid))
      {
        Variable.playerChatAlertCooldown.add(uuid);
        MessageUtil.sendTitle(player, "&c?????? ??????!", "&r????????? ??? ????????? ????????????", 5, 80, 15);
        SoundPlay.playSound(player, Constant.ERROR_SOUND);
        Bukkit.getServer().getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> Variable.playerChatAlertCooldown.remove(uuid), 100L);
      }
      return;
    }
    if (!Permission.EVENT2_ANTI_ALLPLAYER.has(player) && Constant.AllPlayer.CHAT.isEnabled())
    {
      event.setCancelled(true);
      if (!Variable.playerChatAlertCooldown.contains(uuid))
      {
        Variable.playerChatAlertCooldown.add(uuid);
        MessageUtil.sendMessage(player, Prefix.INFO_ALLPLAYER, "????????? ??? ??? ?????? ???????????????");
        SoundPlay.playSound(player, Constant.ERROR_SOUND);
        Bukkit.getServer().getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> Variable.playerChatAlertCooldown.remove(uuid), 100L);
      }
      return;
    }
    if (CustomEffectManager.hasEffect(player, CustomEffectType.COOLDOWN_CHAT))
    {
      event.setCancelled(true);
      MessageUtil.sendWarn(player, ComponentUtil.translate("?????? ????????? ???????????????. ????????? ???????????????."));
      return;
    }
    Location location = player.getLocation();
    if (!Permission.EVENT_CHAT_SPAM.has(player) && Cucumbery.config.getBoolean("no-spam.enable") && !Method.configContainsLocation(location, Cucumbery.config.getStringList("no-spam.no-worlds")))
    {
      int chatCooldown = Cucumbery.config.getInt("no-spam.cooldown-in-ticks");
      if (Variable.playerChatNoSpamAlertCooldown.contains(uuid))
      {
        event.setCancelled(true);
        MessageUtil.sendTitle(player, "&c?????? ??????!", "&r????????? &e" + Constant.Sosu2.format(chatCooldown / 20d) + "???&r ?????? ???????????????", 5, 60, 15);
        SoundPlay.playSound(player, Constant.ERROR_SOUND);
        return;
      }
      Variable.playerChatNoSpamAlertCooldown.add(uuid);
      Bukkit.getServer().getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> Variable.playerChatNoSpamAlertCooldown.remove(uuid), chatCooldown);
      if (Cucumbery.config.getBoolean("no-spam.same-message.enable"))
      {
        chatCooldown = Cucumbery.config.getInt("no-spam.same-message.cooldown-in-ticks");
        if (message.equals(Variable.playerChatSameMessageSpamAlertCooldown.get(uuid)))
        {
          event.setCancelled(true);
          MessageUtil.sendTitle(player, "&c?????? ??????!", "&r?????? ???????????? &e" + Constant.Sosu2.format(chatCooldown / 20d) + "???&r ????????? ????????? ??? ????????????", 5, 60, 15);
          SoundPlay.playSound(player, Constant.ERROR_SOUND);
          return;
        }
        Variable.playerChatSameMessageSpamAlertCooldown.put(uuid, message);
        Bukkit.getServer().getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> Variable.playerChatSameMessageSpamAlertCooldown.remove(uuid), chatCooldown);
      }
      if (chatCooldown > 0 && Cucumbery.config.getBoolean("no-spam.buff"))
      {
        CustomEffectManager.addEffect(player, new CustomEffect(CustomEffectType.COOLDOWN_CHAT, chatCooldown));
      }
    }
    if (CustomEffectManager.hasEffect(player, CustomEffectType.OUTSIDER))
    {
      int amplifier = CustomEffectManager.getEffect(player, CustomEffectType.OUTSIDER).getAmplifier() + 1;
      if (Math.random() * 100 < amplifier * 10)
      {
        event.setCancelled(true);
        return;
      }
    }
    if (Cucumbery.config.getBoolean("play-sound-on-chat"))
    {
      if (!Method.configContainsLocation(location, Cucumbery.config.getStringList("no-play-sound-on-chat-worlds")))
      {
        for (Player online : Bukkit.getServer().getOnlinePlayers())
        {
          online.playSound(online.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1F, 1F);
        }
      }
    }
    int chatRepeat = 1;
    if (CustomEffectManager.hasEffect(player, CustomEffectType.CURSE_OF_BEANS))
    {
      chatRepeat++;
    }
    if (CustomEffectManager.hasEffect(player, CustomEffectType.INSIDER))
    {
      chatRepeat *= Objects.requireNonNull(CustomEffectManager.getEffect(player, CustomEffectType.INSIDER)).getAmplifier() + 2;
    }
    if (chatRepeat > 1)
    {
      Audience audience = Audience.audience(event.viewers());
      for (int i = 1; i < chatRepeat; i++)
      {
        audience.sendMessage(event.renderer().render(player, player.displayName(), event.message(), Audience.audience(event.viewers())));
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void specialParse(AsyncChatEvent event)
  {
    Player player = event.getPlayer();
    Component senderComponent = SenderComponentUtil.senderComponent(player);
    player.displayName(player.displayName().hoverEvent(senderComponent.hoverEvent()));

    String message = ComponentUtil.serialize(event.message());
    if (message.contains("[i]"))
    {
      if (!ItemStackUtil.itemExists(player.getInventory().getItemInMainHand()))
      {
        event.setCancelled(true);
        MessageUtil.sendError(player, ComponentUtil.translate("?????? ???????????? ?????? ???????????? ?????? ????????? ????????? ???????????? ????????? ??? ????????????"));
        return;
      }
      else
      {
        if (CustomEffectManager.hasEffect(player, CustomEffectType.COOLDOWN_ITEM_MEGAPHONE))
        {
          event.setCancelled(true);
          MessageUtil.sendWarn(player, ComponentUtil.translate("?????? ????????? ???????????? ????????? ??? ????????????"));
          return;
        }
        if (!Permission.EVENT_CHAT_SPAM.has(player) && Cucumbery.config.getBoolean("no-spam.item-megaphone.enable"))
        {
          int cooldown = Cucumbery.config.getInt("no-spam.item-megaphone.cooldown-in-ticks");
          if (cooldown > 0)
          {
            Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () ->
                    CustomEffectManager.addEffect(player, new CustomEffect(CustomEffectType.COOLDOWN_ITEM_MEGAPHONE, cooldown)), 0L);
          }
        }
      }
    }
    /* ????????? ?????? ?????? ????????? ????????? ?????? ???????????? ?????? */
    if (Permission.OTHER_PLACEHOLDER.has(player))
    {
      if (message.contains("--noph"))
      {
        message = message.replaceFirst("--noph", "");
      }
      else
      {
        message = PlaceHolderUtil.placeholder(player, message, null);
      }
    }
    if (Permission.OTHER_EVAL.has(player))
    {
      if (message.contains("--noeval") || message.contains("--nocalc"))
      {
        message = message.replaceFirst("--noeval", "");
        message = message.replaceFirst("--nocalc", "");
      }
      else
      {
        message = PlaceHolderUtil.evalString(message);
      }
    }
    if (Permission.EVENT2_CHAT_COLOR.has(player))
    {
      message = message.replace("(void)", "");
      if (message.contains("--nocolor"))
      {
        message = message.replaceFirst("--nocolor", "");
        message = message.replace("??", "&");
      }
      else
      {
        message = MessageUtil.n2s(message);
      }
      if (message.contains("--strip"))
      {
        message = message.replaceFirst("--strip", "");
        message = MessageUtil.stripColor(message);
      }
    }
    event.message(ComponentUtil.create2(player, message, false));
    if (CustomEffectManager.hasEffect(player, CustomEffectType.THICK))
    {
      event.message(MessageUtil.boldify(event.message()));
    }
  }
}
