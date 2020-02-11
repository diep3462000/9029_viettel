/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.centech.sdp.mps.server;

import centech.sdp.smsgw.common.MOMsg;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author Administrator
 */
@WebService(serviceName = "RegActionTestService")
@Stateless()
public class RegActionTestService {

    public static final Logger logger = Logger.getLogger(RegActionService.class.getName());
    DateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final DateFormat logFileDateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final String logDir = "/data/smsgw2vtl/ws-log/op-sdp-mps-ws-register-server-test/";
    private static final String logFilePattern = "-reg-ws";
    private static final String logFileExt = ".log";
    
    private static final String jmsConnectionFactoryName = "jms/ConnectionFactory";
    
    @Resource(mappedName = "jndi/MpsRegRequestProperties")
    private Properties properties;

    @Resource(mappedName = jmsConnectionFactoryName)
    private ConnectionFactory jmsConnectionFactory;

    private static final String jmsQueueName = "jms/OP.MPS.SMS.REG.WS";    
    @Resource(mappedName = jmsQueueName)
    private Queue jmsQueue;
    
    FileWriter logFileWriter = null;
    BufferedWriter logBufferWriter = null;    

    public RegActionTestService(){
        logger.setUseParentHandlers(false);        

    }
    
    private void loggerInitilazation(){
        try {
            Date now = new Date();
            String logFilePath = logDir + logFileDateFormat.format(now) + logFilePattern + logFileExt;
            
            logFileWriter = new FileWriter(logFilePath, true);
            logBufferWriter = new BufferedWriter(logFileWriter);

            String logString = String.format("%s - RegActionService(): Started\n", new Object[] { logDateFormat.format(now)});
            logBufferWriter.write(logString);
            logger.log(Level.INFO, logString);
        
        } catch (IOException ex) {
            Logger.getLogger(RegActionService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(RegActionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Web service operation
     * subRequest
     */
    @WebMethod(operationName = "subRequest")
    public String subRequest(
            @WebParam(name = "username") String username
            , @WebParam(name = "password") String password
            , @WebParam(name = "serviceid") String serviceid
            , @WebParam(name = "msisdn") String msisdn
            , @WebParam(name = "chargetime") String chargetime
            , @WebParam(name = "params") String params
            , @WebParam(name = "mode") String mode
            , @WebParam(name = "amount") String amount
            , @WebParam(name = "command") String command
            ) {

        this.loggerInitilazation();

        String debug = "";
        Date now = new Date();
        String ret = "";
        String resultProcessingCode = "UNSUCCESS";
        String logString = "";
        
        int responseStatusCode = 1;
        String responseStatusDesc = "";

        Connection jmsConnection = null;
        Session jmsSession = null;
        MessageProducer jmsMessageProducer = null;

        debug += "-1";
        try {
            logString = String.format("%s - subRequest(): Request received: username=\"%s\", password=\"%s\", serviceid=\"%s\", msisdn=\"%s\", chargetime=\"%s\", params=\"%s\", mode=\"%s\", amount=\"%s\", command=\"%s\"\n", new Object[] { logDateFormat.format(now), username, password, serviceid, msisdn, chargetime, params, mode, amount, command });
            logBufferWriter.write(logString);
        debug += "-2";
            
            DataProcessor dataProcessor = new DataProcessor(logBufferWriter);
            if (dataProcessor.validateParams(username, password, serviceid, msisdn, chargetime, params, mode, amount, command)) {
        debug += "-3";
                if (dataProcessor.antheticateWS(username, password,properties.getProperty("username"),properties.getProperty("password"))) {
        debug += "-4";
                    MOMsg moMsg = dataProcessor.setWsMsgObject(username, password, serviceid, msisdn, chargetime, params, mode, amount, command,"REGISTER");
//                    String moMsgString = Msg.msgToString(moMsg);
        debug += "-5";

                    if(mode.equalsIgnoreCase("CHECK")){
          debug += "-6";
                      logString = String.format("Request for checking the ws service\n");
                        logBufferWriter.write(logString);
                        
                        resultProcessingCode = "SUCCESS";
                    }
                    else{
        debug += "-7";
                        if (this.jmsConnectionFactory != null) {
         debug += "-8";
                           jmsConnection = this.jmsConnectionFactory.createConnection();
                           jmsSession = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         debug += "-9";
                            if (jmsSession != null) {
         debug += "-10";
                                jmsMessageProducer = jmsSession.createProducer(this.jmsQueue);

                                //jmsMessage = jmsSession.createTextMessage(moMsgString);
                                if (jmsMessageProducer != null) {
                                    ObjectMessage jmsMsg = jmsSession.createObjectMessage(moMsg);
                                   // jmsMessageProducer.send(jmsMsg);
                                    logString = String.format("Msg sent to broker %s: %s\n", new Object[]{this.jmsQueueName, moMsg.debugString()});
                                    logBufferWriter.write(logString);
                                    resultProcessingCode = "SUCCESS";
                                } else {
                                    logString = String.format("ReceiveMO(): JMS Message Producer creation failed: {0}\n", new Object[]{jmsQueueName});
                                    logBufferWriter.write(logString);
                                }
                            } else {
                                logString = String.format("ReceiveMO(): JMS Session creation failed: {0}\n", new Object[]{"jms/ConnectionFactory"});
                                logBufferWriter.write(logString);
                            }
         debug += "-15";
                        } else {
                            logString = String.format("ReceiveMO(): JMS Connection Factory not ready: {0}\n", new Object[]{"jms/ConnectionFactory"});
                            logBufferWriter.write(logString);
                        }
         debug += "-16";
                    }
                } else {
                    resultProcessingCode = "AUTHENTICATION_FAILS";
                }
            } else {
                resultProcessingCode = "RARAMS_INPUT_IS_NOT_CORRECT";
            }
//        
        } catch (Exception ex) {
         debug += "-17";
            try {
                logString = String.format("subRequest(): Exception: {0}\n",new Object[]{ex.getMessage()}) ;
                logBufferWriter.write(logString);
            } catch (IOException ex1) {
         debug += "-18-"+ex1.getMessage();
                Logger.getLogger(RegActionService.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (jmsMessageProducer != null) {
                try {
                    jmsMessageProducer.close();
                } catch (Exception ex) {
                }
            }
            if (jmsSession != null) {
                try {
                    jmsSession.close();
                } catch (Exception ex) {
                }
            }
            if (jmsConnection != null) {
                try {
                    jmsConnection.close();
                } catch (Exception ex) {
                }
            }

         debug += "-19";
            responseStatusCode = ResponseStatusCode.getResponseStatusCode(resultProcessingCode);
            responseStatusDesc = ResponseStatusCode.getResponseStatusDesc(responseStatusCode);
            
            //return
            ret = new Integer(responseStatusCode).toString();
            ret += "|";
            ret += responseStatusDesc;

            logString = String.format("subRequest(): Return string for msisdn %s: \"%s\"\n", new Object[]{msisdn, resultProcessingCode + "[" + ret + "]"});
            try {
                logBufferWriter.write(logString);
            } catch (IOException ex) {
                logger.log(Level.INFO, "Exception on writing log: " + ex.getMessage());
            }
            if (logBufferWriter != null) try { logBufferWriter.close(); } catch (IOException ex) {} 
            if (logFileWriter != null) try { logFileWriter.close(); } catch (IOException ex) {} 
        }

        ret += debug;
        return ret;
    }    
}
