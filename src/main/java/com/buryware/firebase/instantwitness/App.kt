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
package com.buryware.firebase.instantwitness

import android.app.Application
import android.util.Log
import io.firekast.Firekast

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Firekast.setLogLevel(Log.VERBOSE)
        Firekast.initialize(this, "05c21b7c-4539-42f8-a87a-6485a5ecd5a0", "78b89fc0-a346-4ef7-acbf-71e983d74ce8")
    }

}