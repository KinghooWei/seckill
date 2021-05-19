// 主要的交互逻辑代码
// JavaScript 模块化
var seckill = {
    //封装秒杀相关ajax的URL
    URL: {
        now: function () {
            return "/seckill//time/now"
        },
        exposer: function (seckillId) {
            return "/seckill/" + seckillId + "/exposer";
        },
        execution : function (seckillId, md5) {
            return "/seckill" + seckillId + "/" + md5 + "execution";
        }
    },
    //手机验证
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },
    handlerSeckill: function (seckillId, node) {
        node.hide().html('<button id="killBtn" class="btn btn-primary btn-lg">立即抢购</button>');
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    //开启秒杀
                    //获取秒杀地址
                    var md5 = exposer['md5'];
                    var killURL = seckill.URL.execution(seckillId, md5);
                    //注册秒杀按钮一次点击事件
                    $('#killBtn').one('click', function () {
                        //禁用按钮
                        $(this).addClass("disable");
                        //发送秒杀请求
                        $.post(killURL, {}, function (result) {
                            if (result && result['success']) {
                                var killReault = result['data'];
                                var state = killReault['state'];
                                var stateInfo =  killReault['stateInfo'];
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            } else {
                                console.log("result:"+result);
                            }
                        })
                    });
                } else {
                    //未开启秒杀，重新计时
                    var nowTime = exposer['now'];
                    var startTime = exposer['start'];
                    var endTime = exposer['end'];
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                }
            } else {
                console.log("result:" + result);
            }
        });
        node.show();
    },
    //时间判断
    countDown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        if (nowTime > endTime) {
            seckillBox.html('秒杀结束！');
        } else if (nowTime < startTime) {   //秒杀未开启
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {
                var format = event.strftime('秒杀倒计时：d%天 H%时 m%分 s%秒');
                seckillBox.html(format);
            }).on('finish.countdown', function () { //倒计时结束，获取秒杀地址，注册秒杀点击事件
                seckill.handlerSeckill(seckillId, seckillBox);
            });
        } else {    //正在进行秒杀，获取秒杀地址，注册秒杀点击事件
            seckill.handlerSeckill(seckillId, seckillBox);
        }
    },
    //详情页秒杀逻辑
    detail: {
        //详情页初始化
        init: function (params) {
            //手机验证和登录，计时交互
            //规划交互流程
            //在cookie中查找手机号
            var killPhone = $.cookie('killPhone');
            //验证手机号
            if (!seckill.validatePhone(killPhone)) {
                //绑定电话
                //弹窗
                var keyPhoneModal = $('#keyPhoneModal');
                keyPhoneModal.modal({
                    show: true,            //显示弹窗
                    backdrop: 'static',    //禁止位置关闭
                    keyboard: false        //关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#keyPhoneKey').val();
                    if (seckill.validatePhone(inputPhone)) {
                        //电话写入cookie
                        $.cookie("killPhone", inputPhone, {expires: 7, path: "/seckill"});
                        //刷新
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号不正确</label>').show(300);
                    }
                });
            }
            //计时交互
            var seckillId = params['seckillId'];
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log("result:", result);
                }
            })
        }
    }
}