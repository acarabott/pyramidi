/*
This file is part of PyraMIDI2OSC.
Copyright (C) 2014  Arthur Carabott

PyraMIDI2OSC is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

PyraMIDI2OSC is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with PyraMIDI2OSC.  If not, see <http://www.gnu.org/licenses/>.
*/

PM_MIDI2OSCChannel {
    classvar midiChannels;
    classvar midiMsgTypes;
    classvar midiNonNumTypes;
    classvar midiSrcIDs;
    classvar midiSrcLabels;
    classvar controllerMethods;
    classvar defaultPort;

    var <name;
    var <enabled;
    var midiFunc;
    var midiFuncCallback;
    var <midiSrcID;
    var <>midiSrcIDLocked;
    var <midiChannel;
    var <>midiChannelLocked;
    var <midiMsgType;
    var <>midiMsgTypeLocked;
    var <midiNotifying;
    var ip;
    var port;
    var netAddr;
    var <oscAddress;
    var <mapInputMin;
    var <mapInputMax;
    var <mapOutputMin;
    var <mapOutputMax;
    var <outputType;
    var <latency;
    var <testVal1;
    var <testVal2;
    var <controller;

    var storedSettings;
    var <>debugEnabled = false;

    /*
    ==============
    Class
    ==============
    */

    *initClass {
        midiChannels = #[
            nil, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
        ];

        midiMsgTypes = #[
            \noteOn, \noteOff, \control, \program, \bend, \touch, \sysex,
            \polytouch, \sysrt
        ];

        midiNonNumTypes = #[\touch, \program, \bend];

        controllerMethods = IdentityDictionary[
            \error ->   [\this, \key, \string],
            \warning -> [\this, \key, \string],
            \update ->  [\this, \key, \string],
            \debug ->   [\this, \string]
        ];

        defaultPort = 1234;
    }

    *new {|aName|
        ^super.new.midi2oscChannelInit(aName);
    }

    *controllerMethods {
        ^controllerMethods.copy;
    }

    *checkController {|aController|
        ^this.controllerMethods.keys.every {|methodKey|
            var method = aController.class.findMethod(methodKey);
            method.notNil and: {
                method.argNames.includesAll(this.controllerMethods[methodKey])
            };
        };
    }

    *printMessage {|type, string|
        Post << "----------------------------------" << Char.nl
             << type << ":" << Char.nl
             << $\t << string.tr(Char.nl, Char.nl ++ Char.tab)
             << Char.nl;
    }

    /*
    ==============
    Instance
    ==============
    */

    midi2oscChannelInit {|aName|
        name =              aName;
        enabled =           true;
        netAddr =           nil;
        latency =           0.0;
        midiFunc =          nil;
        midiSrcID =         nil; // nil responds to all
        midiSrcIDLocked =   false;
        midiChannel =       nil;
        midiChannelLocked = false;
        midiMsgType =       \noteOn;
        midiMsgTypeLocked = false;
        midiNotifying =     false;
        oscAddress =        "/" ++ midiMsgType;
        port =              defaultPort;
        mapInputMin =       0;
        mapInputMax =       127;
        mapOutputMin =      0;
        mapOutputMax =      127;
        outputType =        Integer;
        testVal1 =          0;
        testVal2 =          0;
        controller =        nil;
        storedSettings =    IdentityDictionary[];

        this.initMidi;
    }

    name_ {|aName|
        if(aName.isString.not) {
            this.notify(\error, \name, "name is not a string");
            ^this;
        };
        if(aName.isEmpty) {
            this.notify(\error, \name, "name is empty");
            ^this;
        };
        if(aName.asSymbol == name.asSymbol) {
            ^this;
        };

        name = aName;

        this.notify(\debug, \set,
            "name:" + name
        );

        ^this;
    }

    enabled_ {|aEnabled|
        if(aEnabled.isKindOf(Boolean).not) {
            this.notify(\error, \enabled, "Not a boolean");
            ^this;
        };

        enabled = aEnabled;

        this.notify(\debug, \set,
            "enabled:" + enabled
        );

        ^this;
    }

    createMidiSrcs {
        ^[nil] ++ MIDIClient.sources.collect (_.uid);
    }

    createMidiSrcLabels {
        ^["all"] ++ MIDIClient.sources.collect (_.device + ":" + _.name);
    }

    initMidi {
        if(midiSrcIDs.isNil) {
            midiSrcIDs = this.createMidiSrcs();
        };
        if(midiSrcLabels.isNil) {
            midiSrcLabels = this.createMidiSrcLabels();
        };

        this.createMidiFuncCallback;
        this.createMidiFunc;

        ^this;
    }

    refreshMidiSrcs {
        midiSrcIDs = this.createMidiSrcs();
        midiSrcLabels = this.createMidiSrcLabels();
    }

    midiSrcIDs {
        ^midiSrcIDs.copy;
    }

    midiSrcLabels {
        ^midiSrcLabels.copy;
    }

    midiChannels {
        // shortcut for classvar
        ^midiChannels.copy;
    }

    midiChannel_ {|aMidiChannel|
        if(midiChannels.includes(aMidiChannel).not) {
            this.notify(\error, \midiChannel,
                "Invalid MIDI channel" ++ Char.nl
                ++ "should be one of:" ++ Char.nl
                ++ Char.tab ++ midiChannels
            );
            ^this;
        };

        midiChannel = aMidiChannel;

        this.createMidiFunc;

        this.notify(\debug, \set,
            "midiChannel:" + midiChannel
        );

        ^this;
    }

    midiMsgTypes {
        // shortcut for classvar
        ^midiMsgTypes.copy;
    }

    midiNonNumTypes {
        // shortcut for classvar
        ^midiNonNumTypes.copy;
    }

    midiMsgType_ {|aMidiMsgType|
        if(midiMsgTypes.includes(aMidiMsgType).not) {
            this.notify(\error, \midiMsgType,
                "Invalid MIDI message type" ++ Char.nl
                ++ "should be a symbol, one of:" ++ Char.nl
                ++ Char.tab ++ midiMsgTypes
            );
            ^this;
        };

        midiMsgType = aMidiMsgType;

        this.createMidiFuncCallback;
        this.createMidiFunc;

        this.notify(\debug, \set,
            "midiMsgType:" + midiMsgType
        );

        ^this;
    }

    midiSrcID_ {|aMidiSrcID|
        var srcIDs = this.midiSrcIDs;

        if(srcIDs.isEmpty) {
            ^this;
        };

        if(srcIDs.includes(aMidiSrcID).not) {
            this.notify(\error, \midiSrcId,
                "Invalid MIDI source ID" ++ Char.nl
                ++ "should be a symbol, one of:" ++ Char.nl
                ++ Char.tab ++ srcIDs
            );
            ^this;
        };

        midiSrcID = aMidiSrcID;

        this.createMidiFunc;

        this.notify(\debug, \set,
            "midiSrcID:" + midiSrcID
        );

        ^this;
    }

    midiNotifying_ {|aMidiNotifying|
        if(aMidiNotifying.isKindOf(Boolean).not) {
            this.notify(\error, \midiNotifying,
                "Invalid valud for midiNotifying, must be boolean"
            );
            ^this;
        };

        midiNotifying = aMidiNotifying;

        if(midiNotifying.not) {
            this.notify(\update, \midiChanged, "");
        };

        this.notify(\debug, \set,
            "midiNotifying:" + midiNotifying
        );

        ^this;
    }

    // ip and port instance variables are only used if there is no netAddr
    ip {
        if(netAddr.isNil) {
            ^ip;
        };

        ^netAddr.ip;
    }

    ip_ {|aIp|
        if(aIp.isString.not) {
            this.notify(\error, \ip,
                "IP address is not a string"
            );
            ^this.ip;
        };
        try {
            aIp.gethostbyname;
        } {|error|
            this.notify(\error, \ip,
                "invalid ip address:" ++ Char.nl
                ++ Char.tab ++ aIp
            );
            ^this.ip;
        };

        ip = aIp;

        this.createNetAddr(ip, this.port);

        ^this;
    }

    port {
        if(netAddr.isNil) {
            ^port;
        };
        ^netAddr.port;
    }

    port_ {|aPort|
        // validate port
        if(aPort.isInteger.not) {
            this.notify(\error, \port,
                "port is not an integer"
            );
            ^this.port;
        };

        port = aPort;

        this.createNetAddr(this.ip, port);

        ^this;
    }

    createNetAddr {|aIp, aPort|
        // check existing
        if(netAddr.notNil) {
            // check redundant call
            if(aIp.asSymbol == netAddr.ip.asSymbol && (aPort == netAddr.port)) {
                this.notify(\warning, \netAddr,
                    "same ip and port specified, nothing changed"
                );
                ^nil;
            };

            // kill old one
            netAddr.disconnect;
        };

        if(aIp.isNil) {
            this.notify(\error, \ip,
                "IP address not set, can't send messages"
            );
            ^nil;
        };
        if(aPort.isNil) {
            this.notify(\error, \port,
                "Port not set, using: " + port
            );
            aPort = port;
        };

        netAddr = NetAddr(aIp, aPort);

        this.notify(\debug, \set,
            "netAddr:" + netAddr
        );

        ^nil;
    }

    oscAddress_ {|aOscAddress|
        if(aOscAddress.isString.not) {
            this.notify(\error, \oscAddress,
                "OSC address must be a string"
            );
            ^this;
        };
        if(aOscAddress.isEmpty) {
            this.notify(\error, \oscAddress,
                "OSC address must be at least one character"
            );
            ^this;
        };

        oscAddress = aOscAddress;
        this.createMidiFuncCallback;

        if(oscAddress[0] != $/) {
            this.notify(\warning, \oscAddress,
                "OSC addresses *should* start with /"
            );
        };
        this.notify(\debug, \set,
            "oscAddress:" + oscAddress
        );

        ^this;
    }

    mapInputMin_ {|aMapInputMin|
        if(aMapInputMin.isInteger.not) {
            this.notify(\error, \mapInputMin,
                "Input range minimum must be an integer"
            );
            ^this;
        };
        mapInputMin = aMapInputMin;
    }

    mapInputMax_ {|aMapInputMax|
        if(aMapInputMax.isInteger.not) {
            this.notify(\error, \mapInputMax,
                "Input range maximum must be an integer"
            );
            ^this;
        };
        mapInputMax = aMapInputMax;
    }

    mapOutputMin_ {|aMapOutputMin|
        if(aMapOutputMin.isNumber.not) {
            this.notify(\error, \mapOutputMin,
                "Output range minimum must be a number"
            );
            ^this;
        };
        mapOutputMin = aMapOutputMin;
    }

    mapOutputMax_ {|aMapOutputMax|
        if(aMapOutputMax.isNumber.not) {
            this.notify(\error, \mapOutputMax,
                "Output range maximum must be a number"
            );
            ^this;
        };
        mapOutputMax = aMapOutputMax;
    }

    outputType_ {|aOutputType|
        if([Float, Integer].includes(aOutputType).not) {
            this.notify(\error, \outputType,
                "Output type is not Float or Integer"
            );
            ^this;
        };
        outputType = aOutputType;
    }

    latency_ {|aLatency|
        if(aLatency.isNumber.not) {
            this.notify(\error, \latency,
                "Latency must be a number"
            );
            ^this;
        };
        if(aLatency < 0) {
            this.notify(\error, \latency,
                "Latency must be greater than 0"
            );
            ^this;
        };

        latency = aLatency;
        this.createMidiFuncCallback;

        this.notify(\debug, \set,
            "latency:" + latency
        );

        ^this;
    }

    createMidiFuncCallback {
        if(midiNonNumTypes.includes(midiMsgType)) {
            midiFuncCallback = {|val, chan, src|
                if(enabled && netAddr.notNil) {
                    SystemClock.sched(latency, {

                        val = val.linlin(
                            mapInputMin, mapInputMax,
                            mapOutputMin, mapOutputMax
                        );

                        if(outputType == Integer) {
                            val = val.asInteger;
                        };
                        netAddr.sendMsg(oscAddress, val);

                        this.notify(\debug, \sendMsg,
                            name ++ Char.nl
                            ++ "sending OSC msg:" + Char.nl
                            ++ Char.tab ++ "val:" + val
                        );
                    });
                };
                this.notify(\debug, \midiIn,
                    name ++ Char.nl
                    ++ "received MIDI:" + Char.nl
                    ++ Char.tab ++ "val:" + val
                );
                if(midiNotifying) {
                    this.notify(\update, \midiChanged,
                        "val:" ++ val
                    );
                };
            };
        } {
            midiFuncCallback = {|val, num, chan, src|
                if(enabled && netAddr.notNil) {
                    SystemClock.sched(latency, {

                        val = val.linlin(
                            mapInputMin, mapInputMax,
                            mapOutputMin, mapOutputMax
                        );

                        if(outputType == Integer) {
                            val = val.asInteger;
                        };
                        netAddr.sendMsg(oscAddress, num, val);

                        this.notify(\debug, \sendMsg,
                            name ++ Char.nl
                            ++ "sending OSC msg:" + Char.nl
                            ++ Char.tab ++ "num:" + num
                            ++ Char.tab ++ "val:" + val
                        );
                    });
                };
                this.notify(\debug, \midiIn,
                    name ++ Char.nl
                    ++ "received MIDI:" + Char.nl
                    ++ Char.tab ++ "num:" + num
                    ++ Char.tab ++ "val:" + val
                );
                if(midiNotifying) {
                    this.notify(\update, \midiChanged,
                        "num:" ++ num ++ ", val:" ++ val
                    );
                };
            };
        };

        this.notify(\debug, \set,
            "midiFuncCallback:" + midiFuncCallback
        );

        this.notify(\update, \midiChanged, "");
        ^nil;
    }

    createMidiFunc {
        var func;

        if(midiChannels.includes(midiChannel).not) {
            this.notify(\error, \key,
                "MIDI channel not set"
            );
            ^nil;
        };
        if(midiMsgTypes.includes(midiMsgType).not) {
            this.notify(\error, \key,
                "MIDI message type not set"
            );
            ^nil;
        };
        if(midiSrcIDs.includes(midiSrcID).not) {
            this.notify(\error, \key,
                "MIDI source ID not set"
            );
            ^nil;
        };

        // free existing if it exists
        midiFunc.free;

        midiFunc = MIDIFunc(
            func:       midiFuncCallback,
            msgNum:     nil, // respond to all vals
            chan:       midiChannel,
            msgType:    midiMsgType,
            srcID:      midiSrcID
        );

        this.notify(\debug, \set,
            "midiFunc" + midiFunc
        );

        this.notify(\update, \midiChanged, "");
        ^nil;
    }

    testVal1_ {|aTestVal|
        if(aTestVal.isInteger.not) {
            this.notify(\error, \testVal1,
                "test value 1 should be an integer"
            );
            ^this;
        };

        testVal1 = aTestVal;

        this.notify(\debug, \set,
            "testVal1:" + testVal1
        );

        ^this;
    }

    testVal2_ {|aTestVal|
        if(aTestVal.isInteger.not) {
            this.notify(\error, \testVal2,
                "test value 2 should be an integer"
            );
            ^this;
        };

        testVal2 = aTestVal;

        this.notify(\debug, \set,
            "testVal2:" + testVal2
        );

        ^this;
    }

    sendTestSignal {
        if(netAddr.isNil) {
            this.notify(\error, \test,
                "ip address not set"
            );
            ^nil;
        };

        if(midiNonNumTypes.includes(midiMsgType)) {
            netAddr.sendMsg(oscAddress, testVal1);

            this.notify(\debug, \sendMsg,
                "sending test OSC msg:" + Char.nl
                ++ Char.tab ++ "val:" + testVal1
            );
        } {
            netAddr.sendMsg(oscAddress, testVal1, testVal2);

            this.notify(\debug, \sendMsg,
                "sending test OSC msg:" + Char.nl
                ++ Char.tab ++ "num:" + testVal1
                ++ Char.tab ++ "val:" + testVal2
            );
        }

        ^this;
    }

    allNoteOff {
        if(netAddr.isNil) {
            this.notify(\error, \test,
                "ip address not set"
            );
            ^nil;
        };

        {
            (1..127).do {|i|
                netAddr.sendMsg(\noteOff, i, 0);
                0.01.wait;
            };
        }.fork;
    }

    controller_ {|aController|
        if(this.class.checkController(aController).not) {
            this.notify(\error, \controller,
                "controller doesn't repond to all methods:" ++ Char.nl
                ++ Char.tab ++ this.class.controllerMethods;
            );
            ^this;
        };

        controller = aController;

        this.notify(\debug, \set,
            "controller:" + controller
        );

        ^this;
    }

    notify {|type, key, string|
        if(type == \debug && debugEnabled.not) {
            ^nil;
        };

        string = string.asString; // ensure it's a string;

        if(controller.notNil) {
            if(controller.respondsTo(type)) {
                if(type == \debug) {
                    controller.perform(\debug,
                        "Channel:" + name ++ Char.nl
                        ++ Char.tab ++ key.asString.toUpper ++ ":" + string
                    );
                } {
                   controller.perform(type, key, string);
                };

            } {
                controller.error(key,
                    "controller doesn't respond to type:" ++ Char.nl
                    ++ Char.tab ++ type.asString
                );
                controller.error(key, string);
            };
        } {
            this.class.printMessage(type.asString.toUpper, string);
        };

        ^this;
    }

    storedSettings {
        this.storeSettings;
        ^storedSettings.copy;
    }

    storeSettings {
        storedSettings['name'] = name;
        storedSettings['enabled'] = enabled;
        storedSettings['midiSrcID'] = midiSrcID;
        storedSettings['midiSrcIDLocked'] = midiSrcIDLocked;
        storedSettings['midiChannel'] = midiChannel;
        storedSettings['midiChannelLocked'] = midiChannelLocked;
        storedSettings['midiMsgType'] = midiMsgType;
        storedSettings['midiMsgTypeLocked'] = midiMsgTypeLocked;
        storedSettings['midiNotifying'] = midiNotifying;
        storedSettings['ip'] = ip;
        storedSettings['port'] = port;
        storedSettings['oscAddress'] = oscAddress;
        storedSettings['latency'] = latency;
        storedSettings['testVal1'] = testVal1;
        storedSettings['testVal2'] = testVal2;


        this.notify(\debug, \settings,
            "stored settings"
        );
    }

    loadSettings {|aSettings|
        if(aSettings.isKindOf(Dictionary).not) {
            this.notify(\error, \settings,
                "settings are not a Dictionary or IdentityDictionary"
            );
            ^false;
        };

        aSettings.keysValuesDo { |key, value|
            if(this.respondsTo(key)) {
                this.perform((key ++ "_").asSymbol, value);
            };
        };

        this.notify(\debug, \settings,
            "settings loaded"
        );
        this.notify(\update, \settingsLoaded, "");

        ^true;
    }

    free {
        if(midiFunc.notNil) {
            midiFunc.free;
        };
        if(netAddr.notNil) {
            netAddr.disconnect;
        };

        ^this;
    }
}