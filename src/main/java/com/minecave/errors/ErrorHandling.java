package com.minecave.errors;

import com.minecave.errors.commands.Commands;
import com.minecave.errors.listener.ExceptionListener;
import lombok.Getter;
import me.ktar5.slackapi.SlackApi;
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
    @Getter
    private SlackApi api = null;

    @Override
    public void onLoad(){
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        api = new SlackApi(this.getConfig().getString("slack.token"));
        instance = this;
        threshold = new ThresholdHandler(
                this.getConfig().getInt("log.threshold.time-to-check"),
                this.getConfig().getInt("log.threshold.logs-allowed"));
        try {
            ExceptionListener.register();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable(){
        threshold.start();
        this.getCommand("error").setExecutor(new Commands());
        if(this.getConfig().getBoolean("debug")){
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                throw new RuntimeException("So, it works eh?");
            }, 150L);
        }
    }

    @Override
    public void onDisable(){
        threshold.stop();
        instance = null;
    }

}
