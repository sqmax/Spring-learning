package top.sqmax.config;

import org.springframework.context.annotation.*;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.util.regex.Pattern;

/**
 * Created by SqMax on 2018/5/25.
 */
@Configuration
@Import(DataConfig.class)
//Spring IoC扫描的包为top.sqmax下的所有包，但排除controller包，因为controller包属于web层，WebConfig这个配置类扫描过
@ComponentScan(basePackages = {"top.sqmax"},
excludeFilters = {
        @Filter(type = FilterType.CUSTOM,value= RootConfig.WebPackage.class)
})
public class RootConfig {

    public static class WebPackage extends RegexPatternTypeFilter{
        public WebPackage() {
            super(Pattern.compile("top\\.sqmax\\.controller"));
        }
    }

}
