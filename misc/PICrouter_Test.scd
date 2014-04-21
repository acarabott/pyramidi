//
// Open the OSC receive port
//
//   30303 : Only used to receive the OSC message "/sys/discover/device".
//   8000  : Used to receive all OSC messages except for "/sys/discover/device".
//
thisProcess.openUDPPort(30303);
thisProcess.openUDPPort(8000);


//
// Set destination IP address(PICrouter's IP) and port number
//
//   If there are a number of PICrouters on your local network and you want to
//   control at the same time, it's convenient to use the multicast address
//   224.0.0.1.
//
n = NetAddr("224.0.0.1", 8080);
// n = NetAddr("172.16.1.12", 8080);


//
// Turn on/off the onboard LEDs (OSC Sending Test)
//

// LED1(Red)
n.sendMsg("/std/onboard/led", 0 , "on");
n.sendMsg("/std/onboard/led", 0 , "off");
// LED2(Yellow)
n.sendMsg("/std/onboard/led", 1 , "on");
n.sendMsg("/std/onboard/led", 1 , "off");


//
// Get the onboard tact switch's state (OSC Receiving Test)
//
//   the OSC message "/std/onboard/sw1" is sent when the onboard tact switch
//   "SW1" is pressed and released.
//
//   note: If your computer has already joined the Bonjour, you can receive
//         the switch state. But if not, please go to "Discover PICrouters
//         on your local network" and change the remote IP ahead.
//
(
OSCdef(\sw1,
	{
		arg msg, time, addr, recvPort;

		msg.postln;
	},
	'/std/onboard/sw1',
	nil
);
)


//
// Discover PICrouters on your local network
//
//   By sending the OSC message "/sys/discover/devices" to 224.0.0.1,
//   you can discover the PICrouters on your local network and get their
//   network configuration. each PICrouter returns the OSC message
//   "/sys/discoverd/device". then,  the following parameters are this
//   message's arguments.
//
//     HostIP      --> PICrouter's IP address(provided by router's DHCP function)
//     HostMAC     --> PICrouter's MAC address(Unique address)
//     HostPort    --> PICrouter's Port number(default:8080)
//     RemoteIP    --> Destination IP address(default:224.0.0.1)
//     Remote Port --> Destination Port number(default:8000)
//
(
OSCdef(\discover,
	{
		arg msg, time, addr, recvPort;
		//msg.postln;
		msg[1].postln; // Host IP
		msg[2].postln; // Host MAC Address
		msg[3].postln; // Host Port
		msg[4].postln; // Remote IP
		msg[5].postln; // Remote Port
	},
	'/sys/discovered/device',
	nil
);
)

n.sendMsg("/sys/discover/devices");


//
// Change the Remote IP address of PICrouter
//
//   the default remote IP address is 224.0.0.1. It's convenient if your PC/Mac
//   joins the Bonjour. However, the communication over the multicast has a latency.
//   By changing the remote IP from the multicast address to PC/Mac's IP, it will
//   be reduced.
//   the OSC message "/sys/remote/ip/set" is to change the remote IP. And then,
//   the PICrouter returns the OSC message "/sys/configuration" when the setting
//   is renewed correctly.
//
(
OSCdef(\configuration,
	{
		arg msg, time, addr, recvPort;

		msg.postln;
	},
	'/sys/configuration',
	nil
);
)

// Change the IP address
n.sendMsg("/sys/remote/ip/set", "172.16.1.11");

// Confirm the current setting
(
OSCdef(\getremoteip,
	{
		arg msg, time, addr, recvPort;

		msg.postln;
	},
	'/sys/remote/ip',
	nil
);
)
n.sendMsg("/sys/remote/ip/get");


//
// OSC <--> MIDI converting
//
//   By connecting an USB_MIDI device to the PICrouter's USB Host Port(USB_A
//   connector), you can use the OSC-MIDI converting function as follows.
//
// Input                                Output
//      OSC:/midi/note/set 0 48 127 --> USB_MIDI:note ch=1, pitch=48, velocity=127
// USB_MIDI:cc ch=3, num=10, val=63 -->      OSC:/midi/cc 2 10 63
//
// Then, the PICrouter supports the following MIDI messages.
//
//   1. Note On/Off
//   2. Control Change
//   3. Pitch Bend
//   4. Key Pressure
//   5. Channel Pressure
//   6. Program Chnage
//

//
// MIDI In/Out Port Settings
//
MIDIIn.connectAll;
m = MIDIOut(2);

//
// USB_MIDI(Host) --> OSC
//
(
OSCdef(\note,
	{
		arg msg, time, addr, recvPort;
		msg.postln;
	},
	'/midi/note',
	nil
);
)

(
OSCdef(\cc,
	{
		arg msg, time, addr, recvPort;
		msg.postln;
	},
	'/midi/cc',
	nil
);
)

(
OSCdef(\pc,
	{
		arg msg, time, addr, recvPort;
		msg.postln;
	},
	'/midi/pc',
	nil
);
)

(
OSCdef(\bend,
	{
		arg msg, time, addr, recvPort;
		msg.postln;
	},
	'/midi/pb',
	nil
);
)

m.noteOn(15, 61, 60);//ch=15, pitch=61, vel=60
m.noteOff(15, 61, 0);//ch=15, pitch=61, vel=0
m.allNotesOff(15);

m.control(13, 24, 39);//ch=13, num=24, val=39

m.program(3, 1);//ch=3, val=1

m.bend(10, 120);//ch=10, val=120


//
// OSC --> USB_MIDI(Host)
//
(
MIDIdef.noteOn(\noteon,
	{
		arg ...args;
		args.postln;
	}
);
)

(
MIDIdef.noteOff(\noteoff,
	{
		arg ...args;
		args.postln;
	}
);
)

(
MIDIdef.cc(\cc,
	{
		arg ...args;
		args.postln;
	}
);
)

(
MIDIdef.program(\pc,
	{
		arg ...args;
		args.postln;
	}
);
)

(
MIDIdef.bend(\bend,
	{
		arg ...args;
		args.postln;
	}
);
)

n.sendMsg("/midi/note/set", 1, 48, 127);// ch=1, pitch=48, vel=127(NOTE_ON)
n.sendMsg("/midi/note/set", 1, 48, 0);// ch=1, pitch=48, vel=0(NOTE_OFF)

n.sendMsg("/midi/cc/set", 3, 30, 15);// ch=3, num=30, val=15(CONTROL_CHANGE)

n.sendMsg("/midi/pc/set", 4, 2);// ch=4, val=2(PROGRAM_CHANGE)

n.sendMsg("/midi/pb/set", 7, 63, 0);// ch=7 val=63(msb=63, lsb=0)(PITCH_BEND)