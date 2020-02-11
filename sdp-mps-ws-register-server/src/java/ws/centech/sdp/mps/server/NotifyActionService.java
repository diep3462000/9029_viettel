
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
import static ws.centech.sdp.mps.server.RegActionService.logger;

/**
 *
 * @author Administrator
 */
@WebService(serviceName = "NotifyActionService")
@Stateless()
public class NotifyActionService {
    public static final Logger logger = Logger.getLogger(RegActionService.class.getName());
    DateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final DateFormat logFileDateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final String logDir = "/data/smsgw2vtl/ws-log/op-sdp-mps-ws-notify-server/";
    private static final String logFilePattern = "-notify-ws";
    private static final String logFileExt = ".log";
    
    private static final String jmsConnectionFactoryName = "jms/ConnectionFactory";
    
    @Resource(mappedName = "jndi/MpsNotifyRequestProperties")
    private Properties properties;

    @Resource(mappedName = jmsConnectionFactoryName)
    private ConnectionFactory jmsConnectionFactory;

    private static final String jmsQueueName = "OP.MPS.SMS.REG.WS.";    
    private String jmsDestinationName = null;    
    
    FileWriter logFileWriter = null;
    BufferedWriter logBufferWriter = null;    

    public NotifyActionService(){
        logger.setUseParentHandlers(false);        

    }
     
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
                Logger.getLogger(RegActionService.class.getName()).log(Level.SEVERE, null, ex1);
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

            String logString = String.format("%s - doNotifyService(): Started\n", new Object[] { logDateFormat.format(now)});
            logBufferWriter.write(logString);
            logger.log(Level.INFO, logString);
        
        } catch (IOException ex) {
            Logger.getLogger(RegActionService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(RegActionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @WebMethod(operationName = "sendNotify")
    public String sendNotify(
            @WebParam(name = "userName") String username
            , @WebParam(name = "password") String password
            , @WebParam(name = "cpRequestId") String cpRequestId
            , @WebParam(name = "mobile") String msisdn
            , @WebParam(name = "price") String price
            , @WebParam(name = "cmd") String cmd
            , @WebParam(name = "responseCode") String responseCode
            ) {
//variable init
        String serviceid = "";
        String chargetime = "";
        String mode = "CHECK";
        String command = "NOTIFYWAP";
        String params = responseCode;
        
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
//-------------------

        try {
            logString = String.format("%s - doNotify(): Request received: username=\"%s\", password=\"%s\", cpRequestId=\"%s\", mobile=\"%s\", price=\"%s\", cmd=\"%s\", responseCode=\"%s\"\n", new Object[] { logDateFormat.format(now), username, password, cpRequestId, msisdn, price, cmd, responseCode });
            logBufferWriter.write(logString);
            
            DataProcessor dataProcessor = new DataProcessor(logBufferWriter);
            if (dataProcessor.validateParamsNotify(username, password, cpRequestId, msisdn, price, cmd, responseCode)) {
                if (dataProcessor.antheticateWS(username, password,properties.getProperty("username"),properties.getProperty("password"))) {
                    serviceid = dataProcessor.getServiceIdFromCpRequestId(cpRequestId,properties);
                                        
                    MOMsg moMsg = dataProcessor.setWsMsgObjectNotify(username, password, serviceid, msisdn, chargetime, params, mode, price, command,cmd);
//                    String moMsgString = Msg.msgToString(moMsg);
                    if(mode.equalsIgnoreCase("CHECK")){
                        logString = String.format("Request for checking the ws service\n");
                        logBufferWriter.write(logString);
                        
                        resultProcessingCode = "SUCCESS";
                    }
                    else{
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
                                    resultProcessingCode = "SUCCESS";
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
                } else {
                    resultProcessingCode = "AUTHENTICATION_FAILS";
                }
            } else {
                resultProcessingCode = "RARAMS_INPUT_IS_NOT_CORRECT";
            }
//        
        } catch (Exception ex) {
            try {
                logString = String.format("doNotify(): Exception: {0}\n",new Object[]{ex.getMessage()}) ;
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

        return ret;
    }  
}
