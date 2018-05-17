package com.pz.auth.common.Snowflake;

public class Snowflakes {

   public static SnowflakeIdWorker of(){
        return  new SnowflakeIdWorker();
    }

    public static SnowflakeIdWorker of(long workerId, long datacenterId){
        return  new SnowflakeIdWorker(workerId,datacenterId);
    }
}
