package com.example.kat.pollinghelper.ui.activity;

import android.view.View;
import android.widget.BaseAdapter;


import com.example.kat.pollinghelper.R;
import com.example.kat.pollinghelper.processor.opera.ArgumentTag;
import com.example.kat.pollinghelper.ui.dialog.EditDialog;
import com.example.kat.pollinghelper.structure.scout.ScoutCellClause;
import com.example.kat.pollinghelper.structure.scout.ScoutEntity;
import com.example.kat.pollinghelper.structure.scout.ScoutCellState;

import java.util.List;

/**
 * Created by KAT on 2016/7/18.
 */
public class ScoutConfigBaseActivity extends ManagedActivity {

    protected class ConfigErrorCancelEvent implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            promptMessage(stringResourceId);
        }

        public ConfigErrorCancelEvent setErrorMessage(int stringResourceId) {
            this.stringResourceId = stringResourceId;
            return this;
        }

        private int stringResourceId;
    }

    @Override
    protected void onInitializeBusiness() {
        entity = importEntity();
        clauses = createClauses();
        initConfigErrorProcessor();
        createEditDialog();
        baseAdapter = initListView();
    }

    protected List<ScoutCellClause> createClauses() {
        return null;
    }

    protected BaseAdapter initListView() {
        return null;
    }

    protected ScoutEntity importEntity() {
        return null;
    }

    private void initConfigErrorProcessor() {
        configErrorCancelEvent = new ConfigErrorCancelEvent();
    }

    private void createEditDialog() {
        editDialog = new EditDialog();
        editDialog.setOnClickPositiveListener(onEditDialogPositiveClickListener);
    }

    @Override
    public void onBackPressed() {
        if (entity.getState() == ScoutCellState.PCS_UNKNOWN) {
            promptMessage(R.string.ui_prompt_item_config_unknown_error);
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        } else {
            if (!updateEntity()) {
                return;
            }
            changeEntityState();
            backAndPushItemEntity(entity);
        }
    }

    private void changeEntityState() {
        if (entity.getState() == ScoutCellState.PCS_INVARIANT) {
            if (isEntityModified()) {
                entity.setState(ScoutCellState.PCS_MODIFIED);
            }
        }
    }

    private void backAndPushItemEntity(ScoutEntity feedbackEntity) {
        if (entity.getState() == ScoutCellState.PCS_NEW) {
            putArgument(ArgumentTag.AT_SCOUT_ENTITY_FEEDBACK, feedbackEntity);
        }
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    protected boolean updateEntity() {
        return false;
    }

    protected void processConfigError(int errorMessageResourceId) {
        configErrorCancelEvent.setErrorMessage(errorMessageResourceId);
        if (isFirstEdit()) {
            showAlternativeDialog(R.string.ui_prompt_abandon_config,
                    onConfirmClickListener, configErrorCancelEvent);
        } else {
            configErrorCancelEvent.onClick(null);
        }
    }

    protected BaseAdapter getBaseAdapter() {
        return baseAdapter;
    }

    protected List<ScoutCellClause> getClauses() {
        return clauses;
    }

    protected ScoutEntity getEntity() {
        return entity;
    }

    protected ScoutCellClause getCurrentClause() {
        return currentItemClause;
    }

    protected ScoutCellClause getCurrentClause(int position) {
        return currentItemClause = clauses.get(position);
    }

    protected void showEditDialog(String label, String content) {
        editDialog.show(getSupportFragmentManager(), label, content);
    }

    protected boolean isEntityModified() {
        boolean result = false;
        for (ScoutCellClause itemClause :
                clauses) {
            if (itemClause.isModified()) {
                result = true;
                break;
            }
        }
        return result;
    }

    protected ScoutCellClause getContent(int stringID) {
        ScoutCellClause result = null;
        final String label = getString(stringID);
        for (ScoutCellClause itemClause :
                clauses) {
            if (itemClause.getLabel().equals(label)) {
                result = itemClause;
                break;
            }
        }
        return result;
    }

    private View.OnClickListener onConfirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            backAndPushItemEntity(null);
        }
    };

    private EditDialog.OnClickPositiveListener onEditDialogPositiveClickListener = new EditDialog.OnClickPositiveListener() {
        @Override
        public void onClick(String content) {
            currentItemClause.setContent(content);
            baseAdapter.notifyDataSetChanged();
        }
    };

    protected boolean isFirstEdit() {
        return firstEdit;
    }

    protected void setFirstEdit(boolean firstEdit) {
        this.firstEdit = firstEdit;
    }

    private boolean firstEdit;
    private ScoutCellClause currentItemClause;
    private BaseAdapter baseAdapter;
    private List<ScoutCellClause> clauses;
    private EditDialog editDialog;
    private ConfigErrorCancelEvent configErrorCancelEvent;
    private ScoutEntity entity;
}
