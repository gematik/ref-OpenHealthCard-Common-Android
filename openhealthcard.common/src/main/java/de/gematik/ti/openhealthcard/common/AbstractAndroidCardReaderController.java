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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import de.gematik.ti.cardreader.provider.api.AbstractCardReaderController;
import de.gematik.ti.openhealthcard.common.exceptions.runtime.NotInitilizedWithContextException;
import de.gematik.ti.openhealthcard.common.exceptions.runtime.WrongThreadException;
import de.gematik.ti.openhealthcard.common.interfaces.IAndroidCardReaderController;

/**
 * include::{userguide}/OHCCOM_Overview.adoc[tag=AbstractAndroidCardReaderController]
 *
 */
public abstract class AbstractAndroidCardReaderController extends AbstractCardReaderController implements IAndroidCardReaderController {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractAndroidCardReaderController.class);
    private static final long MAX_SECONDS_WAIT_FOR_PERMISSION = 30;
    private final ReentrantLock permissionWaitingLock = new ReentrantLock();
    private Context context;

    /**
     * Check if the ApplicationContext is set
     * 
     * @throws NotInitilizedWithContextException
     *             - if Android ApplicationContext is null
     */
    protected void checkContext() {
        if (context == null) {
            throw new NotInitilizedWithContextException();
        }
    }

    /**
     * Get the Android Application Context if set otherwise return it null
     *
     * @return Android Application Context
     */
    protected Context getContext() {
        return context;
    }

    /**
     * Set the Android Application Context für CardReaderController
     *
     * @param context
     *            Android Application Context
     */
    @Override
    public void setContext(final Context context) {
        this.context = context;
    }

    /**
     * Waits for a USB card reader device to get the permission accessing it. This methods creates a broadcast receiver and calls requestPermission with USB
     * device and the necessary permission string for that particular device. It waits until broadcast was received. Granting information of received broadcast
     * is returned. This method must not be called on main thread in order to prevent blocking it.
     *
     * @param device
     *            device for which permission is requested
     * @param permission
     *            permission which shall be requested for this device
     * @return permission granted
     * @throws de.gematik.ti.openhealthcard.common.exceptions.runtime.WrongThreadException
     *             thrown if the method is called on main thread
     */
    public boolean waitForPermission(final UsbDevice device, final String permission) {
        if ("main".equals(Thread.currentThread().getName())) {
            throw new WrongThreadException("Do NOT call this method on main thread, it might block it too long");
        }
        if (permission == null || permission.length() == 0) {
            throw new IllegalArgumentException("waitForPermission(): permission must not be empty.");
        }

        // serialize all parallel permission requests
        boolean result = false;
        permissionWaitingLock.lock();
        try {
            result = requestPermissionAndWait(device, permission);
            LOG.debug("waitForPermission(): granted: " + result);
        } finally {
            permissionWaitingLock.unlock();
        }
        return result;
    }

    private boolean requestPermissionAndWait(final UsbDevice device, final String permission) {
        boolean result = false;
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        if (usbManager != null) {
            WaitingPermissionReceiver receiver = new WaitingPermissionReceiver(context, permission);
            Intent intent = new Intent(permission);
            usbManager.requestPermission(device, PendingIntent.getBroadcast(context, device.getDeviceId(), intent, 0));
            LOG.debug("requestPermissionAndWait(): permission for device {}, id: {}, serialno: {} requested", device.getProductName(), device.getDeviceId(),
                    device.getSerialNumber());
            try {
                result = receiver.get(MAX_SECONDS_WAIT_FOR_PERMISSION, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                LOG.error("requestPermissionAndWait(): timeout while waiting for permission result", e);
            } catch (Exception e) {
                LOG.error("requestPermissionAndWait(): Exception occured while waiting for future to complete", e);
            }
        } else {
            LOG.error("requestPermissionAndWait(): No connection to UsbManager possible");
        }

        return result;
    }
}
