package org.seckill.exception;

/**
 * 秒杀相关业务异常
 * Create by Jinhu Wei on 2021/1/3
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
