/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.centech.sdp.mps.server;

import centech.sdp.gameloft.game.pid.GamePIDLegal;
import centech.sdp.smsgw.common.APIResultObj;
import centech.sdp.smsgw.common.MO9029ParamsMobiFone;
import centech.sdp.smsgw.common.MOMsg;
import centech.sdp.smsgw.common.Msg;
import centech.sdp.smsgw.common.ObjProcess9029Result;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author Administrator
 */
public class process9029MO {
    BufferedWriter logBufferWriter = null;
    private Properties properties;
    DateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Socket wsSocket = null;
    public String wsHost = null;
    public int wsPort = 0;
    public String wsPath = null;
    public String wsUsername = null;
    private String wsPassword = null;
    public String wsMethod = null;
    public String wsDefaultOp = null;
    private String wsClientId = null;
    public String wsDefaultSrc = null;
    
    public process9029MO(BufferedWriter logBufferWriter, Properties properties){
        this.logBufferWriter = logBufferWriter;
        this.properties = properties;
    }
    
    public MO9029ParamsMobiFone ViettelTransformToMBFParams(String serviceid, String msisdn, String params, String amount, String command, String result){
        //VIETTEL: serviceid="GAMELOFT7", msisdn="982051117", params="gft 1000 a", mode="CHECK", amount="1000", command="gft 1000 a"
        //MBF: content_id="nap5|124", cpCode="GFT", gameCode="SKY", totalAmount="5000", account="abc", isdn="0936120577", result="WCG-0000"
        MO9029ParamsMobiFone objRet = new MO9029ParamsMobiFone();
        objRet.totalAmount = amount;
        objRet.msisdn = msisdn;
        objRet.result = result;
        try{
            String extraInfo = "";
            String priceLevelCode = "NAP"+this.getPriceLevelCode(amount);
            if(params!=null && !params.equalsIgnoreCase("")){
                String[] arrParamsItems = params.split(" ");
                for(int i=0;i<arrParamsItems.length;i++){
                    if(i==0){ objRet.cpCode = arrParamsItems[i];}
                    else if(i==2){ objRet.gameCode = arrParamsItems[i];}
                    else if(i==3){ objRet.account = arrParamsItems[i];}
                    else if(i>3){
                        if(!extraInfo.equalsIgnoreCase("")) extraInfo+=" ";
                        extraInfo+=arrParamsItems[i];
                    }
                }
            }
            
            objRet.content_id = priceLevelCode;
            if(!extraInfo.equalsIgnoreCase("")){
                objRet.content_id+="|"+extraInfo;
            }
        }
        catch(Exception ex){}
        
        
        return objRet;
    }

    
    public MO9029ParamsMobiFone VinaphoneTransformToMBFParams(Properties properties, String result){
        //String serviceid, String msisdn, String params, String amount, String command, String result
        //VIETTEL: serviceid="GAMELOFT7", msisdn="982051117", params="gft 1000 a", mode="CHECK", amount="1000", command="gft 1000 a"
        //VINAPHONE: 
//        <name>receiveresultgamecharging</name>
//        <msisdn>84919222400</msisdn>
//        <cpid>GFT</cpid>
//        <gamecode>GL</gamecode>
//        <totalamount>5000</totalamount>
//        <account>abc</account>
//        <result>GCG-0000</result>
//        <extinfo>124</extinfo>
//        <transactionno>GCG_20180321112155935</transactionno>
//        <gcgtransactionno>20180321112155936</gcgtransactionno>
//        <contentid>NAP5</contentid>
//        <application>NAP5</application>
//        <channel>NAP5</channel>
        //MBF: content_id="nap5|124", cpCode="GFT", gameCode="SKY", totalAmount="5000", account="abc", isdn="0936120577", result="WCG-0000"
        String cpid = properties.getProperty("cpid", "");
        String gamecode = properties.getProperty("gamecode", "");
        String contentid = properties.getProperty("contentid", "");
        String account = properties.getProperty("account", "");
        String extinfo = properties.getProperty("extinfo", "");
        
        //Viettel
        String msisdn = properties.getProperty("msisdn", "");
        String params = new StringBuilder().append(cpid).append(" ").append(gamecode).append(" ").append(contentid).append(" ").append(account).append(" ").append(extinfo).append(" ").toString();
        String amount = properties.getProperty("totalamount", "");
        String command = params;
                
        MO9029ParamsMobiFone objRet = new MO9029ParamsMobiFone();
        objRet.totalAmount = amount;
        objRet.msisdn = msisdn;
        objRet.result = result;
        try{
            String extraInfo = "";
            String priceLevelCode = "NAP"+this.getPriceLevelCode(amount);
            if(params!=null && !params.equalsIgnoreCase("")){
                String[] arrParamsItems = params.split(" ");
                for(int i=0;i<arrParamsItems.length;i++){
                    if(i==0){ objRet.cpCode = arrParamsItems[i];}
                    else if(i==2){ objRet.gameCode = arrParamsItems[i];}
                    else if(i==3){ objRet.account = arrParamsItems[i];}
                    else if(i>3){
                        if(!extraInfo.equalsIgnoreCase("")) extraInfo+=" ";
                        extraInfo+=arrParamsItems[i];
                    }
                }
            }
            
            objRet.content_id = priceLevelCode;
            if(!extraInfo.equalsIgnoreCase("")){
                objRet.content_id+="|"+extraInfo;
            }
        }
        catch(Exception ex){}
        
        
        return objRet;
    }
    
    private String getPriceLevelCode(String totalAmount){
        String ret = "";
        
        try{
            if(totalAmount!=null){
                if(totalAmount.length()==4){
                    ret = totalAmount.substring(0, 1);
                }
                else if(totalAmount.length()==5){
                    ret = totalAmount.substring(0, 2);
                }
                else if(totalAmount.length()==6){
                    ret = totalAmount.substring(0, 3);
                }
            }
        }
        catch(Exception ex){
            
        }
        
        return ret;
    }

    /*
     * validateParams
     */
    public boolean validateParams(
            String content_id, String cpCode, String gameCode, String totalAmount, String account, String isdn, String result) {
        boolean ret = true;

        try {
            if (content_id.equalsIgnoreCase("")) {
                logBufferWriter.write("content_id is empty");
                ret = false;
            }
            if (cpCode.equalsIgnoreCase("")) {
                logBufferWriter.write("cpCode is empty");
                ret = false;
            }
            if (gameCode.equalsIgnoreCase("")) {
                logBufferWriter.write("gameCode is empty");
                ret = false;
            }
            if (totalAmount.equalsIgnoreCase("")) {
                logBufferWriter.write("totalAmount is empty");
                ret = false;
            }
            if (account.equalsIgnoreCase("")) {
                logBufferWriter.write("account is empty");
                ret = false;
            }
            if (isdn.equalsIgnoreCase("")) {
                logBufferWriter.write("isdn is empty");
                ret = false;
            }
            if (result.equalsIgnoreCase("")) {
                logBufferWriter.write("result is empty");
                ret = false;
            }
        } catch (Exception ex) {
        }
        return ret;
    }
    

    private String getReqId() {
        String ret = "";

        try {
            Date now = new Date();
            SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String randomStr = this.getRandomNoLeadingZero(4);

            ret = fileNameDateFormat.format(now) + randomStr;
        } catch (Exception ex) {
        }

        return ret;
    }

    public String getResultCode(String result) {
        String ret = "";

        try {
            if(result.equalsIgnoreCase("WCG-0000")){
                ret = "0";
            }
            else{
                ret = "1";
            }
        } catch (Exception ex) {
        }

        return ret;
    }
    
    public String getRandomNoLeadingZero(int numberLeading) {
        String ret = "";

        try {
            String numberGetRandomStr = "1";
            for (int i = 0; i < numberLeading; i++) {
                numberGetRandomStr += "0";
            }

            int numberGetRandom = Integer.parseInt(numberGetRandomStr);
            Random r = new Random();
            ret = ("" + (numberGetRandom + r.nextInt(numberGetRandom))).substring(1);
        } catch (Exception ex) {
        }

        return ret;
    }

    public MOMsg setWsMsgObject(
            String request_id
            , String content_id, String cpCode, String ContentCode, String totalAmount, String account, String msisdn, int result, String extraInfo, String param_cp_report_code, String param_service_report_code, String param_shortcode
            , String param_event_id
            , String param_channel
            , String param_cp_id
            , String keyword
            , String gameCode
        ) {
        MOMsg moMsg = new MOMsg();

        if (!msisdn.startsWith("84")) {
            if (msisdn.startsWith("04")) {
                msisdn = "84" + msisdn.substring(2);
            } else if (msisdn.startsWith("0")) {
                msisdn = "84" + msisdn.substring(1);
            } else {
                msisdn = "84" + msisdn;
            }
        }
        
        moMsg.msgType = Msg.MsgType.MO;
        moMsg.moRxTime = new Date();

        moMsg.content = account;
        moMsg.opId = cpCode;
        moMsg.svcId = ContentCode;
        moMsg.svcName = totalAmount;
        moMsg.msisdn = msisdn;
        moMsg.txStatus = result;
        moMsg.extraMOInfo = extraInfo;
        moMsg.moId = this.getReqId();
        moMsg.opCnxId = param_cp_report_code;
        moMsg.cpMODeliveryId = param_service_report_code;
        moMsg.moTxId = param_event_id;
        moMsg.opMTDeliveryId = param_channel;
        moMsg.cpId = param_cp_id;

        moMsg.shortCode = param_shortcode;
        moMsg.keyword = keyword;
        moMsg.campaignId = content_id;
        moMsg.campaignCode = gameCode;
        
        return moMsg;
    }    
//    
    public com.centech.smsgw.common.MOMsg setWsMOLogObject(
            String requestId, String content_id, String cpCode, String ContentCode, String totalAmount, String account, String msisdn, int result, String extraInfo, String param_cp_report_code, String param_service_report_code, String param_shortcode
            , String param_event_id
            , String param_channel
            , String param_cp_id
            , String keyword
            , String gameCode
            , String org_msgbody
        ) {
        com.centech.smsgw.common.MOMsg moMsg = new com.centech.smsgw.common.MOMsg();

        if (!msisdn.startsWith("84")) {
            if (msisdn.startsWith("04")) {
                msisdn = "84" + msisdn.substring(2);
            } else if (msisdn.startsWith("0")) {
                msisdn = "84" + msisdn.substring(1);
            } else {
                msisdn = "84" + msisdn;
            }
        }

        String moContent = org_msgbody;//new StringBuilder().append(keyword).append(" ").append(gameCode).append(" ").append(content_id).append(" ").append(account).append(" ").append(extraInfo).toString();

        moMsg.msgType = com.centech.smsgw.common.Msg.MsgType.MOLOG;
        moMsg.moRxTime = new Date();

        moMsg.txStatus = -1;
        try{
            moMsg.content = URLEncoder.encode(moContent,"UTF-8");
        }
        catch(Exception ex){}
        moMsg.opId = this.wsDefaultOp;
        moMsg.svcId = ContentCode;
        moMsg.svcName = ContentCode;
        moMsg.msisdn = msisdn;
        moMsg.moId = requestId;
        moMsg.opCnxId = param_cp_report_code;
        moMsg.cpMODeliveryId = param_service_report_code;
        moMsg.moTxId = param_event_id;
        moMsg.opMTDeliveryId = param_channel;
        moMsg.cpId = param_cp_id;

        moMsg.shortCode = param_shortcode;
        moMsg.keyword = keyword;
        moMsg.contentType = com.centech.smsgw.common.Msg.ContentType.TEXT_GSM;

        return moMsg;
    }    
    
    public com.centech.smsgw.common.MTMsg setWsMTLogObject(
            String requestId, String content_id, String cpCode, String ContentCode, String totalAmount, String account, String msisdn, int result, String extraInfo, String param_cp_report_code, String param_service_report_code, String param_shortcode
            , String param_event_id
            , String param_channel
            , String param_cp_id
            , String keyword
            , String gameCode
            , String mtContent
        ) {
        com.centech.smsgw.common.MTMsg mtMsg = new com.centech.smsgw.common.MTMsg();

        if (!msisdn.startsWith("84")) {
            if (msisdn.startsWith("04")) {
                msisdn = "84" + msisdn.substring(2);
            } else if (msisdn.startsWith("0")) {
                msisdn = "84" + msisdn.substring(1);
            } else {
                msisdn = "84" + msisdn;
            }
        }

        mtMsg.msgType = com.centech.smsgw.common.Msg.MsgType.MTLOG;
        mtMsg.mtRxTime = new Date();

        mtMsg.txStatus = -1;
        try{
            mtMsg.content = URLEncoder.encode(mtContent,"UTF-8");
        }
        catch(Exception ex){}
        mtMsg.opId = this.wsDefaultOp;
        mtMsg.svcId = ContentCode;
        mtMsg.svcName = ContentCode;
        mtMsg.msisdn = msisdn;
        mtMsg.moId = requestId;
        mtMsg.opCnxId = param_cp_report_code;
        mtMsg.cpMODeliveryId = param_service_report_code;
        mtMsg.moTxId = param_event_id;
        mtMsg.opMTDeliveryId = param_channel;
        mtMsg.cpId = param_cp_id;

        mtMsg.shortCode = param_shortcode;
        mtMsg.keyword = keyword;
        
        mtMsg.mtId = Utils.genMTSequence18digits();
        mtMsg.contentType = com.centech.smsgw.common.Msg.ContentType.TEXT_GSM;

        return mtMsg;
    }    

    protected void wsInitialize(String host, String port, String path, String username, String password, String clientId, String method, String defaultOp, String wsDefaultSrc) {
        this.wsHost = host;
        this.wsPort = new Integer(port);
        this.wsPath = path;
        this.wsUsername = username;
        this.wsPassword = password;
        this.wsClientId = clientId;
        this.wsMethod = method;
        this.wsDefaultOp = defaultOp;
        this.wsDefaultSrc = wsDefaultSrc;
    }

    
    public ObjProcess9029Result processMO9029_MobiFone(
            String content_id
            , String cpCode
            , String gameCode
            , String totalAmount
            , String account
            , String msisdn
            , String result
            , String org_msgbody
            ) {
        ObjProcess9029Result objReturn = new ObjProcess9029Result();
        
        Date now = new Date();
        String ret = "";
        String responseResult = "";
        String logString = "";
        String param_extrainfo = "";
        int param_result_code = ResponseStatusCode.getResponseStatusCode("");
        String requestId = Utils.genMTSequence18digits();

        try {
            logString = String.format("%s - [processMO9029_MobiFone] Request received: content_id=\"%s\", cpCode=\"%s\", gameCode=\"%s\", totalAmount=\"%s\", account=\"%s\", isdn=\"%s\", result=\"%s\"\n", new Object[] { logDateFormat.format(now), content_id, cpCode, gameCode, totalAmount, account, msisdn, result });
            logBufferWriter.write(logString);
            
            DataProcessor dataProcessor = new DataProcessor(logBufferWriter, properties);
            if(content_id!=null && content_id.contains("|")){
                try{
                    String[] arrItems = content_id.split("\\|");
                    if(arrItems.length==2){
                        content_id = arrItems[0];
                        param_extrainfo = arrItems[1];
                    }
                }
                catch(Exception ex){
                }
            }
            if (this.validateParams(content_id, cpCode, gameCode, totalAmount, account, msisdn, result)) {
                String param_event_id=properties.getProperty("SHORTCODE-9029-EVENTID-"+cpCode.toUpperCase()+"-CMD-"+content_id.toUpperCase());
                String param_channel = properties.getProperty("SHORTCODE-9029-"+cpCode.toUpperCase()+"-PARAM-CHANNEL");
                String param_cp_id = properties.getProperty("SHORTCODE-9029-"+cpCode.toUpperCase()+"-PARAM-CPID");
                String param_content_id = properties.getProperty("SHORTCODE-9029-"+gameCode.toUpperCase()+"-PARAM-CONTENTID");
                //cp report code
                String param_shortcode = properties.getProperty("SHORTCODE-9029-"+cpCode.toUpperCase()+"-REPORT-SHORTCODE");
                //cp report code
                String param_cp_report_code = properties.getProperty("SHORTCODE-9029-"+cpCode.toUpperCase()+"-REPORT-CPCODE");
                //service report code
                String param_service_report_code = properties.getProperty("SHORTCODE-9029-"+cpCode.toUpperCase()+"-REPORT-SERVICECODE");
                //get result code
                param_result_code = ResponseStatusCode.getResponseStatusCode(result);
                //check extra info in command param
                //--------------------------------
//            logBufferWriter.write("param_result_code: " + param_result_code);
                String serviceTypeInfo = properties.getProperty("SHORTCODE-9029-TYPE-CMD-"+content_id.toUpperCase());
//            logBufferWriter.write("serviceTypeInfo: " + serviceTypeInfo);

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
                
                objReturn.serviceType = serviceType;
                
                if(serviceType.equalsIgnoreCase("MO-BILLING")){
                    //record charging billing
                    MOMsg moMsg = null;
                    com.centech.smsgw.common.MOMsg moLogMsg = null;
                    
                    //send to queue
                    if(param_result_code==ResponseStatusCode.getSUCCESS()){
                        //ws api
                        String wsHost = Utils.getStrProperty(properties, cpCode.toLowerCase() + "-" + serviceType.toLowerCase() + "-ws-host", "");
                        String wsPort = Utils.getStrProperty(properties, cpCode.toLowerCase() + "-" + serviceType.toLowerCase() + "-ws-port", "0");
                        String wsPath = Utils.getStrProperty(properties, cpCode.toLowerCase() + "-" + serviceType.toLowerCase() + "-ws-path", "");
                        String wsUsername = Utils.getStrProperty(properties, cpCode.toLowerCase() + "-" + serviceType.toLowerCase() + "-ws-username", "");
                        String wsPassword = Utils.getStrProperty(properties, cpCode.toLowerCase() + "-" + serviceType.toLowerCase() + "-ws-password", "");
                        String wsClientId = Utils.getStrProperty(properties, cpCode.toLowerCase() + "-" + serviceType.toLowerCase() + "-ws-clientid", "");
                        String wsMethod = Utils.getStrProperty(properties, cpCode.toLowerCase() + "-" + serviceType.toLowerCase() + "-ws-method", "");
                        String wsDefaultOp = Utils.getStrProperty(properties, cpCode.toLowerCase() + "-" + serviceType.toLowerCase() + "-ws-default-operator", "");
                        String wsDefaultSrc = Utils.getStrProperty(properties, cpCode.toLowerCase() + "-" + serviceType.toLowerCase() + "-ws-default-src", "");
                        
                        this.wsInitialize(
                                wsHost
                                , wsPort
                                , wsPath
                                , wsUsername
                                , wsPassword
                                , wsClientId
                                , wsMethod
                                , wsDefaultOp
                                , wsDefaultSrc
                                );                        

                        moMsg = this.setWsMsgObject(requestId, content_id, cpCode, param_content_id, totalAmount, account, msisdn, param_result_code, param_extrainfo, param_cp_report_code,param_service_report_code, param_shortcode, param_event_id, param_channel, param_cp_id, cpCode, gameCode);
                        moMsg.moRxId = org_msgbody;
                        moLogMsg = this.setWsMOLogObject(requestId, content_id, cpCode, param_content_id, totalAmount, account, msisdn, param_result_code, param_extrainfo, param_cp_report_code,param_service_report_code, param_shortcode, param_event_id, param_channel, param_cp_id, cpCode, gameCode, org_msgbody);                    

                        logString = String.format("%s - Starting to call ws mo billing to cp: %s\n", new Object[]{logDateFormat.format(new Date()), moMsg.debugString()});
                        logBufferWriter.write(logString);

                        APIResultObj apiResultObj = this.forwardMOBilling(moMsg);

                        logString = String.format("%s - Msg sent to ws: %s\n", new Object[]{logDateFormat.format(new Date()), moMsg.debugString()});
                        logBufferWriter.write(logString);

                        logString = String.format("%s - Response: %s\n", new Object[]{logDateFormat.format(new Date()), (apiResultObj._apiResult == null ? "null" : apiResultObj._apiResult)});
                        logBufferWriter.write(logString);

                        //process result
                        if(apiResultObj!=null && apiResultObj._apiResult!=null && !apiResultObj._apiResult.equalsIgnoreCase("")){
                            moLogMsg.txStatus = 0;
                            try{                                
                                //check PID
                                MTPIDObj objMTPID = this.checkMTWithPID(apiResultObj._mtContentToEndUser, cpCode);
                                apiResultObj._mtContentToEndUser = objMTPID._mtContentToEndUser;
                                moMsg.svcId = objMTPID._PID;
                                moMsg.extraMOInfo = objMTPID._contentName;
                                //-------------------
                                responseResult = apiResultObj._mtContentToEndUser;// properties.getProperty(cpCode.toLowerCase() + "-mt-content-success");
                                if(responseResult.contains("{content_name}")){
                                    responseResult = responseResult.replace("{content_name}", apiResultObj._contentName);
                                }
                                if(responseResult.contains("{content_link}")){
                                    responseResult = responseResult.replace("{content_link}", apiResultObj._contentLink);
                                }
                                if(responseResult.contains("{content_price}")){
                                    String contentPrice = apiResultObj._contentPrice;
                                    try{
                                        contentPrice = properties.getProperty("MBF-9029-PRICE-CMD-" + content_id.toUpperCase());
                                    }
                                    catch(Exception ex){}
                                    
                                    responseResult = responseResult.replace("{content_price}", contentPrice);
                                }          
                                //----------------------------------------
                            }
                            catch(Exception ex){
                            }
                            com.centech.smsgw.common.MTMsg mtLogMsg = this.setWsMTLogObject(requestId, content_id, cpCode, param_content_id, totalAmount, account, msisdn, param_result_code, param_extrainfo, param_cp_report_code,param_service_report_code, param_shortcode, param_event_id, param_channel, param_cp_id, cpCode, gameCode, responseResult);
                            objReturn.mtLogMsg = mtLogMsg;
                        }
                        else{
                            responseResult = Utils.getStrProperty(properties, cpCode.toLowerCase() + "-ws-not-response-default-return", "");
                        }
                    }
                    
                    objReturn.moMsg = moMsg;
                    objReturn.moLogMsg = moLogMsg;
                }
            } else {
                responseResult = properties.getProperty(cpCode.toLowerCase() + "-ws-wrong-input-param-default-return");
            }
//        
        } catch (Exception ex) {
            try {
                logString = String.format("%s - MoReceiver9029(): Exception: %s\n",new Object[]{logDateFormat.format(new Date()), ex.getMessage()}) ;
                logBufferWriter.write(logString);
            } catch (IOException ex1) {
                Logger.getLogger(process9029MO.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            //return
            ret = responseResult;

            logString = String.format("%s - MoReceiver9029(): Return string for msisdn %s: \"%s\"\n", new Object[]{logDateFormat.format(new Date()), msisdn, "[" + ret + "]"});
            try {
                logBufferWriter.write(logString);
            } catch (IOException ex) {
            }
        }

        objReturn.responseStatusDesc = ret;
        return objReturn;
    }        
    
    protected MTPIDObj checkMTWithPID(String mtContent, String cpCode){
        MTPIDObj ret = new MTPIDObj();
        ret._mtContentToEndUser = mtContent;
        
        try{
            if(Utils.getStrProperty(properties, cpCode.toLowerCase() + "-mt-pid-enable", "").equalsIgnoreCase("true")){
                if(mtContent!=null && !mtContent.equalsIgnoreCase("")){
                    logBufferWriter.write("MT Original: " + mtContent);
                    String pId = "";
                    String pIdDefault = "1";
                    String titleByPID = "";
                    String mtContentToEndUser = mtContent;
                    
                    String[] arrMTContent = mtContent.split(" ");
                    
                    if(arrMTContent!=null && arrMTContent.length>0){
                        pId = arrMTContent[0];
                    }
                    
                    if(!pId.equalsIgnoreCase("")){
                        titleByPID = this.getTitleByPID(pId);
                        
                        if(!titleByPID.equalsIgnoreCase("")){
                            mtContentToEndUser = titleByPID;
                            for(int i=1;i<arrMTContent.length;i++){
                                if(!mtContentToEndUser.equalsIgnoreCase("")){
                                    mtContentToEndUser = String.format("%s ",new Object[]{mtContentToEndUser}) ;
                                }
                                mtContentToEndUser = String.format("%s%s",new Object[]{mtContentToEndUser,arrMTContent[i]}) ;
                            }
                        }
                    }
                    
                    ret._PID = pId;
                    ret._contentName = titleByPID;
                    ret._mtContentToEndUser = mtContentToEndUser;
                    logBufferWriter.write("MT modified: " + ret);                    
                }
                        
            }        
        }
        catch(Exception ex){
            try {
                logBufferWriter.write("Exception on checkMTWithPID: " + ex.getMessage());
            } catch (IOException ex1) {
            }
        }
        
        return ret;
    }
    
    protected String getTitleByPID(String pId){
        String pIdDefault = "1";
        String ret = "";
        
        try{
            String titleByPID = Utils.getPropertyStr(GamePIDLegal.pidMap,pId,"");
            if(titleByPID.equalsIgnoreCase("")){
                int rndIndex = new Random().nextInt(GamePIDLegal.arrPIDDefault.length);
                pIdDefault = GamePIDLegal.arrPIDDefault[rndIndex];
                titleByPID = Utils.getPropertyStr(GamePIDLegal.pidMap,pIdDefault,"");
            }
            
            ret = titleByPID;
        }
        catch(Exception ex){
            try {
                logBufferWriter.write("Exception getTitleByPID: " + ex.getMessage() + "\n");
            } catch (IOException ex1) {
                Logger.getLogger(DataProcessor.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        
        return ret;
    }

    protected APIResultObj forwardMOBilling(MOMsg moMsg) {
        APIResultObj ret = new APIResultObj();
        try {
            String[] responseInfo = this.sendHTTPRequest(this.wsMethod, true, this.wsHost, this.wsPort, this.wsPath, moMsg, "mo-billing");// this.sendRequest(moMsg,"mo-billing");
            String responseText = responseInfo[1];
            logBufferWriter.write(String.format("Response: %s\n", new Object[]{responseText}));
            
            //for testing
//            responseText = "<RPLY name='mo_response'>\n" +
//            "<msisdn>84936120577</msisdn >\n" +
//            "<contentName>Sky</contentName>\n" +
//            "<contentLink>https://gameloft.com/</contentLink>\n" +
//            "<responseCode>200</responseCode>\n" +
//            "<moSeq>201803071217021899</moSeq>\n" +
//            "</RPLY>";
            if(responseText!=null
                    && !responseText.equalsIgnoreCase("")){
                Properties requestParamProperties = this.getXMLElementResponse(responseText);
                if(requestParamProperties!=null){
                    ret._apiResult = responseText;
                    ret._contentName = requestParamProperties.getProperty("contentName");
                    ret._contentLink = requestParamProperties.getProperty("contentLink");
                    ret._contentPrice = moMsg.svcName;
                    ret._mtContentToEndUser = requestParamProperties.getProperty("mtContent");
                }
            }
        } catch (Exception ex) {
        }
        return ret;
    }
    
    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }
    
    protected Properties getXMLElementResponse(String responseText){
        Properties requestParamProperties = new Properties();
        try{
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(responseText));

            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("RPLY");
            
            String[] arrKeys = {"msisdn","contentName","contentLink","responseCode","moSeq","mtContent"};
            if(nodes!=null){
                Element element = (Element) nodes.item(0);                
                for(String key : arrKeys){
                    NodeList nodeListElement = element.getElementsByTagName(key);
                    Element line = (Element) nodeListElement.item(0);
                    String val = this.getCharacterDataFromElement(line);

                    requestParamProperties.setProperty(key, val);
                }
            }
        }
        catch(Exception ex){
            try {
                logBufferWriter.write("Exception parsing XML: " + ex.getMessage() + "\n");
            } catch (IOException ex1) {
                Logger.getLogger(DataProcessor.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }        
        
        return requestParamProperties;
    }
    
    /**
     * Ham thiet lap Certificate SSL Duoc su dung khi can goi toi mot url co
     * giao thuc ssl
     *
     * @throws Exception Bung loi khi co loi
     */
    public static void installMyPolicy() throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs,
                        String authType) {
                    // Trust always
                }

                public void checkServerTrusted(X509Certificate[] certs,
                        String authType) {
                    // Trust always
                }
            }};
        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        // Create empty HostnameVerifier
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        };
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
    
    public String[] sendHTTPRequest(String typeOfRequest, boolean blGetHTTPResponseText, String rqHost, int rqPort, String rqPath, MOMsg moMsg, String moType)  {
        String[] ret = new String[2];
        int httpStatusCode = -1;
        
        BufferedReader bufferedReader = null;
        InputStream input = null;
        PrintWriter printWriter = null;
        OutputStream output = null;
        String respOutputContent = "";
        
        try {
            this.wsSocket = new Socket(this.wsHost, this.wsPort);
            logBufferWriter.write(new StringBuilder().append("Connected to ").append(this.wsSocket.getInetAddress()).append(":").append(this.wsSocket.getPort()).append("\n").toString());
            
            logBufferWriter.write(new StringBuilder().append("Connecting to ").append(rqHost).append(":").append(Integer.toString(rqPort)).append(" ...").append("\n").toString());
            if(rqPort==443){//SSL
                try {
                    installMyPolicy();
                } catch (Exception ex) {
                    logBufferWriter.write("Exception on installing Policy: " + ex.getMessage());
                }
                SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                this.wsSocket = (SSLSocket) factory.createSocket(rqHost, rqPort);
            }
            else{
                this.wsSocket = new Socket(rqHost, rqPort);
            }
            logBufferWriter.write(new StringBuilder().append("Connected to ").append(this.wsSocket.getInetAddress()).append(":").append(this.wsSocket.getPort()).toString());
        } catch (Exception ex) {
            try {
                logBufferWriter.write(new StringBuilder().append("Cannot connect to ").append(rqHost).append(":").append(rqPort).append(". ").append(ex).toString());
            } catch (IOException ex1) {
                Logger.getLogger(DataProcessor.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }        

        if (this.wsSocket != null) {
            String request = null;
            try {
                if(moType.equalsIgnoreCase("mo-billing")){
                    request = this.getRequestMOBilling(moMsg);
                }
                logBufferWriter.write(new StringBuilder().append("Request = [").append(request).append("]").toString());
            } catch (Exception ex) {
            }
            if (request == null) {
                try {
                    logBufferWriter.write(new StringBuilder().append("Request = null. Request DROPPED!!!").toString());
                } catch (IOException ex) {
                    Logger.getLogger(DataProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    output = this.wsSocket.getOutputStream();
                    printWriter = new PrintWriter(output);
                    printWriter.print(request);
                    printWriter.flush();
                    logBufferWriter.write("Request sent");
                    
                    try {
                        input = this.wsSocket.getInputStream();
                        
                        bufferedReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                  
                        httpStatusCode = this.getHttpStatusCode(bufferedReader);
                        if(blGetHTTPResponseText){
                            respOutputContent = this.getHttpResponseText(bufferedReader);
                        }
                        logBufferWriter.write(new StringBuilder().append("Http response status = ").append(httpStatusCode).toString());
                        logBufferWriter.write(new StringBuilder().append("respOutputContent = ").append(respOutputContent).toString());
                    } catch (Exception ex) {
                        logBufferWriter.write(new StringBuilder().append("Exception reading response: ").append(ex).toString());
                    }
                } catch (Exception ex) {
                    try {
                        logBufferWriter.write(new StringBuilder().append("Request NOT sent. Exception writing to socket: ").append(ex).toString());
                    } catch (IOException ex1) {
                        Logger.getLogger(DataProcessor.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
                finally{
//                    try{
//                        if(this.wsSocket!=null) this.wsSocket.close();
//                    }
//                    catch(Exception ex){
//                        
//                    }
                }
            }
        }
        
        ret[0] = new Integer(httpStatusCode).toString();
        ret[1] = respOutputContent;

        return ret;
    }          

    
    public int getHttpStatusCode(BufferedReader buffReader) {
        int statusCode = -1;
        String line = null;
        if (buffReader != null) {
            boolean cont = true;
            while (cont) {
                cont = false;
                line = null;
                try {
                    try {
                        line = buffReader.readLine();
                    } catch (Exception ex) {
                        logBufferWriter.write(new StringBuilder().append("Exception reading new line: ").append(ex).append("\n").toString());
                        line = null;
                    }
                    if (line != null) {
                        if (line.contains("HTTP/1.")) {
                            logBufferWriter.write(new StringBuilder().append("Response received: [").append(line).append("]").append("\n").toString());
                        } else {
                            logBufferWriter.write(new StringBuilder().append("Response received: [").append(line).append("]").append("\n").toString());
                        }
                        if (statusCode == -1) {
                            String[] items = line.split("[ ]+");
                            try {
                                if ((items != null) && (items.length > 2) && (items[0].matches("HTTP/[0-9].[0-9]")) && (items[1].matches("[0-9]+"))) {
                                    statusCode = Integer.parseInt(items[1]);
                                }
                            } catch (Exception ex) {
                                logBufferWriter.write(new StringBuilder().append("Exception parsing status code: ").append(ex).append("\n").toString());
                            }
                        }

                        if (!"".equals(line)) {
                            cont = true;
                        }
                    }
                } catch (Exception ex) {
                    try {
                        logBufferWriter.write(new StringBuilder().append("Exception getting status code: ").append(ex).append("\n").toString());
                    } catch (Exception exx) {
                    }
                }
            }
        }
        return statusCode;
    }

    public String getHttpResponseText(BufferedReader buffReader) {
        String ret = "";
        String line = null;
        if (buffReader != null) {
            boolean cont = true;
            boolean blStartRecord = false;
            while (cont) {
                cont = false;
                line = null;
                try {
                    try {
                        line = buffReader.readLine();
                    } catch (Exception ex) {
                        logBufferWriter.write(new StringBuilder().append("Exception reading new line: ").append(ex).append("\n").toString());
                        line = null;
                    }
                    if (line != null) {
                        logBufferWriter.write(new StringBuilder().append("line: ").append(line).append("\n").toString());
                        if(line.startsWith("<?xml version=\"1.0\"?>")){
                            blStartRecord = true;
                        }
                        if(blStartRecord) ret = ret + line;
                        if(line.startsWith("</RPLY>")){
                            blStartRecord = false;
                        }

                        if (!"".equals(line)) {
                            cont = true;
                        }
                    }
                } catch (Exception ex) {
                    try {
                        logBufferWriter.write(new StringBuilder().append("Exception getting status code: ").append(ex).append("\n").toString());
                    } catch (Exception exx) {
                    }
                }
            }
        }
        return ret;
    }
    
    protected String getRequestMOBilling(MOMsg moMsg) {
        String request = null;
        try {
            if (moMsg == null) {
                return null;
            }

            InetAddress ipClient = null;
            try {
                ipClient = InetAddress.getLocalHost();
            } catch (UnknownHostException ex) {
                logBufferWriter.write(new StringBuilder().append("Unkown host: ").append(ex).append("\n").toString());
            }
            
            String content_id = moMsg.campaignId;
            String gameCode = moMsg.campaignCode;
            String cpCode = moMsg.opId;
            String ContentCode = moMsg.svcId;
            String totalAmount = moMsg.svcName;
            String account = moMsg.content;
            String msisdn = moMsg.msisdn;
            String result = new Integer(moMsg.txStatus).toString();        
            String extraInfo = moMsg.extraMOInfo;
            String moId = moMsg.moId;
            String keyword = moMsg.keyword;
            String defaultOp = (this.wsDefaultOp==null ? "" : this.wsDefaultOp); 
            String defaultSrc = (this.wsDefaultSrc==null ? "" : this.wsDefaultSrc); 
            String command = new StringBuilder().append(keyword).append(" ").append(gameCode).append(" ").append(content_id).append(" ").append(account).append(" ").append(extraInfo).toString();
            String org_msgbody = moMsg.moRxId;

            Properties requestProperties = new Properties();
            requestProperties.setProperty("username", this.wsUsername == null ? "" : URLEncoder.encode(this.wsUsername, "UTF-8"));
            requestProperties.setProperty("password", this.wsPassword == null ? "" : URLEncoder.encode(this.wsPassword, "UTF-8"));
            requestProperties.setProperty("contentid", URLEncoder.encode(ContentCode, "UTF-8"));
            requestProperties.setProperty("extraInfo", URLEncoder.encode(extraInfo, "UTF-8"));
            requestProperties.setProperty("moseq", moMsg.moId);
            requestProperties.setProperty("src", URLEncoder.encode(msisdn, "UTF-8"));
            requestProperties.setProperty("dest", URLEncoder.encode(defaultSrc, "UTF-8"));
            requestProperties.setProperty("price", totalAmount);
            requestProperties.setProperty("procresult", URLEncoder.encode(result, "UTF-8"));
            requestProperties.setProperty("keyword", URLEncoder.encode(keyword, "UTF-8"));
            requestProperties.setProperty("command", URLEncoder.encode(org_msgbody, "UTF-8"));
            requestProperties.setProperty("account", URLEncoder.encode(account, "UTF-8"));
            requestProperties.setProperty("opid", defaultOp);

            String requestData = "";
            String seperator = "&";
            Iterator iter = requestProperties.keySet().iterator();
            String key = null;
            String value = null;
            while (iter.hasNext()) {
                key = (String) iter.next();
                if (key != null) {
                    value = requestProperties.getProperty(key);
                    requestData = new StringBuilder().append(requestData).append((requestData.equalsIgnoreCase("") ? "" : seperator)).append(URLEncoder.encode(key, "UTF-8")).append("=").append(value == null ? "" : value).toString();

                    seperator = "&";
                }

            }
            String endline = "\r\n";

            if ("GET".equalsIgnoreCase(this.wsMethod)) {
                request = new StringBuilder().append("GET ").append(this.getForwarderPath(this.wsPath)).append("").append(requestData).append(" ").append("HTTP/1.1").append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append("Host: ").append(this.wsHost).append(this.wsPort != 80 ? new StringBuilder().append(":").append(this.wsPort).toString() : "").append(endline).toString();

                request = new StringBuilder().append(request).append(endline).toString();
            } else if ("POST".equalsIgnoreCase(this.wsMethod)) {
                request = new StringBuilder().append("POST ").append(this.getForwarderPath(this.wsPath)).append(" ").append("HTTP/1.1").append(endline).toString();
                request = new StringBuilder().append(request).append("Host: ").append(this.wsHost).append(this.wsPort != 80 ? new StringBuilder().append(":").append(this.wsPort).toString() : "").append(endline).toString();
                request = new StringBuilder().append(request).append("Content-Length: ").append(requestData.length()).append(endline).toString();
                request = new StringBuilder().append(request).append("Content-Type: application/x-www-form-urlencoded").append(endline).toString();
                request = new StringBuilder().append(request).append(endline).toString();
                request = new StringBuilder().append(request).append(requestData).toString();
            } else {
                logBufferWriter.write(new StringBuilder().append("Unsupported HTTP method: ").append(this.wsMethod).append("\n").toString());
            }
        } catch (Exception ex) {
            try {
                logBufferWriter.write(new StringBuilder().append("Exception getting request: ").append(ex).append("\n").toString());
            } catch (Exception exx) {
            }
        }
        return request;
    }    
    

    private String getForwarderPath(String path) {
        String ret = path;

        try {
            if (this.wsMethod.equalsIgnoreCase("GET")) {
                if (ret.endsWith("?")) {
                    ret += "";
                } else if (ret.contains("?")) {
                    ret += "";
                } else {
                    ret += "?";
                }
            }
        } catch (Exception ex) {
        }

        return ret;
    }
    
}
