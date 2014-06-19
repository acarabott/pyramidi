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
    var <channels;
    var <controllers;
    var <views;
    var <window;
    var menuHeight;
    var autoLoadPath;
    var <>clipboard;

    *new {
        ^super.new.midi2oscInit;
    }

    midi2oscInit {
        channels =      List[];
        controllers =   List[];
        views =         List[];
        autoLoadPath =  Platform.resourceDir +/+ "autoload";
        menuHeight =    70;

        this.createWindow;
        this.createAddButton;
        this.createSaveLoadButtons;
        this.createTitle;

        if(File.exists(autoLoadPath)) {
            this.readSettings(Object.readArchive(autoLoadPath));
        };

        ShutDown.add({
            this.getAllSettings.writeArchive(autoLoadPath);
        });
    }

    createWindow {
        var width =     1260;
        var height =    960;
        var x =         (Window.screenBounds.width - width) * 0.5;
        var y =         (Window.screenBounds.height - height) * 0.5;
        var m =         4;

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
        window.view.decorator.margin.x = m;
        window.view.decorator.margin.y = m;

        ^this;
    }

    relayout {
        var mx = window.view.decorator.margin.x;
        var my = window.view.decorator.margin.y;

        window.view.decorator.left = mx;
        window.view.decorator.top = menuHeight + (my * 2);
        views.do({|widget|
            window.view.decorator.place(widget.view);
        });
    }

    createAddButton {
        Button(window, menuHeight@menuHeight)
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
        Button(window, 140@menuHeight)
            .font_(Font.default.size = 30)
            .states_([
                ["Save All", Color.black, Color.white],
            ])
            .action_({|butt|
                this.saveAll;
            });
        Button(window, 140@menuHeight)
            .font_(Font.default.size = 30)
            .states_([
                ["Load All", Color.white, Color.black],
            ])
            .action_({|butt|
                this.loadAll;
            });
    }

    createTitle {
        StaticText(window, 500@menuHeight)
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

    removeChannel {|channel, view, controller|
        channels.remove(channel);
        views.remove(view);
        controllers.remove(controller);

        this.relayout();
    }

    getAllSettings {
        ^controllers.collect {|controller|
            controller.getSettings;
        };
    }

    saveAll {
        var settings = this.getAllSettings();

        Dialog.savePanel {|path|
            settings.writeArchive(path);
            settings.writeArchive(autoLoadPath);
        };

        ^nil;
    }

    loadAll {
        Dialog.openPanel {|path|
            this.readSettings(Object.readArchive(path));
        };
    }

    readSettings {|object|
        object.do {|settings|
            settings.keysValuesDo {|k, v|
                Post << k << ": " << v << Char.nl;
            };
            this.addChannel(settings['name']);
            controllers.last.loadSettings(settings);
        }
    }
}