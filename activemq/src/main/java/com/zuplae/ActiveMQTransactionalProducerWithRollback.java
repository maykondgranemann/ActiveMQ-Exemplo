package com.zuplae;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQTransactionalProducerWithRollback {
    public static void main(String[] args) {
        String brokerUrl = "failover:(tcp://10.1.1.169:61616,tcp://10.1.1.169:61617,tcp://10.1.1.169:61618)?randomize=true";

        String[] queues = {"FILA_A", "FILA_B", "FILA_C"};

        Connection connection = null;
        Session session = null;

        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            connection = connectionFactory.createConnection();
            connection.start();

            // Criando uma sessão transacional (commit ou rollback manual)
            session = connection.createSession(true, Session.SESSION_TRANSACTED);

            for (int i = 0; i < queues.length; i++) {
                String queueName = queues[i];
                Destination destination = session.createQueue(queueName);
                MessageProducer producer = session.createProducer(destination);

                String messageText = "Mensagem enviada para " + queueName;
                TextMessage message = session.createTextMessage(messageText);

                producer.send(message);
                System.out.println("[TRANSAÇÃO] Enviado para " + queueName + ": " + messageText);

                // Simula uma falha no meio do envio
                if (i == 1) {
                    throw new RuntimeException("Simulação de falha no envio para " + queueName);
                }
            }

            // Se não houver falha, confirma a transação
            session.commit();
            System.out.println("[COMMIT] Mensagens confirmadas.");

        } catch (Exception e) {
            System.err.println("[ROLLBACK] Erro ocorrido: " + e.getMessage());
            try {
                if (session != null) {
                    session.rollback();
                    System.out.println("[ROLLBACK] Transação desfeita. Nenhuma mensagem foi enviada.");
                }
            } catch (JMSException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            // Fechando conexão e sessão
            try {
                if (session != null) {
                    session.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }
}
