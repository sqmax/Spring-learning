package top.sqmax.dao;

import org.springframework.stereotype.Repository;
import top.sqmax.pojo.Role;

import java.util.List;


@Repository
public interface RoleDao {
	
	public Role getRole(Long id);

	public long insertRole(Role role);

	public long deleteRole(long id);

	public void updateRole(Role role);

	public List<Role> findAll();
}
