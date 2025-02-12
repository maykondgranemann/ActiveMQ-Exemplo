package com.zuplae;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQAdvisoryConsumer {
    public static void main(String[] args) {
        String brokerUrl = "failover:(tcp://10.1.1.169:61616,tcp://10.1.1.169:61617,tcp://10.1.1.169:61618)?randomize=true";

        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Criando consumidores para tópicos de Advisory Messages
            consumeAdvisory(session, "ActiveMQ.Advisory.Consumer.Queue.>");
            consumeAdvisory(session, "ActiveMQ.Advisory.Connection");
            consumeAdvisory(session, "ActiveMQ.Advisory.Message.Queue.>");

            System.out.println("Monitorando Advisory Messages...");

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private static void consumeAdvisory(Session session, String advisoryTopic) throws JMSException {
        Topic topic = session.createTopic(advisoryTopic);
        MessageConsumer consumer = session.createConsumer(topic);

        new Thread(() -> {
            try {
                while (true) {
                    Message message = consumer.receive();
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        System.out.println("[" + advisoryTopic + "] " + textMessage.getText());
                    } else {
                        System.out.println("[" + advisoryTopic + "] Mensagem recebida: " + message);
                    }
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
