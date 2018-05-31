package top.sqmax.service.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import top.sqmax.service.RoleService;
import top.sqmax.dto.Role;

/**
 * Created by SqMax on 2018/5/29.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RoleServiceImpl implements RoleService {
    @Override
    public void printRole(Role role) {
        System.out.println("id="+role.getId()+";name="+role.getName()+";note="+role.getNote());
    }
}
