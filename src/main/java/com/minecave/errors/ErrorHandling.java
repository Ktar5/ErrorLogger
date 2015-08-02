package com.minecave.errors;

import com.minecave.errors.commands.Commands;
import com.minecave.errors.listener.ExceptionListener;
import lombok.Getter;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Carter on 7/29/2015.
 */
public class ErrorHandling extends JavaPlugin{

    @Getter
    private ThresholdHandler threshold;
    @Getter
    private static ErrorHandling instance = null;
    private SlackApi api = null;

    @Override
    public void onEnable(){
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        api = new SlackApi(this.getConfig().getString("slack.token"));
        instance = this;
        threshold = new ThresholdHandler(
                this.getConfig().getInt("threshold.time-to-check"),
                this.getConfig().getInt("threshold.logs-allowed"));
        try {
            ExceptionListener.register();
        } catch (Exception e) {
            e.printStackTrace();
        }
        threshold.start();
        this.getCommand("error").setExecutor(new Commands());

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            throw new RuntimeException("This is test to see if this thing works");
        }, 150L);
    }

    @Override
    public void onDisable(){
        threshold.stop();
        instance = null;
    }

    public void send(String message){
        api.call(new SlackMessage("Version: " + this.getServer().getBukkitVersion() + "\\n" +
                "IP: " + this.getServer().getIp() + ":" + this.getServer().getPort() + "\\n" +
                "Server: " +  this.getServer().getName() + " / " + this.getServer().getServerName() + "\\n" +
                message));
    }

}
