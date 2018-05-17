package com.pz.auth.annotation;


import org.apache.commons.lang.StringUtils;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
//@Repeatable(Control.class)
public @interface MyAuth {
    String value() default "";

    /**
     *   name()  证明权限的中文名称
     * @return
     */
    String name() default "";

    /**
     *   other()  暂时没卵用。。。
     * @return
     */
    String other() default "";

    /**
     *    这个只有在单独使用该注解的时候才会起效果
     *  1
     *  @GetMapping("/")
     *  @Control({@MyRole(info = "aaa",name = "aaa",value = {@MyAuth(value = "ss:ss",name = "ssss")})})
     *  @MyAuth(value = "menu:test",name = "ss测试",isAll = true)
     *   public R selectRoleMenu(HttpServletRequest httpServletRequest){
     *
     *   这样的话就会将该方法的所有角色都会用该权限
     *  2
     *      *  @GetMapping("/")
     *      *  @Control({@MyRole(info = "aaa",name = "aaa",value = {@MyAuth(value = "ss:ss",name = "ssss")})})
     *      *  @MyAuth(value = "menu:test",name = "ss测试",isAll = false)  or  *  @MyAuth(value = "menu:test",name = "ss测试")
     *      *   public R selectRoleMenu(HttpServletRequest httpServletRequest){
     *      *
     *      *   这样的话就会将该该权限只会增加权限，不会有任何操作
     *
     * @return
     */
    boolean isAll() default false;
 }
