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

PM_MIDI2OSCChannelController {
    var channel;
    var <view;
    var <>debugEnabled = false;
    var parent;

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

    checkNameField {
        if(channel.name.asSymbol != view.nameField.string.asSymbol) {
            view.nameField.stringColor_(Color.red);
        } {
           view.nameField.stringColor_(Color.black);
        };
    }

    checkIpField {
        if(channel.ip.asSymbol != view.ipField.string.asSymbol) {
            view.ipField.stringColor_(Color.red);
        } {
            view.ipField.stringColor_(Color.black);
        };
    }

    checkOscAddressField {
        if(channel.oscAddress.asSymbol != view.oscAddressField.string.asSymbol) {
            view.oscAddressField.stringColor_(Color.red);
        } {
            view.oscAddressField.stringColor_(Color.black);
        };
    }

    checkTextFields {
        this.checkNameField();
        this.checkIpField();
        this.checkOscAddressField();
    }

    getName {
        ^channel.name;
    }

    setName {|aName|
        channel.name = aName;
        if(view.notNil) {
            this.checkNameField();
        };
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

    getMidiSrcIDLocked {
        ^channel.midiSrcIDLocked;
    }

    setMidiSrcIDLocked {|aMidiSrcIDLocked|
        channel.midiSrcIDLocked = aMidiSrcIDLocked;
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

    setMidiChannelLocked {|aMidiChannelLocked|
        channel.midiChannelLocked = aMidiChannelLocked;
        ^this;
    }

    getMidiChannelLocked {
        ^channel.midiChannelLocked;
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

    setMidiMsgTypeLocked {|aMidiMsgTypeLocked|
        channel.midiMsgTypeLocked = aMidiMsgTypeLocked;
        ^this;
    }

    getMidiMsgTypeLocked {
        ^channel.midiMsgTypeLocked;
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
        if(view.notNil) {
            this.checkIpField();
        };
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
        if(view.notNil) {
            this.checkOscAddressField();
        };
        ^this;
    }

    getMapInputMin {
        ^channel.mapInputMin;
    }

    setMapInputMin {|aMapInputMin|
        channel.mapInputMin = aMapInputMin;
        ^this;
    }

    getMapInputMax {
        ^channel.mapInputMax;
    }

    setMapInputMax {|aMapInputMax|
        channel.mapInputMax = aMapInputMax;
        ^this;
    }

    getMapOutputMin {
        ^channel.mapOutputMin;
    }

    setMapOutputMin {|aMapOutputMin|
        channel.mapOutputMin = aMapOutputMin;
        ^this;
    }

    getMapOutputMax {
        ^channel.mapOutputMax;
    }

    setMapOutputMax {|aMapOutputMax|
        channel.mapOutputMax = aMapOutputMax;
        ^this;
    }

    getOutputType {
        ^channel.outputType;
    }

    setOutputType {|class|
        channel.outputType = class;
        ^this
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

    allNoteOff {
        channel.allNoteOff();
        ^nil;
    }

    getSettings {
        ^channel.storedSettings;
    }

    loadSettings {|aSettings|
        channel.loadSettings(aSettings);
        ^this;
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

    saveSettingsToFile {
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

    loadSettingsFromFile {
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

    freeChannel {
        channel.free;
    }
}