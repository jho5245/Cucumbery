package com.jho5245.cucumbery.listeners.player.no_groups;

import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.custom.customeffect.CustomEffect;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectType;
import com.jho5245.cucumbery.deathmessages.LastTrampledBlockManager;
import com.jho5245.cucumbery.events.entity.EntityLandOnGroundEvent;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.storage.no_groups.SoundPlay;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Permission;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import com.jho5245.cucumbery.util.storage.data.Variable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class PlayerMove implements Listener
{
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event)
  {
    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    if (!Permission.EVENT2_ANTI_ALLPLAYER.has(player) && event.hasChangedPosition() && Constant.AllPlayer.MOVE.isEnabled())
    {
      event.setCancelled(true);
      if (!Variable.playerMoveAlertCooldown.contains(uuid))
      {
        Variable.playerMoveAlertCooldown.add(uuid);
        MessageUtil.sendMessage(player, Prefix.INFO_ALLPLAYER, "움직일 수 없는 상태입니다");
        SoundPlay.playSound(player, Constant.ERROR_SOUND);
        Bukkit.getServer().getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> Variable.playerMoveAlertCooldown.remove(uuid), 100L);
      }
      return;
    }

    this.customEffect(event);
    if (event.isCancelled())
    {
      return;
    }
    this.entityLandOnGround(event);
    this.getLastTrampledBlock(event);
  }

  private void customEffect(PlayerMoveEvent event)
  {
    Player player = event.getPlayer();
    if (CustomEffectManager.hasEffect(player, CustomEffectType.STOP))
    {
      event.setCancelled(true);
    }
    if (CustomEffectManager.hasEffect(player, CustomEffectType.CONFUSION))
    {
      Location from = event.getFrom(), to = event.getTo();
      float yaw = Math.min(360f, Math.max(-360f, 2 * from.getYaw() - to.getYaw()));
      float pitch = Math.min(90f, Math.max(-90f, 2 * from.getPitch() - to.getPitch()));
      event.setTo(new Location(to.getWorld(), to.getX(), to.getY(), to.getZ(), yaw, pitch));
    }
    if (event.hasExplicitlyChangedPosition() && CustomEffectManager.hasEffect(player, CustomEffectType.VAR_PODAGRA) && !player.isSneaking())
    {
      if (!CustomEffectManager.hasEffect(player, CustomEffectType.VAR_PODAGRA_ACTIVATED))
      {
        player.damage(1);
        player.setNoDamageTicks(0);
      }
      // just for some case
      try
      {
        CustomEffectManager.addEffect(player, new CustomEffect(CustomEffectType.VAR_PODAGRA_ACTIVATED, CustomEffectType.VAR_PODAGRA_ACTIVATED.getDefaultDuration(), CustomEffectManager.getEffect(player, CustomEffectType.VAR_PODAGRA).getAmplifier()));
      }
      catch (IllegalStateException ignored)
      {

      }
    }
  }

  private void getLastTrampledBlock(PlayerMoveEvent event)
  {
    LivingEntity livingEntity = event.getPlayer();
    LastTrampledBlockManager.lastTrampledBlock(livingEntity, event.hasChangedBlock());
  }

  private void entityLandOnGround(PlayerMoveEvent event)
  {
    LivingEntity entity = event.getPlayer();
    Location location = entity.getLocation();
    if (!entity.isSwimming() && (event.getFrom().getBlockY() > event.getTo().getBlockY()) && !location.getBlock().getType().isSolid() && location.add(0, -2, 0).getBlock().getType() != Material.AIR)
    {
      EntityLandOnGroundEvent landOnGroundEvent = new EntityLandOnGroundEvent(entity);
      Bukkit.getPluginManager().callEvent(landOnGroundEvent);
    }
  }
}
