PM_MIDI2OSCChannel {
    classvar midiChannels = #[nil, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
                              14, 15];
    classvar midiMsgTypes = #[  \touch, \sysex, \polytouch, \control, \noteOff,
                                \program, \noteOn, \bend, \sysrt ];
    classvar midiNonNumTypes = #[\touch, \program, \bend];
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
    }

    *new {|aName|
        ^super.new.midi2oscChannelInit(aName);
    }

    *midiSrcIDs {
        if(MIDIClient.initialized.not) {
            error("MIDIClient is not initialised, do MIDIClient.init;");

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
            Post << "----------------------------------" ++ Char.nl
                 ++ "ERROR:" ++ Char.nl
                 ++ $\t ++ string ++ Char.nl;
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
        midiChannel =   -1; // nil would respond to all
        midiMsgType =   nil;
        midiSrcID =     -1; // nil would respond to all
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

    error {|string|
        if(controller.notNil) {
            controller.error(string);
        } {
            this.class.error(string);
        };
    }
}