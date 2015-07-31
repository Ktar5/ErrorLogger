package com.minecave.errors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Carter on 7/31/2015.
 */
public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(cmd.getName().equalsIgnoreCase("error")){
            if(args.length == 1){
                ThresholdHandler thresh = ErrorHandling.getInstance().getThreshold();
                if(thresh.exists(args[0])){
                    if(thresh.isHalted(args[0])){
                        thresh.unhalt(args[0]);
                        sender.sendMessage(args[0] + " has been unhalted");
                    }else{
                        thresh.halt(args[0]);
                        sender.sendMessage(args[0] + " has been halted");
                    }
                } else  sender.sendMessage(args[0] + " doesn't exist as a plugin");
            } else sender.sendMessage("Usage: /error <plugin name>");
        }
        return false;
    }
}
