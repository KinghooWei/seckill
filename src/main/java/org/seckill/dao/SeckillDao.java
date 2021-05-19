package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Create by Jinhu Wei on 2020/12/17
 */
public interface SeckillDao {

    // 减库存
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    // 根据id查询秒杀对象
    Seckill queryById(long seckillId);

    // 根据偏移量查询秒杀对象
    // Java没有保存形参的记录，借助mybatis注解@Param
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    //使用存储过程秒杀
    void killByProcedure(Map<String, Object> paramMap);
}
