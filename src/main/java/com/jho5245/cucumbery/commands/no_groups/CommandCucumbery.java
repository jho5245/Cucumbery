package com.jho5245.cucumbery.commands.no_groups;

import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.Initializer;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.no_groups.Method;
import com.jho5245.cucumbery.util.plugin_support.CustomRecipeSupport;
import com.jho5245.cucumbery.util.plugin_support.QuickShopSupport;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Permission;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import com.jho5245.cucumbery.util.storage.data.Variable;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig;
import com.jho5245.cucumbery.util.storage.no_groups.PluginLoader;
import com.jho5245.cucumbery.util.storage.no_groups.RecipeChecker;
import com.jho5245.cucumbery.util.storage.no_groups.Updater;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.maxgamer.quickshop.api.QuickShopAPI;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CommandCucumbery implements CommandExecutor, TabCompleter
{
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
  {
    if (!Method.hasPermission(sender, Permission.CMD_MAINCOMMAND, true))
    {
      return true;
    }
    if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(args)))
    {
      return !(sender instanceof BlockCommandSender);
    }
    if (args.length == 0)
    {
      MessageUtil.shortArg(sender, 1, args);
      MessageUtil.commandInfo(sender, label, Method.getUsage(cmd));
      return true;
    }
    else if (args.length == 1)
    {
      switch (args[0])
      {
        case "reload":
          MessageUtil.broadcastDebug(ComponentUtil.translate("%s???(???) /cucumbery reload ????????? ??????", sender));
          Cucumbery.getPlugin().registerConfig();
          Cucumbery.getPlugin().reloadConfig();
          Cucumbery.config = (YamlConfiguration) Cucumbery.getPlugin().getConfig();
          Initializer.saveUserData();
          Initializer.saveBlockPlaceData();
          Initializer.saveItemUsageData();
          CustomEffectManager.save();
          Initializer.loadCustomConfigs();
          Initializer.loadPlayersConfig();

          if (Cucumbery.using_QuickShop)
          {
            Variable.shops.clear();
            try
            {
              QuickShopAPI shopAPI = (QuickShopAPI) Cucumbery.getPlugin().getPluginManager().getPlugin("QuickShop");
              if (shopAPI != null)
              {
                Variable.shops.addAll(shopAPI.getShopManager().getAllShops());
              }
            }
            catch (Exception ignored)
            {

            }
          }
          RecipeChecker.setRecipes();
          for (Player player : Bukkit.getOnlinePlayers())
          {
            Method.updateInventory(player);
          }
          try
          {
            Constant.WARNING_SOUND = Sound.valueOf(Cucumbery.config.getString("sound-const.warning-sound.sound"));
          }
          catch (Exception e)
          {
            Constant.WARNING_SOUND = Sound.ENTITY_ENDERMAN_TELEPORT;
          }
          try
          {
            Constant.ERROR_SOUND = Sound.valueOf(Cucumbery.config.getString("sound-const.error-sound.sound"));
          }
          catch (Exception e)
          {
            Constant.ERROR_SOUND = Sound.BLOCK_ANVIL_LAND;
          }
          Constant.WARNING_SOUND_PITCH = Cucumbery.config.getDouble("sound-const.warning-sound.pitch");
          Constant.WARNING_SOUND_VOLUME = Cucumbery.config.getDouble("sound-const.warning-sound.volume");
          Constant.ERROR_SOUND_PITCH = Cucumbery.config.getDouble("sound-const.error-sound.pitch");
          Constant.ERROR_SOUND_VOLUME = Cucumbery.config.getDouble("sound-const.error-sound.volume");
          MessageUtil.info(sender, "?????? ?????? ????????? ????????????????????????");
          break;
        case "reloaddata":
          MessageUtil.broadcastDebug(ComponentUtil.translate("%s???(???) /cucumbery reloaddata ????????? ??????", sender));
          Bukkit.reloadData();
          MessageUtil.info(sender, "?????? ????????? ????????? ????????????????????????");
          break;
        case "reloadplugin":
          MessageUtil.broadcastDebug(ComponentUtil.translate("%s???(???) /cucumbery reloadplugin ????????? ??????", sender));
          PluginLoader.unload();
          PluginLoader.load(Cucumbery.file);
          MessageUtil.info(sender, "??????????????? ????????????????????????");
          break;
        case "reloadplugin2":
          MessageUtil.broadcastDebug(ComponentUtil.translate("%s???(???) /cucumbery reloadplugin2 ????????? ??????", sender));
          Bukkit.getServer().getPluginManager().disablePlugin(Cucumbery.getPlugin());
          Bukkit.getServer().getPluginManager().enablePlugin(Cucumbery.getPlugin());
          MessageUtil.info(sender, "??????????????? ????????????????????????. 2");
          break;
        case "version":
          MessageUtil.info(sender, "???????????? ?????? : &e" + Cucumbery.getPlugin().getDescription().getVersion());
          break;
        case "update":
          MessageUtil.broadcastDebug(ComponentUtil.translate("%s???(???) /cucumbery update ????????? ??????", sender));
          MessageUtil.sendMessage(sender, Prefix.INFO, "?????? ???????????? ???????????????...");
          if (Updater.defaultUpdater.updateLatest())
          {
            MessageUtil.info(sender, ComponentUtil.translate("?????? Cucumbery ??????????????? ???????????????. ??????????????? ???????????????..."));
          }
          else
          {
            MessageUtil.sendError(sender, ComponentUtil.translate("?????? ?????? ???????????????"));
          }
          break;
        case "update-quickshop-item":
          if (!Cucumbery.using_QuickShop)
          {
            MessageUtil.sendError(sender, "&eQuickShop&r ??????????????? ???????????? ?????? ????????????");
            return true;
          }
          int size = QuickShopSupport.updateQuickShopItems();
          MessageUtil.info(sender, "&eQuickShop&r??? ?????? ?????? ???????????? ???????????? ??????????????? (??? " + size + "???)");
          break;
        case "update-customrecipe-item":
          size = CustomRecipeSupport.updateCustomRecipe();
          MessageUtil.info(sender, "&e?????? ?????????&r??? ?????? ????????? ???????????? ?????? ???????????? ???????????? ???????????????.(??? " + size + "???)");
          break;
        case "purge-user-data-files":
          File userDataFolder = new File(Cucumbery.getPlugin().getDataFolder() + "/data/UserData");
          if (userDataFolder.exists())
          {
            File[] files = userDataFolder.listFiles();
            if (files == null)
            {
              MessageUtil.sendError(sender, "????????? ?????? ???????????? ???????????? ????????????");
              return true;
            }
            int removeSize = 0;
            for (File file : files)
            {
              String fileName = file.getName();

              if (fileName.endsWith(".yml"))
              {
                fileName = fileName.substring(0, fileName.length() - 4);
                if (Method.isUUID(fileName))
                {
                  UUID uuid = UUID.fromString(fileName);
                  if (CustomConfig.UserData.ID.getString(uuid) == null)
                  {
                    if (file.delete())
                    {
                      removeSize++;
                    }
                    else
                    {
                      System.err.println("[Cucumbery] could not delete " + file.getName() + " file!");
                    }
                  }
                }
              }
            }
            if (removeSize > 0)
            {
              Initializer.saveUserData();
              Initializer.loadNicknamesConfig();
              Initializer.loadPlayersConfig();
              MessageUtil.info(sender, "&e" + removeSize + "???&r??? ?????? ???????????? ???????????? ?????? ???????????? ????????? ???????????????");
            }
            else
            {
              MessageUtil.info(sender, "????????? ?????? ???????????? ???????????? ????????????");
            }
          }
          else
          {
            MessageUtil.info(sender, "????????? ?????? ???????????? ???????????? ????????????");
          }
          break;
      }
    }
    else
    {
      MessageUtil.longArg(sender, 1, args);
      MessageUtil.commandInfo(sender, label, Method.getUsage(cmd));
      return true;
    }
    return true;
  }

  @NotNull
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
  {
    if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(true, args), true))
    {
      return Collections.singletonList(args[0]);
    }
    int length = args.length;

    if (length == 1)
    {
      return Method.tabCompleterList(args, "??????", "reload", "reloaddata", "reloadplugin", "version", "update", "update-quickshop-item", "update-customrecipe-item", "purge-user-data-files");
    }
    return Collections.singletonList(Prefix.ARGS_LONG.toString());
  }
}
