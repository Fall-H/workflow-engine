package org.me.core.execute;

import org.me.core.observer.EventListener;
import org.me.core.observer.EventManager;
import org.me.model.PropertyValue;
import org.me.model.PropertyValues;
import org.me.service.WorkflowLoadService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行器
 */
@Component
public class Actuator {
    private final Map<String, PropertyValues> propertyMap = new ConcurrentHashMap<>();
    private final Map<String, Runnable> workflowRunnable = new ConcurrentHashMap<>();
    @Resource
    private EventManager eventManager;

    @Resource
    private WorkflowLoadService workflowLoadService;

    public void createFlow(String name, String filePath) {
        PropertyValues propertyValues = workflowLoadService.loadWorkflow(filePath);
        propertyValues.createIndex();
        propertyMap.put(name, propertyValues);

        ExecuteRunnable executeRunnable = new ExecuteRunnable(name, propertyValues);
        eventManager.subscribe(name, executeRunnable);
        workflowRunnable.put(name, executeRunnable);
        workflowRunnable.get(name).run();
    }
}
