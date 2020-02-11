/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.centech.sdp.mps.server;

import centech.sdp.smsgw.common.MO9029ParamsMobiFone;
import centech.sdp.smsgw.common.MOMsg;
import centech.sdp.smsgw.common.ObjProcess9029Result;
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
 * @author Administrator
 */
@WebService(serviceName = "MoReceiver9029TEST")
@Stateless()
public class MoReceiver9029TEST {

    public static final Logger logger = Logger.getLogger(MoReceiver9029TEST.class.getName());
    DateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final DateFormat logFileDateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final String logDir = "/data/smsgw2vtl/ws-log/op-sdp-mps-ws-getcontent9029-test-server/";
    private static final String logFilePattern = "-getcontent-ws";
    private static final String logFileExt = ".log";
    
    private static final String jmsConnectionFactoryName = "jms/ConnectionFactory";
    
    @Resource(mappedName = "jndi/9029MpsRegRequestProperties")
    private Properties properties;

    @Resource(mappedName = jmsConnectionFactoryName)
    private ConnectionFactory jmsConnectionFactory;

    private static final String jmsQueueName = "OP.VT.9029.SMS.WS.";    
    private String jmsDestinationName = null;    
    
    FileWriter logFileWriter = null;
    BufferedWriter logBufferWriter = null;    
    
    protected Destination getJmsDestination(String rqServiceId, Session jmsSession){
        Destination retJmsDestination = null;
        String logString = "";
        
        try{
            logString = String.format("getJmsQueue: serviceid=%s\n", new Object[] { rqServiceId});
            logBufferWriter.write(logString);
            
            if(!rqServiceId.equalsIgnoreCase("")){
                String key = "queue-name-" + rqServiceId.toLowerCase();
                String queueNameSuffix = properties.getProperty(key);
                
                logString = String.format("getJmsQueue: properties name =%s\n", new Object[] { key});
                logBufferWriter.write(logString);

                if(queueNameSuffix!=null
                        && !queueNameSuffix.equalsIgnoreCase("")){
                    this.jmsDestinationName = this.jmsQueueName + queueNameSuffix;
                    
                    logString = String.format("getJmsQueue: queue name =%s\n", new Object[] { this.jmsDestinationName});
                    logBufferWriter.write(logString);

                    retJmsDestination = jmsSession.createQueue(this.jmsDestinationName);
                }
            }
        }
        catch(Exception ex){
            logString = String.format("getJmsQueue: Exception=%s\n", new Object[] { ex.getMessage()});
            try {
                logBufferWriter.write(logString);
            } catch (IOException ex1) {
                Logger.getLogger(MoReceiver9029TEST.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        
        return retJmsDestination;
    }
    
    private void loggerInitilazation(){
        try {
            Date now = new Date();
            String logFilePath = logDir + logFileDateFormat.format(now) + logFilePattern + logFileExt;
            
            logFileWriter = new FileWriter(logFilePath, true);
            logBufferWriter = new BufferedWriter(logFileWriter);

            String logString = String.format("%s - GetContent9029(): Started\n", new Object[] { logDateFormat.format(now)});
            logBufferWriter.write(logString);
            logger.log(Level.INFO, logString);
        
        } catch (IOException ex) {
            Logger.getLogger(MoReceiver9029TEST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(MoReceiver9029TEST.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Web service operation
     * subRequest
     */
    @WebMethod(operationName = "contentRequest")
    public String contentRequest(
            @WebParam(name = "username") String username
            , @WebParam(name = "password") String password
            , @WebParam(name = "serviceid") String serviceid
            , @WebParam(name = "msisdn") String msisdn
            , @WebParam(name = "params") String params
            , @WebParam(name = "mode") String mode
            , @WebParam(name = "amount") String amount
            , @WebParam(name = "command") String command
            ) {

        this.loggerInitilazation();
        
        Date now = new Date();
        String ret = "";
        String resultProcessingCode = "UNSUCCESS";
        String logString = "";
        
        int responseStatusCode = 1;
        String responseStatusDesc = "";

        Connection jmsConnection = null;
        Session jmsSession = null;
        MessageProducer jmsMessageProducer = null;


        try {
            logString = String.format("%s - MoReceiver9029 service------------START----------------------\n", new Object[] { logDateFormat.format(now)});
            logBufferWriter.write(logString);

            logString = String.format("%s - subRequest(): Request received: username=\"%s\", password=\"%s\", serviceid=\"%s\", msisdn=\"%s\", params=\"%s\", mode=\"%s\", amount=\"%s\", command=\"%s\"\n", new Object[] { logDateFormat.format(now), username, password, serviceid, msisdn, params, mode, amount, command });
            logBufferWriter.write(logString);
            
            DataProcessor dataProcessor = new DataProcessor(logBufferWriter, properties);
            if (dataProcessor.validateParamsGetContent(username, password, serviceid, msisdn, params, mode, amount, command)) {
                if (dataProcessor.antheticateWS(username, password,properties.getProperty("username"),properties.getProperty("password"))) {
                    process9029MO objProcess9029MO = new process9029MO(this.logBufferWriter, this.properties);
                    
                    if(mode.equalsIgnoreCase("REAL")){
                        MO9029ParamsMobiFone objParamsRequest = objProcess9029MO.ViettelTransformToMBFParams(serviceid, msisdn, params, amount, command, "WCG-0000");
                        ObjProcess9029Result obj9029Result = objProcess9029MO.processMO9029_MobiFone(objParamsRequest.content_id, objParamsRequest.cpCode, objParamsRequest.gameCode, objParamsRequest.totalAmount, objParamsRequest.account, objParamsRequest.msisdn, objParamsRequest.result, params);

                        if(obj9029Result.serviceType.equalsIgnoreCase("MO-BILLING")){
                            this.sendJMSObject(obj9029Result.moMsg);
                        }
                        responseStatusDesc = obj9029Result.responseStatusDesc;
                    }
                    resultProcessingCode = "SUCCESS";
                } else {
                    resultProcessingCode = "AUTHENTICATION_FAILS";
                }
            } else {
                resultProcessingCode = "RARAMS_INPUT_IS_NOT_CORRECT";
            }
//        
        } catch (Exception ex) {
            try {
                logString = String.format("%s - subRequest(): Exception: %s\n",new Object[]{logDateFormat.format(new Date()), ex.getMessage()}) ;
                logBufferWriter.write(logString);
            } catch (IOException ex1) {
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

            responseStatusCode = ResponseStatusCode.getResponseStatusCode(resultProcessingCode);
            responseStatusDesc = (responseStatusDesc.equalsIgnoreCase("")) ? ResponseStatusCode.getResponseStatusDesc(responseStatusCode) : responseStatusDesc;
            
            //return
            ret = new Integer(responseStatusCode).toString();
            ret += "|";
            ret += responseStatusDesc;

            logString = String.format("%s - subRequest(): Return string for msisdn %s: \"%s\"\n", new Object[]{logDateFormat.format(new Date()), msisdn, resultProcessingCode + "[" + ret + "]"});
            try {
                logBufferWriter.write(logString);
            } catch (IOException ex) {
                logger.log(Level.INFO, "Exception on writing log: " + ex.getMessage());
            }

            logString = String.format("%s - MoReceiver9029 service------------END----------------------\n", new Object[] { logDateFormat.format(now)});
            try {
                logBufferWriter.write(logString);
            } catch (IOException ex) {
                logger.log(Level.INFO, "Exception on writing log: " + ex.getMessage());
            }

            if (logBufferWriter != null) try { logBufferWriter.close(); } catch (IOException ex) {} 
            if (logFileWriter != null) try { logFileWriter.close(); } catch (IOException ex) {} 
        }

        return ret;
    }   

    private void sendJMSObject(MOMsg moMsg) throws IOException{
        Connection jmsConnection = null;
        Session jmsSession = null;
        MessageProducer jmsMessageProducer = null;
        String logString = "";

        try{
            if (this.jmsConnectionFactory != null) {
                jmsConnection = this.jmsConnectionFactory.createConnection();
                jmsSession = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                if (jmsSession != null) {

                    jmsMessageProducer = jmsSession.createProducer(this.getJmsDestination(moMsg.opId, jmsSession));

                    //jmsMessage = jmsSession.createTextMessage(moMsgString);
                    if (jmsMessageProducer != null) {
                        ObjectMessage jmsMsg = jmsSession.createObjectMessage(moMsg);
                        jmsMessageProducer.send(jmsMsg);

                        logString = String.format("Msg sent to broker %s: %s\n", new Object[]{this.jmsDestinationName, moMsg.debugString()});
                        logBufferWriter.write(logString);
                    } else {
                        logString = String.format("ReceiveMO(): JMS Message Producer creation failed: {0}\n", new Object[]{this.jmsDestinationName});
                        logBufferWriter.write(logString);
                    }
                } else {
                    logString = String.format("ReceiveMO(): JMS Session creation failed: {0}\n", new Object[]{"jms/ConnectionFactory"});
                    logBufferWriter.write(logString);
                }
            } else {
                logString = String.format("ReceiveMO(): JMS Connection Factory not ready: {0}\n", new Object[]{"jms/ConnectionFactory"});
                logBufferWriter.write(logString);
            }                

        }
        catch(Exception ex){
            logString = String.format("sendJMSObject(): Exceptiony: {0}\n", new Object[]{ex.getMessage()});
            try {
                logBufferWriter.write(logString);
            } catch (IOException ex1) {
                Logger.getLogger(GetContent9029.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        finally{
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
        }
    }        
}
