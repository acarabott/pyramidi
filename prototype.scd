MIDIClient.init;
MIDIIn.connectAll;
thisProcess.openUDPPort(6666);
n = NetAddr("127.0.0.1", 6666)

MIDIdef.noteOn(\noteOn, {|val, num, chan, src|
	n.sendMsg("/noteOn", num);
});

OSCdef(\hello, {|msg, time, addr, recvPort|
	Post << "playing note: " << msg[1] << Char.nl;
}, '/noteOn');