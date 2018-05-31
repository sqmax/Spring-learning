package top.sqmax.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by SqMax on 2018/5/29.
 */
@Configuration
@ComponentScan("top.sqmax")
@EnableAspectJAutoProxy
public class AopConfig {

//    因为使用自动扫描的方式来声明bean，不用显示配置
//    @Bean
//    public RoleAspect getRoleAspect() {
//        return new RoleAspect();
//    }
//    @Bean
//    public RoleService getRoleService() {
//        return new RoleServicerImpl();
//    }
}
