definition(
    name: "Change Location Mode from Switch",
    namespace: "SCHAHAR",
    author: "Sanjeev Chahar",
    description: "Change Location Mode based on Switch status change",
    category: "Convenience",
    iconUrl: "http://cdn.device-icons.smartthings.com/Lighting/light99-hue-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Lighting/light99-hue-icn@2x.png"
)//End definition

preferences {
	section("Select Switch to Trigger Location Mode Change") {
		input "theSwitch", "capability.switch", title:"Select Switch", description:"Select switch to change location mode", required:true,multiple: false,displayDuringSetup: true
	}//end section
    
    section("Set Location Mode When Switch is ON") {
		input "mode_switch_on", "mode", title:"Switch 'ON' Location Mode", description:"Select location mode when switch is ON", required:true,multiple: false,displayDuringSetup: true
	}//end section
    
    section("Set Location Mode When Switch is OFF") {
		input "mode_switch_off", "mode", title:"Switch 'OFF' Location Mode", description:"Select location mode when switch is OFF", required:true,multiple: false,displayDuringSetup: true
	}//end section
    
    section("Debug Log Level") {
     input (name: "zwtLoggingLevelIDE",title: "Debug Log Level?", description:"Select Debug Log Level..",type: "enum",options: ["0":"None", "3" :"Info", "4":"Debug", "5":"Trace"],defaultValue: "0",required: true,displayDuringSetup: true)//End input
    }//end input
            
} //End preferences

def installed(){
	setLoggingLevel()
    subscribe(theSwitch, "switch", switchHandler)
	subscribe(location, changedLocationMode)
} //End installed

def updated(){
	setLoggingLevel()
	unsubscribe()
    
    subscribe(theSwitch, "switch", switchHandler)
	subscribe(location, changedLocationMode)
}//End updated()

def switchHandler(evt){
	logger "Switch Event Status : $evt.value","debug"

	if (evt.value == "on") {
        logger "switch turned on! - $mode_switch_on","debug"
        changeMode("$mode_switch_on")
    } else if (evt.value == "off") {
        logger "switch turned off! - $mode_switch_off","debug"
        changeMode("$mode_switch_off")
    }//end if 
}//End switchHandler

def changedLocationMode(evt) {
	logger "'$location' mode changed to '$evt.value'", "info"
	    
    def currentSwitchState =  theSwitch.currentValue('switch')
    
    if (evt.value == mode_switch_on){
    	if (currentSwitchState != "on"){theSwitch.on()}
    }else if (evt.value == mode_switch_off){
    	if (currentSwitchState != "off"){theSwitch.off()}
    }//end if 
    
} //End changedLocationMode

def changeMode(newMode) {

	if (location.mode == newMode){ return} //dont do anything since mode is correct

    if (newMode && location.mode != newMode) {
        if (location.modes?.find{it.name == newMode}) {
            setLocationMode(newMode)
            logger "'$location' has changed the mode to '${newMode}'","info"
        }else {
            logger "'${location}' tried to change to undefined mode '${newMode}'","error"
        }//end if 
    } //end if 
}//end changeMode


def setLoggingLevel(){
	   // Logging Level:
        state.loggingLevelIDE = (settings.zwtLoggingLevelIDE) ? settings.zwtLoggingLevelIDE.toInteger() : 0
}//end setLoggingLevel

private logger(msg, level = "debug") {
    switch(level) {
        case "error":
            /*if (state.loggingLevelIDE >= 1)*/ log.error msg
            break
        case "warn":
            /*if (state.loggingLevelIDE >= 2)*/ log.warn msg
            break
        case "info":
            if (state.loggingLevelIDE >= 3) log.info msg
            break
        case "debug":
            if (state.loggingLevelIDE >= 4) log.debug msg
            break
        case "trace":
            if (state.loggingLevelIDE >= 5) log.trace msg
            break
        default:
        	if (state.loggingLevelIDE >0){log.debug msg}
            break
    } //end switch
}//end logger

/*
def appTouch(evt) {
	log.debug "appTouch: $evt"
	switches?.on()
} //End appTouch
*/