package com.zuplae;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.DeliveryMode;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQProducer {
    public static void main(String[] args) {
        String brokerUrl = "failover:(tcp://10.1.1.169:61617,tcp://10.1.1.169:61618)?randomize=true"; // Porta do ActiveMQ 5.x
        String queueName = "MINHA_FILA";

        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);

            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            String messageText = "Olá, ActiveMQ master!";
            TextMessage message = session.createTextMessage(messageText);
            producer.send(message);

            System.out.println("Mensagem enviada: " + messageText);

            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
