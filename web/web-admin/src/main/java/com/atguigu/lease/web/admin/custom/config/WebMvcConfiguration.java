package com.atguigu.lease.web.admin.custom.config;

import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.custom.converter.StringToBaseEnumConverterFactory;
import com.atguigu.lease.web.admin.custom.interceptor.AuthenticationInterceptor;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ClassName:
 * Description: TODO
 * Datetime: 2025/10/25 16:19
 * Author: novice21
 * Version: 1.0
 */

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

//    @Resource
//    private StringToItemTypeConverter stringToItemTypeConverter;

    @Resource
    private StringToBaseEnumConverterFactory stringToBaseEnumConverterFactory;

    @Override
    public void addFormatters(FormatterRegistry registry) {
//        registry.addConverter(this.stringToItemTypeConverter);
        registry.addConverterFactory(this.stringToBaseEnumConverterFactory);
    }

    @Resource
    private AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.authenticationInterceptor).addPathPatterns("/admin/**").excludePathPatterns("/admin/login/**");
    }
}
