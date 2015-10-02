package vincentlin.shanbaydictionary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.os.Handler;
import android.widget.TextView;

import java.net.NetworkInterface;

public class MainActivity extends AppCompatActivity {
    private Thread mThreadSearch=null;
    private Handler mHandler=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initListeners();
        initHandler();
    }

    private void initHandler() {
        mHandler=new Handler(){
            public void handleMessage(Message msg)
            {
                switch (msg.what){
                    case 0:
                        ((TextView)findViewById(R.id.tvResult)).setText((String)msg.obj);
                        break;
                    case 1:
                        //((TextView)findViewById(R.id.tvResult)).setText(msg.obj.toString());
                        alert("Error",msg.obj.toString());
                }
                super.handleMessage(msg);
            }
        };
    }

    private void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.etSearch).getApplicationWindowToken(), 0);
    }
    private void initListeners() {
        //EditText-OnKey
        final EditText etSearch=(EditText)findViewById((R.id.etSearch));
        etSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (    event != null &&
                        event.getAction()==KeyEvent.ACTION_DOWN&&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER    ) {
                    //EditText _etSearch=(EditText)findViewById((R.id.etSearch));
                    hideKeyboard();
                    if (isNetworkAvailable()) {
                        //etSearch.clearFocus();
                        //((TextView)findViewById(R.id.tvResult)).setText("clicked");
                        String word = etSearch.getText().toString();
                        mThreadSearch = new DictionarySearch(mHandler, word);
                        mThreadSearch.start();
                    } else {
                        alert("Error", "Can't access internet.");
                    }
                    return true;
                }
            return false;
        }
    });

        //EditText-OnTextChanged
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((TextView) findViewById(R.id.tvResult)).setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void alert(String title,String msg)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title).setMessage(msg).setPositiveButton("OK", null).show();
    }
    private Boolean isNetworkAvailable()
    {
        Boolean bConnected=false;
        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni=cm.getActiveNetworkInfo();
        if(ni!=null&&ni.isAvailable())
            bConnected=true;
        return bConnected;

    }
}
