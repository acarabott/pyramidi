PM_MIDI2OSC {
	var channels;
	var controllers;
	var views;
	var window;
	var <>clipboard;

	*new {
		^super.new.midi2oscInit;
	}

	midi2oscInit {
		channels = 		List[];
		controllers = 	List[];
		views =			List[];

		this.createWindow;
		this.createAddButton;
		this.createSaveLoadButtons;
		this.createTitle;
	}

	createWindow {
		var width = 	1260;
		var height = 	750;
		var x = 		(Window.screenBounds.width - width) * 0.5;
		var y =			(Window.screenBounds.height - height) * 0.5;

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
		};

		^nil;
	}

	loadAll {
		Dialog.openPanel {|path|
			Object.readArchive(path).do {|setting|
				setting.postln;
				this.addChannel(setting['name']);
				controllers.last.loadSettings(setting);
			}
		};
	}
}