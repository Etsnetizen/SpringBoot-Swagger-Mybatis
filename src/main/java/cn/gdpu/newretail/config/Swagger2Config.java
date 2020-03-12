package cn.gdpu.newretail.config;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;


@EnableSwagger2
@Configuration
public class Swagger2Config extends WebMvcConfigurationSupport {

    /**
     * 配置要扫描的api包路径
     */
    private static final String BASE_PACKAGE = "cn.gdpu.newretail.controller";

    /**
     * 服务器路径
     */
    private static final String SERVICE_URL = "";

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 设置扫描包路径
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
                // 扫描使用Api注解的controller
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build()
                // 添加登陆认证（有些接口需要登陆才能访问，此处为swagger添加全局token）
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

//    @Bean
//    public RequestMappingInfoHandlerMapping requestMapping() {
//        return new RequestMappingHandlerMapping();
//    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 标题
                .title("新零售平台API文档")
                .description("创建日期:2020-03-12,修改日期:2020-03-12")
                // 创建路径
                .termsOfServiceUrl(SERVICE_URL)
                .version("1.0.0")
                .build();
    }

    /**
     * 解决swagger显示问题
     *
     * @param registry 资源路径
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 解决静态资源无法访问
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        // 解决swagger无法访问
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        // 解决swagger的js文件无法访问
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        // 解决swagger-bootstrap 无法访问
        registry.addResourceHandler("doc.html").
                addResourceLocations("classpath:/META-INF/resources/");
    }

    private List<ApiKey> securitySchemes() {
        // 设置请求头信息
        List<ApiKey> resultList = new ArrayList<>();
        ApiKey apiKey = new ApiKey("Authorization", "Authorization", "header");
        resultList.add(apiKey);
        return resultList;
    }

    private List<SecurityContext> securityContexts() {
        // 设置需要登陆认证的路径
        List<SecurityContext> resultList = new ArrayList<>();
        resultList.add(getContextByPath("/.*"));
        return resultList;
    }

    private SecurityContext getContextByPath(String pathRegex) {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex(pathRegex))
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        List<SecurityReference> result = new ArrayList<>();
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        result.add(new SecurityReference("Authorization", authorizationScopes));
        return result;
    }

}
