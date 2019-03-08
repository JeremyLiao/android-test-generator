package com.jeremyliao.cucumber.study;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView tvStatus;
    private EditText editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvStatus = findViewById(R.id.tv_status);
        editSearch = findViewById(R.id.edit_search);
    }

    private void showSimpleDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    public void onClickTest1(View v) {
        tvStatus.setText("测试一已点击");
    }

    public void onClickTest2(View v) {
        showSimpleDialog("测试二已点击");
    }

    public void onClickTest3(View v) {
        String s = editSearch.getText().toString();
        showSimpleDialog(s);
    }

    public void onClickTest4(View v) {
        showSimpleDialog("测试四已点击");
    }

    public void onClickTest5(View v) {
        tvStatus.setText("测试五已点击");
    }
}
