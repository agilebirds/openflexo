/* ====================================================*/
/* ================ Denali Javascripts ================*/
/*                                                     */
/* ======== Created by Damien T. on 30/04/2004 ========*/
/* ======== Modified by Damien T. on 30/04/2004 =======*/
/* ====================================================*/

// Reloads the window if Nav4 resized
function MM_reloadPage(init) {
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);

// Opens a popup window that can be centered on the screen
function OpenPopupWindow(theURL,winName,features, myWidth, myHeight, isCentered) {
  if(window.screen)if(isCentered)if(isCentered=="true"){
    var myLeft = (screen.width-myWidth)/2;
    var myTop = (screen.height-myHeight)/2;
    features+=(features!='')?',':'';
    features+=',left='+myLeft+',top='+myTop;
  }


window.open(theURL,winName,features+((features!='')?',':'')+'width='+myWidth+',height='+myHeight)

;
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

// Hides or shows a layer
function MM_showHideLayers() { //v6.0
  var i,p,v,obj,args=MM_showHideLayers.arguments;
  for (i=0; i<(args.length-2); i+=3) if ((obj=MM_findObj(args[i]))!=null) { v=args[i+2];
    if (obj.style) { obj=obj.style; v=(v=='show')?'visible':(v=='hide')?'hidden':v; }
    obj.visibility=v; }
}

function getDom(element){
    if((navigator.appName.indexOf("Netscape")>-1&&parseInt(navigator.appVersion)<5)){
    return eval("document."+element);}
    else{return document.getElementById(element).style;}
}

function hideLoadingLayer(){
    MM_showHideLayers('loading','','hide');
}

function showLoadingLayer(){
    MM_showHideLayers('loading','','show');
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src;

x.src=a[i+2];}
}

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function insertAndSelectInDropDown(dropdownuniquename,newItem,fn){
	dropdown=eval('self.opener.document.'+fn+'.'+dropdownuniquename);
	hf=eval('self.opener.document.'+fn+'.HF'+dropdownuniquename);
	l = dropdown.length;
	var elOptNew = self.opener.document.createElement('option');
    elOptNew.text = newItem;
    elOptNew.value = l-2;
    var elOptOld = dropdown.options[l-2];  
    try {
      dropdown.add(elOptNew, elOptOld); // standards compliant; doesn't work in IE
    }
    catch(ex) {
      dropdown.add(elOptNew, dropdown.selectedIndex); // IE only
    }
    dropdown.options[l-1].value=l;
	dropdown.options[l].value=l+1;
	dropdown.selectedIndex=l-2;
	hf.value=newItem;
}

/**
 * Toggle (check or uncheck) all checkboxes belonging to a checkbox group.
 * If togglePerCheckbox is not passed or is false, all checkboxes will be
 * checked or unchecked according to the status of the first checkbox in
 * the group. If togglePerCheckbox is true, then each checkbox will be
 * toggled individually.
 */
function toggleCheckboxGroup(checkboxGroupName, /*optional: */togglePerCheckbox) {
	var allInputs = document.getElementsByTagName('input');
	
	//used if togglePerCheckbox is not set or false
	var statusToSet = false; 
	var initDone = false; 
	
	for (var i=0; i < allInputs.length; i++) {
		var input = allInputs[i];
		if (input.getAttribute('type') == 'checkbox' && input.getAttribute('name') == checkboxGroupName) {
			if (togglePerCheckbox) {
				input.checked = !input.checked;
			}
			else {
				if (!initDone) {
					statusToSet = !input.checked;
					initDone = true;
				}
				input.checked = statusToSet;
			}
		}
	}
}


/**
* Display a panel that inform the user the number of seconds left in his session.
* 
* @param timoutSize the time, in seconds, duration of the session
* @param panelTimer the time, in seconds, of the countdown. 
**/
function enableTimoutPopup(timeoutSize, panelTimer){
	if (!panelTimer) panelTimer = 120;
	waitBeforeShowPanel = (timeoutSize - panelTimer)*1000;
	timeoutPanelTimer = panelTimer;//store in global var
	waitBeforeShowPanelTimer = waitBeforeShowPanel;//store in global var
	stopTimeoutCountdown = false;
	sessionHasExpired = false;
	if (waitBeforeShowPanel>0) setTimeout("showTimeoutPopup("+panelTimer+");", waitBeforeShowPanel);
}

function showTimeoutPopup(timer){
	var b = document.getElementsByTagName("body")[0];
	var div = document.createElement("div");
	div.setAttribute("id",'timeoutPopup');
	div.setAttribute("class",'timeoutPopup');
	div.innerHTML = '<DIV CLASS=timeoutPopupContent><span name=timeLeft id=timeLeft>Your session expires in ? seconds.</span><br><br>If you submit your form after it has expired, all form content will be lost.<br><br><a href="#" onclick="resetTimeoutPopup();">Refresh your session</a><br><a href="#" onclick="getStyleObject(\'timeoutPopup\').display = \'none\';">Close</a></div>';
	b.appendChild(div);
	var d = getStyleObject('timeoutPopup');
	d.display = 'block';
	var y = (getWindowSize()[1]/2)-50 + getScrollXY()[1]; 
	d.top=y+'px';
	decreaseTimeLeft(timer);
}

function resetTimeoutPopup(){
	if (sessionHasExpired) {
		alert('Your session cannot be refreshed since it has expired.');
		return false;
	}
	if (refreshSession()){
		stopTimeoutCountdown = true;
        alert("Your session has been refreshed, the countdown is reset.");
		var d = getStyleObject('timeoutPopup');
		d.display = 'none';
		if (waitBeforeShowPanel>0) setTimeout("showTimeoutPopup("+timeoutPanelTimer+");", waitBeforeShowPanelTimer);
		return true;
	} else {
		alert("Unable to restore your session.");
		return false;
	}
}

function decreaseTimeLeft(timer){
	var d = document.getElementById('timeLeft');
	timer--;
	if (timer==0) {
		sessionHasExpired = true;
		d.innerHTML = '<font color="#aa0000"><b>Your session has expired.</b></font>';
		return;
	}
	d.innerHTML = "Your session expires in "+timer+" seconds.";
	
	if (!stopTimeoutCountdown) setTimeout("decreaseTimeLeft("+timer+");", 1000);
	else stopTimeoutCountdown = false;
}

//====================================================================================
//       AJAX
//====================================================================================
function makeUrlRelative(absoluteUrl){
    docUrl = document.URL.substring(0,document.URL.lastIndexOf('/')+1);

    k=0;
   
    n=0;
    cst=k;
   
    while(k<docUrl.length){
        if(docUrl.charAt(k)=='/'){n++;}
        k++;
    }
    sub = absoluteUrl.substring(absoluteUrl.substring(0,cst).lastIndexOf('/')+1);
    answer = '';
    while(n>0){
        answer = answer+'../';
        n--;
    }
    return answer+sub;
}
function getSessionIdFromCookie() {
    if (document.cookie == "") return null;
    var cookies = document.cookie.split(";");
    for(var i=0;i<cookies.length;i++){
        var pair = cookies[i].split("=");
        if (pair[0].match(/JSESSIONID/)) return unescape(pair[1]);
    }
    return null;
}

function fillDivInnerHTMLWithUrl(id,url){
    var http = null;
      if(window.XMLHttpRequest)  http = new XMLHttpRequest();
      else if (window.ActiveXObject)  http = new ActiveXObject('Microsoft.XMLHTTP');
    if (typeof netscape != 'undefined' && typeof netscape.security !='undefined'){
	    try	{
			netscape.security.PrivilegeManager.enablePrivilege('UniversalBrowserRead');
		} catch(e) {
		}
    }
    var jsess = getSessionIdFromCookie();
    if(jsess!=null){
        var start = url.substring(0,url.indexOf('?'));
        var end=url.substring(url.indexOf('?'),url.length);
        url = start+';jsessionid='+jsess+end;
    }
    
    //make it obvious that something is happening!
    document.getElementById(id).innerHTML = '<i>Loading...</i>';
    
    try{
        http.open( "GET", makeUrlRelative(url) , true);
    }catch(e1){
    }
    http.onreadystatechange = function() {
	    if (typeof netscape != 'undefined' && typeof netscape.security !='undefined') {
            try {
            	netscape.security.PrivilegeManager.enablePrivilege('UniversalBrowserRead');
            } catch(e) {
            }
        }
        if(http.readyState == 4){
        	if(http.status == 200) {
                document.getElementById(id).innerHTML = http.responseText;
            } else {
                document.getElementById(id).innerHTML = '<B>Server error</B>';
            }   
        };
    }
    http.send("");
}

function deleteFileAndRefreshFileList(base_url,wosid,fileID){
	fillDivInnerHTMLWithUrl("FileList",base_url+'FFDA/deleteFile?wosid='+wosid+'&fileID='+fileID);
}

function refreshSession(){
	var url = "http://"+window.location.host+window.location.pathname;
	url = url.substring(0,url.lastIndexOf("/wa/")+4)+'WDLDirectAction/empty';
	var http = null;
	if(window.XMLHttpRequest)  http = new XMLHttpRequest();
    else if (window.ActiveXObject)  http = new ActiveXObject('Microsoft.XMLHTTP');
    if (typeof netscape != 'undefined' && typeof netscape.security !='undefined'){
	    try	{
			netscape.security.PrivilegeManager.enablePrivilege('UniversalBrowserRead');
		} catch(e) {
		}
    }
    var jsess = getSessionIdFromCookie();
    if(jsess!=null){
        var start = url.substring(0,url.indexOf('?'));
        var end=url.substring(url.indexOf('?'),url.length);
        url = start+';jsessionid='+jsess+end;
    }
    try{
        http.open( "GET", url , true);
        http.send("");
        return true;

    }catch(e1){
    	return false;
    }
}

//====================================================================================
//       LAYERS
//====================================================================================

function getObj(name){
    if (document.getElementById){
        this.obj = document.getElementById(name);
        this.style = document.getElementById(name).style;
    } else if (document.all) {
        this.obj = document.all[name];
        this.style = document.all[name].style;
    } else if (document.layers) {
        this.obj = document.layers[name];
        this.style = document.layers[name];
    }
}
function getStyleObject(objectId) {
    // cross-browser function to get an object's style object given its id
    if(document.getElementById && document.getElementById(objectId)) {
        // W3C DOM
        return document.getElementById(objectId).style;
    } else if (document.all && document.all(objectId)) {
        // MSIE 4 DOM
        return document.all(objectId).style;
    } else if (document.layers && document.layers[objectId]) {
        // NN 4 DOM.. note: this won't find nested layers
        return document.layers[objectId];
    } else {
        return false;
    }
} // getStyleObject

function changeObjectVisibility(objectId, isVisible) {
    // get a reference to the cross-browser style object and make sure the object exists
    var styleObject = getStyleObject(objectId);
    if(styleObject) {
        if (styleObject.visibility) {
            if (isVisible==true) var visibility = 'visible';
            else if (isVisible==false) var visibility = 'hidden';
            else var visibility = isVisible;//alows to specify something than visible or hidden
            styleObject.visibility = visibility;
        }
        if (styleObject.display) {
            if (isVisible==true) var display = 'block';
            else if (isVisible==false) var display = 'none';
            else var display = isVisible;
            styleObject.display = display;
        }
        return true;
    } else {
        // we couldn't find the object, so we can't change its visibility
        return false;
    }
} // changeObjectVisibility

function toggleObjectVisibility(objectId, valueWhenVisible) {
    // get a reference to the cross-browser style object and make sure the object exists
    var styleObject = getStyleObject(objectId);
        if(styleObject) {
            if (styleObject.visibility) styleObject.visibility =

(styleObject.visibility=='hidden') ? 'visible' : 'hidden';
            else if (styleObject.display) {
                var value = (valueWhenVisible)?valueWhenVisible:'block';
                styleObject.display = (styleObject.display =='none') ? value : 'none';
            }
            return true;
        } else {
        // we couldn't find the object, so we can't change its visibility
            return false;
    }
} // changeObjectVisibility


function makeVisible(objectId) {
    // get a reference to the cross-browser style object and make sure the object exists
    var styleObject = getStyleObject(objectId);
        if(styleObject) {
            if (styleObject.visibility) styleObject.visibility = 'visible';
            else if (styleObject.display) {
                styleObject.display = 'block';
            }
            return true;
        } else {
        // we couldn't find the object, so we can't change its visibility
            return false;
    }
} // changeObjectVisibility
function makeInvisible(objectId) {
    // get a reference to the cross-browser style object and make sure the object exists
    var styleObject = getStyleObject(objectId);
        if(styleObject) {
            if (styleObject.visibility) styleObject.visibility = 'hidden';
            else if (styleObject.display) {
                styleObject.display = 'none';
            }
            return true;
        } else {
        // we couldn't find the object, so we can't change its visibility
            return false;
    }
} // changeObjectVisibility


function DL_showPopup(targetObjectId, eventObj, destinationObjectID, xOffset, yOffset){
    
    var newXCoordinate;
    var newYCoordinate;
    
    // hide any currently-visible popups
    hideCurrentPopup(targetObjectId);
    
    if(eventObj) {
        // stop event from bubbling up any farther
        eventObj.cancelBubble = true;
        // move popup div to current cursor position
        // (add scrollTop to account for scrolling for IE)
    }
    
    if(destinationObjectID)
    {
    	var destObj = MM_findObj(destinationObjectID);
        var x = findPosX(destObj);
        var y = findPosY(destObj);
        var newXCoordinate = x + xOffset;
        var newYCoordinate = y + yOffset;
    }
    else if(eventObj)
    {
    	newXCoordinate = (eventObj.pageX)?eventObj.pageX + xOffset:
        	eventObj.x + xOffset + getScrollXY()[0];
        
        newYCoordinate = (eventObj.pageY)?eventObj.pageY + yOffset:
        	eventObj.y + yOffset + getScrollXY()[1];
    }
    else if(xOffset!=null && yOffset!=null)
    {
		newXCoordinate = xOffset;
		newYCoordinate = yOffset;
    }
    
    //Move object at invisible place to be able to determine its size
	moveObject(targetObjectId, -1000, -10000);
    
    // and make it visible
    //need to change overflow property to hidden to get its scroll size, old property is set back at the end of function
    var targetObject = MM_findObj(targetObjectId);
    var oldOverflow = targetObject.style.overflow;
    targetObject.style.overflow = 'hidden';
    if( changeObjectVisibility(targetObjectId, true) ) {
        // if we successfully showed the popup
        // store its Id on a globally-accessible object
        var fatherPopup = addToCurrentlyVisiblePopup(targetObjectId);
        
        //if popup to make visible is included inside another popup, its position is relative to the popup and not to the window
        if(fatherPopup)
        {
        	newXCoordinate = newXCoordinate - findPosX(fatherPopup);
			newYCoordinate = newYCoordinate - findPosY(fatherPopup);
        }
        
        var windowSize = getWindowSize();
	    var targetWidth = parseInt(targetObject.offsetWidth);
	    if(!targetWidth)
	    	targetWidth = 200;
	    var targetHeight = parseInt(targetObject.offsetHeight);
	    if(!targetHeight)
	    	targetHeight = 200;
	    	
	    //Resize object width to be able to display everything inside
	    if(targetObject.scrollWidth >= targetWidth)
	    {
	    	if(targetObject.scrollWidth > windowSize[0]-30)
	    	{
	    		targetObject.style.width = (windowSize[0]-30)+'px';
	    		targetWidth = parseInt(targetObject.offsetWidth);
	    	}
	    	else
	    	{
	    		targetObject.style.width = targetObject.scrollWidth+'px';
	    		targetWidth = parseInt(targetObject.offsetWidth);
	    	}
	    }
	    
	    if(newXCoordinate!=null && newYCoordinate!=null)
	    {
		    if(newXCoordinate+targetWidth+30 > windowSize[0]+getScrollXY()[0])
		    {
		    	newXCoordinate = windowSize[0] - targetWidth - 30 + getScrollXY()[0];
		    }
		    
		    if(newYCoordinate+targetHeight > windowSize[1]+getScrollXY()[1])
		    {
		    	newYCoordinate = windowSize[1] - targetHeight + getScrollXY()[1] ;
		    }
		}
		else
		{
			newXCoordinate = (windowSize[0]-targetWidth)/2;
			newYCoordinate = (windowSize[1]-targetHeight)/2;
		}
		
		if(newXCoordinate<0)
			newXCoordinate = 0;
		if(newYCoordinate<0)
			newYCoordinate = 0;
		
		//IE6 hack to make all dropdown below this object invisible
		//The objective is to display an Iframe of the same size below this object
		if(isIE6())
		{
			var iFrameElement = document.getElementById(targetObjectId+"_iframe");
			
			if(iFrameElement)
			{
				iFrameElement.style.width = targetWidth+"px";
				iFrameElement.style.height = targetHeight+"px";
				iFrameElement.style.left = newXCoordinate + "px";
				iFrameElement.style.top = newYCoordinate + "px";
				iFrameElement.style.display = "";
			}
		}
		
		moveObject(targetObjectId, newXCoordinate, newYCoordinate);
        
        targetObject.style.overflow = oldOverflow;
        return true;
    } else {
        // we couldn't show the popup, boo hoo!
        alert('Unable to find object '+targetObjectId);
        
        targetObject.style.overflow = oldOverflow;
        return false;
    }
}

function hideCurrentPopup(clickedPopupId) {
    // note: we've stored all the currently-visible popups on the global object
	// window.currentlyVisiblePopup
	// If clickedPopupId is set, it hides all the currently-visible popups if not an ancestor of the clicked popup
	
    if(window.currentlyVisiblePopup) {
    
    	for(var i=0; i<window.currentlyVisiblePopup.length && window.currentlyVisiblePopup[i]!=clickedPopupId; i++);
		var isClickedPopupIdInArray = i < window.currentlyVisiblePopup.length;
    
    	while(window.currentlyVisiblePopup.length>0)
    	{
    		var visiblePopup = document.getElementById(window.currentlyVisiblePopup.pop());
    		var clickedPopup;
    		if(clickedPopupId)
    			clickedPopup = document.getElementById(clickedPopupId);
    			
    		if(clickedPopup)
    		{
    			if(visiblePopup.id == clickedPopup.id || clickedPopup.descendantOf(visiblePopup) || (!isClickedPopupIdInArray && visiblePopup.descendantOf(clickedPopup)))
    			{
    				window.currentlyVisiblePopup.push(visiblePopup.id);
    				break;
    			}
    		}
    		//Hide popup
    		changeObjectVisibility(visiblePopup.id, false);
    		
    		if(isIE6())
	        {
	    		var iframe = document.getElementById(visiblePopup.id+'_iframe');
	    		if(iframe!=null)
	    			iframe.style.display = 'none';
	        }
    	}
    }
} // hideCurrentPopup

//Return the father popup if any
function addToCurrentlyVisiblePopup(popupId)
{
	if(!window.currentlyVisiblePopup)
		window.currentlyVisiblePopup = new Array();
	
	var father;
	while(window.currentlyVisiblePopup.length>0)
	{
		var visiblePopup = document.getElementById(window.currentlyVisiblePopup.pop());
		var popup = document.getElementById(popupId);
		
		if(visiblePopup.descendantOf(popup))
		{
			if(window.currentlyVisiblePopup.length>0)
				father = window.currentlyVisiblePopup[window.currentlyVisiblePopup.length-1]; 
			window.currentlyVisiblePopup.push(popup.id);
			window.currentlyVisiblePopup.push(visiblePopup.id);
			break;
		}
		if(popup.descendantOf(visiblePopup))
		{
			window.currentlyVisiblePopup.push(visiblePopup.id);
			window.currentlyVisiblePopup.push(popup.id);
			father = visiblePopup;
			break;
		}
	}
	
	if(window.currentlyVisiblePopup.length==0)
		window.currentlyVisiblePopup.push(popupId);
		
	return father;
}

function getScrollXY() {
  var scrOfX = 0, scrOfY = 0;
  if( typeof( window.pageYOffset ) == 'number' ) {
    //Netscape compliant
    scrOfY = window.pageYOffset;
    scrOfX = window.pageXOffset;
  } else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) {
    //DOM compliant
    scrOfY = document.body.scrollTop;
    scrOfX = document.body.scrollLeft;
  } else if( document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) {
    //IE6 standards compliant mode
    scrOfY = document.documentElement.scrollTop;
    scrOfX = document.documentElement.scrollLeft;
  }
  return [ scrOfX, scrOfY ];
}

function getWindowSize() {
  var myWidth = 0, myHeight = 0;
  if( typeof( window.innerWidth ) == 'number' ) {
    //Non-IE
    myWidth = window.innerWidth;
    myHeight = window.innerHeight;
  } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
    //IE 6+ in 'standards compliant mode'
    myWidth = document.documentElement.clientWidth;
    myHeight = document.documentElement.clientHeight;
  } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
    //IE 4 compatible
    myWidth = document.body.clientWidth;
    myHeight = document.body.clientHeight;
  }
  return [ myWidth, myHeight];
}

//====================================================================================
//       MOVING / POSITION of elements
//====================================================================================

function moveObject(objectId, newXCoordinate, newYCoordinate) {
    // get a reference to the cross-browser style object and make sure the object exists
    var styleObject = getStyleObject(objectId);
    if(styleObject) {
        styleObject.left = newXCoordinate + "px";
        styleObject.top = newYCoordinate + "px";
        return true;
    } else {
        // we couldn't find the object, so we can't very well move it
        return false;
    }
} // moveObject

function findPosX(obj){
        var curleft = 0;
        if (obj.offsetParent) {
                while (obj.offsetParent){
                        curleft += obj.offsetLeft
                        obj = obj.offsetParent;
                }
        }
        else if (obj.x) curleft += obj.x;
        return curleft;
}

function findPosY(obj){
        var curtop = 0;
        if (obj.offsetParent) {
                while (obj.offsetParent){
                        curtop += obj.offsetTop
                        obj = obj.offsetParent;
                }
        }
        else if (obj.y) curtop += obj.y;
        return curtop;
}

function appendOptionInSelect(optionText,optionValue,selectID)
{
  var elOptNew = document.createElement('option');
  elOptNew.text = optionText;
  elOptNew.value = optionValue;
  var elSel = document.getElementById(selectID);


  try {
    elSel.add(elOptNew, null); // standards compliant; doesn't work in IE
  }
  catch(ex) {
    elSel.add(elOptNew); // IE only
  }
}

function removeSelectedOptionsFromSelect(selectID)
{
  var elSel = document.getElementById(selectID);
  var i;
  for (i = elSel.length - 1; i>=0; i--) {
    if (elSel.options[i].selected) {
      elSel.remove(i);
    }
  }
}

//Function binded to onKeyPress, it allows only numbers for a textField
function onlyNumbers(e, obj, decimalSeparator, groupingSeparator, allowingNegative)
{
	var arrSpecialChars = new Array(8, 9, 13, 35, 36, 37, 38, 39, 40, 46, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123);
	
	if(window.event) // IE
	{
		keynum = e.keyCode
		if (keynum  == 13)//enter
        	return true;

	}	
	else // Netscape/Firefox/Opera
	{
		if (InArray(arrSpecialChars, e.keyCode) >= 0)
	        return true;
		keynum = e.which
	}

	var strChar = String.fromCharCode(keynum);
	var finalStr = obj.value.substring(0, getSelectionStart(obj))+strChar+obj.value.substring(getSelectionEnd(obj));

	var arrValidChars=new Array();
	if(decimalSeparator)
	{
		arrValidChars.push(decimalSeparator);
		finalStr = finalStr.replace(decimalSeparator, ".");
	}
	if(allowingNegative)
		arrValidChars.push("-");
	if(groupingSeparator)
	{
		//arrValidChars.push(groupingSeparator);
		while(finalStr.indexOf(groupingSeparator)!=-1)
			finalStr = finalStr.replace(groupingSeparator, "");
	}

    return ((keynum>=48 && keynum<=57) ||InArray(arrValidChars, strChar)>=0) && (isFinite(finalStr) || finalStr=="-");
}

//Return the index of the selection start of the textfield obj
function getSelectionStart(obj)
{
	if ( typeof obj.selectionStart != 'undefined' )
		return obj.selectionStart;
	
	// IE Support
	obj.focus();
	var range = obj.createTextRange();
	range.moveToBookmark(document.selection.createRange().getBookmark());
	range.moveEnd('character', obj.value.length);
	return obj.value.length - range.text.length;
}

//Return the index of the selection end of the textfield obj
function getSelectionEnd(obj)
{
	if ( typeof obj.selectionEnd != 'undefined' )
		return obj.selectionEnd;

	// IE Support
	obj.focus();
	var range = obj.createTextRange();
	range.moveToBookmark(document.selection.createRange().getBookmark());
	range.moveStart('character', - obj.value.length);
	return range.text.length;
}

//Return true if key is in arr
function InArray(arr, key) {
    for (var i=0; i<arr.length; i++) {
        if (arr[i] == key)
            return i;
    }
    return -1;
}

//Alternate background color of list rows
//set isOldCss to TRUE if using the legacy templates (ContentoMasterStyle.css @ 2007)
function setAlternateClassNames(isOldCss, trClassName){
	if(!trClassName)
	{
		trClassName = "denaliRow";
	} 

	var trs = document.getElementsByTagName('TR');
	for (var i=0;i<trs.length;i++) {
	    var tr = trs[i];

		if (isOldCss){
		    if (tr.getAttribute('name')=="TR_LIST") {
		        if (tr.rowIndex%2==0) {
		            tr.className='DLListRow1';
		        } else {
		            tr.className='DLListRow2';
		        }
		    }
	    }
	    else {
			if (tr.className==trClassName) {
		        if (tr.rowIndex%2==1) {
		            tr.className = trClassName+'Even';
		        } else {
		            tr.className= trClassName+'Odd';
		        }
		        
		        var tds = tr.getElementsByTagName('TD');
		        if(tds.length>0 && tds[0].className.indexOf('List')!=-1)
		        {
		        	tr.onmouseover = function() {this.className = this.className+'Hover';};
	        		tr.onmouseout = function() {if(this.className.indexOf("Hover")!=-1)this.className = this.className.substring(0, this.className.indexOf("Hover"));};
	        	}
		    }
	    }
	}
}

/* Browser detection
 * Usage : 
 * BrowserDetect.browser -> return the browser identity (ex. Explorer)
 * BrowserDetect.version -> return the version number
 * 2 usefull function have been added: isIE() and isIE6(), you are free to complete those function to match your desire.
 */
var BrowserDetect = {
	init: function () {
		this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
		this.version = this.searchVersion(navigator.userAgent)
			|| this.searchVersion(navigator.appVersion)
			|| "an unknown version";
		this.OS = this.searchString(this.dataOS) || "an unknown OS";
	},
	searchString: function (data) {
		for (var i=0;i<data.length;i++)	{
			var dataString = data[i].string;
			var dataProp = data[i].prop;
			this.versionSearchString = data[i].versionSearch || data[i].identity;
			if (dataString) {
				if (dataString.indexOf(data[i].subString) != -1)
					return data[i].identity;
			}
			else if (dataProp)
				return data[i].identity;
		}
	},
	searchVersion: function (dataString) {
		var index = dataString.indexOf(this.versionSearchString);
		if (index == -1) return;
		return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
	},
	isIE: function () {
		return this.browser == "Explorer";
	},
	isIE6: function () {
		return this.isIE() && this.version == 6;
	},
	dataBrowser: [
		{ 	string: navigator.userAgent,
			subString: "OmniWeb",
			versionSearch: "OmniWeb/",
			identity: "OmniWeb"
		},
		{
			string: navigator.vendor,
			subString: "Apple",
			identity: "Safari"
		},
		{
			prop: window.opera,
			identity: "Opera"
		},
		{
			string: navigator.vendor,
			subString: "iCab",
			identity: "iCab"
		},
		{
			string: navigator.vendor,
			subString: "KDE",
			identity: "Konqueror"
		},
		{
			string: navigator.userAgent,
			subString: "Firefox",
			identity: "Firefox"
		},
		{
			string: navigator.vendor,
			subString: "Camino",
			identity: "Camino"
		},
		{		// for newer Netscapes (6+)
			string: navigator.userAgent,
			subString: "Netscape",
			identity: "Netscape"
		},
		{
			string: navigator.userAgent,
			subString: "MSIE",
			identity: "Explorer",
			versionSearch: "MSIE"
		},
		{
			string: navigator.userAgent,
			subString: "Gecko",
			identity: "Mozilla",
			versionSearch: "rv"
		},
		{ 		// for older Netscapes (4-)
			string: navigator.userAgent,
			subString: "Mozilla",
			identity: "Netscape",
			versionSearch: "Mozilla"
		}
	],
	dataOS : [
		{
			string: navigator.platform,
			subString: "Win",
			identity: "Windows"
		},
		{
			string: navigator.platform,
			subString: "Mac",
			identity: "Mac"
		},
		{
			string: navigator.platform,
			subString: "Linux",
			identity: "Linux"
		}
	]

};
BrowserDetect.init();

function isIE6()
{
	return BrowserDetect.isIE6();
}

//TODO Implement those methods to allow menu @ submenu implementation
function show_menu(menu,submenu) {
}

 function hide_menu(menu,submenu) {
}

function hide_all_submenu() {
}

//Initialize the fsmenu in parameters with arguments, any null argument will be set to default value (see doc inside method for more informations).
//Only the first two arguments are mandatory
function init_FS_Menu(fsmenu, ulOuterMostId, hideDelay, showDelay, animOutSpeed, animInSpeed, showOnClick, hideOnClick, animationFunctions, arrowNode)
{
	// Here's some optional settings for delays and highlighting:
	//  * showDelay is the time (in milliseconds) to display a new child menu.
	//    Remember that 1000 milliseconds = 1 second.
	//  * switchDelay is the time to switch from one child menu to another child menu.
	//    Set this higher and point at 2 neighbouring items to see what it does.
	//  * hideDelay is the time it takes for a menu to hide after mouseout.
	//    Set this to a negative number to disable hiding entirely.
	//  * cssLitClass is the CSS classname applied to parent items of active menus.
	//  * showOnClick will, suprisingly, set the menus to show on click. Pick one of 3 values:
	//    0 = all mouseover, 1 = first level click, sublevels mouseover, 2 = all click.
	//  * hideOnClick hides all visible menus when one is clicked (defaults to true).
	//  * animInSpeed and animOutSpeed set the animation speed. Set to a number
	//    between 0 and 1 where higher = faster. Setting both to 1 disables animation.
	
	
	if(showDelay!=null)
		fsmenu.showDelay = showDelay;
	//fsmenu.switchDelay = 125;
	if(hideDelay==null)
		hideDelay = 250;
	fsmenu.hideDelay = hideDelay;
	//fsmenu.cssLitClass = 'highlighted';
	if(showOnClick!=null)
		fsmenu.showOnClick = showOnClick;
	if(hideOnClick!=null)
		fsmenu.hideOnClick = hideOnClick;
	if(animInSpeed!=null)
		fsmenu.animInSpeed = animInSpeed;
	if(animOutSpeed==null)
		animOutSpeed = 0.4;
	fsmenu.animOutSpeed = animOutSpeed;
	
	// Now the fun part... animation! This script supports animation plugins you
	// can add to each menu object you create. I have provided 3 to get you started.
	// To enable animation, add one or more functions to the menuObject.animations
	// array; available animations are:
	//  * FSMenu.animSwipeDown is a "swipe" animation that sweeps the menu down.
	//  * FSMenu.animFade is an alpha fading animation using tranparency.
	//  * FSMenu.animClipDown is a "blind" animation similar to 'Swipe'.
	// They are listed inside the "fsmenu.js" file for you to modify and extend :).
	
	if(animationFunctions!=null && animationFunctions.length>0)
	{
		for(var i=0; i<animationFunctions.length; i++)
			fsmenu.animations[fsmenu.animations.length] = animationFunctions[i];
	}
	else
	{
		// I'm applying two at once to listMenu. Delete this to disable!
		fsmenu.animations[fsmenu.animations.length] = FSMenu.animFade;
		//fsmenu.animations[fsmenu.animations.length] = FSMenu.animSwipeDown;
		fsmenu.animations[fsmenu.animations.length] = FSMenu.animClipDown;
	}
	
	// Finally, on page load you have to activate the menu by calling its 'activateMenu()' method.
	// I've provided an "addEvent" method that lets you easily run page events across browsers.
	// You pass the activateMenu() function two parameters:
	//  (1) The ID of the outermost <ul> list tag containing your menu data.
	//  (2) A node containing your submenu popout arrow indicator.
	// If none of that made sense, just cut and paste this next bit for each menu you create.
	
	if (arrowNode==null && document.createElement && document.documentElement)
	{
		arrowNode = document.createElement('span');
	 	arrowNode.appendChild(document.createTextNode('>'));
	 	// Feel free to replace the above two lines with these for a small arrow image...
	 	//arrowNode = document.createElement('img');
		//arrowNode.src = 'arrow.gif';
	 	//arrowNode.style.borderWidth = '0';
	 	arrowNode.className = 'subind';
	}
	//addEvent(window, 'load', new Function('<WEBOBJECT NAME="String3"></WEBOBJECT>.activateMenu("<WEBOBJECT NAME="String2"></WEBOBJECT>", arrow)'));
	if(ulOuterMostId==null)
		ulOuterMostId = 'listMenuRoot';
	fsmenu.activateMenu(ulOuterMostId, arrowNode);
	
	if(isIE6())
	{
		addEvent(fsmenu, 'show', function(mN) { this.ieSelBoxFixShow(mN) }, 1);
		addEvent(fsmenu, 'hide', function(mN) { this.ieSelBoxFixHide(mN) }, 1);
	}
	
	// You may wish to leave your menu as a visible list initially, then apply its style
	// dynamically on activation for better accessibility. Screenreaders and older browsers will
	// then see all your menu data, but there will be a 'flicker' of the raw list before the
	// page has completely loaded. If you want to do this, remove the CLASS="..." attribute from
	// the above outermost UL tag, and uncomment this line:
	//addEvent(window, 'load', new Function('getRef("listMenuRoot").className="menulist"'));
	
	// To create more menus, duplicate this section and make sure you rename your
	// menu object to something different; also, activate another <ul> list with a
	// different ID, of course :). You can hae as many menus as you want on a page.
}


//====================================================================================
//        THE FOLLOWING METHODS HAVE NOT BEEN VERIFIED AND MAY BE DEPRECATED
//====================================================================================

/*
FREESTYLE MENUS v1.0 RC (c) 2001-2006 Angus Turnbull, http://www.twinhelix.com
Altering this notice or redistributing this file is prohibited.

*/

var isDOM=document.getElementById?1:0,isIE=document.all?1:0,isNS4=navigator.appName=='Netscape'&&!isDOM?1:0,isOp=self.opera?1:0,isDyn=isDOM||isIE||isNS4;
function getRef(i,p){p=!p?document:p.navigator?p.document:p;return isIE?p.all[i]:isDOM?(p.getElementById?p:p.ownerDocument).getElementById(i):isNS4?p.layers[i]:null};
function getSty(i,p){var r=getRef(i,p);return r?isNS4?r:r.style:null};
if(!self.LayerObj)var LayerObj=new Function('i','p','this.ref=getRef(i,p);this.sty=getSty(i,p);return this');
function getLyr(i,p){return new LayerObj(i,p)};
function LyrFn(n,f){LayerObj.prototype[n]=new Function('var a=arguments,p=a[0],px=isNS4||isOp?0:"px";with(this){'+f+'}')};
LyrFn('x','if(!isNaN(p))sty.left=p+px;else return parseInt(sty.left)');
LyrFn('y','if(!isNaN(p))sty.top=p+px;else return parseInt(sty.top)');
if(typeof addEvent!='function'){var addEvent=function(o,t,f,l){var d='addEventListener',n='on'+t,rO=o,rT=t,rF=f,rL=l;if(o[d]&&!l)return o[d](t,f,false);if(!o._evts)o._evts={};if(!o._evts[t]){o._evts[t]=o[n]?{b:o[n]}:{};o[n]=new Function('e','var r=true,o=this,a=o._evts["'+t+'"],i;for(i in a){o._f=a[i];r=o._f(e||window.event)!=false&&r;o._f=null}return r');if(t!='unload')addEvent(window,'unload',function(){removeEvent(rO,rT,rF,rL)})}if(!f._i)f._i=addEvent._i++;o._evts[t][f._i]=f};addEvent._i=1;var removeEvent=function(o,t,f,l){var d='removeEventListener';if(o[d]&&!l)return o[d](t,f,false);if(o._evts&&o._evts[t]&&f._i)delete o._evts[t][f._i]}}function FSMenu(myName,nested,cssProp,cssVis,cssHid){this.myName=myName;this.nested=nested;this.cssProp=cssProp;this.cssVis=cssVis;this.cssHid=cssHid;this.cssLitClass='highlighted';this.menus={root:new FSMenuNode('root',true,this)};this.menuToShow=[];this.mtsTimer=null;this.showDelay=0;this.switchDelay=125;this.hideDelay=500;this.showOnClick=0;this.hideOnClick=true;this.animInSpeed=0.2;this.animOutSpeed=0.2;this.animations=[]};FSMenu.prototype.show=function(mN){with(this){menuToShow.length=arguments.length;for(var i=0;i<arguments.length;i++)menuToShow[i]=arguments[i];clearTimeout(mtsTimer);if(!nested)mtsTimer=setTimeout(myName+'.menus.root.over()',10)}};FSMenu.prototype.hide=function(mN){with(this){clearTimeout(mtsTimer);if(menus[mN])menus[mN].out()}};FSMenu.prototype.hideAll=function(){with(this){for(var m in menus)if(menus[m].visible&&!menus[m].isRoot)menus[m].hide(true)}};function FSMenuNode(id,isRoot,obj){this.id=id;this.isRoot=isRoot;this.obj=obj;this.lyr=this.child=this.par=this.timer=this.visible=null;this.args=[];var node=this;this.over=function(evt){with(node)with(obj){if(isNS4&&evt&&lyr.ref)lyr.ref.routeEvent(evt);clearTimeout(timer);clearTimeout(mtsTimer);if(!isRoot&&!visible)node.show();if(menuToShow.length){var a=menuToShow,m=a[0];if(!menus[m]||!menus[m].lyr.ref)menus[m]=new FSMenuNode(m,false,obj);var c=menus[m];if(c==node){menuToShow.length=0;return}clearTimeout(c.timer);if(c!=child&&c.lyr.ref){c.args.length=a.length;for(var i=0;i<a.length;i++)c.args[i]=a[i];var delay=child?switchDelay:showDelay;c.timer=setTimeout('with('+myName+'){menus["'+c.id+'"].par=menus["'+node.id+'"];menus["'+c.id+'"].show()}',delay?delay:1)}menuToShow.length=0}if(!nested&&par)par.over()}};this.out=function(evt){with(node)with(obj){if(isNS4&&evt&&lyr&&lyr.ref)lyr.ref.routeEvent(evt);clearTimeout(timer);if(!isRoot&&hideDelay>=0){timer=setTimeout(myName+'.menus["'+id+'"].hide()',hideDelay);if(!nested&&par)par.out()}}};if(this.id!='root')with(this)with(lyr=getLyr(id))if(ref){if(isNS4)ref.captureEvents(Event.MOUSEOVER|Event.MOUSEOUT);addEvent(ref,'mouseover',this.over);addEvent(ref,'mouseout',this.out);if(obj.nested){addEvent(ref,'focus',this.over);addEvent(ref,'click',this.over);addEvent(ref,'blur',this.out)}}};FSMenuNode.prototype.show=function(forced){with(this)with(obj){if(!lyr||!lyr.ref)return;if(par){if(par.child&&par.child!=this)par.child.hide();par.child=this}var offR=args[1],offX=args[2],offY=args[3],lX=0,lY=0,doX=''+offX!='undefined',doY=''+offY!='undefined';if(self.page&&offR&&(doX||doY)){with(page.elmPos(offR,par.lyr?par.lyr.ref:0))lX=x,lY=y;if(doX)lyr.x(lX+eval(offX));if(doY)lyr.y(lY+eval(offY))}if(offR)lightParent(offR,1);visible=1;if(obj.onshow)obj.onshow(id);lyr.ref.parentNode.style.zIndex='2';setVis(1,forced)}};FSMenuNode.prototype.hide=function(forced){with(this)with(obj){if(!lyr||!lyr.ref||!visible)return;if(isNS4&&self.isMouseIn&&isMouseIn(lyr.ref))return show();if(args[1])lightParent(args[1],0);if(child)child.hide();if(par&&par.child==this)par.child=null;if(lyr){visible=0;if(obj.onhide)obj.onhide(id);lyr.ref.parentNode.style.zIndex='1';setVis(0,forced)}}};FSMenuNode.prototype.lightParent=function(elm,lit){with(this)with(obj){if(!cssLitClass||isNS4)return;if(lit)elm.className+=(elm.className?' ':'')+cssLitClass;else elm.className=elm.className.replace(new RegExp('(\\s*'+cssLitClass+')+$'),'')}};FSMenuNode.prototype.setVis=function(sh,forced){with(this)with(obj){if(lyr.forced&&!forced)return;lyr.forced=forced;lyr.timer=lyr.timer||0;lyr.counter=lyr.counter||0;with(lyr){clearTimeout(timer);if(sh&&!counter)sty[cssProp]=cssVis;var speed=sh?animInSpeed:animOutSpeed;if(isDOM&&speed<1)for(var a=0;a<animations.length;a++)animations[a](ref,counter,sh);counter+=speed*(sh?1:-1);if(counter>1){counter=1;lyr.forced=false}else if(counter<0){counter=0;sty[cssProp]=cssHid;lyr.forced=false}else if(isDOM){timer=setTimeout(myName+'.menus["'+id+'"].setVis('+sh+','+forced+')',50)}}}};FSMenu.animSwipeDown=function(ref,counter,show){if(show&&(counter==0)){ref._fsm_styT=ref.style.top;ref._fsm_styMT=ref.style.marginTop;ref._fsm_offT=ref.offsetTop||0}var cP=Math.pow(Math.sin(Math.PI*counter/2),0.75);var clipY=ref.offsetHeight*(1-cP);ref.style.clip=(counter==1?((window.opera||navigator.userAgent.indexOf('KHTML')>-1)?'':'rect(auto,auto,auto,auto)'):'rect('+clipY+'px,'+ref.offsetWidth+'px,'+ref.offsetHeight+'px,0)');if(counter==1||(counter<0.01&&!show)){ref.style.top=ref._fsm_styT;ref.style.marginTop=ref._fsm_styMT}else{ref.style.top=((0-clipY)+(ref._fsm_offT))+'px';ref.style.marginTop='0'}};FSMenu.animFade=function(ref,counter,show){var done=(counter==1);if(ref.filters){var alpha=!done?' alpha(opacity='+parseInt(counter*100)+')':'';if(ref.style.filter.indexOf("alpha")==-1)ref.style.filter+=alpha;else ref.style.filter=ref.style.filter.replace(/\s*alpha\([^\)]*\)/i,alpha)}else ref.style.opacity=ref.style.MozOpacity=counter/1.001};FSMenu.animClipDown=function(ref,counter,show){var cP=Math.pow(Math.sin(Math.PI*counter/2),0.75);ref.style.clip=(counter==1?((window.opera||navigator.userAgent.indexOf('KHTML')>-1)?'':'rect(auto,auto,auto,auto)'):'rect(0,'+ref.offsetWidth+'px,'+(ref.offsetHeight*cP)+'px,0)')};FSMenu.prototype.activateMenu=function(id,subInd){with(this){if(!isDOM||!document.documentElement)return;var fsmFB=getRef('fsmenu-fallback');if(fsmFB){fsmFB.rel='alternate stylesheet';fsmFB.disabled=true}var a,ul,li,parUL,mRoot=getRef(id),nodes,count=1;var lists=mRoot?mRoot.getElementsByTagName('ul'):[];for(var i=0;i<lists.length;i++){li=ul=lists[i];while(li){if(li.nodeName.toLowerCase()=='li')break;li=li.parentNode}if(!li)continue;parUL=li;while(parUL){if(parUL.nodeName.toLowerCase()=='ul')break;parUL=parUL.parentNode}a=null;for(var j=0;j<li.childNodes.length;j++)if(li.childNodes[j].nodeName.toLowerCase()=='a')a=li.childNodes[j];if(!a)continue;var menuID=myName+'-id-'+count++;if(ul.id)menuID=ul.id;else ul.setAttribute('id',menuID);var sOC=(showOnClick==1&&li.parentNode==mRoot)||(showOnClick==2);var evtProp=navigator.userAgent.indexOf('Safari')>-1||isOp?'safRtnVal':'returnValue';var eShow=new Function('with('+myName+'){var m=menus["'+menuID+'"],pM=menus["'+parUL.id+'"];'+(sOC?'if((pM&&pM.child)||(m&&m.visible))':'')+' show("'+menuID+'",this)}');var eHide=new Function('e','if(e.'+evtProp+'!=false)'+myName+'.hide("'+menuID+'")');addEvent(a,'mouseover',eShow);addEvent(a,'focus',eShow);addEvent(a,'mouseout',eHide);addEvent(a,'blur',eHide);if(sOC)addEvent(a,'click',new Function('e',myName+'.show("'+menuID+'",this);if(e.cancelable&&e.preventDefault)e.preventDefault();e.'+evtProp+'=false;return false'));if(subInd)a.insertBefore(subInd.cloneNode(true),a.firstChild)}if(isIE&&!isOp){var aNodes=mRoot?mRoot.getElementsByTagName('a'):[];for(var i=0;i<aNodes.length;i++){addEvent(aNodes[i],'focus',new Function('e','var node=this.parentNode;while(node){if(node.onfocus)node.onfocus(e);node=node.parentNode}'));addEvent(aNodes[i],'blur',new Function('e','var node=this.parentNode;while(node){if(node.onblur)node.onblur(e);node=node.parentNode}'))}}if(hideOnClick && mRoot)addEvent(mRoot,'click',new Function(myName+'.hideAll()'));menus[id]=new FSMenuNode(id,true,this)}};var page={win:self,minW:0,minH:0,MS:isIE&&!isOp,db:document.compatMode&&document.compatMode.indexOf('CSS')>-1?'documentElement':'body'};page.elmPos=function(e,p){var x=0,y=0,w=p?p:this.win;e=e?(e.substr?(isNS4?w.document.anchors[e]:getRef(e,w)):e):p;if(isNS4){if(e&&(e!=p)){x=e.x;y=e.y};if(p){x+=p.pageX;y+=p.pageY}}if(e&&this.MS&&navigator.platform.indexOf('Mac')>-1&&e.tagName=='A'){e.onfocus=new Function('with(event){self.tmpX=clientX-offsetX;self.tmpY=clientY-offsetY}');e.focus();x=tmpX;y=tmpY;e.blur()}else while(e){x+=e.offsetLeft;y+=e.offsetTop;e=e.offsetParent}return{x:x,y:y}};if(isNS4){var fsmMouseX,fsmMouseY,fsmOR=self.onresize,nsWinW=innerWidth,nsWinH=innerHeight;document.fsmMM=document.onmousemove;self.onresize=function(){if(fsmOR)fsmOR();if(nsWinW!=innerWidth||nsWinH!=innerHeight)location.reload()};document.captureEvents(Event.MOUSEMOVE);document.onmousemove=function(e){fsmMouseX=e.pageX;fsmMouseY=e.pageY;return document.fsmMM?document.fsmMM(e):document.routeEvent(e)};function isMouseIn(sty){with(sty)return((fsmMouseX>left)&&(fsmMouseX<left+clip.width)&&(fsmMouseY>top)&&(fsmMouseY<top+clip.height))}}

FSMenu.prototype.ieSelBoxFixShow = function(mN) { with (this)
{
 var m = menus[mN];
 if (!isIE || !window.createPopup) return;
 if (navigator.userAgent.match(/MSIE ([\d\.]+)/) && parseFloat(RegExp.$1) > 6.5)
  return;
 // Create a new transparent IFRAME if needed, and insert under the menu.
 if (!m.ifr)
 {
  m.ifr = document.createElement('iframe');
  m.ifr.src = 'about:blank';
  with (m.ifr.style)
  {
   position = 'absolute';
   border = 'none';
   filter = 'progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';
  }
  m.lyr.ref.parentNode.insertBefore(m.ifr, m.lyr.ref);
 }
 // Position and show it on each call.
 document.getElementById(mN).style.display = 'block';
 with (m.ifr.style)
 {
  left = m.lyr.ref.offsetLeft + 'px';
  top = m.lyr.ref.offsetTop + 'px';
  width = m.lyr.ref.offsetWidth + 'px';
  height = m.lyr.ref.offsetHeight + 'px';
  visibility = 'visible';
 }
}};
FSMenu.prototype.ieSelBoxFixHide = function(mN) { with (this)
{
 if (!isIE || !window.createPopup) return;
 var m = menus[mN];
 if (m.ifr) m.ifr.style.visibility = 'hidden';
}};
