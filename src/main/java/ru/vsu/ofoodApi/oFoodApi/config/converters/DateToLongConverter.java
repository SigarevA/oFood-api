package ru.vsu.ofoodApi.oFoodApi.config.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Calendar;
import java.util.Date;

@WritingConverter
public class DateToLongConverter implements Converter<Date, Long> {

    @Override
    public Long convert(Date source) {
        return source.getTime();
    }
}