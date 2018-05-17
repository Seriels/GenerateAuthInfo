package com.pz.auth.service;

import com.pz.auth.common.AuthParam;
import com.pz.auth.common.RedisUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author pz
 * @version 2.3
 * @E-mail 2919274153@qq.com
 * @date 2018-5-17 14:59:09
 */
public class GenRedis {

    private RedisUtils redisUtils;

    public GenRedis(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    public Map<String, String> hgetAll(String roles, int i) {
        return redisUtils.hgetAll(roles, i);
    }

    public Set<String> keys(String s, int i) {
        return redisUtils.keys(s, i);
    }

    public Set<String> setGet(String s, int i) {
        return redisUtils.setGet(s, i);
    }

    public Optional<String> hget(String auths, String o, int i) {
        return redisUtils.hget(auths, o, i);
    }

    public Long hset(String roles, String s, String o) {
        return redisUtils.hset(roles, s, o);
    }

    public Long sAdd(String s, int i, String[] authInfo) {
        return redisUtils.sAdd(s, i, authInfo);
    }

    /**
     * 修改redis 里面的权限信息，，也可以看做是增加
     *
     * @return
     */
    void updateRedisAuths(Map<String, String> auths) {
        Optional<Map<String, String>> auths1 = Optional.ofNullable(auths);
        if (auths1.isPresent()) {
            auths1.get().forEach((s, o) -> {
                hset(AuthParam.AUTHS, s, o);
            });
        }
    }

    /**
     * 修改redis 里面的角色信息，，也可以看做是增加
     *
     * @return
     */
    void updateRedisRoles(Map<String, String> roles) {
        Optional<Map<String, String>> roles1 = Optional.ofNullable(roles);
        if (roles1.isPresent()) {
            roles1.get().forEach((s, o) -> {
                hset(AuthParam.ROLES, s, o);
            });
        }
    }

    /**
     * 增加到redis 角色跟权限相关联
     *
     * @return
     */
    void addRoleAndAuth(String rolesInfo, String... authInfo) {
        sAdd(AuthParam.AUTHSROLES + rolesInfo, -1, authInfo);
    }

    /**
     * 查询redis里面的所有角色信息
     *
     * @return
     */
    Map<String, String> queryRedisRoles() {
        return hgetAll(AuthParam.ROLES, -1);
    }

    /**
     * 查询redis里面的所有权限信息
     *
     * @return
     */
    Map<String, String> queryRedisAuths() {
        return hgetAll(AuthParam.AUTHS, -1);
    }

    /**
     * 从redis里面获取所用的角色及其相关权限
     *
     * @return
     */
    Map<String, Map<String, String>> queryRedisRolesAuths() {
        Set<String> strings = keys(AuthParam.AUTHSROLES + "*", -1);
        return new HashMap<String, Map<String, String>>(strings.size()) {{

            Map<String, Set<String>> collect = strings.stream().filter(s -> StringUtils.isBlank(s) || s.lastIndexOf("_") > -1)
                    .collect(Collectors.toMap(o -> o.split("_")[1], s -> setGet(s, -1)));
            if (strings != null || !strings.isEmpty()) {
                collect.forEach((k, v) -> {
                    Map<String, String> collect1 = v.stream().collect(Collectors.toMap(o ->
                                    o
                            , o ->
                                    hget(AuthParam.AUTHS, o, -1).get()
                    ));
                    put(k, collect1);
                });
            }


        }};

    }
}
