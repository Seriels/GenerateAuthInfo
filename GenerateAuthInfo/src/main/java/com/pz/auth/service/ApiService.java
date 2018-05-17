package com.pz.auth.service;

import com.pz.auth.annotation.Control;
import com.pz.auth.annotation.MyAuth;


import com.pz.auth.annotation.MyRole;
import com.pz.auth.common.ApiInterface;
import com.pz.auth.common.PackageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
 class ApiService {


    /**
     * 获取被 Description 注解的所有接口.
     * 要求 所有返回为 json 的接口必须要被 Description 注解.
     *
     * @return 接口列表
     */
    public List<ApiInterface> getProjectApi(Class<?> primarySource) throws ClassNotFoundException {
        Package aPackage = primarySource.getPackage();
        List<ApiInterface> interfaces = new ArrayList<>();
        List<Class<Object>> classes = PackageUtil.findPackageClass(aPackage.getName());
        classes.stream()
                .filter(clazz -> isController(clazz))
                .forEach(clazz -> {
                    try {
                        addInterface(interfaces, clazz);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
        log.info("interfaces.size {}", interfaces.size());
        return interfaces;
    }

    // 添加 Controller 的接口. 只包含有 Description 注解的接口,倘若返回是String,ModelAndView 等时,不能加该注解
    private void addInterface(List<ApiInterface> interfaces, Class clazz) throws ClassNotFoundException {
        // 获取类注解 RequestMapping 的映射路径
        String prefix = getControllerPrefix(clazz);
        // 获取类中 public 且含有 Description 的方法
        Set<Method> methods = PackageUtil.findAnnotationMethods(clazz, Control.class);
        // 遍历所有方法,有目标注解的就解析到接口列表中 interfaces
        for (Method method : methods) {
            List<Annotation> annotations = Arrays.asList(method.getDeclaredAnnotations());
            String name = method.getName();
            ApiInterface function = new ApiInterface();
            function.setMethodName(name);
            //  根据注解类型分组
            Map<Annotation, List<Annotation>> collect = annotations.stream().collect(Collectors.groupingBy(o -> o, Collectors.toList()));
            collect.forEach((k, v) ->{
                //原来的 逻辑  获取Control 里面的数据
                if(k instanceof  Control){
                    getControlInfo(v, function);
                }
                //
                if(k instanceof  MyRole){
                    getMyRoleInfo(v, function);
                }

                if(k instanceof MyAuth ){
                    getMyAuthInfo(v,function);
                }

            } );
            //  如果 角色权限信息跟名字相等就增加，否则不增加,,
            if (function.isInfoEquals()) {
                interfaces.add(function);
            }
        }
    }

    private void getMyAuthInfo(List<Annotation> annotations, ApiInterface function) {
        annotations.stream()
                .filter(a -> a instanceof MyAuth)
                .forEach(a -> {
//                    //  添加权限信息
//                    function.setsAuthInfo(() ->
//
//                    );
                });
    }
    private void getMyRoleInfo(List<Annotation> annotations, ApiInterface function) {
        annotations.stream()
                .filter(a -> a instanceof MyRole)
                .forEach(a -> {
                    //  添加角色信息
                    function.setsRoleInfo(() ->
                           getMyRoleCollect((MyRole) a)

                    );
                    //  添加权限信息
                    function.setsAuthInfo(() ->
                            getMyAuthCollect((MyRole) a)
                    );
                });
    }



    private void getControlInfo(List<Annotation> annotations, ApiInterface function) {
        annotations.stream()
                .filter(a -> a instanceof Control)
                .forEach(a -> {
                    //  添加角色信息
                    function.setsRoleInfo(() ->
                         getMyRoleCollect(((Control) a).value())
                    );
                    //  添加权限信息
                    function.setsAuthInfo(() ->
                            getMyAuthCollect(((Control) a).value())
                    );
                });
    }

    private Map getMyRoleCollect(MyRole... value) {
        return Stream.of(value)
                .filter(myRole -> StringUtils.isNotEmpty(myRole.info()))
                .collect(Collectors.toMap(MyRole::info, MyRole::name));
    }


    /**
     *    传入一个或则多个MyRole对象进行处理
     * @param a  反射获取的一个或者多个@MyRole对象
     * @return 返回一个map<value,name> 对象
     */
    private Map<String, HashMap> getMyAuthCollect(MyRole... a) {
        return Stream.of(a)
                .filter(myRole -> myRole.value().length != 0)
                .collect(Collectors.toMap(MyRole::info, myRole ->
                        new HashMap(myRole.value().length) {{
                            Stream.of(myRole.value()).forEach(
                                    myAuth -> put(myAuth.value(), myAuth.name())
                            );
                        }}));
    }


    /**
     * 封装接口参数
     *
     * @param method 反射获取的方法
     * @param param  参数列表
     */
    private void fillMethodParam(Method method, List<Map<String, String>> param) {
        Class<?>[] types = method.getParameterTypes();
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < types.length; i++) {
            Map<String, String> map = new HashMap<>();
            fillParameters(types[i], parameters[i], map);
            param.add(map);
        }
    }

    /**
     * 填充参数类型和参数名以及是否是必须参数
     *
     * @param type      参数类型
     * @param parameter 参数名
     * @param map       参数map
     */
    private void fillParameters(Class<?> type, Parameter parameter, Map<String, String> map) {
        map.put("type", "" + type.getSimpleName() + "");
        map.put("name", "" + parameter.getName() + "");

        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
        if (requestParam != null) {
            map.put("required", requestParam.required() + "");
        } else if (pathVariable != null && !StringUtils.isBlank(pathVariable.name())) {
            map.put("required", pathVariable.required() + "");
        } else {
            map.put("required", "true");
        }
    }

    /**
     * 获取 RequestMapping 部分
     *
     * @param clazz
     * @return
     */
    private String getControllerPrefix(Class clazz) {
        RequestMapping annotation = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
        String prefix = "";
        if (annotation != null) {
            String value = annotation.value()[0];
            prefix = value.endsWith("/") ? value : value + "/";
        }
        return prefix;
    }

    /**
     * 判断是不是控制器
     *
     * @param clazz 类
     * @return
     */
    private boolean isController(Class clazz) {
        Annotation rest = clazz.getAnnotation(RestController.class);
        Annotation controller = clazz.getAnnotation(Controller.class);
        return rest != null || controller != null;
    }

}

