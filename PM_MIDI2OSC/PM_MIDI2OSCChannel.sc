PM_MIDI2OSCChannel {
    classvar <midiChannels;
    classvar <midiMsgTypes;
    classvar <midiNonNumTypes;
    classvar midiSrcIDs;
    classvar <controller;

    var <name;
    var <enabled;
    var netAddr;
    var <latency;
    var midiFunc;
    var <midiChannel;
    var <midiMsgType;
    var <midiSrcID;

    var <>controller;

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

    *addController {|aController|
        controller = aController;
    }

    *error {|string|
        if(controller.notNil) {
            controller.error(string);
        } {
            Post << "----------------------------------" << Char.nl
                 << "ERROR:" << Char.nl
                 << $\t << string.tr(Char.nl, Char.nl ++ Char.tab)
                 << Char.nl;
        };
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
            this.error("name is not a string");
            ^this;
        };
        if(aName.isEmpty) {
            this.error("name is empty");
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
            this.error("Not a boolean");
            ^this;
        };

        enabled = aEnabled;
        ^this;
    }

    latency_ {|aLatency|
        if(aLatency.isNumber.not) {
            this.error("Latency must be a number");
            ^this;
        };
        if(aLatency < 0) {
            this.error("Latency must be greater than 0");
            ^this;
        };

        latency = aLatency;

        ^this;
    }

    midiChannel_ {|aMidiChannel|
        if(this.class.midiChannels.includes(aMidiChannel).not) {
            this.error(
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
            this.error(
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
            this.error(
                "Invalid MIDI source ID" ++ Char.nl
                ++ "should be a symbol, one of:" ++ Char.nl
                ++ Char.tab ++ this.class.midiSrcIDs
            );
            ^this;
        };

        midiSrcID = aMidiSrcID;

        ^this;
    }

    error {|string|
        if(controller.notNil) {
            controller.error(string);
        } {
            this.class.error(string);
        };
    }
}