package com.example.kat.pollinghelper.fuction.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;

/**
 * Created by KAT on 2016/5/6.
 */
public class PollingProjectConfig {

    public PollingProjectConfig(String name) {
        this.name = name;
        missions = new ArrayList<>();
        scheduledTimes = new TreeSet<SimpleTime>();
    }

    public PollingProjectConfig() {
        this("");
    }

    public ArrayList<PollingMissionConfig> getMissions() {
        return missions;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public TreeSet<SimpleTime> getScheduledTimes() {
        return scheduledTimes;
    }

    public Date getCurrentScheduledTime(long currentMilliseconds, boolean accountTimeZone) {
        if (scheduledTimes == null || scheduledTimes.size() == 0)
            return null;

        SimpleTime currentTime = SimpleTime.from(currentMilliseconds, accountTimeZone);

        SimpleTime lastScheduledTime = scheduledTimes.last();
        for (SimpleTime currentScheduledTime :
                scheduledTimes) {
            if (currentTime.compareTo(currentScheduledTime) == -1)
                break;
            lastScheduledTime = currentScheduledTime;
        }

        return lastScheduledTime.toCurrentScheduleDate(currentMilliseconds, accountTimeZone);
    }

    public String[] getMissionNames() {
        String[] names = null;
        if (!missions.isEmpty()) {
            names = new String[missions.size()];
            for (PollingMissionConfig mission:
                    missions) {
                names[missions.indexOf(mission)] = mission.getName();
            }
        }
        return names;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null)
            return false;

        if (getClass() == o.getClass()) {
            PollingProjectConfig other = (PollingProjectConfig)o;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
        }
        return true;
    }

    private TreeSet<SimpleTime> scheduledTimes;
    private ArrayList<PollingMissionConfig> missions;
    private String description;
    private String name;
}
