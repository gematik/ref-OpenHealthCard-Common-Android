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

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import de.gematik.ti.cardreader.provider.api.card.CardException;

import de.gematik.ti.cardreader.provider.api.ICardReader;
import de.gematik.ti.openhealthcard.common.exceptions.runtime.NotInitilizedWithContextException;
import de.gematik.ti.openhealthcard.common.exceptions.runtime.WrongThreadException;

public class AbstractAndroidCardReaderControllerTest {

    private AbstractAndroidCardReaderController cardReaderController;

    @Before
    public void setup() {
        cardReaderController = new AbstractAndroidCardReaderController() {
            @Override
            public Collection<ICardReader> getCardReaders() throws CardException {
                return new ArrayList<>();
            }
        };
    }

    @Test
    public void getSetContext() {
        Assert.assertNull(cardReaderController.getContext());
        Context context = Mockito.mock(Context.class);
        cardReaderController.setContext(context);
        Assert.assertNotNull(cardReaderController.getContext());
        Assert.assertEquals(context, cardReaderController.getContext());
    }

    @Test(expected = NotInitilizedWithContextException.class)
    public void checkContextException() {
        cardReaderController.checkContext();
    }

    @Test
    public void checkContextWithoutException() {
        cardReaderController.setContext(Mockito.mock(Context.class));
        cardReaderController.checkContext();
    }

    @Ignore // only AndroidStudioTest
    @Test(expected = WrongThreadException.class)
    public void waitForPermissionWrongThreadException() {
        cardReaderController.waitForPermission(Mockito.mock(UsbDevice.class), null);
    }

    // only commandline test
    @Test(expected = IllegalArgumentException.class)
    public void waitForPermissionIllegalArgumentException() {
        cardReaderController.waitForPermission(Mockito.mock(UsbDevice.class), null);
    }

    // only commandline test
    @Test
    public void waitForPermissionTrue() {
        waitForPermission(true);
    }

    // only commandline test
    @Test
    public void waitForPermissionFalse() {
        waitForPermission(false);
    }

    public void waitForPermission(boolean permissionsOk) {
        String permissions = "permissions";
        Intent intent = Mockito.mock(Intent.class);
        UsbDevice usbdevice = Mockito.mock(UsbDevice.class);
        Mockito.when(usbdevice.getDeviceId()).thenReturn(1234);
        Mockito.when(usbdevice.getSerialNumber()).thenReturn("JunitDeviceSerial");

        Mockito.when(intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)).thenReturn(usbdevice);
        Mockito.when(intent.getAction()).thenReturn(permissions);
        Mockito.when(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)).thenReturn(permissionsOk);

        UsbManager usbManager = Mockito.mock(UsbManager.class);
        Context context = Mockito.mock(Context.class);

        Mockito.when(context.registerReceiver(ArgumentMatchers.any(WaitingPermissionReceiver.class), ArgumentMatchers.isA(IntentFilter.class)))
                .thenAnswer((Answer) invocation -> {
                    Object[] args = invocation.getArguments();
                    ((WaitingPermissionReceiver) args[0]).onReceive(context, intent);
                    return Mockito.mock(Intent.class);
                });

        cardReaderController.setContext(context);
        cardReaderController.checkContext();
        Mockito.when(context.getSystemService(Context.USB_SERVICE)).thenReturn(usbManager);

        boolean result = cardReaderController.waitForPermission(usbdevice, permissions);
        Assert.assertEquals(permissionsOk, result);
    }

    @Test
    public void waitForPermissionTimeout() throws Exception {
        String permissions = "permissions";
        Intent intent = Mockito.mock(Intent.class);
        UsbDevice usbdevice = Mockito.mock(UsbDevice.class);
        Mockito.when(usbdevice.getDeviceId()).thenReturn(1234);
        Mockito.when(usbdevice.getSerialNumber()).thenReturn("JunitDeviceSerial");

        Mockito.when(intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)).thenReturn(usbdevice);
        Mockito.when(intent.getAction()).thenReturn(permissions);
        Mockito.when(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)).thenReturn(true);

        UsbManager usbManager = Mockito.mock(UsbManager.class);
        Context context = Mockito.mock(Context.class);

        cardReaderController.setContext(context);
        cardReaderController.checkContext();
        Mockito.when(context.getSystemService(Context.USB_SERVICE)).thenReturn(usbManager);

        boolean result = cardReaderController.waitForPermission(usbdevice, permissions);
        Assert.assertFalse(result);
    }
}
