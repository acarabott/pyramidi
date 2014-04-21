MIDIClient.init;
MIDIIn.connectAll;
thisProcess.openUDPPort(6666);
n = NetAddr("127.0.0.1", 6666)

OSCdef(\hello, {|msg, time, addr, recvPort|
	var len = 15;
	Post << "----------------------------------" << Char.nl
		 << "noteOn " << Char.nl
		 << "-------" << Char.nl
		 << "from:".padRight(len) << addr.ip << ":" << addr.port << Char.nl
		 << "note:".padRight(len) << msg[1] << Char.nl
		 << "velocity:".padRight(len) << msg[2] << Char.nl;
}, '/noteOn');

~channel = IdentityDictionary[];
~channel['midiChans'] = [nil] ++ (0..15);
~channel['midiMsgTypes'] = MIDIFunc.defaultDispatchers.keys.asArray;
~channel['midiSrcIDs'] = [nil] ++ MIDIClient.sources.collect (_.uid);
~channel['midiNonNumTypes'] = [\touch, \program, \bend];

// MIDIClient.sources.collectAs({|item|
// 	((item.device + item.name).asSymbol -> item.uid);
// }, IdentityDictionary);

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
			// Constructor
			that['name'] = name;
			that['enabled'] = true;
			that['netAddr'];
			that['midiFunc'];
			that['midiChan'] = -1; // nil responds to all
			that['midiMsgType'];
			that['midiSrcID'] = -1; // nil responds to all
			that['latency'] = 0;

			// Public
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
				};

				that;
			};

			that['enable'] = {
				that['enabled'] = true;

				that;
			};

			that['disable'] = {
				that['enabled'] = false;

				that;
			};

			that['setLatency'] = {|latency|
				block {|break|
					if(latency.isNumber.not) {
						Post << "ERROR:" << Char.nl
							 << "Latency must be a number" << Char.nl;
						break.();
					};
					if(latency < 0) {
						Post << "ERROR:" << Char.nl
							 << "Latency must be greater than 0" << Char.nl;
						break.();
					};

					that['latency'] = latency;
				};
			};

			that['setMidiParam'] = {|paramKey, lookupKey, value|
				block {|break|
					if(~channel[lookupKey].includes(value).not()) {
						Post << "ERROR: " << Char.nl
							 << paramKey.asString << " is invalid. " << Char.nl
							 << "should be one of " << ~channel[lookupKey]
							 << Char.nl;
						break.();
					};

					that[paramKey] = value;
				};

				that;
			};

			that['setMidiChan'] = {|midiChan|
				that['setMidiParam'].('midiChan', 'midiChans', midiChan);
			};

			that['setMidiMsgType'] = { |midiMsgType|
				that['setMidiParam'].('midiMsgType', 'midiMsgTypes', midiMsgType);
			};

			that['setMidiSrcID'] = { |midiSrcID|
				that['setMidiParam'].('midiSrcID', 'midiSrcIDs', midiSrcID);
			};

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
				};

				that;
			};

 			// private
			that['addMidiFunc'] = {
				block {|break|
					var func, oscAddr;

					if(that['netAddr'].isNil) {
						Post << "ip or port not set" << Char.nl;
						break.();
					};
					if(~channel['midiChans'].includes(that['midiChan']).not) {
						Post << "MIDI channel not set" << Char.nl;
						break.();
					};
					if(~channel['midiMsgTypes'].includes(that['midiMsgType']).not) {
						Post << "MIDI message type not set" << Char.nl;
						break.();
					};
					if(~channel['midiSrcIDs'].includes(that['midiSrcID']).not) {
						Post << "MIDI source ID not set" << Char.nl;
						break.();
					};

					that['midiFunc'].free;

					oscAddr = "/" ++ that['midiMsgType'].asString;

					if(~channel['midiNonNumTypes'].includes(that['midiMsgType'])) {
						func = {|val, chan, src|
							if(that['enabled']) {
								// Post << "received:\t" << that['midiMsgType'].asString
								// 	 << Char.nl
								// 	 << "on channel:\t" << chan << Char.nl
								// 	 << "from ID:\t" << src << Char.nl
								// 	 << Char.nl;
								SystemClock.sched(that['latency'], {
									that['netAddr'].sendMsg(oscAddr, val);
								});
							};
						};
					} {
						func = {|val, num, chan, src|
							if(that['enabled']) {
								// Post << "received:\t" << that['midiMsgType'].asString
								// 	 << Char.nl
								// 	 << "with value:\t" << val<< Char.nl
								// 	 << "on channel:\t" << chan << Char.nl
								// 	 << "from ID:\t" << src
								// 	 << Char.nl;
								SystemClock.sched(that['latency'], {
									that['netAddr'].sendMsg(oscAddr, num, val);
								});
							};
						};
					};
					that['midiFunc'] = MIDIFunc(
						func:		func,
						msgNum: 	nil, // respond to all vals
						chan:		that['midiChan'],
						msgType:	that['midiMsgType'],
						srcID:		that['midiSrcID']
					);
				};

				that;

			};

			that['free'] = {
				that['midiFunc'].free;
				that['netAddr'].disconnect;
				that.free;

				that;
			};

			~channels[name.asSymbol] = that;
		};

		that;
	};

	that;
};
)

~channels = IdentityDictionary();
i = ~addDevice.("piano", true);
i['addNetAddr'].("127.0.0.1", 6666);
i['setMidiMsgType'].('noteOn');
i['setMidiChan'].(0);
i['setMidiSrcID'].(
	MIDIClient.sources.select { |source|
		source.device.asSymbol == 'ipMIDI' && source.name.asSymbol === 'Port 1'
	}[0].uid
);
i['addMidiFunc'].();
i['disable'].();
i['enable'].();
i['setLatency'].(0.5)