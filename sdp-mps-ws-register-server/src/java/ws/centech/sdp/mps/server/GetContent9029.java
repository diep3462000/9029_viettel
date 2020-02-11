/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
 * @author Administrator
 */
@WebService(serviceName = "GetContent9029")
@Stateless()
public class GetContent9029 {

    public static final Logger logger = Logger.getLogger(GetContent9029.class.getName());
    DateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final DateFormat logFileDateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final String logDir = "/data/smsgw2vtl/ws-log/op-sdp-mps-ws-getcontent9029-server/";
    private static final String logFilePattern = "-getcontent-ws";
    private static final String logFileExt = ".log";
    
    private static final String jmsConnectionFactoryName = "jms/ConnectionFactory";
    
    @Resource(mappedName = "jndi/9029MpsRegRequestProperties")
    private Properties properties;

    @Resource(mappedName = jmsConnectionFactoryName)
    private ConnectionFactory jmsConnectionFactory;

    private static final String jmsQueueName = "OP.MPS.SMS.REG.WS.";    
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
                Logger.getLogger(GetContent9029.class.getName()).log(Level.SEVERE, null, ex1);
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
            Logger.getLogger(GetContent9029.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(GetContent9029.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
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



        try {
            String logString = String.format("%s - subRequest(): Request received: username=\"%s\", password=\"%s\", serviceid=\"%s\", msisdn=\"%s\", params=\"%s\", mode=\"%s\", amount=\"%s\", command=\"%s\"\n", new Object[] { logDateFormat.format(now), username, password, serviceid, msisdn, params, mode, amount, command });
            logBufferWriter.write(logString);
        }
        catch(Exception ex){}
        try {
            logBufferWriter.write("aaaaaaaa");
        }
        catch(Exception ex){}
        if (logBufferWriter != null) try { logBufferWriter.close(); } catch (IOException ex) {} 
        if (logFileWriter != null) try { logFileWriter.close(); } catch (IOException ex) {} 
        
        return "abc1111";
    }       
    
    
    /**
     * Web service operation
     * subRequest
     */
/*    @WebMethod(operationName = "contentRequest")
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
            logString = String.format("%s - subRequest(): Request received: username=\"%s\", password=\"%s\", serviceid=\"%s\", msisdn=\"%s\", params=\"%s\", mode=\"%s\", amount=\"%s\", command=\"%s\"\n", new Object[] { logDateFormat.format(now), username, password, serviceid, msisdn, params, mode, amount, command });
            logBufferWriter.write(logString);
            
            DataProcessor dataProcessor = new DataProcessor(logBufferWriter, properties);
            if (dataProcessor.validateParamsGetContent(username, password, serviceid, msisdn, params, mode, amount, command)) {
                if (dataProcessor.antheticateWS(username, password,properties.getProperty("username"),properties.getProperty("password"))) {
                    String serviceCode = properties.getProperty("GETCONTENT-SERVICE-"+serviceid.toUpperCase());
                    String serviceTypeInfo = properties.getProperty("GETCONTENT-TYPE-"+serviceCode.toUpperCase()+"-KEYWORD-"+params.toUpperCase());

                    String serviceType = serviceTypeInfo;
                    String subServiceType = "";
                    //check real serviceType
                    if(serviceTypeInfo.contains(",")){
                        logString = String.format("%s - Checking sub type of action. Service type info: %s\n", new Object[] { logDateFormat.format(new Date()), serviceTypeInfo });
                        logBufferWriter.write(logString);
                        
                        String[] arrServiceTypeItems = serviceTypeInfo.split(",");
                        if(arrServiceTypeItems.length>=2){
                            serviceType = arrServiceTypeItems[0];
                            subServiceType = arrServiceTypeItems[1];
                        }
                    }
                    //-------------------------------------------------------------------------------------
                    logString = String.format("%s - Service type: %s\n", new Object[] { logDateFormat.format(new Date()), serviceType });
                    logBufferWriter.write(logString);
                    logString = String.format("%s - Sub Service type: %s\n", new Object[] { logDateFormat.format(new Date()), subServiceType });
                    logBufferWriter.write(logString);
                    
                    MOMsg moMsg = null;
                    if(serviceType.equalsIgnoreCase("CHECKUSEREXISTS")){
                        String new_serviceid = properties.getProperty("GETCONTENT-PACKAGE-"+serviceid.toUpperCase());
                        moMsg = dataProcessor.setWsMsgObjectGetContent(username, password, new_serviceid, msisdn, "", "REGISTER", mode, amount, "","CHECKUSEREXISTS");
                    }
                    else if(serviceType.equalsIgnoreCase("MOFORWARD")){
                        moMsg = dataProcessor.setWsMsgObjectGetContent(username, password, serviceid, msisdn, "", "REGISTER", mode, amount, params,"MOFORWARD");
                    }
                    else if(serviceType.equalsIgnoreCase("MOFORWARD9029")){
                        moMsg = dataProcessor.setWsMsg9029Object(username, password, serviceid, msisdn, "", "REGISTER", mode, amount, params,"MOFORWARD");
                    }
                    else if(serviceType.equalsIgnoreCase("MOFORWARDGW")){
                        moMsg = dataProcessor.setWsMsgObjectGetContent(username, password, serviceid, msisdn, "", "REGISTER", mode, amount, params,"MOFORWARD");
                    }
//                    String moMsgString = Msg.msgToString(moMsg);
                    
                    //ws api
                    String wsHost = properties.getProperty(serviceCode.toLowerCase() + "-" + serviceType.toLowerCase() + "-ws-host");
                    String wsPort = properties.getProperty(serviceCode.toLowerCase() + "-" + serviceType.toLowerCase() + "-ws-port");
                    String wsPath = properties.getProperty(serviceCode.toLowerCase() + "-" + serviceType.toLowerCase() + "-ws-path");
                    String wsUsername = properties.getProperty(serviceCode.toLowerCase() + "-" + serviceType.toLowerCase() + "-ws-username");
                    String wsPassword = properties.getProperty(serviceCode.toLowerCase() + "-" + serviceType.toLowerCase() + "-ws-password");
                    String wsClientId = properties.getProperty(serviceCode.toLowerCase() + "-" + serviceType.toLowerCase() + "-ws-clientid");
                    String wsMethod = properties.getProperty(serviceCode.toLowerCase() + "-" + serviceType.toLowerCase() + "-ws-method");
                    
                    if(mode.equalsIgnoreCase("CHECK")){
                        dataProcessor.wsInitialize(
                                wsHost
                                , wsPort
                                , wsPath
                                , wsUsername
                                , wsPassword
                                , wsClientId
                                , wsMethod
                                );                        
                        if(serviceType.equalsIgnoreCase("CHECKUSEREXISTS")){
//                            boolean result = dataProcessor.checkUserExists(moMsg);;
//                            logString = String.format("Msg sent to ws: %s\n", new Object[]{moMsg.debugString()});
//                            logBufferWriter.write(logString);
//                            if(result){
//                                responseStatusDesc = properties.getProperty("user-exist-mt-content");
//                            }
//                            else{
//                                responseStatusDesc = properties.getProperty("user-not-exist-mt-content");
//                            }
                        }
                        else if(serviceType.equalsIgnoreCase("MOFORWARD")){
//                            logString = String.format("Msg sent to ws: %s\n", new Object[]{moMsg.debugString()});
//                            logBufferWriter.write(logString);
//                            responseStatusDesc = dataProcessor.forwardMO(moMsg);
                        }
                        resultProcessingCode = "SUCCESS";
                    }
                    else{
                        dataProcessor.wsInitialize(
                                wsHost
                                , wsPort
                                , wsPath
                                , wsUsername
                                , wsPassword
                                , wsClientId
                                , wsMethod
                                );                        

                        if(serviceType.equalsIgnoreCase("CHECKUSEREXISTS")){
                            logString = String.format("%s - Starting to call ws checkUserExists: %s\n", new Object[]{logDateFormat.format(new Date()), moMsg.debugString()});
                            logBufferWriter.write(logString);
                            String result = dataProcessor.checkUserExists(moMsg);
                            logString = String.format("%s - Msg sent to ws: %s\n", new Object[]{logDateFormat.format(new Date()), moMsg.debugString()});
                            logBufferWriter.write(logString);
                            logString = String.format("%s - Response: %s\n", new Object[]{logDateFormat.format(new Date()), result});
                            logBufferWriter.write(logString);
                            if(result!=null && !result.equalsIgnoreCase("")){
                                try{
                                    if(serviceCode.equalsIgnoreCase("citv")){
                                        if(result.toUpperCase().contains("CITV_GOINGAY")){
                                            responseStatusDesc = properties.getProperty(serviceCode.toLowerCase() + "-user-exist-mt-content-" + serviceid.toLowerCase());
                                        }
                                        else if(result.toUpperCase().contains("CITV_GOITUAN")){
                                            responseStatusDesc = properties.getProperty(serviceCode.toLowerCase() + "-user-exist-mt-content-citv_goituan");
                                        }
                                    }
                                    else{
                                        responseStatusDesc = properties.getProperty(serviceCode.toLowerCase() + "-user-exist-mt-content");
                                    }
                                }
                                catch(Exception ex){
                                }
                                if(responseStatusDesc.contains("{{{packagecode}, {packagecode_price}}}")){
                                    responseStatusDesc= responseStatusDesc.replace("{{{packagecode}, {packagecode_price}}}", result);
                                }
                            }
                            else{
                                responseStatusDesc = properties.getProperty(serviceCode.toLowerCase() + "-user-not-exist-mt-content");
                            }
                        }
                        else if(serviceType.equalsIgnoreCase("MOFORWARD")){
                            logString = String.format("%s - Starting to call ws forwardMO: %s\n", new Object[]{logDateFormat.format(new Date()), moMsg.debugString()});
                            logBufferWriter.write(logString);
                            responseStatusDesc = dataProcessor.forwardMO(serviceCode,moMsg);;
                            logString = String.format("Msg sent to ws: %s\n", new Object[]{moMsg.debugString()});
                            logBufferWriter.write(logString);
                            logString = String.format("%s - Response: %s\n", new Object[]{logDateFormat.format(new Date()), responseStatusDesc});
                            logBufferWriter.write(logString);
                        }
                        else if(serviceType.equalsIgnoreCase("MOFORWARDGW")){
                            logString = String.format("%s - Starting to call ws forwardMOGW: %s\n", new Object[]{logDateFormat.format(new Date()), moMsg.debugString()});
                            logBufferWriter.write(logString);
                            responseStatusDesc = dataProcessor.forwardMOGW(serviceCode,moMsg);;
                            logString = String.format("Msg sent to ws: %s\n", new Object[]{moMsg.debugString()});
                            logBufferWriter.write(logString);
                            logString = String.format("%s - Response: %s\n", new Object[]{logDateFormat.format(new Date()), responseStatusDesc});
                            logBufferWriter.write(logString);
                        }
                        else if(serviceType.equalsIgnoreCase("MOFORWARD9029")){
                            logString = String.format("%s - Starting to call ws forwardMO: %s\n", new Object[]{logDateFormat.format(new Date()), moMsg.debugString()});
                            logBufferWriter.write(logString);
                            responseStatusDesc = dataProcessor.forwardMO(serviceCode,moMsg);;
                            logString = String.format("Msg sent to ws: %s\n", new Object[]{moMsg.debugString()});
                            logBufferWriter.write(logString);
                            logString = String.format("%s - Response: %s\n", new Object[]{logDateFormat.format(new Date()), responseStatusDesc});
                            logBufferWriter.write(logString);
                        }
                        resultProcessingCode = "SUCCESS";
                        
                        if(subServiceType.equalsIgnoreCase("DOWNLOADLOG")){
                            dataProcessor.wsHost = properties.getProperty(serviceCode.toLowerCase() + "-cg-log-ws-host");
                            dataProcessor.wsPath = properties.getProperty(serviceCode.toLowerCase() + "-cg-log-ws-path");
                            dataProcessor.wsPort = new Integer(properties.getProperty(serviceCode.toLowerCase() + "-cg-log-ws-port"));
                            dataProcessor.wsMethod = properties.getProperty(serviceCode.toLowerCase() + "-cg-log-ws-method");
                            MOMsg moCgLogMsg = dataProcessor.setWsMsgObjectGetContent(username, password, serviceid, msisdn, "", "DOWNLOAD", mode, amount, params,"DOWNLOAD");

                            logString = String.format("%s - Starting to call ws sendCGLog: %s\n", new Object[]{logDateFormat.format(new Date()), moCgLogMsg.debugString()});
                            logBufferWriter.write(logString);

                            String responseCgLog = dataProcessor.sendCGLog(serviceCode,moCgLogMsg);
                            logString = String.format("%s - Msg sent to ws: %s\n", new Object[]{logDateFormat.format(new Date()), moCgLogMsg.debugString()});
                            logBufferWriter.write(logString);
                            logString = String.format("%s - Response: %s\n", new Object[]{logDateFormat.format(new Date()), responseCgLog});
                            logBufferWriter.write(logString);
                        }
                        else if(subServiceType.equalsIgnoreCase("DOWNLOADLOGCDR")){
                            this.sendJMSObject(moMsg);
                            logString = String.format("%s - Msg sent to queue: %s\n", new Object[]{logDateFormat.format(new Date()), moMsg.debugString()});
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
            if (logBufferWriter != null) try { logBufferWriter.close(); } catch (IOException ex) {} 
            if (logFileWriter != null) try { logFileWriter.close(); } catch (IOException ex) {} 
        }

        return ret;
    }   
    */
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
