package com.jho5245.cucumbery.listeners.inventory;

import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.custom.customeffect.CustomEffect;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectType;
import com.jho5245.cucumbery.util.no_groups.Method;
import com.jho5245.cucumbery.util.itemlore.ItemLore;
import com.jho5245.cucumbery.util.nbt.NBTAPI;
import com.jho5245.cucumbery.util.storage.no_groups.CreateItemStack;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig.UserData;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Constant.RestrictionType;
import com.jho5245.cucumbery.util.storage.data.Permission;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PrepareItemCraft implements Listener
{
  @EventHandler
  public void onPrepareItemCraft(PrepareItemCraftEvent event)
  {
    HumanEntity humanEntity = event.getView().getPlayer();
    if (humanEntity.getType() == EntityType.PLAYER)
    {
      Player player = (Player) humanEntity;
      Inventory inventory = event.getInventory();
      InventoryView inventoryView = event.getView();
      InventoryType inventoryType = inventory.getType();
      if (CustomEffectManager.hasEffect(player, CustomEffectType.UNCRAFTABLE) && ItemStackUtil.itemExists(inventoryView.getItem(0)))
      {
        CustomEffect customEffect = CustomEffectManager.getEffect(player, CustomEffectType.UNCRAFTABLE);
        int amplifier = customEffect.getAmplifier();
        ItemStack result = Cucumbery.config.getBoolean("blind-result-when-crafting-with-uncraftable-item") ? null : CreateItemStack.create(Material.BARRIER, 1,
                ComponentUtil.translate(Constant.NO_CRAFT_ITEM_DISPLAYNAME),
                ComponentUtil.translate("&c???????????? ????????? ??? ????????????"),
                true);
        if (inventoryType == InventoryType.CRAFTING && (amplifier == 0 || amplifier == 2))
        {
          inventoryView.setItem(0, result);
          return;
        }
        if (inventoryType == InventoryType.WORKBENCH && (amplifier == 1 || amplifier == 2))
        {
          inventoryView.setItem(0, result);
          return;
        }
      }
      if (!Cucumbery.config.getBoolean("grant-default-permission-to-players"))
      {
        ItemStack originalResult = event.getView().getItem(0);
        if (!ItemStackUtil.itemExists(originalResult)) // ?????? ????????? ???????????? ???????????? ????????? ?????? ?????? ????????? ???????????? ??????
        {
          return;
        }
        ItemStack result = Cucumbery.config.getBoolean("blind-result-when-crafting-with-uncraftable-item") ? null
                : CreateItemStack.create(Material.BARRIER, 1, ComponentUtil.translate(Constant.NO_CRAFT_ITEM_DISPLAYNAME), ComponentUtil.translate("&7???????????? ????????? ????????? ????????????"), true);
        if (!Permission.EVENT_ITEM_CRAFT.has(player))
        {
          event.getView().setItem(0, result);
          return;
        }
        Material material = originalResult.getType();
        if (!player.hasPermission(Permission.EVENT_ITEM_CRAFT + "." + material.toString().toLowerCase()))
        {
          event.getView().setItem(0, result);
          return;
        }
      }
      if (!UserData.EVENT_EXCEPTION_ACCESS.getBoolean(player.getUniqueId()))
      {
        ItemStack originalResult = event.getView().getItem(0);
        if (!ItemStackUtil.itemExists(originalResult)) // ?????? ????????? ???????????? ???????????? ????????? ?????? ?????? ????????? ???????????? ??????
        {
          return;
        }
        ItemStack result = CreateItemStack.create(Material.BARRIER, 1,
                ComponentUtil.translate(Constant.NO_CRAFT_ITEM_DISPLAYNAME),
                ComponentUtil.translate("&c?????? ?????? ???????????? ????????? ????????? ???????????? ???????????? ???????????????"),
                true);
        ItemMeta itemMeta = result.getItemMeta();
        List<Component> lores = itemMeta.lore();
        if (lores == null)
        {
          lores = new ArrayList<>();
        }
        if (event.getInventory().getType() == InventoryType.CRAFTING)
        {
          ItemStack item1 = event.getView().getItem(1);
          ItemStack item2 = event.getView().getItem(2);
          ItemStack item3 = event.getView().getItem(3);
          ItemStack item4 = event.getView().getItem(4);
          boolean yes = false;
          if (NBTAPI.isRestricted(item1, RestrictionType.NO_CRAFT) || NBTAPI.isRestricted(item1, RestrictionType.NO_CRAFT_IN_INVENTORY))
          {
            yes = true;
            if (item1 != null)
            {
              lores.add(ComponentUtil.translate("&e?????? ?????? ????????? (%s)", item1));
            }
          }
          if (NBTAPI.isRestricted(item2, RestrictionType.NO_CRAFT) || NBTAPI.isRestricted(item2, RestrictionType.NO_CRAFT_IN_INVENTORY))
          {
            yes = true;
            if (item2 != null)
            {
              lores.add(ComponentUtil.translate("&e????????? ?????? ????????? (%s)", item2));
            }
          }
          if (NBTAPI.isRestricted(item3, RestrictionType.NO_CRAFT) || NBTAPI.isRestricted(item3, RestrictionType.NO_CRAFT_IN_INVENTORY))
          {
            yes = true;
            if (item3 != null)
            {
              lores.add(ComponentUtil.translate("&e?????? ????????? ????????? (%s)", item3));
            }
          }
          if (NBTAPI.isRestricted(item4, RestrictionType.NO_CRAFT) || NBTAPI.isRestricted(item4, RestrictionType.NO_CRAFT_IN_INVENTORY))
          {
            yes = true;
            if (item4 != null)
            {
              lores.add(ComponentUtil.translate("&e????????? ????????? ????????? (%s)", item4));
            }
          }
          if (yes)
          {
            itemMeta.lore(lores);
            result.setItemMeta(itemMeta);
            if (Cucumbery.config.getBoolean("blind-result-when-crafting-with-uncraftable-item"))
            {
              event.getView().setItem(0, null);
            }
            else
            {
              event.getView().setItem(0, result);
            }
            return;
          }
        }
        else if (event.getInventory().getType() == InventoryType.WORKBENCH)
        {
          ItemStack item1 = event.getView().getItem(1);
          ItemStack item2 = event.getView().getItem(2);
          ItemStack item3 = event.getView().getItem(3);
          ItemStack item4 = event.getView().getItem(4);
          ItemStack item5 = event.getView().getItem(5);
          ItemStack item6 = event.getView().getItem(6);
          ItemStack item7 = event.getView().getItem(7);
          ItemStack item8 = event.getView().getItem(8);
          ItemStack item9 = event.getView().getItem(9);
          boolean yes = false;
          if (NBTAPI.isRestricted(item1, RestrictionType.NO_CRAFT) || NBTAPI.isRestricted(item1, RestrictionType.NO_CRAFT_IN_CRAFTING_TABLE))
          {
            yes = true;
            if (item1 != null)
            {
              lores.add(ComponentUtil.translate("&e?????? ?????? ????????? (%s)", item1));
            }
          }
          if (NBTAPI.isRestricted(item2, RestrictionType.NO_CRAFT) || NBTAPI.isRestricted(item2, RestrictionType.NO_CRAFT_IN_CRAFTING_TABLE))
          {
            yes = true;
            if (item2 != null)
            {
              lores.add(ComponentUtil.translate("&e?????? ?????? ????????? (%s)", item2));
            }
          }
          if (NBTAPI.isRestricted(item3, RestrictionType.NO_CRAFT) || NBTAPI.isRestricted(item3, RestrictionType.NO_CRAFT_IN_CRAFTING_TABLE))
          {
            yes = true;
            if (item3 != null)
            {
              lores.add(ComponentUtil.translate("&e????????? ?????? ????????? (%s)", item3));
            }
          }
          if (NBTAPI.isRestricted(item4, RestrictionType.NO_CRAFT) || NBTAPI.isRestricted(item4, RestrictionType.NO_CRAFT_IN_CRAFTING_TABLE))
          {
            yes = true;
            if (item4 != null)
            {
              lores.add(ComponentUtil.translate("&e?????? ?????? ????????? (%s)", item4));
            }
          }
          if (NBTAPI.isRestricted(item5, RestrictionType.NO_CRAFT) || NBTAPI.isRestricted(item5, RestrictionType.NO_CRAFT_IN_CRAFTING_TABLE))
          {
            yes = true;
            if (item5 != null)
            {
              lores.add(ComponentUtil.translate("&e?????? ????????? (%s)", item5));
            }
          }
          if (NBTAPI.isRestricted(item6, RestrictionType.NO_CRAFT) || NBTAPI.isRestricted(item6, RestrictionType.NO_CRAFT_IN_CRAFTING_TABLE))
          {
            yes = true;
            if (item6 != null)
            {
              lores.add(ComponentUtil.translate("&e????????? ?????? ????????? (%s)", item6));
            }
          }
          if (NBTAPI.isRestricted(item7, RestrictionType.NO_CRAFT) || NBTAPI.isRestricted(item7, RestrictionType.NO_CRAFT_IN_CRAFTING_TABLE))
          {
            yes = true;
            if (item7 != null)
            {
              lores.add(ComponentUtil.translate("&e?????? ????????? ????????? (%s)", item7));
            }
          }
          if (NBTAPI.isRestricted(item8, RestrictionType.NO_CRAFT) || NBTAPI.isRestricted(item8, RestrictionType.NO_CRAFT_IN_CRAFTING_TABLE))
          {
            yes = true;
            if (item8 != null)
            {
              lores.add(ComponentUtil.translate("&e?????? ????????? ????????? (%s)", item8));
            }
          }
          if (NBTAPI.isRestricted(item9, RestrictionType.NO_CRAFT) || NBTAPI.isRestricted(item9, RestrictionType.NO_CRAFT_IN_CRAFTING_TABLE))
          {
            yes = true;
            if (item9 != null)
            {
              lores.add(ComponentUtil.translate("&e????????? ????????? ????????? (%s)", item9));
            }
          }
          if (yes)
          {
            itemMeta.lore(lores);
            result.setItemMeta(itemMeta);
            if (Cucumbery.config.getBoolean("blind-result-when-crafting-with-uncraftable-item"))
            {
              event.getView().setItem(0, null);
            }
            else
            {
              event.getView().setItem(0, result);
            }
            return;
          }
        }
      }
      this.itemLore(event, player);
    }
  }

  private void itemLore(PrepareItemCraftEvent event, Player player)
  {
    if (!Method.usingLoreFeature(player))
    {
      return;
    }
    ItemStack result = event.getView().getItem(0);
    if (!ItemStackUtil.itemExists(result))
    {
      return;
    }
    ItemLore.setItemLore(result, event);
  }
}
