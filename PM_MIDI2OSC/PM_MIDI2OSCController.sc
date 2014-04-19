PM_MIDI2OSCController {
    *initClass {
    }

    *new {
        ^super.new.midi2oscControllerInit;
    }

    midi2oscControllerInit {
    }

    error {|string|
        this.printMessage("ERROR", string);
        // TODO implement error on view, if no view, print
    }

    warning {|string|
        this.printMessage("WARNING", string);
    }

    printMessage {|type, string|
        Post << "------------Controller------------" << Char.nl
             << type << ":" << Char.nl
             << $\t << string.tr(Char.nl, Char.nl ++ Char.tab)
             << Char.nl;
    }
}