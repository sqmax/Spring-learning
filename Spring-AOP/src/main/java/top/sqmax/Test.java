package top.sqmax;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.sqmax.config.AopConfig;
import top.sqmax.dto.Role;
import top.sqmax.service.RoleService;
import top.sqmax.service.RoleVerifier;

/**
 * Hello world!
 *
 */
public class Test
{
    public static void main( String[] args )
    {
        ApplicationContext context=new AnnotationConfigApplicationContext(AopConfig.class);
        RoleService roleService=context.getBean(RoleService.class);

        Role role=new Role(1,"sun","handsome");
        roleService.printRole(role);

        System.out.println("---------------");

        RoleVerifier roleVerifier=(RoleVerifier)roleService;
        if (roleVerifier.verify(role)) {
            roleService.printRole(role);
        }
    }
}
