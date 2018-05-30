package top.sqmax.aspect;

import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import top.sqmax.dto.Role;
import top.sqmax.service.RoleVerifier;
import top.sqmax.service.impl.RoleVerifierImpl;

/**
 * Created by SqMax on 2018/5/29.
 */
@Component
@Aspect
public class RoleAspect {

    @DeclareParents(value = "top.sqmax.service.impl.RoleServiceImpl+",defaultImpl = RoleVerifierImpl.class)
    public RoleVerifier roleVerifier;

//    将我们上面的业务方法声明为一个切点
    @Pointcut("execution(* top.sqmax.service.impl.RoleServiceImpl.printRole(..))")
    public void print() {
    }

    @Before("print()")
    public void before() {
        System.out.println("before......");
    }

    @After("print()")
    public void after() {
        System.out.println("after....");
    }

    @AfterReturning("print()")
    public void afterReturning() {
        System.out.println("afterReturning.....");
    }

    @AfterThrowing("print()")
    public void afterThrowing() {
        System.out.println("afterThrowing...");
    }
}
