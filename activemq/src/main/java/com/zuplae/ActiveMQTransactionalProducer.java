package com.zuplae;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQTransactionalProducer {
    public static void main(String[] args) {
        String brokerUrl = "failover:(tcp://10.1.1.169:61616,tcp://10.1.1.169:61617,tcp://10.1.1.169:61618)?randomize=true";

        String[] queues = {"FILA_A", "FILA_B", "FILA_C"};

        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Criando uma sessão transacional (commit manual)
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);

            for (String queueName : queues) {
                Destination destination = session.createQueue(queueName);
                MessageProducer producer = session.createProducer(destination);

                String messageText = "Mensagem enviada para " + queueName;
                TextMessage message = session.createTextMessage(messageText);

                producer.send(message);
                System.out.println("[COMMIT] Enviado para " + queueName + ": " + messageText);
            }

            // Confirma a transação
            session.commit();

            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
