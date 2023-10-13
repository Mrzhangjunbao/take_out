package com.itheima.reggie.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import com.itheima.reggie.common.JacksonObjectMapper;
import java.util.List;

@Slf4j
@Configuration
//进行静态资源映射，因为springMvc默认配置，只要静态资源放在类路径下： /static (or /public or /resources or /META-INF/resources
//访问 ： 当前项目根路径/ + 静态资源名
//
//原理： 静态映射/**。
//请求进来，先去找Controller看能不能处理。不能处理的所有请求又都交给静态资源处理器。静态资源也找不到则响应404页面
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        //当前项目+/backend下的请求  都默认在类路径即resources下的backend目录下去找静态资源。也就是不拦截此路径。t
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        //当前项目+/front下的请求  都默认在类路径即resources下的front目录下去找静态资源。
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }
    /**
     * 扩展mvc的消息转换器
     *
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将java对象转换为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将其追加到mvc的转换器集合中
        converters.add(0,messageConverter);

    }
}
