/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Light Up The Night
 *  
 *  Author: SmartThings
 */
definition(
    name: "Manage Office Space",
    namespace: "SCHAHAR",
    author: "Sanjeev Chahar",
    description: "Turn your lights on when it gets dark and off when it becomes light again.",
    category: "Convenience",
    iconUrl: "http://cdn.device-icons.smartthings.com/Lighting/light99-hue-v2-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Lighting/light99-hue-v2-icn@2x.png"
) //End definition

preferences {
	    page(name: "page1")
        page(name: "page_thingsToMonitorControl")
        page(name: "page_lightOn")
        page(name: "page_lightOff")
        page(name: "page_mis")
           
} //End preferences

def page1() { 
	 dynamicPage(name: "page1", title:"MY PAGE",uninstall:true,install:false,nextPage:"page_thingsToMonitorControl") {
        section("Automation Rule Name") {
      	  label(name: "input_rulename",title: "Enter rule name",description: "Automation rule name..",required: true, image:null)
    	}//End section
    
    
        
        section("Debug") {
             input(name: "input_enableLog", type: "bool", title: "Enable debug Logs?",description:null,multiple: false,required:true,submitOnChange:false, defaultValue:false)
        } //End section
        
	} //End dynamic page
} //End method page1

def page_thingsToMonitorControl(){
		 dynamicPage(name: "page_thingsToMonitorControl", title:"Sensors to Monitor and Control",uninstall:true,install: false,nextPage:"page_lightOn") {
         	  section("Select Smart Things to monitor", hideable:false,hidden:false,mobileOnly:false) {
	         		input(name: "input_Sensor", type: "capability.illuminanceMeasurement", title: "Which Motion & Light Sensor?",description: "Select motion sensor..",multiple: false,required:true,submitOnChange:false, defaultValue:null)
             		input(name: "input_light", type: "capability.switch", title: "Which Light?",description: "Select light..",multiple: true,required:true,submitOnChange:false, defaultValue:null)
        		} //End section
		 } //end dynamicpage 
}//end page page_thingsToMonitorControl


def page_lightOn(){
		dynamicPage(name: "page_lightOn", title:"Condition for Switch On",uninstall:true,install: false,nextPage:"page_lightOff") {
        	 section("Turn on a light when...") {
    	         input(name: "input_illumanceMinValue", type: "number", title: "Luminosity(LUX) less then?",description: "Enter luminosity value..",multiple: false,required:true,submitOnChange:false, defaultValue:null)
        	     input(name: "input_checkForMotionON", type: "bool", title: "'AND' Motion detected?",description: null,multiple: false,required:true,submitOnChange:false, defaultValue:false)
   	     	} //End section
   		 } //end dynamicpage
}//end page page_lightOn


def page_lightOff(){
		dynamicPage(name: "page_lightOff", title:"Condition for Switch Off",uninstall:true,install: false,nextPage:"page_mis") {
                section("Turn off a light when...") {
                input(name: "input_illumanceMaxValue", type: "number", title: "Luminosity(LUX) more then?",description: "Enter luminosity value..",multiple: false,required:true,submitOnChange:false, defaultValue:null)
                input(name: "input_checkForMotionOFF", type: "bool", title: "'OR' Motion not detected?",description: null,multiple: false,required:true,submitOnChange:true, defaultValue:false)

                if (input_checkForMotionOFF){
                     input(name: "imput_motionNodetectedforDurationIn",type: "enum", title: "Motion Duration In", description:"Select value..", options: ["Second(s)","Minute(s)","Hour(s)"],multiple: false,required:true,submitOnChange:false,)
                     input(name: "imput_motionNodetectedforDurationValue", type: "number", title: "Motion Duration Value",description: "Enter number..",multiple: false,required:true,submitOnChange:false, defaultValue:null)
                }//End if 
            } //End section

		 } //end dynamicpage
}//end page page_lightOff


def page_mis(){
		dynamicPage(name: "page_mis", title:"Other Settings",uninstall:true,install: true) {

		} //end dynamicPage
}//end page_mis


def installed() {
	subscribe(input_Sensor, "illuminance", illuminanceHandler)
   	subscribe(input_Sensor, "motion", illuminanceHandler)

} //End installed


def updated() {
	unsubscribe()
	subscribe(input_Sensor, "illuminance", illuminanceHandler)
   	subscribe(input_Sensor, "motion", illuminanceHandler)
} //End updated()

// New aeon implementation
def illuminanceHandler(evt) {

	if (input_enableLog == true){
        def data = parseJson(evt.data)
        log.debug "Event Data: ${data}"
        log.debug "Event Created at: ${evt.date}"
        log.debug "Event Raw Description: ${evt.description}"
        log.debug "Event Description text: ${evt.descriptionText}"
        log.debug "Event Device text: ${evt.device}"
        log.debug "Event Display name: ${evt.displayName}"
        log.debug "Event Name: ${evt.name}"
        log.debug "Event Source: ${evt.source}"
        log.debug "Event String value: ${evt.stringValue}"
        log.debug "Event value: ${evt.value}"
        log.debug "Event is state change? :${evt.isStateChange()}"
     }//end if 
    
	//Light turn on logic
    def eventName = evt.name
    //if (eventName.equals("illuminance")){def eventValue = evt.integerValue}//Endif
    
    def currentLUX = input_Sensor.currentState("illuminance").integerValue
    def currentMotionStatus = input_Sensor.currentState("motion").value
    
    log.debug "------currentLUX------ :" + currentLUX
     log.debug "------currentMotionStatus------ :" + currentMotionStatus
    
        
    if ((eventName.equals("illuminance")) && (input_light.currentState("switch").value == "off") ) {
        if (eventValue <=input_illumanceMinValue){
            if ((input_checkForMotionON == true) && (input_Sensor.currentState("motion").value == "active")){
            	log.info "SWITCH ON LIGHT............."
            	input_light.on();
            }else if(input_checkForMotionON != true){
            	log.info "SWITCH ON LIGHT............eee."
            	input_light.on();
            }//end if - motion
        } //end if  = check LUX
    }//End if - check for event type 
   
   
   /*
    //Light turn off logic
	if (input_light.currentState("switch").value == "off"){
    	
         if (currentLUX <=input_illumanceMinValue){
          log.info "SWITCH ON read2" 
            if ((input_checkForMotionON == true) && (currentMotionStatus == "active")){
            	log.info "SWITCH ON LIGHT............."
            	input_light.on();
            }else{
            	input_light.on();
            }//end if - motion
        } //end if  = check LUX
    
    }//end-if light OFF
*/






	/*
	def lastStatus = state.lastStatus
	if (lastStatus != "on" && evt.integerValue < 30) {
		lights.on()
		state.lastStatus = "on"
	}
	else if (lastStatus != "off" && evt.integerValue > 50) {
		lights.off()
		state.lastStatus = "off"
	}
    */
}//end function

def motionHandler(evt){
log.info ""
}//end function