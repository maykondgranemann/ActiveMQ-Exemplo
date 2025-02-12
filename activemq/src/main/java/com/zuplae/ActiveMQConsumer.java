package com.zuplae;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQConsumer {
    public static void main(String[] args) {
        String brokerUrl = "failover:(tcp://10.1.1.169:61616,tcp://10.1.1.169:61617,tcp://10.1.1.169:61618)?randomize=true"; // Porta do ActiveMQ 5.x
        String queueName = "MINHA_FILA";

        try {
            // Criando conexão com ActiveMQ
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Criando sessão JMS
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Criando destino (fila)
            Destination destination = session.createQueue(queueName);

            // Criando consumidor
            MessageConsumer consumer = session.createConsumer(destination);

            System.out.println("Aguardando mensagens...");

            // Receber mensagem (bloqueia até que uma mensagem seja recebida)
            Message message = consumer.receive();

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                System.out.println("Mensagem recebida: " + textMessage.getText());
            } else {
                System.out.println("Mensagem recebida não é do tipo esperado!");
            }

            // Fechando conexão
            consumer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
