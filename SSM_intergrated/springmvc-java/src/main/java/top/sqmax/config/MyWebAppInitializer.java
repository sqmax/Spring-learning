package top.sqmax.config;

import org.springframework.web.SpringServletContainerInitializer;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Created by SqMax on 2018/5/22.
 */
public class MyWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    /**
     * Spring IoC环境配置
     * @return
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{RootConfig.class};
    }

    /**
     * DispatcherServlet环境配置
     * @return
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }

    /**
     * DispatcherServlet拦截请求配置
     * @return
     */
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}