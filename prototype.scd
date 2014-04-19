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

~channels = IdentityDictionary();

(
~addDevice = {|name, overwrite=false|
	var that;
	block {|break|
		if(~channels.includesKey(name.asSymbol) && overwrite.not) {
			Post << "Channel already exists with this name," <<
			"pass true as second argument to overwrite" << Char.nl;
			break.();
		} {
			that = IdentityDictionary();
			that['name'] = name;

			~channels[name.asSymbol] = that;
		};

		that;
	};

	that;
};
)

i = ~addDevice.("drum", true);

