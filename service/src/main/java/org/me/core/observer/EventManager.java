package org.me.core.observer;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 观察者设计模式
 * 当外部条件改变 比如 当审批节点上有人通过之后 触发对应的 workflow 更新
 * workflow 名字作为键
 */
@Component
public class EventManager {
    Map<String, EventListener> listeners = new HashMap<>();

    public EventManager() {
    }

    public void subscribe(String name, EventListener listener) {
        listeners.put(name, listener);
    }

    public void unsubscribe(String name) {
        listeners.remove(name);
    }

    public void notify(String name) {
        listeners.get(name).update();
    }
}
