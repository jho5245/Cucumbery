package com.jho5245.cucumbery.custom.customeffect;

import com.jho5245.cucumbery.custom.customeffect.children.group.LocationCustomEffect;
import com.jho5245.cucumbery.custom.customeffect.children.group.OfflinePlayerCustomEffect;
import com.jho5245.cucumbery.custom.customeffect.children.group.PlayerCustomEffect;
import com.jho5245.cucumbery.util.no_groups.Method;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a custom effect of {@link PotionEffect}.
 */
public class CustomEffect
{
  private final CustomEffectType effectType;
  private final int initDuration;
  private final int initAmplifier;
  private int duration;
  private int amplifier;
  private DisplayType displayType;
  private @Nullable EffectType effectTypeWrapper = null;

  public CustomEffect(@NotNull CustomEffectType effectType)
  {
    this(effectType, effectType.getDefaultDuration());
  }

  public CustomEffect(@NotNull CustomEffectType effectType, int duration)
  {
    this(effectType, duration, 0);
  }

  public CustomEffect(@NotNull CustomEffectType effectType, int duration, int amplifier)
  {
    this(effectType, duration, amplifier, effectType.getDefaultDisplayType());
  }

  public CustomEffect(@NotNull CustomEffectType effectType, int duration, int amplifier, @NotNull DisplayType displayType)
  {
    this.effectType = effectType;
    if (effectType.isToggle())
    {
      this.duration = -1;
      this.initDuration = -1;
    }
    else
    {
      this.duration = duration;
      this.initDuration = duration;
    }
    this.amplifier = amplifier;
    this.initAmplifier = amplifier;
    this.displayType = displayType;
  }

  public @NotNull CustomEffect foo(@NotNull EffectType wrapper)
  {
    this.effectTypeWrapper = wrapper;
    return this;
  }

  @Nullable
  public EffectType getEffectTypeWrapper()
  {
    return effectTypeWrapper;
  }

  @NotNull
  public CustomEffectType getType()
  {
    return effectType;
  }

  public void tick()
  {
    if (this.duration != -1)
    {
      this.duration--;
    }
  }

  public int getDuration()
  {
    return duration;
  }

  public void setDuration(int duration)
  {
    this.duration = duration;
  }

  public int getAmplifier()
  {
    return amplifier;
  }

  public void setAmplifier(int amplifier)
  {
    this.amplifier = amplifier;
  }

  public int getInitDuration()
  {
    return initDuration;
  }

  public int getInitAmplifier()
  {
    return initAmplifier;
  }

  @NotNull
  public DisplayType getDisplayType()
  {
    return displayType;
  }

  public void setDisplayType(@NotNull DisplayType displayType)
  {
    this.displayType = displayType;
  }

  @NotNull
  public Component getDescription()
  {
    Component description = switch ((this.effectType.getNamespacedKey().getNamespace().equals("minecraft") ? "MINECRAFT_" : "") + effectType.getIdString().toUpperCase())
            {
              case "CURSE_OF_MUSHROOM" -> ComponentUtil.translate("%s ????????? 5????????? ??????????????? ????????? ???????????????", "&e" + ((amplifier + 1) / 10d) + "%");
              case "FEATHER_FALLING" -> ComponentUtil.translate("?????? ????????? ?????? ?????? ?????? ????????? %sm ????????????,", "&e" + ((amplifier + 1) * 5))
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("?????? ???????????? %s ???????????????", "&e" + ((amplifier + 1) * 8) + "%"));
              case "BLESS_OF_SANS" -> ComponentUtil.translate("????????? ???????????? %s ???????????????", "&e" + ((amplifier + 1) * 10) + "%");
              case "SHARPNESS" -> ComponentUtil.translate("????????? ???????????? %s ???????????????", "&e" + (amplifier + 1.5));
              case "SMITE" -> ComponentUtil.translate("????????? ?????? ?????? ??? ????????? ???????????? %s ???????????????", "&e" + Constant.Sosu1.format((amplifier + 1) * 2.5));
              case "BANE_OF_ARTHROPODS" -> ComponentUtil.translate("??????????????? ?????? ?????? ??? ????????? ???????????? %s ????????????,", "&e" + Constant.Sosu1.format((amplifier + 1) * 2.5))
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("%s 4?????? ????????? %s?????? ???????????????", ComponentUtil.translate("&ceffect.minecraft.slowness"), "&a1~" + Constant.Sosu1.format((amplifier + 3) * 0.5)));
              case "INSIDER" -> ComponentUtil.translate("????????? %s?????? ????????????, ?????? ??? ?????? ??????????????????", amplifier + 2)
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("???????????? ????????? ?????? ???????????? ???????????????"));
              case "OUTSIDER" -> ComponentUtil.translate("%s ????????? ?????? ???????????? ???????????? ??????", "&e" + ((amplifier + 1) * 10) + "%")
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("?????? ?????????, ?????? ???????????? ?????? ????????????"));
              case "KINETIC_RESISTANCE" -> ComponentUtil.translate("????????? ?????? ??? ????????? ???????????? ?????? ???????????? %s ???????????????", "&e" + ((amplifier + 1) * 10) + "%")
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("?????? ???????????? ???????????? ????????????"));
              case "ELYTRA_BOOSTER" -> ComponentUtil.translate("????????? ?????? ??? ???????????? ????????? ???")
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("%s ????????? ????????? ???????????? ????????????", "&e" + ((amplifier + 1) * 10) + "%"));
              case "LEVITATION_RESISTANCE" -> ComponentUtil.translate("???????????? ??????????????? %s ?????????", "&e" + ((amplifier + 1) * 10) + "%")
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("?????? ?????? ?????? ????????? ???????????? ????????????"));
              case "UNCRAFTABLE" -> switch (amplifier)
                      {
                        case 0 -> ComponentUtil.translate("?????????????????? ???????????? ????????? ??? ?????? ???????????????");
                        case 1 -> ComponentUtil.translate("??????????????? ???????????? ????????? ??? ?????? ???????????????");
                        default -> ComponentUtil.translate("???????????? ????????? ??? ?????? ???????????????");
                      };
              case "SERVER_RADIO_LISTENING" -> ComponentUtil.translate("?????? ????????? ????????? ????????? ?????? ???????????? %s ???????????????", "&e" + ((amplifier + 2) * 5) + "%");
              case "DODGE" -> ComponentUtil.translate("%s ????????? ????????? ???????????????", "&e" + (amplifier + 1) + "%");
              case "NEWBIE_SHIELD" -> ComponentUtil.translate("????????? ????????? 1?????? ????????? ??????!")
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("?????? ???????????? %s ???????????? ???????????? %s ???????????????", "&e" + (switch (amplifier + 1)
                              {
                                case 1 -> "10%";
                                case 2 -> "20%";
                                default -> "40%";
                              }), "&e" + (switch (amplifier + 1)
                              {
                                case 1 -> "5%";
                                case 2 -> "15%";
                                default -> "25%";
                              })
                      ))
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("????????? ????????? ????????? ?????? ????????? ???????????? 1????????? ????????? ????????? ???????????????"));
              case "WA_SANS" -> ComponentUtil.translate("???????????? ????????? ???????????? ??????")
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("???????????? %s ???????????? ???????????? %s ???????????????", "&e" + ((amplifier + 1) * 3) + "%", "&e" + ((amplifier + 1) * 10) + "%"));
              case "HEALTH_INCREASE" -> ComponentUtil.translate("?????? HP??? %s ???????????????", "&e" + ((amplifier + 1) * 10) + "%");
              case "SPREAD" -> ComponentUtil.translate("?????? %s?????? ?????? ?????? ?????????????????? ????????? ?????? ????????? ????????? ?????? ????????? ????????????", amplifier + 1)
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("?????????, 1??? ?????? 0.08% ????????? ?????? ????????? ???????????? ????????? ??? ????????????"))
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("???, ?????? ????????? 4??? ???????????? ????????? ?????? 1??? ????????? ???????????? ????????????"));
              case "VAR_STOMACHACHE" -> ComponentUtil.translate("???????????? ???????????? ????????? ????????? ???????????? ????????????")
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("????????? ?????? ??????????????? ?????? ???????????? ??? ?????? ??????"))
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("????????? 1??? ????????? 0.1??? ???????????? ???????????????"))
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("?????????, ????????? ????????? 100 ????????? ?????? ??? %s ????????? ???????????????", "&e" + Constant.Sosu1.format((amplifier + 1) * 0.1) + "%"));
              case "VAR_PNEUMONIA" -> ComponentUtil.translate("??? ????????? ?????? ?????? ????????? %s ????????????", "&e" + (amplifier + 1) * 10 + "%")
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("??? ????????? ????????? ????????? ???????????? ????????????"));
              case "VAR_DETOXICATE" -> ComponentUtil.translate("%s, %s, %s, %s ?????? ????????? ????????? ?????? ??????", PotionEffectType.POISON, PotionEffectType.CONFUSION, PotionEffectType.BLINDNESS, PotionEffectType.UNLUCK)
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("?????? ?????? ????????? ?????? ????????? 1?????? ???????????? ???????????????"))
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("%s ????????? ?????? ????????? 2????????? ??????????????? %s ????????? 3????????? ?????????", "&e" + (amplifier + 1) + "%", "&e" + Constant.Sosu1.format((amplifier + 1) * 0.1) + "%"))
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("??? ?????????, %s ????????? ????????? ?????? ????????? 4?????? ????????? ??? ????????????", "&e" + Constant.Sosu4.format((amplifier + 1) * (amplifier + 1) * 0.001) + "%"));
              case "VAR_PODAGRA" -> ComponentUtil.translate("??????????????? ????????? ??? ???????????? ?????? ????????????")
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("????????? ????????? ??? 1??? ????????? ?????? ????????? ????????? ???"))
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("3????????? %s??? ????????? ????????????. ????????? 3.5?????? ????????? ???????????? ?????????", Constant.Sosu1.format(0.2 + (amplifier * 0.1))))
                      .append(Component.text("\n"))
                      .append(ComponentUtil.translate("?????? 1??? ????????? ?????? ??? ????????? ???????????? ????????? ?????? ?????? ???????????? 50% ???????????????"));
              case "ENDER_SLAYER" -> ComponentUtil.translate("%s, %s ?????? %s ?????? ??? ???????????? %s ???????????????", EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.ENDER_DRAGON, "&e" + ((amplifier + 1) * 10) + "%");
              case "BOSS_SLAYER" -> ComponentUtil.translate("?????? ????????? ?????? ??? ???????????? %s ???????????????", "&e" + ((amplifier + 1) * 10) + "%");
              case "EXPERIENCE_BOOST" -> ComponentUtil.translate("????????? ???????????? %s ???????????????", "&e" + ((amplifier + 1) * 5) + "%");
              case "MINECRAFT_SPEED" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.SPEED, duration, amplifier));
              case "MINECRAFT_SLOWNESS" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.SLOW, duration, amplifier));
              case "MINECRAFT_HASTE" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.FAST_DIGGING, duration, amplifier));
              case "MINECRAFT_MINING_FATIGUE" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.SLOW_DIGGING, duration, amplifier));
              case "MINECRAFT_STRENGTH" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, amplifier));
              case "MINECRAFT_WEAKNESS" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.WEAKNESS, duration, amplifier));
              case "MINECRAFT_INSTANT_DAMAGE" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.HARM, duration, amplifier));
              case "MINECRAFT_INSTANT_HEAL" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.HEAL, duration, amplifier));
              case "MINECRAFT_JUMP_BOOST" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.JUMP, duration, amplifier));
              case "MINECRAFT_NAUSEA" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.CONFUSION, duration, amplifier));
              case "MINECRAFT_REGENERATION" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier));
              case "MINECRAFT_RESISTANCE" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, amplifier));
              case "MINECRAFT_FIRE_RESISTANCE" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration, amplifier));
              case "MINECRAFT_WATER_BREATHING" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.WATER_BREATHING, duration, amplifier));
              case "MINECRAFT_BLINDNESS" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.BLINDNESS, duration, amplifier));
              case "MINECRAFT_INVISIBILITY" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.INVISIBILITY, duration, amplifier));
              case "MINECRAFT_NIGHT_VISION" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.NIGHT_VISION, duration, amplifier));
              case "MINECRAFT_HUNGER" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.HUNGER, duration, amplifier));
              case "MINECRAFT_POISON" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.POISON, duration, amplifier));
              case "MINECRAFT_WITHER" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.WITHER, duration, amplifier));
              case "MINECRAFT_HEALTH_BOOST" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.HEALTH_BOOST, duration, amplifier));
              case "MINECRAFT_ABSORPTION" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.ABSORPTION, duration, amplifier));
              case "MINECRAFT_SATURATION" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.SATURATION, duration, amplifier));
              case "MINECRAFT_LEVITATION" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.LEVITATION, duration, amplifier));
              case "MINECRAFT_SLOW_FALLING" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.SLOW_FALLING, duration, amplifier));
              case "MINECRAFT_GLOWING" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.GLOWING, duration, amplifier));
              case "MINECRAFT_LUCK" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.LUCK, duration, amplifier));
              case "MINECRAFT_UNLUCK" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.UNLUCK, duration, amplifier));
              case "MINECRAFT_CONDUIT_POWER" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.CONDUIT_POWER, duration, amplifier));
              case "MINECRAFT_DOLPHINS_GRACE" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, duration, amplifier));
              case "MINECRAFT_BAD_OMEN" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.BAD_OMEN, duration, amplifier));
              case "MINECRAFT_HERO_OF_THE_VILLAGE" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, duration, amplifier));
              case "MINECRAFT_DARKNESS" -> VanillaEffectDescription.getDescription(new PotionEffect(PotionEffectType.DARKNESS, duration, amplifier));
              default -> effectType.getDescription();
            };
    if (this instanceof OfflinePlayerCustomEffect offlinePlayerCustomEffect)
    {
      OfflinePlayer player = offlinePlayerCustomEffect.getOfflinePlayer();
      if (effectType == CustomEffectType.NEWBIE_SHIELD)
      {
        description = description.append(Component.text("\n"));
        description = description.append(Component.text("\n"));
        description = description.append(ComponentUtil.translate("&7????????? ?????? : %s", Method.timeFormatMilli(player.getStatistic(Statistic.PLAY_ONE_MINUTE) * 50L, false)));
      }
    }
    if (this instanceof PlayerCustomEffect playerCustomEffect)
    {
      Player player = playerCustomEffect.getPlayer();
      if (effectType == CustomEffectType.CONTINUAL_SPECTATING)
      {
        description = description.append(Component.text("\n"));
        description = description.append(Component.text("\n"));
        description = description.append(ComponentUtil.translate("&7?????? ?????? ???????????? : %s", player));
      }
    }
    if (this instanceof LocationCustomEffect locationCustomEffect)
    {
      Location location = locationCustomEffect.getLocation();
      if (effectType == CustomEffectType.POSITION_MEMORIZE)
      {
        description = description.append(Component.text("\n"));
        description = description.append(Component.text("\n"));
        description = description.append(ComponentUtil.translate("&7????????? ?????? : %s", location));
      }
    }
    description = ComponentUtil.create(description, effectType.getPropertyDescription());
    return description;
  }

  public boolean isKeepOnDeath()
  {
    return this.effectType.isKeepOnDeath();
  }

  public boolean isKeepOnQuit()
  {
    return this.effectType.isKeepOnQuit();
  }

  public boolean isKeepOnMilk()
  {
    return this.effectType.isKeepOnMilk();
  }

  @NotNull
  protected CustomEffect copy()
  {
    return new CustomEffect(getType(), getInitDuration(), getInitAmplifier(), getDisplayType()).foo(effectTypeWrapper);
  }

  public boolean isHidden()
  {
    return this.effectType.isHidden();
  }

  @SuppressWarnings("all")
  public boolean isTimeHidden()
  {
    return effectType.isTimeHidden() || duration == -1;
  }

  public boolean isTimeHiddenWhenFull()
  {
    return effectType.isTimeHiddenWhenFull() || duration + 1 >= initDuration;
  }

  /**
   * @return ?????? ????????? ???????????? ????????? ???????????? ???????????? ????????? <code>null</code>??? ???????????????.
   */
  @Nullable
  public ItemStack getIcon()
  {
    return this.effectType.getIcon();
  }

  public boolean isHiddenEnum()
  {
    return this.effectType.isHiddenEnum();
  }

  /**
   * ????????? ?????? ??????
   */
  public enum DisplayType
  {
    /**
     * ?????????
     */
    ACTION_BAR,
    /**
     * ???????????? ????????? - Tab ?????? ????????? ??? ????????? ???????????? ????????? ????????? ?????????
     * <p>???????????? ???????????? ????????? ??????????????? ???????????? ?????? ??? ??????
     */
    PLAYER_LIST,
    /**
     * ?????????. ??????????????? ?????? ?????? ???????????? ?????? ??????
     */
    BOSS_BAR,
    /**
     * /effect3 query ???????????? ?????? ????????? GUI????????? ????????? ??????
     */
    GUI,
    /**
     * ???????????? ???????????? ??????. ???????????? /effect3 query [??????] ???????????? ???????????? ?????? ??????
     */
    NONE
  }
}
