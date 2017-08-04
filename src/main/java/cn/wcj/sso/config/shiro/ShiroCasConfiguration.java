package cn.wcj.sso.config.shiro;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.cas.CasSubjectFactory;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import cn.wcj.sso.config.redis.RedisConfig;

/**
 * 
 * <p>Module:ShiroCasConfiguration </p>
 * <p>Description: Shiro集成CAS配置</p>
 * <p>Company:Software College Of ZhengZhou University </p> 
 * @author SuccessKey(WangCJ)
 * @date 2017年8月4日 下午9:10:26
 */
@Configuration
@EnableConfigurationProperties(CasConfig.class)
@AutoConfigureAfter(RedisConfig.class)  //在Redis配置完成后加载
public class ShiroCasConfiguration {
	
	
	private static final String CAS_FILTER = "casFilter";
	
	@Bean
	public JavaUuidSessionIdGenerator sessionIdGenerator(){  //随机生成SesssionID
		  return new JavaUuidSessionIdGenerator()  ;
	}
	
	
	
	@Bean
	public RedisSessionDAO  sessionDAO(){
		 RedisSessionDAO redisSessionDAO=new RedisSessionDAO()  ;
		 redisSessionDAO.setActiveSessionsCacheName("shiro-activeSessionsCache");
		 redisSessionDAO.setSessionIdGenerator(this.sessionIdGenerator()) ;
		 return redisSessionDAO   ;
	}
	
	@Bean
	public DefaultSessionManager sessionManager(){
		DefaultSessionManager sessionManager=new DefaultSessionManager()  ;
		sessionManager.setGlobalSessionTimeout(1800000) ;
		sessionManager.setSessionValidationSchedulerEnabled(true) ;
		sessionManager.setSessionDAO(this.sessionDAO());
		sessionManager.setDeleteInvalidSessions(true) ;
		return  sessionManager  ;
	}
   	
	
	@Bean(name = "shiroCasRealm")
	public ShiroCasRealm shiroRealm(CasConfig casConfig){
		ShiroCasRealm realm = new ShiroCasRealm();
		realm.setCasServerUrlPrefix(casConfig.getCasServerUrlPrefix());
		realm.setCasService(casConfig.getCasService());
		return realm;
	}

	/**
	 * 注册shiroFilter
	 */
	@Bean
	public FilterRegistrationBean filterRegistrationBean(){
		FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
		filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
		// 该值缺省为false，表示生命周期有SpringApplicationContext管理，设置为true则表示由ServletContainer管理
		filterRegistration.addInitParameter("targetFilterLifecycle", "true");
		filterRegistration.setEnabled(true);
		filterRegistration.addUrlPatterns("/*");
		return filterRegistration;
	}

	@Bean(name = "lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
		DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
		creator.setProxyTargetClass(true);
		return creator;
	}

	@Bean(name = "securityManager")
	public DefaultWebSecurityManager defaultWebSecurityManager(ShiroCasRealm realm){
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(realm);
		// 指定SubjectFactory
		securityManager.setSubjectFactory(new CasSubjectFactory());
		securityManager.setSessionManager(this.sessionManager());
		return securityManager;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager){
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}

	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager, CasConfig casConfig, CasFilter casFilter){
		ShiroFilterFactoryBean factoryBean = new MyShiroFilterFactoryBean();
		factoryBean.setSecurityManager(securityManager);
		factoryBean.setLoginUrl(casConfig.getLocalServerLoginUrl());
		factoryBean.setSuccessUrl("/user");
		factoryBean.setUnauthorizedUrl("/403");
		// 添加casFilter到shiroFilter中
		Map<String, Filter> filterMap = new HashMap<String, Filter>(1);
		filterMap.put(CAS_FILTER, casFilter);
		factoryBean.setFilters(filterMap);

		loadShiroFilterChain(factoryBean, casConfig);
		return factoryBean;
	}

	/**
	 * 加载ShiroFilter权限控制规则
	 */
	private void loadShiroFilterChain(ShiroFilterFactoryBean factoryBean, CasConfig casConfig) {
		/**下面这些规则配置最好配置到配置文件中*/
		Map<String, String> filterChainMap = new LinkedHashMap<String, String>();
		filterChainMap.put(casConfig.getCasFilterUrlPattern(), CAS_FILTER);//shiro集成cas后，首先添加该规则
		filterChainMap.put("/user", "authc");
		filterChainMap.put("/user/edit/**", "authc,perms[user:edit]");
		filterChainMap.put("/**", "anon");
		factoryBean.setFilterChainDefinitionMap(filterChainMap);
	}

	/**
	 * CAS过滤器
	 */
	@Bean
	public CasFilter casFilter(CasConfig casConfig){
		CasFilter casFilter = new CasFilter();
		casFilter.setName(CAS_FILTER);
		casFilter.setEnabled(true);
		casFilter.setFailureUrl(casConfig.getLocalServerLoginUrl());
		return casFilter;
	}

}
