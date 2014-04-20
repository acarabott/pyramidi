x = PM_MIDI2OSCChannel("rhodes");

x.enabled = false;
x.enabled.not;
x.enabled = true;
x.enabled;
x.enabled = "true"
x.enabled;

x.midiChannel = 1;
x.midiChannel === 1;
x.midiChannel = "1";
x.midiChannel = nil;
x.midiChannel.isNil;

x.midiMsgType = \noteOn;
x.midiMsgType == \noteOn;
x.midiMsgType == \noteOff;
x.midiMsgType = "\\noteOn";


x.midiSrcID = 123
MIDIClient.init;

x.midiSrcID = x.midiSrcIDs[1];
x.midiSrcID == x.midiSrcIDs[1];
x.midiSrcID.postln;


x.createNetAddr(123, 2343);
x.createNetAddr("badip", 2343);
x.createNetAddr("666.666.666.666", 2343);
x.createNetAddr("localhost", 2343);
x.createNetAddr("123.123.123.123", "243");
x.createNetAddr("123.123.123.123", 243.3);
x.createNetAddr("123.123.123.123", 666);

x.ip;
x.port;

c = PM_MIDI2OSCController();
x.controller = c;
x.controller = "hi";

x.createNetAddr("123.123.123.123", 243.3);
x.createNetAddr("123.123.123.123", 666);
x.createNetAddr("123.123.123.123", 666);