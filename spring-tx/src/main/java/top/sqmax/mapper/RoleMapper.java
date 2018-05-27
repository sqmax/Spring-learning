package top.sqmax.mapper;

import org.springframework.stereotype.Repository;
import top.sqmax.pojo.Role;

@Repository
public interface RoleMapper {
	public int insertRole(Role role);
}