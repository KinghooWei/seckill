package org.seckill.entity;

import java.util.Date;

/**
 * Create by Jinhu Wei on 2020/12/17
 */
public class SuccessKilled {

    private long SeckillId;

    private long userPhone;

    private short state;

    private Date createTime;

    // 多个成功秒杀记录对应一个秒杀商品
    private Seckill seckill;

    public long getSeckillId() {
        return SeckillId;
    }

    public void setSeckillId(long seckillId) {
        SeckillId = seckillId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Seckill getSeckill() {
        return seckill;
    }

    public void setSeckill(Seckill seckill) {
        this.seckill = seckill;
    }

    @Override
    public String toString() {
        return "SuccessKilled{" +
                "SeckillId=" + SeckillId +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                '}';
    }
}
