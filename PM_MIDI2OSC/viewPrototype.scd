~refreshDevices.();
~channel.enabled
~channel.free;

(

~channel = PM_MIDI2OSCChannel("rhodes");
~controller = PM_MIDI2OSCChannelController(~channel);
~view = ();
~controller.view = ~view;
~refreshDevices = {
	~device.items = ~controller.midiSrcLabels;
};
~vw = 150;
if (v.notNil) {v.close};
v = View(bounds:Rect(600, 800, ~vw, 530)).front;
v.onClose = {
	"closed".postln;
	~controller.free;
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
		~controller.name = field.string;
	});
v.decorator.nextLine;
~enable = Button(v, ~full@20)
	.states_([
		["off", Color.black, Color.red],
		["on", Color.black, Color.green]
	])
	.action_({|butt|
		~controller.enabled = (butt.value == 1);
	});
~enable.value = if(~controller.enabled, 1, 0);

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("MIDI Device")
	.align_(\center);

v.decorator.nextLine;

~device = PopUpMenu(v, ~full@20)
	.items_(~controller.midiSrcLabels)
	.action_({|menu|
		~controller.midiSrc = menu.value;
	});

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("MIDI Channel")
	.align_(\center);

v.decorator.nextLine;

~midiChannel = PopUpMenu(v, ~full@20)
	.items_(~controller.midiChannels.collect {|x|
		if(x.isNil) { "all" } { (x + 1).asString };
	})
	.action_({|menu|
		~controller.midiChannel = menu.value;
	});

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("MIDI Message")
	.align_(\center);

~midiMsgType = PopUpMenu(v, ~full@20)
	.value_(~controller.midiMsgTypes.indexOf(~controller.midiMsgType))
	.items_(~controller.midiMsgTypes)
	.action_({|menu|
		var sym = menu.item.asSymbol;
		~controller.midiMsgType = sym;
		~testVal2.visible = ~controller.midiNonNumTypes.includes(sym).not;
	});
// ~midiMsgType.valueAction = 3;

~midiMsgType.value = 2

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
			~controller.monitorMidi = true;
		} {
			~controller.monitorMidi = false;
		};
	});


~midiMonitor = StaticText(v, ((~vw - (~m * 4)) - 20)@20)
	.align_(\left)
	.background_(Color.white);

~view.updateMidiMonitor = {|aString|
	~midiMonitor.string = "  " ++ aString;
};

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("Destination IP")
	.align_(\center);

v.decorator.nextLine;

~ip = TextField(v, ~vw - (~m * 2)@20)
	.string_(~controller.ip)
	.align_(\center)
	.action_({|field|
		~controller.ip = field.string;
	});

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("Destination Port")
	.align_(\center);

v.decorator.nextLine;

~port = NumberBox(v, ~full@20)
	.value_(~controller.port ?? {0})
	.clipLo_(0)
	.clipHi_(65535)
	.decimals_(0)
	.align_(\center)
	.action_({ |box|
		~controller.port = box.value.asInteger;
	});

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("OSC Address")
	.align_(\center);

v.decorator.nextLine;


~oscAddress = TextField(v, ~full@20)
	.string_(~controller.oscAddress)
	.align_(\center)
	.action_({|field|
		~controller.oscAddress = field.string;
	});

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("Latency")
	.align_(\center);

v.decorator.nextLine;

~latency = NumberBox(v, ~full@20)
	.value_(~controller.latency)
	.clipLo_(0)
	.clipHi_(60)
	.decimals_(3)
	.align_(\center)
	.action_({|box|
		~controller.latency = box.value;
	});

v.decorator.nextLine;

StaticText(v, ~full@20)
	.string_("Send test message")
	.align_(\center);

v.decorator.nextLine;

~testVal1 = NumberBox(v, (~full * 0.5)-(~m * 0.5)@20)
	.value_(~controller.testVal1)
	.align_(\center)
	.decimals_(0)
	.clipLo_(0)
	.clipHi_(127)
	.action_({|box|
		~controller.testVal1 = box.value.asInteger;
	});

~testVal2 = NumberBox(v, (~full * 0.5)-(~m * 0.5)@20)
	.value_(~controller.testVal2)
	.align_(\center)
	.decimals_(0)
	.clipLo_(0)
	.clipHi_(127)
	.action_({|box|
		~controller.testVal2 = box.value.asInteger;
	});

v.decorator.nextLine;
~test = Button(v, ~full@20)
	.states_([
		["Test", Color.black, Color.white]
	])
	.action_({|butt|
		~controller.sendTestSignal;
	});
);





(
if(~window.notNil) {~window.close};
~window = Window("Pyramidi", Rect(100, 300, 1260, 700), scroll:true).front;
~window.addFlowLayout;

~window.onClose = {
	~channels.do {|channel, i|
		channel.free;
	};
};
~channels = 	List[];
~views = 		List[];
~controllers = 	List[];

~addButton = Button(~window, 70@70)
	.font_(Font.default.size = 40)
	.states_([
		["+", Color.white, Color.black],
	])
	.action_({|butt|
		~channels.add(PM_MIDI2OSCChannel("name" ++ ~channels.size.asString));
		~views.add(PM_MIDI2OSCChannelView(~window));
		~controllers.add(PM_MIDI2OSCChannelController(~channels.last, ~views.last));
	});


~title = StaticText(~window, 1100@70)
	.align_(\center)
	.string_("Pyramidi MIDI to OSC")
	.font_(Font.default.size = 40);

~window.view.decorator.nextLine;

);

