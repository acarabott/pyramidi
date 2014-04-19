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
			Post << "Channel already exists with this name,"
				 << "pass true as second argument to overwrite" << Char.nl;
			break.();
		} {
			that = IdentityDictionary();
			that['name'] = name;
			that['addNetAddr'] = {|ip, port|
				block {|break|
					if(ip.isString.not) {
						Post << "ip address is not a string" << Char.nl;
						break.();
					};
					if(port.isInteger.not) {
						Post << "port is not an integer" << Char.nl;
						break.();
					};

					that['netAddr'] = NetAddr(ip, port);

					nil;
				}
			};
			that['setName'] = {|name|
				block {|break|
					var symName;
					if(name.isString.not) {
						Post << "string is not a name" << Char.nl;
						break.();
					};
					if(name.isEmpty) {
						Post << "name is empty" << Char.nl;
						break.();
					};
					if(name.asSymbol == that['name'].asSymbol) {
						break.();
					};
					symName = name.asSymbol;
					if(~channels.includesKey(symName)) {
						Post << "channel with this name already exists"
							 << Char.nl;
						break.();
					};
					~channels.removeAt(that['name'].asSymbol);
					that['name'] = name;
					~channels[symName] = that;

					nil;
				};
			};

			~channels[name.asSymbol] = that;
		};

		that;
	};

	that;
};
)

i = ~addDevice.("drum", true);
i['addNetAddr'].("127.0.0.1", 6666);
i['setName'].("drum");

~channels.keys

~channels.class.dumpAllMethods;