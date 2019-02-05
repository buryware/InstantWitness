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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.View;

public class ErrorAlert implements OnKeyListener{

  private Context mContext;

  public ErrorAlert(Context context) {
    mContext = context;
  }


public void showErrorDialog(final String title, final String message) {
    AlertDialog aDialog = new AlertDialog.Builder(mContext).setMessage(message).setTitle(title)
        .setNeutralButton("Close", new OnClickListener() {
          public void onClick(final DialogInterface dialog,
              final int which) {
            //Prevent to finish activity, if user clicks about.
            /*if (!title.equalsIgnoreCase("About") && !title.equalsIgnoreCase("Directory Error") && !title.equalsIgnoreCase("View")) {
              ((Activity) mContext).finish();
            }*/
            
          }
        }).create();
    aDialog.setOnKeyListener(this);
    aDialog.show();
  }

  @Override
  public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
    if(keyCode == KeyEvent.KEYCODE_BACK){
      //disable the back button
    }
    return true;
  }
  

}