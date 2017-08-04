package cn.wcj.sso.service;

import java.util.List;
import java.util.Set;

import cn.wcj.sso.pojo.po.TbUser;


/**
 * 
 * <p>Module:IUserService </p>
 * <p>Description:用户服务接口 </p>
 * <p>Company:Software College Of ZhengZhou University </p> 
 * @author SuccessKey(WangCJ)
 * @date 2017年8月4日 下午8:38:54
 */
public interface IUserService {

	TbUser findUserByUserName(String userName)throws Exception   ;
	
	Set<String> findRoleNamesByUserName(String userName)throws Exception  ;
	
	Set<String> findPermissionNamesByUserName(String userName)throws Exception ;
	
	List<TbUser> findAll()throws Exception   ;
	
}
