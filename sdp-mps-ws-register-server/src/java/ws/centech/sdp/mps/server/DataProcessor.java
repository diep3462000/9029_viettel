/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.centech.sdp.mps.server;

import centech.sdp.smsgw.common.MOMsg;
import centech.sdp.smsgw.common.Msg;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class DataProcessor {

    public static final Logger logger = Logger.getLogger("MODataProcessor");
    private Socket wsSocket = null;
    public String wsHost = null;
    public int wsPort = 0;
    public String wsPath = null;
    public String wsUsername = null;
    private String wsPassword = null;
    public String wsMethod = null;
    private String wsClientId = null;
    private BufferedWriter logBufferWriter = null;
    private DateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Properties _properties = new Properties();
    
    public DataProcessor() {
    }

    public DataProcessor(BufferedWriter logBufferWriter) {
        this.logBufferWriter = logBufferWriter;
    }
    
    public DataProcessor(BufferedWriter logBufferWriter, Properties properties) {
        this.logBufferWriter = logBufferWriter;
        this._properties = properties;
    }
    
    protected boolean validateMsisdnPreffix(String msisdn){
        boolean ret = false;
        
        try{
            logBufferWriter.write("[validateMsisdnPreffix] validating " + msisdn + " ...\n");
            if (!msisdn.startsWith("84")) {
                if (msisdn.startsWith("04")) {
                    msisdn = "84" + msisdn.substring(2);
                } else if (msisdn.startsWith("0")) {
                    msisdn = "84" + msisdn.substring(1);
                } else {
                    msisdn = "84" + msisdn;
                }
            }
            String[] preffixMsisdnList = Utils.getStrProperty(this._properties,"msisdn-preffix-list","").split(",");

            for(String preffixMsisdn : preffixMsisdnList){
                try{
                    String userMsisdnPreffix = msisdn.substring(0,preffixMsisdn.length());
//            logBufferWriter.write("preffixMsisdn: " + preffixMsisdn + "\n");
//            logBufferWriter.write("userMsisdnPreffix: " + userMsisdnPreffix + "\n");
                    if(userMsisdnPreffix.equalsIgnoreCase(preffixMsisdn)){
                        ret = true;
                        break;
                    }
                }
                catch(Exception ex){
                    
                }
            }
        }
        catch(Exception ex){
            try {
                logBufferWriter.write("[validateMsisdnPreffix] Exception" + ex.getMessage() + "\n");
            } catch (IOException ex1) {
                Logger.getLogger(DataProcessor.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        try {
            logBufferWriter.write("[validateMsisdnPreffix] result: " + ret + "\n");
        } catch (IOException ex) {
            Logger.getLogger(DataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ret;
    }

    /*
     * validateParams
     */
    public boolean validateParams(
            String username, String password, String serviceid, String msisdn, String chargetime, String params, String mode, String amount, String command) {
        boolean ret = true;

        try {
            logBufferWriter.write("[validateParams] validating...\n");

            if (serviceid.equalsIgnoreCase("")) {
                logger.warning("serviceid is empty");
                ret = false;
            }
            if (msisdn.equalsIgnoreCase("")) {
                logger.warning("msisdn is empty");
                ret = false;
            }
            else{
                if(!this.validateMsisdnPreffix(msisdn)){
                    ret = false;
                }
            }
            
            if (chargetime.equalsIgnoreCase("")) {
                logger.warning("chargetime is empty");
                ret = false;
            }
            if (params.equalsIgnoreCase("")) {
                logger.warning("params is empty");
                ret = false;
            }
            if (mode.equalsIgnoreCase("")) {
                logger.warning("mode is empty");
                ret = false;
            }

            logBufferWriter.write("[validateParams] Done. Result = " + ret + "\n");
        } catch (Exception ex) {
        }
        return ret;
    }

    public boolean validateParamsGetContent(
            String username, String password, String serviceid, String msisdn, String params, String mode, String amount, String command) {
        boolean ret = true;
        Date now = new Date();
        try {
            logBufferWriter.write(String.format("%s - [validateParamsGetContent] validating..." + "\n", new Object[] { logDateFormat.format(now)}));

            if (serviceid.equalsIgnoreCase("")) {
                logger.warning("serviceid is empty");
                ret = false;
            }
            if (msisdn.equalsIgnoreCase("")) {
                logger.warning("msisdn is empty");
                ret = false;
            }
            else{
                if(!this.validateMsisdnPreffix(msisdn)){
                    ret = false;
                }
            }

            if (params.equalsIgnoreCase("")) {
                logger.warning("params is empty");
                ret = false;
            }
            if (mode.equalsIgnoreCase("")) {
                logger.warning("mode is empty");
                ret = false;
            }

            logBufferWriter.write(String.format("%s - [validateParamsGetContent] Done. Result = " + ret + "\n", new Object[] { logDateFormat.format(now)}));
        } catch (Exception ex) {
        }

        return ret;
    }

    /*
     * validateParams
     */
    public boolean validateParamsNotify(
            String username, String password, String cpRequestId, String msisdn, String price, String cmd, String responseCode) {
        boolean ret = true;

        try {
            logBufferWriter.write("[validateParamsNotify] validating..." + "\n");

            if (cpRequestId.equalsIgnoreCase("")) {
                logger.warning("cpRequestId is empty");
                ret = false;
            }
            if (msisdn.equalsIgnoreCase("")) {
                logger.warning("msisdn is empty");
                ret = false;
            }
            if (price.equalsIgnoreCase("")) {
                logger.warning("price is empty");
                ret = false;
            }
            if (cmd.equalsIgnoreCase("")) {
                logger.warning("cmd is empty");
                ret = false;
            }
            if (responseCode.equalsIgnoreCase("")) {
                logger.warning("responseCode is empty");
                ret = false;
            }

            logBufferWriter.write("[validateParamsNotify] Done. Result = " + ret + "\n");
        } catch (Exception ex) {
        }

        return ret;
    }

    /*
     * validateParams
     */
    public boolean validateParamsReceiverCDR(
            String transId, String src, String chargeDate, String amount, String status, String opid, String service, String username, String password) {
        boolean ret = true;
        try {
            logBufferWriter.write("[validateParamsReceiverCDR] validating..." + "\n");

            if (transId.equalsIgnoreCase("")) {
                logger.warning("transId is empty");
                ret = false;
            }
            if (src.equalsIgnoreCase("")) {
                logger.warning("src is empty");
                ret = false;
            }
            if (chargeDate.equalsIgnoreCase("")) {
                logger.warning("chargeDate is empty");
                ret = false;
            }
            if (amount.equalsIgnoreCase("")) {
                logger.warning("amount is empty");
                ret = false;
            }
            if (status.equalsIgnoreCase("")) {
                logger.warning("status is empty");
                ret = false;
            }
            if (opid.equalsIgnoreCase("")) {
                logger.warning("opid is empty");
                ret = false;
            }
            if (service.equalsIgnoreCase("")) {
                logger.warning("service is empty");
                ret = false;
            }

            logBufferWriter.write("[validateParamsReceiverCDR] Done. Result = " + ret + "\n");
        } catch (Exception ex) {
        }

        return ret;
    }
    /*
     * validateParams
     */

    public boolean antheticateWS(
            String username, String password, String wsusername, String wspassword) {
        boolean ret = false;
        Date now = new Date();
        try {
            logBufferWriter.write(String.format("%s - [antheticateWS] starting..." + "\n", new Object[] { logDateFormat.format(now)}));

            if (username.equals(wsusername)
                    && password.equals(wspassword)) {
                ret = true;
            } else {
                ret = false;
            }

            logBufferWriter.write(String.format("%s - [antheticateWS] Done. Result = " + ret + "\n", new Object[] { logDateFormat.format(now)}));
        } catch (Exception ex) {
        }

        return ret;
    }

    public MOMsg setWsMsgObject(
            String username, String password, String serviceid, String msisdn, String chargetime, String params, String mode, String amount, String command, String opId) {
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
        Date now = new Date();

        int org_params = Integer.parseInt(params);
        moMsg.opId = this.getTypeOfRequestByParams(opId, org_params);
        moMsg.moRxTime = now;
        moMsg.msgType = Msg.MsgType.MO;

        moMsg.svcId = serviceid;
        moMsg.msisdn = msisdn;
        moMsg.moRxId = chargetime;
        moMsg.txStatus = Integer.parseInt(params);
        moMsg.content = mode;
        moMsg.extraMOInfo = amount;
        moMsg.keyword = command;

        return moMsg;
    }


    public MOMsg setWsMsgObjectV2(
            String username, String password, String serviceid, String msisdn, String chargetime, String params, String mode, String amount, String command, String opId,String detail,String Chargecode,String nextRenewalTime,String transid) {
        MOMsg moMsg = this.setWsMsgObject(username, password, serviceid, msisdn, chargetime, params, mode, amount, command, opId);
        if(moMsg!=null){
        }

        return moMsg;
    }
    
    public MOMsg setWsMsgObjectGetContent(
            String username, String password, String serviceid, String msisdn, String chargetime, String params, String mode, String amount, String command, String opId) {
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
        Date now = new Date();

        moMsg.opId = opId;
        moMsg.moRxTime = now;
        moMsg.msgType = Msg.MsgType.MO;

        moMsg.svcId = serviceid;
        moMsg.msisdn = msisdn;
        moMsg.moRxId = chargetime;
        moMsg.txStatus = 0;
        moMsg.content = mode;
        moMsg.extraMOInfo = amount;
        moMsg.keyword = command;

        return moMsg;
    }
    
    public MOMsg setWsMsg9029Object(
            String username, String password, String serviceid, String msisdn, String chargetime, String params, String mode, String amount, String command, String opId
        ) {
//            String content_id, String cpCode, String ContentCode, String totalAmount, String account, String msisdn, int result, String extraInfo, String param_cp_report_code, String param_service_report_code, String param_shortcode
//            , String param_event_id
//            , String param_channel
//            , String param_cp_id
//            , String keyword
//            , String gameCode


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
//
//        moMsg.content = account;
//        moMsg.opId = cpCode;
//        moMsg.svcId = ContentCode;
//        moMsg.svcName = totalAmount;
//        moMsg.msisdn = msisdn;
//        moMsg.txStatus = result;
//        moMsg.extraMOInfo = extraInfo;
//        moMsg.moId = this.getReqId();
//        moMsg.opCnxId = param_cp_report_code;
//        moMsg.cpMODeliveryId = param_service_report_code;
//        moMsg.moTxId = param_event_id;
//        moMsg.opMTDeliveryId = param_channel;
//        moMsg.cpId = param_cp_id;
//
//        moMsg.shortCode = param_shortcode;
//        moMsg.keyword = keyword;
//        moMsg.campaignId = content_id;
//        moMsg.campaignCode = gameCode;

        return moMsg;
    }    

    public MOMsg setWsMsgObjectNotify(
            String username, String password, String serviceid, String msisdn, String chargetime, String params, String mode, String amount, String command, String opId) {
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
        Date now = new Date();

        int org_params = Integer.parseInt(params);
        moMsg.opId = this.getTypeOfRequestByParamsForNotify(opId, org_params);
        moMsg.moRxTime = now;
        if (opId.equalsIgnoreCase("getcontent")) {
            moMsg.msgType = Msg.MsgType.MOGetContent;
        } else {
            moMsg.msgType = Msg.MsgType.MO;
        }

        moMsg.svcId = serviceid;
        moMsg.msisdn = msisdn;
        moMsg.moRxId = chargetime;
        moMsg.txStatus = Integer.parseInt(params);
        moMsg.content = mode;
        moMsg.extraMOInfo = amount;
        moMsg.keyword = command;

        return moMsg;
    }

    public MOMsg setWsMsgObjectReceiverCDR(
            String transId, String src, String chargeDate, String amount, String status, String opid, String service, String username, String password, String typeOfTransaction, Properties properties) {
        MOMsg moMsg = new MOMsg();

        if (!src.startsWith("84")) {
            if (src.startsWith("04")) {
                src = "84" + src.substring(2);
            } else if (src.startsWith("0")) {
                src = "84" + src.substring(1);
            } else {
                src = "84" + src;
            }
        }
        Date now = new Date();

        int org_status = Integer.parseInt(status);
        moMsg.opId = this.getTypeOfRequestByParams(typeOfTransaction, org_status);
        moMsg.moRxTime = now;
        moMsg.msgType = Msg.MsgType.MO;
        moMsg.moId = transId;

        moMsg.svcId = this.getPackageCodeByService(opid, properties);//package_code
        moMsg.msisdn = src;
        moMsg.moRxId = chargeDate;
        moMsg.txStatus = org_status;
        // moMsg.content = mode;
        moMsg.extraMOInfo = amount;
        moMsg.keyword = service;

        return moMsg;
    }

    protected String getPackageCodeByService(String rqServiceId, Properties properties) {
        String package_code = "";

        try {
            if (!rqServiceId.equalsIgnoreCase("")) {
                String key = "package-code-by-" + rqServiceId.toLowerCase();
                package_code = properties.getProperty(key);
            }
        } catch (Exception ex) {
        }

        return package_code;
    }

    private String getTypeOfRequestByParams(String typeOfRequest, int org_params) {
        String ret = "";
        int params = org_params;

        try {
            if (typeOfRequest.equalsIgnoreCase("register")) {
                if (org_params == 0) {
                    ret = "REGISTER-CHARGERECORDSUCCESS";
                } else if (org_params == 1) {
                    ret = "CANCEL-CHARGERECORDSUCCESS";
                } else if (org_params == 2) {
                    ret = "PENDING";
                } else if (org_params == 3) {
                    ret = "RESTORE";
                }
            } else if (typeOfRequest.equalsIgnoreCase("renew")) {
                if (org_params == 0) {
                    ret = "RENEW-CHARGERECORDSUCCESS";
                } else if (org_params == 1) {
                    ret = "RENEW-CHARGERECORDFAIL";
                }
            } else if (typeOfRequest.equalsIgnoreCase("getcontent")) {
                if (org_params == 0) {
                    ret = "GETCONTENT-CHARGERECORDSUCCESS";
                } else if (org_params == 1) {
                    ret = "GETCONTENT-CHARGERECORDFAIL";
                }
            } else if (typeOfRequest.equalsIgnoreCase("getcontent_checkuserstatus")) {
                ret = "CHECKUSERSTATUS";
            }
        } catch (Exception ex) {
        }

        return ret;
    }

    private String getTypeOfRequestByParamsForNotify(String typeOfRequest, int org_params) {
        String ret = "";

        try {
            if (typeOfRequest.equalsIgnoreCase("register")) {
                if (org_params == 0) {
                    ret = "REGISTER-CHARGERECORDSUCCESS";
                } else if (org_params == 1) {
                    ret = "REGISTER-CHARGERECORDFAIL";
                }
            } else if (typeOfRequest.equalsIgnoreCase("cancel")) {
                if (org_params == 0) {
                    ret = "CANCEL-CHARGERECORDSUCCESS";
                } else if (org_params == 1) {
                    ret = "CANCEL-CHARGERECORDFAIL";
                }
            }
        } catch (Exception ex) {
        }

        return ret;
    }

    protected String getServiceIdFromCpRequestId(String cpRequestId, Properties properties) {
        String ret = "";

        try {
            if (!cpRequestId.equalsIgnoreCase("")
                    && cpRequestId.length() > 3) {
                String serviceid = cpRequestId.substring(0, 3);
                if (!serviceid.equalsIgnoreCase("")) {
                    ret = this.getPackageCodeByService(serviceid, properties);
                }
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
    
/**========================================================================================
 * callHTTPURL
 ========================================================================================*/    
    public String[] callHTTPURL(String remote_url){
        String[] ret = new String[2];
        ret[0] = "";
        ret[1] = "";
        
        try{
            logBufferWriter.write(new StringBuilder().append("callHTTPURL starting...").append("\n").toString());
            logBufferWriter.write(new StringBuilder().append("URL = ").append(remote_url).append("\n").toString());
            URL url = new URL(remote_url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;

            int httpResponseCode = con.getResponseCode();
            if(httpResponseCode==200) ret[0] = "true";
            //get content
            String response_content = "";
            BufferedReader inreader = new BufferedReader(new InputStreamReader(in));
            String inputLine;
            while ((inputLine = inreader.readLine()) != null){
                logBufferWriter.write(new StringBuilder().append(inputLine).append("\n").toString());
                response_content=response_content+inputLine;
            }
            inreader.close();
            
            ret[1] = response_content;
        }
        catch(Exception ex){
            System.out.println("[callHTTPURL] Exception:" + ex.getMessage());
        }
        
        try{
            logBufferWriter.write(new StringBuilder().append("Return = ").append(ret[0]).append(" | ").append(ret[1]).append("\n").toString());
        }
        catch(Exception ex){
            
        }
        return ret;
    }      
    

    protected String getRequestMOForward(MOMsg moMsg) {
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

            String serviceid = moMsg.svcId;
            String msisdn = moMsg.msisdn;
            String chargetime = moMsg.moRxId;
            int params = moMsg.txStatus;
            String mode = moMsg.content;
            String amount = moMsg.extraMOInfo;
            String command = moMsg.keyword;
            String from = this.wsClientId;
            String typeOfRequest = moMsg.opId;
            String reqid = this.getReqId();
            String charge_status = "";
            String campaignId = "";

            Properties requestProperties = new Properties();
            requestProperties.setProperty("username", this.wsUsername == null ? "" : URLEncoder.encode(this.wsUsername, "UTF-8"));
            requestProperties.setProperty("password", this.wsPassword == null ? "" : URLEncoder.encode(this.wsPassword, "UTF-8"));
            requestProperties.setProperty("msisdn", URLEncoder.encode(msisdn, "UTF-8"));
            requestProperties.setProperty("cmd", URLEncoder.encode(command, "UTF-8"));
            requestProperties.setProperty("packagecode", URLEncoder.encode(serviceid, "UTF-8"));
            requestProperties.setProperty("itemname", amount);
            requestProperties.setProperty("reqid", reqid);
            requestProperties.setProperty("from", URLEncoder.encode(from, "UTF-8"));
            requestProperties.setProperty("userip", (ipClient == null) ? "" : ipClient.getHostAddress());

            requestProperties.setProperty("campaignid", URLEncoder.encode(campaignId, "UTF-8"));


            String requestData = "";
            String seperator = "&";
            Iterator iter = requestProperties.keySet().iterator();
            String key = null;
            String value = null;
            while (iter.hasNext()) {
                key = (String) iter.next();
                if (key != null) {
                    value = requestProperties.getProperty(key);
                    if(!requestData.equalsIgnoreCase("")) {
                        requestData = new StringBuilder().append(requestData).append(seperator).toString();
                    }
                    requestData = new StringBuilder().append(requestData).append("").append(URLEncoder.encode(key, "UTF-8")).append("=").append(value == null ? "" : value).toString();
                }

            }
            String endline = "\r\n";

            if ("GET".equalsIgnoreCase(this.wsMethod)) {
                request = new StringBuilder().append("GET ").append(this.getForwarderPath(this.wsPath)).append("").append(requestData).append(" ").append("HTTP/1.1").append(endline).toString();

                request = new StringBuilder().append(request).append("Host: ").append(this.wsHost).append(this.wsPort != 80 ? new StringBuilder().append(":").append(this.wsPort).toString() : "").append(endline).toString();

                request = new StringBuilder().append(request).append(endline).toString();
            }
            else if ("GETHTTPURL".equalsIgnoreCase(this.wsMethod)) {
                String host = new StringBuilder().append("http://").append(this.wsHost).append(this.wsPort != 80 ? new StringBuilder().append(":").append(this.wsPort).toString() : "").toString();
                String path = new StringBuilder().append(host).append(this.getForwarderPath(this.wsPath)).append("?").toString();

                request = new StringBuilder().append(path).append(requestData).toString();
            } else if ("POST".equalsIgnoreCase(this.wsMethod)) {
                request = new StringBuilder().append("POST ").append(this.getForwarderPath(this.wsPath)).append(" ").append("HTTP/1.1").append(endline).toString();

                request = new StringBuilder().append(request).append("Host: ").append(this.wsHost).append(this.wsPort != 80 ? new StringBuilder().append(":").append(this.wsPort).toString() : "").append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append("Content-Length: ").append(requestData.length()).append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append("Content-Type: application/x-www-form-urlencoded").append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append(requestData).append("\n").toString();
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
    
    protected String getRequestMOForwardGW(MOMsg moMsg) {
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

            String serviceid = moMsg.svcId;
            String msisdn = moMsg.msisdn;
            String chargetime = moMsg.moRxId;
            int params = moMsg.txStatus;
            String mode = moMsg.content;
            String amount = moMsg.extraMOInfo;
            String command = moMsg.keyword;
            String from = this.wsClientId;
            String typeOfRequest = moMsg.opId;
            String reqid = this.getReqId();
            String charge_status = "";
            String campaignId = "";

            Properties requestProperties = new Properties();
            requestProperties.setProperty("username", this.wsUsername == null ? "" : URLEncoder.encode(this.wsUsername, "UTF-8"));
            requestProperties.setProperty("password", this.wsPassword == null ? "" : URLEncoder.encode(this.wsPassword, "UTF-8"));
            requestProperties.setProperty("src", URLEncoder.encode(msisdn, "UTF-8"));
            requestProperties.setProperty("dest", URLEncoder.encode(msisdn, "UTF-8"));
            requestProperties.setProperty("msgbody", URLEncoder.encode(command, "UTF-8"));
            requestProperties.setProperty("cmdcode", URLEncoder.encode(command, "UTF-8"));
            requestProperties.setProperty("moseq", reqid);

            String requestData = "";
            String seperator = "&";
            Iterator iter = requestProperties.keySet().iterator();
            String key = null;
            String value = null;
            while (iter.hasNext()) {
                key = (String) iter.next();
                if (key != null) {
                    value = requestProperties.getProperty(key);
                    if(!requestData.equalsIgnoreCase("")) {
                        requestData = new StringBuilder().append(requestData).append(seperator).toString();
                    }
                    requestData = new StringBuilder().append(requestData).append("").append(URLEncoder.encode(key, "UTF-8")).append("=").append(value == null ? "" : value).toString();
                }

            }
            String endline = "\r\n";

            if ("GET".equalsIgnoreCase(this.wsMethod)) {
                request = new StringBuilder().append("GET ").append(this.getForwarderPath(this.wsPath)).append("").append(requestData).append(" ").append("HTTP/1.1").append(endline).toString();

                request = new StringBuilder().append(request).append("Host: ").append(this.wsHost).append(this.wsPort != 80 ? new StringBuilder().append(":").append(this.wsPort).toString() : "").append(endline).toString();

                request = new StringBuilder().append(request).append(endline).toString();
            }
            else if ("GETHTTPURL".equalsIgnoreCase(this.wsMethod)) {
                String host = new StringBuilder().append("http://").append(this.wsHost).append(this.wsPort != 80 ? new StringBuilder().append(":").append(this.wsPort).toString() : "").toString();
                String path = new StringBuilder().append(host).append(this.getForwarderPath(this.wsPath)).append("?").toString();

                request = new StringBuilder().append(path).append(requestData).toString();
            } else if ("POST".equalsIgnoreCase(this.wsMethod)) {
                request = new StringBuilder().append("POST ").append(this.getForwarderPath(this.wsPath)).append(" ").append("HTTP/1.1").append(endline).toString();

                request = new StringBuilder().append(request).append("Host: ").append(this.wsHost).append(this.wsPort != 80 ? new StringBuilder().append(":").append(this.wsPort).toString() : "").append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append("Content-Length: ").append(requestData.length()).append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append("Content-Type: application/x-www-form-urlencoded").append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append(requestData).append("\n").toString();
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
    
    public static String genMTSequence18digits(){
        String ret = "";
        
        try{
            SimpleDateFormat smpDateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
            Date now = new Date();
            ret = smpDateFormat.format(now);
            
            Random randomGenerator = new Random();
            String randomStr = new Integer(randomGenerator.nextInt(10)).toString();
            
            ret = ret + randomStr;
        }
        catch(Exception ex){
            
        }
        
        return ret;
    }    
    

    protected String getRequestMOChargingLog(String serviceCode, MOMsg moMsg) {
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
        
            String contentid = _properties.getProperty(serviceCode.toLowerCase() + "-content-id-" + moMsg.keyword.toLowerCase());
            String status = "0";
            String cpid = _properties.getProperty(serviceCode.toLowerCase() + "-cg-log-ws-cpid");
            String contenttype = _properties.getProperty(serviceCode.toLowerCase() + "-cg-log-ws-contenttype");
            String type = moMsg.opId;
            String mtseq = this.genMTSequence18digits();
            String serviceid = "";
            String dest = moMsg.msisdn;
            String price = moMsg.extraMOInfo;
            String msgtype = _properties.getProperty(serviceCode.toLowerCase() + "-cg-log-ws-msgtype");
            String originalprice = moMsg.extraMOInfo;
            String src = _properties.getProperty(serviceCode.toLowerCase() + "-cg-log-ws-src");
            String procresult = "1";
            String cpname = _properties.getProperty(serviceCode.toLowerCase() + "-cg-log-ws-cpname");
            String msgbody = _properties.getProperty(serviceCode.toLowerCase() + "-cg-log-ws-msgbody");
            String channel = _properties.getProperty(serviceCode.toLowerCase() + "-cg-log-ws-channel");
            String note = "";
            String msgtitle = _properties.getProperty(serviceCode.toLowerCase() + "-cg-log-ws-msgtitle");
            String contentname = moMsg.keyword;
                        
            String password = _properties.getProperty(serviceCode.toLowerCase() + "-cg-log-ws-password");
            String username = _properties.getProperty(serviceCode.toLowerCase() + "-cg-log-ws-username");
            
            if(cpid.contains("{contentid}")){
                cpid = cpid.replace("{contentid}", contentid);
            }
            if(channel.contains("{contentid}")){
                channel = channel.replace("{contentid}", contentid);
            }

            Properties requestProperties = new Properties();
            requestProperties.setProperty("username", username == null ? "" : URLEncoder.encode(username, "UTF-8"));
            requestProperties.setProperty("password", password == null ? "" : URLEncoder.encode(password, "UTF-8"));
            requestProperties.setProperty("contentid", URLEncoder.encode(contentid, "UTF-8"));
            requestProperties.setProperty("status", URLEncoder.encode(status, "UTF-8"));
            requestProperties.setProperty("cpid", URLEncoder.encode(cpid, "UTF-8"));
            requestProperties.setProperty("contenttype", URLEncoder.encode(contenttype, "UTF-8"));
            requestProperties.setProperty("type", URLEncoder.encode(type, "UTF-8"));
            requestProperties.setProperty("mtseq", URLEncoder.encode(mtseq, "UTF-8"));
            requestProperties.setProperty("serviceid", URLEncoder.encode(serviceid, "UTF-8"));
            requestProperties.setProperty("dest", URLEncoder.encode(dest, "UTF-8"));
            requestProperties.setProperty("price", URLEncoder.encode(price, "UTF-8"));
            requestProperties.setProperty("msgtype", URLEncoder.encode(msgtype, "UTF-8"));
            requestProperties.setProperty("originalprice", URLEncoder.encode(originalprice, "UTF-8"));
            requestProperties.setProperty("procresult", URLEncoder.encode(procresult, "UTF-8"));
            requestProperties.setProperty("cpname", URLEncoder.encode(cpname, "UTF-8"));
            requestProperties.setProperty("msgbody", URLEncoder.encode(msgbody, "UTF-8"));
            requestProperties.setProperty("channel", URLEncoder.encode(channel, "UTF-8"));
            requestProperties.setProperty("note", URLEncoder.encode(note, "UTF-8"));
            requestProperties.setProperty("msgtitle", URLEncoder.encode(msgtitle, "UTF-8"));
            requestProperties.setProperty("contentname", URLEncoder.encode(contentname, "UTF-8"));


            String requestData = "";
            String seperator = "&";
            Iterator iter = requestProperties.keySet().iterator();
            String key = null;
            String value = null;
            while (iter.hasNext()) {
                key = (String) iter.next();
                if (key != null) {
                    value = requestProperties.getProperty(key);
                    if(!requestData.equalsIgnoreCase("")) {
                        requestData = new StringBuilder().append(requestData).append(seperator).toString();
                    }
                    requestData = new StringBuilder().append(requestData).append("").append(URLEncoder.encode(key, "UTF-8")).append("=").append(value == null ? "" : value).toString();
                }

            }
            String endline = "\r\n";

            if ("GET".equalsIgnoreCase(this.wsMethod)) {
                request = new StringBuilder().append("GET ").append(this.getForwarderPath(this.wsPath)).append("").append(requestData).append(" ").append("HTTP/1.1").append(endline).toString();

                request = new StringBuilder().append(request).append("Host: ").append(this.wsHost).append(this.wsPort != 80 ? new StringBuilder().append(":").append(this.wsPort).toString() : "").append(endline).toString();

                request = new StringBuilder().append(request).append(endline).toString();
            }
            else if ("GETHTTPURL".equalsIgnoreCase(this.wsMethod)) {
                String host = new StringBuilder().append("http://").append(this.wsHost).append(this.wsPort != 80 ? new StringBuilder().append(":").append(this.wsPort).toString() : "").toString();
                String path = new StringBuilder().append(host).append(this.getForwarderPath(this.wsPath)).append("?").toString();

                request = new StringBuilder().append(path).append(requestData).toString();
            } else if ("POST".equalsIgnoreCase(this.wsMethod)) {
                request = new StringBuilder().append("POST ").append(this.getForwarderPath(this.wsPath)).append(" ").append("HTTP/1.1").append(endline).toString();

                request = new StringBuilder().append(request).append("Host: ").append(this.wsHost).append(this.wsPort != 80 ? new StringBuilder().append(":").append(this.wsPort).toString() : "").append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append("Content-Length: ").append(requestData.length()).append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append("Content-Type: application/x-www-form-urlencoded").append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append(requestData).append("\n").toString();
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
    
    protected String getRequest(MOMsg moMsg) {
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

            String serviceid = moMsg.svcId;
            String msisdn = moMsg.msisdn;
            String chargetime = moMsg.moRxId;
            int params = moMsg.txStatus;
            String mode = moMsg.content;
            String amount = moMsg.extraMOInfo;
            String command = moMsg.keyword;
            String from = this.wsClientId;
            String typeOfRequest = moMsg.opId;
            String reqid = this.getReqId();
            String charge_status = "";
            String campaignId = "";

            Properties requestProperties = new Properties();
            requestProperties.setProperty("username", this.wsUsername == null ? "" : URLEncoder.encode(this.wsUsername, "UTF-8"));
            requestProperties.setProperty("password", this.wsPassword == null ? "" : URLEncoder.encode(this.wsPassword, "UTF-8"));
            requestProperties.setProperty("msisdn", URLEncoder.encode(msisdn, "UTF-8"));
            requestProperties.setProperty("cmd", URLEncoder.encode(typeOfRequest, "UTF-8"));
            requestProperties.setProperty("packagecode", URLEncoder.encode(serviceid, "UTF-8"));
            requestProperties.setProperty("itemname", amount);
            requestProperties.setProperty("reqid", reqid);
            requestProperties.setProperty("from", URLEncoder.encode(from, "UTF-8"));
            requestProperties.setProperty("userip", (ipClient == null) ? "" : ipClient.getHostAddress());

            requestProperties.setProperty("campaignid", URLEncoder.encode(campaignId, "UTF-8"));


            String requestData = "";
            String seperator = "&";
            Iterator iter = requestProperties.keySet().iterator();
            String key = null;
            String value = null;
            while (iter.hasNext()) {
                key = (String) iter.next();
                if (key != null) {
                    value = requestProperties.getProperty(key);
                    requestData = new StringBuilder().append(requestData).append(seperator).append(URLEncoder.encode(key, "UTF-8")).append("=").append(value == null ? "" : value).toString();

                    seperator = "&";
                }

            }
            String endline = "\r\n";

            if ("GET".equalsIgnoreCase(this.wsMethod)) {
                request = new StringBuilder().append("GET ").append(this.getForwarderPath(this.wsPath)).append("").append(requestData).append(" ").append("HTTP/1.1").append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append("Host: ").append(this.wsHost).append(this.wsPort != 80 ? new StringBuilder().append(":").append(this.wsPort).toString() : "").append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append(endline).toString();
            } else if ("POST".equalsIgnoreCase(this.wsMethod)) {
                request = new StringBuilder().append("POST ").append(this.getForwarderPath(this.wsPath)).append(" ").append("HTTP/1.1").append(endline).toString();

                request = new StringBuilder().append(request).append("Host: ").append(this.wsHost).append(this.wsPort != 80 ? new StringBuilder().append(":").append(this.wsPort).toString() : "").append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append("Content-Length: ").append(requestData.length()).append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append("Content-Type: application/x-www-form-urlencoded").append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append(endline).append("\n").toString();

                request = new StringBuilder().append(request).append(requestData).append("\n").toString();
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
//                        logBufferWriter.write(new StringBuilder().append("line: ").append(line).append("\n").toString());
                        if(line.startsWith("<response>")){
                            ret = ret + line;
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

    protected void wsInitialize(String host, String port, String path, String username, String password, String clientId, String method) {
        this.wsHost = host;
        this.wsPort = new Integer(port);
        this.wsPath = path;
        this.wsUsername = username;
        this.wsPassword = password;
        this.wsClientId = clientId;
        this.wsMethod = method;
    }

    protected String checkUserExists(MOMsg moMsg) {
        String ret = "";
        try {
            String responseText = this.sendRequest(moMsg,"checkuserexists");
            logBufferWriter.write(String.format("Response: %s\n", new Object[]{responseText}));
            
            if(responseText!=null
                    && !responseText.equalsIgnoreCase("")){
                Properties requestParamProperties = this.getXMLElementResponse(responseText);
                if(requestParamProperties!=null){
                    String desc = requestParamProperties.getProperty("desc");
//                    if(desc.startsWith("REGISTER_NOT_AVAILABLE_")){
//                        ret = true;
//                    }
                    if(!desc.equalsIgnoreCase("")){
                        ret = desc;
                    }
                }
            }
        } catch (Exception ex) {
        }

        return ret;
    }


    protected String forwardMO(String serviceCode, MOMsg moMsg) {
        String ret = "";
        try {
            String responseText = this.sendRequestHTTPGET(serviceCode, moMsg,"moforward");
            logBufferWriter.write(String.format("Response: %s\n", new Object[]{responseText}));
            
            if(responseText!=null
                    && !responseText.equalsIgnoreCase("")){
                Properties requestParamProperties = this.getXMLElementResponse(responseText);
                if(requestParamProperties!=null){
                    String desc = requestParamProperties.getProperty("desc");
//                    if(desc.startsWith("REGISTER_NOT_AVAILABLE_")){
//                        ret = true;
//                    }
                    if(!desc.equalsIgnoreCase("")){
                        ret = desc;
                    }
                }
            }
        } catch (Exception ex) {
        }

        return ret;
    }
    
    protected String forwardMOGW(String serviceCode, MOMsg moMsg) {
        String ret = "";
        try {
            String responseText = this.sendRequestHTTPGET(serviceCode, moMsg,"moforwardgw");
            logBufferWriter.write(String.format("Response: %s\n", new Object[]{responseText}));
            
            try{
                if(responseText!=null
                        && !responseText.equalsIgnoreCase("")){
                    Properties requestParamProperties = this.getXMLElementResponse(responseText);
                    if(requestParamProperties!=null){
                        String desc = requestParamProperties.getProperty("desc");
    //                    if(desc.startsWith("REGISTER_NOT_AVAILABLE_")){
    //                        ret = true;
    //                    }
                        if(!desc.equalsIgnoreCase("")){
                            ret = desc;
                        }
                    }
                }
            }
            catch(Exception ex){}
        } catch (Exception ex) {
        }

        return ret;
    }
    
    protected String sendCGLog(String serviceCode, MOMsg moMsg) {
        String ret = "";
        try {
            String responseText = this.sendRequestHTTPGET(serviceCode,moMsg,"cglog");
            logBufferWriter.write(String.format("Response: %s\n", new Object[]{responseText}));
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
            NodeList nodes = doc.getElementsByTagName("response");
            
            String[] arrKeys = {"desc","msisdn","responsecode","reqid"};
            
            if(nodes!=null){
                Element element = (Element) nodes.item(0);                
                for(String key : arrKeys){
                    NodeList nodeListElement = element.getElementsByTagName(key);
                    Element line = (Element) nodeListElement.item(0);
                    String val = this.getCharacterDataFromElement(line);

                    requestParamProperties.setProperty(key.toLowerCase(), val);
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

    protected String sendRequest(MOMsg moMsg, String type) {
        String httpResponseText = null;
        PrintWriter printWriter = null;
        OutputStream output = null;

        try {
            logBufferWriter.write(new StringBuilder().append("Connecting to ").append(this.wsHost).append(":").append(Integer.toString(this.wsPort)).append(" ...").append("\n").toString());
            this.wsSocket = new Socket(this.wsHost, this.wsPort);
            logBufferWriter.write(new StringBuilder().append("Connected to ").append(this.wsSocket.getInetAddress()).append(":").append(this.wsSocket.getPort()).append("\n").toString());
        } catch (Exception ex) {
            try {
                logBufferWriter.write(new StringBuilder().append("Cannot connect to ").append(this.wsHost).append(":").append(this.wsPort).append(". ").append(ex).append("\n").toString());
            } catch (Exception exx) {
            }
        }


        if (this.wsSocket != null) {
            String request = null;
            try {
                if(type.equalsIgnoreCase("checkuserexists")){
                    request = this.getRequest(moMsg);
                }
                else if(type.equalsIgnoreCase("moforward")){
                    request = this.getRequestMOForward(moMsg);
                }
                else if(type.equalsIgnoreCase("moforwardgw")){
                    request = this.getRequestMOForwardGW(moMsg);
                }
                else{
                    request = this.getRequest(moMsg);
                }
                logBufferWriter.write(new StringBuilder().append("Request = [").append(request).append("]").append("\n").toString());
            } catch (Exception ex) {
            }
            if (request == null) {
                try {
                    logBufferWriter.write(new StringBuilder().append("Request = null. MOMsg DROPPED!!! ").append("\n").toString());
                } catch (Exception exx) {
                }
            } else {
                try {
                    output = this.wsSocket.getOutputStream();
                    printWriter = new PrintWriter(output);
                    printWriter.print(request);
                    printWriter.flush();

                    logBufferWriter.write("Request sent");
                    try {
                        InputStream inputStream = this.wsSocket.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                        int httpStatusCode = this.getHttpStatusCode(bufferedReader);
                        httpResponseText = this.getHttpResponseText(bufferedReader);

                        logBufferWriter.write(new StringBuilder().append("Http response status = ").append(httpStatusCode).append("\n").toString());
                        logBufferWriter.write(new StringBuilder().append("Http response text = ").append(httpResponseText).append("\n").toString());
                    } catch (Exception ex) {
                        logBufferWriter.write(new StringBuilder().append("Exception reading response: ").append(ex).append("\n").toString());
                    }
                } catch (Exception ex) {
                    try {
                        logBufferWriter.write(new StringBuilder().append("Request NOT sent. Exception writing to socket: ").append(ex).append("\n").toString());
                    } catch (Exception exx) {
                    }
                }
            }
        }

        return httpResponseText;
    }
    
    protected String sendRequestHTTPGET(String serviceCode, MOMsg moMsg, String type) {
        String httpResponseText = null;

        try {
            this.wsMethod = "gethttpurl";
            String url = "";
            if (type.equalsIgnoreCase("moforward")) {
                url = this.getRequestMOForward(moMsg);
            }
            else if (type.equalsIgnoreCase("moforwardgw")) {
                url = this.getRequestMOForwardGW(moMsg);
            }
            else if (type.equalsIgnoreCase("cglog")) {
                url = this.getRequestMOChargingLog(serviceCode,moMsg);
            }
            logBufferWriter.write(new StringBuilder().append("URL = [").append(url).append("]").append("\n").toString());
            
            String[] result = this.callHTTPURL(url);
            String httpStatusCode = result[0];
            httpResponseText = result[1];

            logBufferWriter.write(new StringBuilder().append("Http response status = ").append(httpStatusCode).append("\n").toString());
            logBufferWriter.write(new StringBuilder().append("Http response text = ").append(httpResponseText).append("\n").toString());
        } catch (Exception ex) {
        }
        
        return httpResponseText;
    }    
}
