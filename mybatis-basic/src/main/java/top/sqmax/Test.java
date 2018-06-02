package top.sqmax;

import org.apache.ibatis.session.SqlSession;
import top.sqmax.mapper.RoleMapper;
import top.sqmax.pojo.Role;
import top.sqmax.utils.SqlSessionFactoryUtils;

/**
 * Hello world!
 *
 */
public class Test
{
    public static void main( String[] args ) {
        SqlSession sqlSession=null;
        sqlSession= SqlSessionFactoryUtils.openSqlSession();
        RoleMapper roleMapper=sqlSession.getMapper(RoleMapper.class);
        Role role= roleMapper.getRole(1L);


        System.out.println( role );
    }
}
