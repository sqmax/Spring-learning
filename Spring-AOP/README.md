>这是举例对SpringAOP概念的一个简单介绍。:horse:


Spring AOP的实现原理是动态代理，关于动态代理可以参见另一篇文章。[动态代理](http://www.cnblogs.com/sqmax/p/9042868.html)

动态代理用语言直白地描述就是，在运行时为目标类生成一个代理对象，然后这个代理对象就可以做被代理对象一样的事，并在这些事之前和之后做一些其他的操作。AOP常用于管理日志、数据库事务等操作，因为这些事情都是通用的，与业务无关，它们可以充分利用Spring AOP的特征，是我们主要集中在业务逻辑的处理上。

在Spring中主要有如下几种方式来实现AOP。

## 一、使用@AspectJ注解

首先是我们的业务类

```java
@Component
public class RoleServicerImpl implements RoleService {
    @Override
    public void printRole(Role role) {
        System.out.println("id="+role.getId()+";name="+role.getName()+";note="+role.getNote());
    }
}
```
然后我们来对这个业务类的printRole方法实现AOP

```java
@Component
@Aspect
public class RoleAspect {

//    将我们上面的业务方法声明为一个切点
    @Pointcut("execution(* top.sqmax.service.impl.RoleServicerImpl.printRole(..))")
    public void print() {
    }

    @Before("print()")
    public void before() {
        System.out.println("before......");
    }

    @After("print()")
    public void after() {
        System.out.println("after....");
    }

    @AfterReturning("print()")
    public void afterReturning() {
        System.out.println("afterReturning.....");
    }

    @AfterThrowing("print()")
    public void afterThrowing() {
        System.out.println("afterThrowing...");
    }
}
```

最后使用在配置类中使用@EnableAspectJAutoProxy注解让Spring启用AspectJ框架的自动代理：

```java
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
```

然后我们进行测试

```java
public class Test
{
    public static void main( String[] args )
    {
        ApplicationContext context=new AnnotationConfigApplicationContext(AopConfig.class);
        RoleService roleService=context.getBean(RoleService.class);

        Role role=new Role(1,"sun","handsome");
        roleService.printRole(role);
    }
}
```

输出结果如下：

```
before......
id=1;name=sun;note=handsome
after....
afterReturning.....
```

## 二、使用xml的方式配置Spring AOP

暂不解释，下次遇到在总结。

## 三、引入

Spring还可以将代理的对象放在多个接口下，进而可以赋予被代理的对象更多的功能。

下面用例子加以说明，使RoleService具有验证角色不为null的功能。

```java
public interface RoleVerifier {
    public boolean verify(Role role);
}
```
实现：

```java
public class RoleVerifierImpl implements RoleVerifier {
    @Override
    public boolean verify(Role role) {
        return role!=null;
    }
}
```

然后在RoleAspect类里加下面一段代码：

```java
@DeclareParents(value = "top.sqmax.service.impl.RoleServiceImpl+",defaultImpl = RoleVerifierImpl.class)
public RoleVerifier roleVerifier;
```

* 其中`value = "top.sqmax.service.impl.RoleServiceImpl+"`的意思就是对Spring容器中RoleServiceImpl的bean进行增强，将其挂在RoleVerifier接口下。
* defaultImpl = RoleVerifierImpl.class是指被代理的类对上面接口的实现。

下面对其进行测试，可以看到从容器中拿到的roleService bean已经具有角色验证的功能，即已挂在RoleVerifier接口下。
```java
ApplicationContext context=new AnnotationConfigApplicationContext(AopConfig.class);
RoleService roleService=context.getBean(RoleService.class);
Role role=new Role(1,"sun","handsome");

RoleVerifier roleVerifier=(RoleVerifier)roleService;
if (roleVerifier.verify(role)) {
    roleService.printRole(role);
}
```

>完整代码：[这里](https://github.com/sqmax/Spring-learning/tree/master/Spring-AOP)






