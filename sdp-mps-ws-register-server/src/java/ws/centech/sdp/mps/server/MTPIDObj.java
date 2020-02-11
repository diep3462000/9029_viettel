/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.centech.sdp.mps.server;

/**
 *
 * @author Administrator
 */
public class MTPIDObj {
    public String _PID = null;
    public String _contentName = null;
    public String _mtContentToEndUser = null;

    public String getPID() {
        return _PID;
    }

    public void setPID(String _PID) {
        this._PID = _PID;
    }

    public String getContentName() {
        return _contentName;
    }

    public void setContentName(String _contentName) {
        this._contentName = _contentName;
    }

    public String getMtContentToEndUser() {
        return _mtContentToEndUser;
    }

    public void setMtContentToEndUser(String _mtContentToEndUser) {
        this._mtContentToEndUser = _mtContentToEndUser;
    }

    
}
