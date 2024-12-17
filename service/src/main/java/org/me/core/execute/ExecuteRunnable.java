package org.me.core.execute;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.me.core.observer.EventListener;
import org.me.model.PropertyValue;
import org.me.model.PropertyValues;

@Slf4j
public class ExecuteRunnable extends ActuatorHandle implements Runnable, EventListener {
    @Getter
    @Setter
    private final PropertyValues propertyValues;
    private final String name;

    public ExecuteRunnable(String name, PropertyValues propertyValues) {
        this.name = name;
        this.propertyValues = propertyValues;
    }

    @Override
    public void run() {
        try {
            execute(name);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void execute(String name) throws InterruptedException {
        long currentId = propertyValues.getCurrentId();
        int retryCount = 0;

        while (true) {
            PropertyValue currPropertyValue = propertyValues.getPropertyValueIndex().get(currentId);

            // 重试机制 出现问题重试块 最多重试 5 次
            if (retryCount > 5) {
                log.error("{} {} Retry Failed", name, currPropertyValue.getMsg());
                break;
            }

            // 执行块
            if (currPropertyValue == null) {
                throw new RuntimeException("未查询到执行块");
            }

            long tempCurrentId = -100;
            switch (currPropertyValue.getPattern()) {
                case 0:
                    tempCurrentId = startHandle(name, currPropertyValue);
                    break;
                case 1:
                    tempCurrentId = executionProcessHandle(name, currPropertyValue);
                    break;
                case 2:
                    tempCurrentId = judgeHandle(name, currPropertyValue);
                    break;
                case 3:
                    tempCurrentId = endHandle(name, currPropertyValue);
                    break;
            }

            if (tempCurrentId == -1) {
                log.error("{} {} Have Some Error", name, currPropertyValue.getMsg());
                break;
            }

            // 进入等待
            // 等待数据输入
            if (tempCurrentId == -2L) {
                synchronized (propertyValues.getLock()) {
                    propertyValues.getLock().wait();
                    currentId = tempCurrentId;
                    System.out.println();
                    continue;
                }
            }

            if (tempCurrentId == currentId) {
                retryCount++;
            } else {
                currentId = tempCurrentId;
                retryCount = 0;
            }
        }
    }

    @Override
    public void update() {
        try {
            propertyValues.getLock().wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}