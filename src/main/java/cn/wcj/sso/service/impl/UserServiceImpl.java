package cn.wcj.sso.service.impl;


import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.wcj.sso.mapper.TbUserMapper;
import cn.wcj.sso.mapper.TbUserMapperCustom;
import cn.wcj.sso.pojo.po.TbUser;
import cn.wcj.sso.pojo.po.TbUserExample;
import cn.wcj.sso.pojo.po.TbUserExample.Criteria;
import cn.wcj.sso.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private TbUserMapper tbUserMapper   ;   //原生User的Mapper
	
	@Autowired
	private TbUserMapperCustom tbUserMapperCustom  ;   //自定义User的Mapper
	
	
	@Override
	public TbUser findUserByUserName(String userName) throws Exception {
		TbUserExample example=new TbUserExample()   ;  //查询模板
		Criteria criteria = example.createCriteria();  //编写查询条件
		criteria.andUsernameEqualTo(userName)   ;  //根据用户名称查询
		List<TbUser> tbUsers=tbUserMapper.selectByExample(example);
		return tbUsers==null || tbUsers.size()==0 ? null : tbUsers.get(0) ;
	}

	@Override
	public Set<String> findRoleNamesByUserName(String userName)
			                                      throws Exception {
		Set<String> roleNames = tbUserMapperCustom.findRoleNamesByUserName(userName);
		return roleNames  ;
	}

	@Override
	public Set<String> findPermissionNamesByUserName(String userName)
			                                            throws Exception {
		Set<String> permissionNames = tbUserMapperCustom.findPermissionNamesByUserName(userName);
		return permissionNames  ;
	}

	@Override
	public List<TbUser> findAll() throws Exception {
		return tbUserMapper.selectByExample(null) ;
	}


}
