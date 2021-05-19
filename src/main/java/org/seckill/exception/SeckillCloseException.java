package org.seckill.exception;

import org.seckill.dto.SeckillExecution;

/**
 * Create by Jinhu Wei on 2021/1/3
 */
public class SeckillCloseException extends SeckillException {
    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
