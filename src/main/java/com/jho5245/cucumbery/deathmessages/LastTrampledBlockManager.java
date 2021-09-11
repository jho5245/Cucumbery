package com.jho5245.cucumbery.deathmessages;

import com.jho5245.cucumbery.util.Method;
import com.jho5245.cucumbery.util.itemlore.ItemLore;
import com.jho5245.cucumbery.util.storage.ItemStackUtil;
import com.jho5245.cucumbery.util.storage.data.Variable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class LastTrampledBlockManager
{
  public static void lastTrampledBlock(@NotNull LivingEntity livingEntity, boolean hasChangedBlock)
  {
    if (livingEntity.getFallDistance() >= 4)
    {
      return;
    }
    if (hasChangedBlock)
    {
      Location location = livingEntity.getLocation().add(0, -0.1, 0);
      Block block = location.getBlock();
      Material type = block.getType();
      ItemStack blockItem = null;
      if (ItemStackUtil.isBlockStateMetadatable(type))
      {
        blockItem = ItemStackUtil.getItemStackFromBlock(block);
        if (Method.containsIgnoreCase(type.toString(), "banner", "sign"))
        {
          blockItem = null;
        }
      }
      switch (type)
      {
        case WATER,
                LAVA, POWDER_SNOW,
                CAVE_VINES_PLANT,
                TWISTING_VINES_PLANT,
                WEEPING_VINES_PLANT,
                LADDER, VINE, SCAFFOLDING, END_ROD, BAMBOO,
                TURTLE_EGG -> blockItem = ItemStackUtil.getItemStackFromBlock(block);
        case LIGHT, AIR, CAVE_AIR, VOID_AIR -> {
          return;
        }
      }
      if (livingEntity.isGliding())
      {
        blockItem = null;
      }
      if (blockItem != null)
      {
        ItemMeta itemMeta = blockItem.getItemMeta();
        if (itemMeta instanceof BlockDataMeta blockDataMeta)
        {
          blockDataMeta.setBlockData(block.getBlockData());
          blockItem.setItemMeta(blockDataMeta);
          ItemLore.setItemLore(blockItem);
        }
        Variable.lastTrampledBlock.put(livingEntity.getUniqueId(), blockItem);
        Variable.lastTrampledBlockType.put(livingEntity.getUniqueId(), type);
      }
      else
      {
        Variable.lastTrampledBlock.remove(livingEntity.getUniqueId());
        Variable.lastTrampledBlockType.remove(livingEntity.getUniqueId());
      }
    }
  }
}
