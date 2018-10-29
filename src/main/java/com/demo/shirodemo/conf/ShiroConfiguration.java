package com.demo.shirodemo.conf;

import com.demo.shirodemo.shiro.CredentialsMatcher;
import com.demo.shirodemo.shiro.Realm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
public class ShiroConfiguration {

    //配置shiro的几大功能模块

    /**
     * shiroFilterFactoryBean 注入 SecurityManage(其中设置了Realm(其中设置了CredentialsMatcher))
     */


    //缓存
    //之后使用redis做shiro缓存
//    public CacheManager cacheManager(RedisConnectionFactory factory){
//        RedisCacheManager cacheManager = new RedisCacheManager();
//        log.error("shiro实例redis缓存");
//        return cacheManager;
//
//    }


    //资源拦截
    //url过滤
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager);
        //bean 中还可设置url拦截 用线程安全的map
        //使用ConcurrentHashMap 读操作>写操作时
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.put("/login", "anon");
        map.put("/root", "roles[root]");
        map.put("/admin", "roles[admin]");
        map.put("/finance", "roles[finance]");
        map.put("/all", "anon");
        map.put("/index", "authc");
        bean.setFilterChainDefinitionMap(map);
        bean.setLoginUrl("/login");
        return bean;
    }


    @Bean
    public SecurityManager securityManager(Realm realm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setCacheManager(new MemoryConstrainedCacheManager());
        //session管理通过dao
        //securityManager.setSessionManager(sessionManager);
        return securityManager;
    }


    @Bean
    public Realm realm(CredentialsMatcher matcher) {
        Realm realm = new Realm();
        realm.setCredentialsMatcher(matcher);
        return realm;
    }

    @Bean
    public CredentialsMatcher matcher() {
        return new CredentialsMatcher();
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();


    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    //开启注解@RequireRole permission
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }


    /**
     * 配置shiro redisManager
     * 网上的一个 shiro-redis 插件，实现了shiro的cache接口、CacheManager接口就
     *
     * @return
     */


    /**
     * cacheManager 缓存 redis实现
     * 网上的一个 shiro-redis 插件
     *
     * @return
     */

//    @Bean
//    public JedisConnectionFactory connectionFactory() throws IOException {
//        // 配置连接信息
//        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
//        standaloneConfig.setHostName("routany.cn");
//        standaloneConfig.setPort(6379);
//        String pass = "clay123456";
//        if(pass != null && !pass.equals(""))
//            standaloneConfig.setPassword(RedisPassword.of(pass));
//        else
//            standaloneConfig.setPassword(RedisPassword.none());
//
//        JedisClientConfiguration.DefaultJedisClientConfigurationBuilder builder = (JedisClientConfiguration.DefaultJedisClientConfigurationBuilder) JedisClientConfiguration.builder();
//        builder.usePooling();
//        JedisClientConfiguration clientConfig = builder.build();
//
//        return new JedisConnectionFactory(standaloneConfig, clientConfig);
//    }


//    @Bean
//    public CacheManager cacheManager(JedisConnectionFactory connectionFactory) throws IOException{
//        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofHours(1)); // 设置缓存有效期一小时
//        return RedisCacheManager
//                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
//                .cacheDefaults(redisCacheConfiguration).build();
//    }


    /**
     * RedisSessionDAO shiro sessionDao层的实现 通过redis
     */


    /**
     * shiro session的管理
     */

}