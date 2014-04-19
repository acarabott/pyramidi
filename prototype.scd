MIDIClient.init;
MIDIIn.connectAll;

MIDIdef.noteOn(\noteOn, {|val, num, chan, src|
	num.postln;
});