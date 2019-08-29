package com.example.demos.wifi;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.demos.R;

public class ConnectWifiDialog extends AlertDialog {
    private static final String TAG = "ConnectWifiDialog";
    private EditText etPwd;
    private TextView cancel;
    private TextView ok;
    private OnClickListener positiveClickListener;
    private OnClickListener negativeClickListener;

    protected ConnectWifiDialog(@NonNull Context context) {
        super(context);
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_connect_wifi, null, false);
        findView(inflate);
        setContentView(inflate);
    }

    private void findView(View inflate) {
        etPwd = inflate.findViewById(R.id.et_pwd);
        cancel = inflate.findViewById(R.id.tv_cancel);
        ok = inflate.findViewById(R.id.tv_ok);
    }

    public void setOnPositiveClickListener(OnClickListener positiveClickListener) {
        this.positiveClickListener = positiveClickListener;
    }

    public void setOnNegativeClickListener(OnClickListener negativeClickListener) {
        this.negativeClickListener = negativeClickListener;
    }

}
