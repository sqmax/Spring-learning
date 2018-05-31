package top.sqmax.service.impl;

import top.sqmax.dto.Role;
import top.sqmax.service.RoleVerifier;

/**
 * Created by SqMax on 2018/5/29.
 */
public class RoleVerifierImpl implements RoleVerifier {
    @Override
    public boolean verify(Role role) {
        return role!=null;
    }
}
