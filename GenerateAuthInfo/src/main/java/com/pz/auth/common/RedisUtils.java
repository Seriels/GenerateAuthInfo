package com.pz.auth.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author pz
 * @E-mail 2919274153@qq.com
 * @version 2.3
 * @date 2018-5-16 16:09:29
 */
@Slf4j
public class RedisUtils {

    JedisConnectionFactory jedisConnectionFactory;


    public RedisUtils(JedisConnectionFactory jedisConnectionFactory) {
        this.jedisConnectionFactory = jedisConnectionFactory;
    }

    /**
     * 获取Jedis实例
     * @return
     */
    public synchronized RedisConnection getJedis() {
        try {
            if (jedisConnectionFactory != null) {
                RedisConnection connection = jedisConnectionFactory.getConnection();

                return connection;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 释放jedis资源
     * @param connection
     */
    public static void returnResource(final RedisConnection connection) {
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * 获取redis键值-object
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        RedisConnection jedis1 = getJedis();
        try {
          Jedis  jedis = (Jedis) jedis1.getNativeConnection();
            String bytes = jedis.get(key);
            if(!StringUtils.isEmpty(bytes)) {
                return bytes;
            }
        } catch (Exception e) {
            log.error("getObject获取redis键值异常:key=" + key + " cause:" + e);
        } finally {
            returnResource(jedis1);
        }
        return null;
    }
    /**
     * 获取redis键值-object
     *
     * @param key
     * @return
     */
    public <T> T get(String key,Class<T> t) {
        RedisConnection jedis1 = getJedis();
        try {
          Jedis  jedis = (Jedis) jedis1.getNativeConnection();
            String bytes = jedis.get(key);
            if(!StringUtils.isEmpty(bytes)) {
                return JSONObject.parseObject(bytes,t);
            }
        } catch (Exception e) {
            log.error("get T 获取redis键值异常:key=" + key + " cause:" + e);
            return  null;
        } finally {
            returnResource(jedis1);
        }
        return null;
    }

    /**
     * 获取redis键值-object
     *
     * @param key
     * @return
     */
    public <T> T get(String key,Class<T> t,int expiretime) {
        RedisConnection jedis1 = getJedis();
        try {
            Jedis  jedis = (Jedis) jedis1.getNativeConnection();
            String bytes = jedis.get(key);
            if(!StringUtils.isEmpty(bytes)) {
                jedis.expire(key, expiretime*1000);
                return JSONObject.parseObject(bytes,t);
            }
        } catch (Exception e) {
            log.error("get T 获取redis键值异常:key=" + key + " cause:" + e);
            return  null;
        } finally {
            returnResource(jedis1);
        }
        return null;
    }


    /**
     * 设置redis键值-object
     * @param key
     * @param value
     * @param
     * @return
     */
    public String set(String key, Object value) {
        RedisConnection jedis1 = getJedis();
        try {
            Jedis  jedis = (Jedis) jedis1.getNativeConnection();
            return jedis.set(key, JSON.toJSONString(value));
        } catch (Exception e) {
            log.error("setObject设置redis键值异常:key=" + key + " value=" + value + " cause:" + e);
            return null;
        } finally {
            returnResource(jedis1);
        }
    }

    public String set(String key, Object value,int expiretime) {
        String result = "";
        RedisConnection jedis1 = getJedis();
        try {
            Jedis  jedis = (Jedis) jedis1.getNativeConnection();
            result = jedis.set(key, value instanceof String?(String) value:JSON.toJSONString(value));
            if(result.equals("OK")) {
                jedis.expire(key, expiretime);
            }
            return result;
        } catch (Exception e) {
            log.error("setObject设置redis键值异常:key=" + key + " value=" + value + " cause:" + e);
        } finally {
            returnResource(jedis1);
        }
        return result;
    }


    /**
     * -----------------------------------------
     * ms     %     Task name
     * -----------------------------------------
     * 00051  035%  hset
     * 00096  065%  hget
     * @param key
     * @param hash
     * @param value
     * @return
     */
    public Long hset(String key,String hash, Object value) {
        return  hset(key,hash,value,-1);
    }
    public Long hset(String key,String hash, Object value,int expiretime) {
        Long result = -1L;
        RedisConnection jedis1 = getJedis();
        try {
            Jedis  jedis = (Jedis) jedis1.getNativeConnection();
            result = jedis.hset(key,hash, value instanceof String ?(String) value:JSON.toJSONString(value));
            UpExpiretime(key, expiretime, Optional.ofNullable(result), jedis);
            log.error("hset设置redis键值成功:key=" + key  + " hash=" + hash + " value=" + value);
            return result;
        } catch (Exception e) {
            log.error("hset设置redis键值异常:key=" + key  + " hash=" + hash +  " value=" + value + " cause:" + e);
        } finally {
            returnResource(jedis1);
        }
        return result;
    }

    private <T> void UpExpiretime(String key, int expiretime,Optional<T> result, Jedis jedis) {
        if(result.isPresent()) {
           if(expiretime>0){
               jedis.expire(key, expiretime);
           }
        }
    }

    public Optional<String> hget(String key,String hash,int expiretime) {
        String result = "";
        RedisConnection jedis1 = getJedis();
        try {
            Jedis  jedis = (Jedis) jedis1.getNativeConnection();
            result = jedis.hget(key,hash);
            UpExpiretime(key, expiretime,  Optional.ofNullable(result), jedis);
           if(!org.apache.commons.lang.StringUtils.isBlank(result)){
               return Optional.ofNullable(result);
           }
        } catch (Exception e) {
            log.error("hget获取redis键值异常:key=" + key  + " hash=" + hash + "  cause:" + e);
        } finally {
            returnResource(jedis1);
        }
        return Optional.ofNullable(null);
    }

    public Map<String, String> hgetAll(String key){
        return  hgetAll(key,-1);
    }
    public Map<String, String> hgetAll(String key,int expiretime) {
        String result = "";
        RedisConnection jedis1 = getJedis();
        try {
            Jedis  jedis = (Jedis) jedis1.getNativeConnection();
            Map<String, String> stringStringMap = jedis.hgetAll(key);
            UpExpiretime(key, expiretime,  Optional.ofNullable(stringStringMap), jedis);
            return stringStringMap;
        } catch (Exception e) {
            log.error("hget获取redis键值异常:key=" + key   + " cause:" + e);
        } finally {
            returnResource(jedis1);
        }
        return Collections.EMPTY_MAP;
    }

    public Long sAdd(String key,int expiretime,Object... arg) {
        int  arglen=arg.length;
        Set<Object> collect = Stream.of(arg).filter(Objects::nonNull).collect(Collectors.toSet());
        long count =collect.size();
        if(arglen!=count) throw  new RuntimeException(" redis list  增加的数组有null值");

        Long result = -1L;
        RedisConnection jedis1 = getJedis();
        try {
            Jedis  jedis = (Jedis) jedis1.getNativeConnection();
            result = jedis.sadd(key,collect.stream().map(Object::toString).toArray(String[]::new));
            UpExpiretime(key, expiretime,  Optional.ofNullable(result), jedis);
            log.error("sadd设置redis键值成功:key=" + key + " value=" + arg);
            return result;
        } catch (Exception e) {
            log.error("sadd设置redis键值异常:key=" + key  +  " value=" + arg + " cause:" + e);
        } finally {
            returnResource(jedis1);
        }
        return result;
    }

    public Set<String> setGet (String key, int expiretime) {
        return smembers(key,expiretime);
    }

    public Set<String> smembers (String key, int expiretime) {
        RedisConnection jedis1 = getJedis();
        try {
            Jedis  jedis = (Jedis) jedis1.getNativeConnection();
            Long llen = jedis.scard(key);
            if(llen==0) throw  new RuntimeException(key +"值为null");
            Set<String> lrange1 = jedis.smembers(key);
            UpExpiretime(key, expiretime, Optional.ofNullable(lrange1), jedis);
            return lrange1;
        } catch (Exception e) {
            e.fillInStackTrace();
            log.error(String.format("smembers  获取redis键值异常:key=%s cause:%s", key, e));
        } finally {
            returnResource(jedis1);
        }
        return Collections.EMPTY_SET;
    }
    /**
     * 删除key
     */
    public Long delkeyObject(String key) {
        RedisConnection jedis1 = getJedis();
        try {
            Jedis  jedis = (Jedis) jedis1.getNativeConnection();
            return jedis.del(key);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }finally{
            returnResource(jedis1);
        }
    }
    /**
     * 删除key
     */
    public void expire(String key,int expiretime) {
        RedisConnection jedis1 = getJedis();
        try {
            Jedis  jedis = (Jedis) jedis1.getNativeConnection();
            jedis.expire(key, expiretime*1000);
        }catch(Exception e) {
            e.printStackTrace();
            return ;
        }finally{
            returnResource(jedis1);
        }
    }

    public Boolean existsObject(String key) {
        RedisConnection jedis1 = getJedis();
        try {
            Jedis  jedis = (Jedis) jedis1.getNativeConnection();
            return jedis.exists(key);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }finally{
            returnResource(jedis1);
        }
    }

    public Set<String> keys (String key, int expiretime) {
        RedisConnection jedis1 = getJedis();
        try {
            Jedis  jedis = (Jedis) jedis1.getNativeConnection();
            Set<String> keys = jedis.keys(key);
            if(keys.size()==0) throw  new RuntimeException(key +"值为null");
            UpExpiretime(key, expiretime, Optional.ofNullable(keys), jedis);
            return keys;
        } catch (Exception e) {
            e.fillInStackTrace();
            log.error(String.format("keys  获取redis键值异常:key=%s cause:%s", key, e));
        } finally {
            returnResource(jedis1);
        }
        return Collections.EMPTY_SET;
    }
}
