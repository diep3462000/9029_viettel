/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.centech.sdp.mps.server;

/**
 *
 * @author Administrator
 */
public class ResponseStatusCode {
    private static int SUCCESS = 0;
    private static int UNSUCCESS = 1;
    private static int RARAMS_INPUT_IS_NOT_CORRECT = 300;
    private static int AUTHENTICATION_FAILS = 301;
    
    public static int getSUCCESS() {
        return SUCCESS;
    }    
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
            ret = "";
        }
        else if(code == UNSUCCESS){
            ret = "Co loi trong qua trinh xu ly";
        }
        
        return ret;
    }  
    
  
}
