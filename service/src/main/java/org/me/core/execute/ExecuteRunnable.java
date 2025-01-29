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

    private volatile Object lock = new Object();

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

    public long execute(String name) throws InterruptedException {
        // 如果 是否执行完毕 为真
        // 代表已经执行完毕
        // 直接返回 结束块 代码就行
        if (propertyValues.isHasExecutionCompleted()) {
            return 3;
        }

        long currentId = propertyValues.getCurrentId();
        int retryCount = 0;
        PropertyValue currPropertyValue;

        while (true) {
            currPropertyValue = propertyValues.getPropertyValueIndex().get(currentId);

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

            if (tempCurrentId == currentId) {
                retryCount++;
            } else if (tempCurrentId > 0) {
                // 0 以下的特殊情况不能直接给 currentId 赋值
                currentId = tempCurrentId;
                propertyValues.setCurrentId(currentId);
                retryCount = 0;
            }

            if (tempCurrentId == -1) {
                log.error("{} {} Have Some Error", name, currPropertyValue.getMsg());
                break;
            }

            // 进入等待
            // 提前弹出 返回通知 等待谁
            // 或者流程执行结束
            if (tempCurrentId == -2 || currPropertyValue.getPattern() == 3) {
                break;
            }
        }

        if (currPropertyValue.getPattern() == 3) {
            postOperation();
        }

        return currentId;
    }

    // 执行后置操作
    private long postOperation() {
        // 归档

        // 通知之后所有再次请求 操作流程 已完成
        propertyValues.setHasExecutionCompleted(true);

        return 0;
    }

    @Override
    public void update() {
        try {
            execute(name);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}