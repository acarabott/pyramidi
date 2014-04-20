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
v = View(bounds:Rect(600, 640, ~vw, 400)).front;
v.onClose = {
	"closed".postln;
	~nameRout.stop;
};
v.addFlowLayout;
~m = v.decorator.margin.x;

~name = TextField(v, ~vw-(~m * 2)@20)
	.string_(~channel.name)
	.align_(\center)
	.action_({|field|
		field.string.postln;
		~controller.setName(field.string);
	});
~nameRout = {
	inf.do {
		if(~name.string.asSymbol == ~controller.getName.asSymbol) {
			~name.background = Color.white;
		} {
			~name.background = Color.yellow;
		};
		0.2.wait;
	}
}.fork(AppClock);

v.decorator.nextLine;
~ed = 20;
v.decorator.left = (~vw * 0.5) - (~ed * 0.5);
~enable = Button(v, ~ed@~ed)
	.states_([
		["x", Color.black, Color.red],
		["o", Color.black, Color.green]
	])
	.action_({|butt|
		if(butt.value == 0) {
			~controller.enable;
		} {
			~controller.disable;
		};
	});

v.decorator.nextLine;

~dw = ~vw - (~m * 2);
~device = PopUpMenu(v, ~dw@20)
	.items_(~controller.getMidiSrcLabels)
	.action_({|menu|
		~controller.setMidiSrc(menu.value);
	});

~midiChannel = PopUpMenu(v, ~dw@20)
	.items_(~controller.getMidiChannels.collect {|x|
		if(x.isNil) { "all" } { (x + 1).asString };
	})
	.action_({|menu, i|
		~controller.setMidiChannel(menu.value);
	});
);

~controller.getName
~channel.name
~name.string == ~controller.getName