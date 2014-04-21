PM_MIDI2OSCChannelView {
	var view;
	var viewWidth;
	var margin;
	var fullWidth;

	var <controller;

	var nameField;
	var enableButton;
	var deviceMenu;
	var midiChannelMenu;
	var midiMsgTypeMenu;
	var midiMonitorText;
	var midiEnableMonitorButton;
	var ipField;
	var portBox;
	var oscAddressField;
	var latencyBox;
	var testVal1Box;
	var testVal2Box;
	var testButton;
	var copyButton;
	var pasteButton;
	var saveButton;
	var loadButton;

	*new {|aParentView|
		^super.new.midi2oscChannelViewInit(aParentView);
	}

	midi2oscChannelViewInit {|aParentView|
		viewWidth = 		150;
		margin =			4;
		fullWidth = 		viewWidth - (margin * 2);

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
		this.createLatencyBox;
		this.createTestControls;
		this.createClipboardButtons;
		this.createSaveLoadButtons;
	}

	createView {|aParentView|
		if([View.implClass, Window.implClass].includes(aParentView.class).not &&
			aParentView.notNil) {

			controller.error(\view,
				"parent view should be a Window, View or nil"
			);

			// Default to nil, use own window
			aParentView = nil;
		};

		view = View(aParentView, viewWidth@660);
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
			});

		view.decorator.nextLine;
		^nil;
	}

	createEnableButton {
		enableButton = Button(view, fullWidth@20)
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
		StaticText(view, fullWidth@20)
			.string_("MIDI Device")
			.align_(\center);

		view.decorator.nextLine;

		deviceMenu = PopUpMenu(view, fullWidth@20)
			.action_({|menu|
				if(controller.notNil) {
					controller.setMidiSrc(menu.value);
				};
			});

		view.decorator.nextLine;
		^nil
	}

	createMidiChannelMenu {
		StaticText(view, fullWidth@20)
			.string_("MIDI Channel")
			.align_(\center);

		view.decorator.nextLine;

		midiChannelMenu = PopUpMenu(view, fullWidth@20)
			.action_({|menu|
				if(controller.notNil) {
					controller.setMidiChannelFromIndex(menu.value);
				};
			});

		view.decorator.nextLine;
		^nil
	}

	createMidiMessageMenu {
		StaticText(view, fullWidth@20)
			.string_("MIDI Message")
			.align_(\center);

		midiMsgTypeMenu = PopUpMenu(view, fullWidth@20)
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
		StaticText(view, fullWidth@20)
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
		{midiMonitorText.string_(aString.asString)}.fork(AppClock);

		^nil;
	}

	createIpField {
		StaticText(view, fullWidth@20)
			.string_("Destination IP")
			.align_(\center);

		view.decorator.nextLine;

		ipField = TextField(view, viewWidth - (margin * 2)@20)
			.align_(\center)
			.action_({|field|
				if(controller.notNil) {
					controller.setIp(field.string);
				};
			});

		view.decorator.nextLine;
		^nil
	}

	createPortBox {
		StaticText(view, fullWidth@20)
			.string_("Destination Port")
			.align_(\center);

		view.decorator.nextLine;

		portBox = NumberBox(view, fullWidth@20)
			.clipLo_(0)
			.clipHi_(65535)
			.decimals_(0)
			.align_(\center)
			.action_({ |box|
				if(controller.notNil) {
					controller.setPort(box.value.asInteger);
				};
			});

		view.decorator.nextLine;
		^nil
	}

	createOscAddressField {
		StaticText(view, fullWidth@20)
			.string_("OSC Address")
			.align_(\center);

		view.decorator.nextLine;

		oscAddressField = TextField(view, fullWidth@20)
			.align_(\center)
			.action_({|field|
				if(controller.notNil) {
					controller.setOscAddress(field.string);
				};
			});

		view.decorator.nextLine;
		^nil
	}

	createLatencyBox {
		StaticText(view, fullWidth@20)
			.string_("Latency")
			.align_(\center);

		view.decorator.nextLine;

		latencyBox = NumberBox(view, fullWidth@20)
			.clipLo_(0)
			.clipHi_(60)
			.decimals_(3)
			.align_(\center)
			.action_({|box|
				if(controller.notNil) {
					controller.setLatency(box.value);
				};
			});

		view.decorator.nextLine;
		^nil
	}

	createTestControls {
		StaticText(view, fullWidth@20)
			.string_("Send test message")
			.align_(\center);

		view.decorator.nextLine;

		testVal1Box = NumberBox(view, (fullWidth * 0.5) - (margin * 0.5)@20)
			.align_(\center)
			.decimals_(0)
			.clipLo_(0)
			.clipHi_(127)
			.action_({|box|
				if(controller.notNil) {
					controller.setTestVal1(box.value.asInteger);
				};
			});

		testVal2Box = NumberBox(view, (fullWidth * 0.5) - (margin * 0.5)@20)
			.align_(\center)
			.decimals_(0)
			.clipLo_(0)
			.clipHi_(127)
			.action_({|box|
				if(controller.notNil) {
					controller.setTestVal2(box.value.asInteger);
				};
			});

		view.decorator.nextLine;
		testButton = Button(view, fullWidth@20)
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

	createClipboardButtons {
		view.decorator.top = view.decorator.top + 20;
		copyButton = Button(view, fullWidth@20)
			.states_([
				["Copy", Color.white, Color.black]
			])
			.action_({|butt|
				if(controller.notNil) {
					controller.copySettings;
				};
			});

		view.decorator.nextLine;

		pasteButton = Button(view, fullWidth@20)
			.states_([
				["Paste", Color.black, Color.yellow]
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
		view.decorator.top = view.decorator.top + 20;

		saveButton = Button(view, fullWidth@20)
			.states_([
				["Save", Color.black, Color.white]
			])
			.action_({|butt|
				controller.saveSettings;
			});

		view.decorator.nextLine;

		loadButton = Button(view, fullWidth@20)
			.states_([
				["Load", Color.white, Color.black]
			])
			.action_({|butt|
				controller.loadSettings;
			});

		view.decorator.nextLine;
		^nil;
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
			nameField.string =				controller.getName;
			enableButton.value = 			if(controller.getEnabled, 1, 0);
			deviceMenu.items = 				controller.getMidiSrcLabels;
			deviceMenu.value = 				controller.getMidiSrcIndex;
			midiChannelMenu.items = 		controller.getMidiChannelLabels;
			midiChannelMenu.value = 		controller.getMidiChannelIndex;
			midiMsgTypeMenu.items = 		controller.getMidiMsgTypes;
			midiMsgTypeMenu.value = 		controller.getMidiMsgTypeIndex;
			midiEnableMonitorButton.value = if(controller.getMidiNotifying,1,0);
			ipField.string = 				controller.getIp;
			portBox.value = 				controller.getPort;
			oscAddressField.string = 		controller.getOscAddress;
			latencyBox.value = 				controller.getLatency;
			testVal1Box.value = 			controller.getTestVal1;
			testVal2Box.value = 			controller.getTestVal2;
		};

	}
}