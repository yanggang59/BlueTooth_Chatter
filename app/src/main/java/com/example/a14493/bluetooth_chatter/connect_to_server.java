package com.example.a14493.bluetooth_chatter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class connect_to_server extends AppCompatActivity {

    Button connect;

    String ip_address;

    EditText ip_1,ip_2,ip_3,ip_4,port;

    Toast mToast;

    String ip1_text;
    String ip2_text;
    String ip3_text;
    String ip4_text;
    String port_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_server);

        connect=(Button)findViewById(R.id.connect);
        ip_1=(EditText)findViewById(R.id.IP_1);
        ip_2=(EditText)findViewById(R.id.IP_2);
        ip_3=(EditText)findViewById(R.id.IP_3);
        ip_4=(EditText)findViewById(R.id.IP_4);
        port=(EditText)findViewById(R.id.port);

    }


    private void showToast(String text)
    {
        if(mToast==null)
            mToast=Toast.makeText(this,text,Toast.LENGTH_LONG);
        else
            mToast.setText(text);

        mToast.show();
    }

    private boolean urlCorrection()
    {
        if(ip1_text.matches("")||ip2_text.matches("")||ip3_text.matches("")
                ||ip4_text.matches(""))
            return false;
        else
            return true;

    }

    public void connect(View view)
    {
        ip1_text=ip_1.getText().toString();
        ip2_text=ip_2.getText().toString();
        ip3_text=ip_3.getText().toString();
        ip4_text=ip_4.getText().toString();
        port_text=port.getText().toString();
        if(urlCorrection())
        {
            ip_address=ip1_text+"."+ip2_text+"."+ip3_text+"."+ip4_text+":"+port_text;
            Intent intent=getIntent();
            intent.putExtra("ip_address",ip_address);
            setResult(0,intent);
            this.finish();
        }

        else
        {
            showToast("Please make sure the ip format is correct");
            return;
        }

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);//新建一个对话框
        dialog.setMessage("确定要退出吗?");//设置提示信息
        //设置确定按钮并监听
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();//结束当前Activity
            }
        });
        //设置取消按钮并监听
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //这里什么也不用做
            }
        });
        dialog.show();//最后不要忘记把对话框显示出来
        Log.d("Debug","Back Pressed");
    }
}
