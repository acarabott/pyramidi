PM_MIDI2OSCChannel {
	classvar <midiChannels = 	[nil] ++ (0..15);
	classvar <midiMsgTypes = 	MIDIFunc.defaultDispatchers.keys.asArray;
	classvar <midiSrcs = 		MIDIClient.sources
	classvar <midiSrcIDs = 		[nil] ++ MIDIClient.sources.collect (_.uid);
	classvar <midiNonNumTypes = [\touch, \program, \bend];

	var <name;;
	var <enabled;
	var netAddr;
	var <latency;
	var midiFunc;
	var <midiChan;
	var <midiMsgType;
	var <midiSrcID;

	*new {|aName|
		^super.new.midi2oscChannelInit(aName);
	}

	midi2oscChannelInit {|aName|
		name = 			aName;
		enabled = 		true;
		midiChan = 		-1; // nil responds to all
		midiSrcID = 	-1; // nil responds to all
		latency = 		0;
	}
}