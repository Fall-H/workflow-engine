package org.me.service;

import org.me.model.PropertyValue;
import org.me.model.PropertyValues;

import java.util.List;

public interface WorkflowLoadService {
    PropertyValues loadWorkflow(String filePath);
}
