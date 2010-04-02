/*
 *  Copyright 2010 Ryszard Wiśniewski <brut.alll@gmail.com>.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package brut.androlib.res.data.value;

import android.util.TypedValue;
import brut.androlib.AndrolibException;
import brut.androlib.res.data.ResPackage;
import brut.util.Duo;

/**
 * @author Ryszard Wiśniewski <brut.alll@gmail.com>
 */
public class ResValueFactory {
    private final ResPackage mPackage;

    public ResValueFactory(ResPackage pakage_) {
        this.mPackage = pakage_;
    }

    public ResScalarValue factory(int type, int value)
            throws AndrolibException {
        switch (type) {
            case TypedValue.TYPE_REFERENCE:
                return newReference(value);
            case TypedValue.TYPE_ATTRIBUTE:
                return newReference(value, true);
            case TypedValue.TYPE_FLOAT:
                return new ResFloatValue(Float.intBitsToFloat(value));
            case TypedValue.TYPE_DIMENSION:
                return new ResDimenValue(value);
            case TypedValue.TYPE_FRACTION:
                return new ResFractionValue(value);
            case TypedValue.TYPE_INT_BOOLEAN:
                return new ResBoolValue(value != 0);
        }

        if (type >= TypedValue.TYPE_FIRST_COLOR_INT
                && type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return new ResColorValue(value);
        }
        if (type >= TypedValue.TYPE_FIRST_INT
                && type <= TypedValue.TYPE_LAST_INT) {
            return new ResIntValue(value);
        }

        throw new AndrolibException("Invalid value type: "+ type);
    }

    public ResValue factory(String value) {
        if (value.startsWith("res/")) {
            return new ResFileValue(value);
        }
        return new ResStringValue(value);
    }

    public ResBagValue bagFactory(int parent,
            Duo<Integer, ResScalarValue>[] items) throws AndrolibException {
        ResReferenceValue parentVal = newReference(parent);

        if (items.length == 0) {
            return new ResBagValue(parentVal);
        }
        int key = items[0].m1;
        if (key == ResAttr.BAG_KEY_ATTR_TYPE) {
            return ResAttr.factory(parentVal, items, this);
        }
        if (key == ResArrayValue.BAG_KEY_ARRAY_START) {
            return new ResArrayValue(parentVal, items);
        }
        if (key >= ResPluralsValue.BAG_KEY_PLURALS_START
                && key <= ResPluralsValue.BAG_KEY_PLURALS_END) {
            return new ResPluralsValue(parentVal, items);
        }
        return new ResStyleValue(parentVal, items, this);
    }

    public ResReferenceValue newReference(int resID) {
        return newReference(resID, false);
    }

    public ResReferenceValue newReference(int resID, boolean theme) {
        return new ResReferenceValue(mPackage, resID, theme);
    }
}
