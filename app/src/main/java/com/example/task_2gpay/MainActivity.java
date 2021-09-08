package com.example.task_2gpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.task_2gpay.databinding.ActivityMainBinding;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    public static final String GOOGLE_PAY_PACKAGE_NAME="com.google.android.apps.nbu.paisa.user";
    int GOOGLE_PAY_REQUEST_CODE=123;
    String amount;
    //String name="Nikhil Munjral";
    //String upiId="nikhilmunjralnk-2@okaxis";
    String name;
    String upiId;
    String transactionNote="pay test";
    String status;
    Uri uri;
    Pattern ptrn = Pattern.compile("^(.+)@(.+)$");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.gpaybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upiId=binding.upiId1.getText().toString();
                name=binding.name1.getText().toString();
                amount=binding.amount.getText().toString();
                if(!amount.isEmpty() && !upiId.isEmpty() && !name.isEmpty()){
                    uri= getUpiPaymentUri(name,upiId,transactionNote,amount);
                    if (!ptrn.matcher(upiId).matches())
                    {
                        Toast.makeText(MainActivity.this, "Enter valid UPI", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        payWithGPay();
                    }

                }else{
                    binding.amount.setError("Amount is required");
                    binding.upiId1.setError("ID is required");
                    binding.name1.setError("Name is required");

                }

            }
        });
    }

    private void payWithGPay() {
        if (isAppInstalled(this,GOOGLE_PAY_PACKAGE_NAME)){
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
            startActivityForResult(intent,GOOGLE_PAY_REQUEST_CODE);
        }
        else{
            Toast.makeText(this, "Please Install GPay", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(data!=null){
            status=data.getStringExtra("Status").toLowerCase();
        }
        if((RESULT_OK ==requestCode) && status.equals("success")){
            Toast.makeText(this, "Transaction Successfull", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Transaction Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean isAppInstalled(Context context,String packageName) {
        try{
            context.getPackageManager().getApplicationInfo(packageName,0);
            return true;
        }catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }

    private Uri getUpiPaymentUri(String name,String upi_Id,String transactionNote,String amount) {
        return  new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa",upi_Id)
                .appendQueryParameter("pn",name)
                .appendQueryParameter("tn",transactionNote)
                .appendQueryParameter("am",amount)
                .appendQueryParameter("inr","INR").build();
    }
}