PM_MIDI2OSCChannelView {
	var view;
	var viewWidth;
	var margin;
	var fullWidth;

	var <controller;

	*new {|aParentView|
		^super.new.midi2oscChannelViewInit(aParentView);
	}

	midi2oscChannelViewInit {|aParentView|
		viewWidth = 		150;
		view = 				this.createView(aParentView);
		margin = 			view.decorator.margin.x;
		fullWidth = 		viewWidth - (margin * 2);

	}

	createView {|aParentView|
		var newView;

		if([View.implClass, Window.implClass].includes(aParentView.class).not &&
			aParentView.notNil) {

			controller.error(\view,
				"parent view should be a Window, View or nil"
			);

			// Default to nil, use own window
			aParentView = nil;
		};

		newView = View(aParentView, viewWidth@580);
		newView.addFlowLayout;
		newView.onClose = {
			// controller.free;
		};

		if(aParentView.isNil) {
			newView.front;
		};

		^newView;
	}

	controller_ {|aController|
		if(aController.isKindOf(PM_MIDI2OSCChannelController).not) {
			Post << "controller not a PM_MIDI2OSCChannelController!" << Char.nl;
			^this;
		};

		controller = aController;
		controller.debug("SET: view now has a controller");

		^this;
	}

	// createName
}