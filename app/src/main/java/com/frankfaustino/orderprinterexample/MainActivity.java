package com.frankfaustino.orderprinterexample;

import android.accounts.Account;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.v1.BindingException;
import com.clover.sdk.v1.ClientException;
import com.clover.sdk.v1.ServiceException;
import com.clover.sdk.v1.printer.job.PrintJob;
import com.clover.sdk.v1.printer.job.StaticOrderPrintJob;
import com.clover.sdk.v3.order.LineItem;
import com.clover.sdk.v3.order.Order;
import com.clover.sdk.v3.order.OrderConnector;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Account account;
    String TAG = "🔮";
    OrderConnector orderConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        account = CloverAccount.getAccount(getApplicationContext());

        if (account != null) {
            Log.d(TAG, "getAccount succeeded with " + account.name);
        } else {
            Log.d(TAG, "getAccount failed");
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (orderConnector == null) {
            orderConnector = new OrderConnector(getApplicationContext(), account, null);
            orderConnector.connect();
            new OrderAsyncTask().execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (orderConnector != null) {
            orderConnector.disconnect();
        }
    }


    private class OrderAsyncTask extends AsyncTask<Void, Void, Order> {
        Order order;

        @Override
        protected final Order doInBackground(Void... params) {
            try {
                order = orderConnector.getOrder("N0X6K2GMNXSSA");
                List<LineItem> list = order.getLineItems();


                PrintJob printJob = new StaticOrderPrintJob.Builder().markPrinted(true).order(order).build();
                printJob.print(getApplicationContext(), account);
                Log.d(TAG, order.getTitle());
                return order;
            } catch (ClientException | ServiceException | BindingException | RemoteException e) {
                e.printStackTrace();
            }
            return order;
        }
    }
}
