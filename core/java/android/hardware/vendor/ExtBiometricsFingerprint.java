/*
 * Copyright (C) 2018 The LineageOS Project
 * Copyright (C) 2018 CypherOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.hardware.vendor;

import android.content.Context;
import android.content.res.Resources;
import android.os.HwBinder;
import android.os.HwParcel;
import android.os.IHwBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.R;

public class ExtBiometricsFingerprint {
    public static final int MMI_TYPE_NAV_ENABLE = 41;
    public static final int MMI_TYPE_NAV_DISABLE = 42;

    private static final String TAG = "VendorBiomectricsB2Hal";

    private static String mBiometricsDescriptor;
    private static int mBiometricsTransactionId;

    private static IHwBinder sBiometricsFingerprint;

    public ExtBiometricsFingerprint(Context context) throws RemoteException {
        mBiometricsDescriptor = context.getResources().getString(
                R.string.config_vendorBiometricsDescriptor);
        mBiometricsTransactionId = context.getResources().getInteger(
                R.integer.config_vendorBiometricsTransactionId);
        sBiometricsFingerprint = HwBinder.getService(mBiometricsDescriptor, "default");
    }

    public int sendCmdToHal(int cmdId) {
        if (sBiometricsFingerprint == null) {
            return -1;
        }

        if (TextUtils.isEmpty(mBiometricsDescriptor)) {
            Log.d(TAG, "Descriptor returned null");
            return -1;
        }

        HwParcel data = new HwParcel();
        HwParcel reply = new HwParcel();
 
        try {
            data.writeInterfaceToken(mBiometricsDescriptor);
            data.writeInt32(cmdId);

            sBiometricsFingerprint.transact(mBiometricsTransactionId, data, reply, 0);

            reply.verifySuccess();
            data.releaseTemporaryStorage();

            return reply.readInt32();
        } catch (Throwable t) {
            return -1;
        } finally {
            reply.release();
        }
    }
}