metadata {

	definition (name: "SOnOff LAN Handler", namespace: "SCHAHAR", author: "Sanjeev Chahar", ocfDeviceType: "oic.d.smartplug",executeCommandsLocally: true) {
		capability "Actuator"
		capability "Health Check"
		capability "Polling"
		capability "Refresh"
		capability "Switch"
 	} //End defination

	tiles(scale: 2) {
		// TODO: define your main and details tiles here - define
        standardTile("switch", "device.switch", width: 6, height: 5, decoration: "flat",canChangeIcon: true,canChangeBackground:flase) {
    		state "off", label:'Off', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#fff", nextState:"turningOn" //white color
    		state "on", label:'On', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#44b621", nextState:"turningOff" //green color
    		state "turningOn", label:'Turning On', icon:"st.switches.switch.on", backgroundColor:"#44b621", nextState: "turningOff" /// green color
    		state "turningOff", label:'Turning Off', icon:"st.switches.switch.off", backgroundColor:"#f1d801", nextState: "turningOn" // yellow color
		}//End standardTile - switch
     
    	standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:'Refresh', action:"refresh.refresh", icon:"st.secondary.refresh"
		} //End standardTile - refresh

		standardTile("statusText2", "device.statusText2", width: 2, height: 2, inactiveLabel: false,decoration: "flat") {
            state "enabled", label:'Enabled', unit:"",backgroundColor:"#44b621" //Green color
            state "disabled", label:'Disabled', unit:"",backgroundColor:"#FF0000"//Red Color
		} //End standardTile - statusText2
        
        preferences {
           input(name: "SONOFF_ipAddress", type: "string", title: "SOnOff IP Address", description: "IP address of SonOff device", required: true, displayDuringSetup: true)
           input("SONOFF_REFRESH_RATE","enum", title: "Health check refresh",description: "Check connection every(minutes)", defaultValue: "5 Minute", options: ["1 Minute","5 Minute","10 Minute","15 Minute","30 Minute","60 Minute"], required: true, displayDuringSetup: true)
           input "SONOFF_enableDevice", "bool", required: true,title: "Enable Device?" ,description: "Enabled this device", defaultValue:true, displayDuringSetup: true
           input "SONOFF_enableDebug", "bool", required: true,title: "Enable Debug Log?" ,description: "Enabled log messages for debug", defaultValue:false, displayDuringSetup: true
       } //End preferences
    
		main "switch"
		details(["switch","refresh","statusText2"])

	} //End tiles
    
} //End Metadata

def on() {sendCommand("on")} //End function - on

def off() {	sendCommand("off")} //End function - off

def showDeviceSettings(){
    try{
    	SONOFF_enableDevice == true ? sendEvent(name:"statusText2", value: "enabled", displayed:false) : sendEvent(name:"statusText2", value: "disabled", displayed:false)
 	} catch(Exception ex) {
    	log.error "[Device name: $device]: Function:showDeviceSettings() Exception: " + ex
    }//End catch
} //End Function - showDeviceSettings


private def sendCommand(String command) {
    
    try{
    	  showDeviceSettings()
    	
    	//if Device is disabled or command is not refresh - stop, no point to refresh or get device status
    	if ((SONOFF_enableDevice != true) && (command != 'status')){ return}
    
    	def logPrefix = "[Device name: $device]: "
		state.RESPONSE_RECEIVED = false
	
		if (SONOFF_enableDebug == true){log.debug "$logPrefix sendCommand(${command}) to device at $SONOFF_ipAddress:$SONOFF_port"}
 
		if (!SONOFF_ipAddress){
       		log.error "$logPrefix SonOff 'IP Address' is not set for device, Stop"
  	   		return null;
    	}//End if 
    
        def SONOFF_port = 80
        def SONOFF_username = "admin"
        def SONOFF_password = "admin"
        def SONOFF_hosthex = convertIPtoHex(SONOFF_ipAddress)
        def SONOFF_porthex = convertPortToHex(SONOFF_port)
        def myMAC = "$SONOFF_hosthex:$SONOFF_porthex"
		def ip = SONOFF_ipAddress + ":" +  SONOFF_port
  		def path = "/cm"
          
    	device.deviceNetworkId = "$SONOFF_hosthex:$SONOFF_porthex"
   
        if (command == "on"){
            path += "?cmnd=Power%20On"
        }else if (command == "off"){
            path += "?cmnd=Power%20Off"
        }else if (command == "status"){
           path += "?cmnd=Power"
		}//End if 

		path += "&user=${SONOFF_username}&password=${SONOFF_password}"
    	
        runIn(7, "response_check") //7 seconds
    	sendHubCommand(new physicalgraph.device.HubAction("""GET $path HTTP/1.1\r\nHOST: $ip\r\n\r\n""", physicalgraph.device.Protocol.LAN, myMAC, [callback: calledBackHandler]))
	
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:sendCommand() Exception: " + ex
    }//End catch
    
} //End Function sendCommand

def response_check (){
	
    try{
        if (state.RESPONSE_RECEIVED == true){
            if (SONOFF_enableDebug == true){log.debug "[Device name: $device]: Response received from SonOff device, process response message"}
        }else{
            if (SONOFF_enableDebug == true){log.error "[Device name: $device]: NO response received from SonOff, Set device status offline"}
            setDeviceHealthStatus('offline')
        }//end if 

    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:response_check() Exception: " + ex
    }//End catch
} //end function - response_check 


void calledBackHandler(physicalgraph.device.HubResponse hubResponse) {
    
    try{
    		state.RESPONSE_RECEIVED = true
    	    def logPrefix = "[Device name: $device]: "
	
            if (SONOFF_enableDebug == true){
                log.debug "$logPrefix Response Message: " + hubResponse.json
                log.debug "$logPrefix Response Status: " + hubResponse.status
                log.debug "$logPrefix Response Error: " + hubResponse.error
            }//end if 

          if (hubResponse.status == 200){
                def sonoff_warning = hubResponse.json?.WARNING
                def sonoff_error = hubResponse.json?.ERROR
				if (SONOFF_enableDebug == true){log.debug "$logPrefix Response status is 200(connection established)"}
		
                if (sonoff_error?.trim()) {
                    log.error "$logPrefix Device is sending Error message, Stop and make device offline"
                    log.error "$logPrefix Device Error response: " + sonoff_error
                    setDeviceHealthStatus('offline')
                }else if (sonoff_warning?.trim()) {
                    log.error "$logPrefix Device is sending Warning message, Stop and make device offline"
                    log.error "$logPrefix Device Warning response: " + sonoff_warning
                    setDeviceHealthStatus('offline')
                }else{
                    def resultJson = {}
                    resultJson = hubResponse.json

                    if ((resultJson?.POWER in ["ON", 1, "1"]) || (resultJson?.Status?.POWER in [1, "1"])) {
                        setSwitchState(true)
                    } else if ((resultJson?.POWER in ["OFF", 0, "0"]) || (resultJson?.Status?.POWER in [0, "0"])) {
                        setSwitchState(false)
                    }//End if 

                    setDeviceHealthStatus('online')
    			}//end if 
    		}else{
    			log.error "$logPrefix NO response from sOnOff device, make device status offline"
    			setDeviceHealthStatus('offline')
    		} //End if 
  
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:calledBackHandler() Exception: " + ex
    }//End catch
}//End function - calledBackHandler

def setupHealthCheck() {
	try{
	    if (SONOFF_enableDebug == true){log.debug "[Device name: $device]: Invoked 'setupHealthCheck'"}
        
        showDeviceSettings() //Show if device is enabled or not on the tile
 		
        unschedule()
		
        //Schedule only if device is enabled
        if (SONOFF_enableDevice == true){
                log.debug "111"
        		def ref_rate = Integer.valueOf(SONOFF_REFRESH_RATE.substring(0, SONOFF_REFRESH_RATE.lastIndexOf(" ")).trim())
                 log.debug "111 : " + ref_rate
        		if (ref_rate == 1){
                    runEvery1Minute(refresh)
                }else if (ref_rate == 5){
                    runEvery5Minutes(refresh)
                }else if (ref_rate == 10){
                    runEvery10Minutes(refresh)
                }else if (ref_rate == 15){
                    runEvery15Minutes(refresh)
                }else if (ref_rate == 30){
                    runEvery30Minutes(refresh)
                }else if (ref_rate == 60){
                    runEvery1Hour(refresh)
                }else{
                    runEvery1Minute(refresh) //default
                } //End if

                //runIn(2, refresh)
                
        }else{ log.debug "222"}//end if - SONOFF_enableDevice == true
 	
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:setupHealthCheck() Exception: " + ex
    }//End catch
}//end function - setupHealthCheck

def ping() {
	try{
    	if (SONOFF_enableDebug == true){log.debug "[Device name: $device]: Executing sOnOff 'ping'"}
    	return refresh()
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:ping() Exception: " + ex
    }//End catch
} //End function - ping

def poll() {
	try{
    	if (SONOFF_enableDebug == true){log.debug "[Device name: $device]: Executing sOnOff 'poll'"}
    	return refresh()
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:poll() Exception: " + ex
    }//End catch
} //End function - poll

def refresh() {
	try{
		if (SONOFF_enableDebug == true){log.info "[Device name: $device]: Executing sOnOff 'refresh'"}
    	sendCommand("status")
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:refresh() Exception: " + ex
    }//End catch
} //End function - refresh

def installed() {
    try{
    	if (SONOFF_enableDebug == true){log.debug "[Device name: $device]: Executing sOnOff 'installed'"}
        setupHealthCheck()
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:installed() Exception: " + ex
    }//End catch
}//end function - installed

def updated(){
    try{
    	if (SONOFF_enableDebug == true){log.debug "[Device name: $device]: Executing sOnOff 'updated'"}
        setupHealthCheck()
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:updated() Exception: " + ex
    }//End catch
}//end function - updated

def setSwitchState(Boolean on) {
     
     try{
     	if (SONOFF_enableDebug == true){log.info "[Device name: $device]: Set to " + (on ? "ON" : "OFF")}
        
        sendEvent(name: "switch", value: on ? "on" : "off" , descriptionText: "Status changed to "  + (on ? "ON" : "OFF"),displayed : true,linkText:"$device " + (on ? "ON" : "OFF"))
        
     } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:setSwitchState() Exception: " + ex
    }//End catch
    
} //end function - setSwitchState

def setDeviceHealthStatus(String statusValue){
	//Value: offline/online
    
    try{
        // Gets the most recent State for device
        def currentDeviceState = device.currentState("switch")

        //If device is offline, flip the button status to original value
        if ( statusValue == "offline"){
            if (currentDeviceState.value == "off"){
                setSwitchState(false)
            }else if (currentDeviceState.value == "on"){
                setSwitchState(true)
            } //End if - currentDeviceState
        }//End if - statusValue

        sendEvent(name: "DeviceWatch-DeviceStatus", value: statusValue)
        sendEvent(name: "healthStatus", value: statusValue)
        sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "lan", scheme:"untracked"].encodeAsJson(), displayed: true)
	
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:setDeviceHealthStatus() Exception: " + ex
    }//End catch
    
}//End function - setDeviceHealthStatus

private String convertIPtoHex(ipAddress) { 
	try{
    	String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
		return hex
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:convertIPtoHex() Exception: " + ex
    }//End catch
 }  //End function - convertIPtoHex

private String convertPortToHex(port) {
	
    try{
        String hexport = port.toString().format('%04x', port.toInteger())
        return hexport
 	} catch(Exception ex) {
    	log.error "[Device name: $device]: Function:convertPortToHex() Exception: " + ex
    }//End catch
} //End function - convertPortToHex
