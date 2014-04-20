PM_MIDI2OSCChannel {
    classvar <midiChannels;
    classvar <midiMsgTypes;
    classvar <midiNonNumTypes;
    classvar midiSrcIDs;
    classvar midiSrcLabels;
    classvar controllerMethods;

    var <name;
    var <enabled;
    var midiFunc;
    var <midiChannel;
    var <midiMsgType;
    var <midiSrcID;
    var <midiNum;
    var <midiVal;
    var netAddr;
    var <oscAddress;
    var <latency;
    var <controller;
    var <>debug = true;

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
            \debug ->   [\this, \string]
        ];
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
        name =          aName;
        enabled =       true;
        netAddr =       nil;
        latency =       0.0;
        midiFunc =      nil;
        midiChannel =   0;
        midiMsgType =   \noteOn;
        midiSrcID =     nil; // nil responds to all
        oscAddress =    "/" ++ midiMsgType;
        controller =    nil;

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

    initMidi {
        if(MIDIClient.initialized.not) {
            this.notify(\error, \initMidi,
                "MIDIClient is not initialised, refresh MIDI clients"
            );

            ^[];
        } {
            if(midiSrcIDs.isNil) {
                midiSrcIDs = [nil] ++ MIDIClient.sources.collect (_.uid);
            };
            if(midiSrcLabels.isNil) {
                midiSrcLabels = ["all"] ++ MIDIClient.sources.collect (
                    _.device + ":" + _.name;
                );
            };
        };
    }

    midiSrcIDs {
        this.initMidi;
        ^midiSrcIDs.copy;
    }

    midiSrcLabels {
        this.initMidi;
        ^midiSrcLabels.copy;
    }

    midiChannel_ {|aMidiChannel|
        if(this.class.midiChannels.includes(aMidiChannel).not) {
            this.notify(\error, \midiChannel,
                "Invalid MIDI channel" ++ Char.nl
                ++ "should be one of:" ++ Char.nl
                ++ Char.tab ++ this.class.midiChannels
            );
            ^this;
        };

        midiChannel = aMidiChannel;

        this.notify(\debug, \set,
            "midiChannel:" + midiChannel
        );

        ^this;
    }

    midiMsgType_ {|aMidiMsgType|
        if(this.class.midiMsgTypes.includes(aMidiMsgType).not) {
            this.notify(\error, \midiMsgType,
                "Invalid MIDI message type" ++ Char.nl
                ++ "should be a symbol, one of:" ++ Char.nl
                ++ Char.tab ++ this.class.midiMsgTypes
            );
            ^this;
        };

        midiMsgType = aMidiMsgType;

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

        this.notify(\debug, \set,
            "midiSrcID:" + midiSrcID
        );

        ^this;
    }

    ip {
        if(netAddr.isNil) {
            ^nil;
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

        this.createNetAddr(aIp, this.port);

        ^this.ip;
    }

    port {
        if(netAddr.isNil) {
            ^nil;
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

        this.createNetAddr(this.ip, aPort);

        ^this.port;
    }

    createNetAddr {|aIp, aPort|
        // check existing
        if(netAddr.notNil) {
            // check redundant call
            if(aIp.asSymbol == netAddr.ip.asSymbol && (aPort == netAddr.port)) {
                this.notify(\warning, \netAddr,
                    "same ip and port specified, nothing changed"
                );
                ^netAddr;
            };

            // kill old one
            netAddr.disconnect;
        };

        if(aIp.isNil) {
            this.notify(\error, \ip,
                "IP address not set, using default"
            );
        };
        if(aPort.isNil) {
            this.notify(\error, \port,
                "Port not set, using default"
            );
        };
        netAddr = NetAddr(aIp, aPort);

        this.notify(\debug, \set,
            "netAddr:" + netAddr
        );

        ^netAddr;
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

        this.notify(\debug, \set,
            "latency:" + latency
        );

        ^this;
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
        if(type == \debug && debug.not) {
            ^nil;
        };

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
    }
}