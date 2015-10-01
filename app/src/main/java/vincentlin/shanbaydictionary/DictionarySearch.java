package vincentlin.shanbaydictionary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import org.json.*;


/**
 * Created by Vincent on 15/9/30.
 */
public class DictionarySearch extends Thread {
    private final String mWord;
    private Handler mHandler;
    private static final String URLBASE="https://api.shanbay.com/bdc/search/?word=";
    public DictionarySearch(Handler handler,String word){
        mWord=word;
        mHandler=handler;


    }
    @Override
    public void run(){
        String context=getContext();
        //String msg=parse(context);
        Message msg=new Message();
        context=parse(context);
        msg.what=0;
        msg.obj=context;
        mHandler.sendMessage(msg);
        }

    private String parse(String context){
        StringBuilder out=new StringBuilder();
        try{
            JSONObject json=new JSONObject(context);

            if(json.getInt("status_code")!=0) {
                out.insert(0,"Not found.");
            }
            else{
                JSONObject data=json.getJSONObject("data");
                out.append(data.getString("content"));
                String pron=data.getString("pronunciation");
                if(!pron.equals("")){
                    out.append("    /"+pron+"/");
                }
                out.append("\n");
                //out.append(data.getJSONObject("en_definitions").getJSONArray("n").join("\n"));
                out.append(data.getString("definition").substring(1));
            }

        }
        catch (JSONException e){
            out.delete(0,out.length());
            out.insert(0,"JSON error: "+e.getMessage());

        }
        return out.toString();


    }

    protected String getContext() {
        String context="";
        String urlstr=URLBASE+mWord;
        BufferedReader in=null;
        try {
            URL url = new URL(urlstr);
            URLConnection conn = url.openConnection();
            conn.connect();
            in=new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                context+= line;
            }

        }
        catch (Exception e) {
            Message errorMsg=new Message();
            errorMsg.what=1;
            errorMsg.obj=e;
            mHandler.sendMessage(errorMsg);
        }
        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch(Exception e) {
                Message errorMsg=new Message();
                errorMsg.what=1;
                errorMsg.obj=e;
                mHandler.sendMessage(errorMsg);
            }
        }
        return context;
    }


}

