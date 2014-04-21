PM_MIDI2OSC {
	var channels;
	var controllers;
	var views;
	var window;

	*new {
		^super.new.midi2oscInit;
	}

	midi2oscInit {
		channels = 		List[];
		controllers = 	List[];
		views =			List[];

		this.createWindow;
		this.createAddButton;
		this.createTitle;
	}

	createWindow {
		var width = 	1260;
		var height = 	700;
		var x = 		(Window.screenBounds.width - width) * 0.5;
		var y =			(Window.screenBounds.height - height) * 0.5;

		window = Window(scroll:true)
			.name_("Pyramidi MIDI to OSC")
			.bounds_(Rect(x, y, width, height))
			.onClose_({
				channels.do {|channel|
					channel.free;
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
				this.addChannel("channel" ++ channels.size);
			});

		^this;
	}

	createTitle {
		StaticText(window, 1100@70)
			.align_(\center)
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
	}
}