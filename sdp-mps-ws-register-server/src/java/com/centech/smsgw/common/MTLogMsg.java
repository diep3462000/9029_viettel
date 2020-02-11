/*    */ package com.centech.smsgw.common;
/*    */ 
/*    */ public class MTLogMsg extends Msg
/*    */ {
/*    */   public MTLogMsg(MTMsg mtMsg)
/*    */   {
/* 32 */     super(Msg.MsgType.MTLOG);
/*    */ 
/* 34 */     this.msisdn = mtMsg.msisdn;
/* 35 */     this.shortCode = mtMsg.shortCode;
/* 36 */     this.keyword = mtMsg.keyword;
/* 37 */     this.contentType = mtMsg.contentType;
/* 38 */     this.content = mtMsg.content;
/* 39 */     this.opCnxId = mtMsg.opCnxId;
/* 40 */     this.opId = mtMsg.opId;
/* 41 */     this.cpCnxId = mtMsg.cpCnxId;
/* 42 */     this.cpId = mtMsg.cpId;
/* 43 */     this.svcId = mtMsg.svcId;
/* 44 */     this.svcName = mtMsg.svcName;
/* 45 */     this.moId = mtMsg.moId;
/* 46 */     this.mtId = mtMsg.mtId;
/* 47 */     this.extraMOInfo = mtMsg.extraMOInfo;
/* 48 */     this.cdrCreated = mtMsg.cdrCreated;
/* 49 */     this.billingStatus = mtMsg.billingStatus;
/* 50 */     this.mtCount = mtMsg.mtCount;
/*    */ 
/* 52 */     this.moRxTime = mtMsg.moRxTime;
/* 53 */     this.moTxTime = mtMsg.moTxTime;
/* 54 */     this.mtRxTime = mtMsg.mtRxTime;
/* 55 */     this.mtTxTime = mtMsg.mtTxTime;
/*    */ 
/* 57 */     this.txAttempts = mtMsg.txAttempts;
/* 58 */     this.txStatus = mtMsg.txStatus;
/*    */ 
/* 60 */     this.moRxId = mtMsg.moRxId;
/* 61 */     this.mtTxId = mtMsg.mtTxId;
/* 62 */     this.opMTDeliveryId = mtMsg.opMTDeliveryId;
/* 63 */     this.opMTDeliveryStatus = mtMsg.opMTDeliveryStatus;
/*    */ 
/* 65 */     this.mtRxId = mtMsg.mtRxId;
/* 66 */     this.moTxId = mtMsg.moTxId;
/* 67 */     this.cpMODeliveryId = mtMsg.cpMODeliveryId;
/* 68 */     this.cpMODeliveryStatus = mtMsg.cpMODeliveryStatus;
/*    */ 
/* 70 */     this.moTxStatus = mtMsg.moTxStatus;
/* 71 */     this.mtTxStatus = mtMsg.mtTxStatus;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\http-gw\dist\lib\common-msg-20140620.jar
 * Qualified Name:     com.centech.smsgw.common.MTLogMsg
 * JD-Core Version:    0.6.0
 */