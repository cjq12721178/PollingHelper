package com.example.kat.pollinghelper.processor.opera;

/**
 * Created by KAT on 2016/6/13.
 */
public class FinishProcess extends Operation {
    @Override
    protected boolean onPreExecute(OperationInfo operationInfo) {
        uiProcessor = (Runnable)operationInfo.getArgument(ArgumentTag.AT_RUNNABLE_FINISH_PROCESSOR);
        return super.onPreExecute(operationInfo);
    }
}
