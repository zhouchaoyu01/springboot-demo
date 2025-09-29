package com.coding.cz.recon.processor;
import com.coding.cz.recon.processor.AbstractTaskProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TaskProcessorFactory {

    private final Map<String, AbstractTaskProcessor> processorMap = new HashMap<>();

    @Autowired
    public TaskProcessorFactory(List<AbstractTaskProcessor> processors) {
        // 根据 @Qualifier 标识收集
        for (AbstractTaskProcessor processor : processors) {
            Qualifier qualifier = processor.getClass().getAnnotation(Qualifier.class);
            if (qualifier != null) {
                processorMap.put(qualifier.value(), processor);
            }
        }
    }

    public AbstractTaskProcessor getProcessor(String fetchMode) {
        AbstractTaskProcessor p = processorMap.get(fetchMode);
        if (p == null) {
            throw new IllegalArgumentException("不支持的取数模式: " + fetchMode);
        }
        return p;
    }
}
