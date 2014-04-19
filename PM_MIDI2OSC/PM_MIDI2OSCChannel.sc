PM_MIDI2OSCChannel {
    classvar <midiChannels;
    classvar <midiMsgTypes;
    classvar <midiNonNumTypes;
    classvar midiSrcIDs;
    classvar <controllerMethods;

    var <name;
    var <enabled;
    var netAddr;
    var <latency;
    var midiFunc;
    var <midiChannel;
    var <midiMsgType;
    var <midiSrcID;

    var <controller;

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
            \touch, \sysex, \polytouch, \control, \noteOff, \program, \noteOn,
            \bend, \sysrt
        ];

        midiNonNumTypes = #[\touch, \program, \bend];

        controllerMethods = #[\error, \warning];
    }

    *new {|aName|
        ^super.new.midi2oscChannelInit(aName);
    }

    *midiSrcIDs {
        if(MIDIClient.initialized.not) {
            this.error("MIDIClient is not initialised, do MIDIClient.init;");

            ^[];
        } {
            if(midiSrcIDs.isNil) {
                midiSrcIDs = [nil] ++ MIDIClient.sources.collect (_.uid);
            };
        };

        ^midiSrcIDs.copy;
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
        midiChannel =   \unset; // nil would respond to all
        midiMsgType =   nil;
        midiSrcID =     \unset; // nil would respond to all
        controller =    nil;
    }

    name_ {|aName|
        if(aName.isString.not) {
            this.notify(\error, "name is not a string");
            ^this;
        };
        if(aName.isEmpty) {
            this.notify(\error, "name is empty");
            ^this;
        };
        if(aName.asSymbol == name.asSymbol) {
            ^this;
        };

        name = aName;

        ^this;
    }

    enabled_ {|aEnabled|
        if(aEnabled.isKindOf(Boolean).not) {
            this.notify(\error, "Not a boolean");
            ^this;
        };

        enabled = aEnabled;
        ^this;
    }

    createNetAddr {|ip, port|
        // validate IP
        if(ip.isString.not) {
            this.notify(\error, "IP address is not a string");
            ^netAddr;
        };
        try {
            ip.gethostbyname;
        } {|error|
            this.notify(\error,
                "invalid ip address:" ++ Char.nl
                ++ Char.tab ++ ip
            );
            ^netAddr;
        };

        // validate port
        if(port.isInteger.not) {
            this.notify(\error, "port is not an integer");
            ^netAddr;
        };

        // check existing
        if(netAddr.notNil) {
            // check redundant call
            if(ip.asSymbol == netAddr.ip.asSymbol && (port == netAddr.port)) {
                this.notify(\warning,
                    "same ip and port specified, nothing changed"
                );
                ^netAddr;
            };

            // kill old one
            netAddr.disconnect;
        };

        // create
        netAddr = NetAddr(ip, port);

        ^netAddr;
    }

    ip {
        ^netAddr.ip;
    }

    port {
        ^netAddr.port;
    }

    latency_ {|aLatency|
        if(aLatency.isNumber.not) {
            this.notify(\error, "Latency must be a number");
            ^this;
        };
        if(aLatency < 0) {
            this.notify(\error, "Latency must be greater than 0");
            ^this;
        };

        latency = aLatency;

        ^this;
    }

    midiChannel_ {|aMidiChannel|
        if(this.class.midiChannels.includes(aMidiChannel).not) {
            this.notify(\error,
                "Invalid MIDI channel" ++ Char.nl
                ++ "should be one of:" ++ Char.nl
                ++ Char.tab ++ this.class.midiChannels
            );
            ^this;
        };

        midiChannel = aMidiChannel;

        ^this;
    }

    midiMsgType_ {|aMidiMsgType|
        if(this.class.midiMsgTypes.includes(aMidiMsgType).not) {
            this.notify(\error,
                "Invalid MIDI message type" ++ Char.nl
                ++ "should be a symbol, one of:" ++ Char.nl
                ++ Char.tab ++ this.class.midiMsgTypes
            );
            ^this;
        };

        midiMsgType = aMidiMsgType;

        ^this;
    }

    midiSrcID_ {|aMidiSrcID|
        if(MIDIClient.initialized.not) {
            this.class.midiSrcIDs;
            ^this;
        };

        if(this.class.midiSrcIDs.includes(aMidiSrcID).not) {
            this.notify(\error,
                "Invalid MIDI source ID" ++ Char.nl
                ++ "should be a symbol, one of:" ++ Char.nl
                ++ Char.tab ++ this.class.midiSrcIDs
            );
            ^this;
        };

        midiSrcID = aMidiSrcID;

        ^this;
    }

    controller_ {|aController|
        if(this.class.controllerMethods.every (aController.respondsTo(_)).not) {
            this.notify(\error,
                "controller doesn't respond to one of:" ++ Char.nl
                ++ Char.tab ++ this.class.controllerMethods;
            );
            ^this;
        };

        controller = aController;
        ^this;
    }

    notify {|type, string|
        if(controller.notNil) {
            if(controller.respondsTo(type)) {
                controller.perform(type, string);
            } {
                controller.error(
                    "controller doesn't respond to type:" ++ Char.nl
                    ++ Char.tab ++ type.asString
                );
                controller.error(string);
            };
        } {
            this.class.printMessage(type.asString.toUpper, string);
        };
    }
}

