package top.sqmax.dao;

import org.springframework.stereotype.Repository;
import top.sqmax.pojo.Role;


@Repository
public interface RoleDao {
	
	public Role getRole(Long id);
}
