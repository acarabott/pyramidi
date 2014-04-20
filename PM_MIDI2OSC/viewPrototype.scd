MIDIClient.init;

~refreshDevices.();

(

~channel = PM_MIDI2OSCChannel("rhodes");
~controller = PM_MIDI2OSCController(~channel);

~refreshDevices = {
	~device.items = ~controller.getMidiSrcLabels;
};
~vw = 150;
if (v.notNil) {v.close};
v = View(bounds:Rect(600, 800, ~vw, 530)).front;
v.onClose = {
	"closed".postln;
	~nameRout.stop;
};
v.addFlowLayout;
~m = v.decorator.margin.x;
~full = ~vw-(~m * 2);

~name = TextField(v, ~full@40)
	.string_(~channel.name)
	.align_(\center)
	.font_(Font.default.size = 25)
	.action_({|field|
		~controller.setName(field.string);
	});
v.decorator.nextLine;
~enable = Button(v, ~full@20)
	.states_([
		["off", Color.black, Color.red],
		["on", Color.black, Color.green]
	])
	.action_({|butt|
		if(butt.value == 1) {
			~controller.enable;
		} {
			~controller.disable;
		};
	});

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("MIDI Device")
	.align_(\center);

v.decorator.nextLine;

~device = PopUpMenu(v, ~full@20)
	.items_(~controller.getMidiSrcLabels)
	.action_({|menu|
		~controller.setMidiSrc(menu.value);
	});

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("MIDI Channel")
	.align_(\center);

v.decorator.nextLine;

~midiChannel = PopUpMenu(v, ~full@20)
	.items_(~controller.getMidiChannels.collect {|x|
		if(x.isNil) { "all" } { (x + 1).asString };
	})
	.action_({|menu|
		~controller.setMidiChannel(menu.value);
	});

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("MIDI Message")
	.align_(\center);

~midiMsgType = PopUpMenu(v, ~full@20)
	.items_(~controller.getMidiMsgTypes)
	.action_({|menu|
		var sym = menu.item.asSymbol;
		~controller.setMidiMsgType(sym);
		~testVal2.visible = ~controller.getMidiNonNumTypes.includes(sym).not;
	});
~midiMsgType.value_(~midiMsgType.items.indexOf(~controller.getMidiMsgType));
// ~midiMsgType.valueAction = 3;

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("MIDI Monitor")
	.align_(\center);

v.decorator.nextLine;

~midiEnableMonitor = Button(v, 20@20)
	.states_([
		["x", Color.white, Color.gray],
		["o", Color.black, Color.green]
	])
	.action_({|butt|
		if(butt.value == 1) {
			// ~controller.monitorMidi = true;
		} {
			// ~controller.monitorMidi = false;
		};
	});

~midiMonitor = StaticText(v, ((~vw - (~m * 4)) - 20)@20)
	// .string_(~controller.getMidiValue)
	.align_(\left)
	.background_(Color.white);

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("Destination IP")
	.align_(\center);

v.decorator.nextLine;

~ip = TextField(v, ~vw - (~m * 2)@20)
	.string_(~controller.getIp)
	.align_(\center)
	.action_({|field|
		~controller.setIp(field.string);
	});

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("Destination Port")
	.align_(\center);

v.decorator.nextLine;

~port = NumberBox(v, ~full@20)
	.value_(~controller.getPort ?? {0})
	.clipLo_(0)
	.clipHi_(65535)
	.decimals_(0)
	.align_(\center)
	.action_({ |box|
		~controller.setPort(box.value.asInteger);
	});

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("OSC Address")
	.align_(\center);

v.decorator.nextLine;


~oscAddress = TextField(v, ~full@20)
	// .string_(~controller.getOSCAddress)
	.align_(\center)
	.action_({|field|
		// ~controller.setOSCAddress(field.string);
	});

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("Latency")
	.align_(\center);

v.decorator.nextLine;

~latency = NumberBox(v, ~full@20)
	// .value_(~controller.getLatency)
	.clipLo_(0)
	.clipHi_(60)
	.decimals_(4)
	.align_(\center)
	.action_({ |box|
		// ~controller.setLatency(box.value);
	});

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("Send test message")
	.align_(\center);

v.decorator.nextLine;

~testVal1 = NumberBox(v, (~full * 0.5)-(~m * 0.5)@20)
	.align_(\center)
	.action_({|box|
		// ~controller.setTestVal(0, box.value)
	});

~testVal2 = NumberBox(v, (~full * 0.5)-(~m * 0.5)@20)
	.align_(\center)
	.action_({|box|
		// ~controller.setTestVal(1, box.value)
	});

v.decorator.nextLine;
~test = Button(v, ~full@20)
	.states_([
		["Test", Color.black, Color.white]
	])
	.action_({|butt|
		butt.value.postln;
	});
);

