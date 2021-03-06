package uk.ac.abertay.cmp400.java_app;

import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DisplayHolder extends RecyclerView.ViewHolder{

    final TextView mTitle;
    final WebView mDesc;

    public DisplayHolder(@NonNull View itemView) {
        super(itemView);

        this.mDesc = itemView.findViewById(R.id.RowDescription);
        this.mTitle = itemView.findViewById(R.id.RowTitle);
    }
}
