package com.pz.auth.common;

import com.pz.auth.functionalInterfaces.MygetRoles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiInterface implements Serializable {
   // 方法名字
    private String methodName;
    //  方法的描述
//    private String description;
    // 方法的类别
    private String methodType;
     // 方法的地址
//    private String requestURI;
    // 角色信息
    private Map<String, String> roleInfo=new HashMap<>();

    //   权限信息
    private  Map<String,  Map<String, String>> authInfo=new HashMap<>();


    public Stream<Map<String, String>> getsRole(){
        return Stream.of(roleInfo);
    }
    public Stream< Map<String,  Map<String, String>>> getsAuth(){
        return Stream.of(authInfo);
    }
    public void setsAuthInfo(MygetRoles sRole){
        authInfo.putAll(sRole.roleOrAuth());
    }
    public void setsRoleInfo(MygetRoles sRole){
        roleInfo.putAll(sRole.roleOrAuth());
    }

    public boolean isInfoEquals(){
//        if(authInfo.length!=authName.length){ log.error("authInfo {} != authName {}   ",authInfo.length,authName.length); return  false;}
//        if(roleInfo.length!=roleName.length){ log.error("authInfo {} != authName {}   ",authInfo.length,authName.length); return  false;}
        return  true;
    }


}

