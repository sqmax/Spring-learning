package top.sqmax.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import top.sqmax.pojo.Role;
import top.sqmax.service.RoleListService;

import java.util.ArrayList;
import java.util.List;

public class Chapter13Main {
	public static void main(String [] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext ("spring-cfg.xml");
		RoleListService roleListService = ctx.getBean(RoleListService. class);
		List<Role> roleList = new ArrayList<Role>();
        for (int i=1; i<=2; i++) {
            Role role = new Role();
            role.setRoleName("role_name_" + i);
            role.setNote("note_" + i);
            roleList.add(role);
        }
        int count = roleListService.insertRoleList(roleList);
	   System.out.println(count);

	}
}