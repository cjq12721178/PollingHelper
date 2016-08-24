package com.example.kat.pollinghelper.structure.scout;


/**
 * Created by KAT on 2016/7/18.
 */
public interface ScoutEntity {
    ScoutCellState getState();
    void setState(ScoutCellState state);
}
