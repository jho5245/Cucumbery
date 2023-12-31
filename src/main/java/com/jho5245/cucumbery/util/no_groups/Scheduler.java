package com.jho5245.cucumbery.util.no_groups;

import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.Initializer;
import com.jho5245.cucumbery.commands.reinforce.CommandReinforce;
import com.jho5245.cucumbery.commands.sound.CommandSong;
import com.jho5245.cucumbery.custom.customeffect.CustomEffect;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectType;
import com.jho5245.cucumbery.custom.customeffect.children.group.PlayerCustomEffect;
import com.jho5245.cucumbery.custom.customeffect.children.group.PlayerCustomEffectImple;
import com.jho5245.cucumbery.custom.customeffect.scheduler.CustomEffectScheduler;
import com.jho5245.cucumbery.custom.customrecipe.recipeinventory.RecipeInventoryCategory;
import com.jho5245.cucumbery.custom.customrecipe.recipeinventory.RecipeInventoryMainMenu;
import com.jho5245.cucumbery.custom.customrecipe.recipeinventory.RecipeInventoryRecipe;
import com.jho5245.cucumbery.util.itemlore.ItemLore;
import com.jho5245.cucumbery.util.nbt.CucumberyTag;
import com.jho5245.cucumbery.util.nbt.NBTAPI;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Constant.RestrictionType;
import com.jho5245.cucumbery.util.storage.data.Permission;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import com.jho5245.cucumbery.util.storage.data.Variable;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig.UserData;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import com.jho5245.cucumbery.util.storage.no_groups.SoundPlay;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Scheduler
{
  public static final BossBar serverRadio = BossBar.bossBar(Component.empty(), 0, BossBar.Color.GREEN, Overlay.PROGRESS);
  /**
   * 플레이어가 플레이어를 관전 중일 때 표시할 영양 게이지 텍스트
   */
  private static final String EXHAUSTION_GUAGE = "▁▂▃▄▅▆▇█";
  public static boolean delay = false, delay2 = false;
  public static BukkitTask delayTask = null;
  public static int fileNameLength = -1;

  @SuppressWarnings("all")
  public static void Schedule(Cucumbery cucumbery)
  {
    Bukkit.getServer().getScheduler().runTaskTimer(cucumbery, () ->
    {
      // 틱 단위로 무한 반복하는 애들
      tickSchedules();
      // 서버 라디오
      serverRadio();
      // sendBossBar
      BossBarMessage.tick();
      // 플러그인 실행 시간
      Cucumbery.runTime++;
    }, 0L, 1L);
    Bukkit.getServer().getScheduler().runTaskTimer(cucumbery, () ->
    {
      // 레시피 메뉴 업데이트
      updateCustomRecipeGUI();
    }, 0L, 5L);
    Bukkit.getServer().getScheduler().runTaskTimer(cucumbery, () ->
    {
      // 플레이어 인벤토리의 아이템(손에 들고 있는 아이템 제외) 플레이어가 열고 있는 인벤토리 아이템 루프
      playerExpireItem();
    }, 0L, 10L);
    Bukkit.getServer().getScheduler().runTaskTimer(cucumbery, () ->
    {
      // 관전 중인 개체의 거리가 멀어졌을 때 관전 위치 갱신
      spectateUpdater();
      // 인벤토리가 가득 찼을때 타이틀 알림
      inventoryFullNotify();
    }, 20L, 20L);
    Bukkit.getServer().getScheduler().runTaskTimer(cucumbery, () ->
    {
      expireItemAvailableTime();
    }, 400L, 400L);
    Bukkit.getServer().getScheduler().runTaskTimer(cucumbery, () ->
    {
      starCatchPenalty();
    }, 20L * 60L * 10L, 20L * 60L * 10L);
    Bukkit.getServer().getScheduler().runTaskTimer(cucumbery, () ->
    {
      playerExpireHandItem();
    }, 1200L, 1200L);
    Bukkit.getServer().getScheduler().runTaskTimer(cucumbery, () ->
    {
      Initializer.saveUserData();
      Initializer.saveBlockPlaceData();
      Initializer.saveItemUsageData();
      CustomEffectManager.save();
    }, 1200L, 20L * 60L * 5L);
    reinforceChancetime();
  }

  private static void tickSchedules()
  {
    entityTick();
    playerTick();
    damageIndicator();
  }

  private static void damageIndicator()
  {
    Variable.lastDamageMillis.keySet().removeIf(uuid ->
    {
      Entity entity = Bukkit.getEntity(uuid);
      return entity == null || !entity.isValid() || entity.isDead();
    });
    Variable.damageIndicatorStack.keySet().removeIf(uuid ->
    {
      int stack = Variable.damageIndicatorStack.get(uuid);
      if (stack > 300)
      {
        return true;
      }
      Entity entity = Bukkit.getEntity(uuid);
      return entity == null || !entity.isValid() || entity.isDead();
    });
  }

  private static void entityTick()
  {
    for (World world : Bukkit.getWorlds())
    {
      for (Entity entity : world.getEntities())
      {
        // 커스텀 인챈트
        customEnchant(entity);
        CustomEffectScheduler.tick(entity);
        CustomEffectScheduler.trueInvisibility(entity);
        CustomEffectScheduler.axolotlsGrace(entity);
        CustomEffectScheduler.stop(entity);
        CustomEffectScheduler.damageIndicator(entity);
        CustomEffectScheduler.vanillaEffect(entity);
      }
    }
  }

  private static void playerTick()
  {
    for (Player player : Bukkit.getOnlinePlayers())
    {
      // 관전 중인 개체의 정보 표시
      showSpectatorTargetInfoActionbar(player);
      // 주로 사용하는 손에 들고 있는 아이템의 재사용/재발동 대기 시간 액션바에 표시
      showCooldownActionbar(player);
      // 특정 인벤토리(숫돌, 지도 제작대, 석재 절단기 등)의 인벤토리 결과물 실시간 업데이트
      if (Cucumbery.config.getBoolean("use-helpful-lore-feature"))
      {
        itemLore(player);
      }
      // 관전 제한 루프 (관전 권한이 없을 때 관전 취소)
      spectateLoop(player);
      // 장착 불가 갑옷 사용 제한
      if (!Cucumbery.getPlugin().getConfig().getBoolean("disable-item-usage-restriction"))
      {
        armorEquipRestriction(player);
      }
      // 명령 블록 명령어 미리 보기
      commandBlockPreview(player);
      worldEditPositionParticle(player);

      CustomEffectScheduler.display(player);
      CustomEffectScheduler.gliding(player);
      CustomEffectScheduler.spreadAndVariation(player);
      CustomEffectScheduler.fancySpotlight(player);
      CustomEffectScheduler.newbieShield(player);
      CustomEffectScheduler.darknessTerror(player);
      CustomEffectScheduler.serverRadio(player);
      CustomEffectScheduler.trollInventoryProperty(player);
      CustomEffectScheduler.townShield(player);
      CustomEffectScheduler.starCatch(player);
    }
  }

  private static void starCatchPenalty()
  {
    for (UUID uuid : Variable.starCatchPenalty.keySet())
    {
      int i = Variable.starCatchPenalty.get(uuid);
      Variable.starCatchPenalty.put(uuid, Math.max(0, i - 20));
    }
  }

  private static void worldEditPositionParticle(@NotNull Player player)
  {
    if (!Cucumbery.using_WorldEdit)
    {
      return;
    }
    if (!UserData.SHOW_WORLDEDIT_POSITION_PARTICLE.getBoolean(player))
    {
      return;
    }
    if (player.getInventory().getItemInMainHand().getType() != Material.WOODEN_AXE)
    {
      return;
    }
    BukkitPlayer bukkitPlayer = BukkitAdapter.adapt(player);
    LocalSession localSession = Cucumbery.worldEditPlugin.getSession(player);
    Region region;
    try {
      localSession.getSelection();
      region = localSession.getSelection(bukkitPlayer.getWorld());
    } catch (Throwable t) {
      return;
    }
    BlockVector3 max = region.getMaximumPoint();
    BlockVector3 min = region.getMinimumPoint();
    int maxx = Math.max(max.getBlockX(), min.getBlockX());
    int maxy = Math.max(max.getBlockY(), min.getBlockY());
    int maxz = Math.max(max.getBlockZ(), min.getBlockZ());
    int minx = Math.min(max.getBlockX(), min.getBlockX());
    int miny = Math.min(max.getBlockY(), min.getBlockY());
    int minz = Math.min(max.getBlockZ(), min.getBlockZ());
    double avgx = (maxx + minx) / 2.;
    double avgy = (maxy + miny) / 2.;
    double avgz = (maxz + minz) / 2.;
    World world = player.getWorld();
    player.spawnParticle(Particle.REDSTONE, new Location(world, avgx, avgy, avgz), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(245, 189, 4), 1));
    player.spawnParticle(Particle.REDSTONE, new Location(world, avgx + 1, avgy + 1, avgz + 1), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(245, 189, 4), 1));
    player.spawnParticle(Particle.REDSTONE, new Location(world, avgx, avgy + 1, avgz), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(245, 189, 4), 1));
    player.spawnParticle(Particle.REDSTONE, new Location(world, avgx, avgy, avgz + 1), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(245, 189, 4), 1));
    player.spawnParticle(Particle.REDSTONE, new Location(world, avgx + 1, avgy + 1, avgz), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(245, 189, 4), 1));
    player.spawnParticle(Particle.REDSTONE, new Location(world, avgx, avgy, avgz + 1), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(245, 189, 4), 1));
    player.spawnParticle(Particle.REDSTONE, new Location(world, avgx + 1, avgy, avgz + 1), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(245, 189, 4), 1));
    player.spawnParticle(Particle.REDSTONE, new Location(world, avgx + 1, avgy, avgz), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(245, 189, 4), 1));
    player.spawnParticle(Particle.REDSTONE, new Location(world, avgx, avgy + 1, avgz + 1), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(245, 189, 4), 1));
    player.spawnParticle(Particle.REDSTONE, new Location((world), minx, miny, minz), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    player.spawnParticle(Particle.REDSTONE, new Location((world), minx, miny, maxz + 1), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    player.spawnParticle(Particle.REDSTONE, new Location((world), maxx + 1, maxy + 1, maxz + 1), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    player.spawnParticle(Particle.REDSTONE, new Location((world), minx, maxy + 1, maxz + 1), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    player.spawnParticle(Particle.REDSTONE, new Location((world), minx, maxy + 1, minz), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    player.spawnParticle(Particle.REDSTONE, new Location((world), maxx + 1, miny, maxz + 1), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    player.spawnParticle(Particle.REDSTONE, new Location((world), maxx + 1, miny, minz), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    player.spawnParticle(Particle.REDSTONE, new Location((world), maxx + 1, maxy + 1, minz), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    for (int a = 0; !(maxx - minx == a); a++) {
      player.spawnParticle(Particle.REDSTONE, new Location(world, minx + a + 1, miny, minz), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    }
    for (int b = 0; !(maxy - miny == b); b++) {
      player.spawnParticle(Particle.REDSTONE, new Location(world, minx, miny + b + 1, minz), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    }
    for (int c = 0; !(maxz - minz == c); c++) {
      player.spawnParticle(Particle.REDSTONE, new Location(world, minx, miny, minz + c + 1), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    }
    for (int d = 0; !(maxx - d == minx); d++) {
      player.spawnParticle(Particle.REDSTONE, new Location(world, maxx - d, maxy + 1, maxz + 1), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    }
    for (int e = 0; !(maxz - e == minz); e++) {
      player.spawnParticle(Particle.REDSTONE, new Location((world), maxx + 1, maxy + 1, maxz - e), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    }
    for (int f = 0; !(maxy - f == miny); f++) {
      player.spawnParticle(Particle.REDSTONE, new Location((world), maxx + 1, maxy - f, maxz + 1), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    }
    for (int g = 0; !(maxx - minx == g); g++) {
      player.spawnParticle(Particle.REDSTONE, new Location(world, minx + g + 1, maxy + 1, minz), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    }
    for (int h = 0; !(maxz - minz == h); h++) {
      player.spawnParticle(Particle.REDSTONE, new Location(world, minx, maxy + 1, minz + h + 1), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    }
    for (int i = 0; !(maxx - i == minx); i++) {
      player.spawnParticle(Particle.REDSTONE, new Location(world, maxx - i, miny, maxz + 1), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    }
    for (int j = 0; !(maxz - j == minz); j++) {
      player.spawnParticle(Particle.REDSTONE, new Location((world), maxx + 1, miny, maxz - j), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    }
    for (int k = 0; !(maxy - k == miny); k++) {
      player.spawnParticle(Particle.REDSTONE, new Location((world), maxx + 1, maxy - k, minz), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    }
    for (int l = 0; !(maxy - l == miny); l++) {
      player.spawnParticle(Particle.REDSTONE, new Location((world), minx, maxy - l, maxz + 1), 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 255, 196), 1));
    }
  }

  private static void serverRadio()
  {
    if (!Cucumbery.using_NoteBlockAPI || CommandSong.radioSongPlayer == null || CommandSong.song == null)
    {
      for (Player onlone : Bukkit.getOnlinePlayers())
      {
        onlone.hideBossBar(serverRadio);
      }
      return;
    }
    RadioSongPlayer radio = CommandSong.radioSongPlayer;
    Song song = CommandSong.song;
    short current = radio.getTick(), max = song.getLength();
    float ratio = Math.min(1f, Math.max(0f, 1f * current / max));
    float speed = song.getSpeed();

    if (!delay)
    {
      delay = true;
      delayTask = Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> delay = false, (long) (160 / speed));
      BossBar.Color[] colors = BossBar.Color.values();
      int ordinal = serverRadio.color().ordinal();
      if (ordinal + 1 >= colors.length)
      {
        ordinal = 0;
      }
      else
      {
        ordinal++;
      }
      serverRadio.color(colors[ordinal]);
    }
    String songName = song.getPath().getName();
    songName = songName.substring(0, songName.length() - 4);
    final String originalName = songName;
    if (songName.length() > 32)
    {
      final String extendName = originalName + "      " + originalName;
      if (fileNameLength < 31 || fileNameLength > extendName.length())
      {
        fileNameLength = 31;
      }
      songName = extendName.substring(fileNameLength - 31, fileNameLength);
      if (!delay2)
      {
        delay2 = true;
        Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> delay2 = false, 8);
        fileNameLength++;
        if (fileNameLength > extendName.length())
        {
          fileNameLength = originalName.length() + 1;
        }
      }
    }
    else
    {
      fileNameLength = -1;
    }

    TextColor textColor = switch (serverRadio.color())
            {
              case PINK -> NamedTextColor.LIGHT_PURPLE;
              case BLUE -> NamedTextColor.DARK_AQUA;
              case RED -> NamedTextColor.RED;
              case GREEN -> NamedTextColor.GREEN;
              case YELLOW -> NamedTextColor.YELLOW;
              case PURPLE -> NamedTextColor.DARK_PURPLE;
              case WHITE -> NamedTextColor.WHITE;
            };
    serverRadio.progress(ratio).name(ComponentUtil.translate((radio.isPlaying() ? "♬" : "■") + " %s" + (radio.getRepeatMode() == RepeatMode.ONE ? " ♺" : ""),
            Component.text(songName, NamedTextColor.WHITE), Constant.JeongsuFloor.format(ratio * 100d) + "%", Constant.Sosu2.format(speed)).color(textColor));

    for (Player online : Bukkit.getOnlinePlayers())
    {
      if (UserData.LISTEN_GLOBAL.getBoolean(online))
      {
        online.showBossBar(serverRadio);
      }
      else
      {
        online.hideBossBar(serverRadio);
      }
    }
  }

  private static void customEnchant(@NotNull Entity entity)
  {
    if (!(entity instanceof LivingEntity livingEntity))
    {
      return;
    }
    EntityEquipment equipment = livingEntity.getEquipment();
    if (equipment == null)
    {
      return;
    }
    ItemStack helmet = equipment.getHelmet();
    if (!ItemStackUtil.itemExists(helmet))
    {
      return;
    }
    ItemMeta itemMeta = helmet.getItemMeta();
  }

  private static void showSpectatorTargetInfoActionbar(@NotNull Player player)
  {
    if (player.getGameMode() != GameMode.SPECTATOR)
    {
      return;
    }
    Entity target = player.getSpectatorTarget();
    if (target == null)
    {
      return;
    }
    if (!UserData.SHOW_SPECTATOR_TARGET_INFO_IN_ACTIONBAR.getBoolean(player))
    {
      return;
    }
    Component message = ComponentUtil.translate("%s 관전 중", target);
    if (target instanceof Player targetPlayer)
    {
      int level = targetPlayer.getLevel();
      float exp = targetPlayer.getExp();
      message = message.append(ComponentUtil.create(" | &aLv." + level + "(" + Constant.Sosu2.format(exp * 100) + "%)"));
    }
    if (target instanceof Damageable && target instanceof Attributable attributable)
    {
      AttributeInstance attributeInstanceMaxHealth = attributable.getAttribute(Attribute.GENERIC_MAX_HEALTH);
      if (attributeInstanceMaxHealth != null)
      {
        Damageable damageable = (Damageable) target;
        double hp = damageable.getHealth();
        double mhp = attributeInstanceMaxHealth.getValue();
        message = message.append(ComponentUtil.create(" | &c" + Constant.Sosu2.format(hp) + "&7/&c" + Constant.Sosu2.format(mhp) + "❤"));
      }

      AttributeInstance attributeInstanceArmor = attributable.getAttribute(Attribute.GENERIC_ARMOR);
      AttributeInstance attributeInstanceArmorToughness = attributable.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);

      if (attributeInstanceArmor != null && attributeInstanceArmorToughness != null)
      {
        double armor = attributeInstanceArmor.getValue();
        double armorToughness = attributeInstanceArmorToughness.getValue();
        message = message.append(ComponentUtil.create(" | &b" + Constant.Sosu2.format(armor) + (armorToughness != 0 ? "(" + Constant.Sosu2.format(armorToughness) + ")" : "") + "⛨"));
      }
    }
    if (target instanceof Player targetPlayer)
    {

      int foodLevel = targetPlayer.getFoodLevel();
      float saturation = targetPlayer.getSaturation();
      float exhaustion = targetPlayer.getExhaustion();

      if (UserData.SHOW_SPECTATOR_TARGET_INFO_IN_ACTIONBAR_TMI_MODE.getBoolean(player))
      {
        message = message.append(ComponentUtil.create(" | &6" + foodLevel + "⛁ " + Constant.Sosu2.format(saturation)
                + "s " + Constant.Sosu2Force.format(Math.max(0, 4 - exhaustion) * 25) + "%&l⚡ " + EXHAUSTION_GUAGE.charAt(Math.max(0, 8 - (int) (exhaustion * 2) - 1))));
      }
      else
      {
        message = message.append(ComponentUtil.create(
                " | &6" + Constant.Sosu2.format(foodLevel + saturation) + (exhaustion != 0 ? "(" + EXHAUSTION_GUAGE.charAt(Math.max(0, 8 - (int) (exhaustion * 2) - 1)) + ")" : "") + "⛁"));
      }
    }
    MessageUtil.sendActionBar(player, message);

  }

  private static String getCooldownActionbar(UUID uuid, ItemStack item, boolean mainHand)
  {
    String returnValue = "";
    String prefixColor = mainHand ? "&6" : "&2";
    String suffix = "&r, ";
    YamlConfiguration configCooldownItemUsage = Variable.cooldownsItemUsage.get(uuid);
    long currentTime = System.currentTimeMillis();
    NBTCompound itemTag = NBTAPI.getMainCompound(item);
    NBTCompound usageTag = NBTAPI.getCompound(itemTag, CucumberyTag.USAGE_KEY);
    NBTCompound cooldownRightClickTag = NBTAPI.getCompound(NBTAPI.getCompound(usageTag, CucumberyTag.USAGE_COMMANDS_RIGHT_CLICK_KEY), CucumberyTag.COOLDOWN_KEY);
    if (cooldownRightClickTag != null)
    {
      try
      {
        String cooldownRightClickTagTag = cooldownRightClickTag.getString(CucumberyTag.TAG_KEY);
        long nextAvailable = configCooldownItemUsage.getLong(cooldownRightClickTagTag);
        long remain = nextAvailable - currentTime;
        if (remain > 0)
        {
          if (remain < 100)
          {
            returnValue += " ";
          }
          else
          {
            String remainTime = Method.timeFormatMilli(remain, remain > 60000 ? 0 : 1);
            returnValue = prefixColor + "남은 우클릭 재사용 대기 시간 : &e" + remainTime + suffix;
          }
        }
      }
      catch (Exception ignored)
      {

      }
    }
    NBTCompound cooldownLeftClickTag = NBTAPI.getCompound(NBTAPI.getCompound(usageTag, CucumberyTag.USAGE_COMMANDS_LEFT_CLICK_KEY), CucumberyTag.COOLDOWN_KEY);
    if (cooldownLeftClickTag != null)
    {
      try
      {
        String cooldownLeftClickTagTag = cooldownLeftClickTag.getString(CucumberyTag.TAG_KEY);
        long nextAvailable = configCooldownItemUsage.getLong(cooldownLeftClickTagTag);
        long remain = nextAvailable - currentTime;
        if (remain > 0)
        {
          if (remain < 100)
          {
            returnValue += " ";
          }
          else
          {
            String remainTime = Method.timeFormatMilli(remain, remain > 60000 ? 0 : 1);
            returnValue += prefixColor + "남은 좌클릭 재사용 대기 시간 : &e" + remainTime + suffix;
          }
        }
      }
      catch (Exception ignored)
      {

      }
    }
    NBTCompound cooldownAttackTag = NBTAPI.getCompound(NBTAPI.getCompound(usageTag, CucumberyTag.USAGE_COMMANDS_ATTACK_KEY), CucumberyTag.COOLDOWN_KEY);
    if (cooldownAttackTag != null)
    {
      try
      {
        String cooldownAttackTagTag = cooldownAttackTag.getString(CucumberyTag.TAG_KEY);
        long nextAvailable = configCooldownItemUsage.getLong(cooldownAttackTagTag);
        long remain = nextAvailable - currentTime;
        if (remain > 0)
        {
          if (remain < 100)
          {
            returnValue += " ";
          }
          else
          {
            String remainTime = Method.timeFormatMilli(remain, remain > 60000 ? 0 : 1);
            returnValue += prefixColor + "남은 공격 재발동 대기 시간 : &e" + remainTime + suffix;
          }
        }
      }
      catch (Exception ignored)
      {

      }
    }
    NBTCompound cooldownConsumeTag = NBTAPI.getCompound(NBTAPI.getCompound(usageTag, CucumberyTag.USAGE_COMMANDS_CONSUME_KEY), CucumberyTag.COOLDOWN_KEY);
    if (cooldownConsumeTag != null)
    {
      try
      {
        String cooldownConsumeTagTag = cooldownConsumeTag.getString(CucumberyTag.TAG_KEY);
        long nextAvailable = configCooldownItemUsage.getLong(cooldownConsumeTagTag);
        long remain = nextAvailable - currentTime;
        if (remain > 0)
        {
          if (remain < 100)
          {
            returnValue += " ";
          }
          else
          {
            String remainTime = Method.timeFormatMilli(remain, remain > 60000 ? 0 : 1);
            returnValue += prefixColor + "남은 섭취 대기 시간 : &e" + remainTime + suffix;
          }
        }
      }
      catch (Exception ignored)
      {

      }
    }
    NBTCompound cooldownResurrectTag = NBTAPI.getCompound(NBTAPI.getCompound(usageTag, CucumberyTag.USAGE_COMMANDS_RESURRECT_KEY), CucumberyTag.COOLDOWN_KEY);
    if (cooldownResurrectTag != null)
    {
      try
      {
        String cooldownResurrectTagTag = cooldownResurrectTag.getString(CucumberyTag.TAG_KEY);
        long nextAvailable = configCooldownItemUsage.getLong(cooldownResurrectTagTag);
        long remain = nextAvailable - currentTime;
        if (remain > 0)
        {
          if (remain < 100)
          {
            returnValue += " ";
          }
          else
          {
            String remainTime = Method.timeFormatMilli(remain, remain > 60000 ? 0 : 1);
            returnValue += prefixColor + "남은 부활 대기 시간 : &e" + remainTime + suffix;
          }
        }
      }
      catch (Exception ignored)
      {

      }
    }
    NBTCompound cooldownSneakTag = NBTAPI.getCompound(NBTAPI.getCompound(usageTag, CucumberyTag.USAGE_COMMANDS_SNEAK_KEY), CucumberyTag.COOLDOWN_KEY);
    if (cooldownSneakTag != null)
    {
      try
      {
        String cooldownSneakTagTag = cooldownSneakTag.getString(CucumberyTag.TAG_KEY);
        long nextAvailable = configCooldownItemUsage.getLong(cooldownSneakTagTag);
        long remain = nextAvailable - currentTime;
        if (remain > 0)
        {
          if (remain < 100)
          {
            returnValue += " ";
          }
          else
          {
            String remainTime = Method.timeFormatMilli(remain, remain > 60000 ? 0 : 1);
            returnValue += prefixColor + "남은 웅크리기 대기 시간 : &e" + remainTime + suffix;
          }
        }
      }
      catch (Exception ignored)
      {

      }
    }
    NBTCompound cooldownSwapTag = NBTAPI.getCompound(NBTAPI.getCompound(usageTag, CucumberyTag.USAGE_COMMANDS_SWAP_KEY), CucumberyTag.COOLDOWN_KEY);
    if (cooldownSwapTag != null)
    {
      try
      {
        String cooldownSneakTagTag = cooldownSwapTag.getString(CucumberyTag.TAG_KEY);
        long nextAvailable = configCooldownItemUsage.getLong(cooldownSneakTagTag);
        long remain = nextAvailable - currentTime;
        if (remain > 0)
        {
          if (remain < 100)
          {
            returnValue += " ";
          }
          else
          {
            String remainTime = Method.timeFormatMilli(remain, remain > 60000 ? 0 : 1);
            returnValue += prefixColor + "남은 스와핑 대기 시간 : &e" + remainTime + suffix;
          }
        }
      }
      catch (Exception ignored)
      {

      }
    }
    NBTCompound customItemTag = NBTAPI.getCompound(itemTag, CucumberyTag.CUSTOM_ITEM_TAG_KEY);
    if (customItemTag != null)
    {
      try
      {
        String tagId = customItemTag.getString(CucumberyTag.ID_KEY);
        if (tagId.equals("railgun"))
        {
          NBTCompound customItemTagTag = customItemTag.getCompound(CucumberyTag.TAG_KEY);
          String railgunCooldownTag = customItemTagTag.getString(CucumberyTag.CUSTOM_ITEM_RAILGUN_COOLDOWN_TAG);
          if (railgunCooldownTag == null || railgunCooldownTag.equals(""))
          {
            railgunCooldownTag = "railgun-default";
          }
          long nextAvailable = configCooldownItemUsage.getLong(railgunCooldownTag);
          long remain = nextAvailable - currentTime;
          if (remain > 0)
          {
            if (remain < 100)
            {
              returnValue += " ";
            }
            else
            {
              String remainTime = Method.timeFormatMilli(remain, remain > 60000 ? 0 : 1);
              returnValue += prefixColor + "남은 재발사 대기 시간 : &e" + remainTime + suffix;
            }
          }
        }
      }
      catch (Exception ignored)
      {

      }
    }
    return returnValue;
  }

  private static void showCooldownActionbar(@NotNull Player player)
  {
    if (player.getGameMode() == GameMode.SPECTATOR)
    {
      return;
    }
    if (!UserData.SHOW_ACTIONBAR_WHEN_ITEM_IS_COOLDOWN.getBoolean(player.getUniqueId()) || UserData.FORCE_HIDE_ACTIONBAR_WHEN_ITEM_IS_COOLDOWN
            .getBoolean(player.getUniqueId()))
    {
      return;
    }
    String actionbar = "";
    ItemStack item = player.getInventory().getItemInOffHand();
    UUID uuid = player.getUniqueId();
    if (ItemStackUtil.itemExists(item))
    {
      actionbar += getCooldownActionbar(uuid, item, false);
    }
    item = player.getInventory().getItemInMainHand();
    if (ItemStackUtil.itemExists(item))
    {
      actionbar += getCooldownActionbar(uuid, item, true);
    }
    if (!actionbar.equals(""))
    {
      if (actionbar.endsWith(", "))
      {
        actionbar = actionbar.substring(0, actionbar.length() - 2);
      }
      MessageUtil.sendActionBar(player, actionbar);
      Variable.playerActionbarCooldownIsShowing.add(uuid);
    }
  }

  private static void inventoryFullNotify()
  {
    for (Player player : Bukkit.getOnlinePlayers())
    {
      if (player.getGameMode() == GameMode.SPECTATOR)
      {
        continue;
      }
      PlayerInventory playerInventory = player.getInventory();
      if (playerInventory.firstEmpty() == -1)
      {
        UUID uuid = player.getUniqueId();
        boolean inventoryContentsAreSame = false;
        if (Variable.playerNotifyIfInventoryIsFullCheckInventory.containsKey(uuid))
        {
          boolean tempSame = true;
          Material[] contents = Variable.playerNotifyIfInventoryIsFullCheckInventory.get(uuid);
          for (int i = 0; i < contents.length; i++)
          {
            Material type = contents[i];
            ItemStack playerItem = playerInventory.getItem(i);
            Material playerType = playerItem != null ? playerItem.getType() : Material.AIR;
            if (type != playerType)
            {
              tempSame = false;
              break;
            }
          }
          inventoryContentsAreSame = tempSame;
        }
        if (!inventoryContentsAreSame && !UserData.NOTIFY_IF_INVENTORY_IS_FULL_FORCE_DISABLE
                .getBoolean(player.getUniqueId()) && UserData.NOTIFY_IF_INVENTORY_IS_FULL
                .getBoolean(player.getUniqueId()) && !Variable.playerNotifyIfInventoryIsFullCooldown.contains(uuid))
        {
          Material[] contents = new Material[36];
          for (int i = 0; i < 36; i++)
          {
            ItemStack item = playerInventory.getItem(i);
            contents[i] = item != null ? item.getType() : Material.AIR;
          }
          Variable.playerNotifyIfInventoryIsFullCheckInventory.put(uuid, contents);
          Variable.playerNotifyIfInventoryIsFullCooldown.add(uuid);
          MessageUtil.sendTitle(player, "", "&c인벤토리 가득 참!", 5, 80, 15);
          MessageUtil.sendMessage(player, Prefix.INFO_WARN, "&c인벤토리가 가득 찼습니다!");
          if (!Variable.playerNotifyChatIfInventoryIsFullStack.containsKey(uuid))
          {
            Variable.playerNotifyChatIfInventoryIsFullStack.put(uuid, 0);
          }
          int currentStack = Variable.playerNotifyChatIfInventoryIsFullStack.get(uuid);
          if (currentStack == 1)
          {
            MessageUtil.info(player, "&r이 메시지와 경고는 &e/메뉴 &r혹은 &e/apsb&r 명령어로 개인 설정(철사 덫 갈고리)에서 비활성화할 수 있습니다");
          }
          currentStack++;
          if (currentStack > 4)
          {
            currentStack = 0;
          }
          Variable.playerNotifyChatIfInventoryIsFullStack.put(uuid, currentStack);
          SoundPlay.playSound(player, Sound.BLOCK_CHEST_OPEN, 1f, 2f);
          Bukkit.getServer().getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> Variable.playerNotifyIfInventoryIsFullCooldown.remove(uuid), 400L);
        }
      }
    }
  }

  /**
   * 플레이어가 손에 들고 있는 아이템은 기간제 설명 업데이트 주기를 20초로 합니다.
   */
  private static void playerExpireHandItem()
  {
    for (Player player : Bukkit.getServer().getOnlinePlayers())
    {
      if (player.getGameMode() == GameMode.SPECTATOR)
      {
        continue;
      }
      PlayerInventory playerInventory = player.getInventory();
      ItemStack mainHand = playerInventory.getItemInMainHand();
      NBTCompound itemTag = NBTAPI.getMainCompound(mainHand);
      @Nullable String expireDate = itemTag == null ? null : itemTag.getString(CucumberyTag.EXPIRE_DATE_KEY);
      if (expireDate != null)
      {
        if (UserData.EVENT_EXCEPTION_ACCESS.getBoolean(player.getUniqueId()))
        {
          continue;
        }
        Method.updateInventory(player, mainHand);
      }
      ItemStack offHand = playerInventory.getItemInOffHand();
      itemTag = NBTAPI.getMainCompound(offHand);
      expireDate = itemTag == null ? null : itemTag.getString(CucumberyTag.EXPIRE_DATE_KEY);
      if (expireDate != null)
      {
        if (UserData.EVENT_EXCEPTION_ACCESS.getBoolean(player.getUniqueId()))
        {
          continue;
        }
        Method.updateInventory(player, offHand);
      }
    }
  }

  /**
   * 플레이어의 인벤토리에 있는 아이템(손에 들고 있는 아이템 제외)의 기간제 설명 업데이트 주기를 1초로 합니다. 만약 GUI가 아닌 인벤토리(일반 상자, 화로, 발사기, 공급기 등)가 열린 상태라면 해당 인벤토리에 있는 아이템 설명도 1초마다 업데이트 합니다.
   */
  private static void playerExpireItem()
  {
    Outter:
    for (Player player : Bukkit.getServer().getOnlinePlayers())
    {
      if (player.getGameMode() == GameMode.SPECTATOR)
      {
        continue;
      }
      if (UserData.EVENT_EXCEPTION_ACCESS.getBoolean(player.getUniqueId()))
      {
        continue;
      }
      PlayerInventory playerInventory = player.getInventory();
      int heldItemSlot = playerInventory.getHeldItemSlot();
      for (int i = 0; i < playerInventory.getSize(); i++)
      {
        if (i == heldItemSlot)
        {
          continue;
        }
        ItemStack item = playerInventory.getItem(i);
        String expireDate = NBTAPI.getString(NBTAPI.getMainCompound(item), CucumberyTag.EXPIRE_DATE_KEY);
        if (expireDate != null && (i != 40 || !ItemStackUtil.itemExists(playerInventory.getItemInOffHand())))
        {
          Method.updateInventory(player, item);
        }
      }

      Inventory openInventoryTop = player.getOpenInventory().getTopInventory();
      if (openInventoryTop.getLocation() != null)
      {
        for (int i = 0; i < openInventoryTop.getSize(); i++)
        {
          ItemStack item = openInventoryTop.getItem(i);
          String expireDate = NBTAPI.getString(NBTAPI.getMainCompound(item), CucumberyTag.EXPIRE_DATE_KEY);
          if (expireDate != null)
          {
            if (UserData.EVENT_EXCEPTION_ACCESS.getBoolean(player.getUniqueId()))
            {
              continue Outter;
            }
            Method.updateInventory(player, item);
          }
        }
      }
    }
  }

  private static void commandBlockPreview(@NotNull Player player)
  {
    if (player.getGameMode() == GameMode.SPECTATOR)
    {
      return;
    }
    if (!player.isOp())
    {
      return;
    }
    ItemStack item = player.getInventory().getItemInMainHand();
    if (!ItemStackUtil.itemExists(item))
    {
      return;
    }
    if (!UserData.SHOW_PREVIEW_COMMAND_BLOCK_COMMAND.getBoolean(player.getUniqueId()))
    {
      return;
    }
    switch (item.getType())
    {
      case COMMAND_BLOCK, REPEATING_COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, NETHERITE_SWORD, DIAMOND_SWORD, GOLDEN_SWORD,
              IRON_SWORD, STONE_SWORD, WOODEN_SWORD, COMPASS, DEBUG_STICK, REDSTONE_BLOCK, WOODEN_AXE, IRON_BLOCK, BARRIER, COMMAND_BLOCK_MINECART, TRIDENT -> {
        Set<Material> transparent = new HashSet<>();
        for (Material material : Material.values())
        {
          if (!material.isOccluding())
          {
            transparent.add(material);
          }
        }
        Block block = player.getTargetBlock(transparent, 10);
        switch (block.getType())
        {
          case COMMAND_BLOCK, REPEATING_COMMAND_BLOCK, CHAIN_COMMAND_BLOCK -> {
            CommandBlock commandBlock = (CommandBlock) block.getState();
            String command = commandBlock.getCommand();
            if (command.isEmpty())
            {
              command = " ";
            }
            MessageUtil.sendActionBar(player, command, false);
          }
        }
      }
    }
  }

  /**
   * 1초마다 플레이어의 레시피 화면을 업데이트 합니다.
   */
  private static void updateCustomRecipeGUI()
  {
    for (Player player : Bukkit.getServer().getOnlinePlayers())
    {
      InventoryView openInventory = player.getOpenInventory();
      String title = openInventory.getTitle();
      // 커스텀 레시피 목록 선택 화면 업데이트
      if (title.startsWith(Constant.CANCEL_STRING + Constant.CUSTOM_RECIPE_RECIPE_LIST_MENU))
      {
        int page = Integer.parseInt(title.split(Method.format("page:", "§"))[1].replace("§", ""));
        RecipeInventoryMainMenu.openRecipeInventory(player, page, false);
      }

      // 커스텀 레시피 선택 화면 업데이트

      if (title.startsWith(Constant.CANCEL_STRING + Constant.CUSTOM_RECIPE_MENU))
      {
        String categorySplitter = Method.format("category:", "§");
        String mainSplitter = Method.format("mainpage:", "§");
        int page = Integer.parseInt(title.split(Method.format("page:", "§"))[1].split(Method.format("category:", "§"))[0].replace("§", ""));
        String category = title.split(categorySplitter)[1].split(mainSplitter)[0].replace("§", "");
        int mainPage = Integer.parseInt(title.split(mainSplitter)[1].replace("§", ""));
        RecipeInventoryCategory.openRecipeInventory(player, mainPage, category, page, false);
      }

      // 커스텀 레시피 제작 화면 업데이트
      if (title.startsWith(Constant.CANCEL_STRING + Method.format("category:", "§")))
      {
        String category = title.split(Method.format("category:", "§"))[1].split(Constant.CUSTOM_RECIPE_CRAFTING_MENU)[0].replace("§", "");
        String mainSplitter = Method.format("mainpage:", "§");
        String categorySplitter = Method.format("categorypage:", "§");
        String recipe = title.split(Method.format("recipe:", "§"))[1].split(mainSplitter)[0].replace("§", "");
        int mainPage = Integer.parseInt(title.split(mainSplitter)[1].split(categorySplitter)[0].replace("§", ""));
        int categoryPage = Integer.parseInt(title.split(categorySplitter)[1].replace("§", ""));
        RecipeInventoryRecipe.openRecipeInventory(player, mainPage, category, categoryPage, recipe, false);
      }
    }
  }

  // GUI 아이템 사용 제한(화로 - 땔감, 제련 아이템/양조기 - 물병 등)
  @SuppressWarnings("all")
  private static void guiItemRestriction(@NotNull Player player)
  {
    // WIP TODO
    boolean isNotImplemented = true;
    if (isNotImplemented)
    {
      return;
    }
    if (UserData.EVENT_EXCEPTION_ACCESS.getBoolean(player.getUniqueId()))
    {
      return;
    }
    InventoryView view = player.getOpenInventory();
    if (view.getType() == InventoryType.FURNACE)
    {
      try
      {
        FurnaceInventory furnaceInv = (FurnaceInventory) view.getTopInventory();
        ItemStack smelting = furnaceInv.getSmelting(), fuel = furnaceInv.getFuel();
        player.sendMessage(
                "smelting : " + (ItemStackUtil.itemExists(smelting) ? smelting.toString() : "null") + ", fuel : " + (ItemStackUtil.itemExists(fuel) ? fuel.toString() : "null"));
      }
      catch (Exception e)
      {
        MessageUtil.broadcastDebug("error");
      }
    }
  }

  private static void armorEquipRestriction(@NotNull Player player)
  {
    if (Cucumbery.getPlugin().getConfig().getBoolean("disable-item-usage-restriction"))
    {
      return;
    }
    GameMode gameMode = player.getGameMode();
    if (gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE)
    {
      guiItemRestriction(player);
      PlayerInventory inv = player.getInventory();
      ItemStack helmet = inv.getHelmet(), chestplate = inv.getChestplate(), leggings = inv.getLeggings(), boots = inv.getBoots();
      ItemStack cursor = player.getItemOnCursor();
      boolean cursorExists = ItemStackUtil.itemExists(cursor);
      if (ItemStackUtil.itemExists(helmet) && NBTAPI.isRestricted(player, helmet, RestrictionType.NO_EQUIP))
      {
        helmet = helmet.clone();
        boolean addItem = false;
        if (cursorExists)
        {
          cursor = cursor.clone();
          if (NBTAPI.isRestricted(player, cursor, RestrictionType.NO_EQUIP))
          {
            inv.addItem(cursor);
            addItem = true;
          }
          else
          {
            inv.setHelmet(cursor);
          }
        }
        else
        {
          inv.setHelmet(null);
        }
        if (!addItem)
        {
          ItemStack cursor2 = player.getItemOnCursor();
          if (ItemStackUtil.itemExists(cursor2))
          {
            inv.addItem(cursor2);
          }
          player.setItemOnCursor(helmet);
        }
        player.updateInventory();
      }
      if (ItemStackUtil.itemExists(chestplate) && NBTAPI.isRestricted(player, chestplate, RestrictionType.NO_EQUIP))
      {
        chestplate = chestplate.clone();
        boolean addItem = false;
        if (cursorExists)
        {
          cursor = cursor.clone();
          if (NBTAPI.isRestricted(player, cursor, RestrictionType.NO_EQUIP))
          {
            inv.addItem(cursor);
            addItem = true;
          }
          else
          {
            inv.setChestplate(cursor);
          }
        }
        else
        {
          inv.setChestplate(null);
        }
        if (!addItem)
        {
          ItemStack cursor2 = player.getItemOnCursor();
          if (ItemStackUtil.itemExists(cursor2))
          {
            inv.addItem(cursor2);
          }
          player.setItemOnCursor(chestplate);
        }
        player.updateInventory();
      }
      if (ItemStackUtil.itemExists(leggings) && NBTAPI.isRestricted(player, leggings, RestrictionType.NO_EQUIP))
      {
        leggings = leggings.clone();
        boolean addItem = false;
        if (cursorExists)
        {
          cursor = cursor.clone();
          if (NBTAPI.isRestricted(player, cursor, RestrictionType.NO_EQUIP))
          {
            inv.addItem(cursor);
            addItem = true;
          }
          else
          {
            inv.setLeggings(cursor);
          }
        }
        else
        {
          inv.setLeggings(null);
        }
        if (!addItem)
        {
          ItemStack cursor2 = player.getItemOnCursor();
          if (ItemStackUtil.itemExists(cursor2))
          {
            inv.addItem(cursor2);
          }
          player.setItemOnCursor(leggings);
        }
        player.updateInventory();

      }
      if (ItemStackUtil.itemExists(boots) && NBTAPI.isRestricted(player, boots, RestrictionType.NO_EQUIP))
      {
        boots = boots.clone();
        boolean addItem = false;
        if (cursorExists)
        {
          cursor = cursor.clone();
          if (NBTAPI.isRestricted(player, cursor, RestrictionType.NO_EQUIP))
          {
            inv.addItem(cursor);
            addItem = true;
          }
          else
          {
            inv.setBoots(cursor);
          }
        }
        else
        {
          inv.setBoots(null);
        }
        if (!addItem)
        {
          ItemStack cursor2 = player.getItemOnCursor();
          if (ItemStackUtil.itemExists(cursor2))
          {
            inv.addItem(cursor2);
          }
          player.setItemOnCursor(boots);
        }
        player.updateInventory();
      }
    }
  }

  public static void itemLore(@NotNull Player player) // 아이템 설명 자동설정
  {
    if (player.getGameMode() == GameMode.SPECTATOR)
    {
      return;
    }
    World world = player.getLocation().getWorld();
    if (!Cucumbery.config.getStringList("no-use-helpful-lore-feature-worlds").contains(world.getName()))
    {
      if (UserData.USE_HELPFUL_LORE_FEATURE.getBoolean(player.getUniqueId()))
      {
        if (player.getOpenInventory().getType() == InventoryType.CARTOGRAPHY)
        {
          ItemStack item = player.getOpenInventory().getTopInventory().getItem(2);
          if (item != null)
          {
            ItemLore.setItemLore(item);
          }
          // 지도 제작대에서 지도 결과물 아이템 실시간 반영
        }
        if (player.getOpenInventory().getType() == InventoryType.STONECUTTER)
        {
          ItemStack item = player.getOpenInventory().getTopInventory().getItem(1);
          if (item != null)
          {
            ItemLore.setItemLore(item);
          }
          // 석재 절단기에서 석재 결과물 아이템 실시간 반영
        }
        ItemStack mainHand = player.getInventory().getItemInMainHand(), offHand = player.getInventory().getItemInOffHand();
        if (ItemStackUtil.itemExists(mainHand))
        {
          ItemMeta itemMeta = mainHand.getItemMeta();
          Material type = mainHand.getType();
          if (type == Material.WRITABLE_BOOK)
          {
            // 야생에서는 불가능. 명령어로 강제로 책의 서명을 없앨때만 생기는 현상
            if (itemMeta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS))
            {
              ItemLore.setItemLore(mainHand);
            }
          }
        }
        if (ItemStackUtil.itemExists(offHand))
        {
          ItemMeta itemMeta = offHand.getItemMeta();
          Material type = offHand.getType();
          if (type == Material.WRITABLE_BOOK)
          {
            // 야생에서는 불가능. 명령어로 강제로 책의 서명을 없앨때만 생기는 현상
            if (itemMeta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS))
            {
              ItemLore.setItemLore(offHand);
            }
          }
        }
      }
    }
  }

  /**
   * 아이템 유효기간 만료 체크를 5초마다 합니다. 만약 기간이 지난 아이템이 존재하면 삭제합니다. 인벤토리를 열고 있는 상태라면 인벤토리에 있는 아이템도 모두 체크합니다.
   */
  private static void expireItemAvailableTime() // 아이템 유효 기간
  {
    for (World world : Bukkit.getServer().getWorlds())
    {
      for (Entity entity : world.getEntities())
      {
        EntityType type = entity.getType();
        if (type == EntityType.DROPPED_ITEM)
        {
          Item itemEntity = (Item) entity;
          ItemStack item = itemEntity.getItemStack();
          String expireDate = NBTAPI.getString(NBTAPI.getMainCompound(item), CucumberyTag.EXPIRE_DATE_KEY);
          if (expireDate != null && Method.isTimeUp(item, expireDate))
          {
            ItemLore.setItemLore(item);
            Location location = itemEntity.getLocation();
            Collection<Entity> nearByEntites = world.getNearbyEntities(location, 10D, 10D, 10D);
            for (Entity nearByEntity : nearByEntites)
            {
              if (nearByEntity instanceof Player player)
              {
                MessageUtil.info(player, ComponentUtil.translate("근처에 떨어져 있는 아이템 [%s](%s)의 유효 기간이 지나서 아이템이 제거되었습니다", item, location));
              }
            }
            item.setAmount(0);
            entity.remove();
          }
        }
        if (type == EntityType.ITEM_FRAME)
        {
          ItemFrame itemFrame = (ItemFrame) entity;
          ItemStack item = itemFrame.getItem();
          String expireDate = NBTAPI.getString(NBTAPI.getMainCompound(item), CucumberyTag.EXPIRE_DATE_KEY);
          if (expireDate != null && Method.isTimeUp(item, expireDate))
          {
            ItemLore.setItemLore(item);
            Location location = itemFrame.getLocation();
            Collection<Entity> nearByEntites = world.getNearbyEntities(location, 10D, 10D, 10D);
            for (Entity nearByEntity : nearByEntites)
            {
              if (nearByEntity instanceof Player player)
              {
                MessageUtil.info(player, ComponentUtil.translate("근처에 있는 아이템 액자(%s)에 설치되어 있는 아이템 %s의 유효 기간이 지나서 아이템이 제거되었습니다", location, item));
              }
            }
            itemFrame.setItem(null);
          }
        }
        if (type == EntityType.ARMOR_STAND)
        {
          ArmorStand armorStand = (ArmorStand) entity;
          EntityEquipment entityEquipment = armorStand.getEquipment();
          if (entityEquipment != null)
          {
            ItemStack item = entityEquipment.getHelmet();
            Location location = armorStand.getLocation();
            Collection<Entity> nearByEntites = world.getNearbyEntities(location, 10D, 10D, 10D);
            String expireDate = NBTAPI.getString(NBTAPI.getMainCompound(item), CucumberyTag.EXPIRE_DATE_KEY);
            if (expireDate != null && Method.isTimeUp(item, expireDate))
            {
              ItemLore.setItemLore(item);
              for (Entity nearByEntity : nearByEntites)
              {
                if (nearByEntity.getType() == EntityType.PLAYER)
                {
                  Player player = (Player) nearByEntity;
                  MessageUtil.info(player, ComponentUtil.translate("근처에 있는 갑옷 거치대(%s)의 머리에 장착되어 있는 아이템 %s의 유효 기간이 지나서 아이템이 제거되었습니다", location, item));
                }
              }
              entityEquipment.setHelmet(null);
            }
            item = entityEquipment.getChestplate();
            expireDate = NBTAPI.getString(NBTAPI.getMainCompound(item), CucumberyTag.EXPIRE_DATE_KEY);
            if (expireDate != null && Method.isTimeUp(item, expireDate))
            {
              ItemLore.setItemLore(item);

              for (Entity nearByEntity : nearByEntites)
              {
                if (nearByEntity instanceof Player player)
                {
                  MessageUtil.info(player, ComponentUtil.translate("근처에 있는 갑옷 거치대(%s)의 몸에 장착되어 있는 아이템 %s의 유효 기간이 지나서 아이템이 제거되었습니다", location, item));
                }
              }
              entityEquipment.setChestplate(null);
            }
            item = entityEquipment.getLeggings();
            expireDate = NBTAPI.getString(NBTAPI.getMainCompound(item), CucumberyTag.EXPIRE_DATE_KEY);
            if (expireDate != null && Method.isTimeUp(item, expireDate))
            {
              ItemLore.setItemLore(item);

              for (Entity nearByEntity : nearByEntites)
              {
                if (nearByEntity instanceof Player player)
                {
                  MessageUtil.info(player, ComponentUtil.translate("근처에 있는 갑옷 거치대(%s)의 다리에 장착되어 있는 아이템 %s의 유효 기간이 지나서 아이템이 제거되었습니다", location, item));
                }
              }
              entityEquipment.setLeggings(null);
            }
            item = entityEquipment.getBoots();
            expireDate = NBTAPI.getString(NBTAPI.getMainCompound(item), CucumberyTag.EXPIRE_DATE_KEY);
            if (expireDate != null && Method.isTimeUp(item, expireDate))
            {
              ItemLore.setItemLore(item);

              for (Entity nearByEntity : nearByEntites)
              {
                if (nearByEntity instanceof Player player)
                {
                  MessageUtil.info(player, ComponentUtil.translate("근처에 있는 갑옷 거치대(%s)의 발에 장착되어 있는 아이템 %s의 유효 기간이 지나서 아이템이 제거되었습니다", location, item));
                }
              }
              entityEquipment.setBoots(null);
            }
            item = entityEquipment.getItemInMainHand();
            expireDate = NBTAPI.getString(NBTAPI.getMainCompound(item), CucumberyTag.EXPIRE_DATE_KEY);
            if (expireDate != null && Method.isTimeUp(item, expireDate))
            {
              ItemLore.setItemLore(item);
              for (Entity nearByEntity : nearByEntites)
              {
                if (nearByEntity instanceof Player player)
                {
                  MessageUtil.info(player, ComponentUtil.translate("근처에 있는 갑옷 거치대(%s)의 주로 사용하는 손에 장착되어 있는 아이템 %s의 유효 기간이 지나서 아이템이 제거되었습니다", location, item));
                }
              }
              entityEquipment.setItemInMainHand(null);
            }
            item = entityEquipment.getItemInOffHand();
            expireDate = NBTAPI.getString(NBTAPI.getMainCompound(item), CucumberyTag.EXPIRE_DATE_KEY);
            if (expireDate != null && Method.isTimeUp(item, expireDate))
            {
              ItemLore.setItemLore(item);
              for (Entity nearByEntity : nearByEntites)
              {
                if (nearByEntity instanceof Player player)
                {
                  MessageUtil.info(player, ComponentUtil.translate("근처에 있는 갑옷 거치대(%s)의 다른 손에 장착되어 있는 아이템 %s의 유효 기간이 지나서 아이템이 제거되었습니다", location, item));
                }
              }
              entityEquipment.setItemInOffHand(null);
            }
          }
        }
      }
    }
    for (Player player : Bukkit.getServer().getOnlinePlayers())
    {
      if (player.getGameMode() == GameMode.SPECTATOR)
      {
        continue;
      }
      if (!UserData.EVENT_EXCEPTION_ACCESS.getBoolean(player.getUniqueId()))
      {
        InventoryView playerOpenInventory = player.getOpenInventory();
        Inventory openInventoryTop = playerOpenInventory.getTopInventory();
        if (openInventoryTop.getLocation() != null)
        {
          for (int i = 0; i < openInventoryTop.getSize(); i++)
          {
            ItemStack item = openInventoryTop.getItem(i);
            if (item != null)
            {
              String expireDate = NBTAPI.getString(NBTAPI.getMainCompound(item), CucumberyTag.EXPIRE_DATE_KEY);
              if (expireDate != null && Method.isTimeUp(item, expireDate))
              {
                MessageUtil.info(player, ComponentUtil.translate("아이템 %s의 유효 기간이 지나서 아이템이 제거되었습니다", item));
                item.setAmount(0);
                player.updateInventory();
                if (player.getOpenInventory().getType() == InventoryType.CRAFTING || player.getOpenInventory().getType() == InventoryType.WORKBENCH)
                {
                  ItemStack tempItem = openInventoryTop.getItem(0);
                  if (tempItem != null)
                  {
                    tempItem.setAmount(0);
                  }
                }
                if (player.getOpenInventory().getType() == InventoryType.ANVIL)
                {
                  ItemStack tempItem = openInventoryTop.getItem(2);
                  if (tempItem != null)
                  {
                    tempItem.setAmount(0);
                  }
                }
              }
            }
          }
        }
        for (int i = 0; i < player.getInventory().getSize(); i++)
        {
          ItemStack item = player.getInventory().getItem(i);
          if (item != null)
          {
            String expireDate = NBTAPI.getString(NBTAPI.getMainCompound(item), CucumberyTag.EXPIRE_DATE_KEY);
            if (expireDate != null && Method.isTimeUp(item, expireDate))
            {
              MessageUtil.info(player, ComponentUtil.translate("아이템 %s의 유효 기간이 지나서 아이템이 제거되었습니다", item));
              item.setAmount(0);
            }
          }
        }
        ItemStack cursor = player.getItemOnCursor();
        String expireDate = NBTAPI.getString(NBTAPI.getMainCompound(cursor), CucumberyTag.EXPIRE_DATE_KEY);
        if (expireDate != null && Method.isTimeUp(cursor, expireDate))
        {
          MessageUtil.info(player, ComponentUtil.translate("아이템 %s의 유효 기간이 지나서 아이템이 제거되었습니다", cursor));
          cursor.setAmount(0);
          player.updateInventory();
        }
      }
    }
  }

  private static void reinforceChancetime()
  {
    try
    {
      Bukkit.getServer().getScheduler().runTaskTimer(Cucumbery.getPlugin(), () ->
      {
        for (Player player : Bukkit.getServer().getOnlinePlayers())
        {
          UUID uuid = player.getUniqueId();
          if (CommandReinforce.CHANCE_TIME.contains(uuid))
          {
            Method.playSound(player, "reinforce_chancetime", 1000F, 1F);
          }
        }
      }, 0L, 19L);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    try
    {
      Bukkit.getServer().getScheduler().runTaskTimer(Cucumbery.getPlugin(), () ->
      {
        for (Player player : Bukkit.getServer().getOnlinePlayers())
        {
          UUID uuid = player.getUniqueId();
          if (CommandReinforce.CHANCE_TIME.contains(uuid))
          {
            MessageUtil.sendTitle(player, "&eCHANCE TIME!", "&b강화 성공 확률 100%!", 0, 40, 0);

            Bukkit.getServer().getScheduler()
                    .runTaskLater(Cucumbery.getPlugin(), () -> MessageUtil.sendTitle(player, "&bCHANCE TIME!", "&e강화 성공 확률 100%!", 0, 40, 0), 5L);
          }
        }
      }, 0L, 10L);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private static void spectateLoop(@NotNull Player player)
  {
    if (player.getOpenInventory().getType() != InventoryType.CRAFTING)
    {
      return;
    }
    if (player.getGameMode() == GameMode.SPECTATOR)
    {
      Entity spectatorTarget = player.getSpectatorTarget();
      if (CustomEffectManager.hasEffect(player, CustomEffectType.CONTINUAL_SPECTATING))
      {
        CustomEffect effect = CustomEffectManager.getEffect(player, CustomEffectType.CONTINUAL_SPECTATING);
        if (effect instanceof PlayerCustomEffect playerCustomEffect)
        {
          Player target = playerCustomEffect.getPlayer();
          if (!target.equals(spectatorTarget) && spectatorTarget instanceof Player newTarget)
          {
            playerCustomEffect.setPlayer(newTarget);
            target = newTarget;
          }
          if (!CustomEffectManager.hasEffect(player, CustomEffectType.CONTINUAL_SPECTATING_EXEMPT) && spectatorTarget == null && !target.isDead() && target.isOnline() && target.isValid() && player.canSee(target))
          {
            player.setSpectatorTarget(null);
            player.teleport(target);
            player.setSpectatorTarget(target);
          }
        }
        else if (spectatorTarget instanceof Player target)
        {
          int dura = effect.getDuration(), ample = effect.getAmplifier();
          effect = new PlayerCustomEffectImple(effect.getType(), effect.getInitDuration(), effect.getInitAmplifier(), effect.getDisplayType(), target);
          effect.setDuration(dura);
          effect.setAmplifier(ample);
          CustomEffectManager.addEffect(player, effect, true);
        }
      }
      if (spectatorTarget instanceof Player target)
      {
        if (!Cucumbery.config.getBoolean("grant-default-permission-to-players") && Method
                .hasPermission(target, Permission.EVENT2_ANTI_SPECTATE, false) && !Method.hasPermission(player, Permission.EVENT2_ANTI_SPECTATE_BYPASS, false))
        {
          player.setSpectatorTarget(null);
          UUID uuid = player.getUniqueId();
          if (!Permission.EVENT_ERROR_HIDE.has(player) && !Variable.antispecateAlertCooldown.contains(uuid))
          {
            Variable.antispecateAlertCooldown.add(uuid);
            MessageUtil.sendTitle(player, "&c관전 불가!", "&r관전할 수 없는 플레이어입니다", 5, 80, 15);
            SoundPlay.playSound(player, Constant.ERROR_SOUND);
            Bukkit.getServer().getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> Variable.antispecateAlertCooldown.remove(uuid), 100L);
          }
          return;
        }
        Location plLoc = player.getLocation(), tarLoc = target.getLocation();
        Collection<Entity> entities = plLoc.getWorld().getNearbyEntities(plLoc, 1D, 1D, 1D);
        if (!entities.contains(target) && plLoc != tarLoc)
        {
          player.setSpectatorTarget(null);
          player.teleport(target);
          player.setSpectatorTarget(target);
          Bukkit.getServer().getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> player.setSpectatorTarget(target), 1L);
        }
      }
    }
  }

  private static void spectateUpdater()
  {
    for (Player player : Bukkit.getServer().getOnlinePlayers())
    {
      if (player.getGameMode() == GameMode.SPECTATOR)
      {
        if (player.getOpenInventory().getType() == InventoryType.CRAFTING)
        {
          if (player.getSpectatorTarget() instanceof Player target)
          {
            UUID uuid = player.getUniqueId();
            if (!Variable.spectateUpdater.containsKey(uuid))
            {
              Variable.spectateUpdater.put(uuid, target.getLocation());
            }
            Location plLoc = player.getLocation(), tarLoc = Variable.spectateUpdater.get(uuid);
            double distance = Method2.distance(plLoc, tarLoc);
            if (distance == -1D || distance > 100D)
            {
              player.setSpectatorTarget(null);
              player.teleport(target);
              Bukkit.getServer().getScheduler().runTaskLater(Cucumbery.getPlugin(), () ->
              {
                player.teleport(target);
                player.setSpectatorTarget(target);
              }, 1L);
              Variable.spectateUpdater.put(uuid, target.getLocation());
            }
          }
        }
      }
    }
  }

  @SuppressWarnings("all")
  private void airPack() // 산소통 기능
  {
    for (Player player : Bukkit.getServer().getOnlinePlayers())
    {
      GameMode gameMode = player.getGameMode();
      if (gameMode != GameMode.SURVIVAL && gameMode != GameMode.ADVENTURE)
      {
        continue;
      }
      if (player.hasPotionEffect(PotionEffectType.WATER_BREATHING) && player.getPotionEffect(PotionEffectType.WATER_BREATHING)
              .getAmplifier() != 5)
      {
        continue;
      }
      Material type = player.getLocation().getWorld().getBlockAt(player.getEyeLocation()).getType();
      if (type != Material.WATER)
      {
        continue;
      }
      boolean mainHand = false;
      ItemStack item = player.getInventory().getItemInMainHand();
      ItemMeta meta = item.getItemMeta();
      if (ItemStackUtil.hasLore(item))
      {
        List<String> lores = meta.getLore();
        for (int i = 0; i < lores.size(); i++)
        {
          String lore = lores.get(i);
          if (lore.startsWith(Constant.AIR_PREFIX + Constant.AIR + " : "))
          {
            long current = 0, max = 0;
            String split = MessageUtil.stripColor(lore.split(Constant.AIR + " : ")[1]);
            try
            {
              current = Long.parseLong(split.split(" / ")[0]);
              max = Long.parseLong(split.split(" / ")[1]);
              mainHand = true;
            }
            catch (NumberFormatException e)
            {
              mainHand = false;
            }
            if (mainHand && current > 0)
            {
              current--;
              lores.set(i, Constant.AIR_PREFIX + Constant.AIR + " : " + current + " / " + max);
              meta.setLore(lores);
              item.setItemMeta(meta);
              player.getInventory().setItemInMainHand(item);
              player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1, 5, true, false));
              player.setRemainingAir(player.getMaximumAir());
              break;
            }
          }
        }
      }
      if (mainHand)
      {
        continue;
      }
      item = player.getInventory().getItemInOffHand();
      meta = item.getItemMeta();
      if (ItemStackUtil.hasLore(item))
      {
        List<String> lores = meta.getLore();
        for (int i = 0; i < lores.size(); i++)
        {
          String lore = lores.get(i);
          if (lore.startsWith(Constant.AIR_PREFIX + Constant.AIR + " : "))
          {
            long current, max;
            String split = MessageUtil.stripColor(lore.split(Constant.AIR + " : ")[1]);
            try
            {
              current = Long.parseLong(split.split(" / ")[0]);
              max = Long.parseLong(split.split(" / ")[1]);
            }
            catch (NumberFormatException e)
            {
              continue;
            }
            if (current > 0)
            {
              current--;
              lores.set(i, Constant.AIR_PREFIX + Constant.AIR + " : " + current + " / " + max);
              meta.setLore(lores);
              item.setItemMeta(meta);
              player.getInventory().setItemInOffHand(item);
              player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1, 5, true, false));
              player.setRemainingAir(player.getMaximumAir());
              break;
            }
          }
        }
      }
    }
  }
}
