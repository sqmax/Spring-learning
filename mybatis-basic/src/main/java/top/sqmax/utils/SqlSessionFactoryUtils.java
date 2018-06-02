package top.sqmax.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by SqMax on 2018/5/30.
 */
public class SqlSessionFactoryUtils {
    private final static Class<SqlSessionFactoryUtils> LOCK=SqlSessionFactoryUtils.class;
    private static SqlSessionFactory sqlSessionFactory=null;

//    将构造其私有，其他代码就不能通过new的方式创建它
    private SqlSessionFactoryUtils(){};

//    只能创建一个SqlSessionFactory对象
    public static SqlSessionFactory getSqlSessionFactory(){
        synchronized (LOCK) {
            if (sqlSessionFactory != null) {
                return sqlSessionFactory;
            }
            InputStream inputStream;
            SqlSessionFactory sqlSessionFactory=null;
            try {
                inputStream = Resources.getResourceAsStream("mybatis-config.xml");
                sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);
            } catch (IOException e){
                e.printStackTrace();
            }
            return sqlSessionFactory;
        }
    }

    public static SqlSession openSqlSession() {
        if (sqlSessionFactory == null) {
           sqlSessionFactory=getSqlSessionFactory();
        }
        return sqlSessionFactory.openSession();
    }
}
