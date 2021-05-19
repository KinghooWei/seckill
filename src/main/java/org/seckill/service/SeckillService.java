package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatSeckillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * 业务接口:站在"使用者"角度设计接口
 * 三个方面:方法定义粒度,参数,返回类型(return类型/异常)
 * Create by Jinhu Wei on 2021/1/3
 */
public interface SeckillService {
    // 获取所有秒杀对象
    List<Seckill> getSeckillList();

    // 获取指定id的对象
    Seckill getById(long seckillId);

    // 秒杀开启时输出秒杀接口地址,
    // 否则输出系统时间和秒杀时间
    Exposer exportSeckillUrl(long seckillId);

    // 执行秒杀操作
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatSeckillException, SeckillCloseException;

    // 执行秒杀操作
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);
}
