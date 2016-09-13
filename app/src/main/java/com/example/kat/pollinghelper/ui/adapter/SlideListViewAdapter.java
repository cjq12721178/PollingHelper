package com.example.kat.pollinghelper.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.bean.scout.ScoutCell;
import com.example.kat.pollinghelper.bean.scout.ScoutCellType;
import com.example.kat.pollinghelper.ui.layout.SlideListItem;
import com.example.kat.pollinghelper.ui.listview.SlideMenuListView;

import java.util.List;

/**
 * Created by KAT on 2016/5/18.
 */
public class SlideListViewAdapter extends BaseAdapter implements SlideMenuListView.OnItemClickListener {

    private enum ViewHolderType {
        VHT_ENTITY,
        VHT_VIRTUAL;
    }

    private class ViewHolderEntity {
        private TextView lable;
        private TextView content;
        private TextView delete;
        private SlideListItem item;
    }

    private class ViewHolderVirtual {
        private TextView add;
    }

    public interface OnTextViewClickListener {
        void onClick(View view, ScoutCell listItem);
    }

    public SlideListViewAdapter(Context context, List<ScoutCell> datas) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        listItems = datas;
        missionIndent = context.getResources().getInteger(R.integer.polling_config_listitem_indent);
        itemIndent = missionIndent * 2;
    }

    @Override
    public void onItemClick(SlideMenuListView view, MotionEvent event) {
        int position = view.pointToPosition((int)event.getX(), (int)event.getY());
        SlideListItem listItem = (SlideListItem)view.getChildAt(position);
        if (event.getX() + listItem.getScrollX() < ((LinearLayout)listItem.getChildAt(0)).getChildAt(0).getRight()) {
            if (lableClickListener != null) {
                lableClickListener.onClick(listItem, getItem(position));
            }
        } else {
            if (contentClickListener != null) {
                contentClickListener.onClick(listItem, getItem(position));
            }
        }
    }

    public void setLableClickListener(OnTextViewClickListener lableClickListener) {
        this.lableClickListener = lableClickListener;
    }

    public void setContentClickListener(OnTextViewClickListener contentClickListener) {
        this.contentClickListener = contentClickListener;
    }

    public void setDeleteClickListener(OnTextViewClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }

    public void setAddClickListener(OnTextViewClickListener addClickListener) {
        this.addClickListener = addClickListener;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public ScoutCell getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return ViewHolderType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isEntity() ? ViewHolderType.VHT_ENTITY.ordinal() : ViewHolderType.VHT_VIRTUAL.ordinal();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ScoutCell currentListItem = getItem(position);
        if (currentListItem.isEntity()) {
            convertView = getEntityConvertView(convertView);
            final ViewHolderEntity viewHolderEntity = (ViewHolderEntity) convertView.getTag();
            setEntityTextContent(viewHolderEntity, currentListItem);
            setDeleteClickListener(viewHolderEntity, currentListItem);
            setEntityLayoutFromListItemType(viewHolderEntity, currentListItem);
        } else {
            convertView = getVirtualConvertView(convertView);
            final ViewHolderVirtual viewHolderVirtual = (ViewHolderVirtual) convertView.getTag();
            setVirtualTextContent(viewHolderVirtual, currentListItem);
            setAddClickListener(viewHolderVirtual, currentListItem);
            setVirtualLayoutFromListItemType(viewHolderVirtual, currentListItem);
        }

        return convertView;
    }

    private View getEntityConvertView(View convertView) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listitem_polling_config_entity, null);
            ViewHolderEntity viewHolderEntity = new ViewHolderEntity();
            viewHolderEntity.lable = (TextView) convertView.findViewById(R.id.tv_polling_config_item);
            viewHolderEntity.content = (TextView) convertView.findViewById(R.id.tv_polling_config_name);
            viewHolderEntity.delete = (TextView) convertView.findViewById(R.id.tv_polling_config_delete);
            viewHolderEntity.item = (SlideListItem) convertView.findViewById(R.id.li_polling_config_entity);
            convertView.setTag(viewHolderEntity);
        }
        return convertView;
    }

    private View getVirtualConvertView(View convertView) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listitem_polling_config_virtual, null);
            ViewHolderVirtual viewHolderVirtual = new ViewHolderVirtual();
            viewHolderVirtual.add = (TextView) convertView.findViewById(R.id.tv_polling_config_add);
            convertView.setTag(viewHolderVirtual);
        }
        return convertView;
    }

    private void setVirtualTextContent(ViewHolderVirtual viewHolderVirtual, ScoutCell currentListItem) {
        viewHolderVirtual.add.setText(currentListItem.getContent());
    }

    private void setAddClickListener(ViewHolderVirtual viewHolderVirtual, final ScoutCell currentListItem) {
        viewHolderVirtual.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addClickListener != null) {
                    addClickListener.onClick(v, currentListItem);
                }
            }
        });
    }

    private void setDeleteClickListener(final ViewHolderEntity viewHolderEntity, final ScoutCell currentListItem) {
        viewHolderEntity.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteClickListener != null) {
                    deleteClickListener.onClick(v, currentListItem);
                    viewHolderEntity.item.reset();
                }
            }
        });
    }

    private void setEntityTextContent(ViewHolderEntity viewHolderEntity, ScoutCell currentListItem) {
        viewHolderEntity.lable.setText(currentListItem.getLable());
        viewHolderEntity.content.setText(currentListItem.getContent());
        viewHolderEntity.delete.setText(currentListItem.getHideMenuLable());
    }

    private void setVirtualLayoutFromListItemType(ViewHolderVirtual viewHolderVirtual, ScoutCell currentListItem) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(viewHolderVirtual.add.getLayoutParams());
        if (currentListItem.getType() == ScoutCellType.PCLIT_PROJECT_VIRTUAL) {
            params.setMargins(0, 0, 0, 0);
        } else if (currentListItem.getType() == ScoutCellType.PCLIT_MISSION_VIRTUAL) {
            params.setMargins(missionIndent, 0, 0, 0);
        } else {
            params.setMargins(itemIndent, 0, 0, 0);
        }
        viewHolderVirtual.add.setLayoutParams(params);
    }

    private void setEntityLayoutFromListItemType(ViewHolderEntity viewHolderEntity, ScoutCell currentListItem) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(viewHolderEntity.lable.getLayoutParams());
        if (currentListItem.getType() == ScoutCellType.PCLIT_PROJECT_ENTITY) {
            viewHolderEntity.content.setBackgroundResource(R.drawable.style_textview_content_project);
            params.setMargins(0, 0, 0, 0);
        } else if (currentListItem.getType() == ScoutCellType.PCLIT_MISSION_ENTITY) {
            viewHolderEntity.content.setBackgroundResource(R.drawable.style_textview_content_mission);
            params.setMargins(missionIndent, 0, 0, 0);
        } else {
            viewHolderEntity.content.setBackgroundResource(R.drawable.style_textview_content_item);
            params.setMargins(itemIndent, 0, 0, 0);
        }
        viewHolderEntity.lable.setLayoutParams(params);
    }

    private final int missionIndent;
    private final int itemIndent;
    private Context context;
    private LayoutInflater layoutInflater;
    private List<ScoutCell> listItems;
    private OnTextViewClickListener lableClickListener;
    private OnTextViewClickListener contentClickListener;
    private OnTextViewClickListener deleteClickListener;
    private OnTextViewClickListener addClickListener;
}