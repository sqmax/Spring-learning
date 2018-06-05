>这是举例对SpringIoC概念的一个简单介绍。:horse:


Spring框架的核心理念是IoC（控制反转）和AOP（面向切面编程），其中IoC是Spring框架的基础，而AOP则是其重要的功能。
Spring IoC的核心理念就是通过容器来管理bean以及bean之间的依赖关系。而前提是我们通过一些方式声明bean。

## Spring IoC容器

Spring IoC容器的设计主要是基于BeanFactory和ApplicationContext两个接口，其中BeanFactory是Spring IoC容器所定义的最底层接口，而ApplicationContext是其高级接口之一。

下图展示了Spring对BeanFactory的一些实现，可以看到非常复杂，但这不是我们关注的重点。我们主要关注如何去声明装配bean。

![](http://p91462zt8.bkt.clouddn.com/ClassPathXmlApplicationContext.png)

下面使用Spring实战的一个例子对Spring  bean的装配做总结。场景是，分别有一个请求（Quest）和骑士(Knight)的接口。对请求接口，有两个实现：营救少女的请求（RescueDamselQuest）和杀龙的请求（SlayDragonQuest）；对于骑士，也有两个实现：勇敢的骑士（BraveKnight）和营救少女的骑士（RescueDamselQuest），勇敢骑士的可以执行任何请求。

## Spring Bean的声明

Spring通过容器来管理对象之间的依赖关系，首先我们需要先将需要Spring管理的对象声明出来，我参考的书上介绍的有些混乱，其实我觉得Spring只有如下三种方式来声明bean，来交给Spring容器管理。

### 通过xml方式声明bean

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.springframework.org/schema/beans 
      http://www.springframework.org/schema/beans/spring-beans.xsd">

  <bean id="knight" class="top.sqmax.impl.BraveKnight">
    <constructor-arg ref="quest" />
  </bean>

  <bean id="quest" class="top.sqmax.impl.SlayDragonQuest">
    <constructor-arg value="#{T(System).out}" />
  </bean>

</beans>
```

### 在注解类中使用@Bean来声明bean（使用java来配置bean）

为避免xml文件的泛滥，我们也可以使用一个配置类来专门声明bean，如下免得KnightConfig类：

```java
package top.sqmax.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import top.sqmax.impl.BraveKnight;
import top.sqmax.Knight;
import top.sqmax.Quest;
import top.sqmax.impl.SlayDragonQuest;

@Configuration
@ComponentScan(basePackages = "top.sqmax",
        excludeFilters = { @ComponentScan.Filter(Configuration.class) })
public class KnightConfig {

  @Bean
  public Knight knight() {
    return new BraveKnight(quest());
  }
  
  @Bean
  public Quest quest() {
    return new SlayDragonQuest(System.out);
  }

}
```
上面类中有两个方法，都是用了@Bean注解，这样就和在xml文件中声明bean，而且相比xml生命bean更为简单。其实对于我们自己写的类，还有一种更为简单的方式来声明bean，那就是下面的第三种方式。同时也会看到该类上有一些特别的注解`@ComponentScan`，稍后在一起解释。

这种方式主要用来声明一些引用的第三方jar包的bean，由于类不是我们写的，我们不可能往类上加注解，如数据源的bean。

### 通过@Component注解自定义的类，并让Spring扫描的方式发现bean

对于我们自己写的类，我们可以使用@Component注解该类，来声明bean，如下：

```java
package top.sqmax.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import top.sqmax.Knight;

@Component
public class DamselRescuingKnight implements Knight {

  @Autowired
  private RescueDamselQuest quest;

  public DamselRescuingKnight() {
    this.quest = new RescueDamselQuest();
  }

  public void embarkOnQuest() {
    quest.embark();
  }
}
```
但仅仅加上这么一个注解还不行，我们在第二种方式中提到了KnightConfig类上有一段特殊的注解：

```java
@ComponentScan(basePackages = "top.sqmax",
        excludeFilters = { @ComponentScan.Filter(Configuration.class) })
```
这就是通知Spring对top.sqmax及其子包进行扫描，Spring就会去寻找被@Component注解的类，将其作为Spring的bean，纳入Spring容器中进行管理。

## Spring bean的装配        

装配bean的方式，我觉得概括起来也只有一下三种。

### 通过xml的方式装配bean

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.springframework.org/schema/beans 
      http://www.springframework.org/schema/beans/spring-beans.xsd">

  <bean id="knight" class="top.sqmax.impl.BraveKnight">
    <constructor-arg ref="quest" />
  </bean>

  <bean id="quest" class="top.sqmax.impl.SlayDragonQuest">
    <constructor-arg value="#{T(System).out}" />
  </bean>

</beans>
```

### 在配置类中装配bean

我们在KnightConfig配置类中，声明了两个bean，其实他们都还依赖其它的组件，我们可以直接new出一个它所依赖的对象：

```java
@Configuration
@ComponentScan(basePackages = "top.sqmax",
        excludeFilters = { @ComponentScan.Filter(Configuration.class) })
public class KnightConfig {

  @Bean
  public Knight knight() {
    return new BraveKnight(quest());
  }
  
  @Bean
  public Quest quest() {
    return new SlayDragonQuest(System.out);
  }
}
```

### 使用@Autowired注解装配bean

```java
@Component
public class DamselRescuingKnight implements Knight {

  @Autowired
  //使用该注解，Spring会去容器中寻找类型为RescueDamselQuest的bean，来装配该实例变量
  private RescueDamselQuest quest;

  public void embarkOnQuest() {
    quest.embark();
  }

}
```

## 获取Spring bean

方式一：

```java
ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/knight.xml");
    Knight knight = (Knight)context.getBean("knight");
    knight.embarkOnQuest();

```

方式二：

```java
AnnotationConfigApplicationContext context1=new AnnotationConfigApplicationContext(KnightConfig.class);
    Knight knight1=(Knight) context1.getBean("braveKnight");
    knight1.embarkOnQuest();
```

上面ClassPathXmlApplicationContext是从xml配置文件中获取bean，AnnotationConfigApplicationContext是从配置类中获取bean。其实对于bean的声明也就两种方式，一个是使用xml配置，另一个是通过java配置。第二和第三种方式声明的bean都是通过AnnotationConfigApplicationContext这个Spring容器获取。而且从两个地方获取的bean不相等。

```java
//    输出false
    System.out.println("从AnnotationConfigApplicationContext和ClassPathXmlApplicationContext获取的bean是否是同一个bean："+
            (knight==knight1));
```

## bean的作用域

Spring提供了4种作用域，它会根据情况来决定是否生成新的对象。

* 单例（singleton）：它是默认的选项，在整个应用中，Spring只为其生成一个bean实例。
* 原型（prototype）：当每次注入，或者通过Spring IoC容器获取bean时，Spring都会为它创建一个新的实例。
* 会话（session）：在Web应用中使用，就是在会话过程中Spring只创建一个实例。
* 请求（request）：在Web应用中使用的，就是在一次请求中Spring会创建一个实例。

使用注解的方式生命bean是，可以用如下方式指定范围：

```java
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RoleServiceImpl implements RoleService{...}
```


>完整代码：[这里](https://github.com/sqmax/Spring-learning/tree/master/Spring-IoC)





