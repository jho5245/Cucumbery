package com.jho5245.cucumbery.listeners.entity.customeffect;

import com.jho5245.cucumbery.custom.customeffect.CustomEffect;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectType;
import com.jho5245.cucumbery.events.entity.EntityCustomEffectApplyEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityCustomEffectApply implements Listener
{
  @EventHandler
  public void onEntityCustomEffectApply(EntityCustomEffectApplyEvent event)
  {
    Entity entity = event.getEntity();
    CustomEffect customEffect = event.getCustomEffect();
    CustomEffectType customEffectType = customEffect.getType();
    if (customEffectType.isToggle() && CustomEffectManager.hasEffect(entity, customEffectType))
    {
      CustomEffectManager.removeEffect(entity, customEffectType);
      event.setCancelled(true);
    }
  }
}
