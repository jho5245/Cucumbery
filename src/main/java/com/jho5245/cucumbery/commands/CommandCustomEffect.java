package com.jho5245.cucumbery.commands;

import com.jho5245.cucumbery.customeffect.CustomEffect;
import com.jho5245.cucumbery.customeffect.CustomEffect.DisplayType;
import com.jho5245.cucumbery.customeffect.CustomEffectGUI;
import com.jho5245.cucumbery.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.customeffect.CustomEffectType;
import com.jho5245.cucumbery.util.MessageUtil;
import com.jho5245.cucumbery.util.Method;
import com.jho5245.cucumbery.util.SelectorUtil;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Permission;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CommandCustomEffect implements CommandExecutor, TabCompleter
{
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
  {
    boolean failure = !(sender instanceof BlockCommandSender);
    if (!Method.hasPermission(sender, Permission.CMD_CUSTOM_EFFECT, true))
    {
      return true;
    }
    if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(args)))
    {
      return failure;
    }
    int length = args.length;
    if (length == 0)
    {
      MessageUtil.shortArg(sender, 1, args);
      MessageUtil.commandInfo(sender, label, "<give|clear|query> ...");
      return failure;
    }
    switch (args[0])
    {
      case "query" -> {
        if (length > 2)
        {
          MessageUtil.longArg(sender, 2, args);
          MessageUtil.commandInfo(sender, label, "query" + (sender instanceof Player ? " [개체]" : " <개체>"));
          return failure;
        }
        if (!(sender instanceof Player) && length < 2)
        {
          MessageUtil.shortArg(sender, 2, args);
          MessageUtil.commandInfo(sender, label, "query <개체>");
          return failure;
        }
        Entity target = (Entity) sender;
        boolean gui = true;
        if (length == 2)
        {
          target = SelectorUtil.getEntity(sender, args[1]);
          gui = false;
        }
        if (target == null)
        {
          return failure;
        }
        queryEffect(sender, target, gui);
        return true;
      }
      case "clear" -> {
        if (length > 4)
        {
          MessageUtil.longArg(sender, 4, args);
          MessageUtil.commandInfo(sender, label, args[0] + (sender instanceof Player ? " [개체]" : " <개체>") + " [효과 종류] [명령어 출력 숨김 여부]");
          return failure;
        }
        if (!(sender instanceof Player) && length < 2)
        {
          MessageUtil.shortArg(sender, 2, args);
          MessageUtil.commandInfo(sender, label, args[0] + " <개체> [효과 종류] [명령어 출력 숨김 여부]");
          return failure;
        }
        Entity target = (Entity) sender;
        CustomEffectType effectType = null;
        boolean hideOutput = false;
        if (length == 1)
        {
          if (CustomEffectManager.hasEffects(target))
          {
            CustomEffectManager.clearEffects(target);
            MessageUtil.info(sender, "모든 효과를 제거했습니다.");
            MessageUtil.sendAdminMessage(sender, null, ComponentUtil.translate("[%s: 모든 효과를 제거했습니다.]", sender));
            return true;
          }
          else
          {
            MessageUtil.sendError(sender, "가지고 있는 효과가 없습니다.");
            return failure;
          }
        }
        List<Entity> entities = SelectorUtil.getEntities(sender, args[1]);
        if (entities == null)
        {
          MessageUtil.commandInfo(sender, label, "clear &c<개체>&p [효과 종류] [명령어 출력 숨김 여부]");
          return failure;
        }
        if (length >= 3)
        {
          try
          {
            effectType = CustomEffectType.valueOf(args[2].toUpperCase());
          }
          catch (Exception e)
          {
            MessageUtil.wrongArg(sender, 3, args);
            MessageUtil.commandInfo(sender, label, "clear <개체> &c[효과 종류]&p [명령어 출력 숨김 여부]");
            return failure;
          }
        }
        if (length >= 4)
        {
          if (!MessageUtil.isBoolean(sender, args, 4, true))
          {
            MessageUtil.commandInfo(sender, label, "clear <개체> [효과 종류] &c[명령어 출력 숨김 여부]");
            return failure;
          }
          hideOutput = Boolean.parseBoolean(args[3]);
        }
        return clearEffect(sender, entities, effectType, hideOutput) || failure;
      }
      case "give" -> {
        if (length < 3)
        {
          MessageUtil.shortArg(sender, 3, args);
          MessageUtil.commandInfo(sender, label, "give <개체> <효과> [지속 시간(초)] [강도] [표시 유형] [명령어 출력 숨김 여부]");
          return failure;
        }
        if (length > 7)
        {
          MessageUtil.longArg(sender, 7, args);
          MessageUtil.commandInfo(sender, label, "give <개체> <효과> [지속 시간(초)] [강도] [표시 유형] [명령어 출력 숨김 여부]");
          return failure;
        }
        List<Entity> entities = SelectorUtil.getEntities(sender, args[1]);
        if (entities == null)
        {
          MessageUtil.commandInfo(sender, label, "give &c<개체>&p <효과> [지속 시간(초)] [강도] [표시 유형] [명령어 출력 숨김 여부]");
          return failure;
        }
        boolean force = args[2].endsWith("--force");
        if (force)
        {
          args[2] = args[2].substring(0, args[2].length() - 7);
        }
        CustomEffectType customEffectType;
        try
        {
          customEffectType = CustomEffectType.valueOf(args[2].toUpperCase());
        }
        catch (Exception e)
        {
          MessageUtil.wrongArg(sender, 3, args);
          MessageUtil.commandInfo(sender, label, "give <개체> &c<효과>&p [지속 시간(초)] [강도] [표시 유형] [명령어 출력 숨김 여부]");
          return failure;
        }
        int duration = customEffectType.getDefaultDuration(), amplifier = 0;
        DisplayType displayType = customEffectType.getDefaultDisplayType();
        boolean hideOutput = false;
        if (length >= 4)
        {
          if (args[3].equals("infinite"))
          {
            duration = -1;
          }
          else if (!args[3].equals("default"))
          {
            if (!MessageUtil.isDouble(sender, args[3], true))
            {
              MessageUtil.commandInfo(sender, label, "give <개체> <효과> &c[지속 시간(초)]&p [강도] [표시 유형] [명령어 출력 숨김 여부]");
              return failure;
            }
            double durationInput = Double.parseDouble(args[3]);
            duration = (int) (durationInput * 20);
            if (!MessageUtil.checkNumberSize(sender, durationInput, 0.05, Integer.MAX_VALUE / 20d))
            {
              MessageUtil.commandInfo(sender, label, "give <개체> <효과> &c[지속 시간(초)]&p [강도] [표시 유형] [명령어 출력 숨김 여부]");
              return failure;
            }
          }
        }
        if (length >= 5)
        {
          if (args[4].equals("max"))
          {
            amplifier = customEffectType.getMaxAmplifier();
          }
          else if (!MessageUtil.isInteger(sender, args[4], true))
          {
            MessageUtil.commandInfo(sender, label, "give <개체> <효과> [지속 시간(초)] &c[강도]&p [표시 유형] [명령어 출력 숨김 여부]");
            return failure;
          }
          else
          {
            amplifier = Integer.parseInt(args[4]);
            if (!MessageUtil.checkNumberSize(sender, amplifier, 0, customEffectType.getMaxAmplifier()))
            {
              MessageUtil.commandInfo(sender, label, "give <개체> <효과> [지속 시간(초)] &c[강도]&p [표시 유형] [명령어 출력 숨김 여부]");
              return failure;
            }
          }
        }
        if (length >= 6)
        {
          try
          {
            displayType = DisplayType.valueOf(args[5].toUpperCase());
          }
          catch (Exception e)
          {
            MessageUtil.wrongArg(sender, 6, args);
            MessageUtil.commandInfo(sender, label, "give <개체> <효과> [지속 시간(초)] [강도] &c[표시 유형]&p [명령어 출력 숨김 여부]");
            return failure;
          }
        }
        if (length >= 7)
        {
          if (!MessageUtil.isBoolean(sender, args, 7, true))
          {
            MessageUtil.commandInfo(sender, label, "give <개체> <효과> [지속 시간(초)] [강도] [표시 유형] &c[명령어 출력 숨김 여부]");
            return failure;
          }
          hideOutput = Boolean.parseBoolean(args[6]);
        }
        return giveEffect(sender, entities, customEffectType, duration, amplifier, displayType, hideOutput, force) || failure;
      }
      default -> {
        MessageUtil.wrongArg(sender, 1, args);
        MessageUtil.commandInfo(sender, label, "&c<give|clear|query>&p ...");
        return failure;
      }
    }
  }

  public boolean giveEffect(@NotNull CommandSender sender, @NotNull List<Entity> entities, @NotNull CustomEffectType customEffectType, int duration, int amplifier, @NotNull DisplayType displayType, boolean hideOutput, boolean force)
  {
    CustomEffect customEffect = new CustomEffect(customEffectType, duration, amplifier, displayType);
    List<Entity> successEntities = new ArrayList<>();
    for (Entity entity : entities)
    {
      if (CustomEffectManager.addEffect(entity, customEffect, force))
      {
        successEntities.add(entity);
      }
    }
    boolean successEntitiesIsEmpty = successEntities.isEmpty();
    if (!hideOutput)
    {
      List<Entity> failureEntities = new ArrayList<>(entities);
      failureEntities.removeAll(successEntities);
      if (!failureEntities.isEmpty())
      {
        MessageUtil.sendWarnOrError(successEntitiesIsEmpty, sender,
                ComponentUtil.translate("%s에게 %s 효과를 적용할 수 없습니다. (대상이 효과를 받을 수 없는 상태이거나 더 강한 효과를 가지고 있습니다.)", failureEntities, customEffect));
      }
      if (!successEntitiesIsEmpty)
      {
        MessageUtil.info(sender, ComponentUtil.translate("%s에게 %s 효과를 적용했습니다.", successEntities, customEffect));
        MessageUtil.info(successEntities, ComponentUtil.translate("%s이(가) 당신에게 %s 효과를 적용했습니다.", sender, customEffect));
        MessageUtil.sendAdminMessage(sender, new ArrayList<>(successEntities), ComponentUtil.translate("[%s: %s에게 %s 효과를 적용했습니다.]", sender, successEntities, customEffect));
      }
    }
    return !successEntitiesIsEmpty;
  }

  public boolean clearEffect(@NotNull CommandSender sender, @NotNull List<Entity> entities, @Nullable CustomEffectType customEffectType, boolean hideOutput)
  {
    boolean all = customEffectType == null;
    List<Entity> successEntities = new ArrayList<>();
    for (Entity entity : entities)
    {
      if (all)
      {
        if (CustomEffectManager.clearEffects(entity))
        {
          successEntities.add(entity);
        }
      }
      else
      {
        if (CustomEffectManager.removeEffect(entity, customEffectType))
        {
          successEntities.add(entity);
        }
      }
    }
    boolean successEntitiesIsEmpty = successEntities.isEmpty();
    if (!hideOutput)
    {
      List<Entity> failureEntities = new ArrayList<>(entities);
      failureEntities.removeAll(successEntities);
      if (!failureEntities.isEmpty())
      {
        if (all)
        {
          MessageUtil.sendWarnOrError(successEntitiesIsEmpty, sender,
                  ComponentUtil.translate("%s은(는) 효과를 가지고 있지 않습니다.", failureEntities));
        }
        else
        {
          MessageUtil.sendWarnOrError(successEntitiesIsEmpty, sender,
                  ComponentUtil.translate("%s은(는) %s 효과를 가지고 있지 않습니다.", failureEntities, customEffectType));
        }
      }
      if (!successEntitiesIsEmpty)
      {
        if (all)
        {
          MessageUtil.info(sender, ComponentUtil.translate("%s의 모든 효과를 제거했습니다.", successEntities));
          MessageUtil.info(successEntities, ComponentUtil.translate("%s이(가) 당신의 모든 효과를 제거했습니다.", sender));
          MessageUtil.sendAdminMessage(sender, new ArrayList<>(successEntities), ComponentUtil.translate("[%s: %s의 모든 효과를 제거했습니다.]", sender, successEntities));
        }
        else
        {
          MessageUtil.info(sender, ComponentUtil.translate("%s의 %s 효과를 제거했습니다.", successEntities, customEffectType));
          MessageUtil.info(successEntities, ComponentUtil.translate("%s이(가) 당신의 %s 효과를 제거했습니다.", sender, customEffectType));
          MessageUtil.sendAdminMessage(sender, new ArrayList<>(successEntities), ComponentUtil.translate("[%s: %s의 %s 효과를 제거했습니다.]", sender, successEntities, customEffectType));
        }
      }
    }
    return !successEntitiesIsEmpty;
  }

  public void queryEffect(@NotNull CommandSender sender, @NotNull Entity entity, boolean gui)
  {
    if (sender instanceof Player player && gui)
    {
      CustomEffectGUI.openGUI(player, true);
      return;
    }
    boolean hasVanilla = entity instanceof LivingEntity livingEntity && !livingEntity.getActivePotionEffects().isEmpty(), hasCustom = CustomEffectManager.hasEffects(entity);

    if (hasVanilla)
    {
      LivingEntity livingEntity = (LivingEntity) entity;
      Collection<PotionEffect> potionEffects = livingEntity.getActivePotionEffects();
      MessageUtil.sendMessage(sender, Prefix.INFO_CUSTOM_EFFECT, ComponentUtil.translate("%s은(는) %s개의 포션 효과를 가지고 있습니다: %s", entity,
              potionEffects.size(), CustomEffectManager.getVanillaDisplay(potionEffects, true)));
    }
    else
    {
      MessageUtil.sendMessage(sender, Prefix.INFO_CUSTOM_EFFECT, ComponentUtil.translate("%s은(는) 포션 효과를 가지고 있지 않습니다.", entity));
    }
    if (hasCustom)
    {
      List<CustomEffect> customEffects = CustomEffectManager.getEffects(entity);
      MessageUtil.sendMessage(sender, Prefix.INFO_CUSTOM_EFFECT, ComponentUtil.translate("%s은(는) %s개의 커스텀 효과를 가지고 있습니다: %s", entity,
              customEffects.size(), CustomEffectManager.getDisplay(customEffects, true)));
    }
    else
    {
      MessageUtil.sendMessage(sender, Prefix.INFO_CUSTOM_EFFECT, ComponentUtil.translate("%s은(는) 커스텀 효과를 가지고 있지 않습니다.", entity));
    }
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
      return Method.tabCompleterList(args, "<인수>", "give", "clear", "query");
    }
    switch (args[0])
    {
      case "query" -> {
        if (length == 2)
        {
          return Method.tabCompleterEntity(sender, args, "[개체]");
        }
      }
      case "clear" -> {
        if (length == 2)
        {
          return Method.tabCompleterEntity(sender, args, "[개체]", true);
        }
        List<Entity> entities = SelectorUtil.getEntities(sender, args[1], false);
        if (entities == null)
        {
          return Collections.singletonList(Prefix.NO_ENTITY.toString());
        }
        if (!args[2].equals(""))
        {
          try
          {
            CustomEffectType.valueOf(args[2].toUpperCase());
          }
          catch (Exception e)
          {
            return Collections.singletonList("'" + args[2] + "'" + MessageUtil.getFinalConsonant(args[2], MessageUtil.ConsonantType.은는) + " 잘못되거나 알 수 없는 효과입니다.");
          }
        }
        if (length == 3)
        {
          List<String> list = new ArrayList<>();
          for (Entity entity : entities)
          {
            List<CustomEffect> customEffects = CustomEffectManager.getEffects(entity);
            for (CustomEffect customEffect : customEffects)
            {
              String effect = customEffect.getEffectType().toString().toLowerCase();
              list.add(effect);
            }
          }
          if (list.isEmpty())
          {
            return Collections.singletonList(MessageUtil.stripColor(ComponentUtil.serialize(ComponentUtil.translate("%s에게 효과가 없습니다.", entities))));
          }
          return Method.tabCompleterList(args, list, "[효과]");
        }
        if (length == 4)
        {
          return Method.tabCompleterBoolean(args, "[명령어 출력 숨김 여부]");
        }
      }
      case "give" -> {
        if (length == 2)
        {
          return Method.tabCompleterEntity(sender, args, "<개체>", true);
        }
        boolean force = args[2].endsWith("--force");
        if (force)
        {
          args[2] = args[2].substring(0, args[2].length() - 7);
        }
        String effect = args[2].toUpperCase();
        if (length == 3)
        {
          try
          {
            CustomEffectType customEffectType = CustomEffectType.valueOf(effect);
            if (force)
            {
              return Collections.singletonList(customEffectType.translationKey() + " 효과를 강제로 적용합니다.");
            }
            else
            {
              return Method.tabCompleterList(args, Method.addAll(CustomEffectType.values(), args[2] + "--force"), "<효과>");
            }
          }
          catch (Exception ignored)
          {

          }
          return Method.tabCompleterList(args, CustomEffectType.values(), "<효과>");
        }
        CustomEffectType effectType;
        try
        {
          effectType = CustomEffectType.valueOf(effect);
        }
        catch (Exception e)
        {
          return Collections.singletonList("'" + args[2] + "'" + MessageUtil.getFinalConsonant(args[2], MessageUtil.ConsonantType.은는) + " 잘못되거나 알 수 없는 효과입니다.");
        }
        if (length == 4)
        {
          int defaultDuration = effectType.getDefaultDuration();
          return Method.tabCompleterDoubleRadius(args, 0.05, Integer.MAX_VALUE / 20d, "[지속 시간(초)]", "infinite", "default",
                  "default(기본값, " + (defaultDuration == -1 ? "무제한" : Method.timeFormatMilli(defaultDuration * 50L, true, 2)) + ")");
        }
        if (length == 5)
        {
          int maxAmplifier = effectType.getMaxAmplifier();
          return Method.tabCompleterIntegerRadius(args, 0, maxAmplifier, "[농도 레벨]", "max", "max(최댓값, " + maxAmplifier + ")");
        }
        if (length == 6)
        {
          List<String> list = new ArrayList<>(Method.enumToList(DisplayType.values()));
          list.add(effectType.getDefaultDisplayType().toString().toLowerCase() + "(기본값)");
          return Method.tabCompleterList(args, list, "[표시 유형]");
        }
        if (length == 7)
        {
          return Method.tabCompleterBoolean(args, "[명령어 출력 숨김 여부]");
        }
      }
    }
    return Collections.singletonList(Prefix.ARGS_LONG.toString());
  }
}















