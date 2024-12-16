package org.me;

import org.me.api.controller.WorkflowController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MEApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MEApplication.class, args);
        WorkflowController workflowController = context.getBean(WorkflowController.class);
        workflowController.createActuator();
    }
}
