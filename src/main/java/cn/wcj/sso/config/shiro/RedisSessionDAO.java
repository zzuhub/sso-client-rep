package cn.wcj.sso.config.shiro;

import java.io.Serializable;


import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
/**
 * 
 * <p>Module:MySessionDao </p>
 * <p>Description: 自定义SessionDAO</p>
 * <p>Company:Software College Of ZhengZhou University </p> 
 * @author SuccessKey(WangCJ)
 * @date 2017年8月4日 下午10:12:50
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class RedisSessionDAO extends EnterpriseCacheSessionDAO {

	@Autowired
	private RedisTemplate redisTemplate   ;

	@Override
	public Serializable doCreate(Session session) {     //存Session
		Serializable sessionId = super.generateSessionId(session) ; //生成sessionId
		super.assignSessionId(session, sessionId) ;
		redisTemplate.opsForValue().set(sessionId.toString(), 
				                        SerializableUtils.serialize(session).toString());
		return session.getId()  ;
	}

	@Override
	public Session doReadSession(Serializable sessionId) {   //读取Session
		Object object = redisTemplate.opsForValue().get(sessionId)  ;
		if(object==null) return null ;
		return SerializableUtils.deserialize(object.toString());
	}
	
	@Override
	public void doUpdate(Session session) {   //更新Session
		if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) return; 
		redisTemplate.getConnectionFactory().getConnection().del(session.getId().toString().getBytes()) ; //删除原有 
		redisTemplate.opsForValue().set(session.getId().toString(), SerializableUtils.serialize(session).toString());
	}

	@Override
	public void doDelete(Session session) {  //删除Session
		redisTemplate.opsForSet().remove(session.getId().toString(), SerializableUtils.serialize(session).toString())   ;
	}
}
