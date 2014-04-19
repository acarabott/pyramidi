PM_MIDI2OSCChannel {
	classvar midiChannels;
	classvar midiMsgTypes;
	classvar midiSrcIDs;
	classvar midiNonNumTypes;

	var <name;
	var <enabled;
	var netAddr;
	var <latency;
	var midiFunc;
	var <midiChannel;
	var <midiMsgType;
	var <midiSrcID;

	var <>controller;

	*initClass {
		midiChannels = 		[nil] ++ (0..15);
		midiMsgTypes = 		[\touch, \sysex, \polytouch, \control, \noteOff,
								\program, \noteOn, \bend, \sysrt ];
		midiNonNumTypes = 	[\touch, \program, \bend];
	}

	*new {|aName|
		^super.new.midi2oscChannelInit(aName);
	}

	*midiSrcIDs {
		if(MIDIClient.initialized.not) {
			PM_MIDI2OSCController.displayError(
				"MIDIClient is not initialised, do MIDIClient.init;"
			);

			^[];
		} {
			if(midiSrcIDs.isNil) {
				midiSrcIDs = [nil] ++ MIDIClient.sources.collect (_.uid);
			};
		};

		^midiSrcIDs.copy;
	}

	midi2oscChannelInit {|aName|
		name = 			aName;
		enabled = 		true;
		netAddr = 		nil;
		latency =		0.0;
		midiFunc = 		nil;
		midiChannel = 	-1; // nil would respond to all
		midiMsgType = 	nil;
		midiSrcID = 	-1; // nil would respond to all

		controller = 	nil;
	}
}