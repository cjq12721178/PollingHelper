package com.example.kat.pollinghelper.ui.structure;

import com.example.kat.pollinghelper.fuction.config.SimpleTime;

import java.io.Serializable;

/**
 * Created by KAT on 2016/6/29.
 */
public class ProjectTimeInfo implements Serializable {

    public ProjectTimeInfo(String projectName, SimpleTime pollingTime) {
        if (projectName == null || pollingTime == null)
            throw new NullPointerException();
        this.projectName = projectName;
        this.pollingTime = pollingTime;
    }

    public String getProjectName() {
        return projectName;
    }

    public ProjectTimeInfo setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public SimpleTime getPollingTime() {
        return pollingTime;
    }

    public ProjectTimeInfo setPollingTime(SimpleTime pollingTime) {
        this.pollingTime = pollingTime;
        return this;
    }

    @Override
    public int hashCode() {
        return pollingTime.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null)
            return false;

        if (getClass() == o.getClass()) {
            ProjectTimeInfo other = (ProjectTimeInfo)o;
            if (pollingTime == null) {
                if (other.pollingTime != null)
                    return false;
            } else if (!pollingTime.equals(other.pollingTime))
                return false;

            if (projectName == null) {
                if (other.projectName != null)
                    return false;
            } else if (!projectName.equals(other.projectName))
                return false;
        } else {
            return false;
        }

        return true;
    }

    private String projectName;
    private SimpleTime pollingTime;
}
