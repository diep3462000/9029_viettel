package com.centech.smsgw.common;

import java.io.PrintStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Msg
  implements Serializable
{
  public MsgType msgType = null;

  public String msisdn = null;
  public String shortCode = null;
  public String keyword = null;
  public ContentType contentType = null;
  public String content = null;
  public String opCnxId = null;
  public String opId = null;
  public String cpCnxId = null;
  public String cpId = null;
  public String svcId = null;
  public String svcName = null;
  public String moId = null;
  public String mtId = null;
  public String extraMOInfo = null;
  public boolean cdrCreated = false;
  public BillingStatus billingStatus = null;
  public int mtCount = 0;

  public Date moRxTime = null;
  public Date moTxTime = null;
  public Date mtRxTime = null;
  public Date mtTxTime = null;

  public int txAttempts = 0;
  public int txStatus = -1;

  public String moRxId = null;
  public String mtTxId = null;
  public String opMTDeliveryId = null;
  public int opMTDeliveryStatus = -1;

  public String mtRxId = null;
  public String moTxId = null;
  public String cpMODeliveryId = null;
  public int cpMODeliveryStatus = -1;

  public int moTxStatus = -1;
  public int mtTxStatus = -1;

  public Msg(MsgType msgType)
  {
    this.msgType = msgType;
  }

  public String debugString()
  {
    SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    String s = "";

    s = new StringBuilder().append(s).append("msgType=").append(this.msgType == null ? "[null]" : this.msgType).toString();
    s = new StringBuilder().append(s).append(", msisdn=").append(this.msisdn == null ? "[null]" : this.msisdn).toString();
    s = new StringBuilder().append(s).append(", shortCode=").append(this.shortCode == null ? "[null]" : this.shortCode).toString();
    s = new StringBuilder().append(s).append(", keyword=").append(this.keyword == null ? "[null]" : this.keyword).toString();
    s = new StringBuilder().append(s).append(", contentType=").append(this.contentType == null ? "[null]" : this.contentType).toString();
    s = new StringBuilder().append(s).append(", content=").append(this.content == null ? "[null]" : this.content).toString();
    s = new StringBuilder().append(s).append(", opCnxId=").append(this.opCnxId == null ? "[null]" : this.opCnxId).toString();
    s = new StringBuilder().append(s).append(", opId=").append(this.opId == null ? "[null]" : this.opId).toString();
    s = new StringBuilder().append(s).append(", cpCnxId=").append(this.cpCnxId == null ? "[null]" : this.cpCnxId).toString();
    s = new StringBuilder().append(s).append(", cpId=").append(this.cpId == null ? "[null]" : this.cpId).toString();
    s = new StringBuilder().append(s).append(", svcId=").append(this.svcId == null ? "[null]" : this.svcId).toString();
    s = new StringBuilder().append(s).append(", svcName=").append(this.svcName == null ? "[null]" : this.svcName).toString();
    s = new StringBuilder().append(s).append(", moId=").append(this.moId == null ? "[null]" : this.moId).toString();
    s = new StringBuilder().append(s).append(", mtId=").append(this.mtId == null ? "[null]" : this.mtId).toString();
    s = new StringBuilder().append(s).append(", extraMOInfo=").append(this.extraMOInfo == null ? "[null]" : this.extraMOInfo).toString();
    s = new StringBuilder().append(s).append(", cdrCreated=").append(this.cdrCreated).toString();
    s = new StringBuilder().append(s).append(", billingStatus=").append(this.billingStatus == null ? "[null]" : this.billingStatus.toString()).toString();
    s = new StringBuilder().append(s).append(", mtCount=").append(this.mtCount).toString();

    s = new StringBuilder().append(s).append(", moRxTime=").append(this.moRxTime == null ? "[null]" : dateFormater.format(this.moRxTime)).toString();
    s = new StringBuilder().append(s).append(", moTxTime=").append(this.moTxTime == null ? "[null]" : dateFormater.format(this.moTxTime)).toString();
    s = new StringBuilder().append(s).append(", mtRxTime=").append(this.mtRxTime == null ? "[null]" : dateFormater.format(this.mtRxTime)).toString();
    s = new StringBuilder().append(s).append(", mtTxTime=").append(this.mtTxTime == null ? "[null]" : dateFormater.format(this.mtTxTime)).toString();

    s = new StringBuilder().append(s).append(", txAttempts=").append(this.txAttempts).toString();
    s = new StringBuilder().append(s).append(", txStatus=").append(this.txStatus).toString();

    s = new StringBuilder().append(s).append(", moRxId=").append(this.moRxId).toString();
    s = new StringBuilder().append(s).append(", mtTxId=").append(this.mtTxId).toString();
    s = new StringBuilder().append(s).append(", opMTDeliveryId=").append(this.opMTDeliveryId).toString();
    s = new StringBuilder().append(s).append(", opMTDeliveryStatus=").append(this.opMTDeliveryStatus).toString();

    s = new StringBuilder().append(s).append(", mtRxId=").append(this.mtRxId).toString();
    s = new StringBuilder().append(s).append(", moTxId=").append(this.moTxId).toString();
    s = new StringBuilder().append(s).append(", cpMODeliveryId=").append(this.cpMODeliveryId).toString();
    s = new StringBuilder().append(s).append(", cpMODeliveryStatus=").append(this.cpMODeliveryStatus).toString();

    s = new StringBuilder().append(s).append(", moTxStatus=").append(this.moTxStatus).toString();
    s = new StringBuilder().append(s).append(", mtTxStatus=").append(this.mtTxStatus).toString();

    return s;
  }

  public static String msgToString(Msg msg)
  {
    SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    String s = "";

    s = new StringBuilder().append(s).append(msg.msgType == null ? "" : msg.msgType).toString();
    s = new StringBuilder().append(s).append(",").append(msg.msisdn == null ? "" : msg.msisdn).toString();
    s = new StringBuilder().append(s).append(",").append(msg.shortCode == null ? "" : msg.shortCode).toString();
    s = new StringBuilder().append(s).append(",").append(msg.keyword == null ? "" : msg.keyword).toString();
    s = new StringBuilder().append(s).append(",").append(msg.contentType == null ? "" : msg.contentType).toString();
    s = new StringBuilder().append(s).append(",").append(msg.content == null ? "" : msg.content).toString();
    s = new StringBuilder().append(s).append(",").append(msg.opCnxId == null ? "" : msg.opCnxId).toString();
    s = new StringBuilder().append(s).append(",").append(msg.opId == null ? "" : msg.opId).toString();
    s = new StringBuilder().append(s).append(",").append(msg.cpCnxId == null ? "" : msg.cpCnxId).toString();
    s = new StringBuilder().append(s).append(",").append(msg.cpId == null ? "" : msg.cpId).toString();
    s = new StringBuilder().append(s).append(",").append(msg.svcId == null ? "" : msg.svcId).toString();
    s = new StringBuilder().append(s).append(",").append(msg.svcName == null ? "" : msg.svcName).toString();
    s = new StringBuilder().append(s).append(",").append(msg.moId == null ? "" : msg.moId).toString();
    s = new StringBuilder().append(s).append(",").append(msg.mtId == null ? "" : msg.mtId).toString();
    s = new StringBuilder().append(s).append(",").append(msg.extraMOInfo == null ? "" : msg.extraMOInfo).toString();
    s = new StringBuilder().append(s).append(",").append(msg.cdrCreated).toString();
    s = new StringBuilder().append(s).append(",").append(msg.billingStatus == null ? "" : msg.billingStatus.toString()).toString();
    s = new StringBuilder().append(s).append(",").append(msg.mtCount).toString();

    s = new StringBuilder().append(s).append(",").append(msg.moRxTime == null ? "" : dateFormater.format(msg.moRxTime)).toString();
    s = new StringBuilder().append(s).append(",").append(msg.moTxTime == null ? "" : dateFormater.format(msg.moTxTime)).toString();
    s = new StringBuilder().append(s).append(",").append(msg.mtRxTime == null ? "" : dateFormater.format(msg.mtRxTime)).toString();
    s = new StringBuilder().append(s).append(",").append(msg.mtTxTime == null ? "" : dateFormater.format(msg.mtTxTime)).toString();

    s = new StringBuilder().append(s).append(",").append(msg.txAttempts).toString();
    s = new StringBuilder().append(s).append(",").append(msg.txStatus).toString();

    s = new StringBuilder().append(s).append(",").append(msg.moRxId).toString();
    s = new StringBuilder().append(s).append(",").append(msg.mtTxId).toString();
    s = new StringBuilder().append(s).append(",").append(msg.opMTDeliveryId).toString();
    s = new StringBuilder().append(s).append(",").append(msg.opMTDeliveryStatus).toString();

    s = new StringBuilder().append(s).append(",").append(msg.mtRxId).toString();
    s = new StringBuilder().append(s).append(",").append(msg.moTxId).toString();
    s = new StringBuilder().append(s).append(",").append(msg.cpMODeliveryId).toString();
    s = new StringBuilder().append(s).append(",").append(msg.cpMODeliveryStatus).toString();

    s = new StringBuilder().append(s).append(",").append(msg.moTxStatus).toString();
    s = new StringBuilder().append(s).append(",").append(msg.mtTxStatus).toString();

    return s;
  }

  public static MOMsg stringToMOMsg(String str)
  {
    SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    MOMsg msg = null;
    try
    {
      String[] items = str.split(",");
      if (MsgType.MO.toString().equals(items[0])) {
        msg = new MOMsg();
        msg.msisdn = ("".equals(items[1]) ? null : items[1]);
        msg.shortCode = ("".equals(items[2]) ? null : items[2]);
        msg.keyword = ("".equals(items[3]) ? null : items[3]);
        msg.contentType = ContentType.TEXT_GSM;
        msg.content = ("".equals(items[5]) ? null : items[5]);
        msg.opCnxId = ("".equals(items[6]) ? null : items[6]);
        msg.opId = ("".equals(items[7]) ? null : items[7]);
        msg.cpCnxId = ("".equals(items[8]) ? null : items[8]);
        msg.cpId = ("".equals(items[9]) ? null : items[9]);
        msg.svcId = ("".equals(items[10]) ? null : items[10]);
        msg.svcName = ("".equals(items[11]) ? null : items[11]);
        msg.moId = ("".equals(items[12]) ? null : items[12]);
        msg.mtId = ("".equals(items[13]) ? null : items[13]);
        msg.extraMOInfo = ("".equals(items[14]) ? null : items[14]);
        msg.cdrCreated = "true".equalsIgnoreCase(items[15]);
        msg.billingStatus = (BillingStatus.SUCCESS.toString().equalsIgnoreCase(items[16]) ? BillingStatus.SUCCESS : BillingStatus.FAILED.toString().equalsIgnoreCase(items[16]) ? BillingStatus.FAILED : BillingStatus.WAIT);

        msg.mtCount = Integer.parseInt(items[17]);

        msg.moRxTime = (!"".equals(items[18]) ? dateFormater.parse(items[18]) : null);
        msg.moTxTime = (!"".equals(items[19]) ? dateFormater.parse(items[19]) : null);
        msg.mtRxTime = (!"".equals(items[20]) ? dateFormater.parse(items[20]) : null);
        msg.mtTxTime = (!"".equals(items[21]) ? dateFormater.parse(items[21]) : null);

        msg.txAttempts = Integer.parseInt(items[22]);
        msg.txStatus = Integer.parseInt(items[23]);

        msg.moRxId = ("".equals(items[24]) ? null : items[24]);
        msg.mtTxId = ("".equals(items[25]) ? null : items[25]);
        msg.opMTDeliveryId = ("".equals(items[26]) ? null : items[26]);
        msg.opMTDeliveryStatus = Integer.parseInt(items[27]);

        msg.mtRxId = ("".equals(items[28]) ? null : items[28]);
        msg.moTxId = ("".equals(items[29]) ? null : items[29]);
        msg.cpMODeliveryId = ("".equals(items[30]) ? null : items[30]);
        msg.cpMODeliveryStatus = Integer.parseInt(items[31]);

        msg.moTxStatus = Integer.parseInt(items[32]);
        msg.mtTxStatus = Integer.parseInt(items[33]);
      }
    } catch (Exception ex) {
      System.out.println(new StringBuilder().append("Exception converting String to MOMsg: ").append(ex).toString());
    }

    return msg;
  }

  public static enum ContentType
  {
    TEXT_GSM, 
    TEXT_UCS2, 
    FLASH_GSM, 
    FLASH_UCS2, 
    BOOKMARK_URL, 
    SERVICE_INDICATION, 
    RINGTONE, 
    OPERATOR_LOGO, 
    PICTURE_MESSAGE, 
    BINARY_DATA, 
    SMPP34_SUBMITSM, 
    MMS_CONTENT_URLS;
  }

  public static enum BillingStatus
  {
    FAILED, 
    SUCCESS, 
    WAIT;
  }

  public static enum MsgType
  {
    MO, 
    MT, 
    CDR, 
    MOLOG, 
    MTLOG, 
    CDRLOG, 
    MORESP, 
    MTRESP, 
    MTCHARGINGREQ, 
    MTCHARGINGRESP, 
    SMSCDeliveryReceipt, 
    MTSVCREQ, 
    MTSVCRESP, 
    MTCHARGINGLOG, 
    MTSVCLOG, 
    SYSTEM, 
    USSDMO, 
    USSDMT, 
    USSDCDR, 
    USSDMOLOG, 
    USSDMTLOG, 
    USSDCDRLOG, 
    USSDMORESP, 
    MMSMT, 
    MMSMTRESP, 
    MMSMTLOG, 
    MMSMO, 
    MMSMORESP, 
    MMSMOLOG, 
    MMSDLRYRPT, 
    MMSDLRYRPTRESP, 
    MMSDLRYRPTLOG, 
    MMSREADRPT, 
    MMSREADRPTRESP, 
    MMSREADRPTLOG;
  }

  public static enum MsgDirection
  {
    MO, 
    MT;
  }
}