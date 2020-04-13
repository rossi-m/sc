package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodeUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import sun.nio.cs.US_ASCII;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String key_prefix="user:verify:phone:";

    public Boolean checkData(String data, Integer type) {
        User record=new User();
        if (type == 1) {
            record.setUsername(data);
        } else if (type == 2) {
            record.setPhone(data);
        } else {
            return null;
        }
        return this.userMapper.selectCount(record) == 0;
    }

    //发送短信
    public void sendCode(String phone) {
        //生成key
        String key =key_prefix+phone;
        //生成指定位数的随机数字
        String code = NumberUtils.generateCode(6);
        Map<String,String> msg=new HashMap<>();
        msg.put("phone","15707943954");
        msg.put("code",code);

        //发送验证码
        amqpTemplate.convertAndSend("ly.sms.exchange","sms.verify.code",msg);

        //保存验证码到redis数据库只能保存5分钟，5分钟后删除
        redisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);

    }


    public void register(User user, String code) {

        //从redis中取出验证码
        String cacheCode = redisTemplate.opsForValue().get(key_prefix + user.getPhone());
        //校验验证码
        if (!StringUtils.equals(code,cacheCode)){
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        //生成盐
        String salt=CodeUtils.generateSalt();
        user.setSalt(salt);
        // 对密码进行加密
        user.setPassword(CodeUtils.md5Hex(user.getPassword(),salt));
        //写入数据库
        user.setCreated(new Date());
        userMapper.insert(user);
    }

    public User queryUserByUsernameAndPassword(String username, String password) {
        //查询用户
        User record=new User();
        record.setUsername(username);
        User user = userMapper.selectOne(record);
        //校验
        if (user ==null){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }

        //校验密码
       if (!StringUtils.equals(user.getPassword(),CodeUtils.md5Hex(password,user.getSalt()))){
           throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);

       }
       //用户名和密码正确
        return user;
    }
}
