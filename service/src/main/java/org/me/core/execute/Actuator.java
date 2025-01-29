package org.me.core.execute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.me.core.observer.EventListener;
import org.me.core.observer.EventManager;
import org.me.model.PropertyValue;
import org.me.model.PropertyValues;
import org.me.service.WorkflowLoadService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行器
 */
@Component
@AllArgsConstructor
public class Actuator {
    @Getter
    private final Map<String, PropertyValues> propertyMap = new ConcurrentHashMap<>();
    private final Map<String, Runnable> workflowRunnable = new ConcurrentHashMap<>();
    @Getter
    private EventManager eventManager;
    private WorkflowLoadService workflowLoadService;

    public void createFlow(String name, String filePath) {
        try {
            if (filePath.indexOf("classpath") != -1) {
                Resource resource = new ClassPathResource(filePath.substring("classpath:".length()));
                filePath = resource.getFile().getAbsolutePath();
            }

            PropertyValues propertyValues = workflowLoadService.loadWorkflow(filePath);
            propertyValues.createIndex();
            propertyMap.put(name, propertyValues);

            ExecuteRunnable executeRunnable = new ExecuteRunnable(name, propertyValues);
            eventManager.subscribe(name, executeRunnable);
            workflowRunnable.put(name, executeRunnable);
            workflowRunnable.get(name).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
