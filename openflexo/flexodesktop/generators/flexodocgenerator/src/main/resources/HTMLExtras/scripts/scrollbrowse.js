
var ScrollBrowsable = Class.create();
ScrollBrowsable.prototype = {
	initialize: function(element) {
		this.element = element;
		this.isActive = false;
		
		this.element.onmousedown = this.mouseDown.bindAsEventListener(this);
		this.element.onmousemove = this.mouseMove.bindAsEventListener(this);
		Event.observe(document, "mouseup", this.mouseUp.bindAsEventListener(this));	
	},
	
	setIsActive: function()
	{
		this.isActive = true;
		this.element.style.cursor = 'move';
	},
	
	mouseDown: function(event)
	{
		if(!event)
			event = window.event;
	
		var pointer = [Event.pointerX(event), Event.pointerY(event)];
    	var offsets = Position.cumulativeOffset(this.element);
    
		if(pointer[0] < (offsets[0] + this.element.offsetWidth - 20) && pointer[1] < (offsets[1] + this.element.offsetHeight - 20))
		{
			this.timer = setTimeout(this.setIsActive.bindAsEventListener(this), 100);
			this.startX = event.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
	    	this.startY = event.clientY + document.body.scrollTop + document.documentElement.scrollTop;
		}
	
		event.returnValue = false; 
		return false;
	},
	
	mouseMove: function(event)
	{
		if(this.isActive)
		{
			if(!event)
				event = window.event;
				
			var positionX = event.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
	    	var positionY = event.clientY + document.body.scrollTop + document.documentElement.scrollTop;
	    	
	    	var scrollByX = this.startX - positionX;
	    	var scrollByY = this.startY - positionY;
		
			this.element.scrollTop = this.element.scrollTop + scrollByY;
			this.element.scrollLeft = this.element.scrollLeft + scrollByX;
			
			this.startX = positionX;
	   		this.startY = positionY;
		}
		
		event.returnValue = false; 
		return false;
	},
	
	mouseUp: function(event)
	{
		clearTimeout(this.timer);
		if(this.isActive)
		{
			if(!event)
				event = window.event;
		
			this.isActive = false;
			this.element.style.cursor = '';
			event.returnValue = false; 
			return false;
		}
	}
}