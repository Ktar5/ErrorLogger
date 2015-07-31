package com.minecave.errors;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Carter on 7/29/2015.
 */
public class ErrorHandling extends JavaPlugin{

    @Getter
    private ThresholdHandler threshold;
    @Getter
    private static ErrorHandling instance = null;

    @Override
    public void onEnable(){
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
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
    }

    @Override
    public void onDisable(){
        threshold.stop();
        instance = null;
    }

}
