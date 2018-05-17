package com.pz.auth.common.enums;

import com.pz.auth.common.Snowflake.SnowflakeIdWorker;
import com.pz.auth.common.Snowflake.Snowflakes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pz
 * @version 2.3
 * @E-mail 2919274153@qq.com
 * @date 2018-5-17 15:01:17
 */
public enum Jurisdiction {

    SAVE("save","增加"),UPDATE("update","修改"),DELETE("delete","删除"),LISR("list","集合"),SELECT("select","查询");
    static SnowflakeIdWorker idWorker=Snowflakes.of();

    private String jurName;

    /**
     * 西恩刀塔  CN 中国
     */
    private String CnName;

    Jurisdiction(String jurName, String cnName) {
        this.jurName = jurName;
        CnName = cnName;
    }

    public String getJurName() {
        return jurName;
    }

    public void setJurName(String jurName) {
        this.jurName = jurName;
    }

    public String getCnName() {
        return CnName;
    }

    public void setCnName(String cnName) {
        CnName = cnName;
    }

    public static List<String> GenJurNames(String  modelName, Jurisdiction... jur){
      return   Arrays.asList(jur).stream().map(Jurisdiction::getJurName).map(jurName -> modelName+":"+jurName).collect(Collectors.toList());
    }
    public static String GenJurNames(String  modelName, Jurisdiction jur){
        return    modelName+":"+jur.getJurName();
    }
    public static List<String> GenCnNames(String  modelName, Jurisdiction... jur){
        return   Arrays.asList(jur).stream().map(Jurisdiction::getCnName).map(cnName -> modelName+":"+cnName).collect(Collectors.toList());
    }
    public static String GenCnNames(String  modelName, Jurisdiction jur){
        return    modelName+":"+jur.getCnName();
    }

    public static Long GenUUID(){
        return idWorker.nextId();
    }
}
