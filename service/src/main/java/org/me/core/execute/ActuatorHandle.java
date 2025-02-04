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
     * @return -1 调用方法返回值错误 -2 当前节点需要等待输入
     */
    protected long executionProcessHandle(String name, PropertyValue propertyValue) {
        log.info("{} executionProcessHandle", name);

        try {
            Integer result = executeMethod(propertyValue);

            if (result == null) {
                return -1;
            }

            if (result == 2) {
                return -2;
            }

            return propertyValue.getNextPropertyIds().get(0);
        } catch (Exception e) {
            // 更详细的异常处理
            log.error("Error executing method", e);
            return propertyValue.getPropertyId();
        }
    }

    /**
     * 判断块 如果成功 执行列表中第一个块 否则执行列表中第二个块
     *
     * @param name
     * @param propertyValue
     * @return -1 调用方法返回值错误 -2 当前节点需要等待输入
     */
    protected long judgeHandle(String name, PropertyValue propertyValue) {
        log.info("{} judgeHandle", name);

        try {
            Integer result = executeMethod(propertyValue);

            if (result == null) {
                return -1;
            }

            if (result == 2) {
                return -2;
            }

            return propertyValue.getNextPropertyIds().get(result);
        } catch (Exception e) {
            // 更详细的异常处理
            log.error("Error executing method", e);
            return propertyValue.getPropertyId();
        }
    }

    /**
     * 结束块
     *
     * @param name
     * @param propertyValue
     * @return 返回 0 正常退出
     */
    protected long endHandle(String name, PropertyValue propertyValue) {
        log.info("{} endHandle", name);

        return 0;
    }

    private Integer executeMethod(PropertyValue propertyValue) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String methodSignature = propertyValue.getExecutionLogic();

        if (methodSignature == null || "".equals(methodSignature)) {
            throw new RuntimeException("No executable method is given to the execution block");
        }

        int lastDotIndex = methodSignature.lastIndexOf(".");
        String className = methodSignature.substring(0, lastDotIndex);
        String methodName = methodSignature.substring(lastDotIndex + 1);

        // 加载类
        Class<?> handleClass = Class.forName(className);
        // 定义参数类型
        Class<?>[] parameterTypes = new Class<?>[]{PropertyValue.class};
        // 获取方法对象
        Method handleMethod = handleClass.getMethod(methodName, parameterTypes);
        // 创建类的实例
        Object handleInstance = handleClass.getDeclaredConstructor().newInstance();

        Class<?> returnType = handleMethod.getReturnType();

        if (!(returnType == int.class || returnType == Integer.class)) {
            return null;
        }

        return (int) handleMethod.invoke(handleInstance, propertyValue);
    }
}
