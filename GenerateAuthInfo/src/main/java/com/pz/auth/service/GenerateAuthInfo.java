package com.pz.auth.service;


import com.pz.auth.common.ApiInterface;
import com.pz.auth.common.AuthParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author pz
 * @version 2.3
 * @E-mail 2919274153@qq.com
 * @date 2018-5-17 11:55:31
 */
@Slf4j
public abstract class GenerateAuthInfo {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public GenerateAuthInfo(GenRedis genRedis) {
        this.genRedis = genRedis;
    }

    public GenerateAuthInfo() {
    }

    private GenRedis genRedis;
    private ApiService apiService = new ApiService();
    /**
     * auths
     * save  roles  and auths
     */
    private Map<String, String> auths;
    /**
     * save  roles  and auths
     */
    private Map<String, Map<String, String>> roleAuths;
    /**
     * save  roles
     */
    private Map<String, String> roles;


    void init() {
        try {
            roles = genRedis.queryRedisRoles();
            auths = genRedis.queryRedisAuths();
            roleAuths = genRedis.queryRedisRolesAuths();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (null == roleAuths || roleAuths.isEmpty()) {
            roleAuths = new LinkedHashMap<>();
        }
        if (null == roles || roleAuths.isEmpty()) {
            roles = new LinkedHashMap<>();
            roles.put(AuthParam.ROOT, "rootUser");
        }
        if (null == auths || roleAuths.isEmpty()) {
            auths = new LinkedHashMap<>();
        }
    }

    void noInit() {
        roleAuths = new LinkedHashMap<>();
        roles = new LinkedHashMap<>();
        roles.put(AuthParam.ROOT, "rootUser");
        auths = new LinkedHashMap<>();
    }


    void after() {
        // 增加权限表数据  跟角色数据
        genRedis.updateRedisRoles(roles);
        queryAuthsInfo();
        genRedis.updateRedisAuths(auths);

        roleAuths.forEach((k, v) -> {
            // 增加角色跟相关的权限
            if (StringUtils.isNotBlank(k) && !AuthParam.ROOT.equals(k.trim())) {
                genRedis.addRoleAndAuth(k, v.keySet().toArray(new String[]{}));
            }
            genRedis.addRoleAndAuth(AuthParam.ROOT, v.keySet().toArray(new String[]{}));
        });

        // 查询是否正确
        Map<String, String> roles = genRedis.queryRedisRoles();
        Map<String, String> auths = genRedis.queryRedisAuths();
        Map<String, Map<String, String>> roleAuths = Collections.EMPTY_MAP;
        try {
            roleAuths = genRedis.queryRedisRolesAuths();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        loggerAll(roles, auths, roleAuths);


    }

    void noAfter() {
        queryAuthsInfo();
        loggerAll(roles, auths, roleAuths);
    }


    public abstract void run(Class<?> primarySource);

    void runCore(Class<?> primarySource) {
        try {
            List<ApiInterface> projectApi = apiService.getProjectApi(primarySource);
            log.info(String.valueOf(projectApi.size()));
            List<ApiInterface> collect = projectApi.stream().collect(Collectors.toList());
            collect.stream().forEach((v) -> {
                roleAuths.putAll(v.getRoleAndAuthInfo());
                roles.putAll(v.getRoleInfo());
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void queryAuthsInfo() {
        roleAuths.values().stream().filter(s -> s != null).forEach(s -> auths.putAll(s));
    }

    private void loggerAll(Map<String, String> roles, Map<String, String> auths, Map<String, Map<String, String>> roleAuths) {
        roles.forEach((k, v) -> log.info("k {}  \n v{}", k, v));
        auths.forEach((k, v) -> log.info("k {}   \n v{}", k, v));
        roleAuths.forEach((k, v) -> log.info("k {} \n v{}", k, v));
    }
}
