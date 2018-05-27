package top.sqmax.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.web.servlet.resource.CachingResourceTransformer;


import javax.sql.DataSource;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by SqMax on 2018/5/24.
 */
//对持久层的配置，这里使用的是MyBatis
@Configuration
public class DataConfig implements TransactionManagementConfigurer{

    @Bean
    public DataSource dataSource() {
        DataSource dataSource= null;
        Properties properties=new Properties();
        properties.setProperty("driverClassName","com.mysql.jdbc.Driver");
        properties.setProperty("url","jdbc:mysql://localhost:3306/chapter14");
        properties.setProperty("username","root");
        properties.setProperty("password","123456");
        properties.setProperty("maxIdle","20");
        properties.setProperty("maxActive","255");
        properties.setProperty("maxWait","10000");
        try {
            dataSource=BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    /**
     * 配置SqlSessionFactoryBean
     * @return
     */
    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean() {
        SqlSessionFactoryBean sqlSessionFactoryBean=new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
//        MyBatis配置文件
        Resource resource=new ClassPathResource("mybatis-config.xml");
        sqlSessionFactoryBean.setConfigLocation(resource);

        return sqlSessionFactoryBean;
    }

    /**
     * 通过自动扫描，发现MyBatis Mapper接口
     * @return
     */
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer=new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("top.sqmax.dao");
        mapperScannerConfigurer.setAnnotationClass(Repository.class);
        return mapperScannerConfigurer;
    }

    /**
     * 实现接口方法，注册注解事务，当@Transactional使用的时候产生数据库事务
     * @return
     */
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        DataSourceTransactionManager transactionManager=new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }
}
