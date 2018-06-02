package top.sqmax.main;

import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.log4j.Logger;

import top.sqmax.enumeration.SexEnum;
import top.sqmax.mapper.RoleMapper;
import top.sqmax.mapper.UserMapper;
import top.sqmax.pojo.Role;
import top.sqmax.pojo.User;
import top.sqmax.utils.SqlSessionFactoryUtils;

public class Chapter4Main {

	public static void main(String[] args) {
		testRoleMapper();
		testTypeHandler();
	}

	private static void testRoleMapper() {
		Logger log = Logger.getLogger(Chapter4Main.class);
		SqlSession sqlSession = null;
		try {
			sqlSession = SqlSessionFactoryUtils.openSqlSession();
			RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
			Role role = roleMapper.getRole(1L);
			log.info(role.getRoleName());
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
	}

	private static void testTypeHandler() {
		Logger log = Logger.getLogger(Chapter4Main.class);
		SqlSession sqlSession = null;
		try {
			sqlSession = SqlSessionFactoryUtils.openSqlSession();
			UserMapper userMapper  = sqlSession.getMapper(UserMapper.class);
			User user = userMapper.getUser(1L);
			System.out.println(user.getSex().getName());
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
	}

}