PM_MIDI2OSCChannelController {
    var channel;
    var <view;
    var midiMonitorRout;
    var debugEnabled = true;

    // TODO have routine that checks text fields for matches
    // these should check if the view is actually visible
    *initClass {
    }

    *new {|aChannel, aView|
        ^super.new.midi2oscControllerInit(aChannel, aView);
    }

    midi2oscControllerInit {|aChannel, aView|
        // use custom setters
        this.channel = aChannel;
        this.view = aView;
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
        if(view.notNil) {
            view.update;
        };

        this.debug(
            "Controller for:" + channel.name ++ Char.nl
            ++ Char.tab ++ "SET: channel:" + channel
        );

        ^this;
    }

    view_ {|aView|
        if(aView.isKindOf(PM_MIDI2OSCChannelView).not) {
            this.error(\view,
                "view is not a PM_MIDI2OSCChannelView"
            );
            ^this;
        };

        view = aView;
        view.controller = this;
        if(channel.notNil) {
            view.update;
        };

        this.debug(
            "Controller:" + this ++ Char.nl
            ++ Char.tab ++ "SET: view:" + view
        );

        ^this;
    }

    getName {
        ^channel.name;
    }

    setName {|aName|
        channel.name = aName;
        ^this;
    }

    getEnabled {
        ^channel.enabled;
    }

    setEnabled {|aEnabled|
        channel.enabled = aEnabled;
        ^this;
    }

    getMidiSrcIDs {
        ^channel.midiSrcIDs;
    }

    getMidiSrcLabels {
        ^channel.midiSrcLabels;
    }

    getMidiSrcIndex {
        ^channel.midiSrcIDs.indexOf(channel.midiSrcID);
    }

    setMidiSrc {|aMidiSrcIndex|
        channel.midiSrcID = channel.midiSrcIDs[aMidiSrcIndex];
        ^this;
    }

    getMidiChannels {
        ^channel.midiChannels;
    }

    setMidiChannelFromIndex {|aMidiChannelIndex|
        channel.midiChannel = channel.midiChannels[aMidiChannelIndex];
        ^this;
    }

    getMidiChannelLabels {
        ^channel.midiChannels.collect {|x|
            if(x.isNil) {
                "all"
            } {
                (x + 1).asString;
            };
        };
    }

    getMidiChannelIndex {
        ^channel.midiChannels.indexOf(channel.midiChannel);
    }

    getMidiMsgTypes {
        ^channel.midiMsgTypes;
    }

    getMidiMsgType {
        ^channel.midiMsgType;
    }

    setMidiMsgType {|aMidiMsgType|
        channel.midiMsgType = aMidiMsgType;
        ^this;
    }

    getMidiMsgTypeIndex {
        ^channel.midiMsgTypes.indexOf(channel.midiMsgType);
    }

    getMidiNonNumTypes {
        ^channel.midiNonNumTypes;
    }

    monitorMidi_ {|aMonitorMidi|
        if(aMonitorMidi) {
            this.debug(
                "Controller for:" + channel.name ++ Char.nl
                ++ Char.tab ++ "MIDI monitoring STARTED"
            );
            midiMonitorRout.stop;
            midiMonitorRout = {
                inf.do {
                    var val = channel.midiVal,
                        num = channel.midiNum;

                    view.updateMidiMonitor(
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
            midiMonitorRout = nil;
        };

        ^this;
    }

    isMonitoring {
        ^midiMonitorRout.isNil.not;
    }

    getIp {
        ^channel.ip;
    }

    setIp {|aIp|
        channel.ip = aIp;
        ^this;
    }

    getPort {
        ^channel.port;
    }

    setPort {|aPort|
        channel.port = aPort;
        ^this;
    }

    getOscAddress {
        ^channel.oscAddress;
    }

    setOscAddress {|aOscAddress|
        channel.oscAddress = aOscAddress;
        ^this;
    }

    getLatency {
        ^channel.latency;
    }

    setLatency {|aLatency|
        channel.latency = aLatency;
        ^this;
    }

    getTestVal1 {
        ^channel.testVal1;
    }

    getTestVal2 {
        ^channel.testVal2;
    }

    setTestVal1 {|aTestVal|
        channel.testVal1 = aTestVal;
        ^this;
    }

    setTestVal2 {|aTestVal|
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
        if(debugEnabled) {
            this.printMessage("DEBUG", string);
        };
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