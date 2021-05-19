package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatSeckillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by Jinhu Wei on 2021/1/3
 */
@Service    //@Conponent @Controller @Dao @Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired  //注入service依赖
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    // md5盐值字符串,用于混淆MD5
    private final String slat = "sjnoirjhto34r2ih2nbdvnsl53k4n^TF&*^&&%f#rfvjhb";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    // 暴露秒杀地址，否则返回秒杀时间
    public Exposer exportSeckillUrl(long seckillId) {
        // redis缓存优化
        // 1：访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            // 2：访问数据库
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                //3：放入redis
                redisDao.putSeckill(seckill);
            }
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        // 转换特定字符串，不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true, seckillId, md5);
    }

    /**
     * 使用注解控制事务方法的优点:
     * 1:开发团队达成一致约定,明确标注事务方法的编程风格.
     * 2:保证事务方法的执行时间尽可能短,不要穿插其他网络操作RPC/HTTP请求或者剥离到事务方法外部.
     * 3:不是所有的方法都需要事务,如只有一条修改操作,只读操作不需要事务控制.
     */
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatSeckillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("秒杀数据被篡改");
        } else {
            // 执行秒杀逻辑:减库存+记录购买行为
            try {
                // 记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if (insertCount <= 0) {
                    throw new RepeatSeckillException("重复秒杀");
                } else {
                    // 减库存，获取行级锁，热点商品竞争。先记录后减库存，减少行级锁持有时间
                    int updateCount = seckillDao.reduceNumber(seckillId, new Date());
                    if (updateCount <= 0) {
                        // 秒杀结束，不更新记录，rollback
                        throw new SeckillCloseException("秒杀结束");
                    } else {
                        // commit
                        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                        return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                    }
                }

            } catch (SeckillCloseException e1) {
                throw e1;
            } catch (RepeatSeckillException e2) {
                throw e2;
            } catch (Exception e3) {
                logger.error(e3.getMessage(), e3);
                throw new SeckillException("seckill inner error:" + e3.getMessage());
            }

        }
    }

    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        try {
            seckillDao.killByProcedure(map);
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, sk);
            } else {
                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
}
