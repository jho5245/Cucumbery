package com.jho5245.cucumbery.custom.customeffect;

import com.jho5245.cucumbery.custom.customeffect.CustomEffect.DisplayType;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class TypeBuilder
{
  private @Nullable String shortenTranslationKey;

  private int maxAmplifier, defaultDuration = 20 * 30, customModelData;

  private boolean isBuffFreezable = true;
  private boolean isKeepOnDeath;
  private boolean isKeepOnMilk = true;
  private boolean isKeepOnQuit = true;
  private boolean isRealDuration;
  private boolean isRemoveable = true;
  private boolean isNegative;
  private boolean isInstant;
  private boolean isToggle;
  private boolean isHidden;
  private boolean isTimeHidden;
  private boolean isTimeHiddenWhenFull;
  private boolean isEnumHidden;
  private boolean isStackDisplayed;

  private Component description = Component.empty();

  private @Nullable ItemStack icon;

  private DisplayType defaultDisplayType = DisplayType.BOSS_BAR;
  
  @NotNull
  public TypeBuilder nonBuffFreezable()
  {
    this.isBuffFreezable = false;
    return this;
  }
  
  @NotNull
  public TypeBuilder keepOnDeath()
  {
    this.isKeepOnDeath = true;
    return this;
  }

  @NotNull
  public TypeBuilder removeOnMilk()
  {
    this.isKeepOnMilk = false;
    return this;
  }

  @NotNull
  public TypeBuilder removeOnQuit()
  {
    this.isKeepOnQuit = false;
    return this;
  }

  @NotNull
  public TypeBuilder realDuration()
  {
    this.isRealDuration = true;
    return this;
  }

  @NotNull
  public TypeBuilder nonRemovable()
  {
    this.isRemoveable = false;
    return this;
  }

  @NotNull
  public TypeBuilder negative()
  {
    this.isNegative = true;
    return this;
  }

  @NotNull
  public TypeBuilder instant()
  {
    this.isInstant = true;
    return this;
  }

  @NotNull
  public TypeBuilder toggle()
  {
    this.isToggle = true;
    return this;
  }

  /**
   * 명령어 탭 목록에서도 보이지 않고 효과 리스트에도 보이지 않음
   * @return this
   */
  @NotNull
  public TypeBuilder hidden()
  {
    this.isHidden = true;
    this.isEnumHidden = true;
    return this;
  }

  @NotNull
  public TypeBuilder timeHidden()
  {
    this.isTimeHidden = true;
    return this;
  }

  @NotNull
  public TypeBuilder timeHiddenWhenFull()
  {
    this.isTimeHiddenWhenFull = true;
    return this;
  }

  @NotNull
  public TypeBuilder enumHidden()
  {
    this.isEnumHidden = true;
    return this;
  }

  @NotNull
  public TypeBuilder stackDisplayed()
  {
    this.isStackDisplayed = true;
    return this;
  }

  @NotNull
  public TypeBuilder maxAmplifier(int maxAmplifier)
  {
    this.maxAmplifier = maxAmplifier;
    return this;
  }

  @NotNull
  public TypeBuilder defaultDuration(int defaultDuration)
  {
    this.defaultDuration = defaultDuration;
    return this;
  }

  @NotNull
  public TypeBuilder customModelData(int customModelData)
  {
    this.customModelData = customModelData;
    return this;
  }

  @NotNull
  public TypeBuilder description(@NotNull Component description)
  {
    this.description = description;
    return this;
  }

  @NotNull
  public TypeBuilder icon(@NotNull ItemStack icon)
  {
    this.icon = icon;
    return this;
  }

  @NotNull
  public TypeBuilder defaultDisplayType(@NotNull DisplayType defaultDisplayType)
  {
    this.defaultDisplayType = defaultDisplayType;
    return this;
  }

  @NotNull
  public TypeBuilder shortenTransltionKey(@NotNull String s)
  {
    this.shortenTranslationKey = s;
    return this;
  }

  @Nullable
  public String getShortenTranslationKey()
  {
    return shortenTranslationKey;
  }

  public boolean isBuffFreezable()
  {
    return isBuffFreezable;
  }

  public boolean isKeepOnDeath()
  {
    return isKeepOnDeath;
  }
  public boolean isKeepOnMilk()
  {
    return isKeepOnMilk;
  }
  
  public boolean isKeepOnQuit()
  {
    return isKeepOnQuit;
  }
  
  public boolean isRealDuration()
  {
    return isRealDuration;
  }
  
  public boolean isRemoveable()
  {
    return !isNegative && isRemoveable;
  }
  
  public boolean isNegative()
  {
    return isNegative;
  }

  public int getMaxAmplifier()
  {
    return maxAmplifier;
  }

  public int getDefaultDuration()
  {
    return defaultDuration;
  }

  public Component getDescription()
  {
    return description;
  }

  public int getCustomModelData()
  {
    return customModelData;
  }

  @Nullable
  public ItemStack getIcon()
  {
    return icon;
  }

  public boolean isInstant()
  {
    return isInstant;
  }

  public boolean isToggle()
  {
    return isToggle;
  }

  public boolean isHidden()
  {
    return isHidden;
  }

  public boolean isTimeHidden()
  {
    return isTimeHidden;
  }

  public boolean isTimeHiddenWhenFull()
  {
    return isTimeHiddenWhenFull;
  }

  public boolean isEnumHidden()
  {
    return isEnumHidden;
  }

  public boolean isStackDisplayed()
  {
    return isStackDisplayed;
  }

  public DisplayType getDefaultDisplayType()
  {
    return defaultDisplayType;
  }
}
