package com.example.kat.pollinghelper.structure.scout;

/**
 * Created by KAT on 2016/5/18.
 */
public enum ScoutCellType {
    PCLIT_PROJECT_ENTITY(6),
    PCLIT_PROJECT_VIRTUAL(5),
    PCLIT_MISSION_ENTITY(4),
    PCLIT_MISSION_VIRTUAL(3),
    PCLIT_ITEM_ENTITY(2),
    PCLIT_ITEM_VIRTUAL(1);

    ScoutCellType(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static ScoutCellType from(int level) {
        switch (level) {
            case 1:return PCLIT_ITEM_VIRTUAL;
            case 2:return PCLIT_ITEM_ENTITY;
            case 3:return PCLIT_MISSION_VIRTUAL;
            case 4:return PCLIT_MISSION_ENTITY;
            case 5:return PCLIT_PROJECT_VIRTUAL;
            case 6:return PCLIT_PROJECT_ENTITY;
            default:return null;
        }
    }

    private final int level;
}
