package com.example.demos.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.demos.R;

import java.util.List;

public class WiFiAdapter extends RecyclerView.Adapter<WiFiAdapter.ViewHolder> {


    private Context context;
    private List<ScanResult> data;
    private OnItemClickListener listener;

    public WiFiAdapter(Context context, List<ScanResult> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public WiFiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_wifi_info,
                viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final WiFiAdapter.ViewHolder viewHolder, int i) {
        if (data == null) {
            return;
        }
        final ScanResult scanResult = data.get(i);
        viewHolder.setData(scanResult);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(viewHolder.getAdapterPosition(), scanResult);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvWiFiName, tvWiFiRssi, tvWiFiFrequency, tvWiFiEncrypt;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWiFiName = itemView.findViewById(R.id.tv_wifi_name);
            tvWiFiRssi = itemView.findViewById(R.id.tv_wifi_rssi);
            tvWiFiFrequency = itemView.findViewById(R.id.tv_wifi_frequency);
            tvWiFiEncrypt = itemView.findViewById(R.id.tv_wifi_wpa);
        }

        void setData(ScanResult scanResult) {
            tvWiFiName.setText(scanResult.SSID);
            tvWiFiRssi.setText(scanResult.level + "");
            int frequency = scanResult.frequency;
            tvWiFiFrequency.setText(frequency > 2400 && frequency < 2500 ? "2.4G" : "5G");
            tvWiFiEncrypt.setText(scanResult.capabilities);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(int position, ScanResult scanResult);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
