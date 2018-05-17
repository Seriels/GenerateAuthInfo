package com.pz.auth.service;


import com.pz.auth.common.ApiInterface;
import com.pz.auth.common.AuthParam;
import com.pz.auth.common.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;



@Slf4j
public class MyControl {
    public MyControl(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
        apiService=new ApiService();
    }
    private RedisUtils redisUtils;
    private ApiService apiService;

    private  Map<String, String> auths;
    private  Map<String,  Map<String, String>> roleAuths;
    private Map<String,String> roles;

    /**
     * aaaa
     *
     * @return
     */
     Map<String,String> queryRedisRoles() {
        return redisUtils.hgetAll(AuthParam.ROLES, -1);
    }

     Map<String,String> queryRedisAuths() {
        return redisUtils.hgetAll(AuthParam.AUTHS, -1);
    }


    Map<String,  Map<String, String>> queryRedisRolesAuths() {
        return   new  HashMap<String,  Map<String, String>>(){{
            Set<String> strings = redisUtils.keys(AuthParam.AUTHSROLES + "*", -1);
            Map<String, Set<String>> collect = strings.stream().filter(s -> StringUtils.isBlank(s)||s.lastIndexOf("_")>-1)
                    .collect(Collectors.toMap(o -> o.split("_")[1], s -> redisUtils.setGet(s, -1)));

            if(strings!=null||!strings.isEmpty()){
                collect.forEach((k,v) -> {
                    Map<String, String> collect1 = v.stream().collect(Collectors.toMap(o ->
                                    o
                    , o ->
                            redisUtils.hget(AuthParam.AUTHS, o, -1).get()
                    ));
                    put(k,collect1);
                });
                }
          }};

    }
     void updateRedisRoles(Map<String,String> roles) {
        Optional<Map<String, String>> roles1 = Optional.ofNullable(roles);
        if (roles1.isPresent()) {
            roles1.get().forEach((s, o) -> {
                redisUtils.hset(AuthParam.ROLES, s,o);
            });
        }
     }

     void updateRedisAuths(Map<String,String> auths) {
        Optional<Map<String, String>> auths1 = Optional.ofNullable(auths);
        if (auths1.isPresent()) {
            auths1.get().forEach((s, o) -> {
                redisUtils.hset(AuthParam.AUTHS, s,o);
            });
        }
    }


     void init() {
         try {
             roles = queryRedisRoles();
             auths = queryRedisAuths();
             roleAuths=queryRedisRolesAuths();
         } catch (Exception e) {
             log.error(e.getMessage(),e);
         }

         if(null==roleAuths||roleAuths.isEmpty()){
             roleAuths=new LinkedHashMap<>();
         }
        if (null==roles||roleAuths.isEmpty()) {
            roles=new LinkedHashMap<>();
            roles.put(AuthParam.ROOT,"rootUser");
        }
        if (null==auths||roleAuths.isEmpty()) {
            auths=new LinkedHashMap<>();
        }
    }

    void addRoleAndAuth(String rolesInfo,String... authInfo){
         redisUtils.sAdd(AuthParam.AUTHSROLES+rolesInfo,-1,authInfo);
    }

    void After() {
        // 增加权限表数据  跟角色数据
        updateRedisRoles(roles);
        roleAuths.values().stream().filter(s -> s!=null).forEach(s -> auths.putAll(s) );
        updateRedisAuths(auths);

        roleAuths.forEach((k, v) -> {
            // 增加角色跟相关的权限
              if(StringUtils.isNotBlank(k)&&!AuthParam.ROOT.equals(k.trim())){
                  addRoleAndAuth(k,v.keySet().toArray(new String[]{}));
              }
            addRoleAndAuth(AuthParam.ROOT,v.keySet().toArray(new String[]{}));
        });


       // 查询是否正确
        Map<String,String> strings = queryRedisRoles();
        strings.forEach((k,v) -> log.info("k {} ,, v{}",k,v));
        Map<String,String> strings1 = queryRedisAuths();
        strings1.forEach((k,v) -> log.info("k {} ,, v{}",k,v));
        try {
            Map<String, Map<String, String>> stringMapMap = queryRedisRolesAuths();
            stringMapMap.forEach((k,v) -> log.info("k {} ,, v{}",k,v));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

    }

    public void run(Class<?> primarySource) {
        init();
        try {
            List<ApiInterface> projectApi = apiService.getProjectApi(primarySource);
            log.info(String.valueOf(projectApi.size()));
            List<ApiInterface> collect = projectApi.stream().collect(Collectors.toList());
            collect.stream().forEach((v) -> {
                  roleAuths.putAll( v.getAuthInfo());
                  roles.putAll( v.getRoleInfo());
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        After();
    }
}
