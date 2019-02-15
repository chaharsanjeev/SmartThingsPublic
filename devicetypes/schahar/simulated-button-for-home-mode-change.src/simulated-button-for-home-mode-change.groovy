	metadata {
            definition (name: "Simulated Button for Home Mode Change", namespace: "SCHAHAR", author: "Sanjeev Chahar") {
                capability "Actuator"
                capability "Sensor"
                capability "Health Check"
                capability "Switch"
            }//end definition


			simulator {	}
    
    		tiles(scale: 2) {
    				multiAttributeTile(name:"statusText2_main", type:"generic", width:6, height:4) {
                        tileAttribute("statusText2_1", key: "PRIMARY_CONTROL") {
                                attributeState "Shutdown All", label:'Shutdown All', action:null, icon:null,backgroundColor: "#153591" /* blue color */
                                attributeState "Home", label:'Home', action:null, icon:null,backgroundColor: "#44b621" 
                        }//end tileAttribute

                        tileAttribute ("statusText2", key: "SECONDARY_CONTROL") {
                            attributeState "statusText2", label:'Home Mode: ${currentValue}'
                        } //End tileAttribute
                    } //End multiAttributeTile
        
  					standardTile("theSwitch", "device.switch", width: 6, height: 3, decoration: "flat",canChangeIcon: true,canChangeBackground:true) {
                        state "off", label:'Home', action:"switch.on", icon:"st.switches.switch.off",backgroundColor:"#44b621" /*  color */
                        state "on", label:'Shutdown All', action:"switch.off", icon:"st.switches.switch.on",backgroundColor:"#153591" /* blue color */
                    }//End standardTile - switch

                    standardTile("mode_shutdown", "device.mode_shutdown", width: 6, height: 2, decoration: "flat") {
                        state "default", label: "Change Mode: Shutdown All", backgroundColor: "#ffffff", action: "switch.on"
                    } //end standardTile
                    
                    standardTile("mode_home", "device.mode_home", width: 6, height: 2, decoration: "flat") {
                        state "default", label: "Change Mode: Home", backgroundColor: "#ffffff", action: "switch.off"
                    }  //End standardTile
        
             preferences {}
             
             main "switch"
             details(["statusText2_main","mode_shutdown","mode_home"])

		}//end tiles
}//end metadata

def updateCurrentMode(){sendEvent(name:"statusText2", value: "${location.mode}", displayed:false)}//end updateCurrentMode

def on(){
	log.debug "on"
    changeMode("Shutdown All")
} //end on

def off(){
	
    //sendEvent(name:"statusText2", value: "${location.mode}", displayed:false)
	log.debug "off"
    changeMode("Home")
}//end off


def parse(String description) {
    updateCurrentMode()
}//end parse



def installed() {
	log.trace "Executing 'installed'"
	initialize()
}//end installed

def updated() {
	log.trace "Executing 'updated'"
	initialize()
}//end updated

private initialize() {
	log.trace "Executing 'initialize'"
	
    updateCurrentMode()
    
   
    subscribe(location, changedLocationMode)

	sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
	sendEvent(name: "healthStatus", value: "online")
	sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
}//end initialize


def changedLocationMode(evt) {
	log.info "'$location' mode changed to '$evt.value'"
	updateCurrentMode()
        
    def currentSwitchState =  theSwitch.currentValue('switch')
    if (evt.value == "Shutdown All"){
    	if (currentSwitchState != "on"){theSwitch.on()}
    }else /*if (evt.value == "Home") */{
    	if (currentSwitchState != "off"){theSwitch.off()}
    }//end if 
    
} //End changedLocationMode

def changeMode(newMode) {

	if (location.mode == newMode){ return} //dont do anything since mode is correct

    if (newMode && location.mode != newMode) {
        if (location.modes?.find{it.name == newMode}) {
            setLocationMode(newMode)
            log.info "'$location' has changed the mode to '${newMode}'"
        }else {
            log.error "'${location}' tried to change to undefined mode '${newMode}'"
        }//end if 
    } //end if 
}//end changeMode