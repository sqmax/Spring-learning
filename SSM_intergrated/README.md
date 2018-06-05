之前做过一个Spring Boot的项目，因为几乎没有任何配置，在感叹它使开发过程变简单的同时，也深深地明白自己对很多东西还不清楚，所以又查阅了SSM整合开发的书籍，把SSM整合开发的基本配置重新梳理了一遍。如果你正在做SSM的项目，但对一些配置还不太清楚，可以参考下。

>建议先导入IDEA里运行起来，再根据下面的讲解和代码里的详细注释来进行理解。

我们知道传统的web开发都有一个web.xml文件，由于在Servlet3.0之后的规范允许取消web.xml配置，只使用注解方式就可以了，我这里对两种方式都有总结，分开为两个项目`springmvc-xml`，`springmvc-java`，分别是使用xml配置的和使用java进行配置的。

## 使用xml配置SSM整合开发

> 项目源码：[xml配置SSM整合开发Demo](https://github.com/sqmax/Spring-learning/tree/master/SSM_intergrated/springmvc-xml)

### 首先是web.xml文件

它要声明Sping IoC容器配置文件路径，并配置 `ContextLoaderListener`用以初始化Spring IoC容器，还有Spring MVC的DispatcherServlet配置文件路径。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="3.1" xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
         http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd">
    <!-- 配置Spring IoC容器配置文件路径,Spring会从“/WEB-INF/applicationContext.xml”中加载bean定义。 -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/applicationContext.xml</param-value>
    </context-param>
    <!-- 配置ContextLoaderListener用以初始化Spring IoC容器 -->
    <listener>
      <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
    <!-- 配置DispatcherServlet -->
    <servlet>
        <!-- 注意：Spring MVC框架会根据servlet-name配置，找到/WEB-INF/dispatcher-servlet.xml作为配置文件载入Web工程中 -->
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!-- 使得Dispatcher在服务器启动的时候就初始化 -->
        <load-on-startup>2</load-on-startup>
    </servlet>
    <!-- Servlet拦截配置 ，这里是拦截所有的请求-->
    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>
```

### Spring IoC容器配置文件

上面我们在web.xml文件里声明的Spring IoC容器的配置文件为applicationContext.mxl，它的内容如下：

```xml
<?xml version='1.0' encoding='UTF-8' ?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
	xmlns:tx="http://www.springframework.org/schema/tx" xmlns:context="http://www.springframework.org/schema/context"
	xmlns:mvc="http://www.springframework.org/schema/mvc"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
       http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-4.0.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.0.xsd
       http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-4.0.xsd">
	<!-- 使用注解驱动 -->
	<context:annotation-config />

	<!-- 配置扫描注解,不扫描@Controller注解 -->
	<context:component-scan base-package="top.sqmax">
		<context:exclude-filter type="annotation"
								expression="org.springframework.stereotype.Controller" />
	</context:component-scan>

	<!-- 数据库连接池 -->
	<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
		<property name="driverClassName" value="com.mysql.jdbc.Driver" />
		<property name="url" value="jdbc:mysql://localhost:3306/chapter14" />
		<property name="username" value="root" />
		<property name="password" value="sq712816" />
		<property name="maxActive" value="255" />
		<property name="maxIdle" value="5" />
		<property name="maxWait" value="10000" />
	</bean>

	<!-- 集成mybatis -->
	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<property name="dataSource" ref="dataSource" />
		<property name="configLocation" value="classpath:/mybatis-config.xml" />
	</bean>

	<!-- 配置数据源事务管理器 -->
	<bean id="transactionManager"
		class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		<property name="dataSource" ref="dataSource" />
	</bean>

	<!-- 采用自动扫描方式创建mapper bean -->
	<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
	    <property name="basePackage" value="top.sqmax" />
		<!--sqlSessionFactory可以不显式配置-->
	    <!--<property name="sqlSessionFactory" ref="sqlSessionFactory" />-->
		<!--表示被这个注解标识时，才进行扫描，Spring中往往使用@Repository来表示数据访问层（DAO）-->
	    <property name="annotationClass" value="org.springframework.stereotype.Repository" />
	</bean>
</beans>
```

可以看到该配置文件里配置了一些引用的第三方jar包的bean的配置，因为我们使用的是MyBatis持久层，可以看到许多bean都是MyBatis相关的。
另外还有这么一段比较特殊的：

```xml
<!-- 使用注解驱动 -->
<context:annotation-config />

<!-- 配置扫描注解,不扫描@Controller注解 -->
<context:component-scan base-package="top.sqmax">
	<context:exclude-filter type="annotation"
							expression="org.springframework.stereotype.Controller" />
</context:component-scan>
```
它声明我们可以使用注解的方式声明bean，扫描的包是`top.sqmax`及其子包，以后就可以使用注解的方式来声明bean，不需要在xml配置，这对与自己写的类非常方便，但对于引入的第三方java包，如上面MyBatis相关的bean，由于它的源码我们没法去注解，所以需要在xml配置（还可以用java配置，稍后说明）。
但排除@Controller注解，因为@Controller是属于web层，稍后DispatcherServlet的配置文件会声明扫描该注解。否则会扫描两次。

#### MyBatis的配置文件

上面web.xml中，我们配置了DispatcherServlet，Spring MVC配置相关的内容，就是DispatcherServlet的配置文件，在上面web.xml中，我们并没有显示指定它的配置文件名和路径。其实，Spring默认其在WEB-INF目录下，名字为`<dispatchername>-servlet.mxl`。如上面web.xml中，Dispatcher名字为dispatcher，那么它的默认文件就是`WEB-INF/dispatcher-servlet.xml`，它的内容如下：


```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <!--这里可以写MaBatis的许多配置,如下面为pojo配置别名,以后就可以在mapper映射文件中使用-->
    <typeAliases>
        <typeAlias type="top.sqmax.pojo.Role" alias="Role"/>
    </typeAliases>
    <!--<settings>-->
    <!--<setting name="" value=""/>-->
    <!--</settings>-->

    <!-- 指定映射器路径 -->
    <mappers>
        <mapper resource="top/sqmax/mapper/RoleMapper.xml" />
    </mappers>
</configuration>
```

里面还指定了MyBatis的映射文件`<mapper resource="top/sqmax/mapper/RoleMapper.xml" />`，它可以有多个。

### Spring MVC相关的xml配置

上面web.xml中，我们配置了DispatcherServlet，Spring MVC配置相关的内容，就是DispatcherServlet的配置文件，在上面web.xml中，我们并没有显示指定它的配置文件名和路径。其实，Spring默认其在WEB-INF目录下，名字为`<dispatchername>-servlet.mxl`。如上面web.xml中，Dispatcher名字为dispatcher，那么它的默认文件就是`WEB-INF/dispatcher-servlet.xml`，它的内容如下：

```xml
<?xml version='1.0' encoding='UTF-8' ?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
	xmlns:tx="http://www.springframework.org/schema/tx" xmlns:context="http://www.springframework.org/schema/context"
	xmlns:mvc="http://www.springframework.org/schema/mvc"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
       http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-4.0.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.0.xsd
       http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-4.0.xsd">
	<!-- 使用注解驱动 -->
	<mvc:annotation-driven />
	<!-- 定义扫描装载的包 -->
	<context:component-scan base-package="top.sqmax.controller" />
	<!-- 定义视图解析器 -->
	<!-- 找到Web工程/WEB-INF/JSP文件夹，且文件结尾为jsp的文件作为映射 -->
	<bean id="viewResolver"
		class="org.springframework.web.servlet.view.InternalResourceViewResolver"
		p:prefix="/WEB-INF/view/" p:suffix=".jsp" />
	<!-- 如果有配置数据库事务,需要开启注解事务的，需要开启这段代码 -->
	<tx:annotation-driven transaction-manager="transactionManager" />
</beans>
```
可以看到这里配置了一个视图解析器bean，我们使用的是InternalResourceViewResolver，它就是jsp相关的试图解析器。  
另外，想Spring IoC配置文件一样，它也有一段特别的配置：

```xml
<!-- 使用注解驱动 -->
<mvc:annotation-driven />
<!-- 定义扫描装载的包 -->
<context:component-scan base-package="top.sqmax.controller" />
```

它指明使用注解驱动，扫描的包是`top.sqmax.controller`，我们的所有控制器的写在这个包里，这就是我们的web层。


上面就是使用xml的方式对SSM整合开发的最基本配置，项目复杂时，需要更多配置，而且配置也是多样的，xml配置和java配置共同存在。


## java配置SSM整合开发

>项目源码：[java配置SSM整合Demo](https://github.com/sqmax/Spring-learning/tree/master/SSM_intergrated/springmvc-java)

在Servlet3.0之后的规范允许取消web.xml配置，只使用java配置就可以了，所以在Spring3.1之后的版本也提供了解决方案。首先，继承`AbstractAnnotationConfigDispatcherServletInitializer`这个类。

### 继承AbstractAnnotationConfigDispatcherServletInitializer

```java
package top.sqmax.config;

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
```

这是因为在Servlet3.0环境中，容器会在类路径中查找实现`javax.servlet.ServletContainerInitializer`接口的类，如果能发现的话，就会用它来配置Servlet容器。

Spring提供了这个接口的实现，名为`SpringServletContainerInitializer`，这个类反过来又会查找实现`WebApplicationInitializer`的类并将配置的任务交给它们来完成。Spring 3.2引入了一个便利的`WebApplicationInitializer`基础实现，也就是`AbstractAnnotationConfigDispatcherServletInitializr`。
因为我们的`MyWebAppInitializer`实现了`AbstractAnnotationConfigDispatcherServletInitializr`(同时也就实现了`WebApplicationInitializer`)，因此当部署到Servlet3.0容器中的时候，容器就会自动发现它，并用它来配置Servlet上下文。

可以看到`MyWebAppInitializer`重写了三个方法（可以和web.xml中的配置项对应起来，作以下比较）。
第一个方法是getServletMappings()，它会将一个或多个路径映射到DispatcherServlet上。在本例中，它映射的是“/”，这表示它会是应用的默认Servlet。它会处理进入应用的所有请求。

为了理解其他的两个方法，我们首先要理解`DispatcherServlet`和一个Servlet监听器（也就是`ContextLoaderListener`）的关系。

两个应用上下文之间的故事

* 当DispatcherServlet启动的时候，它会创建Spring MVC应用上下文，并加载配置文件或配置类中所声明的bean。其中getServletConfigClasses()方法中，返回的就是与DispatcherServlet的配置相关，我们这里返回的是WebConfig，用它来配置Spring MVC 相关的bean。

* 但是在Spring Web应用中，通常还会有另外一个应用上下文，它是用来配置Spring IoC容器。另外的这个应用上下文是由ContextLoaderListener创建的。它是由getRootConfigClasses()方法返回的带有@Configuration注解的类将会用来配置，这里我们返回的是RootConfig这个类。

* 我们希望DispatcherServlet加载包含Web组件的bean，如控制器、视图解析器以及处理器映射。而ContextLoaderListener要加载应用中的其他bean，这些bean通常是驱动应用后端的中间层和数据层组件。

下面我们看一下WebConfig和RootConfig这两个类。

### Spring IoC容器相关的配置

```java
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
```
上面用@Import导入了另一个配置类DataConfig，它就是持久层的配置，我们这里持久层用的是MyBatis，DataConfig的java配置如下：

#### MyBatis的java配置

```java
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
        properties.setProperty("password","sq712816");
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
```

可以看到它配置的都是数据层组件，这里我们用的是MyBatis，所以都是MyBatis相关的bean。

### Spring MVC相关的java配置

再来看一下WebConfig：

```java
/**
 * Created by SqMax on 2018/5/22.
 */
@Configuration
@ComponentScan("top.sqmax.controller")
@EnableWebMvc
public class WebConfig {

    @Bean(name = "viewResolver")
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/view/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }
}
```
它配置了一个jsp相关的视图解析器。该类还使用了`@EnableWebMvc`这个注释，它代表启动Spring MVC框架的配置。

## 总结

不管使用web.xml和使用java配置(继承自`AbstractAnnotationConfigDispatcherServletInitializer`的那个类)，里面的配置内容都是一致的，对比起来会有更深的理解（可以按整篇博客的目录结构作对比）。上面的讲解也都是从大而概括的层面去解释的，主要参考自Spring实战和另一本SSM整合开发的书籍。当然想要从更深的层面去理解，还是要参考书籍对Spring MVC请求流程的讲解并对照源码进行分析。















