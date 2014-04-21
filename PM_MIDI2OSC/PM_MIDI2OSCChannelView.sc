PM_MIDI2OSCChannelView {
	var view;
	var viewWidth;
	var margin;
	var fullWidth;


	var controller;

	*new {|aParent|
		^super.new.midi2oscChannelViewInit(aParent);
	}

	midi2oscChannelViewInit {|aParent|
		viewWidth = 150;
		this.initView(aParent);

	}

	initView {|aParent|
		view = View(aParent, viewWidth@580);
		view.addFlowLayout;
		view.onClose = {
			// controller.free;
		};

		margin = view.decorator.margin.x;
		fullWidth = viewWidth - (margin * 2);

		if(aParent.isNil) {
			view.front;
		};
	}

	controller_ {|aController|
		if(aController.isKindOf(PM_MIDI2OSCChannelController).not) {
			^this;
		};

		controller = aController;

		// controller.debug("view now has a controller");
		^this;
	}
}