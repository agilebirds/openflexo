var LEVEL_PROJECT = 'Project';
var LEVEL_PROCESS = 'Process';
var LEVEL_ACTIVITY = 'Activity';
var LEVEL_OPERATION = 'Operation';
var LEVEL_ACTION = 'Action';
var LEVEL_OTHER = 'Other';

var TYPE_PROJECT = 'Project';
var TYPE_PROCESSFOLDER = 'ProcessFolder';
var TYPE_PROCESS = 'Process';
var TYPE_SUBPROCESS = 'SubProcess';
var TYPE_ACTIVITY = 'Activity';
var TYPE_ACTIVITYBEGIN = 'ActivityBegin';
var TYPE_ACTIVITYEND = 'ActivityEnd';
var TYPE_ACTIVITYSELFEXECUTABLE = 'ActivitySelfExecutable';
var TYPE_ACTIVITYSUBPROCESS = 'ActivitySubProcess';
var TYPE_OPERATOR = 'Operator';
var TYPE_OPERATORAND = 'OperatorAnd';
var TYPE_OPERATOROR = 'OperatorOr';
var TYPE_OPERATORIF = 'OperatorIf';
var TYPE_OPERATORLOOP = 'OperatorLoop';
var TYPE_OPERATORSWITCH = 'OperatorSwitch';
var TYPE_OPERATION = 'Operation';
var TYPE_OPERATIONBEGIN = 'OperationBegin';
var TYPE_OPERATIONEND = 'OperationEnd';
var TYPE_OPERATIONSELFEXECUTABLE = 'OperationSelfExecutable';
var TYPE_EVENT = 'Event';
var TYPE_ARTEFACT = 'Artefact';
var TYPE_EVENTCANCELHANDLER = 'EventCancelHandler';
var TYPE_EVENTCANCELTHROWER = 'EventCancelThrower';
var TYPE_EVENTCOMPENSATEHANDLER = 'EventCompensateHandler';
var TYPE_EVENTCOMPENSATETHROWER = 'EventCompensateThrower';
var TYPE_EVENTCHECKPOINT = 'EventCheckpoint';
var TYPE_EVENTFAULTHANDLER = 'EventFaultHandler';
var TYPE_EVENTFAULTTHROWER = 'EventFaultThrower';
var TYPE_EVENTMAILIN = 'EventMailIn';
var TYPE_START_EVENT = 'StartEvent';
var TYPE_END_EVENT = 'EndEvent';
var TYPE_EVENTMAILOUT = 'EventMailOut';
var TYPE_EVENTREVERT = 'EventRevert';
var TYPE_EVENTTIMEOUT = 'EventTimeOut';
var TYPE_EVENTTIMER = 'EventTimer';
var TYPE_OTHER = 'Other';

var OPTION_KEEPIMAGESIZE = 'OptionKeepImageSize';
var OPTION_AUTORESIZEIMAGE = 'OptionAutoResizeImage';

var MAP_PREFIX = 'map_';
var CONTENT_PREFIX = 'content_';
var AREA_PREFIX = 'area_';
var DESCRIPTION_PREFIX = 'desc_';
var AREAPLUS_SUFIX = '_plus';

var FlexoReader = 
{
    initialize: function()
    {
		this.currentContentNode = null;
		this.currentDescriptionNode = null;
		this.allNodes = new Object();
		
		this.nextArray = [];
		this.backArray = [];
		this.currentLitbox = null;
		this.selectionDiv = null;
		
		this.useAlphabeticalOrder = false;
		
		this.rootNode = new FlexoNode('ROOT', 'Flexo reader', LEVEL_PROJECT, TYPE_PROJECT, null, 0, null);
		this.allNodes[this.rootNode.id] = this.rootNode;
    },
    
    addNode: function(node, parentNodeId)
    {
    	this.addNode2(node,parentNodeId, true);
    },
    
    addNode2: function(node, parentNodeId, addToTree)
    {
		if (parentNodeId && this.getNode(parentNodeId)) 
		{
			if (addToTree) {
				node.index = this.getNode(parentNodeId).childNodes.length == 0 ? 1 : this.getNode(parentNodeId).childNodes[this.getNode(parentNodeId).childNodes.length-1].index + 1;
			}
			node.setPreviousNode2(this.getNode(parentNodeId),addToTree);
		} else {
			if (addToTree) {
				node.index = this.rootNode.childNodes.length == 0 ? 1 : this.rootNode.childNodes[this.rootNode.childNodes.length-1].index + 1;
			}
			node.setPreviousNode2(this.rootNode,addToTree);
		}
    	this.allNodes[node.id] = node;
    },
    
    insertFolder: function(node, parentNodeId, childIds)
    {
    	var parentNode;
    	if (parentNodeId) 
    		parentNode = this.getNode(parentNodeId);
    	
    	if(!parentNode)
    		parentNode = this.rootNode;
    		
    	for(var i in childIds)
    	{
    		if(parentNode.childNodes[childIds[i]])
    			parentNode.childNodes[childIds[i]].setPreviousNode(node);
    	}
    	
    	node.index = parentNode.childNodes.length == 0 ? 1 : parentNode.childNodes[parentNode.childNodes.length-1].index + 1;
    	node.setPreviousNode(parentNode);
    	
    	this.allNodes[node.id] = node;
    },
    
    getNode: function(id)
    {
        return this.allNodes[id];
    },
    
    render: function()
    {
		this.divLeftColumn = document.getElementById('htmlDocLeftColumn');
		this.divUpperLeftColumn = document.getElementById('htmlDocUpperLeftColumn');
		this.divUpperLeftColumnHeader = document.getElementById('htmlDocUpperLeftColumnHeader');
		this.divUpperLeftColumnContent = document.getElementById('htmlDocUpperLeftColumnContent');
		this.divLowerLeftColumn = document.getElementById('htmlDocLowerLeftColumn');
		this.divRightColumn = document.getElementById('htmlDocRightColumn');
		this.divRightColumnHeader = document.getElementById('htmlDocRightColumnHeader');
		this.divRightColumnContent = document.getElementById('htmlDocRightColumnContent');
		
		//Add link to selected element in lower left column
		if(this.divLowerLeftColumn)
		{
			var infoDiv = document.createElement("div");
			infoDiv.className = 'htmlDocLowerLeftColumnInfo';
			var buttonElement = document.createElement("img");
			buttonElement.src = 'resources/img/Info.gif';
			buttonElement.onclick = FlexoReader.displayElementInfo;
			infoDiv.appendChild(buttonElement);
			this.divLowerLeftColumn.appendChild(infoDiv);
		}
		
		//Fill right column header
		if(this.divRightColumnHeader)
		{
			this.divRightColumnHeader.innerHTML = 
				'<div class="htmlDocRightColumnHeaderLeft">' +
	            '<img id="img_Back" src="resources/img/MenuNAV_Fleche_01.gif" onclick="FlexoReader.openBackNode();" title="Previous component" style="display: none;"></img><img id="img_BackNA" src="resources/img/MenuNAV_Fleche_01_NA.gif"></img><img id="img_Up" src="resources/img/MenuNAV_Fleche_02.gif" onclick="FlexoReader.openParentNode();" title="Parent component" style="display: none;"></img><img id="img_UpNA" src="resources/img/MenuNAV_Fleche_02_NA.gif"></img><img id="img_Next" src="resources/img/MenuNAV_Fleche_03.gif" onclick="FlexoReader.openNextNode();" title="Next component" style="display: none;"></img><img id="img_NextNA" src="resources/img/MenuNAV_Fleche_03_NA.gif"></img>' +
	            '&nbsp;&nbsp;&nbsp;' +
	            '<img id="img_ZoomIn" src="resources/img/zoomPlus.gif" onclick="FlexoReader.zoomIn();" title="Zoom in"></img><img id="img_ZoomInNA" src="resources/img/zoomPlusNA.gif" title="Zoom in not available when option auto resize is on" style="display: none;"></img><img id="img_ZoomOut" src="resources/img/zoomMinus.gif" onclick="FlexoReader.zoomOut();" title="Zoom out"></img><img id="img_ZoomOutNA" src="resources/img/zoomMinusNA.gif" title="Zoom out not available when option auto resize is on" style="display: none;"></img>' +
	            '</div>';
	            
	        var divRightColumnHeaderRight = document.createElement('div');
	        divRightColumnHeaderRight.className = 'htmlDocRightColumnHeaderRight';
	        divRightColumnHeaderRight.innerHTML = '<a href="javascript: void(0);" onclick="FlexoReader.resizeLeftColumnAccordingToImage();">Reset size</a> | ';
	            
	        var aElement = document.createElement('a');
	        aElement.href = 'javascript: void(0);';
	        aElement.onclick = function() { new LITBox('<div><p><strong>Options:</strong><br/><br/><input type=\'radio\' name=\'optionImageResize\' id=\'optionKeepImageSize\' onclick=\'FlexoReader.setOptionKeepImageSize();\'' +(FlexoReader.isOptionKeepImageSize()?'checked=true':'')+' /><label for=\'optionKeepImageSize\'>Keep workflow image size</label><br/><input type=\'radio\' name=\'optionImageResize\' id=\'optionAutoResizeImage\'  onclick=\'FlexoReader.setOptionAutoResizeImage();\'' +(FlexoReader.isOptionAutoResizeImage()?'checked=true':'')+'/><label for=\'optionAutoResizeImage\'>Automatically resize the worflow image according the available space</label><br/></p></div>', {type: 'html', height: 150, top: 40, left: (getWindowSize()[0]-700)});}
	        aElement.innerHTML = 'Options';
	        divRightColumnHeaderRight.appendChild(aElement);
	        divRightColumnHeaderRight.appendChild(document.createTextNode(' | '));
	        
	        aElement = document.createElement('a');
	        aElement.href = 'javascript: void(0);';
	        aElement.onclick = function() { new LITBox('<div><p><strong>Activity level:</strong><br/><ul><li>Clicking any activity level element will select it and display its description.</li><li>Double clicking an activity will open its operation level.</li><li>Clicking the &quot;+&quot; on a sub process activity will open its linked process.</li></ul></p><p><strong>Operation level:</strong><br/><ul><li>Clicking any operation level element will select it and display its description.</li><li>Clicking with &quot;Shift&quot; pressed on any operation will display its linked screen (if any).</li><li>If an operation element has a linked screen, it is displayed in the associated description.</li><li>Clicking the upper left &quot;x&quot; or clicking anywhere outside the operation level will close the operation level.</li></ul></p></div>', {type: 'html', height: 300});}
	        aElement.innerHTML = 'Tips';
	        divRightColumnHeaderRight.appendChild(aElement);
	        
	        this.divRightColumnHeader.appendChild(divRightColumnHeaderRight);
	    }
		
		
		this.imgBack = document.getElementById('img_Back');
		this.imgBackNA = document.getElementById('img_BackNA');
		this.imgNext = document.getElementById('img_Next');
		this.imgNextNA = document.getElementById('img_NextNA');
		
		this.imgZoomIn = document.getElementById('img_ZoomIn');
        this.imgZoomInNA = document.getElementById('img_ZoomInNA');
        this.imgZoomOut = document.getElementById('img_ZoomOut');
        this.imgZoomOutNA = document.getElementById('img_ZoomOutNA');
        
        this.imgUp = document.getElementById('img_Up');
        this.imgUpNA = document.getElementById('img_UpNA');
    
		this.optionImageResize = OPTION_KEEPIMAGESIZE;
		
        this.dtree = new dTree('FlexoReader.dtree');
        this.dtree.config.inOrder = true;
        this.dtree.config.useCookies = false;
        this.dtree.config.closeSameLevel = true;

		var allNodesSorted = new Array();
		        
        this.dtree.add(this.rootNode.id, -1, this.rootNode.name, null, null, null, this.rootNode.getIconPath(), this.rootNode.getIconPath(), true);
        for(var i=0; i<this.rootNode.childNodes.length; i++)
        {
            this.rootNode.childNodes[i].addToDtree(this.dtree);
            this.rootNode.childNodes[i].addToArray(allNodesSorted);
        }

		this.divUpperLeftColumnHeader.innerHTML = '<input id="FlexoReaderAutoComplete" autocomplete="off" type="text" value="Type to search" onblur="if(this.value == \'\') this.value = \'Type to search\'" onfocus="if(this.value == \'Type to search\') this.value = \'\'"/>';
        this.divUpperLeftColumnContent.innerHTML = this.dtree;
        var autocompleteDiv = document.createElement('div');
        autocompleteDiv.className = 'autocomplete';
        autocompleteDiv.id = 'FlexoReaderAutoCompleteDiv';
        autocompleteDiv.style.display = 'none';
        document.body.appendChild(autocompleteDiv);
        
        var selectedNodeId = getUrlParameter('selectedNode');
        if(selectedNodeId && this.getNode(selectedNodeId))
        	this.getNode(selectedNodeId).select();
        else
        	this.rootNode.childNodes[0].select();
    
        window.onresize = this.onWindowResize.bindAsEventListener(this);
		var resizeableTreeVsDoc = new Resizeable(this.divUpperLeftColumn.id, {top: 0, right:0, left: 0, duringresize: FlexoReader.resizeLowerLeftColumnAccordingToUpperOne.bindAsEventListener(this)});
		var resizeableLeftVsRight = new Resizeable(this.divLeftColumn.id, {top: 0, bottom:0, left: 0, duringresize: function(){FlexoReader.resizeAccordingToSelectedOption(false);}, resize: function(){FlexoReader.resizeAccordingToSelectedOption(true);}});
		
	  	Event.observe(this.divUpperLeftColumn, "scroll", resizeableLeftVsRight.endResize.bindAsEventListener(resizeableLeftVsRight));
	  	Event.observe(this.divLowerLeftColumn, "scroll", resizeableLeftVsRight.endResize.bindAsEventListener(resizeableLeftVsRight));
	  	
	  	Event.observe(this.divUpperLeftColumn, "scroll", resizeableTreeVsDoc.endResize.bindAsEventListener(resizeableTreeVsDoc));
	    
	    this.resizeVertical();
		new ScrollBrowsable(this.divRightColumnContent);
		//new ScrollBrowsable(this.divUpperLeftColumn);
		//new ScrollBrowsable(this.divLowerLeftColumn);
		
		//Create autocomplete
		new Autocompleter.FlexoReader('FlexoReaderAutoComplete', 'FlexoReaderAutoCompleteDiv', allNodesSorted, {fullSearch: true});
    },
    
    getSelectionDiv: function()
    {
    	if(!this.selectionDiv)
    	{
    		this.selectionDiv = document.getElementById('FlexoReader_SelectionDiv');
    		if(!this.selectionDiv)
    		{ //Create div
    			this.selectionDiv = document.createElement('div');
    			this.selectionDiv.id = 'FlexoReader_SelectionDiv';
    			this.selectionDiv.className = 'htmlDocSelectionDiv';
    			this.selectionDiv.style.display = 'none';
    			
    			this.subSelectionDiv = document.createElement('div');
    			this.subSelectionDiv.id = 'FlexoReader_SubSelectionDiv';
    			
    			this.selectionDiv.appendChild(this.subSelectionDiv);
    			this.divRightColumnContent.appendChild(this.selectionDiv);
    		}
    	}
    	return this.selectionDiv;
    },
    
    getSubSelectionDiv: function()
    {
    	this.getSelectionDiv();
    	
    	return this.subSelectionDiv;
    },
    
    close: function()
    {
        var previousImageNode = this.currentContentNode;
        this.currentContentNode.previousNode.open();
        previousImageNode.select();
        this.scrollWorkflowImageTo(this.getScrollVal(this.currentDescriptionNode.getArea()));
    },
    
    open: function(nodeId)
    {
        this.getNode(nodeId).open();
    },
    
    openParentNode: function()
    {
        var parentNode = this.currentContentNode.getProcessOrOtherNode().previousNode.getProcessOrOtherNode();
        if(parentNode)
            parentNode.select();
    },
    
    openBackNode: function()
    {
        if(this.backArray.length > 0)
        {
            var node = this.backArray.pop();
            this.nextArray.push(this.currentContentNode);
            node.select(true);
            
            if(this.backArray.length == 0)
            {
				if(this.imgBack)
               		this.imgBack.style.display = 'none';
               	if(this.imgBackNA)
                	this.imgBackNA.style.display = '';
            }
            
            if(this.imgNext)
            	this.imgNext.style.display = '';
            if(this.imgNextNA)
            	this.imgNextNA.style.display = 'none';
        }
    },
    
    openNextNode: function()
    {
        if(this.nextArray.length > 0)
        {
            var node = this.nextArray.pop();
            this.backArray.push(this.currentContentNode);
            node.select(true);
            
            if(this.nextArray.length == 0)
            {
            	if(this.imgNext)
                	this.imgNext.style.display = 'none';
                if(this.imgNextNA)
                	this.imgNextNA.style.display = '';
            }
            if(this.imgBack)
            	this.imgBack.style.display = '';
            if(this.imgBackNA)
            	this.imgBackNA.style.display = 'none';
        }
    },
    
    select: function(nodeId, allowAutoScroll)
    {
        if(!nodeId)
            this.currentDescriptionNode.select(false, allowAutoScroll);
        else
            this.getNode(nodeId).select(false, allowAutoScroll);
    },
    
    setOptionKeepImageSize: function()
    {
        this.optionImageResize = OPTION_KEEPIMAGESIZE;
        
        if(this.imgZoomIn)
       		this.imgZoomIn.style.display = '';
       	if(this.imgZoomInNA)
        	this.imgZoomInNA.style.display = 'none';
        if(this.imgZoomOut)
        	this.imgZoomOut.style.display = '';
        if(this.imgZoomOutNA)
        	this.imgZoomOutNA.style.display = 'none';
        	
        this.resizeLeftColumnAccordingToImage();
    },
    
    setOptionAutoResizeImage: function()
    {
        this.optionImageResize = OPTION_AUTORESIZEIMAGE;
        
        if(this.imgZoomIn)
       		this.imgZoomIn.style.display = 'none';
       	if(this.imgZoomInNA)
        	this.imgZoomInNA.style.display = '';
        if(this.imgZoomOut)
        	this.imgZoomOut.style.display = 'none';
        if(this.imgZoomOutNA)
        	this.imgZoomOutNA.style.display = '';
        	
        this.resizeRightColumnAccordingToLeftColumn(true, true);
    },
    
    isOptionKeepImageSize: function()
    {
        return this.optionImageResize == OPTION_KEEPIMAGESIZE;
    },
    
    isOptionAutoResizeImage: function()
    {
        return this.optionImageResize == OPTION_AUTORESIZEIMAGE;
    },
    
    zoom: function(percent)
    {
        if(this.isOptionKeepImageSize() && this.currentContentNode.isContentImage())
        {
            var tmpOldSize = [this.currentContentNode.getImageWidth(), this.currentContentNode.getImageHeight()];
            var newWidth = Math.round(tmpOldSize[0] * (1 + percent));
            var newHeight = Math.round(tmpOldSize[1] * (1 + percent));

            var oldScrollHeight = this.divRightColumnContent.scrollHeight;
            var oldScrollWidth = this.divRightColumnContent.scrollWidth;

            this.currentContentNode.getContent().style.width = newWidth + 'px';
            this.currentContentNode.getContent().style.height = newHeight + 'px';

            this.resizeAreas();

            this.divRightColumnContent.scrollTop = this.divRightColumnContent.scrollTop +  Math.round((this.divRightColumnContent.scrollHeight - oldScrollHeight)/2);
            this.divRightColumnContent.scrollLeft = this.divRightColumnContent.scrollLeft +  Math.round((this.divRightColumnContent.scrollWidth - oldScrollWidth)/2);
        }
    },
    
    zoomIn: function()
    {
        this.zoom(0.1);
    },
    
    zoomOut: function()
    {
        this.zoom(-0.1);
    },
    
    resizeAccordingToSelectedOption: function(resizeArea)
	{
	    if(this.isOptionAutoResizeImage())
	    {
	        this.resizeRightColumnAccordingToLeftColumn(true, resizeArea);
	    }
	    else
	    {
	        this.resizeRightColumnAccordingToLeftColumn(false);
	    }
	},

	resizeRightColumnAccordingToLeftColumn: function(resizeImage, resizeArea)
	{
	    this.getSelectionDiv().style.display = 'none';
	    if(this.divLeftColumn.offsetWidth > getWindowSize()[0]*70/100)
	        this.divLeftColumn.style.width = Math.round(getWindowSize()[0]*70/100) + 'px';
	    else if(this.divLeftColumn.offsetWidth < getWindowSize()[0]*15/100)
	        this.divLeftColumn.style.width = Math.round(getWindowSize()[0]*15/100) + 'px';
	
	    if(resizeImage && this.currentContentNode.isContentImage())
	    {
	        var tmpOldSize = [this.currentContentNode.getImageWidth(), this.currentContentNode.getImageHeight()];
	        var newWidth = Math.round(getWindowSize()[0] - this.divLeftColumn.offsetWidth - 50);
	        var dif = newWidth - tmpOldSize[0];
	        var newHeight = Math.round(tmpOldSize[1]+((tmpOldSize[1]/tmpOldSize[0])*dif));
	        
	        if(newHeight > (getWindowSize()[1]-40))
	        {
	            newHeight = getWindowSize()[1]-40;
	            dif = newHeight - tmpOldSize[1];
	            newWidth = Math.round(tmpOldSize[0]+((tmpOldSize[0]/tmpOldSize[1])*dif));
	        }
	        
	        this.currentContentNode.getContent().style.width = newWidth + 'px';
	        this.currentContentNode.getContent().style.height = newHeight + 'px';
	        
	        if(resizeArea)
	            this.resizeAreas();
	    }
	    else
	    {
	        this.select(null);
	    }
	
		if(this.divLeftColumn.style.width)
		{
	    	if(isIE6())
	    	    this.divRightColumn.style.marginLeft = parseInt(this.divLeftColumn.style.width)+12 + 'px'; //border 2px + strange ie6
	    	else
	    	    this.divRightColumn.style.marginLeft = parseInt(this.divLeftColumn.style.width)+2 + 'px'; //border 2px
	   	}
	},

	resizeAreas: function()
	{
		if(this.currentContentNode.isContentImage())
		{
		    var map = document.getElementById('map_' + this.currentContentNode.id);
		    var areas = map.getElementsByTagName('area');
		    var newCoords;
		
		    for(var i=0; i<areas.length; i++)
		    {
		        var coords = areas[i].coords.split(',');
		
		        var x1 = Math.round(Number(coords[0])*(this.currentContentNode.getImageWidth()/this.oldSize[0]));
		        var y1 = Math.round(Number(coords[1])*(this.currentContentNode.getImageHeight()/this.oldSize[1]));
		        var x2 = Math.round(Number(coords[2])*(this.currentContentNode.getImageWidth()/this.oldSize[0]));
		        var y2 = Math.round(Number(coords[3])*(this.currentContentNode.getImageHeight()/this.oldSize[1]));
		
		        areas[i].coords = x1+','+y1+','+x2+','+y2;
		    }
		}
	
	    this.select(null);
	
	    this.oldSize = [this.currentContentNode.getImageWidth(), this.currentContentNode.getImageHeight()];
	}, 

	resizeLeftColumnAccordingToImage: function()
	{
		if(this.currentContentNode.isContentImage())
		{
		    this.currentContentNode.getContent().style.width = this.currentContentNode.imageNaturalWidth + 'px';
		    this.currentContentNode.getContent().style.height = this.currentContentNode.imageNaturalHeight + 'px';
		
		    var leftColumnWidth = Math.round(getWindowSize()[0] - this.currentContentNode.getImageWidth()-30);
		    if(leftColumnWidth > getWindowSize()[0]*35/100)
		        leftColumnWidth = Math.round(getWindowSize()[0]*35/100);
		    else if(leftColumnWidth < getWindowSize()[0]*15/100)
		        leftColumnWidth = Math.round(getWindowSize()[0]*15/100);
		
		    this.divLeftColumn.style.width = leftColumnWidth + 'px';
		    if(isIE6())
		        this.divRightColumn.style.marginLeft = parseInt(this.divLeftColumn.style.width)+12 + 'px'; //border 2px + strange ie6
		    else
		        this.divRightColumn.style.marginLeft = parseInt(this.divLeftColumn.style.width)+2 + 'px'; //border 2px
		
	    	this.resizeAreas();
	    	
	    	this.scrollWorkflowImageTo([0, 0]);
	    }
	},

	getScrollVal: function(area)
	{
	    if(!area || !area.coords)
	        return 0;
	    
	    var x = Number(area.coords.split(',')[0]);
	    var y = Number(area.coords.split(',')[1]);
	    
	    var windowHeight = getWindowSize()[1] - 40;
	    var windowWidth = getWindowSize()[0] - this.divLeftColumn.offsetWidth;
	    
	    var scrollVal = [];
	    if(y <= windowHeight/2)
	        scrollVal[1] = 0;
	    else
	        scrollVal[1] = y - windowHeight/2;
	    
	    if(x <= windowWidth/2)
	        scrollVal[0] = 0;
	    else
	        scrollVal[0] = x - windowWidth/2;
	        
	    return scrollVal;
	},

	scrollWorkflowImageTo: function(scrollVal)
	{
	    if(scrollVal)
	    {
	        this.divRightColumnContent.scrollTop = scrollVal[1];
	        this.divRightColumnContent.scrollLeft = scrollVal[0];
	    }
	},

	onWindowResize: function()
	{
	    this.resizeVertical();
	    this.resizeAccordingToSelectedOption(true);
	},

	resizeVertical: function()
	{
	    var windowHeight = getWindowSize()[1];
	    //Resize left column according to current proportion
	    
	    var upperHeight = Math.round(windowHeight * (this.divUpperLeftColumn.offsetHeight/(this.divUpperLeftColumn.offsetHeight + this.divLowerLeftColumn.offsetHeight)));
	    
	    this.divUpperLeftColumn.style.height = (upperHeight-2) + 'px'; //2 = border
	    this.divLowerLeftColumn.style.height = (windowHeight - upperHeight - 10) + 'px'; //10 = padding & border
	    
	    //Resize right column
	    this.divRightColumnContent.style.height = (windowHeight-this.divRightColumnHeader.offsetHeight-10) + 'px'; //10 = padding & border
	},

	resizeLowerLeftColumnAccordingToUpperOne: function()
	{
	    var windowHeight = getWindowSize()[1];
	
		if(this.divUpperLeftColumn.offsetHeight > windowHeight*90/100)
	        this.divUpperLeftColumn.style.height = Math.round(windowHeight*90/100) + 'px';
	    else if(this.divUpperLeftColumn.offsetHeight < windowHeight*10/100)
	        this.divUpperLeftColumn.style.height = Math.round(windowHeight*10/100) + 'px';
	
	    this.divLowerLeftColumn.style.height = (windowHeight - this.divUpperLeftColumn.offsetHeight - 10) + 'px'; //Remove padding & border
	},
	
	sortNodesInAlphabeticalOrder: function()
	{
		this.useAlphabeticalOrder = true;
		this.sortChildNodes(this.rootNode);
	},
	
	sortNodesInIndexOrder: function()
	{
		this.useAlphabeticalOrder = false;
		this.sortChildNodes(this.rootNode);
	},
	
	sortChildNodes: function(node)
	{
		node.childNodes.sort(FlexoReader.sortNode);
		for(var i=0; i < node.childNodes.length; i++)
		{
			this.sortChildNodes(node.childNodes[i]);
		}
	},
	
	sortNode: function(node1, node2)
	{
		if(node1.type == TYPE_OTHER && node2.type != TYPE_OTHER)
			return -1;
			
		if(node1.type != TYPE_OTHER && node2.type == TYPE_OTHER)
			return 1;
			
		if(node1.type == TYPE_PROCESSFOLDER && node2.type != TYPE_PROCESSFOLDER)
			return -1;

		if(node1.type != TYPE_PROCESSFOLDER && node2.type == TYPE_PROCESSFOLDER)
			return 1;

		if((node1.type == TYPE_PROCESS || node1.type == TYPE_SUBPROCESS) && (node2.type != TYPE_PROCESS && node2.type != TYPE_SUBPROCESS))
			return -1;
			
		if((node1.type != TYPE_PROCESS && node1.type != TYPE_SUBPROCESS) && (node2.type == TYPE_PROCESS || node2.type == TYPE_SUBPROCESS))
			return 1;
		
		if(!FlexoReader.useAlphabeticalOrder)
		{
		    var node1Index = node1.index;
		    var node2Index = node2.index;
		    
		    return node1Index - node2Index;
		}
		
		if(!isNaN(parseInt(node1.name)) && !isNaN(parseInt(node2.name)) && parseInt(node1.name) != parseInt(node2.name))
			return parseInt(node1.name) - parseInt(node2.name);
			
		if(node1.name < node2.name)
			return -1;
		
		if(node1.name > node2.name)
			return 1;
			
		return 0;
	},
	
	getPrefixDirectoryPath: function(scriptSrc)
	{
		var scripts = document.getElementsByTagName('script');
		var thisScript = null;
		for(var i=0; i < scripts.length && !thisScript; i++)
		{
			var indexOfThisScriptSrc = scripts[i].src.lastIndexOf(scriptSrc);
			if(indexOfThisScriptSrc > -1 && indexOfThisScriptSrc == scripts[i].src.length-scriptSrc.length)
				thisScript = scripts[i];
		}
		
		var prefixDirectoryPath = '';
		if(thisScript && thisScript.getAttribute('prefixDirectoryPath'))
			prefixDirectoryPath = thisScript.getAttribute('prefixDirectoryPath')
			
		return prefixDirectoryPath;
	},
	
	displayElementInfo: function(event)
	{
		var elementUrl = window.location.href;
		var indexOf = elementUrl.indexOf('?');
		if(indexOf != -1)
			elementUrl = elementUrl.substring(0, indexOf);
		elementUrl = elementUrl + '?selectedNode=' +  FlexoReader.currentDescriptionNode.id;
		
		new LITBox('<div><p><strong>Url:</strong><br/>' +
			'<input type=\'text\' name=\'elementUrl\' id=\'elementUrlId\' style=\'width: 95%\' value=\'' +elementUrl+ '\'/><br/></p></div>', {type: 'html', height: 120});
	}
}

var FlexoNode = Class.create();
FlexoNode.prototype = 
{
    initialize: function(id, name, level, type, index, createDivDescriptionFct, createContentFct)
    {
        this.id = id;
        this.content = null;
        this.divDescription = null;
        this.createDivDescriptionFct = createDivDescriptionFct;
        this.createContentFct = createContentFct;
        this.childNodes = [];
        this.name = (name == null?'':name);
        this.type = type;
        this.level = level;
        this.index = index;
        
        if(!this.createDivDescriptionFct)
        { //Add default div description
        	this.createDivDescriptionFct = function(fatherDiv, flexoNode)
        	{
        		var div = document.createElement('div');
				div.id = DESCRIPTION_PREFIX + flexoNode.id;
				div.style.display = 'none';
				div.innerHTML = 'No description found for element \'' + flexoNode.name + '\'.';
				fatherDiv.appendChild(div);
				
				return div;
        	}
        }
    },
    
    setPreviousNode: function(previousNode) {
    	this.setPreviousNode2(previousNode,true);
    },
    
    setPreviousNode2: function(previousNode, addToChilds)
    {
    	if(this.previousNode != previousNode)
    	{
    		if(this.previousNode)
    		{	//Remove from old node
    			if(this.previousNode.childNodes[this.id])
    			{
	    			this.previousNode.childNodes[this.id] = null;
	    			for(var i=0; i < this.previousNode.childNodes.length; i++)
	    			{
	    				if(this.previousNode.childNodes[i].id == this.id)
	    				{
	    					this.previousNode.childNodes.splice(i, 1);
	    					break;
	    				}
	    			}
	    		}
    		}
    		
	        this.previousNode = previousNode;
	        if(previousNode && addToChilds)
	        {
	            previousNode.childNodes[previousNode.childNodes.length] = this;
	            previousNode.childNodes[this.id] = this;
	            previousNode.childNodes.sort(FlexoReader.sortNode);
	        }
		}
    },
    
    getContent: function()
    {
    	if(!this.content && this.createContentFct)
    	{
    		this.content = document.getElementById(CONTENT_PREFIX + this.id);
    		if(!this.content)
    		{
    			this.content = this.createContentFct(FlexoReader.divRightColumnContent, this);
    			
    			if(this.content.tagName.toLowerCase() == 'img')
		        {
		            this.imageNaturalWidth = this.getImageWidth();
		            this.imageNaturalHeight = this.getImageHeight();
		        }
		        
		        this.createContentFct = null;
    		}
    	}
    	
    	return this.content;
    },
    
    isContentImage: function()
    {
    	return this.getContent().tagName.toLowerCase() == 'img';
    },
    
    getDivDescription: function()
    {
    	if(!this.divDescription && this.createDivDescriptionFct)
    	{
    		this.divDescription = document.getElementById(DESCRIPTION_PREFIX + this.id);
    		if(!this.divDescription)
    		{
    			this.divDescription = this.createDivDescriptionFct(FlexoReader.divLowerLeftColumn, this);
    			this.createDivDescriptionFct = null;
    		}
    	}
    	
    	return this.divDescription;
    },
    
    getImageWidth: function()
    {
    	if(!this.isContentImage())
    		return null;
    
        var imageWidth = this.getContent().offsetWidth;
        if((!imageWidth || imageWidth==0) && this.getContent().style.width)
            imageWidth = parseInt(this.getContent().style.width);
            
        if(!imageWidth || imageWidth==0)
            imageWidth = this.getContent().width;
            
        return imageWidth;
    },
    
    getImageHeight: function()
    {
    	if(!this.isContentImage())
    		return null;
    
        var imageHeight = this.getContent().offsetHeight;
        if((!imageHeight || imageHeight==0) && this.getContent().style.height)
            imageHeight = parseInt(this.getContent().style.height);
            
        if(!imageHeight || imageHeight==0)
            imageHeight = this.getContent().height;
            
        return imageHeight;
    },
    
    getArea: function()
    {
    	if(FlexoReader.currentContentNode)
    	{
			var map = document.getElementById(MAP_PREFIX + FlexoReader.currentContentNode.id);
			
			if(map)
			{
				var areas = map.getElementsByTagName('area');
	
			    for(var i=0; i<areas.length; i++)
			    {
			    	if(areas[i].name == AREA_PREFIX + this.id)
			    		return areas[i];
			    }
			}
		}
		
		return null;
    },
    
    getAreaPlus: function()
    {
    	if(FlexoReader.currentContentNode)
    	{
			var map = document.getElementById(MAP_PREFIX + FlexoReader.currentContentNode.id);
			
			if(map)
			{
				var areas = map.getElementsByTagName('area');
	
			    for(var i=0; i<areas.length; i++)
			    {
			    	if(areas[i].name == AREA_PREFIX + this.id + AREAPLUS_SUFIX)
			    		return areas[i];
			    }
			}
		}
		
		return null;
    },
    
    getProcessOrOtherNode: function()
    {
        var node = this;
        while(node && !node.isProcessLevel() && !node.isOtherLevel())
        {
            node = node.previousNode;
        }
        
        return node;
    },
    
    isProcessLevel: function()
    {
        return this.level == LEVEL_PROCESS;
    },
    
    isActivityLevel: function()
    {
        return this.level == LEVEL_ACTIVITY;
    },
    
    isOperationLevel: function()
    {
        return this.level == LEVEL_OPERATION;
    },
    
    isActionLevel: function()
    {
        return this.level == LEVEL_ACTION;
    },
    
    isOtherLevel: function()
    {
        return this.level == LEVEL_OTHER;
    },
    
    getIconPath: function()
    {
        if(this.type == TYPE_PROJECT)
            return "resources/img/Library_WKF.gif";
            
        if(this.type == TYPE_PROCESSFOLDER)
            return "resources/img/folder.gif";    
            
        if(this.type == TYPE_PROCESS || this.type == TYPE_SUBPROCESS)
            return "resources/img/SmallProcess.gif";
            
        if(this.type == TYPE_ACTIVITY)
            return "resources/img/SmallActivity.gif";
        if(this.type == TYPE_ACTIVITYBEGIN)
            return "resources/img/SmallBeginActivity.gif";
        if(this.type == TYPE_ACTIVITYEND)
            return "resources/img/SmallEndActivity.gif";
        if(this.type == TYPE_ACTIVITYSELFEXECUTABLE)
            return "resources/img/SmallSelfExecutable.gif";
        if(this.type == TYPE_ACTIVITYSUBPROCESS)
            return "resources/img/SmallSubProcessNode.gif";
            
        if(this.type == TYPE_OPERATORAND)
            return "resources/img/SmallAnd.gif";
        if(this.type == TYPE_OPERATOROR)
            return "resources/img/SmallOr.gif";
        if(this.type == TYPE_OPERATORIF)
            return "resources/img/SmallIf.gif";
        if(this.type == TYPE_OPERATORLOOP)
            return "resources/img/SmallLoop.gif";
		if(this.type == TYPE_OPERATORSWITCH)
            return "resources/img/SmallSwitch.gif";
        if(this.type == TYPE_OPERATOR)
            return "";
            
        if(this.type == TYPE_OPERATION)
            return "resources/img/SmallOperation.gif";
        if(this.type == TYPE_OPERATIONBEGIN)
            return "resources/img/SmallBeginOperation.gif";
        if(this.type == TYPE_OPERATIONEND)
            return "resources/img/SmallEndOperation.gif";
        if(this.type == TYPE_OPERATIONSELFEXECUTABLE)
            return "resources/img/SmallSelfExecutable.gif";
        
        if(this.type == TYPE_EVENTCANCELHANDLER)
            return "resources/img/SmallCancelHandler.gif";
        if(this.type == TYPE_EVENTCANCELTHROWER)
            return "resources/img/SmallCancelThrower.gif";
        if(this.type == TYPE_EVENTCOMPENSATEHANDLER)
            return "resources/img/SmallCompensateHandler.gif";
        if(this.type == TYPE_EVENTCOMPENSATETHROWER)
            return "resources/img/SmallCompensateThrower.gif";
        if(this.type == TYPE_EVENTCHECKPOINT)
            return "resources/img/SmallCheckpoint.gif";
        if(this.type == TYPE_EVENTFAULTHANDLER)
            return "resources/img/SmallCatch.gif";
        if(this.type == TYPE_EVENTFAULTTHROWER)
            return "resources/img/SmallThrow.gif";
        if(this.type == TYPE_EVENTMAILIN)
            return "resources/img/SmallMailIn.gif";
        if(this.type == TYPE_EVENTMAILOUT)
            return "resources/img/SmallMailOut.gif";
        if(this.type == TYPE_EVENTREVERT)
            return "resources/img/SmallRevert.gif";
        if(this.type == TYPE_EVENTTIMEOUT)
            return "resources/img/SmallTimeOut.gif";
        if(this.type == TYPE_EVENTTIMER)
            return "resources/img/SmallTimer.gif";
        if(this.type == TYPE_ARTEFACT)
        	return "resources/img/SmallArtefact.gif";
        if(this.type == TYPE_START_EVENT)
        	return "resources/img/SmallStartEvent.gif";
        if(this.type == TYPE_END_EVENT)
        	return "resources/img/SmallEndEvent.gif";
            
        if(this.type == TYPE_OTHER)
            return "resources/img/page.gif";
    },
    
    addToDtree: function(dtree)
    {
        dtree.add(this.id, this.previousNode.id, this.name, "javascript: FlexoReader.select('"+this.id+"', true);", null, null, this.getIconPath(), this.getIconPath(), false);
        
        for(var i=0; i<this.childNodes.length; i++)
        {
            this.childNodes[i].addToDtree(dtree);
        }
    },
    
    addToArray: function(array)
    {
    	array.push(this);
        
        for(var i=0; i<this.childNodes.length; i++)
        {
            this.childNodes[i].addToArray(array);
        }
    },
    
    select: function(isBackOrNext, allowAutoScroll)
    {
        var needResizeSelectionDiv = true;
        if(!FlexoReader.currentDescriptionNode || FlexoReader.currentDescriptionNode.id != this.id)
        {
        	if(FlexoReader.currentDescriptionNode)
            	FlexoReader.currentDescriptionNode.getDivDescription().style.display = 'none';
            
            FlexoReader.currentDescriptionNode = this;
            
            this.getDivDescription().style.display = '';
            
            FlexoReader.dtree.openTo(this.id, true);
            
            if(this.isProcessLevel() || this.isOtherLevel())
                needResizeSelectionDiv = !this.open(isBackOrNext);
            else if(this.previousNode!=null && this.previousNode!='undefined')
                needResizeSelectionDiv = !this.previousNode.open(isBackOrNext);
        }
        
        if(needResizeSelectionDiv)
        {
            var area = this.getArea();
            
            if(area && area.coords)
            {
                var deltaX = 4;
                var deltaY = 4;
                
                if(isIE6())
                {
                    deltaX = 0;
                    deltaY = 5;
                }
            
                var position = area.coords.split(',');
                var x = Number(position[0])+deltaX;
                var y = Number(position[1])+deltaY;
                var width = (Number(position[2])-Number(position[0]));
                var height = (Number(position[3])-Number(position[1]));
                
                FlexoReader.getSelectionDiv().style.width = width + 'px';
                FlexoReader.getSelectionDiv().style.height = height + 'px';
                moveObject(FlexoReader.getSelectionDiv().id, x, y);
                FlexoReader.getSelectionDiv().onclick = area.onclick;
                FlexoReader.getSelectionDiv().ondblclick = area.ondblclick;
                FlexoReader.getSelectionDiv().title = area.title;
                
                if(allowAutoScroll || this.globalAllowAutoScroll)
                    Effect.Appear(FlexoReader.getSelectionDiv(), { queue: 'end', duration: 0, afterFinish: function(){FlexoReader.scrollWorkflowImageTo(FlexoReader.getScrollVal(FlexoReader.currentDescriptionNode.getArea()));}});
                else if(FlexoReader.getSelectionDiv().style.display == 'none')
                    Effect.Appear(FlexoReader.getSelectionDiv(), { queue: 'end', duration: 0});
                    
                area = this.getAreaPlus();
                var subDiv = FlexoReader.getSubSelectionDiv();
                
                if(area && area.coords)
                { //SubProcess selected -> need div for the plus expand
                    position = area.coords.split(',');
                    x = Number(position[0])+deltaX - x;
                    y = Number(position[1])+deltaY - y;
                    width = (Number(position[2])-Number(position[0]));
                    height = (Number(position[3])-Number(position[1]));
                    
                    subDiv.style.width = width + 'px';
                    subDiv.style.height = height + 'px';
                    moveObject(subDiv.id, x, y);
                    subDiv.onclick = area.onclick;
                    subDiv.ondblclick = area.ondblclick;
                    subDiv.title = area.title;
                    subDiv.style.display = '';
                }
                else
                {
                    subDiv.style.display = 'none';
                }
            }
            else
            {
                FlexoReader.getSelectionDiv().style.display = 'none';
            }
            
            this.globalAllowAutoScroll = false;
        }
        else if(allowAutoScroll)
        {
            this.globalAllowAutoScroll = true;
        }
    },
    
    open: function(isBackOrNext)
    {
        if((!FlexoReader.currentContentNode || FlexoReader.currentContentNode.id != this.id) && this.getContent())
        {
            var scrollVal = FlexoReader.getScrollVal(this.getArea());
            
            var processChanged = FlexoReader.currentContentNode && this.getProcessOrOtherNode().id != FlexoReader.currentContentNode.getProcessOrOtherNode().id;
            
            if(processChanged)
            {
                FlexoReader.getSelectionDiv().style.display = 'none';
                
                Effect.Shrink(FlexoReader.currentContentNode.getContent(), { queue: 'front', duration: 0.5, afterFinish: function(){FlexoReader.resizeAccordingToSelectedOption(true);}});
                
                if(!isBackOrNext)
                {
                    FlexoReader.nextArray = [];
                    FlexoReader.backArray.push(FlexoReader.currentContentNode);
                    if(FlexoReader.imgBack)
                    	FlexoReader.imgBack.style.display = '';
                    if(FlexoReader.imgBackNA)
                    	FlexoReader.imgBackNA.style.display = 'none';
                    if(FlexoReader.imgNext)
                    	FlexoReader.imgNext.style.display = 'none';
                    if(FlexoReader.imgNextNA)
                    	FlexoReader.imgNextNA.style.display = '';
                }
            }
            else if(FlexoReader.currentContentNode)
                FlexoReader.currentContentNode.getContent().style.display = 'none';
            
            FlexoReader.currentContentNode = this;
            
            if(FlexoReader.isOptionKeepImageSize())
            {
	            if(!this.isContentImage())
	            {
	            	if(FlexoReader.imgZoomIn)
			       		FlexoReader.imgZoomIn.style.display = 'none';
			       	if(FlexoReader.imgZoomInNA)
			        	FlexoReader.imgZoomInNA.style.display = '';
			        if(FlexoReader.imgZoomOut)
			        	FlexoReader.imgZoomOut.style.display = 'none';
			        if(FlexoReader.imgZoomOutNA)
			        	FlexoReader.imgZoomOutNA.style.display = '';
	            }
	            else
	            {
	            	if(FlexoReader.imgZoomIn)
			       		FlexoReader.imgZoomIn.style.display = '';
			       	if(FlexoReader.imgZoomInNA)
			        	FlexoReader.imgZoomInNA.style.display = 'none';
			        if(FlexoReader.imgZoomOut)
			        	FlexoReader.imgZoomOut.style.display = '';
			        if(FlexoReader.imgZoomOutNA)
			        	FlexoReader.imgZoomOutNA.style.display = 'none';
	            }
			}
			
			if(this.getProcessOrOtherNode().previousNode.getProcessOrOtherNode())
            {
            	if(FlexoReader.imgUp)
                	FlexoReader.imgUp.style.display = '';
                if(FlexoReader.imgUpNA)
                	FlexoReader.imgUpNA.style.display = 'none';
            }
            else
            {
            	if(FlexoReader.imgUp)
               		FlexoReader.imgUp.style.display = 'none';
               	if(FlexoReader.imgUpNA)
               		FlexoReader.imgUpNA.style.display = '';
            }
            
            FlexoReader.oldSize = [this.getImageWidth(), this.getImageHeight()];
            
            if(processChanged)
                Effect.Grow(FlexoReader.currentContentNode.getContent(), { queue: 'end', duration: 0.5, afterFinish: function(){FlexoReader.resizeAccordingToSelectedOption(true); /*Hack Firefox*/ if(!BrowserDetect.isIE()){FlexoReader.divRightColumnContent.style.overflowY = 'hidden'; setTimeout("FlexoReader.divRightColumnContent.style.overflowY = '';", 100);}}});
            else
            {
                FlexoReader.resizeAccordingToSelectedOption(true);
                this.getContent().style.display = '';
            }
            
            FlexoReader.scrollWorkflowImageTo(scrollVal);
                
            if(this.isActivityLevel())
                FlexoReader.divRightColumnContent.onclick = function() {FlexoReader.close();};
            else
                FlexoReader.divRightColumnContent.onclick = null;
            
            return true;
        }
        
        return false;
    }
}

function getUrlParameter(name)
{
	name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
	var regexS = "[\\?&]"+name+"=([^&#]*)";
	var regex = new RegExp( regexS );
	var results = regex.exec( window.location.href );
	if( results == null )
		return null;
	else
		return results[1];
}

Autocompleter.FlexoReader = Class.create();
Autocompleter.FlexoReader.prototype = Object.extend(new Autocompleter.Base(), 
{
	initialize: function(element, update, array, options) 
	{
		this.baseInitialize(element, update, options);
		this.options.array = array;
	},
	
	getUpdatedChoices: function() 
	{
		this.updateChoices(this.options.selector(this));
	},

	setOptions: function(options) 
	{
		this.options = Object.extend(
		{
			choices: 10,
			partialSearch: true,
			partialChars: 2,
			ignoreCase: true,
			fullSearch: false,
			selector: function(instance) 
			{
				var ret       = []; // Beginning matches
				var partial   = []; // Inside matches
				var entry     = instance.getToken();
				var count     = 0;
	
				for (var i = 0; i < instance.options.array.length && ret.length < instance.options.choices ; i++) 
				{ 
					var elem = instance.options.array[i];
					var foundPos = instance.options.ignoreCase ? elem.name.toLowerCase().indexOf(entry.toLowerCase()) : elem.name.indexOf(entry);
	
					var liString = "<li nodeId='" +elem.id+ "'><div><img alt='' src='" +elem.getIconPath()+ "'/></div>";
	
					while (foundPos != -1) 
					{
						if (foundPos == 0 && elem.name.length != entry.length) 
						{ 
							ret.push(liString + "<strong>" + elem.name.substr(0, entry.length) + "</strong>" + 
							elem.name.substr(entry.length) + "</li>");
							break;
						}
						else if (entry.length >= instance.options.partialChars && instance.options.partialSearch && foundPos != -1) 
						{
							if (instance.options.fullSearch || /\s/.test(elem.name.substr(foundPos-1,1))) 
							{
								partial.push(liString + elem.name.substr(0, foundPos) + "<strong>" +
								elem.name.substr(foundPos, entry.length) + "</strong>" + elem.name.substr(foundPos + entry.length) + "</li>");
	                			break;
							}
						}
	
						foundPos = instance.options.ignoreCase ? elem.name.toLowerCase().indexOf(entry.toLowerCase(), foundPos + 1) : elem.name.indexOf(entry, foundPos + 1);
					}
				}
				
				if (partial.length)
					ret = ret.concat(partial.slice(0, instance.options.choices - ret.length))
	
				return "<ul>" + ret.join('') + "</ul>";
			},
			
			onShow: function(element, update)
			{
				if(!update.style.position || update.style.position=='absolute') 
				{
					update.style.position = 'absolute';
					Position.clone(element, update, {setHeight: false, setWidth: false, offsetTop: element.offsetHeight});
				}
				Effect.Appear(update,{duration:0.15});
			},
			
			afterUpdateElement: function(inputElement, liElement)
			{
				inputElement.value = "";
				FlexoReader.select(liElement.getAttribute('nodeId'), true);
				inputElement.blur();
			}
		}, options || {});
	}
});

FlexoReader.initialize();