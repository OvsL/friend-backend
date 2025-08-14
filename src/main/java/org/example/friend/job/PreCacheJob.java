package org.example.friend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.friend.constant.UserConstant;
import org.example.friend.mapper.UserMapper;
import org.example.friend.model.domain.User;
import org.example.friend.service.UserService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 缓存预热任务
 *
 * @author Qi
 * @since 2025-06-26
 */
@Slf4j
@Component
@EnableScheduling
public class PreCacheJob {

    @Autowired
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    /**
     * 基础用户id
     */
    private final List<Long> mainUserList = List.of(1L);
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;


    @Scheduled(cron = "0 0 0 * * ?")
    public void preCacheJob() {
        RLock lock = redissonClient.getLock(UserConstant.REDISSON_NAME + ":precachejob:docache:lock");
        try {
            //加锁
            if(lock.tryLock(0, 30000, TimeUnit.MICROSECONDS) ){
                for (Long mainUserId : mainUserList) {
                    String redisKey = String.format("%s:recommend:%s", UserConstant.REDISSON_NAME, mainUserId);
                    ValueOperations<String, Object> objectValueOperations = redisTemplate.opsForValue();
                    //缓存不存在
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    IPage<User> page = userService.page(new Page<>(1, 10), queryWrapper);
                    List<User> collect = page.getRecords().stream().peek(user -> user.setUserPassword(null)).collect(Collectors.toList());
                    //缓存数据
                    try {
                        objectValueOperations.set(redisKey, collect, 1, TimeUnit.MINUTES);
                    } catch (Exception e) {
                        log.error("redis set error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("redis lock error", e);
            throw new RuntimeException(e);
        } finally {
            //只能释放自己的锁
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }


    }
}
