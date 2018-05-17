package com.pz.auth.service;

import com.pz.auth.common.RedisUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GenRedis {

    private RedisUtils redisUtils;

    public GenRedis(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    public Map<String,String> hgetAll(String roles, int i) {
        return redisUtils.hgetAll(roles,i);
    }

    public Set<String> keys(String s, int i) {
     return    redisUtils.keys(s,i);
    }

    public Set<String> setGet(String s, int i) {
       return  redisUtils.setGet(s,i);
    }

    public Optional<String> hget(String auths, String o, int i) {
      return   redisUtils.hget(auths,o,i);
    }

    public Long hset(String roles, String s, String o) {
        return   redisUtils.hset(roles,s,o);
    }

    public Long sAdd(String s, int i, String[] authInfo) {
        return redisUtils.sAdd(s,i,authInfo);
    }
}
