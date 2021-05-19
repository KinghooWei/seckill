package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

/**
 * Create by Jinhu Wei on 2020/12/17
 */
public interface SuccessKilledDao {

    // 插入秒杀记录，返回插入行数，可防止重复秒杀
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    // 根据id查询成功秒杀记录，并携带秒杀对象
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
}
