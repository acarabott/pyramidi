b = Buffer.read(s, "/Volumes/Data/Users/arthurc/Library/Application Support/SuperCollider/Extensions/sounds/guitarNotes/guitar68.aif");

(
SynthDef(\bend, {|buf=0, out=0, ratio=1, lag=0.5, sel=0|
	var player = PlayBuf.ar(1, buf, BufRateScale.kr(buf), doneAction:2);
	var shift = PitchShift.ar(player, 0.2, Lag.kr(ratio, lag), 0, 0.0001);

	var signal = Select.ar(sel, [player, shift]);
	Out.ar(out,
		signal.dup
	);
}).add;
)

x = Synth(\bend, [\buf, b]);
x.set(\ratio, Scale.major.ratios[2], \sel, 1, \lag, 2);

s.makeWindow