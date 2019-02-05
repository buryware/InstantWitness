/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
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

import android.graphics.Bitmap;
import android.media.Image;

import com.google.android.gms.maps.model.LatLng;

public class FriendlyMessage {

    private String id;
    private String text;
    private String name;
    private String msgType;
    private String timeStamp;
    private String streamID;
    private String lat;
    private String lng;
    private Bitmap preview;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String mMsgID, String text, String name, String latitude, String longitude, String streamID, String msgType, String timeStamp) {
        this.text = text;
        this.name = name;
        this.msgType = msgType;
        this.lat = latitude;
        this.lng = longitude;
        this.streamID = streamID.toString();
        this.timeStamp = timeStamp;
        this.preview = preview;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLatLng() {

        return new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }


    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setmsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getmsgType() {
        return this.msgType;
    }


    public String getStreamID()  {
        return this.streamID;
    }

    public void setStreamID(String stream) {
        this.streamID = stream;
    }

    public void setTimeStamp(String timestamp) {
        this.timeStamp = timestamp;
    }

    public String getTimeStamp()  {
        return this.timeStamp;
    }

    public void setPriview(Bitmap preview) {
        this.preview = preview;
    }

    public Bitmap getImage()  { return this.preview; }

}
