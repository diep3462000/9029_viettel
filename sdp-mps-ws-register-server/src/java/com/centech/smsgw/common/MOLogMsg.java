/*    */ package com.centech.smsgw.common;
/*    */ 
/*    */ public class MOLogMsg extends Msg
/*    */ {
/*    */   public MOLogMsg(MOMsg moMsg)
/*    */   {
/* 32 */     super(Msg.MsgType.MOLOG);
/*    */ 
/* 34 */     this.msisdn = moMsg.msisdn;
/* 35 */     this.shortCode = moMsg.shortCode;
/* 36 */     this.keyword = moMsg.keyword;
/* 37 */     this.contentType = moMsg.contentType;
/* 38 */     this.content = moMsg.content;
/* 39 */     this.opCnxId = moMsg.opCnxId;
/* 40 */     this.opId = moMsg.opId;
/* 41 */     this.cpCnxId = moMsg.cpCnxId;
/* 42 */     this.cpId = moMsg.cpId;
/* 43 */     this.svcId = moMsg.svcId;
/* 44 */     this.svcName = moMsg.svcName;
/* 45 */     this.moId = moMsg.moId;
/* 46 */     this.mtId = moMsg.mtId;
/* 47 */     this.extraMOInfo = moMsg.extraMOInfo;
/* 48 */     this.cdrCreated = moMsg.cdrCreated;
/* 49 */     this.billingStatus = moMsg.billingStatus;
/* 50 */     this.mtCount = moMsg.mtCount;
/*    */ 
/* 52 */     this.moRxTime = moMsg.moRxTime;
/* 53 */     this.moTxTime = moMsg.moTxTime;
/* 54 */     this.mtRxTime = moMsg.mtRxTime;
/* 55 */     this.mtTxTime = moMsg.mtTxTime;
/*    */ 
/* 57 */     this.txAttempts = moMsg.txAttempts;
/* 58 */     this.txStatus = moMsg.txStatus;
/*    */ 
/* 60 */     this.moRxId = moMsg.moRxId;
/* 61 */     this.mtTxId = moMsg.mtTxId;
/* 62 */     this.opMTDeliveryId = moMsg.opMTDeliveryId;
/* 63 */     this.opMTDeliveryStatus = moMsg.opMTDeliveryStatus;
/*    */ 
/* 65 */     this.mtRxId = moMsg.mtRxId;
/* 66 */     this.moTxId = moMsg.moTxId;
/* 67 */     this.cpMODeliveryId = moMsg.cpMODeliveryId;
/* 68 */     this.cpMODeliveryStatus = moMsg.cpMODeliveryStatus;
/*    */ 
/* 70 */     this.moTxStatus = moMsg.moTxStatus;
/* 71 */     this.mtTxStatus = moMsg.mtTxStatus;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\http-gw\dist\lib\common-msg-20140620.jar
 * Qualified Name:     com.centech.smsgw.common.MOLogMsg
 * JD-Core Version:    0.6.0
 */