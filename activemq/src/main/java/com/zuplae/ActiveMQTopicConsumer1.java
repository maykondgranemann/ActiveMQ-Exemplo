package com.zuplae;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQTopicConsumer1 {
    public static void main(String[] args) {
        String brokerUrl = "failover:(tcp://10.1.1.169:61616,tcp://10.1.1.169:61617,tcp://10.1.1.169:61618)?randomize=true";
        String topicName = "MEU_TOPICO";

        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = connectionFactory.createConnection();
            connection.setClientID("Consumer1");  // Cliente com ID único
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);

            MessageConsumer consumer = session.createDurableSubscriber(topic, "Consumer1");

            System.out.println("Consumer 1 está escutando o tópico...");

            while (true) {
                Message message = consumer.receive();
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    System.out.println("Consumer 1 recebeu: " + textMessage.getText());
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
