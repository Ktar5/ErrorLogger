package com.minecave.errors;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Carter on 7/31/2015.
 */
public class ThresholdHandler implements Runnable{

    private Map<String, Integer> reports;
    private Set<String> halted;
    private int taskId;
    private int thresholdTime, threshold;

    public ThresholdHandler(int thresholdTime, int threshold){
        this.threshold = threshold;
        this.thresholdTime = thresholdTime;
        reports = new HashMap<>();
        halted = new HashSet<>();
    }

    public void start(){
        taskId = ErrorHandling.getInstance().getServer().getScheduler()
                .runTaskTimer(ErrorHandling.getInstance(), this,
                        0L, thresholdTime/*min*/ * 60/*sec*/ * 20/*ticks*/) /*maths*/
                .getTaskId();
    }

    public void stop(){
        ErrorHandling.getInstance().getServer().getScheduler().cancelTask(taskId);
    }

    public void addPlugin(String plugin){
        reports.put(plugin.toLowerCase(), 0);
    }

    public boolean exists(String plugin){
        return reports.containsKey(plugin.toLowerCase());
    }

    public void halt(String plugin){
        if(!isHalted(plugin)){
            halted.add(plugin.toLowerCase());
        }
    }

    public boolean isHalted(String plugin){
        return halted.contains(plugin.toLowerCase());
    }

    public void unhalt(String plugin){
        if(halted.contains(plugin.toLowerCase())){
            halted.remove(plugin.toLowerCase());
        }
    }

    @Override
    public void run() {
        halted.addAll(reports.keySet().stream()
                .filter(key -> reports.get(key) >= threshold)
                .filter(key -> !isHalted(key))
                .collect(Collectors.toList()));
        reports.replaceAll((k, v) -> 0);
    }
}
