package ru.vsu.ofoodApi.oFoodApi.config.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import java.util.Date;

@WritingConverter
public class LongToDateConverter implements Converter<Long, Date> {
    @Override
    public Date convert(Long source) {
        return new Date(source);
    }
}
