package com.example.kat.pollinghelper.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.kat.pollinghelper.R;

/**
 * Created by KAT on 2016/6/7.
 */
public class ListDialog extends DialogFragment {

    public interface OnContentItemClickListener {
        void onClick(int position);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_with_listview, null);
        lvContent = (ListView)view.findViewById(R.id.lv_item_config_dialog_content);
        lvContent.setAdapter(adapter);
        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onContentItemClickListener != null) {
                    onContentItemClickListener.onClick(position);
                    dismiss();
                }
            }
        });
        builder.setTitle(getTag());
        builder.setView(view);
        return builder.create();
    }

    public void setContentAdapter(ListAdapter adapter) {
        this.adapter = adapter;
    }

    public void setOnContentItemClickListener(OnContentItemClickListener onContentItemClickListener) {
        this.onContentItemClickListener = onContentItemClickListener;
    }

    private OnContentItemClickListener onContentItemClickListener;
    private ListAdapter adapter;
    private ListView lvContent;
}
