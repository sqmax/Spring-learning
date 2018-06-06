>本项目是对Spring+MyBatis组合使用事务的学习源码
>里面还包含了一个根据jar文件生成pom依赖的工具类，可以独立使用。


##数据库事务的基本知识

### 什么是事务

事务是逻辑上的一组操作，这组操作要么全部成功，要么全部失败。

事务的特性：

* 原子性：指事务是一个不可分割的工作单位，事务中的操作要么都发生，要么都不发生。
* 一致性：指事务前后数据的完整性必须保持一致。
* 隔离性：指多个用户并发访问数据库时，一个用户的事务不能被其他用户的事务所干扰，多个并发事务之间要相互隔离。
* 持久性：持久性是指一个事务一旦被提交，它对数据库中的数据的改变就是永久的。

### 事务隔离级别

按SQL规范，把隔离级别定义为4层，分别是：脏读（dirty read）、读提交（read commit）、可重复读（repeatable read）和序列化（Serializable）。

* **脏读**：即一个事务可以读取另一个未提交事务的数据。			
如下事例：  
老板要给程序员发工资，程序员的工资是3.6万/月。但是发工资时老板不小心按错了数字，按成3.9万/月，该钱已经打到程序员的户口，但是事务还没有提交，就在这时，程序员去查看自己这个月的工资，发现比往常多了3千元，以为涨工资了非常高兴。但是老板及时发现了不对，马上回滚差点就提交了的事务，将数字改成3.6万再提交。可以知道程序员读到的数据是未提交的，错误的，这就是脏读。
* **读提交**： 读提交是更高级别的隔离，为了解决脏读的问题，使用该隔离级别一个事务要等另一个事务提交后才能读取数据。但该隔离级别还会有一些问题。  
如下事例：			
程序员拿着信用卡去享受生活（卡里当然是只有3.6万），当他买单时（程序员事务开启），收费系统事先检测到他的卡里有3.6万，就在这个时候！程序员的妻子要把钱全部转出充当家用（妻子事务开启，注意进行update操作，不是读取操作），并提交。当收费系统准备扣款时，再检测卡里的金额，发现已经没钱了（第二次检测金额当然要等待妻子转出金额事务提交完）。程序员就会很郁闷，明明卡里是有钱的…         
可以看到，读提交隔离级别可以解决脏读的问题，但如果另一个事务在一个事务执行期间进行update操作，就会出现另一种问题。
* **可重复读**：为了解决上一个问题，SQL规范制定了更高的隔离级别-可重复读，就是在读取数据的事务开始后，不再允许修改操作。但该隔离级别还会引发一些问题。                              
如下事例：                                                    
程序员某一天去消费，花了2千元，然后他的妻子去查看他今天的消费记录（全表扫描FTS，妻子事务开启），看到确实是花了2千元，就在这个时候，程序员花了1万买了一部电脑，即新增INSERT了一条消费记录，并提交。当妻子打印程序员的消费记录清单时（妻子事务提交），发现花了1.2万元，似乎出现了幻觉，这就是幻读问题。
* **序列化**：为了解决泛读问题，SQL规范制定了序列化，它是最高的事务隔离级别，在该级别下，事务串行化顺序执行，脏读、不可重复读和泛读问题。但是这种事务隔离效率最低，一般不使用。

-----------
>以上是数据库的基本知识，也是学习Spring事务的前置知识。
参考自：[https://blog.csdn.net/qq_33290787/article/details/51924963](https://blog.csdn.net/qq_33290787/article/details/51924963)



## Spring事务管理

Spring事务管理高层抽象主要包括3个接口：

* PlatformTransactionManager
* TransactionDefinition
* TransactionStatus

### 事务管理器

Spring为不同的持久层框架提供了不同的PlatformTransactionManager接口实现。

![springtransaction.PNG](https://i.loli.net/2018/05/13/5af8137019b56.png)

其中Spring JDBC和MyBatis使用DataSourceTransactionManager进行事务管理，图中其它的根据名字Jdo，Jpa，Hibernate就可知道分别是为谁提供的。

### 隔离级别
在TransactionDefinition中定义了如下几个常量对应上面的隔离级别：

```java
ISOLATION_READ_UNCOMMITTED
ISOLATION_READ_COMMITTED
ISOLATION_REPEATABLE_READ
ISOLATION_SERIALIZABLE

ISOLATION_DEFAULT
```

其中`ISOLATION_DEFAULT`，对应底层使用的数据库的默认事务隔离级别。


### 传播行为

解决业务层方法之间的相互调用的问题。
TransactionDefinition接口里定义了一些常量对应事务的传播行为。

```java
PROPAGATION_REQUIRED
PROPAGATION_REQUIRES_NEW
PROPAGATION_NESTED
```
## 在Spring+MyBatis组合中使用事务

### 配置事务管理器

```xml
<?xml version='1.0' encoding='UTF-8' ?>
<!-- was: <?xml version="1.0" encoding="UTF-8"?> -->
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
	xmlns:aop="http://www.springframework.org/schema/aop" xmlns:tx="http://www.springframework.org/schema/tx"
	xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
	   http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
       http://www.springframework.org/schema/aop 
       http://www.springframework.org/schema/aop/spring-aop-4.0.xsd
       http://www.springframework.org/schema/tx 
       http://www.springframework.org/schema/tx/spring-tx-4.0.xsd
       http://www.springframework.org/schema/context 
       http://www.springframework.org/schema/context/spring-context-4.0.xsd">
	<!--启用扫描机制，并指定扫描对应的包-->
	<context:annotation-config />
	<context:component-scan base-package="com.ssm.chapter13.*" />
	<!-- 数据库连接池 -->
	<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
		<property name="driverClassName" value="com.mysql.jdbc.Driver" />
		<property name="url" value="jdbc:mysql://localhost:3306/chapter13"/>
		<property name="username" value="root" />
		<property name="password" value="sq712816" />
		<property name="maxActive" value="255" />
		<property name="maxIdle" value="5" />
		<property name="maxWait" value="10000" />
	</bean>
	<!-- 事务管理器配置数据源事务 -->
	<bean id="transactionManager"
		class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		<property name="dataSource" ref="dataSource" />
	</bean>

	<!-- 使用注解定义事务 -->
	<tx:annotation-driven transaction-manager="transactionManager" />

</beans>
```

### 声明式事务编程

Spring支持两种方式事务管理:

* 编程式的事务管理：通过`TransactionTemplate`手动管理事务。（不常用）
* 声明式事务：通过使用XML配置或java注解AOP实现，实际开发主要用注解的方式。
由于编程式事务管理实际开发中不常用，这里主要讲解编程式事务管理。声明式事务管理主要使用`@Transactional`注解来实现的。
@Transactional的源码如下：

```
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transactional {

	@AliasFor("transactionManager")
	String value() default "";

	@AliasFor("value")
	String transactionManager() default "";

	Propagation propagation() default Propagation.REQUIRED;

	Isolation isolation() default Isolation.DEFAULT;

	int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;
    
    //省略...........

}

```
可以看到该注解的属性有事务管理器，传播行为，隔离级别等，其中事务管理器，我们已在spring配置文件里配置。我们使用该注解时要指定实务的隔离级别和传播行为。
下面是我在一个插入角色的业务方法里面对该注解的一个应用：

```xml
@Override
@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
public int insertRole(Role role) {
	return roleMapper.insertRole(role);
}
```

### 事务间的传播行为

为了演示Spring事务的传播行为，我们在另外一个业务方法里面，调用上一个方法，插入多个角色：

```java
@Service
public class RoleListServiceImpl implements RoleListService {
	@Autowired
	private RoleService roleService = null;
	Logger log = Logger.getLogger(RoleListServiceImpl.class);
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public int insertRoleList(List<Role> roleList) {
		int count = 0;
		for (Role role : roleList) {
			try {
				count += roleService.insertRole(role);
			} catch (Exception ex) {
				log.info(ex);
			}
		}
		return count;
	}
}
```
这样insertRoleList方法调用了insertRole，而这两个方法都开启了事务，因为insertRole方法的事务传播行为是`REQUIRES_NEW`，所以会产生一个新的事务，

## Spring事务原理的简单概述

在Spring IoC容器初始化时，Spring会读入`@Transactional`注解或XML配置的事务信息，并且保存到一个事务定义类里面（`TransactionDefinition`接口的子类），以备将来使用。当运行时会让Spring拦截`@Transaction`注解标注的某一个方法或类的所有方法。谈到拦截，可能会想到AOP，Spring就是通过AOP把事务相关的功能按照注解里的属性来实现事务的。
首先Spring通过事务管理器（`PlatformTransactionManager`的子类）创建事务，与此同时会把事务定义类（`Transcation`的之类）中的隔离级别、超时时间等属性根据配置内容往事务上设置。Spring通过反射的方式调度开发者的业务代码，但是反射的结果可能是正常返回或者产生异常返回，那么它给的约定是只要发生异常，并且符合事务定义类回滚条件的，Spring就会将数据库事务回滚，否则将数据库事务提交，这是Spring自己完成的。你会惊奇地发现，在整个开发过程中，只需编写业务代码和对事务属性进行配置就行了，并不需要使用代码干预，工作量比较少，代码逻辑也更为清晰，更有利于维护。    

声明式事务的流程如下：

![spring-tx.PNG](https://i.loli.net/2018/05/17/5afd9cd6a6f12.png)

### 自调用问题

再次使用前面插入角色的例子作说明：

```java
@Override
@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
public int insertRole(Role role) {
	return roleMapper.insertRole(role);
}
```

这里没有数据库的资源打开和释放代码，也没看到数据库提交的代码，只看到注解`@Transactional`。它配置了`Propagation.REQUIRES_NEW`的传播行为，这意味着当别的方法调用该方法时，如果存在事务就沿用下来，如果不存在事务就开启新的事务，而隔离级别采用`Isolation.READ_COMMITTED`，并且设置超时时间为3秒。这样Spring就让开发人员主要的精力放在业务的开发上，而不是控制数据库的资源和事务上。
但是我们必须清楚地是，这里使用到了Spring AOP技术，而其底层的实现原理是动态代理，这就意味着对于静态方法和非public方法，注解`@Transactional`是失效的。还有一个更为隐蔽的问题-**自调用**，就是一个类的一个方法去调用自身另一个方法的过程，如下：
```
@Service
public class RoleServiceImpl implements RoleService {
	
	@Autowired
	private RoleMapper roleMapper = null;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
	public int insertRole(Role role) {
		return roleMapper.insertRole(role);
	}
	
    //自调用问题
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation=Isolation.READ_COMMITTED)
	public int insertRoleList(List<Role> roleList) {
		int count = 0;
		for (Role role : roleList) {
			try {
                //调用自身类的方法，产生自调用问题
				insertRole(role);
				count++;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return count;
	}
}
```
上面insertRoleList方法里角色插入两次，本来应该产生两个事务，但却使用了同一个事务，也就是说，在insertRole上标注的`@Transactional`失效了。出现这个问题的原因在于AOP的实现原理。由于`@Transactional`的实现原理是AOP，而AOP的实现原理是动态代理，在上面代码中使用的是自己调用自己的过程。换句话说，并不存在代理对象的调用，这样就不会产生AOP去为我们设置`@Transactional`配置的参数，就会出现自调用注解失效的问题。
可以如下修改，直接从容器中获取RoleService的代理对象，进行插入：

```java
@Service
public class RoleServiceImpl implements RoleService, ApplicationContextAware {
	
	@Autowired
	private RoleMapper roleMapper = null;
	
	private ApplicationContext ctx = null;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
	public int insertRole(Role role) {
		return roleMapper.insertRole(role);
	}
	
    //自调用问题
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation=Isolation.READ_COMMITTED)
	public int insertRoleList(List<Role> roleList) {
		int count = 0;
		for (Role role : roleList) {
			try {
                //调用自身类的方法，产生自调用问题
				insertRole(role);
				count++;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return count;
	}
	
	//消除自调用问题
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation= Isolation.READ_COMMITTED)
	public int insertRoleList2(List<Role> roleList) {
		int count = 0;
		//从容器中获取RoleService对象，实际是一个代理对象
		RoleService service = ctx.getBean(RoleService.class);
		for (Role role : roleList) {
			try {
				service.insertRole(role);
				count++;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return count;
	}

	
	//使用生命周期的接口方法，获取IoC容器
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
	}
}
```
也可以另写一个Service类，来解决自调用问题：

```java
@Service
public class RoleListServiceImpl implements RoleListService {
	@Autowired
	private RoleService roleService = null;
	Logger log = Logger.getLogger(RoleListServiceImpl.class);
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
	public int insertRoleList(List<Role> roleList) {
		int count = 0;
		for (Role role : roleList) {
			try {
				count += roleService.insertRole(role);
			} catch (Exception ex) {
				log.info(ex);
			}
		}
		return count;
	}
}
```

>项目源码：[https://github.com/sqmax/springlearning/tree/master/spring-tx](https://github.com/sqmax/springlearning/tree/master/spring-tx)







