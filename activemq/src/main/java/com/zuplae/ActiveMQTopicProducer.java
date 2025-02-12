package com.zuplae;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.DeliveryMode;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQTopicProducer {
    public static void main(String[] args) {
        String brokerUrl = "failover:(tcp://10.1.1.169:61616,tcp://10.1.1.169:61617,tcp://10.1.1.169:61618)?randomize=true";
        String topicName = "MEU_TOPICO";

        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);

            MessageProducer producer = session.createProducer(topic);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            String messageText = "Nova mensagem no topico!";
            TextMessage message = session.createTextMessage(messageText);
            producer.send(message);

            System.out.println("Mensagem enviada para o tópico: " + messageText);

            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
