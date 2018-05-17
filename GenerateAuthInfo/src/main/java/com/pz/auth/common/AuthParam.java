package com.pz.auth.common;



import java.io.Serializable;

public class AuthParam {

    /**  root user */
    public  static final String ROOT="root";

    
    /** redis 存储role 的key */
    public  static final String ROLES="roles";
     
    /** redis 存储auth 的key */
    public  static final String AUTHS="auths";

    /** redis 存储 roles and auth 的key */
    public  static final String AUTHSROLES="authsRoels_";

    /** 工作机器ID(0~31) */
    public static final long WORKERID=0;

    /** 数据中心ID(0~31) */
    public static final long DATACENTERID=0;


}


