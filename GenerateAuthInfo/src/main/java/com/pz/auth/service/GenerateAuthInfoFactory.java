package com.pz.auth.service;


import java.util.Optional;

/**
 * @author pz
 * @version 2.3
 * @E-mail 2919274153@qq.com
 * @date 2018-5-17 14:59:19
 */
public class GenerateAuthInfoFactory {


    public static GenerateAuthInfo  getGenerateAuthInfo(){
           return getGenerateAuthInfo(null);
    }

    public static GenerateAuthInfo  getGenerateAuthInfo(GenRedis genRedis){
        Optional<GenRedis> genRedis1 = Optional.ofNullable(genRedis);
        if(genRedis1.isPresent()){
          return new GenerateAuthInfo(genRedis) {
              @Override
              public void run(Class<?> primarySource) {
                      init();
                      runCore(primarySource);
                      after();
              }
          };
        }else{
           return new GenerateAuthInfo() {
               @Override
               public void run(Class<?> primarySource) {
                   noInit();
                   runCore(primarySource);
                   noAfter();
               }
           };
        }
    }

}
