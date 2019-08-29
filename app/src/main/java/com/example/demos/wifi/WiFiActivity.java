package com.example.demos.wifi;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.demos.R;
import com.example.demos.restriction.funny.addworddemo.util.LogUtils;

import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class WiFiActivity extends AppCompatActivity {

    private static final String TAG = "WiFiActivity";
    private RecyclerView recyclerView;
    private List<ScanResult> mWifiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi);
        init();
    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerView);
        requestPermission();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION) != PERMISSION_GRANTED) {
                // 检查权限状态
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    //  用户彻底拒绝授予权限，一般会提示用户进入设置权限界面
                } else {
                    //  用户未彻底拒绝授予权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    // 申请成功
                    scanWifiInfo();
                } else {
                    // 申请失败
                }
            }
        }
    }

    /**
     * 扫描附近wifi
     */
    private void scanWifiInfo() {
        WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiManager.setWifiEnabled(true);
        mWifiManager.startScan();
        if (mWifiList != null) {
            mWifiList.clear();
        }
        mWifiList = mWifiManager.getScanResults();
        LogUtils.d(TAG, "wifi List : " + mWifiList);
        WiFiAdapter wiFiAdapter = new WiFiAdapter(this, mWifiList);
        wiFiAdapter.setOnItemClickListener(new WiFiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ScanResult scanResult) {
                showConnectWifiDialog(scanResult);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(wiFiAdapter);

    }

    private void showConnectWifiDialog(ScanResult scanResult) {
//      new ConnectWifiDialog(this).show();
        connectWifi(scanResult.SSID,"","OPEN");


    }

    /**
     * 连接wifi
     * @param targetSsid wifi的SSID
     * @param targetPsd 密码
     * @param enc 加密类型
     */
    public void connectWifi(String targetSsid, String targetPsd, String enc) {
        // 1、注意热点和密码均包含引号，此处需要需要转义引号
        String ssid = "\"" + targetSsid + "\"";
        String psd = "\"" + targetPsd + "\"";

        //2、配置wifi信息
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
        switch (enc) {
            case "WEP":
                // 加密类型为WEP
                conf.wepKeys[0] = psd;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case "WPA":
                // 加密类型为WPA
                conf.preSharedKey = psd;
                break;
            case "OPEN":
                //开放网络
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        //3、链接wifi
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals(ssid)) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }
}
