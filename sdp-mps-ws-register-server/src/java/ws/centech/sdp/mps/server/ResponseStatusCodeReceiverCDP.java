/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.centech.sdp.mps.server;

/**
 *
 * @author Administrator
 */
public class ResponseStatusCodeReceiverCDP {
    public static int SUCCESS = 202;
    public static int UNSUCCESS = 204;
    public static int RARAMS_INPUT_IS_NOT_CORRECT = 400;
    public static int AUTHENTICATION_FAILS = 404;
    
    public static int getResponseStatusCode(String code){
        int ret = 0;
        
        if(code.equalsIgnoreCase("SUCCESS")){
            ret = SUCCESS;
        }
        else if(code.equalsIgnoreCase("UNSUCCESS")){
            ret = UNSUCCESS;
        }
        else if(code.equalsIgnoreCase("RARAMS_INPUT_IS_NOT_CORRECT")){
            ret = RARAMS_INPUT_IS_NOT_CORRECT;
        }
        else if(code.equalsIgnoreCase("AUTHENTICATION_FAILS")){
            ret = AUTHENTICATION_FAILS;
        }
        
        return ret;
    }
    
    
    public static String getResponseStatusDesc(int code){
        String ret = "";
        
        if(code == SUCCESS){
            ret = "Accepted";
        }
        else if(code == UNSUCCESS){
            ret = "Not ok (error)";
        }
        else if(code == RARAMS_INPUT_IS_NOT_CORRECT){
            ret = "Invalid data format";
        }
        else if(code == AUTHENTICATION_FAILS){
            ret = "Wrong user name/password";
        }
        
        return ret;
    }  
    
  
}
