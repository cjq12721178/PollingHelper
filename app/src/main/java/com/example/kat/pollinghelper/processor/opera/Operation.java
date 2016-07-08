package com.example.kat.pollinghelper.processor.opera;

/**
 * Created by KAT on 2016/6/12.
 */
public abstract class Operation  {

    protected String errorMessage = "";
    private OperationInfo operationInfo;

    public Operation(OperationInfo operationInfo) {
        this.operationInfo = operationInfo;
    }

    public boolean execute() {
        return onPreExecute() && onExecute() && onPostExecute();
    }

    protected boolean onPreExecute() {
        return true;
    }

    protected boolean onPostExecute() {
        return true;
    }

    protected abstract boolean onExecute();

    protected void setValue(ArgumentTag tag, Object arg) {
        operationInfo.putArgument(tag, arg);
    }

    protected Object getValue(ArgumentTag tag) {
        return operationInfo.getArgument(tag);
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
