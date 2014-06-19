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

PM_MIDI2OSCChannelView {
    var <view;
    var viewWidth;
    var margin;
    var fullWidth;

    var <controller;

    var standardSize;
    var <nameField;
    var enableButton;
    var deviceMenu;
    var deviceMenuToggle;
    var midiChannelMenu;
    var midiChannelMenuToggle;
    var midiMsgTypeMenu;
    var midiMsgTypeMenuToggle;
    var midiMonitorText;
    var midiEnableMonitorButton;
    var <ipField;
    var portBox;
    var <oscAddressField;
    var mapInputMinBox;
    var mapInputMaxBox;
    var mapOutputMinBox;
    var mapOutputMaxBox;
    var outputTypeButton;
    var latencyBox;
    var testVal1Box;
    var testVal2Box;

    *new {|aParentView|
        ^super.new.midi2oscChannelViewInit(aParentView);
    }

    midi2oscChannelViewInit {|aParentView|
        viewWidth =         170;
        margin =            4;
        fullWidth =         viewWidth - (margin * 2);
        standardSize =      fullWidth@20;

        this.createView(aParentView);
        this.createNameField;
        this.createEnableButton;
        this.createMidiDeviceMenu;
        this.createMidiChannelMenu;
        this.createMidiMessageMenu;
        this.createMidiMonitor;
        this.createIpField;
        this.createPortBox;
        this.createOscAddressField;
        this.createMappingControls;
        this.createLatencyBox;
        this.createTestControls;
        this.createAllNoteOffButton;
        this.createClipboardButtons;
        this.createSaveLoadButtons;
        this.createRemoveButton;
    }

    createView {|aParentView|
        view = View(aParentView, viewWidth@890);
        view.addFlowLayout;
        view.decorator.margin.x = margin;
        view.decorator.margin.y = margin;

        view.onClose = {
            // controller.free;
        };

        if(aParentView.isNil) {
            view.front;
        };

        ^nil;
    }

    createNameField {
        nameField = TextField(view, fullWidth@40)
            .align_(\center)
            .font_(Font.default.size = 25)
            .action_({|field|
                if(controller.notNil) {
                    controller.setName(field.string);
                };
            })
            .keyUpAction_({|field|
                controller.checkNameField();
            });

        view.decorator.nextLine;
        ^nil;
    }

    createEnableButton {
        enableButton = Button(view, standardSize)
            .states_([
                ["off", Color.black, Color.red],
                ["on", Color.black, Color.green]
            ])
            .action_({|butt|
                if(controller.notNil) {
                    controller.setEnabled(butt.value == 1);
                };
            });

        view.decorator.nextLine;
        ^nil
    }

    createMidiDeviceMenu {
        view.decorator.left = view.decorator.left + 10;
        StaticText(view, (fullWidth * 0.55)@20)
            .string_("MIDI Device")
            .align_(\center);

        deviceMenuToggle = Button(view, (fullWidth * 0.35)@20)
            .states_([
                ["Lock", Color.black, Color.white],
                ["Unlock", Color.white, Color.gray]
            ])
            .action_({|butt|
                deviceMenu.enabled = butt.value == 0;
                controller.setMidiSrcIDLocked(deviceMenu.enabled.not);
            });

        view.decorator.nextLine;
        deviceMenu = PopUpMenu(view, standardSize)
            .action_({|menu|
                if(controller.notNil) {
                    controller.setMidiSrc(menu.value);
                };
            });
        view.decorator.nextLine;
        ^nil
    }

    createMidiChannelMenu {
        view.decorator.left = view.decorator.left + 10;
        StaticText(view, (fullWidth * 0.55)@20)
            .string_("MIDI Channel")
            .align_(\center);

        midiChannelMenuToggle = Button(view, (fullWidth * 0.35)@20)
            .states_([
                ["Lock", Color.black, Color.white],
                ["Unlock", Color.white, Color.gray]
            ])
            .action_({|butt|
                midiChannelMenu.enabled = butt.value == 0;
                controller.setMidiChannelLocked(midiChannelMenu.enabled.not);
            });
        view.decorator.nextLine;

        midiChannelMenu = PopUpMenu(view, standardSize)
            .action_({|menu|
                if(controller.notNil) {
                    controller.setMidiChannelFromIndex(menu.value);
                };
            });

        view.decorator.nextLine;
        ^nil
    }

    createMidiMessageMenu {
        view.decorator.left = view.decorator.left + 10;
        StaticText(view, (fullWidth * 0.55)@20)
            .string_("MIDI Message")
            .align_(\center);

        midiMsgTypeMenuToggle = Button(view, (fullWidth * 0.35)@20)
            .states_([
                ["Lock", Color.black, Color.white],
                ["Unlock", Color.white, Color.gray]
            ])
            .action_({|butt|
                midiMsgTypeMenu.enabled = butt.value == 0;
                controller.setMidiMsgTypeLocked(midiMsgTypeMenu.enabled.not);
            });
        view.decorator.nextLine;

        midiMsgTypeMenu = PopUpMenu(view, standardSize)
            .action_({|menu|
                if(controller.notNil) {
                    var sym = menu.item.asSymbol;

                    controller.setMidiMsgType(sym);
                    testVal2Box.visible = controller
                                            .getMidiNonNumTypes
                                            .includes(sym)
                                            .not;
                };
            });

        view.decorator.nextLine;
        ^nil
    }

    createMidiMonitor {
        StaticText(view, standardSize)
            .string_("MIDI Monitor")
            .align_(\center);

        view.decorator.nextLine;

        midiEnableMonitorButton = Button(view, 20@20)
            .states_([
                ["x", Color.white, Color.gray],
                ["o", Color.black, Color.green]
            ])
            .action_({|butt|
                if(controller.notNil) {
                    controller.setMidiNotifying(butt.value == 1);
                };
            });

        midiMonitorText = StaticText(view, ((viewWidth - (margin * 4)) - 20)@20)
            .align_(\left)
            .background_(Color.white);

        view.decorator.nextLine;
        ^nil
    }

    updateMidiMonitor {|aString|
        {
            midiMonitorText.string_(aString.asString);
            midiMonitorText.background_(Color.green);
            0.1.wait;
            midiMonitorText.background_(Color.clear);
        }.fork(AppClock);

        ^nil;
    }

    createIpField {
        StaticText(view, standardSize)
            .string_("Destination IP")
            .align_(\center);

        view.decorator.nextLine;

        ipField = TextField(view, viewWidth - (margin * 2)@20)
            .align_(\center)
            .action_({|field|
                if(controller.notNil) {
                    controller.setIp(field.string);
                };
            })
            .keyUpAction_({|field|
                controller.checkIpField();
            });

        view.decorator.nextLine;
        ^nil
    }

    createPortBox {
        StaticText(view, standardSize)
            .string_("Destination Port")
            .align_(\center);

        view.decorator.nextLine;

        portBox = NumberBox(view, standardSize)
            .clipLo_(0)
            .clipHi_(65535)
            .decimals_(0)
            .align_(\center)
            .scroll_(false)
            .action_({ |box|
                if(controller.notNil) {
                    controller.setPort(box.value.asInteger);
                };
            });

        view.decorator.nextLine;
        ^nil
    }

    createOscAddressField {
        StaticText(view, standardSize)
            .string_("OSC Address")
            .align_(\center);

        view.decorator.nextLine;

        oscAddressField = TextField(view, standardSize)
            .align_(\center)
            .action_({|field|
                if(controller.notNil) {
                    controller.setOscAddress(field.string);
                };
            })
            .keyUpAction_({|field|
                controller.checkOscAddressField();
            });

        view.decorator.nextLine;
        ^nil
    }

    createMappingControls {
        StaticText(view, standardSize)
            .string_("Value Mapping")
            .font_(Font.default.size_(15).boldVariant)
            .align_(\center);

        view.decorator.nextLine;

        StaticText(view, standardSize)
            .string_("Input")
            .align_(\center);

        view.decorator.nextLine;

        mapInputMinBox = NumberBox(view, (fullWidth * 0.5) - (margin * 0.5)@20)
            .align_(\center)
            .decimals_(0)
            .clipLo_(0)
            .clipHi_(127)
            .scroll_(false)
            .action_({|box|
                if(controller.notNil) {
                    controller.setMapInputMin(box.value.asInteger);
                };
            });

        mapInputMaxBox = NumberBox(view, (fullWidth * 0.5) - (margin * 0.5)@20)
            .align_(\center)
            .decimals_(0)
            .clipLo_(0)
            .clipHi_(127)
            .scroll_(false)
            .action_({|box|
                if(controller.notNil) {
                    controller.setMapInputMax(box.value.asInteger);
                };
            });

        StaticText(view, standardSize)
            .string_("Output")
            .align_(\center);

        view.decorator.nextLine;

        outputTypeButton = Button(view, standardSize)
            .states_([
                ["Integer", Color.black, Color.white],
                ["Float", Color.white, Color.black]
            ])
            .action_({|butt|
                if(controller.notNil) {
                    if(butt.value == 0) {
                        controller.setOutputType(Integer);
                    } {
                        controller.setOutputType(Float);
                    };
                };

                if(butt.value == 0) {
                    mapOutputMinBox.decimals = 0;
                    mapOutputMaxBox.decimals = 0;
                } {
                   mapOutputMinBox.decimals = 4;
                   mapOutputMaxBox.decimals = 4;
                }
            });


        mapOutputMinBox = NumberBox(view, (fullWidth * 0.5) - (margin * 0.5)@20)
            .align_(\center)
            .decimals_(0)
            .scroll_(false)
            .action_({|box|
                if(controller.notNil) {
                    controller.setMapOutputMin(box.value.asInteger);
                };
            });

        mapOutputMaxBox = NumberBox(view, (fullWidth * 0.5) - (margin * 0.5)@20)
            .align_(\center)
            .decimals_(0)
            .scroll_(false)
            .action_({|box|
                if(controller.notNil) {
                    controller.setMapOutputMax(box.value.asInteger);
                };
            });

        view.decorator.nextLine;

        ^nil;
    }

    createLatencyBox {
        StaticText(view, standardSize)
            .string_("Latency")
            .align_(\center);

        view.decorator.nextLine;

        latencyBox = NumberBox(view, standardSize)
            .clipLo_(0)
            .clipHi_(60)
            .decimals_(3)
            .align_(\center)
            .scroll_(false)
            .action_({|box|
                if(controller.notNil) {
                    controller.setLatency(box.value);
                };
            });

        view.decorator.nextLine;
        ^nil
    }

    createTestControls {
        StaticText(view, standardSize)
            .string_("Send test message")
            .align_(\center);

        view.decorator.nextLine;

        testVal1Box = NumberBox(view, (fullWidth * 0.5) - (margin * 0.5)@20)
            .align_(\center)
            .decimals_(0)
            .scroll_(false)
            .action_({|box|
                if(controller.notNil) {
                    controller.setTestVal1(box.value.asInteger);
                };
            });

        testVal2Box = NumberBox(view, (fullWidth * 0.5) - (margin * 0.5)@20)
            .align_(\center)
            .decimals_(0)
            .scroll_(false)
            .action_({|box|
                if(controller.notNil) {
                    controller.setTestVal2(box.value.asInteger);
                };
            });

        view.decorator.nextLine;

        Button(view, standardSize)
            .states_([
                ["Test", Color.black, Color.white]
            ])
            .action_({|butt|
                if(controller.notNil) {
                    controller.sendTestSignal;
                };
            });

        view.decorator.nextLine;
        ^nil;
    }

    createAllNoteOffButton {
        Button(view, standardSize)
            .states_([
                ["All Notes Off", Color.white, Color.red]
            ])
            .action_({|butt|
                if(controller.notNil) {
                    controller.allNoteOff();
                };
            });
        view.decorator.nextLine;
        ^nil;
    }

    createClipboardButtons {
        view.decorator.top = view.decorator.top + 10;

        Button(view, standardSize)
            .states_([
                ["Copy Channel", Color.white, Color.black]
            ])
            .action_({|butt|
                if(controller.notNil) {
                    controller.copySettings;
                };
            });

        view.decorator.nextLine;

        Button(view, standardSize)
            .states_([
                ["Paste Channel", Color.black, Color.yellow]
            ])
            .action_({|butt|
                if(controller.notNil) {
                    controller.pasteSettings;
                };
            });

        view.decorator.nextLine;
        ^nil;
    }

    createSaveLoadButtons {
        view.decorator.top = view.decorator.top + 10;

        Button(view, standardSize)
            .states_([
                ["Save Channel", Color.black, Color.white]
            ])
            .action_({|butt|
                controller.saveSettingsToFile;
            });

        view.decorator.nextLine;

        Button(view, standardSize)
            .states_([
                ["Load Channel", Color.white, Color.black]
            ])
            .action_({|butt|
                controller.loadSettingsFromFile;
            });

        view.decorator.nextLine;
        ^nil;
    }

    createRemoveButton {
        var remove, confirm, cancel;
        view.decorator.top = view.decorator.top + 10;

        remove = Button(view, standardSize)
            .states_([
                ["Remove Channel", Color.white, Color.red]
            ])
            .action_({|butt|
                remove.visible =    false;
                confirm.visible =   true;
                cancel.visible =    true;
            });

        cancel = Button(view, (fullWidth * 0.5) - margin@20)
            .states_([
                ["Cancel", Color.black, Color.white]
            ])
            .action_({|butt|
                remove.visible =    true;
                confirm.visible =   false;
                cancel.visible =    false;
            })
            .visible_(false);

        confirm = Button(view, (fullWidth * 0.5) - margin@20)
            .states_([
                ["Confirm", Color.black, Color.yellow]
            ])
            .action_({|butt|
                remove.visible =    true;
                confirm.visible =   false;
                cancel.visible =    false;

                controller.removeChannel;
            })
            .visible_(false);


        view.decorator.nextLine;
    }

    controller_ {|aController|
        if(aController.isKindOf(PM_MIDI2OSCChannelController).not) {
            Post << "controller not a PM_MIDI2OSCChannelController!" << Char.nl;
            ^this;
        };

        controller = aController;

        controller.debug("SET: view now has a controller");

        ^this;
    }

    update {
        if(controller.notNil) {
            nameField.string =              controller.getName;
            enableButton.value =            if(controller.getEnabled, 1, 0);
            deviceMenu.items =              controller.getMidiSrcLabels;
            deviceMenu.value =              controller.getMidiSrcIndex;
            deviceMenu.enabled =            controller.getMidiSrcIDLocked.not;
            deviceMenuToggle.value =        if(deviceMenu.enabled, 0, 1);
            midiChannelMenu.items =         controller.getMidiChannelLabels;
            midiChannelMenu.value =         controller.getMidiChannelIndex;
            midiChannelMenu.enabled =       controller.getMidiChannelLocked.not;
            midiChannelMenuToggle.value =   if(midiChannelMenu.enabled, 0, 1);
            midiMsgTypeMenu.items =         controller.getMidiMsgTypes;
            midiMsgTypeMenu.value =         controller.getMidiMsgTypeIndex;
            midiMsgTypeMenu.enabled =       controller.getMidiMsgTypeLocked.not;
            midiMsgTypeMenuToggle.value =   if(midiMsgTypeMenu.enabled, 0, 1);
            midiEnableMonitorButton.value = if(controller.getMidiNotifying,1,0);
            ipField.string =                controller.getIp;
            portBox.value =                 controller.getPort;
            oscAddressField.string =        controller.getOscAddress;
            latencyBox.value =              controller.getLatency;
            mapInputMinBox.value =          controller.getMapInputMin;
            mapInputMaxBox.value =          controller.getMapInputMax;
            mapOutputMinBox.value =         controller.getMapOutputMin;
            mapOutputMaxBox.value =         controller.getMapOutputMax;
            testVal1Box.value =             controller.getTestVal1;
            testVal2Box.value =             controller.getTestVal2;

            if(controller.getOutputType == Integer) {
                outputTypeButton.value = 0;
            } {
                if(controller.getOutputType == Float) {
                    outputTypeButton.value = 1;
                };
            };
        };

    }
}