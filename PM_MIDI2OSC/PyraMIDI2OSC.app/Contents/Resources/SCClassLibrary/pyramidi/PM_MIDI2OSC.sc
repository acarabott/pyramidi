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

PM_MIDI2OSC {
    var channels;
    var controllers;
    var views;
    var window;
    var autoLoadPath;
    var <>clipboard;

    *new {
        ^super.new.midi2oscInit;
    }

    midi2oscInit {
        channels =      List[];
        controllers =   List[];
        views =         List[];
        autoLoadPath =  Platform.resourceDir +/+ "autoLoad";

        this.createWindow;
        this.createAddButton;
        this.createSaveLoadButtons;
        this.createTitle;

        if(File.exists(autoLoadPath)) {
            var path = Object.readArchive(autoLoadPath);
            this.readSettings(Object.readArchive(path));
        };
    }

    createWindow {
        var width =     1260;
        var height =    900;
        var x =         (Window.screenBounds.width - width) * 0.5;
        var y =         (Window.screenBounds.height - height) * 0.5;

        window = Window(scroll:true)
            .name_("Pyramidi MIDI to OSC")
            .bounds_(Rect(x, y, width, height))
            .onClose_({
                controllers.do {|controller|
                    controller.freeChannel;
                };
            })
            .userCanClose_(false)
            .front;

        window.addFlowLayout;

        ^this;
    }

    createAddButton {
        Button(window, 70@70)
            .font_(Font.default.size = 40)
            .states_([
                ["+", Color.white, Color.black]
            ])
            .action_({|butt|
                this.addChannel("channel" ++ (channels.size + 1));
            });

        ^this;
    }

    createSaveLoadButtons {
        Button(window, 140@70)
            .font_(Font.default.size = 30)
            .states_([
                ["Save All", Color.black, Color.white],
            ])
            .action_({|butt|
                this.saveAll;
            });
        Button(window, 140@70)
            .font_(Font.default.size = 30)
            .states_([
                ["Load All", Color.white, Color.black],
            ])
            .action_({|butt|
                this.loadAll;
            });
    }

    createTitle {
        StaticText(window, 500@70)
            .align_(\right)
            .string_("Pyramidi MIDI to OSC")
            .font_(Font.default.size = 40);

        window.view.decorator.nextLine;
    }

    addChannel {|name|
        channels.add(PM_MIDI2OSCChannel(name));
        views.add(PM_MIDI2OSCChannelView(window));
        controllers.add(
            PM_MIDI2OSCChannelController(channels.last, views.last)
        );

        controllers.last.parent = this;
    }

    saveAll {
        var settings = controllers.collect {|controller|
            controller.getSettings
        };

        Dialog.savePanel {|path|
            settings.writeArchive(path);
            path.writeArchive(autoLoadPath);
        };

        ^nil;
    }

    loadAll {
        Dialog.openPanel {|path|
            this.readSettings(Object.readArchive(path));
        };
    }

    readSettings {|object|
        object.do {|setting|
            setting.postln;
            this.addChannel(setting['name']);
            controllers.last.loadSettings(setting);
        }
    }
}