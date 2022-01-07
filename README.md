[![No Maintenance Intended](http://unmaintained.tech/badge.svg)](http://unmaintained.tech/)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-no-red.svg)](https://bitbucket.org/lbesson/ansi-colors)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# OpenHealthCard-Common Android Library

## Introduction

This part describes the functionality and usability of android common library

## API Documentation

Generated API docs are available at <https://gematik.github.io/ref-OpenHealthCard-Common-Android>.

## License

Licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).

## Overview

This Library contains Controller to inject Android Context to CardReader Provider. For that
extends this Library the cardreader.provider.api functionality for Android. Furthermore
Controller to request Permissions from Provider to handle e.g. card reader.

![OpenHealthCard Common Android library overview](openhealthcard.common/doc/images/OHCCOM/generated/common.png)

  

### AndroidCardReaderController

This class is an android specific extension of [AbstractCardReaderController](#crpapi_cardreadercontroller) from package de.gematik.ti.cardreader.provider.api.
The additional functionalities are android context handling and permissions requests, necessary for android card reader provider.

![OpenHealthCard Common Android library overview](openhealthcard.common/doc/images/OHCCOM/generated/interfaces.png)

  

![OpenHealthCard AbstractCardReaderController](openhealthcard.common/doc/images/OHCCOM/generated/common.png)

  

## Getting Started

### Build setup

To use OpenHealthCard Common library in a project, you need just to include following dependency:

**Gradle dependency settings to use OpenHealthCard Common library.**

    dependencies {
        implementation group: 'de.gematik.ti', name: 'openhealthcard.common', version: '1.1.2'
    }

**Maven dependency settings to use OpenHealthCard Common library.**

    <dependencies>
        <dependency>
            <groupId>de.gematik.ti</groupId>
            <artifactId>openhealthcard.common</artifactId>
            <version>1.1.2</version>
        </dependency>
    </dependencies>
