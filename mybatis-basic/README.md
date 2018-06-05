## MyBatis的核心组件

* SqlSessionFactoryBuilder：它会根据配置或者代码生成SqlSessionFactory。
* SqlSessionFactory：依赖它来生成SqlSession。
* SqlSession：可以获取Mapper的接口。
* SQL Mapper：它由一个Java接口和XML文件构成，需要给出对应的SQL和映射规则。ta它负责发送SQL去执行，并返回结果。

## MyBatis的配置

MyBatis中的XML配置分为两类，一类是基础的配置文件，通常只有一个，主要是配置一些最基本的上下文参数和运行环境；另一类是映射文件，他可以配置映射关系、SQL、参数等信息。

下面是一份基础配置文件mybatis-config.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration   PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
  <typeAliases><!-- 别名 -->
      <typeAlias alias="role" type="top.sqmax.pojo.Role"/>
  </typeAliases>
  <!-- 数据库环境 -->
  <environments default="development">
    <environment id="development">
      <transactionManager type="JDBC"/>
      <dataSource type="POOLED">
        <property name="driver" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/chapter3"/>
        <property name="username" value="root"/>
        <property name="password" value="123456"/>
      </dataSource>
    </environment>
  </environments>
  <!-- 映射文件 -->
  <mappers>
    <mapper resource="RoleMapper.xml"/>
    <!--<mapper class="top.sqmax.mapper.RoleMapper2"/>-->
  </mappers>
  </mappers>
</configuration>
```

有了基础配置文件，就可以用一段简短的代码生成SqlSessionFactory了，如下：

```java
InputStream inputStream;
SqlSessionFactory sqlSessionFactory=null;
try {
    inputStream = Resources.getResourceAsStream("mybatis-config.xml");
    sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);
} catch (IOException e){
    e.printStackTrace();
}
```
然后可以通过SqlSessionFactory获取SqlSession:

```java
sqlSessionFactory.openSession();
```

上面的mybatis-config.xml还引入了一个RoleMapper.xml文件，他就是MyBatis的核心组件，包括一个xml文件和一个接口。
如下是一个简单的例子：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="top.sqmax.mapper.RoleMapper">

	<insert id="insertRole" parameterType="role">
		insert into t_role(role_name, note) values(#{roleName}, #{note})
	</insert>

	<delete id="deleteRole" parameterType="long">
		delete from t_role where id= #{id}
	</delete>

	<update id="updateRole" parameterType="role">
		update t_role set role_name = #{roleName}, note = #{note} where id= #{id}
	</update>

	<select id="getRole" parameterType="long" resultType="role">
		select id,
		role_name as roleName, note from t_role where id = #{id}
	</select>

	<select id="findRoles" parameterType="string" resultType="role">
		select id, role_name as roleName, note from t_role
		where role_name like concat('%', #{roleName}, '%')
	</select>
</mapper>
```
它对应的接口如下：

```java
public interface RoleMapper {
	public int insertRole(Role role);
	public int deleteRole(Long id);
	public int updateRole(Role role);
	public Role getRole(Long id);
	public List<Role> findRoles(String roleName);
}
```

然后我们就可以使用SqlSession对象获得Mapper，使用Mapper对数据库操作。如下：

```java
RoleMapper roleMapper=sqlSession.getMapper(RoleMapper.class);
Role role= roleMapper.getRole(1L); 
```

完整代码：[这里](https://github.com/sqmax/Spring-learning/tree/master/mybatis-basic)


