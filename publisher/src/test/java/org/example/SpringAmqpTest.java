package org.example;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Correlation;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest
public class SpringAmqpTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSimpleQueue() {
        // 队列名称
        String queueName = "simple.queue";
        // 消息
        String message = "hello, spring amqp!";
        // 发送消息
        rabbitTemplate.convertAndSend(queueName, message);
    }
//
//    /**
//     * workQueue
//     * 向队列中不停发送消息，模拟消息堆积。
//     */
//    @Test
//    public void testWorkQueue() throws InterruptedException{
//
//        //队列名称
//        String queueName = "work.queue";
//
//        //消息
//        String message = "hello, message_";
//
//        for (int i = 0; i < 50; i++) {
//            //发送消息 每20毫秒发送一次 相当于每秒发送50条
//            rabbitTemplate.convertAndSend(queueName, message + i);
//            Thread.sleep(20);
//        }
//    }


//    @Test
//    public void testFanoutExchange() throws InterruptedException{
//
//        //交换机名称
//        String exchangeName = "hmall.fanout";
//        //发送的消息
//        String message = "hello, everyone!";
//
//        rabbitTemplate.convertAndSend(exchangeName,"",message);
//
//    }

//    @Test
//    public void testSendDirectExchange() {
//        // 交换机名称
//        String exchangeName = "hmall.direct";
//        // 消息
//        String message = "红色警报！日本乱排核废水，导致海洋生物变异，惊现哥斯拉！";
//        // 发送消息
//        rabbitTemplate.convertAndSend(exchangeName, "blue", message);
//    }

    /**
     * topicExchange
     */
//    @Test
//    public void testSendTopicExchange() {
//        // 交换机名称
//        String exchangeName = "hmall.topic";
//        // 消息
//        String message = "喜报！孙悟空大战哥斯拉，胜!";
//        // 发送消息
//        rabbitTemplate.convertAndSend(exchangeName, "china.news", message);
//    }

//    @Test
//    public void testSendTopicExchange(){
//        //交换机的名称
//        String exchangeName = "hmall.topic";
//        //消息
//        String message = "喜报！孙悟空大战哥斯拉，胜!";
//        //发送消息
//        rabbitTemplate.convertAndSend(exchangeName, "china.news",message);
//    }

//    @Test
//    public void testSendMap() throws InterruptedException {
//        // 准备消息
//        Map<String,Object> msg = new HashMap<>();
//        msg.put("name", "柳岩");
//        msg.put("age", 21);
//        // 发送消息
//        rabbitTemplate.convertAndSend("object.queue", msg);
//    }

    @Test
    void testPublisherConfirm(){
        //1.创建correlationdata
        CorrelationData cd = new CorrelationData();
        //2. 给future添加 confirmcallback
        cd.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
            @Override
            public void onFailure(Throwable ex) {
                //2.1 future 发送异常时候的处理逻辑，基本不会触发
                log.error("send message fail ",ex);
            }

            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                //2.2 future 接收到回执的处理逻辑 参数中的result就是回执内容
                if(result.isAck()){//result.isack(), boolean类型， true代表ack回执
                    //false代表nack回执
                    log.debug("发送消息成功， 收到ack！");
                }else{//result.getReason(), string类型， 返回nack的异常时描述
                    log.error("发送消息失败，收到nack，reason：{}",
                            result.getReason());
                }
            }
        });
        //3.发送消息
        rabbitTemplate.convertAndSend("hmall.direct", "red", "hello",cd);
    }
}