/*    */ package centech.sdp.smsgw.common;
/*    */ 
/*    */ import java.util.Date;
/*    */ 
/*    */ public class MTMsg extends Msg
/*    */ {
/*    */   public MTMsg(String msisdn, String shortCode, Msg.ContentType contentType, String content, String opCnxId, String opId, String cpCnxId, String cpId, String svcId, String svcName, String moId, String mtId, Msg.BillingStatus billingStatus, int mtCount, Date mtRxTime)
/*    */   {
/* 52 */     super(Msg.MsgType.MT);
/* 53 */     this.msisdn = msisdn;
/* 54 */     this.shortCode = shortCode;
/* 55 */     this.contentType = contentType;
/* 56 */     this.content = content;
/* 57 */     this.opCnxId = opCnxId;
/* 58 */     this.opId = opId;
/* 59 */     this.cpCnxId = cpCnxId;
/* 60 */     this.cpId = cpId;
/* 61 */     this.svcId = svcId;
/* 62 */     this.svcName = svcName;
/* 63 */     this.moId = moId;
/* 64 */     this.mtId = mtId;
/* 65 */     this.billingStatus = billingStatus;
/* 66 */     this.mtCount = mtCount;
/* 67 */     this.mtRxTime = mtRxTime;
/*    */   }
/*    */ /*    */ 
/*    */   public MTMsg()
/*    */   {
/* 74 */     super(Msg.MsgType.MT);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\http-gw\dist\lib\common-msg-20140620.jar
 * Qualified Name:     com.centech.smsgw.common.MTMsg
 * JD-Core Version:    0.6.0
 */