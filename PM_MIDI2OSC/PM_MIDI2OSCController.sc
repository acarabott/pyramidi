PM_MIDI2OSCController {
	classvar controllers;

	*initClass {
		controllers = List[];
	}

	*new {
		^super.new.midi2oscControllerInit;
	}

	*addController {|controller|
		controllers.add(controller);
	}

	midi2oscControllerInit {
		PM_MIDI2OSCController.addController(this);
	}

	*displayError {|string|

		string =  "----------------------------------" ++ Char.nl
			   ++ "ERROR:" ++ Char.nl
			   ++ $\t ++ string ++ Char.nl;

		if(controllers.size > 0) {
			controllers.do {|controller|
				controller.displayError(string);
			}
		} {
			string.postln;
		};
	}

	displayError {|string|
		string.postln;
	}
}