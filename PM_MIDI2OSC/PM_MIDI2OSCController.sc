PM_MIDI2OSCController { // rename PM_MIDI2OSCChannelController ?
    var <channel;
    var <>view;
    var midiMonitorRout;

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

    setView {|aView|
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

    getName {
        ^channel.name;
    }

    setName {|aName|
        channel.name = aName;
    }

    enable {
        ^channel.enabled = true;
    }

    disable {
        ^channel.enabled = false;
    }

    getMidiSrcIDs {
        ^channel.midiSrcIDs;
    }

    getMidiSrcLabels {
        ^channel.midiSrcLabels;
    }

    setMidiSrc {|aMidiSrcIndex|
        ^channel.midiSrcID = this.getMidiSrcIDs[aMidiSrcIndex];
    }

    getMidiChannels {
        ^channel.class.midiChannels;
    }

    setMidiChannel {|aMidiChannelIndex|
        ^channel.midiChannel = this.getMidiChannels[aMidiChannelIndex];
    }

    getMidiMsgTypes {
        ^channel.class.midiMsgTypes;
    }

    getMidiMsgType {
        ^channel.midiMsgType;
    }

    setMidiMsgType {|aMidiMsgType|
        ^channel.midiMsgType = aMidiMsgType
    }

    getMidiNonNumTypes {
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

                    view.updateMidiMonitor(
                        val.asString ++ if(num.isNil, "", " - " ++ num.asString)
                    );

                    0.1.wait;
                }
            }.fork;
        } {
            this.debug(
                "Controller for:" + channel.name ++ Char.nl
                ++ Char.tab ++ "MIDI monitoring STOPPED"
            );
            midiMonitorRout.stop;
        };
    }

    getIp {
        ^channel.ip;
    }

    setIp {|aIp|
        ^channel.ip = aIp;
    }

    getPort {
        ^channel.port;
    }

    setPort {|aPort|
        ^channel.port = aPort;
    }

    getOscAddress {
        ^channel.oscAddress;
    }

    setOSCAddress {|aOscAddress|
        ^channel.oscAddress = aOscAddress;
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