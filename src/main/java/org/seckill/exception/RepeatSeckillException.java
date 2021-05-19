package org.seckill.exception;

import org.seckill.dto.SeckillExecution;

/**
 * 重复秒杀异常
 * Create by Jinhu Wei on 2021/1/3
 */
public class RepeatSeckillException extends SeckillException {
    public RepeatSeckillException(String message) {
        super(message);
    }

    public RepeatSeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
