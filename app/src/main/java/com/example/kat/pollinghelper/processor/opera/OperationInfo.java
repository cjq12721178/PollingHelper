package com.example.kat.pollinghelper.processor.opera;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


/**
 * Created by KAT on 2016/5/4.
 */
public class OperationInfo {

    private Map<ArgumentTag, Object> arguments;
    private Queue<OperaType> operaQueue;
    private int continuousOperaNumber;

    public OperationInfo() {
        operaQueue = new LinkedList<>();
        arguments = new HashMap<>();
        continuousOperaNumber = 0;
    }

    public boolean hasOpera() {
        return operaQueue.isEmpty();
    }

    public OperaType popOpera() {
        if (continuousOperaNumber > 0) {
            --continuousOperaNumber;
        }
        return operaQueue.poll();
    }

    public Object getArgument(ArgumentTag tag) {
        return arguments.get(tag);
    }

    public OperationInfo putArgument(ArgumentTag tag, Object arg) {
        arguments.put(tag, arg);
        return this;
    }

    public void notifyExecutor(OperaType type) {
        if (type != null) {
            synchronized (operaQueue) {
                operaQueue.offer(type);
                operaQueue.notify();
            }
        }
    }

    public void notifyExecutor(OperaType... types) {
        if (types != null) {
            synchronized (operaQueue) {
                for (OperaType type :
                        types) {
                    operaQueue.offer(type);
                }
                continuousOperaNumber = types.length;
                operaQueue.notify();
            }
        }
    }

    public void notifyExecutor() {
        synchronized (operaQueue) {
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

    public boolean isRunningContinuousOpera() {
        return continuousOperaNumber != 0;
    }
}
