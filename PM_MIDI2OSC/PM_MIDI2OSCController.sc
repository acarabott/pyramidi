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
             << $\t << string.tr(Char.nl, Char.nl ++ Char.tab)
             << Char.nl;

        // TODO implement error on view, if no view, print
    }
}