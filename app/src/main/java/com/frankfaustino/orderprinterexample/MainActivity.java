package com.frankfaustino.orderprinterexample;

import android.accounts.Account;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.util.CloverAuth;
import com.clover.sdk.v1.BindingException;
import com.clover.sdk.v1.ClientException;
import com.clover.sdk.v1.ServiceException;
import com.clover.sdk.v1.printer.job.PrintJob;
import com.clover.sdk.v1.printer.job.StaticOrderPrintJob;
import com.clover.sdk.v3.order.LineItem;
import com.clover.sdk.v3.order.Order;
import com.clover.sdk.v3.order.OrderConnector;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String PAYLOAD = "payload";
    public static final String TAG = "🔮";
    private Account account;
    private OrderConnector orderConnector;

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
            new GetAccountDetails().execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (orderConnector != null) {
            orderConnector.disconnect();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        String orderId = intent.getStringExtra(PAYLOAD);
        Log.i(TAG, "orderId " + orderId);
        new OrderAsyncTask().execute(orderId);
    }

    private class GetAccountDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                CloverAuth.AuthResult authResult = CloverAuth.authenticate(getApplicationContext(), account);
                final String authToken = authResult.authToken;
                final String merchantId = authResult.merchantId;

                Log.i(TAG, "authToken " + authToken);
                Log.i(TAG, "merchantId " + merchantId);
            } catch (AuthenticatorException | OperationCanceledException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private class OrderAsyncTask extends AsyncTask<String, Void, Order> {
        Order order;

        @Override
        protected final Order doInBackground(String... params) {
            if (params[0] == null) {
                return order;
            }

            try {
                Log.d(TAG, params[0]);
                order = orderConnector.getOrder(params[0]);

                PrintJob printJob = new StaticOrderPrintJob.Builder().markPrinted(true).order(order).build();
                printJob.print(getApplicationContext(), account);
                Log.i(TAG, "orderTitle " + order.getTitle());
                return order;
            } catch (ClientException | ServiceException | BindingException | RemoteException e) {
                e.printStackTrace();
            }
            return order;
        }
    }
}
