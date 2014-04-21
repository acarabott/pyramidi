PM_MIDI2OSCChannelController {
    var channel;
    var view;
    var midiMonitorRout;

    // TODO have routine that checks text fields for matches
    // these should check if the view is actually visible
    *initClass {
    }

    *new {|aChannel|
        ^super.new.midi2oscControllerInit(aChannel);
    }

    midi2oscControllerInit {|aChannel|
        this.channel = aChannel; // use custom setter
    }

    // adding a channel automatically assigns this controller to that channel
    channel_ {|aChannel|
        if(aChannel.isKindOf(PM_MIDI2OSCChannel).not) {
            this.error(\channel,
                "channel is not a PM_MIDI2OSCChannel"
            );
            ^this;
        };
        channel = aChannel;
        channel.controller = this;

        this.debug(
            "Controller for:" + channel.name ++ Char.nl
            ++ Char.tab ++ "SET: channel:" + channel
        );

        ^this;
    }

    view_ {|aView|
        // TODO remove when class implemented
        // if(aView.isKindOf(PM_MIDI2OSCChannelView).not) {
        //     this.error(\view,
        //         "view is not a PM_MIDI2OSCChannelView"
        //     );
        //     ^this;
        // };

        view = aView;
        view.controller = this;

        this.debug(
            "Controller for:" + channel.name ++ Char.nl
            ++ Char.tab ++ "SET: view:" + view
        );

        ^this;
    }

    name {
        ^channel.name;
    }

    name_ {|aName|
        channel.name = aName;
        ^this;
    }

    enabled {
        ^channel.enabled;
    }

    enabled_ {|aEnabled|
        channel.enabled = aEnabled;
        ^this;
    }

    midiSrcIDs {
        ^channel.midiSrcIDs;
    }

    midiSrcLabels {
        ^channel.midiSrcLabels;
    }

    midiSrc_ {|aMidiSrcIndex|
        channel.midiSrcID = this.midiSrcIDs[aMidiSrcIndex];
        ^this;
    }

    midiChannels {
        ^channel.class.midiChannels;
    }

    midiChannel_ {|aMidiChannelIndex|
        channel.midiChannel = this.midiChannels[aMidiChannelIndex];
        ^this;
    }

    midiMsgTypes {
        ^channel.class.midiMsgTypes;
    }

    midiMsgType {
        ^channel.midiMsgType;
    }

    midiMsgType_ {|aMidiMsgType|
        channel.midiMsgType = aMidiMsgType;
        ^this;
    }

    midiNonNumTypes {
        ^channel.class.midiNonNumTypes;
    }

    monitorMidi_ {|aMonitorMidi|
        if(aMonitorMidi) {
            this.debug(
                "Controller for:" + channel.name ++ Char.nl
                ++ Char.tab ++ "MIDI monitoring STARTED"
            );
            midiMonitorRout = {
                inf.do {
                    var val = channel.midiVal,
                        num = channel.midiNum;

                    view['updateMidiMonitor'].(
                    // view.updateMidiMonitor( //TODO swap this out
                        val.asString ++ if(num.isNil, "", ", " ++ num.asString)
                    );
                    0.1.wait;
                }
            }.fork(AppClock);
        } {
            this.debug(
                "Controller for:" + channel.name ++ Char.nl
                ++ Char.tab ++ "MIDI monitoring STOPPED"
            );
            midiMonitorRout.stop;
        };

        ^this;
    }

    ip {
        ^channel.ip;
    }

    ip_ {|aIp|
        channel.ip = aIp;
        ^this;
    }

    port {
        ^channel.port;
    }

    port_ {|aPort|
        channel.port = aPort;
        ^this;
    }

    oscAddress {
        ^channel.oscAddress;
    }

    oscAddress_ {|aOscAddress|
        channel.oscAddress = aOscAddress;
        ^this;
    }

    latency {
        ^channel.latency;
    }

    latency_ {|aLatency|
        channel.latency = aLatency;
        ^this;
    }

    testVal1 {
        ^channel.testVal1;
    }

    testVal2 {
        ^channel.testVal2;
    }

    testVal1_ {|aTestVal|
        channel.testVal1 = aTestVal;
        ^this;
    }

    testVal2_ {|aTestVal|
        channel.testVal2 = aTestVal;
        ^this;
    }

    sendTestSignal {
        ^channel.sendTestSignal;
    }

    error {|key, string|

        switch (key)
            {\name}    {
                // TODO error on the interface
            }
            {}    {};

        this.printMessage("ERROR", string);
        // TODO implement error on view, if no view, print
    }

    warning {|key, string|
        this.printMessage("WARNING", string);
    }

    debug {|string|
        this.printMessage("DEBUG", string);
    }

    printMessage {|type, string|
        Post << "------------Controller------------" << Char.nl
             << type << ":" << Char.nl
             << $\t << string.tr(Char.nl, Char.nl ++ Char.tab)
             << Char.nl;
    }

    free {
        midiMonitorRout.stop;
    }
}