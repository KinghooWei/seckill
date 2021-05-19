package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatSeckillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Create by Jinhu Wei on 2021/1/5
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/spring/spring-dao.xml", "classpath:/spring/spring-service.xml"})
public class SeckillServiceTest {

    @Autowired
    SeckillService seckillService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void getSeckillList() {
        List<Seckill> seckillList = seckillService.getSeckillList();
        logger.info("seckill list = {}", seckillList);
    }

    @Test
    public void getById() {
        long seckillId = 1000L;
        Seckill seckill = seckillService.getById(seckillId);
        logger.info("seckill = {}", seckill);
    }

    @Test
    public void exportSeckillUrl() {
        long seckillId = 1000L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        logger.info("exposer = {}", exposer);
    }

    @Test
    public void executeSeckill() {
        long seckillId = 1000L;
        String md5 = "0a1527b0153392182fa49aff6def08c7";
        long phoneNum = 13600000001L;
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, phoneNum, md5);
            logger.info("seckill execution = {}", seckillExecution);
        } catch (SeckillCloseException e) {
            logger.warn("SeckillCloseException");
        } catch (RepeatSeckillException e) {
            logger.warn("RepeatSeckillException");
        }
    }

    public void seckillLogic() {
        long seckillId = 1000L;
        long phoneNum = 13600000001L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {
            String md5 = exposer.getMd5();
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, phoneNum, md5);
                logger.info("seckill execution = {}", seckillExecution);
            } catch (SeckillCloseException e) {
                logger.warn("SeckillCloseException");
            } catch (RepeatSeckillException e) {
                logger.warn("RepeatSeckillException");
            }
        } else {
            logger.warn("秒杀未开启");
        }
    }

    @Test
    public void executeSeckillProcedure() {
        long seckillId = 1001;
        long phone = 13600000002L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {
            String md5 = exposer.getMd5();
            SeckillExecution seckillExecution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
            logger.info(seckillExecution.getStateInfo());
        }
    }
}