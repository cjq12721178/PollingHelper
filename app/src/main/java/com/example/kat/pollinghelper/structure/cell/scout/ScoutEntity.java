package com.example.kat.pollinghelper.structure.cell.scout;


import com.example.kat.pollinghelper.structure.cell.scout.ScoutCellState;

/**
 * Created by KAT on 2016/7/18.
 */
public interface ScoutEntity {
    ScoutCellState getState();
    void setState(ScoutCellState state);
}
