package com.example.kat.pollinghelper.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.fuction.config.PollingMissionConfig;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.ui.dialog.EditDialog;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItem;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemClause;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemMissionEntity;
import com.example.kat.pollinghelper.ui.structure.PollingConfigListItemProjectEntity;
import com.example.kat.pollinghelper.ui.structure.PollingConfigState;

import java.util.ArrayList;
import java.util.List;

public class PollingMissionConfigActivity extends ManagedActivity {

    private enum MissionAdapterLayoutType {
        MALT_DOUBLE_TEXTVIEW,
        MALT_TEXTVIEW_IMAGEVIEW
    }

    private class MissionConfigAdapter extends BaseAdapter {

        private class TextViewHolder {
            private TextView lable;
            private TextView content;
        }

        private class ImageViewHolder {
            private TextView lable;
            private ImageView content;
        }

        public MissionConfigAdapter(Context context) {
            missionConfigInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return itemClauses.size();
        }

        @Override
        public PollingConfigListItemClause getItem(int position) {
            return itemClauses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return MissionAdapterLayoutType.values().length;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getLabel() != getString(R.string.ui_tv_mission_config_label_device_image) ?
                    MissionAdapterLayoutType.MALT_DOUBLE_TEXTVIEW.ordinal() : MissionAdapterLayoutType.MALT_TEXTVIEW_IMAGEVIEW.ordinal();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PollingConfigListItemClause currentItemClause = getItem(position);
            if (getItemViewType(position) == MissionAdapterLayoutType.MALT_DOUBLE_TEXTVIEW.ordinal()) {
                final TextViewHolder textViewHolder;
                if (convertView == null) {
                    convertView = missionConfigInflater.inflate(R.layout.listitem_polling_config_double_textview, null);
                    textViewHolder = new TextViewHolder();
                    textViewHolder.lable = (TextView)convertView.findViewById(R.id.tv_polling_config_lable);
                    textViewHolder.content = (TextView)convertView.findViewById(R.id.tv_polling_config_content);
                    convertView.setTag(textViewHolder);
                } else {
                    textViewHolder = (TextViewHolder)convertView.getTag();
                }
                textViewHolder.lable.setText(currentItemClause.getLabel());
                textViewHolder.content.setText(currentItemClause.getContentString());
                if (currentItemClause.isModified()) {
                    textViewHolder.lable.getPaint().setFakeBoldText(true);
                    textViewHolder.content.getPaint().setFakeBoldText(true);
                }
            } else {
                final ImageViewHolder imageViewHolder;
                if (convertView == null) {
                    convertView = missionConfigInflater.inflate(R.layout.listitem_polling_config_textview_imageview, null);
                    imageViewHolder = new ImageViewHolder();
                    imageViewHolder.lable = (TextView)convertView.findViewById(R.id.tv_mission_config_label);
                    imageViewHolder.content = (ImageView)convertView.findViewById(R.id.iv_mission_config_device);
                    convertView.setTag(imageViewHolder);
                } else {
                    imageViewHolder = (ImageViewHolder)convertView.getTag();
                }
                imageViewHolder.lable.setText(currentItemClause.getLabel());
                if (currentItemClause.getContent() != null) {
                    imageViewHolder.content.setImageBitmap(getBitmapFromByteArray((byte[])currentItemClause.getContent()));
                } else {
                    imageViewHolder.content.setImageResource(R.drawable.ic_device_empty);
                }
                if (currentItemClause.isModified()) {
                    imageViewHolder.lable.getPaint().setFakeBoldText(true);
                }
            }
            return convertView;
        }

        private LayoutInflater missionConfigInflater;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling_mission_config);
    }

    @Override
    protected void onInitializeBusiness() {
        importMissionEntity();
        createEditDialog();
        initializeMissionConfigListView();
    }

    private void createEditDialog() {
        editDialog = new EditDialog();
        editDialog.setOnClickPositiveListener(onEditDialogPositiveClickListener);
    }

    private void initializeMissionConfigListView() {
        missionConfigAdapter = new MissionConfigAdapter(this);
        ListView listView = (ListView)findViewById(R.id.lv_mission_config);
        listView.setAdapter(missionConfigAdapter);
        listView.setOnItemClickListener(missionConfigClickListener);
    }

    private void importMissionEntity() {
        PollingConfigListItem listItem = (PollingConfigListItem)getArgument(ArgumentTag.AT_CONFIG_LIST_ITEM_CURRENT_CLICKED);
        if (listItem.isEntity()) {
            missionEntity = (PollingConfigListItemMissionEntity)listItem;
        } else {
            missionEntity = new PollingConfigListItemMissionEntity(this, new PollingMissionConfig());
            missionEntity.setState(PollingConfigState.PCS_NEW);
        }

        //currentProjectEntities = operationInfo.getCurrentProjectEntities();
        currentProjectEntities = (List<PollingConfigListItemProjectEntity>)getArgument(ArgumentTag.AT_LIST_PROJECT_ENTITY);

        itemClauses = new ArrayList<>();
        itemClauses.add(new PollingConfigListItemClause(getString(R.string.ui_tv_mission_config_label_name),
                missionEntity.getMissionConfig().getName()));
        itemClauses.add(new PollingConfigListItemClause(getString(R.string.ui_tv_mission_config_label_description),
                missionEntity.getMissionConfig().getDescription()));
        itemClauses.add(new PollingConfigListItemClause(getString(R.string.ui_tv_mission_config_label_device_image),
                missionEntity.getMissionConfig().getDeviceImageData()));
    }

    private AdapterView.OnItemClickListener missionConfigClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentItemClause = itemClauses.get(position);
            if (currentItemClause.getLabel() != getString(R.string.ui_tv_mission_config_label_device_image)) {
                editDialog.show(getSupportFragmentManager(), currentItemClause.getLabel(), currentItemClause.getContentString());
            } else {
                openAlbum();
            }
        }
    };

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            Uri selectedDeviceImage = data.getData();
            if (selectedDeviceImage != null) {
                currentItemClause.setContent(uriToByteArray(selectedDeviceImage));
                missionConfigAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (missionEntity.getState() == PollingConfigState.PCS_UNKNOWN) {
            promptMessage(R.string.ui_prompt_mission_config_unknown_error);
            setResult(RESULT_CANCELED);
        } else {
            if (!updateMissionEntity()) {
                return;
            }
            if (missionEntity.getState() == PollingConfigState.PCS_NEW) {
                //operationInfo.setNewListItemEntity(missionEntity);
                putArgument(ArgumentTag.AT_CONFIG_LIST_ITEM_ENTITY_NEW, missionEntity);
            }
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    private boolean updateMissionEntity() {
        PollingConfigListItemClause missionNameClause = getContent(R.string.ui_tv_mission_config_label_name);
        PollingConfigListItemClause descriptionClause = getContent(R.string.ui_tv_mission_config_label_description);
        PollingConfigListItemClause deviceImageDataClause = getContent(R.string.ui_tv_mission_config_label_device_image);

        if (missionNameClause.getContentString().isEmpty()) {
            promptMessage(R.string.ui_prompt_mission_name_empty);
            return false;
        }

        if (isMissionNameRepetition(missionNameClause.getContentString())) {
            promptMessage(R.string.ui_prompt_mission_name_repetition);
            return false;
        }

        if (deviceImageDataClause.getContent() == null) {
            promptMessage(R.string.ui_prompt_device_image_empty);
            return false;
        }

        PollingMissionConfig missionConfig = missionEntity.getMissionConfig();
        missionConfig.setName((String)missionNameClause.getContent());
        if (missionEntity.getState() == PollingConfigState.PCS_NEW) {
            missionEntity.setName(missionConfig.getName());
        }
        missionConfig.setDescription((String)descriptionClause.getContent());
        missionConfig.setDeviceImageData((byte[])deviceImageDataClause.getContent());

        if (missionEntity.getState() == PollingConfigState.PCS_INVARIANT) {
            if (isMissionConfigModified()) {
                missionEntity.setState(PollingConfigState.PCS_MODIFIED);
            }
        }

        return true;
    }

    public boolean isMissionConfigModified() {
        boolean result = false;
        for (PollingConfigListItemClause itemClause :
                itemClauses) {
            if (itemClause.isModified()) {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean isMissionNameRepetition(String newMissionName) {
        if (missionEntity.getState() != PollingConfigState.PCS_NEW) {
            if (missionEntity.getMissionConfig().getName().equals(newMissionName)) {
                return false;
            }
        }
        for (PollingConfigListItemProjectEntity projectEntity :
                currentProjectEntities) {
            for (int missionIndex = 0;missionIndex < projectEntity.getMissionEntitySize();++missionIndex) {
                PollingConfigListItemMissionEntity missionEntity = projectEntity.getMissionEntity(missionIndex);
                if (missionEntity.getMissionConfig().getName().equals(newMissionName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private PollingConfigListItemClause getContent(int stringID) {
        PollingConfigListItemClause result = null;
        final String label = getString(stringID);
        for (PollingConfigListItemClause itemClause :
                itemClauses) {
            if (itemClause.getLabel().equals(label)) {
                result = itemClause;
                break;
            }
        }
        return result;
    }

    private EditDialog.OnClickPositiveListener onEditDialogPositiveClickListener = new EditDialog.OnClickPositiveListener() {
        @Override
        public void onClick(String content) {
            currentItemClause.setContent(content);
            missionConfigAdapter.notifyDataSetChanged();
        }
    };

    private EditDialog editDialog;
    private List<PollingConfigListItemProjectEntity> currentProjectEntities;
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private PollingConfigListItemClause currentItemClause;
    private PollingConfigListItemMissionEntity missionEntity;
    private MissionConfigAdapter missionConfigAdapter;
    private List<PollingConfigListItemClause> itemClauses;
}
