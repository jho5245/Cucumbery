package com.jho5245.cucumbery.commands.debug;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent.Completion;
import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.util.no_groups.*;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.component.util.ItemNameUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Permission;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import com.jho5245.cucumbery.util.storage.data.TranslatableKeyParser;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig.UserData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.*;
import org.bukkit.Statistic.Type;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommandWhoIs implements CommandExecutor, AsyncTabCompleter
{
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
  {
    try
    {
      if (!Method.hasPermission(sender, Permission.CMD_WHOIS, true))
      {
        return true;
      }
      if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(args)))
      {
        return !(sender instanceof BlockCommandSender);
      }
      String usage = cmd.getUsage().replace("/<command> ", "");
      if (args.length < 2)
      {
        if (args.length == 0)
        {
          if (sender instanceof Player)
          {
            Bukkit.getServer().dispatchCommand(sender, "cwhois " + sender.getName() + " state");
            return true;
          }
        }
        else
        {
          if (SelectorUtil.getPlayer(sender, args[0], false) == null)
          {
            Bukkit.getServer().dispatchCommand(sender, "cwhois " + args[0] + " offline");
          }
          else
          {
            Bukkit.getServer().dispatchCommand(sender, "cwhois " + args[0] + " state");
          }
          return true;
        }
        MessageUtil.shortArg(sender, 1, args);
        MessageUtil.commandInfo(sender, label, usage);
        return true;
      }
      else if (args.length <= 2 ||
              (args[1].equals("stats") && args.length <= 3) ||
              (args[1].equals("stats_general") && args.length <= 3) ||
              (args[1].equals("stats_entity") && args.length <= 4) ||
              (args[1].equals("stats_material") && args.length <= 4))
      {
        if (!Method.equals(args[1], "state", "pos", "name", "effect", "stats", "stats_general", "stats_entity", "stats_material", "offline"))
        {
          MessageUtil.wrongArg(sender, 2, args);
          return true;
        }
        OfflinePlayer offlinePlayer = SelectorUtil.getOfflinePlayer(sender, args[0]);
        if (offlinePlayer == null)
        {
          return true;
        }
        Player player = offlinePlayer.getPlayer();
        if (player == null && !Method.equals(args[1], "name", "stats", "stats_general", "stats_entity", "stats_material", "offline"))
        {
          MessageUtil.wrongArg(sender, 2, args);
          MessageUtil.info(sender, "?????? ?????? ????????? ????????? ????????? ????????????????????? ????????? ??? ????????????");
          return true;
        }
        String playerName = Method.getName(offlinePlayer);
        if (args.length == 2 && args[1].equalsIgnoreCase("pos"))
        {
          if (player == null)
          {
            return true;
          }
          Location spawnPointLocation = player.getBedSpawnLocation();
          String spawnPointWorld = "";
          double spawnPointX = 0d;
          double spawnPointY = 0d;
          double spawnPointZ = 0d;
          boolean hasSpawnPoint = false;
          if (spawnPointLocation != null)
          {
            spawnPointWorld = spawnPointLocation.getWorld().getName();
            spawnPointX = spawnPointLocation.getBlockX();
            spawnPointY = spawnPointLocation.getBlockY();
            spawnPointZ = spawnPointLocation.getBlockZ();
            hasSpawnPoint = true;
          }
          double distance = -1D;
          Location location = player.getLocation();
          if (sender instanceof Player playerSender)
          {
            Location loc2 = playerSender.getLocation();
            if (location.getWorld().getName().equals(loc2.getWorld().getName()))
            {
              distance = location.distance(loc2);
            }
          }
          World world = location.getWorld();
          Biome biome = world.getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ());
          String distanceStr = Constant.Sosu4.format(distance);
          String locationWorld = world.getName();
          String locationX = Constant.Sosu4.format(location.getX());
          String locationY = Constant.Sosu4.format(location.getY());
          String locationZ = Constant.Sosu4.format(location.getZ());
          String locationYaw = Constant.Sosu4.format(location.getYaw());
          String locationPitch = Constant.Sosu4.format(location.getPitch());
          String locationBiome = biome.toString();
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "-------------------------------------------------");
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "&e" + playerName + "&6??? ??????");
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "&2-?????? ?????????-");
          if (hasSpawnPoint)
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? : &e" + spawnPointWorld);
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "X : &e" + Constant.Jeongsu.format(spawnPointX));
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "Y : &e" + Constant.Jeongsu.format(spawnPointY));
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "Z : &e" + Constant.Jeongsu.format(spawnPointZ));
            if (sender instanceof Player p)
            {
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? : &e" + Constant.Sosu4.format(Method2.distance(spawnPointLocation, p.getLocation())) + "&em");
            }
          }
          else
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "&c??????");
          }
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "&2-?????? ??????-");
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? : &e" + locationWorld);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "X : &e" + locationX);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "Y : &e" + locationY);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "Z : &e" + locationZ);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "Yaw : &e" + locationYaw);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "Pitch : &e" + locationPitch);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? : &e" + distanceStr + "&em");
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? : &e" + locationBiome + "&e(&r" + biome + "&e)");
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "-------------------------------------------------");
        }
        else if (args.length == 2 && args[1].equalsIgnoreCase("name"))
        {
          String display = UserData.DISPLAY_NAME.getString(offlinePlayer);
          if (display == null)
          {
            display = offlinePlayer.getName();
          }
          if (display == null)
          {
            display = offlinePlayer.getUniqueId().toString();
          }
          String listName = UserData.PLAYER_LIST_NAME.getString(offlinePlayer);
          if (listName == null)
          {
            listName = offlinePlayer.getName();
          }
          if (listName == null)
          {
            listName = offlinePlayer.getUniqueId().toString();
          }
          @Nullable Component displayComponent = ComponentUtil.create(display), listComponent = ComponentUtil.create(listName);
          String playerDisplayName = ComponentUtil.serialize(displayComponent),
                  playerListName = ComponentUtil.serialize(listComponent);
          displayComponent = displayComponent.clickEvent(ClickEvent.copyToClipboard(playerDisplayName));
          if (displayComponent.hoverEvent() == null)
          {
            displayComponent = displayComponent.hoverEvent(Component.translatable("chat.copy.click"));
          }
          listComponent = listComponent.clickEvent(ClickEvent.copyToClipboard(playerListName));
          if (listComponent.hoverEvent() == null)
          {
            listComponent = listComponent.hoverEvent(Component.translatable("chat.copy.click"));
          }
          if (displayComponent.color() == null)
          {
            displayComponent = displayComponent.color(Constant.THE_COLOR);
          }
          if (listComponent.color() == null)
          {
            listComponent = listComponent.color(Constant.THE_COLOR);
          }
          if (Method.isUUID(playerDisplayName))
          {
            displayComponent = null;
          }
          if (Method.isUUID(playerListName))
          {
            listComponent = null;
          }
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "-------------------------------------------------");
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "%s??? ?????? ??????", offlinePlayer);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "UUID : %s", Constant.THE_COLOR_HEX + offlinePlayer.getUniqueId());
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? : %s", displayComponent != null ? displayComponent : ComponentUtil.translate("&c??? ??? ??????"));
          if (displayComponent != null)
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????????(NBT) : %s", Component.text(display).hoverEvent(Component.translatable("chat.copy.click")).clickEvent(ClickEvent.copyToClipboard(display)));
          }
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ????????? : %s", listComponent != null ? listComponent : ComponentUtil.translate("&c??? ??? ??????"));
          if (listComponent != null)
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????????(NBT) : %s", Component.text(listName).hoverEvent(Component.translatable("chat.copy.click")).clickEvent(ClickEvent.copyToClipboard(listName)));
          }
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "-------------------------------------------------");
        }
        else if (args[1].equalsIgnoreCase("state"))
        {
          if (player == null)
          {
            return true;
          }
          int entityID = player.getEntityId();
          float exp = player.getExp();
          int level = player.getLevel();
          int expToLevel = player.getExpToLevel();
          int totalExp = player.getTotalExperience();
          String fallDistance = Constant.Sosu4.format(player.getFallDistance());
          int fireTicks = player.getFireTicks();
          int foodLevel = player.getFoodLevel();
          String saturation = Constant.Sosu4.format(player.getSaturation());
          String exhaustion = Constant.Sosu4.format(4F - player.getExhaustion());
          GameMode gamemode = player.getGameMode();
          String gm = switch (gamemode)
                  {
                    case SPECTATOR -> "??????";
                    case ADVENTURE -> "??????";
                    case SURVIVAL -> "????????????";
                    case CREATIVE -> "??????????????????";
                  };
          String healthPoint = Constant.Sosu4.format(player.getHealth());
          String maxHealthPoint = Method.attributeString(player, Attribute.GENERIC_MAX_HEALTH);
          String healthScale = Constant.Sosu4.format(player.getHealthScale());
          String lastDamage = Constant.Sosu4.format(player.getLastDamage());
          EntityDamageEvent lastDamageCause = player.getLastDamageCause();
          DamageCause damageCause = null;
          String causeString = null;
          if (lastDamageCause != null)
          {
            damageCause = lastDamageCause.getCause();
            causeString = damageCause.toString();
          }
          Entity damager; // ????????? ??? ??????
          String damagerString = "??c??????";
          Entity damagerShooter; // ????????? ??? ????????? ???????????? ??? ???????????? ??? ??????
          Player shooterPlayer; // ???????????? ??? ????????? ????????????
          boolean isProjectile = false;
          String shooterString = "??c????????? ?????? ??????";
          if (lastDamageCause instanceof EntityDamageByEntityEvent)
          {
            damager = ((EntityDamageByEntityEvent) lastDamageCause).getDamager();
            isProjectile = damager instanceof Projectile;
            if (isProjectile)
            {
              ProjectileSource source = ((Projectile) damager).getShooter();
              if (source instanceof LivingEntity)
              {
                damagerShooter = (LivingEntity) ((Projectile) damager).getShooter();
                if (damagerShooter != null)
                {
                  shooterString = damagerShooter.getName();
                }
                if (damagerShooter instanceof Player)
                {
                  shooterPlayer = (Player) damagerShooter;
                  shooterString = shooterPlayer.getName();
                }
              }
              else if (source instanceof BlockProjectileSource blockSource)
              {
                Block block = blockSource.getBlock();
                shooterString = (ItemNameUtil.itemName(block.getType())).toString();
              }
            }
            damagerString = damager.toString();
          }
          int air = player.getMaximumAir();
          int noDmgTicksMax = player.getMaximumNoDamageTicks();
          int noDmgTicks = player.getNoDamageTicks();
          StringBuilder passangers = new StringBuilder("&7??????");
          player.getPassengers();
          for (Entity entity : player.getPassengers())
          {
            passangers.append("&e").append((entity)).append("&r, ");
          }
          passangers = new StringBuilder("&r?????? ??? : &e" + player.getPassengers().size() + "&r??? : " + passangers.substring(0, passangers.length() - 2));
          if (player.getPassengers().isEmpty())
          {
            passangers = new StringBuilder("&7?????? ?????? ????????? ??????");
          }
          WeatherType weatherType = player.getPlayerWeather();
          String weather = "&e??????";
          if (weatherType != null)
          {
            weather = switch (weatherType)
                    {
                      case DOWNFALL -> "??????";
                      case CLEAR -> "???";
                    };
          }
          int portalCooldown = player.getPortalCooldown();
          int remainAir = player.getRemainingAir();
          int sleepTicks = player.getSleepTicks();
          Entity spectatorTarget = player.getSpectatorTarget();
          String targetType = "??e??????";
          if (spectatorTarget != null)
          {
            targetType = spectatorTarget.getName();
          }
          Entity vehicle = player.getVehicle();
          String vehicleType = "??e??????";
          if (vehicle != null)
          {
            vehicleType = vehicle.getName();
          }
          String speed = Constant.Sosu4.format(player.getWalkSpeed());
          boolean isDead = player.isDead();
          String dead = isDead ? "&4??????" : "&a??????";
          boolean fly = player.getAllowFlight();
          String flyStr = fly ? "&a?????? ??????" : "&7?????? ?????????";
          boolean isFlying = player.isFlying();
          boolean isGliding = player.isGliding();
          boolean isGlowing = player.isGlowing();
          String flying = isFlying ? "&a?????? ???" : "&c?????? ?????? ??????";
          String gliding = isGliding ? "&a????????? ?????? ???" : "&c????????? ?????? ?????? ??????";
          String glowing = isGlowing ? "&a?????? ?????? ?????? ?????? ???" : "&c?????? ?????? ?????? ?????? ?????? ??????";
          String op = player.isOp() ? "&a?????????(??????)" : "&7????????? ??????(?????? ??????)";
          boolean isSleeping = player.isSleeping();
          boolean isSneaking = player.isSneaking();
          boolean isSprinting = player.isSprinting();
          String sleep = isSleeping ? "&a???????????? ?????? ??????" : "&c???????????? ?????? ?????? ??????";
          String sneak = isSneaking ? "&c???????????? ??????" : "&a???????????? ?????? ??????";
          String sprint = isSprinting ? "&a????????? ???" : "&c????????? ?????? ??????";
          double percentage = player.getHealth() / Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
          if (percentage < 0.0D)
          {
            percentage = 0.0D;
          }
          if (percentage > 1.0D)
          {
            percentage = 1.0D;
          }
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "-------------------------------------------------");
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "&e" + playerName + "&6??? ??????");
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "HP??? : &a" + healthScale);
          if (percentage < 0.05D)
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "HP : &4" + healthPoint + "&6 / &a" + maxHealthPoint);
          }
          else if (percentage < 0.3D)
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "HP : &c" + healthPoint + "&6 / &a" + maxHealthPoint);
          }
          else if (percentage < 0.5D)
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "HP : &e" + healthPoint + "&6 / &a" + maxHealthPoint);
          }
          else
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "HP : &a" + healthPoint + "&6 / &a" + maxHealthPoint);
          }
          Vector velocity = player.getVelocity();
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? : &e" + foodLevel);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? : &e" + saturation);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? : &e" + exhaustion);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? : &e" + Constant.Jeongsu.format(level));
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS,
                  "????????? : &e" +
                          Constant.Jeongsu.format(exp * expToLevel) +
                          "&r / &e" +
                          Constant.Jeongsu.format(expToLevel) +
                          "&r (&e" +
                          Constant.Sosu4.format(player.getExp() * 100.0D) +
                          "%&r)");
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "??? ????????? : &e" + Constant.Jeongsu.format(totalExp));
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? : &e" + gm + " ??????");
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? : &e" + flyStr + "&r, &e" + flying);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? : &e" + op);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? : &e" + dead);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, gliding);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, glowing);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, sneak);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, sprint);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, sleep);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? : &e" + weather);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ????????? : &e" + entityID);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? : &e" + Constant.Jeongsu.format(remainAir) + "&r / &e" + Constant.Jeongsu.format(air));
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ??? ?????? : &e" + fallDistance + "m");
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "??? ?????? ?????? ?????? : &e" + Constant.Jeongsu.format(fireTicks));
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ????????? ?????? ????????? : &e" + lastDamage);
          if (damageCause != null)
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ????????? ?????? ?????? ?????? : &e" + causeString);
          }
          if (isProjectile)
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ????????? ?????? ?????? ????????? : ??e" + shooterString, false);
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ????????? ?????? ?????? ?????? ????????? : ??e" + damagerString, false);
          }
          else
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ????????? ?????? ?????? ????????? : ??e" + damagerString, false);
          }
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? : &e" + Constant.Jeongsu.format(noDmgTicks));
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? ?????? : &e" + Constant.Jeongsu.format(noDmgTicksMax));
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? : &e" + speed);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? : &e" + Constant.Sosu4.format(player.getFlySpeed()));
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "???????????? ?????? : &e" + Constant.Sosu4.format(velocity.getX()) + ", " + Constant.Sosu4.format(velocity.getY()) + ", " + Constant.Sosu4.format(velocity.getZ()));
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? ?????? ?????? : &e" + vehicleType);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "-????????? ?????? ?????? ?????? ??????-");
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, passangers.toString());
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? ?????? : &e" + portalCooldown);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? ?????? : ??e" + targetType, false);
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "??? ?????? ?????? ?????? : &e" + Constant.Jeongsu.format(sleepTicks));
          if (Cucumbery.using_Vault_Economy)
          {
            String world = player.getLocation().getWorld().getName();
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? : &e" + Constant.Sosu2.format(Cucumbery.eco.getBalance(player, world)) + "???&r (&e" + world + "&r ?????? ??????)");
          }
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "---------Attribute---------");
          for (Attribute attrs : Attribute.values())
          {
            AttributeInstance attr = player.getAttribute(attrs);
            if (attr == null || attrs == Attribute.GENERIC_MAX_HEALTH)
            {
              continue;
            }
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, ComponentUtil.translate(attrs.translationKey()), " : &e" + Method.attributeString(player, attrs));
          }
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "-------------------------------------------------");
        }
        else if (args.length <= 3 && args[1].equalsIgnoreCase("stats"))
        {
          int page = 1;
          if (args.length == 3)
          {
            try
            {
              page = Integer.parseInt(args[2]);
            }
            catch (Exception e)
            {
              MessageUtil.noArg(sender, Prefix.ONLY_INTEGER, args[2]);
              return true;
            }
          }
          if (!MessageUtil.checkNumberSize(sender, page, 1, 4))
          {
            return true;
          }
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "-------------------------------------------------");
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "&e" + playerName + "&r??? ?????? ?????? (&e" + page + "&r ?????????)");
          switch (page)
          {
            case 1:
              int leaveGame = offlinePlayer.getStatistic(Statistic.LEAVE_GAME);
              int playOneTick = offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE);
              int timeSinceDeath = offlinePlayer.getStatistic(Statistic.TIME_SINCE_DEATH);
              int sneakTime = offlinePlayer.getStatistic(Statistic.SNEAK_TIME);
              int walkOneCm = offlinePlayer.getStatistic(Statistic.WALK_ONE_CM);
              int crouchOneCm = offlinePlayer.getStatistic(Statistic.CROUCH_ONE_CM);
              int sprintOneCm = offlinePlayer.getStatistic(Statistic.SPRINT_ONE_CM);
              int swimOneCm = offlinePlayer.getStatistic(Statistic.SWIM_ONE_CM);
              int fallOneCm = offlinePlayer.getStatistic(Statistic.FALL_ONE_CM);
              int climbOneCm = offlinePlayer.getStatistic(Statistic.CLIMB_ONE_CM);
              int flyOneCm = offlinePlayer.getStatistic(Statistic.FLY_ONE_CM);
              int minecartOneCm = offlinePlayer.getStatistic(Statistic.MINECART_ONE_CM);
              int boatOneCm = offlinePlayer.getStatistic(Statistic.BOAT_ONE_CM);
              int pigOneCm = offlinePlayer.getStatistic(Statistic.PIG_ONE_CM);
              int horseOneCm = offlinePlayer.getStatistic(Statistic.HORSE_ONE_CM);
              int aviateOneCm = offlinePlayer.getStatistic(Statistic.AVIATE_ONE_CM);
              int jump = offlinePlayer.getStatistic(Statistic.JUMP);
              int damageDealt = offlinePlayer.getStatistic(Statistic.DAMAGE_DEALT);
              int damageTaken = offlinePlayer.getStatistic(Statistic.DAMAGE_TAKEN);
              int deaths = offlinePlayer.getStatistic(Statistic.DEATHS);
              int mobKills = offlinePlayer.getStatistic(Statistic.MOB_KILLS);
              int animalsBred = offlinePlayer.getStatistic(Statistic.ANIMALS_BRED);
              int playerKills = offlinePlayer.getStatistic(Statistic.PLAYER_KILLS);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? : &e" + leaveGame);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? : &e" + MessageUtil.periodRealTimeAndGameTime(playOneTick));
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? ?????? ?????? ?????? : &e" + MessageUtil.periodRealTimeAndGameTime(timeSinceDeath));
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? : &e" + MessageUtil.periodRealTimeAndGameTime(sneakTime));
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? : &e" + Constant.Sosu2.format(walkOneCm / 100.0D) + "m");
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "???????????? ?????? ?????? : &e" + Constant.Sosu2.format(crouchOneCm / 100.0D) + "m");
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? : &e" + Constant.Sosu2.format(sprintOneCm / 100.0D) + "m");
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? : &e" + Constant.Sosu2.format(swimOneCm / 100.0D) + "m");
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? : &e" + Constant.Sosu2.format(fallOneCm / 100.0D) + "m");
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? : &e" + Constant.Sosu2.format(climbOneCm / 100.0D) + "m");
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? : &e" + Constant.Sosu2.format(flyOneCm / 100.0D) + "m");
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ????????? ?????? ????????? ?????? : &e" + Constant.Sosu2.format(minecartOneCm / 100.0D) + "m");
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? ????????? ?????? : &e" + Constant.Sosu2.format(boatOneCm / 100.0D) + "m");
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? ????????? ?????? : &e" + Constant.Sosu2.format(pigOneCm / 100.0D) + "m");
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? ????????? ?????? : &e" + Constant.Sosu2.format(horseOneCm / 100.0D) + "m");
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "???????????? ????????? ?????? : &e" + Constant.Sosu2.format(aviateOneCm / 100.0D) + "m");
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? : &e" + jump);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? : &e" + Constant.Sosu2.format(damageDealt / 10.0D));
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? : &e" + Constant.Sosu2.format(damageTaken / 10.0D));
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? : &e" + deaths);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "??? ?????? ?????? : &e" + mobKills);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ????????? ?????? : &e" + animalsBred);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "??????????????? ?????? ?????? : &e" + playerKills);
              break;
            case 2:
              int fishCaught = offlinePlayer.getStatistic(Statistic.FISH_CAUGHT);
              int talkedToVillager = offlinePlayer.getStatistic(Statistic.TALKED_TO_VILLAGER);
              int tradedWithVillager = offlinePlayer.getStatistic(Statistic.TRADED_WITH_VILLAGER);
              int cakeSlicesEaten = offlinePlayer.getStatistic(Statistic.CAKE_SLICES_EATEN);
              int cauldronFilled = offlinePlayer.getStatistic(Statistic.CAULDRON_FILLED);
              int cauldronUsed = offlinePlayer.getStatistic(Statistic.CAULDRON_USED);
              int armorCleaned = offlinePlayer.getStatistic(Statistic.ARMOR_CLEANED);
              int bannerCleaned = offlinePlayer.getStatistic(Statistic.BANNER_CLEANED);
              int brewingstandInteraction = offlinePlayer.getStatistic(Statistic.BREWINGSTAND_INTERACTION);
              int beaconInteraction = offlinePlayer.getStatistic(Statistic.BEACON_INTERACTION);
              int dropperInspected = offlinePlayer.getStatistic(Statistic.DROPPER_INSPECTED);
              int hopperInspected = offlinePlayer.getStatistic(Statistic.HOPPER_INSPECTED);
              int dispenserInspected = offlinePlayer.getStatistic(Statistic.DISPENSER_INSPECTED);
              int noteblockPlayed = offlinePlayer.getStatistic(Statistic.NOTEBLOCK_PLAYED);
              int noteblockTuned = offlinePlayer.getStatistic(Statistic.NOTEBLOCK_TUNED);
              int flowerPotted = offlinePlayer.getStatistic(Statistic.FLOWER_POTTED);
              int trappedChestTriggered = offlinePlayer.getStatistic(Statistic.TRAPPED_CHEST_TRIGGERED);
              int enderchestOpened = offlinePlayer.getStatistic(Statistic.ENDERCHEST_OPENED);
              int itemEnchanted = offlinePlayer.getStatistic(Statistic.ITEM_ENCHANTED);
              int recordPlayed = offlinePlayer.getStatistic(Statistic.RECORD_PLAYED);
              int furnaceInteraction = offlinePlayer.getStatistic(Statistic.FURNACE_INTERACTION);
              int craftingTableInteraction = offlinePlayer.getStatistic(Statistic.CRAFTING_TABLE_INTERACTION);
              int chestOpened = offlinePlayer.getStatistic(Statistic.CHEST_OPENED);
              int sleepInBed = offlinePlayer.getStatistic(Statistic.SLEEP_IN_BED);
              int shulkerBoxOpened = offlinePlayer.getStatistic(Statistic.SHULKER_BOX_OPENED);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "???????????? ?????? ?????? : &e" + fishCaught);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ????????? ?????? : &e" + talkedToVillager);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ????????? ?????? : &e" + tradedWithVillager);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ????????? ?????? ?????? : &e" + cakeSlicesEaten);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "???????????? ?????? ?????? : &e" + cauldronFilled);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "??????????????? ?????? ??? ?????? : &e" + cauldronUsed);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? ?????? : &e" + armorCleaned);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "???????????? ?????? ?????? : &e" + bannerCleaned);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? ?????? : &e" + brewingstandInteraction);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? ?????? : &e" + beaconInteraction);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "???????????? ???????????? ?????? : &e" + dropperInspected);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "???????????? ???????????? ?????? : &e" + hopperInspected);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "???????????? ???????????? ?????? : &e" + dispenserInspected);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? ?????? ?????? : &e" + noteblockPlayed);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? ?????? ?????? : &e" + noteblockTuned);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ????????? ?????? ?????? : &e" + flowerPotted);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "???????????? ??? ?????? ?????? : &e" + trappedChestTriggered);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ????????? ????????? ?????? : &e" + enderchestOpened);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "???????????? ????????? ????????? ?????? : &e" + itemEnchanted);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ????????? ?????? : &e" + recordPlayed);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? ?????? : &e" + furnaceInteraction);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? ?????? : &e" + craftingTableInteraction);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ????????? ?????? : &e" + chestOpened);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "???????????? ??? ?????? : &e" + sleepInBed);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ????????? ??? ?????? : &e" + shulkerBoxOpened);
              break;
            case 3:
              for (EntityType entity : EntityType.values())
              {
                if (!entity.isAlive())
                {
                  continue;
                }
                try
                {
                  MessageUtil.sendMessage(
                          sender, Prefix.INFO_WHOIS +
                                  "&e" +
                                  (entity) +
                                  "&r" +
                                  MessageUtil.getFinalConsonant((entity.name()), MessageUtil.ConsonantType.??????) +
                                  " &e" +
                                  offlinePlayer.getStatistic(Statistic.KILL_ENTITY, entity) +
                                  "&e??? &r??????");
                }
                catch (IllegalArgumentException | NullPointerException ignored)
                {
                }
              }
              break;
            case 4:
              for (EntityType entity : EntityType.values())
              {
                if (!entity.isAlive())
                {
                  continue;
                }
                try
                {
                  MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "&e" + (entity) + "&r?????? &e" + offlinePlayer.getStatistic(Statistic.ENTITY_KILLED_BY, entity) + "&e??? &r??????");
                }
                catch (IllegalArgumentException | NullPointerException ignored)
                {
                }
              }
          }
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "-------------------------------------------------");
        }
        else if (args.length == 3 && args[1].equals("stats_general"))
        {
          Statistic stats;
          try
          {
            stats = Statistic.valueOf(args[2].toUpperCase());
          }
          catch (Exception e)
          {
            MessageUtil.noArg(sender, Prefix.ARGS_WRONG, args[2]);
            return true;
          }
          switch (stats)
          {
            case ANIMALS_BRED, ARMOR_CLEANED, AVIATE_ONE_CM, BANNER_CLEANED, BEACON_INTERACTION, BELL_RING, BOAT_ONE_CM, BREWINGSTAND_INTERACTION,
                    CAKE_SLICES_EATEN, CAULDRON_FILLED, CAULDRON_USED, CHEST_OPENED, CLEAN_SHULKER_BOX, CLIMB_ONE_CM, CRAFTING_TABLE_INTERACTION,
                    CROUCH_ONE_CM, DAMAGE_ABSORBED, DAMAGE_BLOCKED_BY_SHIELD, DAMAGE_DEALT, DAMAGE_DEALT_ABSORBED, DAMAGE_DEALT_RESISTED, DAMAGE_RESISTED,
                    DAMAGE_TAKEN, DEATHS, DISPENSER_INSPECTED, DROPPER_INSPECTED, DROP_COUNT, ENDERCHEST_OPENED, FALL_ONE_CM, FISH_CAUGHT, FLOWER_POTTED,
                    FLY_ONE_CM, FURNACE_INTERACTION, HOPPER_INSPECTED, HORSE_ONE_CM, INTERACT_WITH_ANVIL, INTERACT_WITH_BLAST_FURNACE, INTERACT_WITH_CAMPFIRE,
                    INTERACT_WITH_CARTOGRAPHY_TABLE, INTERACT_WITH_GRINDSTONE, INTERACT_WITH_LECTERN, INTERACT_WITH_LOOM, INTERACT_WITH_SMOKER, INTERACT_WITH_STONECUTTER,
                    ITEM_ENCHANTED, JUMP, LEAVE_GAME, MINECART_ONE_CM, MOB_KILLS, NOTEBLOCK_PLAYED, NOTEBLOCK_TUNED, OPEN_BARREL, PIG_ONE_CM, PLAYER_KILLS,
                    PLAY_ONE_MINUTE, RAID_TRIGGER, RAID_WIN, RECORD_PLAYED, SHULKER_BOX_OPENED, SLEEP_IN_BED, SNEAK_TIME, SPRINT_ONE_CM, SWIM_ONE_CM, TALKED_TO_VILLAGER,
                    TIME_SINCE_DEATH, TIME_SINCE_REST, TRADED_WITH_VILLAGER, TRAPPED_CHEST_TRIGGERED, WALK_ONE_CM, WALK_ON_WATER_ONE_CM, WALK_UNDER_WATER_ONE_CM,
                    INTERACT_WITH_SMITHING_TABLE, STRIDER_ONE_CM, TARGET_HIT, TOTAL_WORLD_TIME -> {
              Component key = Component.translatable(TranslatableKeyParser.getKey(stats));
              String statisticName = ComponentUtil.serialize(key);
              double value = offlinePlayer.getStatistic(stats);
              String valueString = Constant.THE_COLOR_HEX;
              String suffix = "";
              if (statisticName.endsWith("??????"))
              {
                valueString += Method.timeFormatMilli((long) (value * 50));
              }
              else
              {
                if (stats.toString().endsWith("CM"))
                {
                  value /= 100d;
                  suffix = "m";
                }
                else if (statisticName.endsWith("??????"))
                {
                  suffix = "???";
                }
                else if (statisticName.endsWith("??????"))
                {
                  suffix = "???";
                }
                valueString += Constant.Sosu2.format(value);
              }

              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "-------------------------------------------------");
              MessageUtil.sendMessage(
                      sender, Prefix.INFO_WHOIS, ComponentUtil.translate("%s??? %s???(???) %s?????????", offlinePlayer, key.color(Constant.THE_COLOR),
                              ComponentUtil.translate("%s" + suffix, valueString)));
            }
            case BREAK_ITEM, CRAFT_ITEM, DROP, MINE_BLOCK, PICKUP, USE_ITEM -> MessageUtil.sendError(sender, "?????? ?????? &e/whois <???????????? ID> stats_material <??????> <????????? ??????>&r ???????????? ??????????????????.");
            case ENTITY_KILLED_BY, KILL_ENTITY -> MessageUtil.sendError(sender, "?????? ?????? &e/whois <???????????? ID> stats_entity <??????> <??? ??????>&r ???????????? ??????????????????.");
          }
        }
        else if (args.length >= 3 && args[1].equals("stats_material"))
        {
          if (args.length == 3)
          {
            MessageUtil.shortArg(sender, 4, args);
            MessageUtil.sendMessage(sender, Prefix.INFO, "/" + label + " stats_material <??????> <????????? ??????>");
            return true;
          }
          Statistic stats;
          try
          {
            stats = Statistic.valueOf(args[2].toUpperCase());
          }
          catch (Exception e)
          {
            MessageUtil.noArg(sender, Prefix.ARGS_WRONG, args[2]);
            return true;
          }
          Material type;
          try
          {
            type = Material.valueOf(args[3].toUpperCase());
          }
          catch (Exception e)
          {
            MessageUtil.noArg(sender, Prefix.ARGS_WRONG, args[3]);
            return true;
          }
          switch (stats)
          {
            case ANIMALS_BRED, ARMOR_CLEANED, AVIATE_ONE_CM, BANNER_CLEANED, BEACON_INTERACTION, BELL_RING, BOAT_ONE_CM, BREWINGSTAND_INTERACTION,
                    CAKE_SLICES_EATEN, CAULDRON_FILLED, CAULDRON_USED, CHEST_OPENED, CLEAN_SHULKER_BOX, CLIMB_ONE_CM, CRAFTING_TABLE_INTERACTION,
                    CROUCH_ONE_CM, DAMAGE_ABSORBED, DAMAGE_BLOCKED_BY_SHIELD, DAMAGE_DEALT, DAMAGE_DEALT_ABSORBED, DAMAGE_DEALT_RESISTED, DAMAGE_RESISTED,
                    DAMAGE_TAKEN, DEATHS, DISPENSER_INSPECTED, DROPPER_INSPECTED, DROP_COUNT, ENDERCHEST_OPENED, FALL_ONE_CM, FISH_CAUGHT, FLOWER_POTTED,
                    FLY_ONE_CM, FURNACE_INTERACTION, HOPPER_INSPECTED, HORSE_ONE_CM, INTERACT_WITH_ANVIL, INTERACT_WITH_BLAST_FURNACE, INTERACT_WITH_CAMPFIRE,
                    INTERACT_WITH_CARTOGRAPHY_TABLE, INTERACT_WITH_GRINDSTONE, INTERACT_WITH_LECTERN, INTERACT_WITH_LOOM, INTERACT_WITH_SMOKER, INTERACT_WITH_STONECUTTER,
                    ITEM_ENCHANTED, JUMP, LEAVE_GAME, MINECART_ONE_CM, MOB_KILLS, NOTEBLOCK_PLAYED, NOTEBLOCK_TUNED, OPEN_BARREL, PIG_ONE_CM, PLAYER_KILLS,
                    PLAY_ONE_MINUTE, RAID_TRIGGER, RAID_WIN, RECORD_PLAYED, SHULKER_BOX_OPENED, SLEEP_IN_BED, SNEAK_TIME, SPRINT_ONE_CM, SWIM_ONE_CM, TALKED_TO_VILLAGER,
                    TIME_SINCE_DEATH, TIME_SINCE_REST, TRADED_WITH_VILLAGER, TRAPPED_CHEST_TRIGGERED, WALK_ONE_CM, WALK_ON_WATER_ONE_CM, WALK_UNDER_WATER_ONE_CM,
                    INTERACT_WITH_SMITHING_TABLE, STRIDER_ONE_CM, TARGET_HIT, TOTAL_WORLD_TIME -> MessageUtil.sendError(sender, "?????? ?????? &e/whois <???????????? ID> stats_general <??????>&r ???????????? ??????????????????.");
            case BREAK_ITEM, CRAFT_ITEM, DROP, MINE_BLOCK, PICKUP, USE_ITEM -> {
              Component key = Component.translatable(TranslatableKeyParser.getKey(stats));
              int value = offlinePlayer.getStatistic(stats, type);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "-------------------------------------------------");
              MessageUtil.sendMessage(
                      sender, Prefix.INFO_WHOIS, ComponentUtil.translate("%s??? %s %s???(???) %s?????????", offlinePlayer, type, key,
                              ComponentUtil.translate("%s???", Constant.THE_COLOR_HEX + Constant.Sosu2.format(value))));
            }
            case ENTITY_KILLED_BY, KILL_ENTITY -> MessageUtil.sendError(sender, "?????? ?????? &e/whois <???????????? ID> stats_entity <??????> <??? ??????>&r ???????????? ??????????????????.");
          }
        }
        else if (args.length >= 3 && args[1].equals("stats_entity"))
        {
          if (args.length == 3)
          {
            MessageUtil.shortArg(sender, 4, args);
            MessageUtil.sendMessage(sender, Prefix.INFO, "/" + label + " stats_entity <??????> <??? ??????>");
            return true;
          }
          Statistic stats;
          try
          {
            stats = Statistic.valueOf(args[2].toUpperCase());
          }
          catch (Exception e)
          {
            MessageUtil.noArg(sender, Prefix.ARGS_WRONG, args[2]);
            return true;
          }
          EntityType type;
          try
          {
            type = EntityType.valueOf(args[3].toUpperCase());
          }
          catch (Exception e)
          {
            MessageUtil.noArg(sender, Prefix.ARGS_WRONG, args[3]);
            return true;
          }
          switch (stats)
          {
            case ANIMALS_BRED, ARMOR_CLEANED, AVIATE_ONE_CM, BANNER_CLEANED, BEACON_INTERACTION, BELL_RING, BOAT_ONE_CM, BREWINGSTAND_INTERACTION,
                    CAKE_SLICES_EATEN, CAULDRON_FILLED, CAULDRON_USED, CHEST_OPENED, CLEAN_SHULKER_BOX, CLIMB_ONE_CM, CRAFTING_TABLE_INTERACTION,
                    CROUCH_ONE_CM, DAMAGE_ABSORBED, DAMAGE_BLOCKED_BY_SHIELD, DAMAGE_DEALT, DAMAGE_DEALT_ABSORBED, DAMAGE_DEALT_RESISTED, DAMAGE_RESISTED,
                    DAMAGE_TAKEN, DEATHS, DISPENSER_INSPECTED, DROPPER_INSPECTED, DROP_COUNT, ENDERCHEST_OPENED, FALL_ONE_CM, FISH_CAUGHT, FLOWER_POTTED,
                    FLY_ONE_CM, FURNACE_INTERACTION, HOPPER_INSPECTED, HORSE_ONE_CM, INTERACT_WITH_ANVIL, INTERACT_WITH_BLAST_FURNACE, INTERACT_WITH_CAMPFIRE,
                    INTERACT_WITH_CARTOGRAPHY_TABLE, INTERACT_WITH_GRINDSTONE, INTERACT_WITH_LECTERN, INTERACT_WITH_LOOM, INTERACT_WITH_SMOKER, INTERACT_WITH_STONECUTTER,
                    ITEM_ENCHANTED, JUMP, LEAVE_GAME, MINECART_ONE_CM, MOB_KILLS, NOTEBLOCK_PLAYED, NOTEBLOCK_TUNED, OPEN_BARREL, PIG_ONE_CM, PLAYER_KILLS,
                    PLAY_ONE_MINUTE, RAID_TRIGGER, RAID_WIN, RECORD_PLAYED, SHULKER_BOX_OPENED, SLEEP_IN_BED, SNEAK_TIME, SPRINT_ONE_CM, SWIM_ONE_CM, TALKED_TO_VILLAGER,
                    TIME_SINCE_DEATH, TIME_SINCE_REST, TRADED_WITH_VILLAGER, TRAPPED_CHEST_TRIGGERED, WALK_ONE_CM, WALK_ON_WATER_ONE_CM, WALK_UNDER_WATER_ONE_CM,
                    INTERACT_WITH_SMITHING_TABLE, STRIDER_ONE_CM, TARGET_HIT, TOTAL_WORLD_TIME -> MessageUtil.sendError(sender, "?????? ?????? &e/whois <???????????? ID> stats_general <??????>&r ???????????? ??????????????????.");
            case BREAK_ITEM, CRAFT_ITEM, DROP, MINE_BLOCK, PICKUP, USE_ITEM -> MessageUtil.sendError(sender, "?????? ?????? &e/whois <???????????? ID> stats_material <??????> <????????? ??????>&r ???????????? ??????????????????.");
            case ENTITY_KILLED_BY, KILL_ENTITY -> {
              int value = offlinePlayer.getStatistic(stats, type);
              MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "-------------------------------------------------");
              if (stats == Statistic.KILL_ENTITY)
              {
                MessageUtil.sendMessage(
                        sender, Prefix.INFO_WHOIS, ComponentUtil.translate("%s???(???) %s", offlinePlayer,
                                ComponentUtil.translate(TranslatableKeyParser.getKey(stats),
                                        Constant.THE_COLOR_HEX + Constant.Sosu2.format(value), Component.translatable(type.translationKey()).color(Constant.THE_COLOR))));
              }
              else
              {

                MessageUtil.sendMessage(
                        sender, Prefix.INFO_WHOIS, ComponentUtil.translate("%s???(???) %s", offlinePlayer,
                                ComponentUtil.translate(TranslatableKeyParser.getKey(stats),
                                        Component.translatable(type.translationKey()).color(Constant.THE_COLOR), Constant.THE_COLOR_HEX + Constant.Sosu2.format(value))));
              }
            }
          }
        }
        else if (args.length == 2 && args[1].equalsIgnoreCase("effect"))
        {
          if (player == null)
          {
            return true;
          }
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "-------------------------------------------------");
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, ComponentUtil.translate("%s??? ?????? ?????? ??????", player));
          List<PotionEffect> potionEffects = new ArrayList<>(player.getActivePotionEffects());
          if (potionEffects.isEmpty())
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, ComponentUtil.translate("&c&o?????? ?????? ??????"));
          }
          else
          {
            Component message = Component.empty();
            for (int i = 0; i < potionEffects.size(); i++)
            {
              PotionEffect potionEffect = potionEffects.get(i);
              message = message.append(ComponentUtil.create(potionEffect).append(ComponentUtil.translate("(%s)", potionEffect.getAmplifier() + 1)));
              if (i + 1 < potionEffects.size())
              {
                message = message.append(ComponentUtil.translate("&7, "));
              }
            }
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, message);
          }
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "-------------------------------------------------");
        }
        else if (args.length == 2 && args[1].equals("offline"))
        {
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "-------------------------------------------------");
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, offlinePlayer, "??? ??????");
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? : " + (offlinePlayer.isOnline() ? "&a?????????" : "&c????????????"));
          if (Bukkit.getServer().hasWhitelist())
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????????????????? ?????? : " + (offlinePlayer.isWhitelisted() ? "&a????????????????????? ?????????" : "&c????????????????????? ???????????? ??????"));
          }
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? : " + (offlinePlayer.isBanned() ? "&4????????????" : "&a??????????????? ??????"));
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "????????? ?????? : &e" + (offlinePlayer.isOp() ? "&a?????????(??????)" : "&7????????? ??????(?????? ??????)"));
          long firstPlayed = offlinePlayer.getFirstPlayed();
          if (!offlinePlayer.hasPlayedBefore() && firstPlayed == 0)
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "&e??? ?????? ????????? ????????? ??????");
          }
          else
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? : &e" + ((offlinePlayer.isOnline() ? 1 : 0) + offlinePlayer.getStatistic(Statistic.LEAVE_GAME)));
          }
          long lastLogin = offlinePlayer.getLastLogin();
          long lastSeen = offlinePlayer.getLastSeen();
          long current = System.currentTimeMillis();
          Calendar calendar = Calendar.getInstance();
          int timeDiffer = Cucumbery.config.getInt("adjust-time-difference-value");
          if (firstPlayed != 0)
          {
            calendar.setTimeInMillis(firstPlayed);
            calendar.add(Calendar.HOUR, timeDiffer);
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? ?????? : &e"
                    + Method.getCurrentTime(calendar, true, false) + " (" + Method.timeFormatMilli(current - firstPlayed, false) + " ???)");
          }
          if (lastLogin != 0)
          {
            calendar.setTimeInMillis(lastLogin);
            calendar.add(Calendar.HOUR, timeDiffer);
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "??????????????? ????????? ?????? : &e" +
                    Method.getCurrentTime(calendar, true, false) + " (" + Method.timeFormatMilli(current - lastLogin, false) + " ???)");
          }
          if (lastSeen != 0 && !offlinePlayer.isOnline())
          {
            calendar.setTimeInMillis(lastSeen);
            calendar.add(Calendar.HOUR, timeDiffer);
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "??????????????? ????????? ?????? : &e" +
                    Method.getCurrentTime(calendar, true, false) + " (" + Method.timeFormatMilli(current - lastSeen, false) + " ???)");
          }
          if (Cucumbery.using_Vault_Economy)
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? ?????? : &e" + Constant.Sosu2.format(Cucumbery.eco.getBalance(offlinePlayer)) + "???");
          }
          Location spawnPointLocation = offlinePlayer.getBedSpawnLocation();
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "&2-?????? ?????????-");
          if (spawnPointLocation != null)
          {
            String spawnPointWorld = spawnPointLocation.getWorld().getName();
            double spawnPointX = spawnPointLocation.getBlockX();
            double spawnPointY = spawnPointLocation.getBlockY();
            double spawnPointZ = spawnPointLocation.getBlockZ();
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? : &e" + spawnPointWorld);
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "X : &e" + Constant.Jeongsu.format(spawnPointX));
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "Y : &e" + Constant.Jeongsu.format(spawnPointY));
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "Z : &e" + Constant.Jeongsu.format(spawnPointZ));
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "?????? : &e" + (sender instanceof Player player1 ? Constant.Sosu4.format(Method2.distance(spawnPointLocation, player1.getLocation())) +
                    "m" : "-1m"));
          }
          else
          {
            MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "&c??????");
          }
          MessageUtil.sendMessage(sender, Prefix.INFO_WHOIS, "-------------------------------------------------");
        }
        else
        {
          MessageUtil.sendError(sender, "???????????? ????????? ????????? ????????? ???????????? ??????????????????");
          MessageUtil.commandInfo(sender, label, usage);
          return true;
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
    catch (Exception ignored)
    {

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
      if (label.equals("whois"))
      {
        return Method.tabCompleterPlayer(sender, args);
      }
      return Method.tabCompleterOfflinePlayer(sender, args);
    }
    else if (length == 2)
    {
      Player player = SelectorUtil.getPlayer(sender, args[0], false);
      OfflinePlayer offlinePlayer = null;
      if (Method.isUUID(args[0]))
      {
        offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(args[0]));
      }
      else if (!args[0].equals(""))
      {
        offlinePlayer = Bukkit.getOfflinePlayerIfCached(args[0]);
      }
      if (args[0].equals("") || (player == null && (offlinePlayer == null || !offlinePlayer.isOnline())))
      {
        if (Method.equals(args[1], "pos", "state", "effect"))
        {
          return Collections.singletonList("?????? ?????? ????????? ????????? ????????? ????????????????????? ????????? ??? ????????????");
        }
        return Method.tabCompleterList(args, "[?????? ??????]", "name", "stats", "stats_general", "stats_entity", "stats_material", "offline");
      }
      boolean hasPotionEffects = player != null && !player.getActivePotionEffects().isEmpty();
      return Method.tabCompleterList(args, "[?????? ??????]", "pos", "name", "state", "stats",
              "effect" + (!hasPotionEffects ? "(?????? ?????? ?????? ??????)" : ""), "stats_general", "stats_entity", "stats_material", "offline");
    }
    else if (length == 3)
    {
      if (args[1].equals("stats"))
      {
        return Method.tabCompleterIntegerRadius(args, 1, 4, "[?????????]");
      }
      if (Method.equals(args[1], "stats_general", "stats_entity", "stats_material"))
      {
        return Method.tabCompleterStatistics(args, args[1].replace("stats_", ""), "<??????>");
      }
    }
    else if (length == 4)
    {
      if (args[1].equals("stats_entity"))
      {
        List<String> list = new ArrayList<>();
        for (EntityType entityType : EntityType.values())
        {
          if (entityType.isAlive())
          {
            list.add(entityType.toString().toLowerCase());
          }
        }
        return Method.tabCompleterList(args, list, "<??????>");
      }
      else if (args[1].equals("stats_material"))
      {
        if (args[2].equals("mine_block"))
        {
          List<String> list = new ArrayList<>();
          for (Material material : Material.values())
          {
            if (material.isBlock())
            {
              list.add(material.toString().toLowerCase());
            }
          }
          return Method.tabCompleterList(args, list, "<??????>");
        }
        else if (args[2].equals("break_item"))
        {
          List<String> list = new ArrayList<>();
          for (Material material : Constant.DURABLE_ITEMS)
          {
            list.add(material.toString().toLowerCase());
          }
          return Method.tabCompleterList(args, list, "<???????????? ?????? ?????????>");
        }
        else if (Method.equals(args[2], "use_item", "drop", "pickup", "craft_item"))
        {
          List<String> list = new ArrayList<>();
          for (Material material : Material.values())
          {
            if (material.isItem() && material != Material.AIR)
            {
              list.add(material.toString().toLowerCase());
            }
          }
          return Method.tabCompleterList(args, list, "<?????????>");
        }
      }
    }
    return Collections.singletonList(Prefix.ARGS_LONG.toString());
  }

  @Override
  public @NotNull List<Completion> completion(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args, @NotNull Location location)
  {
    int length = args.length;
    if (length == 1)
    {
      if (label.equals("whois"))
      {
        return CommandTabUtil.tabCompleterPlayer(sender, args, "<????????????>");
      }
      return CommandTabUtil.tabCompleterOfflinePlayer(sender, args, "<????????????>");
    }
    else if (length == 2)
    {
      Player player = Method.getPlayer(sender, args[0], false);
      OfflinePlayer offlinePlayer = null;
      if (Method.isUUID(args[0]))
      {
        offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(args[0]));
      }
      else if (!args[0].equals(""))
      {
        offlinePlayer = Bukkit.getOfflinePlayerIfCached(args[0]);
      }
      if (args[0].equals("") || (player == null && (offlinePlayer == null || !offlinePlayer.isOnline())))
      {
        if (Method.equals(args[1], "pos", "state", "effect"))
        {
          return CommandTabUtil.errorMessage("?????? ?????? ????????? ????????? ????????? ????????????????????? ????????? ??? ????????????");
        }
        return CommandTabUtil.tabCompleterList(args, "[?????? ??????]", false,
                Completion.completion("name", Component.translatable("?????????")),
                Completion.completion("stats", Component.translatable("???????????? ??????")),
                Completion.completion("stats_general", Component.translatable("?????? ??????")),
                Completion.completion("stats_entity", Component.translatable("?????? ????????? ??????")),
                Completion.completion("stats_material", Component.translatable("????????? ????????? ??????")),
                Completion.completion("offline", Component.translatable("?????? ??????")));
      }
      boolean hasPotionEffects = player != null && !player.getActivePotionEffects().isEmpty();
      return CommandTabUtil.tabCompleterList(args, "[?????? ??????]", false,
              Completion.completion("state", Component.translatable("??????")),
              Completion.completion("pos", Component.translatable("?????? ??? ?????? ????????? ??????")),
              Completion.completion(Constant.TAB_COMPLETER_QUOTE_ESCAPE + "effect" + (hasPotionEffects ? "" : "(?????? ?????? ?????? ??????)"), Component.translatable("?????? ?????? ??????")),
              Completion.completion("name", Component.translatable("?????????")),
              Completion.completion("stats", Component.translatable("???????????? ??????")),
              Completion.completion("stats_general", Component.translatable("?????? ??????")),
              Completion.completion("stats_entity", Component.translatable("?????? ????????? ??????")),
              Completion.completion("stats_material", Component.translatable("????????? ????????? ??????")),
              Completion.completion("offline", Component.translatable("?????? ??????")));
    }
    else if (length == 3)
    {
      switch (args[1])
      {
        case "stats" -> {
          return CommandTabUtil.tabCompleterIntegerRadius(args, 1, 4, "[?????????]");
        }
        case "stats_general" -> {
          return CommandTabUtil.tabCompleterList(args, Statistic.values(), "<?????? ??????>", e -> e instanceof Statistic statistic && statistic.getType() != Type.UNTYPED);
        }
        case "stats_entity" -> {
          return CommandTabUtil.tabCompleterList(args, Statistic.values(), "<?????? ??????>", e -> e instanceof Statistic statistic && statistic.getType() != Type.ENTITY);
        }
        case "stats_material" -> {
          return CommandTabUtil.tabCompleterList(args, Statistic.values(), "<????????? ??????>", e -> e instanceof Statistic statistic && statistic.getType() != Type.ITEM && statistic.getType() != Type.BLOCK);
        }
      }
    }
    else if (length == 4)
    {
      switch (args[1])
      {
        case "stats_entity" -> {
          return CommandTabUtil.tabCompleterList(args, EntityType.values(), "<?????? ??????>", e -> e instanceof EntityType entityType && !entityType.isAlive());
        }
        case "stats_material" -> {
          switch (args[2])
          {
            case "mine_block" -> {
              return CommandTabUtil.tabCompleterList(args, Material.values(), "<??????>", e -> e instanceof Material material && !material.isBlock());
            }
            case "break_item" -> {
              return CommandTabUtil.tabCompleterList(args, Material.values(), "<???????????? ?????? ?????????>", e -> e instanceof Material material && material.getMaxDurability() == 0);
            }
            case "use_item", "drop", "pickup", "craft_item" -> {
              return CommandTabUtil.tabCompleterList(args, Material.values(), "<?????????>", e -> e instanceof Material material && (!material.isItem() || material.isAir()));
            }
          }
        }
      }
    }
    return Collections.singletonList(CommandTabUtil.ARGS_LONG);
  }
}
