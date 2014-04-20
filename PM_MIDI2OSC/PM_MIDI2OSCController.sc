PM_MIDI2OSCController { // rename PM_MIDI2OSCChannelController ?
    var <channel;
    var <>view;

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
            this.error("channel is not a PM_MIDI2OSCChannel");
            ^this;
        };
        channel = aChannel;
        channel.controller = this;
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

    error {|key, string|
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
}