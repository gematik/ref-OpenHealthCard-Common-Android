/*
 * Copyright (c) 2020 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.ti.openhealthcard.common;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

/**
 * BroadcastReceiver for Permission Requests
 *
 */
public class WaitingPermissionReceiver extends BroadcastReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(WaitingPermissionReceiver.class);
    private final String permission;
    private final Context context;
    private final CompletableFuture<Boolean> receivedResult;

    protected WaitingPermissionReceiver(final Context context, final String permission) {
        this.context = context;
        this.permission = permission;
        receivedResult = new CompletableFuture<>();
        registerPermissionReceiver();
    }

    /**
     * Handle intent for permission requests
     *
     * @param context
     *            - Android Application Context
     * @param intent
     *            - permission intent
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

        if (device != null && intent.getAction().equals(permission)) {
            boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);

            LOG.debug("onReceive(): permission request answered for device: {}, id: {}, serialno: {}, permission granted: {}", device.getProductName(),
                    device.getDeviceId(), device.getSerialNumber(), granted);
            receivedResult.complete(granted);
        } else {
            LOG.error("onReceive(): device: {}, action: {}", device, intent.getAction());
        }
    }

    /**
     * Get the result of permission request
     *
     * @param timeout
     *            request timeout
     * @param timeUnit
     *            unit for request timeout value
     * @return true if permissions granted
     * @throws TimeoutException
     *             permission request is not handled at the given time
     * @throws ExecutionException
     *             permission request could not executed
     * @throws InterruptedException
     *             permission request aborted
     */
    boolean get(final long timeout, final TimeUnit timeUnit) throws TimeoutException, ExecutionException, InterruptedException {
        boolean result = false;
        try {
            result = receivedResult.get(timeout, timeUnit);
            LOG.debug("get(): permission request result received, granted: " + result);
        } finally {
            unregisterPermissionReceiver();
        }

        return result;
    }

    private void registerPermissionReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(permission);
        context.registerReceiver(this, filter);
        LOG.debug("registerPermissionReceiver(): broadcast receiver registered for permission: {}", permission);
    }

    private void unregisterPermissionReceiver() {
        context.unregisterReceiver(this);
        LOG.debug("unregisterPermissionReceiver(): broadcast receiver unregistered for permission: {}", permission);
    }
}
