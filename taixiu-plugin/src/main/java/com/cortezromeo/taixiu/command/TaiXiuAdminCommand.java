package com.cortezromeo.taixiu.command;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.TaiXiuState;
import com.cortezromeo.taixiu.file.InventoryFile;
import com.cortezromeo.taixiu.file.MessageFile;
import com.cortezromeo.taixiu.manager.AutoSaveManager;
import com.cortezromeo.taixiu.manager.TaiXiuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static com.cortezromeo.taixiu.manager.DebugManager.setDebug;
import static com.cortezromeo.taixiu.util.MessageUtil.sendBoardCast;

public class TaiXiuAdminCommand implements CommandExecutor {
    private TaiXiu plugin;

    public TaiXiuAdminCommand(TaiXiu plugin) {
        this.plugin = plugin;
        plugin.getCommand("taixiuadmin").setExecutor((CommandExecutor) this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        FileConfiguration messageF = MessageFile.get();

        if (sender instanceof Player) {
            if (!sender.hasPermission("taixiu.admin")) {
                sendMessage(sender, messageF.getString("no-permission"));
                return false;
            }
        }

        TaiXiuManager manager = TaiXiu.plugin.getManager();

        if (args.length == 1) {
            switch (args[0]) {
                case "changestate":
                    if (manager.getState() == TaiXiuState.PLAYING) {
                        manager.setState(TaiXiuState.PAUSING);
                    } else {
                        manager.setState(TaiXiuState.PLAYING);
                    }
                    sendMessage(sender, messageF.getString("admin-changestate").replace("%state%", manager.getState().toString()));
                    sendBoardCast(messageF.getString("admin-changestate-boardcast")
                            .replaceAll("%playerName%", sender.getName())
                            .replaceAll("%state%", manager.getState().toString()));
                    return false;
                case "reload":

                    TaiXiu.plugin.reloadConfig();
                    MessageFile.reload();
                    InventoryFile.reload();

                    setDebug(TaiXiu.plugin.getConfig().getBoolean("debug"));
                    if (AutoSaveManager.getAutoSaveStatus() && !TaiXiu.plugin.getConfig().getBoolean("auto-save-database.enable")) {
                        AutoSaveManager.stopAutoSave();
                    } else {
                        AutoSaveManager.startAutoSave(TaiXiu.plugin.getConfig().getInt("auto-save-database.time"));
                    }
                    AutoSaveManager.reloadTimeAutoSave();

                    sendMessage(sender, messageF.getString("admin-reload"));
                    return false;
                default:
                    sendMessage(sender, messageF.getString("wrong-argument"));
                    return false;

            }
        }

        if (args.length == 2) {
            switch (args[0]) {
                case "settime":
                    try {
                        int time = Integer.parseInt(args[1]);

                        if (time <= 0) {
                            sendMessage(sender, messageF.getString("admin-invalid-int-input"));
                            return false;
                        }

                        manager.setTime(time);
                        sendMessage(sender, messageF.getString("admin-settime").replace("%time%", String.valueOf(time)));
                        sendBoardCast(messageF.getString("admin-settime-boardcast")
                                .replaceAll("%playerName%", sender.getName())
                                .replaceAll("%time%", String.valueOf(time)));
                    } catch (Exception e) {
                        sendMessage(sender, messageF.getString("admin-invalid-int-input"));
                    }
                    return false;
                default:
                    sendMessage(sender, messageF.getString("wrong-argument"));
                    return false;

            }
        }

        if (args.length == 4) {
            switch (args[0]) {
                case "setresult":

                    if (manager.getSessionData().getResult() != TaiXiuResult.NONE) {
                        sendMessage(sender, "%prefix%&eVui lòng đợi vài giây và xài lại lệnh này!");
                        return false;
                    }

                    try {
                        int dice1 = Integer.parseInt(args[1]);
                        int dice2 = Integer.parseInt(args[2]);
                        int dice3 = Integer.parseInt(args[3]);

                        if (dice1 < 0 || dice2 <0 || dice3 < 0 || dice1 > 6 || dice2 > 6 || dice3 > 6) {
                            sendMessage(sender, messageF.getString("admin-invalid-dice-input"));
                            return false;
                        }

                        manager.resultSeason(manager.getSessionData(), dice1, dice2, dice3);

                        sendMessage(sender, messageF.getString("admin-setresult")
                                .replaceAll("%dice1%", String.valueOf(dice1))
                                .replaceAll("%dice2%", String.valueOf(dice2))
                                .replaceAll("%dice3%", String.valueOf(dice3)));
                        sendBoardCast(messageF.getString("admin-setresult-boardcast")
                                .replaceAll("%playerName%", sender.getName())
                                .replaceAll("%dice1%", String.valueOf(dice1))
                                .replaceAll("%dice2%", String.valueOf(dice2))
                                .replaceAll("%dice3%", String.valueOf(dice3)));

                    } catch (Exception e) {
                        sendMessage(sender, messageF.getString("admin-invalid-dice-input"));
                    }
                    return false;
                default:
                    sendMessage(sender, messageF.getString("wrong-argument"));
                    return false;

            }
        }

        for (String string : messageF.getStringList("command-taixiuadmin")) {
            string = string.replace("%version%", TaiXiu.plugin.getDescription().getVersion());
            sendMessage(sender, string);
        }

        return false;
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(TaiXiu.nms.addColor(message.replace("%prefix%", MessageFile.get().getString("admin-prefix"))));
    }

}
