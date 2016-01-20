/*
 * Copyright 2005-2015 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Toggles a tab to show / hide and changes the source image to properly reflect this
// change. Returns false to avoid post. Example usage:
// onclick="javascript: return toggleTab(document, 'KualiForm', ${currentTabIndex}) }
function toggleTab(doc, formId, tabKey) {
	if (doc.getElementById(formId).elements['tabStates(' + tabKey + ')'].value == 'CLOSE') {
        showTab(doc, formId, tabKey);
    } else {
        hideTab(doc, formId, tabKey);
	}
	return false;
}

/** expands all tabs by unhiding them. */
function expandAllTab(formId) {
	doToAllTabs(showTab, formId);
	return false;
	}

/** collapses all tab by hiding them. */
function collapseAllTab(formId) {
	doToAllTabs(hideTab, formId);
	return false;
}

/** executes a function on all tabs.  The function will be passed a document, a formname & a partial tab name. */
function doToAllTabs(func, formId) {
	var elements = document.getElementsByTagName('div');
	if (!formId) {
		formId = 'kualiForm';
	}
	
	for (var x in elements) {
		if (elements[x].id && elements[x].id.substring(0, 4) === 'tab-' 
			&& elements[x].id.substring(elements[x].id.length - 4, elements[x].id.length) === '-div') {
			func(document, formId, elements[x].id.substring(4, elements[x].id.length - 4));
		}
	}
	return false;
}

function showTab(doc, formId, tabKey) {
    if (!doc.getElementById('tab-' + tabKey + '-div') || !doc.getElementById('tab-' + tabKey + '-imageToggle')) {
		return false;
	}
	
    // replaced 'block' with '' to make budgetExpensesRow.tag happy.
    doc.getElementById('tab-' + tabKey + '-div').style.display = '';
    doc.getElementById(formId).elements['tabStates(' + tabKey + ')'].value = 'OPEN';
    var toggler = doc.getElementById('tab-' + tabKey + '-imageToggle');
    var toggleDiv = toggler.lastElementChild;
    if (toggleDiv == null) {
        toggler.value = 'Hide';
        toggler.title = 'close';
        toggler.alt = 'close';
    } else {
        var span = toggleDiv.firstElementChild;
        $(span).removeClass('glyphicon-menu-down');
        $(span).addClass('glyphicon-menu-up');
        span.title = span.title.replace(/^show/, 'hide');
        span.title = span.title.replace(/^open/, 'close');
    }
    return false;
}

function hideTab(doc, formId, tabKey) {
    if (!doc.getElementById('tab-' + tabKey + '-div') || !doc.getElementById('tab-' + tabKey + '-imageToggle')) {
		return false;
	}

    doc.getElementById('tab-' + tabKey + '-div').style.display = 'none';
	doc.getElementById(formId).elements['tabStates(' + tabKey + ')'].value = 'CLOSE';
    var toggler = doc.getElementById('tab-' + tabKey + '-imageToggle');
    var toggleDiv = toggler.lastElementChild;
    if (toggleDiv == null) {
        toggler.value = 'Show';
        toggler.title = 'open';
        toggler.alt = 'open';
    } else {
        var span = toggleDiv.firstElementChild;
        $(span).removeClass('glyphicon-menu-up');
        $(span).addClass('glyphicon-menu-down');
        span.title = span.title.replace(/^hide/, 'show');
        span.title = span.title.replace(/^close/, 'open');
    }
    return false;
}

var formHasAlreadyBeenSubmitted = false;
var excludeSubmitRestriction = false;

function hasFormAlreadyBeenSubmitted() {
//	alert( "submitting form" );
	try {
		// save the current scroll position
		saveScrollPosition();
	} catch ( ex ) {
		// do nothing - don't want to stop submit
	}

	if ( document.getElementById( "formComplete" ) ) { 
	    if (formHasAlreadyBeenSubmitted && !excludeSubmitRestriction) {
	       alert("Page already being processed by the server.");
	       return false;
	    } else {
	       formHasAlreadyBeenSubmitted = true;
	       return true;
	    }
	    excludeSubmitRestriction = false;
    } else {
	       alert("Page has not finished loading.");
	       return false;
	} 
}

// Called when we want to submit the form from a field event and 
// want focus to be placed on the next field according to the current tab order
// when the page refreshes
function setFieldToFocusAndSubmit(triggerElement) {
	if(document.forms[0].fieldNameToFocusOnAfterSubmit) {
		if (document.forms.length > 0) {
			var nextTabField;
			var field = document.forms[0];
			for (i = 0; i < field.length; i++) {
				if (field.elements[i].tabIndex > triggerElement.tabIndex) {
					if (nextTabField) {
						if (field.elements[i].tabIndex < nextTabField.tabIndex) {
					       nextTabField = field.elements[i];
			         	}
					}
		       	    else {
				       nextTabField = field.elements[i];
			        }
				}	
			}
	
	        if (nextTabField) {
	        	document.forms[0].fieldNameToFocusOnAfterSubmit.value = nextTabField.name;
	        }
		}	
	}
	
    document.forms[0].submit();
}

function submitForm() {
    document.forms[0].submit();
}

function resetScrollPosition() {
	window.scrollTo(0,0);
	parent.window.scrollTo(0,0);
}

function saveScrollPosition() {
	if ( document.forms[0].formKey ) {
		// KULRICE-8292: Timeout issues across servers (3535) 
		scrollPositionKey = document.forms[0].formKey.value % 20;
		
		if( document.documentElement ) { 
			x = Math.max(parent.document.documentElement.scrollLeft, parent.document.body.scrollLeft); 
		  	y = Math.max(parent.document.documentElement.scrollTop, parent.document.body.scrollTop); 
		} else if( document.body && typeof document.body.scrollTop != "undefined" ) { 
			x = parent.document.body.scrollLeft; 
		  	y = parent.document.body.scrollTop; 
		} else if ( typeof window.pageXOffset != "undefined" ) { 
			x = parent.window.pageXOffset; 
		  	y = parent.window.pageYOffset; 
		} 
		document.cookie = "KulScrollPos"+scrollPositionKey+"="+x+","+y+"; path="+document.location.pathname;
	}
}

function restoreScrollPosition() {
    if ( document.forms.length > 0 && document.forms[0].formKey ) {
    	// KULRICE-8292: Timeout issues across servers (3535) 
    	scrollPositionKey = document.forms[0].formKey.value % 20;
    	
        var cookieName = "KulScrollPos"+scrollPositionKey;
        var matchResult = document.cookie.match(new RegExp(cookieName+"=([^;]+);?"));
        if ( matchResult ) {
            var coords = matchResult[1].split( ',' );
            window.scrollTo(coords[0],coords[1]);
            parent.window.scrollTo(coords[0],coords[1]);
            expireCookie( cookieName );
            return true;
        } else { // check for entry before form key set
        	cookieName = "KulScrollPos";
	        var matchResult = document.cookie.match(new RegExp(cookieName+"=([^;]+);?"));
	        if ( matchResult ) {
	            var coords = matchResult[1].split( ',' );
	            window.scrollTo(coords[0],coords[1]);
	            parent.window.scrollTo(coords[0],coords[1]);
	            expireCookie( cookieName );
	            return true;
	        } //else {
	        	//no match for cookie... new screen???
	        	//resetScrollPosition();
	        //}
	        
        }
    }
    return false;
}

function expireCookie( cookieName ) {
	var date = new Date();
	date.setTime( date.getTime() - 60000 );
	document.cookie = cookieName+"=0,0; expires="+date.toGMTString()+"; path="+document.location.pathname;
}

/* script to prevent the return key from submitting a form unless the user is on a button or on a link. fix for KULFDBCK-555 */ 
function isReturnKeyAllowed(buttonPrefix , event) {
	/* use IE naming first then firefox. */
    var elemType = event.srcElement ? event.srcElement.type : event.target.type;
    if (elemType != null && elemType.toLowerCase() == 'textarea') {
      // KULEDOCS-1728: textareas need to have the return key enabled
      return true;
    }
	var initiator = event.srcElement ? event.srcElement.name : event.target.name;
	var key = event.keyCode;
	/* initiator is undefined check is to prevent return from doing anything if not in a form field since the initiator is undefined */
	/* 13 is return key code */
	/* length &gt; 0 check is to allow user to hit return on links */
	if ( key == 13 ) {
		if( initiator == undefined || ( initiator.indexOf(buttonPrefix) != 0 && initiator.length > 0) ) {
		  // disallow enter key from fields that dont match prefix.
		  return false;
		}
	}
    return true;
}

//The following javascript is intended to resize the route log iframe
// to stay at an appropriate height based on the size of the documents
// contents contained in the iframe.
//  NOTE: this will only work when the domain serving the content of kuali
//         is the same as the domain serving the content of workflow.
var routeLogResizeTimer = ""; // holds the timer for the  route log iframe resizer
var currentHeight = 500; // holds the current height of the iframe
var safari = navigator.userAgent.toLowerCase().indexOf('safari');

function setRouteLogIframeDimensions() {
  var routeLogFrame = document.getElementById("routeLogIFrame");
  var routeLogFrame = document.getElementById("routeLogIFrame");
  var routeLogFrameWin = window.frames["routeLogIFrame"];
  var frameDocHeight = 0;
  try {
    frameDocHeight = routeLogFrameWin.document.documentElement.scrollHeight;
  } catch ( e ) {
    // unable to set due to cross-domain scripting
    frameDocHeight = 0;
  }

  if ( frameDocHeight > 0 ) {
	  if (routeLogFrame && routeLogFrameWin) {
	  	
	    if ((Math.abs(frameDocHeight - currentHeight)) > 30 ) {
	      if (safari > -1) {
	        if ((Math.abs(frameDocHeight - currentHeight)) > 59 ) {
	          routeLogFrame.style.height = (frameDocHeight + 30) + "px";
	          currentHeight = frameDocHeight;
	        }
	      } else {    
	        routeLogFrame.style.height = (frameDocHeight + 30) + "px";
	        currentHeight = frameDocHeight;
	      }
	    }
	  }
  }
	  
	    if (routeLogResizeTimer == "" ) {
	      routeLogResizeTimer = setInterval("resizeTheRouteLogFrame()",300);
	    }
	  }

function resizeTheRouteLogFrame() {
  setRouteLogIframeDimensions();
}

// should be in rice for direct inquiry 
 function inquiryPop(boClassName, inquiryParameters){
  parameterPairs = inquiryParameters.split(",");
  queryString="businessObjectClassName="+boClassName+"&methodToCall=start"
  for (i in parameterPairs) {
  
    parameters = parameterPairs[i].split(":");
  	if (document.forms[0].elements[parameters[0]].value=="") 
  	{
  		alert("Please enter a value in the appropriate field.");
  		//queryString=queryString+"&"+parameters[1]+"=directInquiryParameterNotSpecified";
		return false;
  	} else {
    	queryString=queryString+"&"+parameters[1]+"="+document.forms[0].elements[parameters[0]].value;
  	}
  }
  url=window.location.href
  pathname=window.location.pathname
  idx1=url.indexOf(pathname);
  idx2=url.indexOf("/",idx1+1);
  baseUrl=url.substr(0,idx2)

  if (baseUrl.length > 3 && baseUrl.substr(baseUrl.length - 3)=="/kr") {
    window.open(baseUrl+"/directInquiry.do?"+queryString, "_blank", "width=640, height=600, scrollbars=yes");
  }
  else {
    window.open(baseUrl+"/kr/directInquiry.do?"+queryString, "_blank", "width=640, height=600, scrollbars=yes");
  }
}
 
function textAreaPop(textAreaName, htmlFormAction, textAreaLabel, docFormKey, textAreaReadOnly, textAreaMaxLength) {
	
	if (textAreaReadOnly === null || textAreaReadOnly === undefined) {
		textAreaReadOnly = false;
	}
	
	if (textAreaMaxLength === null || textAreaMaxLength === undefined) {
		textAreaMaxLength = "";
	}
	
	var documentWebScope="session"
	window.open("updateTextArea.do?textAreaFieldName="+textAreaName+"&htmlFormAction="+htmlFormAction+"&textAreaFieldLabel="+textAreaLabel+"&docFormKey="+docFormKey+"&documentWebScope="+documentWebScope+"&textAreaReadOnly="+textAreaReadOnly+"&textAreaMaxLength="+textAreaMaxLength, "_blank", "width=580, height=560, scrollbars=yes");
}

function setTextArea(textAreaName) {
  document.getElementById(textAreaName).value = window.opener.document.getElementById(textAreaName).value; 
}

function textLimit(taElement, maxlen) 
{
	var fieldValue = taElement.value;
    if (fieldValue.length > maxlen) 
    { 
	    taElement.value = taElement.value.substr(0, maxlen); 
    } 
} 

function postValueToParentWindow(textAreaName) {
  window.opener.document.getElementById(textAreaName).value = document.getElementById(textAreaName).value; 
  self.close();
}

function showHide(showId,hideId){
  var style_sheet = getStyleObject(showId);
  if (style_sheet)
  {
	changeObjectVisibility(showId, "block");
	changeObjectVisibility(hideId, "none");
  }
  else 
  {
    alert("sorry, this only works in browsers that do Dynamic HTML");
  }
}

function changeObjectVisibility(objectId, newVisibility) {
    // first get the object's stylesheet
    var styleObject = getStyleObject(objectId);

    // then if we find a stylesheet, set its visibility
    // as requested
    //
    if (styleObject) {
		styleObject.display = newVisibility;
	return true;
    } else {
	return false;
    } 
}

function getStyleObject(objectId) {
  // checkW3C DOM, then MSIE 4, then NN 4.
  //
  if(document.getElementById && document.getElementById(objectId)) {
	return document.getElementById(objectId).style;
   }
   else if (document.all && document.all(objectId)) {  
	return document.all(objectId).style;
   } 
   else if (document.layers && document.layers[objectId]) { 
	return document.layers[objectId];
   } else {
	return false;
   }
}

// used on multiple value lookup pages to support the select/deselect all on page functions
function setAllMultipleValueLookuResults(checked) {
	for (i = 0; i < kualiElements.length; i++) {
		if (kualiElements[i].type == 'checkbox' && kualiElements[i].name.match('^selectedObjId-') == 'selectedObjId-') {
			kualiElements[i].checked = checked;
		}
	}
}
function placeFocus() {
	if (document.forms.length > 0) {
	  var fieldNameToFocus;
	  if (document.forms[0].fieldNameToFocusOnAfterSubmit) {
	    fieldNameToFocus = document.forms[0].fieldNameToFocusOnAfterSubmit.value;
	  }
	  
	  var focusSet = false;
	  var field = document.forms[0];
	  for (i = 0; i < field.length; i++) {
		if (fieldNameToFocus) {
	  	  if (field.elements[i].name == fieldNameToFocus) {
			  document.forms[0].elements[i].focus();
			  focusSet = true;
		  }	 
		}
		else if ((field.elements[i].type == "text") || (field.elements[i].type == "textarea")) {
		  document.forms[0].elements[i].focus();
		  focusSet = true;
		}
		
		if (focusSet) {
			break;
		}
	  }
   }
}
