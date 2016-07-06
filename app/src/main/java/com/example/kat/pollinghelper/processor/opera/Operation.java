package com.example.kat.pollinghelper.processor.opera;

import android.os.Handler;

/**
 * Created by KAT on 2016/6/12.
 */
public abstract class Operation implements Command {
    @Override
    public void execute(OperationInfo operationInfo) {
        if (onPreExecute(operationInfo)) {
            onExecute();
        }
        onPostExecute();
    }

    protected boolean onPreExecute(OperationInfo operationInfo) {
        paraSetter = operationInfo;
        uiFeedBacker = (Handler)operationInfo.getArgument(ArgumentTag.AT_HANDLER_UI_FEEDBACK);
        return true;
    }

    protected void onExecute() {

    }

    protected void onPostExecute() {
        feedbackToUI(uiProcessor);
    }

    protected void feedbackToUI(Runnable runnable) {
        if (runnable != null) {
            uiFeedBacker.post(runnable);
        }
    }

    protected void setValue(ArgumentTag tag, Object arg) {
        paraSetter.putArgument(tag, arg);
    }

    protected Object getValue(ArgumentTag tag) {
        return paraSetter.getArgument(tag);
    }

    private Handler uiFeedBacker;
    private OperationInfo paraSetter;
    protected Runnable uiProcessor;
}
