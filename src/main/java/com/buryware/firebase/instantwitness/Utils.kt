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

import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

fun <T> observing(initialValue: T,
                  willSet: () -> Unit = { },
                  didSet: () -> Unit = { }
) = object : ObservableProperty<T>(initialValue) {
    override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean =
            true.apply { willSet() }

    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) = didSet()
}