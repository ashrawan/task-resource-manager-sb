package com.lk.taskmanager.services.generic;

import com.lk.taskmanager.utils.exceptions.LKAppException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

public class StringToEnumConverter implements ConverterFactory<String, Enum> {

    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return source -> {
            try {
                return (T) Enum.valueOf(targetType, source.toUpperCase());
            } catch (Exception e) {
//                return targetType.getEnumConstants()[Integer.parseInt(source)];
                throw new LKAppException(e.getMessage());
            }
        };
    }
}
