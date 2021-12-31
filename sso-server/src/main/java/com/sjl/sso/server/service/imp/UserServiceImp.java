package com.sjl.sso.server.service.imp;

import com.alibaba.fastjson.JSON;
import com.sjl.sso.core.constant.SsoConstant;
import com.sjl.sso.core.to.UserTo;
import com.sjl.sso.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void saveUser(String username, String password) {
        UserTo userTo = new UserTo();
        userTo.setUsername(username);
        userTo.setPassword(password);
        String key = SsoConstant.REDIS_USER_PREFIX+username;
        redisTemplate.opsForValue().set(key, JSON.toJSONString(userTo));
    }
}
