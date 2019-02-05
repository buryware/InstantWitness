/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Instant Witnesses is written by Steve Stansbury  for Jason Nelson
 *
 * Created May 25, 2018 by the Buryware.
 */
package com.buryware.firebase.instantwitness;

import java.io.IOException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.buryware.firebase.instantwitness.*;

import static java.lang.Thread.sleep;

public class PinEntryView extends Activity {

	private static final int PIN_CODE_SET = 3;
	private static final int PIN_CODE_RESET = 4;
	private static final int PIN_CODE_SOUND = 5;

	private static final int PIN_CODE_OK = 6;
	private static final int PIN_CODE_WRONG = 7;

	private static final int PIN_CODE_RETURN = 8;

	private static final String PIN_CODE_DEFUALT = "1234";

	String userEntered;
	String userPin="8888";
	int mPinCode;
	
	final int PIN_LENGTH = 4;
	boolean keyPadLockedFlag = false;
	Context appContext;
	
	TextView titleView;
	
	TextView pinBox0;
	TextView pinBox1;
	TextView pinBox2;
	TextView pinBox3;

	TextView [] pinBoxArray;
	
	TextView statusView;
	
	Button button0;
	Button button1;
	Button button2;
	Button button3;
	Button button4;
	Button button5;
	Button button6;
	Button button7;
	Button button8;
	Button button9;
	Button button10;
	Button buttonExit;
	Button buttonDelete;

	Boolean bNewPin = false;
    Intent PinCodeIntent = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		appContext = this;
		userEntered = "";
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_pin_entry_view);

        PinCodeIntent = getIntent();
        TextView tv = findViewById(R.id.titleBox);
        final int pincode = Integer.parseInt(PinCodeIntent.getStringExtra("pincode_state"));

        if (pincode == PIN_CODE_SET ) {

            tv.setText("Enter New PIN");
            bNewPin = true;

        } else if (pincode == PIN_CODE_RESET || pincode == PIN_CODE_SOUND) {

            tv.setText("Enter your PIN");
        }
		
		Typeface xpressive=Typeface.createFromAsset(getAssets(), "fonts/XpressiveBold.ttf");  
		
		buttonExit = findViewById(R.id.buttonExit);
		buttonExit.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {

				PinEntryView.super.onBackPressed();
		    }
		    
		    }
		);
		buttonExit.setTypeface(xpressive);
		
		
		buttonDelete = findViewById(R.id.buttonDeleteBack);
		buttonDelete.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {

		        if (keyPadLockedFlag == true)
		    	{
		    		return;
		    	}

		    	if (userEntered.length()>0)
		    	{
		    		userEntered = userEntered.substring(0,userEntered.length()-1);
		    		pinBoxArray[userEntered.length()].setText("");
		    	}
		    }
		    
		    }
		);
		
		titleView = findViewById(R.id.titleBox);
		titleView.setTypeface(xpressive);
		
		pinBox0 = findViewById(R.id.pinBox0);
		pinBox1 = findViewById(R.id.pinBox1);
		pinBox2 = findViewById(R.id.pinBox2);
		pinBox3 = findViewById(R.id.pinBox3);
		
		pinBoxArray = new TextView[PIN_LENGTH];
		pinBoxArray[0] = pinBox0;
		pinBoxArray[1] = pinBox1;
		pinBoxArray[2] = pinBox2;
		pinBoxArray[3] = pinBox3;

		statusView = findViewById(R.id.statusMessage);
		statusView.setTypeface(xpressive);
	
		View.OnClickListener pinButtonHandler = new View.OnClickListener() {
		    public void onClick(View v) {
		    	
            if (keyPadLockedFlag == true)
            {
                return;
            }

            Button pressedButton = (Button)v;

            if (userEntered.length()<PIN_LENGTH)
            {
                userEntered = userEntered + pressedButton.getText();
                Log.v("PinView", "User entered="+userEntered);

                //Update pin boxes
                pinBoxArray[userEntered.length()-1].setText("8");

                if (userEntered.length() == PIN_LENGTH)
                {
                    PinCodeIntent = getIntent();

                    if (bNewPin) {
                        Log.v("PinView", "Your new PIN");
                        statusView.setTextColor(Color.GREEN);
                        statusView.setText("Write down our new PIN : " + userEntered);

                        PinCodeIntent.putExtra("pincode_state", PIN_CODE_SET);
                        PinCodeIntent.putExtra("pincode_number", userEntered);
						setResult(RESULT_OK, PinCodeIntent);
						mPinCode = PIN_CODE_OK;

                    }
                    //Check if entered PIN is correct
                    else if (userEntered.equals(userPin) || userEntered.equals(PIN_CODE_DEFUALT))
                    {
                        Log.v("PinView", "Correct PIN");
                        statusView.setTextColor(Color.GREEN);
                        statusView.setText("Correct");

                        PinCodeIntent.putExtra("pincode_state", PIN_CODE_OK);
						setResult(RESULT_OK, PinCodeIntent);
						mPinCode = PIN_CODE_OK;
                    }
                    else
                    {
                        statusView.setTextColor(Color.RED);
                        statusView.setText("Wrong PIN. Keypad Locked");
                        keyPadLockedFlag = true;
                        Log.v("PinView", "Wrong PIN");

                        PinCodeIntent.putExtra("pincode_state", PIN_CODE_WRONG);
						setResult(RESULT_OK, PinCodeIntent);
						mPinCode = PIN_CODE_WRONG;
                    }

                    new LockKeyPadOperation().execute("");
                }
            }
            else
            {
                //Roll over
                pinBoxArray[0].setText("");
                pinBoxArray[1].setText("");
                pinBoxArray[2].setText("");
                pinBoxArray[3].setText("");

                userEntered = "";

                statusView.setText("");

                userEntered = userEntered + pressedButton.getText();
                Log.v("PinView", "User entered="+userEntered);

                //Update pin boxes
                pinBoxArray[userEntered.length()-1].setText("8");

            }
		    }
		};

		button0 = findViewById(R.id.button0);
		button0.setTypeface(xpressive);
		button0.setOnClickListener(pinButtonHandler);
		
		button1 = findViewById(R.id.button1);
		button1.setTypeface(xpressive);
		button1.setOnClickListener(pinButtonHandler);
		
		button2 = findViewById(R.id.button2);
		button2.setTypeface(xpressive);
		button2.setOnClickListener(pinButtonHandler);
		
		
		button3 = findViewById(R.id.button3);
		button3.setTypeface(xpressive);
		button3.setOnClickListener(pinButtonHandler);
		
		button4 = findViewById(R.id.button4);
		button4.setTypeface(xpressive);
		button4.setOnClickListener(pinButtonHandler);
		
		button5 = findViewById(R.id.button5);
		button5.setTypeface(xpressive);
		button5.setOnClickListener(pinButtonHandler);
		
		button6 = findViewById(R.id.button6);
		button6.setTypeface(xpressive);
		button6.setOnClickListener(pinButtonHandler);
		
		button7 = findViewById(R.id.button7);
		button7.setTypeface(xpressive);
		button7.setOnClickListener(pinButtonHandler);
		
		button8 = findViewById(R.id.button8);
		button8.setTypeface(xpressive);
		button8.setOnClickListener(pinButtonHandler);
		
		button9 = findViewById(R.id.button9);
		button9.setTypeface(xpressive);
		button9.setOnClickListener(pinButtonHandler);

		buttonDelete = findViewById(R.id.buttonDeleteBack);
		buttonDelete.setTypeface(xpressive);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		//App not allowed to go back to Parent activity until correct pin entered.
		//return;
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_pin_entry_view, menu);
		return true;
	}

	
	private class LockKeyPadOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
              for(int i=0;i<2;i++) {
                  try {
                      sleep(2000);
                  } catch (InterruptedException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                  }
              }

              return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
                statusView.setText("");

            	//Roll over
				pinBoxArray[0].setText("");
                pinBoxArray[1].setText("");
                pinBoxArray[2].setText("");
                pinBoxArray[3].setText("");

                userEntered = "";

                keyPadLockedFlag = false;

                bNewPin = false;
                finishActivity(mPinCode);
                finish();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
  }
}
