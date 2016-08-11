package com.example.kat.pollinghelper.processor.opera;

/**
 * Created by KAT on 2016/6/12.
 */
public abstract class Operation  {

    public Operation(OperationInfo operationInfo) {
        this.operationInfo = operationInfo;
    }

    public boolean execute() {
        boolean result;
        try {
            result = onPreExecute() && onExecute() && onPostExecute();
        } catch (Exception e) {
            result = false;
            onProcessError(e);
        }
        return result;
    }

    protected boolean onPreExecute() {
        return true;
    }

    protected boolean onPostExecute() {
        return true;
    }

    protected abstract boolean onExecute();

    protected void onProcessError(Exception e) {
        errorMessage = e.getMessage();
    }

    protected void setValue(ArgumentTag tag, Object arg) {
        operationInfo.putArgument(tag, arg);
    }

    protected Object getValue(ArgumentTag tag) {
        return operationInfo.getArgument(tag);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorMessageForOnce() {
        String realErrorMessage = errorMessage;
        errorMessage = null;
        return realErrorMessage;
    }

    private String errorMessage;
    private OperationInfo operationInfo;
}
