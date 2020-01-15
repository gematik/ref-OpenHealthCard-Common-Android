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

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WaitingPermissionReceiverTest {

    @Test
    public void onReceive() {
    }

    @Test
    public void doWorkflow() throws InterruptedException, ExecutionException, TimeoutException {
        Context context = Mockito.mock(Context.class);
        String permission = "permission";
        WaitingPermissionReceiver receiver = new WaitingPermissionReceiver(context, permission);
        verify(context, times(1)).registerReceiver(ArgumentMatchers.isA(WaitingPermissionReceiver.class), ArgumentMatchers.any(IntentFilter.class));

        Intent intent = Mockito.mock(Intent.class);
        UsbDevice usbdevice = Mockito.mock(UsbDevice.class);
        Mockito.when(usbdevice.getDeviceId()).thenReturn(1234);
        Mockito.when(usbdevice.getSerialNumber()).thenReturn("JunitDeviceSerial");
        Mockito.when(usbdevice.getProductName()).thenReturn("JunitDeviceProductName");


        Mockito.when(intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)).thenReturn(usbdevice);
        Mockito.when(intent.getAction()).thenReturn(permission);
        Mockito.when(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,false)).thenReturn(true);

        receiver.onReceive(context,intent);

        boolean result = receiver.get(1000, TimeUnit.MICROSECONDS);
        Assert.assertTrue(result);
    }

    @Test(expected = TimeoutException.class)
    public void doWorkflowTimeout2() throws InterruptedException, ExecutionException, TimeoutException {
        Context context = Mockito.mock(Context.class);
        String permission = "permission";
        WaitingPermissionReceiver receiver = new WaitingPermissionReceiver(context, permission);
        verify(context, times(1)).registerReceiver(ArgumentMatchers.isA(WaitingPermissionReceiver.class), ArgumentMatchers.any(IntentFilter.class));

        Intent intent = Mockito.mock(Intent.class);
        UsbDevice usbdevice = Mockito.mock(UsbDevice.class);
        Mockito.when(intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)).thenReturn(usbdevice);
        Mockito.when(intent.getAction()).thenReturn("WrongPermissions");

        receiver.onReceive(context,intent);

        boolean result = receiver.get(1000, TimeUnit.MICROSECONDS);
        Assert.assertTrue(result);
    }

    @Test(expected = TimeoutException.class)
    public void doWorkflowTimeout() throws InterruptedException, ExecutionException, TimeoutException {
        Context context = Mockito.mock(Context.class);
        String permission = "permission";
        WaitingPermissionReceiver receiver = new WaitingPermissionReceiver(context, permission);
        verify(context, times(1)).registerReceiver(ArgumentMatchers.isA(WaitingPermissionReceiver.class), ArgumentMatchers.any(IntentFilter.class));

        Intent intent = Mockito.mock(Intent.class);
        Mockito.when(intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)).thenReturn(null);
        receiver.onReceive(context,intent);

        receiver.get(1000, TimeUnit.MICROSECONDS);
    }
}
