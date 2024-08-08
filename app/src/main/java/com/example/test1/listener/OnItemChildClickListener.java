package com.example.test1.listener;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface OnItemChildClickListener {
    void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState, int position);

    void onItemChildClick(int position);
}
