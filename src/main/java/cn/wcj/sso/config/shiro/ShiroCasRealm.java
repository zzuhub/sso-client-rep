package cn.wcj.sso.config.shiro;

import java.util.Set;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import cn.wcj.sso.pojo.po.TbUser;
import cn.wcj.sso.service.IUserService;

/**
 * Created by Administrator on 2016/12/5 0005.
 */
public class ShiroCasRealm extends CasRealm{
	
	
	@Autowired
	private IUserService userService;



	/**
	 * 由于使用了SSO单点登录系统，此Ream只负责授权
	 * 权限认证（为当前登录的Subject授予角色和权限）
	 * 
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		String userName = (String) super.getAvailablePrincipal(principals) ; //获取用户名 
		TbUser user=null ;
		SimpleAuthorizationInfo info=null ;
		try {
			user = userService.findUserByUserName(userName);
		} catch (Exception e) {}
		if (user != null){
			// 权限信息对象info，用来存放查出的用户的所有的角色及权限
			info = new SimpleAuthorizationInfo();
			try{
				Set<String> roles = userService.findRoleNamesByUserName(userName);   //根据用户名查询角色
				info.setRoles(roles);
				Set<String> permissions= userService.findPermissionNamesByUserName(userName); //根据用户名查询权限    
				info.setStringPermissions(permissions);
			}catch(Exception e){
				info=null ;
			}
		}
		return info;
	}
}
