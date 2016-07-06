package com.example.kat.pollinghelper.processor.opera;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


/**
 * Created by KAT on 2016/5/4.
 */
public class OperationInfo {

    public OperationInfo() {
        operaQueue = new LinkedList<>();
        arguments = new HashMap<>();
    }

    public Queue<OperaType> getOperaQueue() {
        return operaQueue;
    }

    public Object getArgument(ArgumentTag tag) {
        return arguments.get(tag);
    }

    public OperationInfo putArgument(ArgumentTag tag, Object arg) {
        arguments.put(tag, arg);
        return this;
    }

    public void notifyExecutor(OperaType type) {
        synchronized (operaQueue) {
            operaQueue.offer(type);
            operaQueue.notify();
        }
    }

    public void waitNotifier() {
        synchronized (operaQueue) {
            try {
                operaQueue.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<ArgumentTag, Object> arguments;
    private Queue<OperaType> operaQueue;
}
