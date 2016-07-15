package com.example.kat.pollinghelper.utility;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by KAT on 2016/7/11.
 */
public interface TreeNode<E extends TreeNode> {
    //注意，请将生成的convertView以parent为root，但attachToRoot为false，即返回的必须是子view
    View getView(Context context, View convertView, ViewGroup parent);
    boolean isExpanded();
    void setExpanded(boolean expanded);
    List<E> getChildren();
    //viewType值必须小于viewTypeCount
    int getViewType();
}
