package org.me.core.execute;

import lombok.extern.slf4j.Slf4j;
import org.me.core.observer.EventListener;
import org.me.model.PropertyValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class ActuatorHandle {
    /**
     * 启动函数
     *
     * @param name
     * @param propertyValue
     * @return 直接返回开始后的第一个节点
     */
    protected long startHandle(String name, PropertyValue propertyValue) {
        log.info("{} startHandle", name);

        return propertyValue.getNextPropertyIds().get(0);
    }

    /**
     * 加载 PropertyValue.executionLogic 中的代码
     * 通过 Java 反射机制加载逻辑
     *
     * @param name
     * @param propertyValue
     * @return
     */
    protected long executionProcessHandle(String name, PropertyValue propertyValue) {
        log.info("{} executionProcessHandle", name);

        try {
            Object[] object = loadObject(propertyValue.getExecutionLogic());
            Method handleMethod = (Method) object[0];
            Object handleInstance = object[1];
            // 调用方法
            handleMethod.invoke(handleInstance, propertyValue.getParams(), propertyValue);

            return propertyValue.getNextPropertyIds().get(0);
        } catch (Exception e) {
            // 更详细的异常处理
            log.error("Error executing method", e);
            return propertyValue.getPropertyId();
        }
    }

    /**
     * 判断块 如果成功 执行列表中第一个块 否则执行列表中第二个块
     * @param name
     * @param propertyValue
     * @return -1 调用方法返回值错误
     */
    protected long judgeHandle(String name, PropertyValue propertyValue) {
        log.info("{} judgeHandle", name);

        try {
            Object[] object = loadObject(propertyValue.getExecutionLogic());
            Method handleMethod = (Method) object[0];
            Object handleInstance = object[1];

            Class<?> returnType = handleMethod.getReturnType();

            if (!(returnType == boolean.class || returnType == Boolean.class)) {
                return -1;
            }

            // 调用方法
            Boolean result = (Boolean) handleMethod.invoke(handleInstance, propertyValue.getParams(), propertyValue);

            if (result) {
                return propertyValue.getNextPropertyIds().get(0);
            } else {
                return propertyValue.getNextPropertyIds().get(1);
            }

        } catch (Exception e) {
            // 更详细的异常处理
            log.error("Error executing method", e);
            return propertyValue.getPropertyId();
        }
    }

    /**
     * 结束块
     * @param name
     * @param propertyValue
     * @return 返回 0 正常退出
     */
    protected long endHandle(String name, PropertyValue propertyValue) {
        log.info("{} startHandle", name);

        return 0;
    }

    private Object[] loadObject(String methodSignature) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        int lastDotIndex = methodSignature.lastIndexOf(".");
        String className = methodSignature.substring(0, lastDotIndex);
        String methodName = methodSignature.substring(lastDotIndex + 1);

        // 加载类
        Class<?> handleClass = Class.forName(className);
        // 定义参数类型
        Class<?>[] parameterTypes = new Class<?>[]{Map.class, PropertyValue.class};
        // 获取方法对象
        Method handleMethod = handleClass.getMethod(methodName, parameterTypes);
        // 创建类的实例
        Object handleInstance = handleClass.getDeclaredConstructor().newInstance();

        return new Object[]{handleMethod, handleInstance};
    }
}
