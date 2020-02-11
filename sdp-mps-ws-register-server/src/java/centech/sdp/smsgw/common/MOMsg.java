package centech.sdp.smsgw.common;

import java.util.Date;

public class MOMsg extends Msg {

    public MOMsg(String msisdn, String shortCode, String keyword, Msg.ContentType contentType, String content, String opCnxId, String opId, String cpCnxId, String cpId, String moId, String extraMOInfo, Date moRxTime, String moRxId) {
        super(Msg.MsgType.MO);
        this.msisdn = msisdn;
        this.shortCode = shortCode;
        this.keyword = keyword;
        this.contentType = contentType;
        this.content = content;
        this.opCnxId = opCnxId;
        this.opId = opId;
        this.cpCnxId = cpCnxId;
        this.cpId = cpId;
        this.moId = moId;
        this.extraMOInfo = extraMOInfo;
        this.moRxTime = moRxTime;
        this.moRxId = moRxId;
    }

    public MOMsg() {
        super(Msg.MsgType.MO);
    }
}