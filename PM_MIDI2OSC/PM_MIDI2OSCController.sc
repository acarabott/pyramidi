PM_MIDI2OSCController {
    *initClass {
    }

    *new {
        ^super.new.midi2oscControllerInit;
    }

    midi2oscControllerInit {
        PM_MIDI2OSCController.addController(this);
    }

    error {|string|
        Post << "----------------------------------" << Char.nl
             << "ERROR:" << Char.nl
             << $\t << string << Char.nl;

        // TODO implement error on view, if no view, print
    }
}