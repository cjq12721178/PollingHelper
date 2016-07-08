package com.example.kat.pollinghelper.processor.opera;

import com.example.kat.pollinghelper.fuction.config.PollingProjectConfig;
import com.example.kat.pollinghelper.ui.structure.QueryInfo;

import java.util.List;

/**
 * Created by KAT on 2016/7/8.
 */
public class QueryPollingRecord extends Operation {

    private QueryInfo queryInfo;

    public QueryPollingRecord(OperationInfo operationInfo) {
        super(operationInfo);
    }

    @Override
    protected boolean onPreExecute() {
        queryInfo = (QueryInfo)getValue(ArgumentTag.AT_QUERY_INFO);
        return true;
    }

    @Override
    protected boolean onExecute() {
        //TODO 根据queryInfo进行查询，详见该类注释
        //注意，queryInfo可能为null，如果为null则部分查询参数通过getValue得到
        //这个主要针对LATEST_RECORD_FOR_PER_PROJECT，具体见QueryInfo类注释
        return true;
    }

    @Override
    protected boolean onPostExecute() {
        setValue(ArgumentTag.AT_QUERY_INFO, null);
        return true;
    }
}
