/*    */ package com.centech.smsgw.common;
/*    */ 
/*    */ import java.util.Date;
/*    */ 
/*    */ public class MOMsg extends Msg
/*    */ {
/*    */   public MOMsg(String msisdn, String shortCode, String keyword, Msg.ContentType contentType, String content, String opCnxId, String opId, String cpCnxId, String cpId, String moId, String extraMOInfo, Date moRxTime, String moRxId)
/*    */   {
/* 50 */     super(Msg.MsgType.MO);
/* 51 */     this.msisdn = msisdn;
/* 52 */     this.shortCode = shortCode;
/* 53 */     this.keyword = keyword;
/* 54 */     this.contentType = contentType;
/* 55 */     this.content = content;
/* 56 */     this.opCnxId = opCnxId;
/* 57 */     this.opId = opId;
/* 58 */     this.cpCnxId = cpCnxId;
/* 59 */     this.cpId = cpId;
/* 60 */     this.moId = moId;
/* 61 */     this.extraMOInfo = extraMOInfo;
/* 62 */     this.moRxTime = moRxTime;
/* 63 */     this.moRxId = moRxId;
/*    */   }
/*    */ /*    */ 
/*    */   public MOMsg()
/*    */   {
/* 70 */     super(Msg.MsgType.MO);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\http-gw\dist\lib\common-msg-20140620.jar
 * Qualified Name:     com.centech.smsgw.common.MOMsg
 * JD-Core Version:    0.6.0
 */