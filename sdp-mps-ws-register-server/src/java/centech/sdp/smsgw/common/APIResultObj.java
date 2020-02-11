/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centech.sdp.smsgw.common;

/**
 *
 * @author Administrator
 */
public class APIResultObj {
    public String _apiResult = null;
    public String _contentName = null;
    public String _contentPrice = null;
    public String _contentLink = null;
    public String _mtContentToEndUser = null;

    public String getMtContentToEndUser() {
        return _mtContentToEndUser;
    }

    public void setMtContentToEndUser(String _mtContentToEndUser) {
        this._mtContentToEndUser = _mtContentToEndUser;
    }
    
    public String getApiResult() {
        return _apiResult;
    }

    public void setApiResult(String _apiResult) {
        this._apiResult = _apiResult;
    }

    public String getContentName() {
        return _contentName;
    }

    public void setContentName(String _contentName) {
        this._contentName = _contentName;
    }

    public String getContentPrice() {
        return _contentPrice;
    }

    public void setContentPrice(String _contentPrice) {
        this._contentPrice = _contentPrice;
    }

    public String getContentLink() {
        return _contentLink;
    }

    public void setContentLink(String _contentLink) {
        this._contentLink = _contentLink;
    }
    
    
}
