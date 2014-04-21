PM_MIDI2OSCChannelController {
    var channel;
    var <view;
    var <>debugEnabled = false;
    var parent;

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

    parent_ {|aParent|
        if(aParent.isKindOf(PM_MIDI2OSC).not) {
            this.error(\parent,
                "parent needs to be a PM_MIDI2OSC"
            );
            ^this;
        };

        parent = aParent;
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

    getMidiNotifying {
        ^channel.midiNotifying;
    }

    setMidiNotifying {|aMidiNotifying|
        channel.midiNotifying =  aMidiNotifying;
        ^this;
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

    copySettings {
        if(parent.isNil) {
            this.error(\parent,
                "parent is nil"
            );
            ^nil;
        };

        parent.clipboard = channel.storedSettings;

        ^nil;
    }

    pasteSettings {
        if(parent.isNil) {
            this.error(\parent,
                "parent is nil"
            );
            ^nil;
        };
        if(parent.clipboard.isNil) {
            this.error(\clipboard,
                "clipboard is empty"
            );
            ^nil;
        };

        channel.loadSettings(parent.clipboard);

        ^this;
    }

    saveSettings {
        Dialog.savePanel {|path|
            channel.storedSettings.writeArchive(path);
            this.update(\save,
                "settings saved at: " ++ Char.nl
                ++ Char.tab ++ path
            );
        } {
            this.update(\save,
                "settings not saved"
            );
        };

        ^nil;
    }

    loadSettings {
        Dialog.openPanel {|path|
            channel.loadSettings(Object.readArchive(path));
        } {
            this.warning(\load,
                "settings not loaded"
            );
        };

        ^nil;
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

    update {|key, string|
        switch (key)
            {\midiChanged}    { view.updateMidiMonitor(string); }
            {\settingsLoaded} { view.update; };
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
    }
}