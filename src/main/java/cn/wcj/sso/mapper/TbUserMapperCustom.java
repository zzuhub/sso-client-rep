package cn.wcj.sso.mapper;

import java.util.Set;

public interface TbUserMapperCustom {

     Set<String> findRoleNamesByUserName(String userName)throws Exception  ;
	
	Set<String> findPermissionNamesByUserName(String userName)throws Exception ;
	
}
