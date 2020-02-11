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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 *
 * @author dung.t
 */
@WebService(serviceName = "RenewActionService")
@Stateless()
public class RenewActionService {

    public static final Logger logger = Logger.getLogger("RenewActionService");
    private static final DateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
    private static final DateFormat logDateFormatFileName = new SimpleDateFormat("yyyyMMdd");
    private static final String logDir = "/data/smsgw2vtl/ws-log/op-sdp-mps-ws-renew-server/";
    private static final String logFilePattern = "-renew-ws";
    private static final String logFileExt = ".log";
    private static final String jmsConnectionFactoryName = "jms/ConnectionFactory";
    @Resource(mappedName = "jndi/MpsRenewRequestProperties")
    private Properties properties;
    @Resource(mappedName = jmsConnectionFactoryName)
    private ConnectionFactory jmsConnectionFactory;
    private static final String jmsQueueName = "OP.MPS.SMS.RENEW.WS.";
    private String jmsDestinationName = null;
    FileWriter logFileWriter = null;
    BufferedWriter logBufferWriter = null;

    public RenewActionService() {
        logger.setUseParentHandlers(false);

    }

    protected Destination getJmsDestination(String rqServiceId, Session jmsSession) {
        Destination retJmsDestination = null;
        String logString = "";

        try {
            logString = String.format("getJmsQueue: serviceid=%s\n", new Object[]{rqServiceId});
            logBufferWriter.write(logString);

            if (!rqServiceId.equalsIgnoreCase("")) {
                String key = "queue-name-" + rqServiceId.toLowerCase();
                String queueNameSuffix = properties.getProperty(key);

                logString = String.format("getJmsQueue: properties name =%s\n", new Object[]{key});
                logBufferWriter.write(logString);

                if (queueNameSuffix != null
                        && !queueNameSuffix.equalsIgnoreCase("")) {
                    this.jmsDestinationName = this.jmsQueueName + queueNameSuffix;

                    logString = String.format("getJmsQueue: queue name =%s\n", new Object[]{this.jmsDestinationName});
                    logBufferWriter.write(logString);

                    retJmsDestination = jmsSession.createQueue(this.jmsDestinationName);
                }
            }
        } catch (Exception ex) {
            logString = String.format("getJmsQueue: Exception=%s\n", new Object[]{ex.getMessage()});
            try {
                logBufferWriter.write(logString);
            } catch (IOException ex1) {
                Logger.getLogger(RegActionService.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return retJmsDestination;
    }

    private void loggerInitilazation() {
        try {
            Date now = new Date();
            String logFilePath = logDir + logDateFormatFileName.format(now) + logFilePattern + logFileExt;

            logFileWriter = new FileWriter(logFilePath, true);
            logBufferWriter = new BufferedWriter(logFileWriter);

            String logString = String.format("%s - RenewActionService(): Started\n", new Object[]{logDateFormat.format(now)});
            logBufferWriter.write(logString);
            logger.log(Level.INFO, logString);

        } catch (IOException ex) {
            Logger.getLogger(RegActionService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(RegActionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Web service operation subRequest
     */
    @WebMethod(operationName = "resultRequest")
    public String resultRequest(
            @WebParam(name = "username") String username, @WebParam(name = "password") String password, @WebParam(name = "serviceid") String serviceid, @WebParam(name = "msisdn") String msisdn, @WebParam(name = "chargetime") String chargetime, @WebParam(name = "params") String params, @WebParam(name = "mode") String mode, @WebParam(name = "amount") String amount) {


        this.loggerInitilazation();

        Date now = new Date();
        String ret = "";
        String resultProcessingCode = "UNSUCCESS";
        String command = "";
        String logString = "";

        int responseStatusCode = 1;
        String responseStatusDesc = "";

        Connection jmsConnection = null;
        Session jmsSession = null;
        MessageProducer jmsMessageProducer = null;

        try {
            logString = String.format("%s - resultRequest(): Request received: username=\"%s\", password=\"%s\", serviceid=\"%s\", msisdn=\"%s\", chargetime=\"%s\", params=\"%s\", mode=\"%s\", amount=\"%s\", command=\"%s\"\n", new Object[]{logDateFormat.format(now), username, password, serviceid, msisdn, chargetime, params, mode, amount, command});
            logBufferWriter.write(logString);

            DataProcessor dataProcessor = new DataProcessor(logBufferWriter);
            if (dataProcessor.validateParams(username, password, serviceid, msisdn, chargetime, params, mode, amount, command)) {
                if (dataProcessor.antheticateWS(username, password, properties.getProperty("username"), properties.getProperty("password"))) {
                    MOMsg moMsg = dataProcessor.setWsMsgObject(username, password, serviceid, msisdn, chargetime, params, mode, amount, command, "RENEW");
//                    String moMsgString = Msg.msgToString(moMsg);

                    if (this.jmsConnectionFactory != null) {
                        jmsConnection = this.jmsConnectionFactory.createConnection();
                        jmsSession = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        if (jmsSession != null) {

                            jmsMessageProducer = jmsSession.createProducer(this.getJmsDestination(serviceid, jmsSession));

                            //jmsMessage = jmsSession.createTextMessage(moMsgString);
                            if (jmsMessageProducer != null) {
                                ObjectMessage jmsMsg = jmsSession.createObjectMessage(moMsg);
                                jmsMessageProducer.send(jmsMsg);
                                logString = String.format("Msg sent to broker %s: %s\n", new Object[]{this.jmsDestinationName, moMsg.debugString()});
                                logBufferWriter.write(logString);
                                logger.log(Level.INFO, logString);
                                resultProcessingCode = "SUCCESS";
                            } else {
                                logString = String.format("ReceiveMO(): JMS Message Producer creation failed: {0}\n", new Object[]{this.jmsDestinationName});
                                logger.log(Level.WARNING, logString);
                            }
                        } else {
                            logString = String.format("ReceiveMO(): JMS Session creation failed: {0}\n", new Object[]{"jms/ConnectionFactory"});
                            logger.log(Level.WARNING, logString);
                        }
                    } else {
                        logString = String.format("ReceiveMO(): JMS Connection Factory not ready: {0}\n", new Object[]{"jms/ConnectionFactory"});
                        logger.log(Level.WARNING, logString);
                    }
                } else {
                    resultProcessingCode = "AUTHENTICATION_FAILS";
                }
            } else {
                resultProcessingCode = "RARAMS_INPUT_IS_NOT_CORRECT";
            }
//        
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "resultRequest(): Exception: ", ex);
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

            responseStatusCode = ResponseStatusCode.getResponseStatusCode(resultProcessingCode);
            responseStatusDesc = ResponseStatusCode.getResponseStatusDesc(responseStatusCode);

            //return
            ret = new Integer(responseStatusCode).toString();
//            ret += "|";
//            ret += responseStatusDesc;
            Date nowEnd = new Date();
            try{
                logString = String.format("\"%s - resultRequest(): Return string for msisdn %s: \"%s\"\n", new Object[]{logDateFormat.format(now), msisdn, resultProcessingCode + "[" + ret + "]"});
                logBufferWriter.write(logString);
            }
            catch(Exception ex){
            
            }
            logger.log(Level.INFO, logString);
        }

        if (logBufferWriter != null) {
            try {
                logBufferWriter.close();
            } catch (IOException ex) {
            }
        }
        if (logFileWriter != null) {
            try {
                logFileWriter.close();
            } catch (IOException ex) {
            }
        }

        return ret;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "ReceiveCDR")
    public Integer ReceiveCDR(@WebParam(name = "transId") String transId, @WebParam(name = "src") String src, @WebParam(name = "chargeDate") String chargeDate, @WebParam(name = "amount") String amount, @WebParam(name = "status") String status, @WebParam(name = "opid") String opid, @WebParam(name = "service") String service, @WebParam(name = "username") String username, @WebParam(name = "password") String password) {
        this.loggerInitilazation();

        Date now = new Date();
        int ret = -1;
        String resultProcessingCode = "UNSUCCESS";
        String command = "";
        String logString = "";

        int responseStatusCode = 1;
        String responseStatusDesc = "";

        Connection jmsConnection = null;
        Session jmsSession = null;
        MessageProducer jmsMessageProducer = null;

        logString = String.format("%s - ============================================================================================\n", new Object[]{logDateFormat.format(now)});
        try {
            logBufferWriter.write(logString);
        } catch (IOException ex) {
            //Logger.getLogger(RenewActionService.class.getName()).log(Level.SEVERE, null, ex);
        }

        logString = String.format("%s - resultRequest(): Request received: transId=\"%s\", src=\"%s\", chargeDate=\"%s\", amount=\"%s\", status=\"%s\", opid=\"%s\", service=\"%s\", username=\"%s\", password=\"%s\"\n", new Object[]{logDateFormat.format(now), transId, src, chargeDate, amount, status, opid, service, username, password});
        try {
            logBufferWriter.write(logString);
        } catch (IOException ex) {
            //Logger.getLogger(RenewActionService.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            DataProcessor dataProcessor = new DataProcessor();
            if (dataProcessor.validateParamsReceiverCDR(transId, src, chargeDate, amount, status, opid, service, username, password)) {
                if (dataProcessor.antheticateWS(username, password, properties.getProperty("username"), properties.getProperty("password"))) {
                    MOMsg moMsg = dataProcessor.setWsMsgObjectReceiverCDR(transId, src, chargeDate, amount, status, opid, service, username, password, "RENEW", properties);
//                    String moMsgString = Msg.msgToString(moMsg);

                    if (this.jmsConnectionFactory != null) {
                        jmsConnection = this.jmsConnectionFactory.createConnection();
                        jmsSession = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        if (jmsSession != null) {

                            jmsMessageProducer = jmsSession.createProducer(this.getJmsDestination(opid, jmsSession));

                            //jmsMessage = jmsSession.createTextMessage(moMsgString);
                            if (jmsMessageProducer != null) {
                                ObjectMessage jmsMsg = jmsSession.createObjectMessage(moMsg);
                                jmsMessageProducer.send(jmsMsg);
                                logString = String.format("Msg sent to broker %s: %s\n", new Object[]{this.jmsDestinationName, moMsg.debugString()});
                                try {
                                    logBufferWriter.write(logString);
                                } catch (IOException ex) {
                                    //Logger.getLogger(RenewActionService.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                resultProcessingCode = "SUCCESS";
                            } else {
                                logString = String.format("ReceiveMO(): JMS Message Producer creation failed: {0}\n", new Object[]{this.jmsDestinationName});
                                try {
                                    logBufferWriter.write(logString);
                                } catch (IOException ex) {
                                    //Logger.getLogger(RenewActionService.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        } else {
                            logString = String.format("ReceiveMO(): JMS Session creation failed: {0}\n", new Object[]{"jms/ConnectionFactory"});
                            try {
                                logBufferWriter.write(logString);
                            } catch (IOException ex) {
                                //Logger.getLogger(RenewActionService.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } else {
                        logString = String.format("ReceiveMO(): JMS Connection Factory not ready: {0}\n", new Object[]{"jms/ConnectionFactory"});
                        try {
                            logBufferWriter.write(logString);
                        } catch (IOException ex) {
                            //Logger.getLogger(RenewActionService.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    resultProcessingCode = "AUTHENTICATION_FAILS";
                }
            } else {
                resultProcessingCode = "RARAMS_INPUT_IS_NOT_CORRECT";
            }
//        
        } catch (Exception ex) {
            logString = String.format("resultRequest(): Exception: {0}\n", ex);
            try {
                logBufferWriter.write(logString);
            } catch (IOException ex1) {
                //Logger.getLogger(RenewActionService.class.getName()).log(Level.SEVERE, null, ex);
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

            responseStatusCode = ResponseStatusCodeReceiverCDP.getResponseStatusCode(resultProcessingCode);
            responseStatusDesc = ResponseStatusCodeReceiverCDP.getResponseStatusDesc(responseStatusCode);

            //return
            ret = responseStatusCode;
//            ret += "|";
//            ret += responseStatusDesc;
            Date nowEnd = new Date();
            logString = String.format("\"%s - resultRequest(): Return string for msisdn %s: \"%s\"\n", new Object[]{logDateFormat.format(now), src, resultProcessingCode + "[" + ret + "]"});
            try {
                logBufferWriter.write(logString);
            } catch (IOException ex1) {
                //Logger.getLogger(RenewActionService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (logBufferWriter != null) {
            try {
                logBufferWriter.close();
            } catch (IOException ex) {
            }
        }
        if (logFileWriter != null) {
            try {
                logFileWriter.close();
            } catch (IOException ex) {
            }
        }

        return ret;
    }
    
    /**
     * Web service operation subRequest
     */
    @WebMethod(operationName = "resultRequestv2")
    public String resultRequestv2(
            @WebParam(name = "username") String username, @WebParam(name = "password") String password, @WebParam(name = "serviceid") String serviceid, @WebParam(name = "msisdn") String msisdn, @WebParam(name = "chargetime") String chargetime, @WebParam(name = "params") String params, @WebParam(name = "mode") String mode, @WebParam(name = "amount") String amount, @WebParam(name = "detail") String detail, @WebParam(name = "Chargecode") String Chargecode, @WebParam(name = "nextRenewalTime") String nextRenewalTime, @WebParam(name = "transid") String transid) {


        this.loggerInitilazation();

        Date now = new Date();
        String ret = "";
        String resultProcessingCode = "UNSUCCESS";
        String command = "";
        String logString = "";

        int responseStatusCode = 1;
        String responseStatusDesc = "";

        Connection jmsConnection = null;
        Session jmsSession = null;
        MessageProducer jmsMessageProducer = null;

        try {

            logString = String.format("%s - resultRequest(): Request received: username=\"%s\""
                    + ", password=\"%s\""
                    + ", serviceid=\"%s\""
                    + ", msisdn=\"%s\""
                    + ", chargetime=\"%s\""
                    + ", params=\"%s\""
                    + ", mode=\"%s\""
                    + ", amount=\"%s\""
                    + ", command=\"%s\""
                    + ", detail=\"%s\""
                    + ", Chargecode=\"%s\""
                    + ", nextRenewalTime=\"%s\""
                    + ", transid=\"%s\""
                    + "\n"
                    , new Object[]{logDateFormat.format(now), username, password, serviceid, msisdn, chargetime, params, mode, amount, command,detail,Chargecode,nextRenewalTime,transid});
            logFileWriter.write(logString);
            logger.log(Level.INFO, logString);
            DataProcessor dataProcessor = new DataProcessor(logBufferWriter);
            if (dataProcessor.validateParams(username, password, serviceid, msisdn, chargetime, params, mode, amount, command)) {
                if (dataProcessor.antheticateWS(username, password, properties.getProperty("username"), properties.getProperty("password"))) {
                    MOMsg moMsg = dataProcessor.setWsMsgObjectV2(username, password, serviceid, msisdn, chargetime, params, mode, amount, command, "RENEW", detail, Chargecode, nextRenewalTime, transid);
//                    String moMsgString = Msg.msgToString(moMsg);

                    if (this.jmsConnectionFactory != null) {
                        jmsConnection = this.jmsConnectionFactory.createConnection();
                        jmsSession = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        if (jmsSession != null) {

                            jmsMessageProducer = jmsSession.createProducer(this.getJmsDestination(serviceid, jmsSession));

                            //jmsMessage = jmsSession.createTextMessage(moMsgString);
                            if (jmsMessageProducer != null) {
                                ObjectMessage jmsMsg = jmsSession.createObjectMessage(moMsg);
                                jmsMessageProducer.send(jmsMsg);
                                logString = String.format("Msg sent to broker %s: %s\n", new Object[]{this.jmsDestinationName, moMsg.debugString()});
                                logger.log(Level.INFO, logString);
                                resultProcessingCode = "SUCCESS";
                            } else {
                                logString = String.format("ReceiveMO(): JMS Message Producer creation failed: {0}\n", new Object[]{this.jmsDestinationName});
                                logger.log(Level.WARNING, logString);
                            }
                        } else {
                            logString = String.format("ReceiveMO(): JMS Session creation failed: {0}\n", new Object[]{"jms/ConnectionFactory"});
                            logger.log(Level.WARNING, logString);
                        }
                    } else {
                        logString = String.format("ReceiveMO(): JMS Connection Factory not ready: {0}\n", new Object[]{"jms/ConnectionFactory"});
                        logger.log(Level.WARNING, logString);
                    }
                } else {
                    resultProcessingCode = "AUTHENTICATION_FAILS";
                }
            } else {
                resultProcessingCode = "RARAMS_INPUT_IS_NOT_CORRECT";
            }
//        
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "resultRequest(): Exception: ", ex);
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

            responseStatusCode = ResponseStatusCode.getResponseStatusCode(resultProcessingCode);
            responseStatusDesc = ResponseStatusCode.getResponseStatusDesc(responseStatusCode);

            //return
            ret = new Integer(responseStatusCode).toString();
//            ret += "|";
//            ret += responseStatusDesc;
            Date nowEnd = new Date();
            try{
                logString = String.format("\"%s - resultRequest(): Return string for msisdn %s: \"%s\"\n", new Object[]{logDateFormat.format(now), msisdn, resultProcessingCode + "[" + ret + "]"});
                logBufferWriter.write(logString);
            }
            catch(Exception ex){}
                    
            logger.log(Level.INFO, logString);
        }

        if (logBufferWriter != null) {
            try {
                logBufferWriter.close();
            } catch (IOException ex) {
            }
        }
        if (logFileWriter != null) {
            try {
                logFileWriter.close();
            } catch (IOException ex) {
            }
        }

        return ret;
    }
    
}
