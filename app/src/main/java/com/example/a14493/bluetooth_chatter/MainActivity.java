package com.example.a14493.bluetooth_chatter;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a14493.bluetooth_chatter.CombineRelated.CombineDataFrame;
import com.example.a14493.bluetooth_chatter.Protocol.ProtocolHandler;
import com.example.a14493.bluetooth_chatter.SQLiteRelated.MySQLiteHandler;
import com.example.a14493.bluetooth_chatter.UploadToRemoteDataBase.UploadDataHandler;
import com.example.a14493.bluetooth_chatter.bluetooth_connection.Constant;
import com.example.a14493.bluetooth_chatter.controller.BlueToothController;
import com.example.a14493.bluetooth_chatter.controller.ChatController;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 0;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private List<BluetoothDevice> mBondedDeviceList = new ArrayList<>();

    private BlueToothController mController = new BlueToothController();
    private ListView mListView;
    private DeviceAdapter mAdapter;
    private Toast mToast;

    private View mChatPanel;
    private Button mSendBt;
    private EditText mInputBox;
    private TextView mChatContent;
    private Button connect_to_server;

    private StringBuilder mChatText = new StringBuilder();

    private String servletName="CombineDataServlet";

    private String url;

    private static String strAcc="";
    public final String TAG="--调试--";
    private ProtocolHandler protocolHandler;  //协议解析

    private MySQLiteHandler mySQLiteHandler;



    /**
     * 默认为本地存储模式:LOCAL_SAVING_MODE
     */
    private int SAVING_MODE=Constant.LOCAL_SAVING_MODE;



    /**
     * 模式选择：0为exitChatMode，1为enterChatMode
     */
    private static int mode=0; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        exitChatMode();//刚开始的时候不显示聊天面板

        registerBluetoothReceiver();

        protocolHandler=new ProtocolHandler();

        mySQLiteHandler=new MySQLiteHandler(this);

        connect_to_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isNetworkAvailable(MainActivity.this))
                {
                    showToast("网络不可用，请先连接网络");
                    return;
                }
                Intent intent=new Intent(MainActivity.this,connect_to_server.class);

                startActivityForResult(intent,0);

                SAVING_MODE=Constant.LOCAL_AND_REMOTE_SAVING_MODE;

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
        if(requestCode==REQUEST_CODE && resultCode==0)
        {
            Bundle data=intent.getExtras();
            String ip_address=data.getString("ip_address");
            url="http://"+ip_address+"/"+servletName;
            showToast("url is "+url);
        }
    }


    private void initUI() {
        mListView = (ListView) findViewById(R.id.device_list);
        mAdapter = new DeviceAdapter(mDeviceList, this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(bindDeviceClick);
        mChatPanel = findViewById(R.id.chat_panel);
        mSendBt = (Button) findViewById(R.id.bt_send);
        mSendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ext = mInputBox.getText().toString();
                ChatController.getInstance().sendMessage(ext);
                //
                mChatText.append(ext).append("\n");
                mChatContent.setText(mChatText.toString());
               // mChatContent.setGravity(Gravity.RIGHT);
                mInputBox.setText("");
            }
        });
        mInputBox = (EditText) findViewById(R.id.chat_edit);
        mChatContent = (TextView) findViewById(R.id.chat_content);

        connect_to_server=(Button)findViewById(R.id.connect_to_server);
    }

    private void registerBluetoothReceiver() {
        IntentFilter filter = new IntentFilter();
        //开始查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //结束查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //查找设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备扫描模式改变
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        //绑定状态
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(mReceiver, filter);
    }


    private Handler mUIHandler = new MyHandler();

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.MSG_START_LISTENING:
                    //setProgressBarIndeterminateVisibility(true);
                    Log.d(TAG,"MSG_START_LISTENING");
                    break;
                case Constant.MSG_FINISH_LISTENING:
                    //setProgressBarIndeterminateVisibility(false);
                    exitChatMode();
                    Log.d(TAG,"MSG_FINISH_LISTENING");
                    break;
                case Constant.MSG_GOT_DATA:
                    final String tmp=protocolHandler.handleStringRec(msg.obj);
                    /**
                     * 传输的数据格式正确才会存入本地数据库
                     */
                    if(tmp.length()>0)
                    {
                        mChatText.append("RawData:"+tmp+" "+tmp.length()+"  Bytes").append("\n");

                        mChatContent.setText(mChatText.toString());

                        String data=new CombineDataFrame(tmp).getDATA();
                        showToast(data+data.length());

                        //在此处将收到的数据传给数本地据库存储
                        if(SAVING_MODE==Constant.LOCAL_SAVING_MODE)
                        {
                            CombineDataFrame test=new CombineDataFrame(tmp);
                            mySQLiteHandler.addCombineData(test);
                            int i=(int)test.getDATA().charAt(0) & 0xff;
                            showToast(String.valueOf(i));//new CombineDataFrame(tmp).getDATA().charAt(0)
                            //showToast("Data saved into Local DataBase");
                        }
                        else if(SAVING_MODE==Constant.LOCAL_AND_REMOTE_SAVING_MODE)
                        {
                            mySQLiteHandler.addCombineData(new CombineDataFrame(tmp));
                            String params=UploadDataHandler.CombineDataHandler(new CombineDataFrame(tmp));
                            showToast(params);
                            sendPost(url,params);

                        }

                    }
                    break;
                case Constant.MSG_ERROR:
                    Log.d(TAG,"MSG_ERROR");
                    exitChatMode();
                   // showToast("error: "+String.valueOf(msg.obj));
                    break;
                case Constant.MSG_CONNECTED_TO_SERVER:
                    enterChatMode();
                    Log.d(TAG,"MSG_CONNECTED_TO_SERVER");
                    showToast("Connected to Device");
                    break;
                case Constant.MSG_GOT_A_CLINET:
                    enterChatMode();
                    Log.d(TAG,"MSG_GOT_A_CLINET");
                    showToast("Got a Client");
                    break;
            }
        }
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if( BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action) ) {
                setProgressBarIndeterminateVisibility(true);
                //初始化数据列表
                mDeviceList.clear();
                mAdapter.notifyDataSetChanged();
            }
            else if( BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
            }
            else if( BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //找到一个，添加一个
                mDeviceList.add(device);
                mAdapter.notifyDataSetChanged();
            }
            else if( BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,0);
                if( scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    //setProgressBarIndeterminateVisibility(true);
                }
                else {
                   // setProgressBarIndeterminateVisibility(false);
                }
            }
            else if( BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action) ) {
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if( remoteDevice == null ) {
                    showToast("no device");
                    return;
                }
                int status = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,0);
                if( status == BluetoothDevice.BOND_BONDED) {
                    showToast("Bonded " + remoteDevice.getName());
                }
                else if( status == BluetoothDevice.BOND_BONDING){
                    showToast("Bonding " + remoteDevice.getName());
                }
                else if(status == BluetoothDevice.BOND_NONE){
                    showToast("Not bond " + remoteDevice.getName());
                }
            }
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"Paused");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG," Resume 0");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"Stop");
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
        Log.d(TAG,"Back Pressed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatController.getInstance().stopChat();
        unregisterReceiver(mReceiver);
        Log.d(TAG,"on Destroy");
    }


    public void enterChatMode() {
        mListView.setVisibility(View.GONE);
        mChatPanel.setVisibility(View.VISIBLE);
    }

    public void exitChatMode() {
        mListView.setVisibility(View.VISIBLE);
        mChatPanel.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }




    private void showToast(String text) {

        if( mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        }
        else {
            mToast.setText(text);
        }
        mToast.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.enable_visiblity) {
            mController.enableVisibly(this);
        }
        else if( id == R.id.find_device) {
            //查找设备
            mAdapter.refresh(mDeviceList);
            mController.findDevice();
            mListView.setOnItemClickListener(bindDeviceClick);
            exitChatMode();
        }
        else if (id == R.id.open_bluetooth) {
            //打开蓝牙
            mController.turnOnBlueTooth(this, REQUEST_CODE);
            //showToast("蓝牙已打开");

        }
        else if (id == R.id.close_bluetooth) {
            //关闭蓝牙
            mController.turnOffBlueTooth();
            showToast("蓝牙已关闭");

        }
        else if (id == R.id.bonded_device) {
            //查看已绑定设备
            mBondedDeviceList = mController.getBondedDeviceList();
            mAdapter.refresh(mBondedDeviceList);
            mListView.setOnItemClickListener(bindedDeviceClick);
            exitChatMode();
        }
        else if( id == R.id.listening) {
            ChatController.getInstance().waitingForConnection(mController.getAdapter(), mUIHandler);
        }
        else if( id == R.id.stop_listening) {
            showToast("已停止等待连接");
            ChatController.getInstance().stopChat();
            exitChatMode();
        }
        else if( id == R.id.disconnect) {
            if(!mController.isBlueToothOpen())
            {
                showToast("请先打开蓝牙");
                return false;
            }
            ChatController.getInstance().stopChat();
            showToast("已断开连接");
            exitChatMode();
        }
        else if( id == R.id.clearMSG) {
            mChatText.setLength(0);
            mChatContent.setText("");
        }

        return super.onOptionsItemSelected(item);
    }




    private AdapterView.OnItemClickListener bindDeviceClick = new AdapterView.OnItemClickListener() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice device = mDeviceList.get(i);
            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                device.createBond();
            }
        }
    };


    private AdapterView.OnItemClickListener bindedDeviceClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice device = mBondedDeviceList.get(i);
            ChatController.getInstance().startChatWith(device, mController.getAdapter(),mUIHandler);
        }
    };


    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
    }

    public void sendPost(final String url,final String params)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                String response = GetPostUtil.sendPost(url,params);
            }
        }.start();
    }
}



