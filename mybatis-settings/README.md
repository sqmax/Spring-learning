上节学习了使用MyBatis作为持久层的基本配置，其实MyBatis还有很多的特征需要配置，这一节进行更详细的总结。

下面是一份详细的MyBatis配置文件，先对里面的配置项有一个概览。

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
	<properties resource="jdbc.properties">
		<!-- <property name="database.driver" value="com.mysql.jdbc.Driver"/> <property 
			name="database.url" value="jdbc:mysql://localhost:3306/chapter4"/> <property 
			name="database.username" value="root"/> <property name="database.password" 
			value="123456"/> -->
	</properties>
	<typeAliases><!-- 别名 -->
		<!-- <typeAlias alias="role" type="top.sqmax.pojo.Role"/> -->
		<package name="top.sqmax.pojo" />
	</typeAliases>
	<typeHandlers>
		<!-- <typeHandler jdbcType="VARCHAR" javaType="string" handler="top.sqmax.typehandler.MyTypeHandler"
			/> -->
		<package name="top.sqmax.typehandler" />

	</typeHandlers>
	<!-- 数据库环境 -->
	<environments default="development">
		<environment id="development">
			<transactionManager type="JDBC" />
			<dataSource type="top.sqmax.datasource.DbcpDataSourceFactory">
				<property name="driver" value="${database.driver}" />
				<property name="url" value="${database.url}" />
				<property name="username" value="${database.username}" />
				<property name="password" value="${database.password}" />
			</dataSource>
		</environment>
	</environments>
	
    <!-- 
    type="DB_VENDOR"表明使用系统默认的databaseIdProvider
	<databaseIdProvider type="DB_VENDOR">
		<property name="Oracle" value="oracle" />
		<property name="MySQL" value="mysql" />
		<property name="DB2" value="db2" />
	</databaseIdProvider>
	 -->
	<databaseIdProvider
		type="top.sqmax.databaseidprovider.MyDatabaseIdProvider">
		<property name="msg" value="自定义DatabaseIdProvider" />
	</databaseIdProvider>
	<mappers>
		<package name="top.sqmax.mapper" />
	</mappers>
</configuration>
```

## properties属性

配置一些属性信息，下面在配置数据源的时候就有用到，比较简单。有两种方式：1、property子元素。 2、写入.properties属性文件。

## typeAliases属性

配置别名，因为对象实体需要包名和类名标识，写起来就很长，所以可以为其起个别名。
同样有两种方式：1、为单个的实体对象起别名。2、直接将配置所有对象实体所在的包名，这样为对象实体默认的映射别名是，类名的首字母小写。

```xml
<typeAliases><!-- 别名 -->
	<!-- <typeAlias alias="role" type="top.sqmax.pojo.Role"/> -->
	<package name="top.sqmax.pojo" />
</typeAliases>
```

## typeHandler类型转换器

因为持久层框架数据表和java类都有一个映射关系，它是为了解决数jdbcType（数据库表的列类型）和javaType之间的转换。

MyBatis本身在org.apache.ibatis.type包下已定义了许多typeHandler，来解决jdbcType和javaType之间的映射关系。大部分情况下我们不需要写typeHandler，但我们也可以写，来修改一些转换规则。通过实现TypeHandler接口来实现。

TypeHandler源码如下：

```java
public interface TypeHandler<T> {

  void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

  T getResult(ResultSet rs, String columnName) throws SQLException;

  T getResult(ResultSet rs, int columnIndex) throws SQLException;

  T getResult(CallableStatement cs, int columnIndex) throws SQLException;

}
```
我们可以实现该接口来自定义转换器，如下是实现varchar和String之间转化的Handler:

```java
public class MyTypeHandler implements TypeHandler<String> {

	Logger logger = Logger.getLogger(MyTypeHandler.class);

	@Override	
    public void setParameter(PreparedStatement ps, int i, String parameter,
			JdbcType jdbcType) throws SQLException {
		logger.info("设置string参数【" + parameter+"】");
		ps.setString(i, parameter);
	}

	@Override	
    public String getResult(ResultSet rs, String columnName)
			throws SQLException {
		String result = rs.getString(columnName);
		logger.info("读取string参数1【" + result+"】");
		return result;
	}

	@Override
	public String getResult(ResultSet rs, int columnIndex) throws SQLException {
		String result = rs.getString(columnIndex);
		logger.info("读取string参数2【" + result+"】");
		return result;
	}

	@Override
	public String getResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		String result = cs.getString(columnIndex);
		logger.info("读取string参数3【" + result+"】");
		return result;
	}
}
```

对于上面的转换器，我们还需要在MyBatis配置文件里配置一下：

```xml
<typeHandlers>
	<!-- <typeHandler jdbcType="VARCHAR" javaType="string" handler="top.sqmax.typehandler.MyTypeHandler"
		/> -->
	<package name="top.sqmax.typehandler" />

</typeHandlers>
```
上面可以看到两种配置方式，其中第二种对于MyTypeHandler还需要使用如下注解：

```java
//启用扫描注册的时候需要注解
@MappedTypes(String.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class MyTypeHandler implements TypeHandler<String>{...}
```
在映射文件里我们就可以使用该转换器了，有如下两种使用方式：

```xml
<resultMap id="roleMapper" type="role">
	<result property="id" column="id" />
	<result property="roleName" column="role_name" jdbcType="VARCHAR"
		javaType="string" />
	<result property="note" column="note"
		typeHandler="top.sqmax.typehandler.MyTypeHandler" />
</resultMap>
```

## environments配置

它主要配置数据库信息，下面有分为两个可配置的元素：事务管理器（transactionManager）和数据源（dataSource）。

其中transactionManager的type可以有两个选择JDBC、MANAGED。

在MyBatis中，transactionManager的值是一个Transaction类型，它是一个接口，有两个实现，JdbcTransaction和ManagedTransaction。

* 当配置`<transactionManager type="JDBC" />`，默认使用的事务管理器就是JdbcTransaction，它是已JDBC的方式对数据库的提交和回滚进行操作。
* 当配置`<transactionManager type="MANAGED" />`，默认使用的数据管理器就是ManagedTransaction。它的提交和回滚方法不用任何操作，而是把事务交给容器处理。

如果不想采用MyBatis的规则时，我们也可以自定义一个事务工厂，配置如下：

`<transactionManager type="to'ptop.sqmax.transaction.MyTransactionFactory" />`

自定义的事务工厂类都需要实现TransactionFactory接口。

## 数据源属性

在配置数据源时，可以看到下面的写法：

```xml
<dataSource type="POOLED">
    <property name="driver" value="com.mysql.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://localhost:3306/chapter3"/>
    <property name="username" value="root"/>
    <property name="password" value="123456"/>
</dataSource>
```
其中<dataSource type="POOLED">是什么意思？

在MyBatis中，数据库通过PooledDataSourceFactory、UnpooledDataSourceFactory、JndiDataSourceFactory三个工厂类来提供，前两者对应产生PooledDataSource、UnpooledDataSource，而JndiDataSourceFactory
则会根据JNDI的信息拿到外部容器实现的数据库连接对象。这三个工厂类，最后都会生成一个实现了DataSource接口的数据库连接对象。

![](http://p91462zt8.bkt.clouddn.com/db.PNG)

对于PooledDataSourceFactory、UnpooledDataSourceFactory他们产生的数据源实现了DataSource接口的类。

![](http://p91462zt8.bkt.clouddn.com/db2.PNG)

而JndiDataSourceFactory产生的数据源是自己配置的数据源类型。

除了上面那种配置，type还可以有如下两种类型：

```xml
<dataSource type="UNPOOLED">
<dataSource type="JNDI">
```

经过上面的分析，下面总结一下上面三种数据源属性的含义。

* UNPOLED：采用非数据库池的管理方式，每次请求都会打开一个新的数据库连接，所以创建会比较慢。在一些对性能没有很高要求的场合可以使用它。
* POOLED：使用连接池的方式获取数据库连接。
* JNDI：数据源JNDI的实现是为了能在应用服务器如Tomcat中使用，容器可以集中或在外部配置数据源。这个我不太熟悉，以后遇到在总结。

上面数据源的类型分别对应上图的三种DatSourceFactory的实现。

其实type的值除了上面3中字符串类型，还可以是一个类，如下：

```xml
<dataSource type="top.sqmax.datasource.DbcpDataSourceFactory">
	<property name="driver" value="${database.driver}" />
	<property name="url" value="${database.url}" />
	<property name="username" value="${database.username}" />
	<property name="password" value="${database.password}" />
</dataSource>
```
上面的top.sqmax.datasource.DbcpDataSourceFactory是一个自定义的类，它实现了MyBatis的DataSourceFactory接口，内容如下：

```java
public class DbcpDataSourceFactory implements DataSourceFactory {
	private Properties props = null;

	@Override
	public void setProperties(Properties props) {
		this.props = props;
	}

	@Override
	public DataSource getDataSource() {
		DataSource dataSource = null;
		try {
			dataSource = BasicDataSourceFactory.createDataSource(props);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dataSource;
	}
}
```

可以看到它重写了一个getDataSource()方法。

这就牵涉到使用第三方数据源了，MyBatis也支持第三方数据源。因为DataSource是JDBC定义的一个接口，它有许多第三方的实现，包括一些商用服务器（如WebLogin，WebSphere）等提供的实现，也有一些开源组织提供的实现（如DBCP和C3P0等）。

上面的`dataSource = BasicDataSourceFactory.createDataSource(props);`中的BasicDataSourceFactory就是属于DBCP数据源中的类， `BasicDataSourceFactory.createDataSource(props);`返回的也是DBCP对DataSource接口的一个实现。

## databaseIdProvider数据库厂商标识

```xml
<select id="getRole" parameterType="long" resultMap="roleMapper" databaseId="oracle">
	select id, role_name, note from t_role where id = #{id}
</select>
```

上面的databaseId是“oracle”，表明使用oracle数据库，其实这些值都是在MaBatis基础配置文件里配置的，如下：

```xml
<databaseIdProvider type="DB_VENDOR">
	<property name="Oracle" value="oracle" />
	<property name="MySQL" value="mysql" />
	<property name="DB2" value="db2" />
</databaseIdProvider>
```
上面是MyBatis的默认规则，可以不用配置。其实我们也可以自己写一个类来实现DataBaseIdProvider来改变默认规则，不常用，这里就不再贴代码。









