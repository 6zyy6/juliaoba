package com.yupi.yupao.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupao.mapper.UserMapper;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    // 重点用户
    private List<Long> mainUserList = Arrays.asList(1L);
    
    // 常用标签组合
    private List<List<String>> hotTagCombinations = Arrays.asList(
            Arrays.asList("Java", "Python"),
            Arrays.asList("Java", "Spring"),
            Arrays.asList("Python", "数据分析"),
            Arrays.asList("前端", "Vue"),
            Arrays.asList("游戏", "音乐"),
            Arrays.asList("健身", "旅游")
    );

    // 每天执行，预热推荐用户
    @Scheduled(cron = "0 31 0 * * *")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("yupao:precachejob:docache:lock");
        try {
            // 只有一个线程能获取到锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                System.out.println("getLock: " + Thread.currentThread().getId());
                for (Long userId : mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("yupao:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    // 写缓存
                    try {
                        valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
    
    /**
     * 每6小时执行一次，预热热门标签搜索
     */
    @Scheduled(cron = "0 0 */6 * * *")
    public void doCacheTagsSearch() {
        RLock lock = redissonClient.getLock("yupao:precachejob:dotagscache:lock");
        try {
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                log.info("开始预热标签搜索缓存");
                for (List<String> tags : hotTagCombinations) {
                    try {
                        List<User> userList = userService.searchUsersByTags(tags);
                        log.info("缓存标签组合: {}, 结果数: {}", tags, userList.size());
                    } catch (Exception e) {
                        log.error("缓存标签搜索异常", e);
                    }
                }
                log.info("标签搜索缓存预热完成");
            }
        } catch (InterruptedException e) {
            log.error("doCacheTagsSearch error", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
