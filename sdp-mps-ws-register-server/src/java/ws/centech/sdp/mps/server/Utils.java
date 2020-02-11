/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.centech.sdp.mps.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

/**
 *
 * @author dung.t
 */
public class Utils {
/**========================================================================================
 * callHTTPURL
 ========================================================================================*/    
    public static String callHTTPURL(String remote_url){
        String ret = "";
        
        try{
            URL url = new URL(remote_url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;

            BufferedReader reader = new BufferedReader( new InputStreamReader( in )  );

            String line = null;
            while( ( line = reader.readLine() ) != null )  {
               ret += line;
            }
            reader.close();
//            int httpResponseCode = con.getResponseCode();
//            if(httpResponseCode==200) ret[0] = "true";
//            ret = IOUtils.toString(in, encoding);
        }
        catch(Exception ex){
            System.out.println("[callHTTPURL] Exception:" + ex.getMessage());
        }
        
        return ret;
    }      
    
    public static String getStrProperty(Properties properties, String key, String defaultValue) {
        String ret = defaultValue;

        try {
            ret = (properties.getProperty(key)==null) ? defaultValue : properties.getProperty(key);
        } catch (Exception ex) {
        }

        return ret;
    }
    
    public static String genMTSequence18digits(){
        String ret = "";
        
        try{
            SimpleDateFormat smpDateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
            Date now = new Date();
            ret = smpDateFormat.format(now);
            
            Random randomGenerator = new Random();
            String randomStr = new Integer(randomGenerator.nextInt(10)).toString();
            
            ret = ret + randomStr;
        }
        catch(Exception ex){
            
        }
        
        return ret;
    }        

    public static String getPropertyStr(Map<String,String> mapProperties, String key, String defaultValue){
        String ret=defaultValue;
        
        try{
            ret = (mapProperties.get(key)==null) ? "" : mapProperties.get(key);
        }
        catch(Exception ex){
            //System.out.println("[getPropertyStr] Exception: " + ex.getMessage());
        }
        
        return ret;
    }
    
}
