/**
* GIT : https://github.com/erocm123/SmartThingsPublic/blob/master/devicetypes/erocm123/aeon-multisensor-6-advanced.src/aeon-multisensor-6-advanced.groovy
* */


 metadata {
	definition (name: "Aeon Multisensor 6 (Advanced - sanjeev changes)", namespace: "erocm123", author: "Eric Maycock", vid:"generic-motion-7") {
		capability "Motion Sensor"
		//capability "Acceleration Sensor"
		capability "Temperature Measurement"
		capability "Relative Humidity Measurement"
		capability "Illuminance Measurement"
		capability "Ultraviolet Index" 
		capability "Configuration"
		capability "Sensor"
		capability "Battery"
        capability "Health Check"
        capability "Refresh"
        capability "Power Source"
        capability "Tamper Alert"
               
        command "resetBatteryRuntime"
        command "resetTamperAlert"
		
        attribute   "needUpdate", "string"
        
        fingerprint deviceId: "0x2101", inClusters: "0x5E,0x86,0x72,0x59,0x85,0x73,0x71,0x84,0x80,0x30,0x31,0x70,0x98,0x7A,0x5A" // 1.07 & 1.08 Secure
        fingerprint deviceId: "0x2101", inClusters: "0x5E,0x86,0x72,0x59,0x85,0x73,0x71,0x84,0x80,0x30,0x31,0x70,0x7A,0x5A" // 1.07 & 1.08
        fingerprint deviceId: "0x2101", inClusters: "0x5E,0x86,0x72,0x59,0x85,0x73,0x71,0x84,0x80,0x30,0x31,0x70,0x7A", outClusters: "0x5A" // 1.06
        fingerprint mfr:"0086", prod:"0102", model:"0064", deviceJoinName: "Aeon MultiSensor 6"
	} //end definition 
	
    preferences {
        input description: "Once you change values on this page, the corner of the \"configuration\" icon will change orange until all configuration parameters are updated.", title: "Settings", displayDuringSetup: false, type: "paragraph", element: "paragraph"
		generate_preferences(configuration_model())
    }//end preferences
	
	simulator {} //End - simulator
	
	tiles (scale: 2) {
		multiAttributeTile(name:"temperature", type:"generic", width:6, height:4) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
            	attributeState "temperature", label:'${currentValue}°', icon:"st.motion.motion.inactive", backgroundColors:[
                	[value: 31, color: "#153591"],
                    [value: 44, color: "#1e9cbb"],
                    [value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
            }
            tileAttribute ("statusText", key: "SECONDARY_CONTROL") {
				attributeState "statusText", label:'${currentValue}'
			} //End tileAttribute
		} //End multiAttributeTile
       
       
        standardTile("motion","device.motion", inactiveLabel: false, width: 2, height: 2,canChangeIcon: true) {
                state "inactive",label:'no motion',icon:"st.motion.motion.inactive",backgroundColor:"#ffffff"
                state "active",label:'motion',icon:"st.motion.motion.active",backgroundColor:"#FF0000" //red color "#00a0dc"
		} //End standardTile
        
		valueTile("humidity","device.humidity", inactiveLabel: false, width: 2, height: 2) {
           	state "humidity",label:'${currentValue} % RH'
		}
		valueTile("illuminance", "device.illuminance", inactiveLabel: false, width: 2, height: 2) {
           state "luminosity", label:'${currentValue} LUX', unit:"lux", 
                backgroundColors:[
                	[value: 0, color: "#000000"],
                    [value: 1, color: "#FF0000"],
                    [value: 3, color: "#3E3900"],
                    [value: 12, color: "#8E8400"],
					[value: 24, color: "#C5C08B"],
					[value: 36, color: "#DAD7B6"],
					[value: 128, color: "#F3F2E9"],
                    [value: 1000, color: "#FFFFFF"]
				]
		}
        
		valueTile(
        	"ultravioletIndex","device.ultravioletIndex", inactiveLabel: false, width: 2, height: 2) {
				state "ultravioletIndex",label:'${currentValue} UV INDEX',unit:""
		}
		standardTile("acceleration", "device.acceleration", inactiveLabel: false, width: 2, height: 2) {
			state("inactive", label:'clear', icon:"st.motion.acceleration.inactive", backgroundColor:"#ffffff")
            state("active", label:'tamper', icon:"st.motion.acceleration.active", backgroundColor:"#f39c12")
		}
        
        standardTile("tamper", "device.tamper", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state("clear", label:'clear', icon:"st.contact.contact.closed", backgroundColor:"#cccccc", action: "resetTamperAlert")
            state("detected", label:'tamper', icon:"st.contact.contact.open", backgroundColor:"#e86d13", action: "resetTamperAlert")
		} //End standardTile - temper
		
        
        
        //valueTile("battery", "device.battery", inactiveLabel: false, width: 2, height: 2) {
		//	state "battery", label:'${currentValue}% battery', unit:""
		//}
        
       
        
        valueTile("currentFirmware", "device.currentFirmware", inactiveLabel: false, width: 2, height: 2) {
			state "currentFirmware", label:'v${currentValue}', unit:"", icon: "https://cdn3.iconfinder.com/data/icons/electronic-devices-vol-1-1/36/chip_circuit_ic_microchip_microprocessor_semiconductor_integratedcircuit-512.png"
		}//End title - currentFirmware
        
        
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'Refresh', action:"refresh.refresh", icon:"st.secondary.refresh"
		} //End standardTile - 
        
        standardTile("configure", "device.needUpdate", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "NO" , label:'Synced', action:"configuration.configure", icon:"st.secondary.configure"
            state "YES", label:'Pending', action:"configuration.configure", icon:"https://github.com/erocm123/SmartThingsPublic/raw/master/devicetypes/erocm123/qubino-flush-1d-relay.src/configure@2x.png"
        } //End standardTile - configure
        
       // standardTile(
	//		"batteryRuntime", "device.batteryRuntime", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
	//		state "batteryRuntime", label:'Battery: ${currentValue} Double tap to reset counter', unit:"", action:"resetBatteryRuntime"
	//	}  //End standardTile - 
       
        
        
        //For firmware status details
        // void standardTile(String tileName, String attributeName [, Map options, Closure closure])
		//		String tileName - the name of the tile. This is used to identify the tile when specifying the tile layout.
		//		String attributeName - the attribute this tile is associated with. The typical pattern is to prefix the attribute name with "device." - e.g., "device.water".
		// 		Map options (optional) - Various options for this tile. Valid options are found in the table below:
        //			1. width	Integer	controls how wide the tile is. Default is 1.
		//			2. height	Integer	controls how tall this tile is. Default is 1.
		//			3. canChangeIcon	Boolean	true to allow the user to pick their own icon. Defaults to false.
		//			4. canChangeBackground	Boolean	true to allow a user to choose their own background image for the tile. Defaults to false.
		//			5. decoration	String	specify "flat" for the tile to render without a ring.
        //
        standardTile("batteryTile", "device.batteryTile", inactiveLabel: false, width: 4, height: 2,decoration: "flat") {
		    state("Battery", label:'${currentValue}', action:"resetBatteryRuntime", icon: "https://pngriver.com/wp-content/uploads/2017/11/Battery-PNG-transparent-images-free-download-clipart-pics-full_battery1600.png") //Battery 
            state("USB Powered", label:'${currentValue}', icon: "http://chittagongit.com//images/electric-plug-icon/electric-plug-icon-25.jpg") //USB - electric power
     	} //End title - batteryTile
        
        standardTile("statusText2","device.statusText2",inactiveLabel:false,width:2,height:2,decoration:"flat",icon: "https://cdn3.iconfinder.com/data/icons/electronic-devices-vol-1-1/36/chip_circuit_ic_microchip_microprocessor_semiconductor_integratedcircuit-512.png") {
			state "statusText2", label:'${currentValue}', unit:""
		} //End standardTile - statusText2
        
		main(["motion","temperature"])
		details(["temperature","humidity","illuminance","ultravioletIndex","motion","tamper","batteryTile","refresh", "configure", "statusText2","battery"])
        
	} //End tiles
} //End metadata






//Method to sent the event for Battery label attribute
def setBatteryTitleEvent(Number batteryLevel){
	
    	def battryInfoDetails = ""
        if(settings."101" == null || settings."101" == "241") {
        	//its a battery power
            battryInfoDetails = " Battery: ${batteryLevel} % \n Duration: ${getBatteryRuntime()} \nDouble tap to reset"
            sendEvent(name:"batteryTile", value: "${battryInfoDetails}", displayed:false)  
        }else{
        	//its USB power
      		sendEvent(name:"batteryTile", value: "USB Powered", displayed:false)  
        } //End if
 
}//End function


def parse(String description)
{
	def result = []
    switch(description){
        case ~/Err 106.*/:
			state.sec = 0
			result = createEvent( name: "secureInclusion", value: "failed", isStateChange: true,
			descriptionText: "This sensor failed to complete the network security key exchange. If you are unable to control it via SmartThings, you must remove it from your network and add it again.")
        break
		case "updated":
        	result = createEvent( name: "Inclusion", value: "paired", isStateChange: true,
			descriptionText: "Update is hit when the device is paired")
            result << response(zwave.wakeUpV1.wakeUpIntervalSet(seconds: 3600, nodeid:zwaveHubNodeId).format())
            result << response(zwave.batteryV1.batteryGet().format())
            result << response(zwave.versionV1.versionGet().format())
            result << response(zwave.manufacturerSpecificV2.manufacturerSpecificGet().format())
            result << response(configure())
        break
        default:
			def cmd = zwave.parse(description, [0x31: 5, 0x30: 2, 0x84: 1])
			if (cmd) {
                try {
				result += zwaveEvent(cmd)
                } catch (e) {
                log.error "error: $e cmd: $cmd description $description"
                }
			}
        break
	}
    
    if(state.batteryRuntimeStart != null){
        sendEvent(name:"batteryRuntime", value:getBatteryRuntime(), displayed:false)
       
       // log.info "getBatteryRuntime()  --- ${getBatteryRuntime()} : --- Level: " + device.battery
       
       	def battryInfoDetails = ""
        if(settings."101" == null || settings."101" == "241") {
        	//its a battery power
            battryInfoDetails = " Battery: ${getBatteryRuntime()} Double tap to reset(2)"
        } //End if
        
        if (device.currentValue('currentFirmware') != null){
            //sendEvent(name:"statusText2", value: "Firmware: v${device.currentValue('currentFirmware')} - Battery: ${getBatteryRuntime()} Double tap to reset", displayed:false)
            sendEvent(name:"statusText2", value: "Firmware: v${device.currentValue('currentFirmware')} ${battryInfoDetails}", displayed:false)
        } else {
            //sendEvent(name:"statusText2", value: "Battery: ${getBatteryRuntime()} Double tap to reset", displayed:false)
            sendEvent(name:"statusText2", value: "${battryInfoDetails}", displayed:false)
        }//End if 
        
    } else {
        state.batteryRuntimeStart = now()
    } //Ebd if battery check
    
    def statusTextmsg = ""
    result.each {
        if ((it instanceof Map) == true && it.find{ it.key == "name" }?.value == "humidity") {
            statusTextmsg = "${it.value}% RH - ${device.currentValue('illuminance')? device.currentValue('illuminance') : "0%"} LUX - ${device.currentValue('ultravioletIndex')? device.currentValue('ultravioletIndex') : "0"} UV"
        }
        if ((it instanceof Map) == true && it.find{ it.key == "name" }?.value == "illuminance") {
            statusTextmsg = "${device.currentValue('humidity')? device.currentValue('humidity') : "0"}% RH - ${it.value} LUX - ${device.currentValue('ultravioletIndex')? device.currentValue('ultravioletIndex') : "0"} UV"
        }
        if ((it instanceof Map) == true && it.find{ it.key == "name" }?.value == "ultravioletIndex") {
            statusTextmsg = "${device.currentValue('humidity')? device.currentValue('humidity') : "0"}% RH - ${device.currentValue('illuminance')? device.currentValue('illuminance') : "0"} LUX - ${it.value} UV"
        }
    }
    if (statusTextmsg != "") sendEvent(name:"statusText", value:statusTextmsg, displayed:false)

	if ( result[0] != null ) { result }
}








def ping() {
	if (device.latestValue("powerSource") == "battery") {
		log.debug "Can't ping a wakeup device on battery"
	} else {
		//dc or unknown - get sensor report
		command(zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 0x01)) //poll the temperature to ping
	}
} //End - Ping


def configure() {
    state.enableDebugging = settings.enableDebugging
    logging("Configuring Device For SmartThings Use")
    def cmds = []

    cmds = update_needed_settings()
    
    if (cmds != []) commands(cmds)
}//End - configure


def installed() {
// Device-Watch simply pings if no device events received for 122min(checkInterval)
	sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID])
	sendEvent(name: "tamper", value: "clear", displayed: false)
} //End - installed

def updated(){
    state.enableDebugging = settings.enableDebugging
    sendEvent(name: "checkInterval", value: 6 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID])
    logging("updated() is being called")
    
    setBatteryTitleEvent(device.currentValue("battery"))
    
    //if (device.latestValue("powerSource") == "battery
    
    
    //if(settings."101" != null && settings."101" == "240") { 
    //    sendEvent(name:"batteryTile", value: "USB Powered", displayed:false)
    //} else {
    //    try {
    //        sendEvent(name:"batteryTile", value: "Battery ${(device.currentValue("battery") == null ? '?' : device.currentValue("battery"))}%", displayed:false)
    //    } catch (e) {
    //        logging("$e")
    //        sendEvent(name:"battery", value: "100", displayed:false)
    //        sendEvent(name:"batteryTile", value: "Battery ${(device.currentValue("battery") == null ? '?' : device.currentValue("battery"))}%", displayed:false)
    //    }
    //}

    state.needfwUpdate = ""
    
    if (state.realTemperature != null) sendEvent(name:"temperature", value: getAdjustedTemp(state.realTemperature))
    if (state.realHumidity != null) sendEvent(name:"humidity", value: getAdjustedHumidity(state.realHumidity))
    if (state.realLuminance != null) sendEvent(name:"illuminance", value: getAdjustedLuminance(state.realLuminance))
    if (state.realUV != null) sendEvent(name:"ultravioletIndex", value: getAdjustedUV(state.realUV))
    
    def cmds = update_needed_settings()
    
    if (device.currentValue("battery") == null) cmds << zwave.batteryV1.batteryGet()
    if (device.currentValue("temperature") == null) cmds << zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType:1, scale:1)
    if (device.currentValue("humidity") == null) cmds << zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType:3, scale:1)
    if (device.currentValue("illuminance") == null) cmds << zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType:5, scale:1)
    if (device.currentValue("ultravioletIndex") == null) cmds << zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType:27, scale:1)
    
    if (state.rawFW) {
        updateDataValue("firmware", "${state.rawFW}${getOverride()}")
        sendEvent(name: "currentFirmware", value: "${state.rawFW}${getOverride()}")
    } else if (device.currentValue("currentFirmware") != null) {
        updateDataValue("firmware", "${device.currentValue("currentFirmware") - ~/[A-Za-z]+/}${getOverride()}")
        sendEvent(name: "currentFirmware", value: "${device.currentValue("currentFirmware") - ~/[A-Za-z]+/}${getOverride()}")
    }
    
    if(state.batteryRuntimeStart != null){
        sendEvent(name:"batteryRuntime", value:getBatteryRuntime(), displayed:false)
        
        def battryInfoDetails = ""
        if (device.currentValue('batteryTile').toUpperCase().contains('BATTERY')){
        	battryInfoDetails = " Battery: ${getBatteryRuntime()} Double tap to reset"
        }//End if 
        
        if (device.currentValue('currentFirmware') != null){
            //sendEvent(name:"statusText2", value: "Firmware: v${device.currentValue('currentFirmware')} - Battery: ${getBatteryRuntime()} Double tap to reset", displayed:false)
            sendEvent(name:"statusText2", value: "Firmware: v${device.currentValue('currentFirmware')} ${battryInfoDetails}", displayed:false)
        } else {
            //sendEvent(name:"statusText2", value: "Battery: ${getBatteryRuntime()} Double tap to reset", displayed:false)
            sendEvent(name:"statusText2", value: "${battryInfoDetails}", displayed:false)
        }//End if
        
    } else {
        state.batteryRuntimeStart = now()
    }
    
    sendEvent(name:"needUpdate", value: device.currentValue("needUpdate"), displayed:false, isStateChange: true)
    
    response(commands(cmds))
}//End - updated

private getOverride() {
    switch(regionOverride) {
    case "0":
        return location.getTemperatureScale() == 'C' ? 'EU':''
    break
    case "1":
        return ""
    break
    case "2":
        return "EU"
    break
    case "3":
        return "AU"
    break
    default:
        return location.getTemperatureScale() == 'C' ? 'EU':''
    break
    }
}//end - getOverride



def update_current_properties(cmd){
    
	def currentProperties = state.currentProperties ?: [:]
    
    currentProperties."${cmd.parameterNumber}" = cmd.configurationValue

    if (settings."${cmd.parameterNumber}" != null)
    {   
            if (convertParam("${cmd.parameterNumber}".toInteger(), settings."${cmd.parameterNumber}".toInteger()) == cmd2Integer(cmd.configurationValue))
            {
                sendEvent(name:"needUpdate", value:"NO", displayed:false, isStateChange: true)
            }else{
                sendEvent(name:"needUpdate", value:"YES", displayed:false, isStateChange: true)
            }//end-if
    }//end-if

    state.currentProperties = currentProperties
	
	
}//End - update_current_properties

def update_needed_settings(){
	
	
    def cmds = []
    def currentProperties = state.currentProperties ?: [:]
     
    def configuration = parseXml(configuration_model())
    def isUpdateNeeded = "NO"
    
    if(!state.needfwUpdate || state.needfwUpdate == "") {
       logging("Requesting device firmware version")
       cmds << zwave.versionV1.versionGet()
    }

    if (state.currentProperties?."252" != [0]) {
        logging("Unlocking configuration.")
        cmds << zwave.configurationV1.configurationSet(configurationValue: integer2Cmd(0, 1), parameterNumber: 252, size: 1)
        cmds << zwave.configurationV1.configurationGet(parameterNumber: 252)
    }

    if(state.wakeInterval == null || state.wakeInterval != getAdjustedWake()){
        logging("Setting Wake Interval to ${getAdjustedWake()}")
        cmds << zwave.wakeUpV1.wakeUpIntervalSet(seconds: getAdjustedWake(), nodeid:zwaveHubNodeId)
        cmds << zwave.wakeUpV1.wakeUpIntervalGet()
    }

    configuration.Value.each
    {     
        if ("${it.@setting_type}" == "zwave"){
            if (currentProperties."${it.@index}" == null)
            {
                if (device.currentValue("currentFirmware") == null || "${it.@fw}".indexOf(device.currentValue("currentFirmware")) >= 0){
                    isUpdateNeeded = "YES"
                    log.error "Current value of parameter ${it.@index} is unknown"
                    cmds << zwave.configurationV1.configurationGet(parameterNumber: it.@index.toInteger())
                }
            } 
            else if (settings."${it.@index}" != null && cmd2Integer(currentProperties."${it.@index}") != convertParam(it.@index.toInteger(), settings."${it.@index}".toInteger()))
            { 
                if (device.currentValue("currentFirmware") == null || "${it.@fw}".indexOf(device.currentValue("currentFirmware")) >= 0){
                    isUpdateNeeded = "YES"

                    logging("Parameter ${it.@index} will be updated to " + convertParam(it.@index.toInteger(), settings."${it.@index}".toInteger()))
                    
                    if (it.@index == "41") {
                        if (device.currentValue("currentFirmware") == "1.06" || device.currentValue("currentFirmware") == "1.06EU") {
                            cmds << zwave.configurationV1.configurationSet(configurationValue: integer2Cmd(convertParam(it.@index.toInteger(), settings."${it.@index}".toInteger()), 2), parameterNumber: it.@index.toInteger(), size: 2)
                        } else if (device.currentValue("currentFirmware") == "1.10" || device.currentValue("currentFirmware") == "1.10EU" || device.currentValue("currentFirmware") == "1.11EU") {
                            cmds << zwave.configurationV1.configurationSet(configurationValue: integer2Cmd(convertParam(it.@index.toInteger(), settings."${it.@index}".toInteger()), 4), parameterNumber: it.@index.toInteger(), size: 4)
                        } else {
                            cmds << zwave.configurationV1.configurationSet(configurationValue: integer2Cmd(convertParam(it.@index.toInteger(), settings."${it.@index}".toInteger()), 3), parameterNumber: it.@index.toInteger(), size: 3)
                        }
                    } else {
                        cmds << zwave.configurationV1.configurationSet(configurationValue: integer2Cmd(convertParam(it.@index.toInteger(), settings."${it.@index}".toInteger()), it.@byteSize.toInteger()), parameterNumber: it.@index.toInteger(), size: it.@byteSize.toInteger())
                    }

                    cmds << zwave.configurationV1.configurationGet(parameterNumber: it.@index.toInteger())
                }
            } 
        }
    }
    
    sendEvent(name:"needUpdate", value: isUpdateNeeded, displayed:false, isStateChange: true)
    return cmds
	
	
} //End - update_needed_settings







def generate_preferences(configuration_model){
    def configuration = parseXml(configuration_model)
   
    configuration.Value.each
    {
        switch(it.@type)
        {   
            case ["byte","short","four"]:
                input "${it.@index}", "number",
                    title:"${it.@label}\n" + "${it.Help}",
                    range: "${it.@min}..${it.@max}",
                    defaultValue: "${it.@value}",
                    displayDuringSetup: "${it.@displayDuringSetup}"
            break
            case "list":
                def items = []
                it.Item.each { items << ["${it.@value}":"${it.@label}"] }
                input "${it.@index}", "enum",
                    title:"${it.@label}\n" + "${it.Help}",
                    defaultValue: "${it.@value}",
                    displayDuringSetup: "${it.@displayDuringSetup}",
                    options: items
            break
            case "decimal":
               input "${it.@index}", "decimal",
                    title:"${it.@label}\n" + "${it.Help}",
                    range: "${it.@min}..${it.@max}",
                    defaultValue: "${it.@value}",
                    displayDuringSetup: "${it.@displayDuringSetup}"
            break
            case "boolean":
               input "${it.@index}", "boolean",
                    title:"${it.@label}\n" + "${it.Help}",
                    defaultValue: "${it.@value}",
                    displayDuringSetup: "${it.@displayDuringSetup}"
            break
        }  
    }
} //End - generate_preferences


private updateStatus(){
   def result = []
   if(state.batteryRuntimeStart != null){
        sendEvent(name:"batteryRuntime", value:getBatteryRuntime(), displayed:false)
        
        def battryInfoDetails = ""
        if (device.currentValue('batteryTile').toUpperCase().contains('BATTERY')){
        	battryInfoDetails = " Battery: ${getBatteryRuntime()} Double tap to reset"
        }//End if 
        
        if (device.currentValue('currentFirmware') != null){
            //sendEvent(name:"statusText2", value: "Firmware: v${device.currentValue('currentFirmware')} - Battery: ${getBatteryRuntime()} Double tap to reset", displayed:false)
            sendEvent(name:"statusText2", value: "Firmware: v${device.currentValue('currentFirmware')} ${battryInfoDetails}", displayed:false)
        } else {
            //sendEvent(name:"statusText2", value: "Battery: ${getBatteryRuntime()} Double tap to reset", displayed:false)
            sendEvent(name:"statusText2", value: "${battryInfoDetails}", displayed:false)
        }//End if 
        
    } else {
        state.batteryRuntimeStart = now()
    }

    String statusText = ""
    if(device.currentValue('humidity') != null)
        statusText = "RH ${device.currentValue('humidity')}% - "
    if(device.currentValue('illuminance') != null)
        statusText = statusText + "LUX ${device.currentValue('illuminance')} - "
    if(device.currentValue('ultravioletIndex') != null)
        statusText = statusText + "UV ${device.currentValue('ultravioletIndex')} - "
        
    if (statusText != ""){
        statusText = statusText.substring(0, statusText.length() - 2)
        sendEvent(name:"statusText", value: statusText, displayed:false)
    }
} //End - updateStatus

private def logging(message) {
    if (state.enableDebugging == null || state.enableDebugging == "true") log.debug "$message"
}//End - logging



//************** START Command Class ******************//
def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
	state.sec = 1
	def result = []
	
	try{
		//we need to catch payload so short that it does not contain configuration parameter size (NullPointerException)
		//and actual size smaller than indicated by configuration parameter size (IndexOutOfBoundsException)
		if (cmd.payload[1] == 0x70 && cmd.payload[2] == 0x06 && (cmd.payload.size() < 5 || cmd.payload.size < 5 + cmd.payload[4])) {
			logMessage("debug", "Configuration Report command for parameter ${cmd.payload[3]} returned by the device is too short. Retry.")
			sendHubCommand(command(zwave.configurationV1.configurationGet(parameterNumber: cmd.payload[3])))
		} else {
			def encapsulatedCommand = cmd.encapsulatedCommand([0x31: 5, 0x30: 2, 0x84: 1])
			logMessage("debug", "encapsulated: ${encapsulatedCommand}")
			if (encapsulatedCommand) {
				result = zwaveEvent(encapsulatedCommand)
			} else {
				logMessage("warn","Unable to extract encapsulated cmd from $cmd")
				result = createEvent(descriptionText: cmd.toString())
			} //End if 
		}//End if 
		
		result //return
		
	}catch(Exception ex){
		logMessage("error", "zwaveEvent:commands.securityv1.SecurityMessageEncapsulation: " + ex.toString())
	}//End catch
} //End - physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation


def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityCommandsSupportedReport cmd) {
	try{
		
		state.sec = 1
		response(configure())
		
	}catch(Exception ex){
		logMessage("error", "zwaveEvent:commands.securityv1.SecurityCommandsSupportedReport: " + ex.toString())
	}//End catch
} //End - physicalgraph.zwave.commands.securityv1.SecurityCommandsSupportedReport


def zwaveEvent(physicalgraph.zwave.commands.securityv1.NetworkKeyVerify cmd) {
	
	try{
		state.sec = 1
		log.info "Executing zwaveEvent 98 (SecurityV1): 07 (NetworkKeyVerify) with cmd: $cmd (node is securely included)"
		def result = [createEvent(name: "secureInclusion", value: "success", descriptionText: "Secure inclusion was successful", isStateChange: true)]
		result
	}catch(Exception ex){
		logMessage("error", "zwaveEvent:commands.securityv1.NetworkKeyVerify: " + ex.toString())
	}//End catch
}//End - physicalgraph.zwave.commands.securityv1.NetworkKeyVerify


def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
	
	try{
		logMessage("info", "Executing zwaveEvent 72 (ManufacturerSpecificV2) : 05 (ManufacturerSpecificReport) with cmd: $cmd")
		logMessage("debug", "manufacturerId:   ${cmd.manufacturerId}")
		logMessage("debug", "manufacturerName: ${cmd.manufacturerName}")
		logMessage("debug", "productId:        ${cmd.productId}")
		logMessage("debug", "productTypeId:    ${cmd.productTypeId}")
		//def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
	}catch(Exception ex){
		logMessage("error", "zwaveEvent:commands.manufacturerspecificv2.ManufacturerSpecificReport: " + ex.toString())
	}//End catch
}//End - physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport


def zwaveEvent(physicalgraph.zwave.commands.notificationv3.NotificationReport cmd) {
	try{
	
		def result = []
		if (cmd.notificationType == 7) {
			switch (cmd.event) {
				case 0:
					result << createEvent(name: "tamper", value: "clear", descriptionText: "$device.displayName tamper cleared")
					break
				case 3:
					result << createEvent(name: "tamper", value: "detected", descriptionText: "$device.displayName was tampered")
					// Clear the tamper alert after 10s. This is a temporary fix for the tamper attribute until local execution handles it
					unschedule(clearTamper, [forceForLocallyExecuting: true])
					runIn(10, clearTamper, [forceForLocallyExecuting: true])
					break
				case 8:
					result << motionEvent(1)
					break
				default:
					logMessage("error","CASE not found : $cmd : $cmd.notificationType $cmd.event ")
					break
			}//End switch
		} else {
			logMessage("error", "Need to handle this cmd.notificationType: ${cmd.notificationType}")
			result << createEvent(descriptionText: cmd.toString(), isStateChange: false)
		}//End if 
		
		result //return 
		
	}catch(Exception ex){
		logMessage("error", "zwaveEvent:commands.notificationv3.NotificationReport: " + ex.toString())
	}//End catch
} //End physicalgraph.zwave.commands.notificationv3.NotificationReport


def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {

	try{
		def events = []
		def map = [ name: "battery", unit: "%" ]
		
		if (cmd.batteryLevel == 0xFF) {
			map.value = 1
			map.descriptionText = "${device.displayName} battery is low"
			map.isStateChange = true
		} else {
			map.value = cmd.batteryLevel
		}  //end if 
		
		//241 : Battery powered
		//240 : USB powered
		if(settings."101" == null || settings."101" == "241") {
			try {
				setBatteryTitleEvent(map.value)
			} catch (e) {
				logMessage("error", "zwaveEvent:commands.batteryv1.BatteryReport: " + e.toString())
			}//end catch
		} //end if 
		
		events << createEvent(map)
		
		state.lastBatteryReport = now()

		return events
		
	}catch(Exception ex){
		logMessage("error", "zwaveEvent:commands.batteryv1.BatteryReport: " + ex.toString())
	}//End catch
		
} //End - physicalgraph.zwave.commands.batteryv1.BatteryReport


def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv5.SensorMultilevelReport cmd)
{
	try{
		def map = [:]
		switch (cmd.sensorType) {
			case 1:
				map.name = "temperature"
				def cmdScale = cmd.scale == 1 ? "F" : "C"
				state.realTemperature = convertTemperatureIfNeeded(cmd.scaledSensorValue, cmdScale, cmd.precision)
				map.value = getAdjustedTemp(state.realTemperature)
				map.unit = getTemperatureScale()
			    logMessage("debug","Temperature Report: $map.value")
				break;
			case 3:
				map.name = "illuminance"
				state.realLuminance = cmd.scaledSensorValue.toInteger()
				map.value = getAdjustedLuminance(cmd.scaledSensorValue.toInteger())
				map.unit = "lux"
				logMessage("debug","Illuminance Report: $map.value : " + cmd.scaledSensorValue + "   " + cmd)
				break;
			case 5:
				map.name = "humidity"
				state.realHumidity = cmd.scaledSensorValue.toInteger()
				map.value = getAdjustedHumidity(cmd.scaledSensorValue.toInteger())
				map.unit = "%"
			    logMessage("debug","Humidity Report: $map.value")
				break;
			case 27:
				map.name = "ultravioletIndex"
				state.realUV = cmd.scaledSensorValue.toInteger()
				map.value = getAdjustedUV(cmd.scaledSensorValue.toInteger())
				map.unit = ""
			    logMessage("debug","UV Report: $map.value")
				break;
			default:
				map.descriptionText = cmd.toString()
				logMessage("error","No description switch found for : " + cmd.toString())
		} //end switch
		
		def request = update_needed_settings()
		
		if(request != []){
			return [response(commands(request)), createEvent(map)]
		} else {
			return createEvent(map)
		}//end if 

	}catch(Exception ex){
		logMessage("error", "zwaveEvent:commands.sensormultilevelv5.SensorMultilevelReport: " + ex.toString())
	}//End catch
			
} //End command - SensorMultilevelReport


def zwaveEvent(physicalgraph.zwave.commands.configurationv2.ConfigurationReport cmd) {
	try{
		if (cmd.parameterNumber.toInteger() == 81 && cmd.configurationValue == [255]) {
			update_current_properties([parameterNumber: "81", configurationValue: [1]])
			logMessage("info", "Parameter '${cmd.parameterNumber}' with a byte size of '${cmd.size}' is set to '1'")
		} else {
			update_current_properties(cmd)
			logMessage("info", "Parameter '${cmd.parameterNumber}' with a byte size of '${cmd.size}' is set to '${cmd2Integer(cmd.configurationValue)}'")
		} //End if
	
	}catch(Exception ex){
		logMessage("error", "zwaveEvent:commands.configurationv2.ConfigurationReport: " + ex.toString())
	}//End catch
}//End - physicalgraph.zwave.commands.configurationv2.ConfigurationReport


def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpIntervalReport cmd){
	
	try{
		
		logMessage("info", "WakeUpIntervalReport ${cmd.toString()}")
		state.wakeInterval = cmd.seconds
		
	}catch(Exception ex){
		logMessage("error", "zwaveEvent:commands.wakeupv1.WakeUpIntervalReport: " + ex.toString())
	}//End catch
	
}//End - physicalgraph.zwave.commands.wakeupv1.WakeUpIntervalReport 


def zwaveEvent(physicalgraph.zwave.commands.sensorbinaryv2.SensorBinaryReport cmd) {
    try{
		
		logMessage("info", "SensorBinaryReport: $cmd")
		motionEvent(cmd.sensorValue)
		
	}catch(Exception ex){
		logMessage("error", "zwaveEvent:commands.sensorbinaryv2.SensorBinaryReport: " + ex.toString())
	}//End catch
	
}//End - physicalgraph.zwave.commands.sensorbinaryv2.SensorBinaryReport


def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
    try{
		
		logMessage("info", "BasicSet: $cmd")
		motionEvent(cmd.value)
	
	}catch(Exception ex){
		logMessage("error", "zwaveEvent:commands.basicv1.BasicSet: " + ex.toString())
	}//End catch
	
}//End -physicalgraph.zwave.commands.basicv1.BasicSet

//this notification will be sent only when device is battery powered
def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd){
	
	try{
		logMessage("info", "Woke up")
		
		def request = update_needed_settings()

		if(request != []){
		   response(commands(request) + ["delay 5000", zwave.wakeUpV1.wakeUpNoMoreInformation().format()])
		} else {
		   //No commands to send
		   response([zwave.wakeUpV1.wakeUpNoMoreInformation().format()])
		}//end if 
	
	}catch(Exception ex){
		logMessage("error", "zwaveEvent:commands.wakeupv1.WakeUpNotification: " + ex.toString())
	}//End catch
	
}//End -physicalgraph.zwave.commands.wakeupv1.WakeUpNotification


def zwaveEvent(physicalgraph.zwave.commands.versionv1.VersionReport cmd) {
    
	try{

		if(cmd.applicationVersion && cmd.applicationSubVersion) {
			state.rawFW = "${cmd.applicationVersion}.${cmd.applicationSubVersion.toString().padLeft(2,'0')}"
			state.needfwUpdate = "false"
			updateDataValue("firmware", "${state.rawFW}${getOverride()}")
			createEvent(name: "currentFirmware", value: "${state.rawFW}${getOverride()}")
		}//End if 
	}catch(Exception ex){
		logMessage("error", "zwaveEvent:commands.versionv1.VersionReport: " + ex.toString())
	}//End catch

}//End -physicalgraph.zwave.commands.versionv1.VersionReport


def zwaveEvent(physicalgraph.zwave.Command cmd) {
  
  try{
		logMessage("error","Unknown Z-Wave Command: ${cmd.toString()}")
		createEvent(descriptionText: cmd.toString(), isStateChange: false)
	
	}catch(Exception ex){
		logMessage("error", "zwaveEvent:Command " + ex.toString())
	}//End catch
}//End -physicalgraph.zwave.Command

private command(physicalgraph.zwave.Command cmd) {
    try{
		
		if (state.sec && cmd.toString() != "WakeUpIntervalGet()") {
			zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
		} else {
			cmd.format()
		}//end if 
	
	}catch(Exception ex){
		logMessage("error", "physicalgraph.zwave.Command: " + ex.toString())
	}//End catch
	
} //End - command

//************** END Command Class ******************//



//************** START Common Functions ******************//
def logMessage(logLevel, message){
	if (logLevel == "debug"){
		log.debug "$device.displayName: $message"
	}else if (logLevel == "info"){
		log.info "$device.displayName: $message"
	}else if (logLevel == "error"){
		log.error "$device.displayName: $message"
	}else if (logLevel == "warn"){	
		log.warn "$device.displayName: $message"
	} //End if 
}//End - logMessage

private commands(commands, delay=1000) {
	
	try{
		
		delayBetween(commands.collect{ command(it) }, delay)
	
	}catch(Exception ex){
		logMessage("error", "commands(): " + ex.toString())
	}//End catch
} //End - commands


private getAdjustedWake(){
	
	try{
		def wakeValue
		
		if (device.currentValue("currentFirmware") != null && settings."101" != null && settings."111" != null){
			if (device.currentValue("currentFirmware") == "1.08"){
				if (settings."101".toInteger() == 241){   
					if (settings."111".toInteger() <= 3600){
						wakeValue = getRoundedInterval(settings."111")
					} else {
						wakeValue = 3600
					}//end-if
				} else {
					wakeValue = 1800
				}//end-if
			} else {
				if (settings."101".toInteger() == 241){   
					if (settings."111".toInteger() <= 3600){
						wakeValue = getRoundedInterval(settings."111")
					} else {
						wakeValue = getRoundedInterval(settings."111".toInteger() / 2)
					}//end-if
				} else {
					wakeValue = 240
				}//end-if
			}//end-if
		} else {
			wakeValue = 3600
		}//end-if
		
		return wakeValue.toInteger()
		
	}catch(Exception ex){
		logMessage("error", "getAdjustedWake(): " + ex.toString())
	}//End catch

	} //End - getAdjustedWake

private getAdjustedTemp(value) {
    
	try{
		value = Math.round((value as Double) * 100) / 100

		if (settings."201") {
		   return value =  value + Math.round(settings."201" * 100) /100
		} else {
		   return value
		}//end-if
		
	}catch(Exception ex){
		logMessage("error", "getAdjustedTemp(): " + ex.toString())
	}//End catch
	
} //End - getAdjustedTemp

private getAdjustedHumidity(value) {
    try{
		value = Math.round((value as Double) * 100) / 100

		if (settings."202") {
		   return value =  value + Math.round(settings."202" * 100) /100
		} else {
		   return value
		}//end if
		
	}catch(Exception ex){
		logMessage("error", "getAdjustedHumidity(): " + ex.toString())
	}//End catch
} //End - getAdjustedHumidity

private getAdjustedLuminance(value) {
	try{
		value = Math.round((value as Double) * 100) / 100

		if (settings."203") {
		   return value =  value + Math.round(settings."203" * 100) /100
		} else {
		   return value
		}//end if
    }catch(Exception ex){
		logMessage("error", "getAdjustedLuminance(): " + ex.toString())
	}//End catch
} //End - getAdjustedLuminance

private getAdjustedUV(value) {
	try{
		value = Math.round((value as Double) * 100) / 100

		if (settings."204") {
		   return value =  value + Math.round(settings."204" * 100) /100
		} else {
		   return value
		}//end-if
    }catch(Exception ex){
		logMessage("error", "getAdjustedUV(): " + ex.toString())
	}//End catch
} //End - getAdjustedUV


def resetBatteryRuntime() {
	try{
		logMessage("info","state.lastReset: " + state.lastReset)
		logMessage("info", "state.batteryRuntimeStart : " + state.batteryRuntimeStart)
		
		if (state.lastReset != null && now() - state.lastReset < 5000) {
			logMessage("info","Reset Double Press")
			state.batteryRuntimeStart = now()
		}//end if 
		
		state.lastReset = now()
		
	  }catch(Exception ex){
		logMessage("error", "resetBatteryRuntime(): " + ex.toString())
	}//End catch
} //End - resetBatteryRuntime


def motionEvent(value) {
	try{
		def map = [name: "motion"]
		if (value) {
			map.value = "active"
			map.descriptionText = "$device.displayName detected motion"
			logMessage("info", "MOTION ACTIVE")
		} else {
			map.value = "inactive"
			map.descriptionText = "$device.displayName motion has stopped"
			logMessage("info", "MOTION NOT ACTIVE")
		}//end-if
		
		createEvent(map)
		
	}catch(Exception ex){
		logMessage("error", "motionEvent(): " + ex.toString())
	}//End catch
	
}//End - motionEvent


def refresh() {
	
	try{
		logMessage("info", "$device.displayName refresh()")

		def request = []
		if (state.lastRefresh != null && now() - state.lastRefresh < 5000) {
			logMessage("info", "Refresh Double Press")
			state.currentProperties."111" = null
			state.wakeInterval = null
			def configuration = parseXml(configuration_model())
			configuration.Value.each
			{
				if ( "${it.@setting_type}" == "zwave" ) {
					request << zwave.configurationV1.configurationGet(parameterNumber: "${it.@index}".toInteger())
				}//end if 
			} //each 
			request << zwave.versionV1.versionGet()
			request << zwave.wakeUpV1.wakeUpIntervalGet()
		} else {
			request << zwave.batteryV1.batteryGet()
			request << zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType:1, scale:1)
			request << zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType:3, scale:1)
			request << zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType:5, scale:1)
			request << zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType:27, scale:1)
		}

		state.lastRefresh = now()
		
		commands(request)
	
	}catch(Exception ex){
		logMessage("error", "refresh(): " + ex.toString())
	}//End catch
	
}//End - refresh()


//Return battery last changed time in hh:mm:ss
private getBatteryRuntime() {
	
	try{
	   def currentmillis = now() - state.batteryRuntimeStart
	   def days=0
	   def hours=0
	   def mins=0
	   def secs=0
	   secs = (currentmillis/1000).toInteger() 
	   mins=(secs/60).toInteger() 
	   hours=(mins/60).toInteger() 
	   days=(hours/24).toInteger() 
	   secs=(secs-(mins*60)).toString().padLeft(2, '0') 
	   mins=(mins-(hours*60)).toString().padLeft(2, '0') 
	   hours=(hours-(days*24)).toString().padLeft(2, '0') 

		if (days>0) { 
			  return "$days days and $hours:$mins:$secs"
		} else {
			  return "$hours:$mins:$secs"
		}//End if
		
	}catch(Exception ex){
		logMessage("error", "getBatteryRuntime(): " + ex.toString())
	}//End catch
	
} //End - getBatteryRuntime


private getRoundedInterval(number) {
	try{
		double tempDouble = (number / 60)
		if (tempDouble == tempDouble.round())
		   return (tempDouble * 60).toInteger()
		else 
		   return ((tempDouble.round() + 1) * 60).toInteger()
	  }catch(Exception ex){
		logMessage("error", "getRoundedInterval(): " + ex.toString())
	}//End catch
} //End - getRoundedInterval


def cmd2Integer(array) { 
	try {
			switch(array.size()) {
			case 1:
				array[0]
			break
			case 2:
				((array[0] & 0xFF) << 8) | (array[1] & 0xFF)
			break
			case 3:
				((array[0] & 0xFF) << 16) | ((array[1] & 0xFF) << 8) | (array[2] & 0xFF)
			break
			case 4:
				((array[0] & 0xFF) << 24) | ((array[1] & 0xFF) << 16) | ((array[2] & 0xFF) << 8) | (array[3] & 0xFF)
			break
		}//end switch
	}catch(Exception ex){
		logMessage("error", "cmd2Integer(): " + ex.toString())
	}//End catch
	
}//End - cmd2Integer

def integer2Cmd(value, size) {
    try{
		switch(size) {
		case 1:
			[value]
		break
		case 2:
			def short value1   = value & 0xFF
			def short value2 = (value >> 8) & 0xFF
			[value2, value1]
		break
		case 3:
			def short value1   = value & 0xFF
			def short value2 = (value >> 8) & 0xFF
			def short value3 = (value >> 16) & 0xFF
			[value3, value2, value1]
		break
		case 4:
			def short value1 = value & 0xFF
			def short value2 = (value >> 8) & 0xFF
			def short value3 = (value >> 16) & 0xFF
			def short value4 = (value >> 24) & 0xFF
			[value4, value3, value2, value1]
		break
		}//end switch
		
   }catch(Exception ex){
		logMessage("error", "integer2Cmd(): " + ex.toString())
	}//End catch
} //End - integer2Cmd

def resetTamperAlert() {
	try{
		sendEvent(name: "tamper", value: "clear", descriptionText: "$device.displayName tamper cleared")
		sendEvent(name: "acceleration", value: "inactive", descriptionText: "$device.displayName tamper cleared")
		sendEvent(name: "motion", value: "inactive", descriptionText: "$device.displayName motion has stopped")
	}catch(Exception ex){
		logMessage("error", "resetTamperAlert(): " + ex.toString())
	}//End catch
	
} //End - resetTamperAlert

def convertParam(number, value) {
	try{
		switch (number){
			case 41:
				//Parameter difference between firmware versions
				if (settings."41".toInteger() != null && device.currentValue("currentFirmware") != null) {
					if (device.currentValue("currentFirmware") == "1.07" || device.currentValue("currentFirmware") == "1.08" || device.currentValue("currentFirmware") == "1.09") {
						(value * 256) + 2
					} else if (device.currentValue("currentFirmware") == "1.10") {
						(value * 65536) + 512
					} else if (device.currentValue("currentFirmware") == "1.10EU" || device.currentValue("currentFirmware") == "1.11EU") {
						(value * 65536) + 256
					} else if (device.currentValue("currentFirmware") == "1.07EU" || device.currentValue("currentFirmware") == "1.08EU" || device.currentValue("currentFirmware") == "1.09EU") {
						(value * 256) + 1
					} else {
						value
					}	
				} else {
					value
				}
			break
			case 45:
				//Parameter difference between firmware versions
				if (settings."45".toInteger() != null && device.currentValue("currentFirmware") != null && device.currentValue("currentFirmware") != "1.08")
					2
				else
					value
			break
			case 101:
				if (settings."40".toInteger() != null) {
					if (settings."40".toInteger() == 1) {
					   0
					} else {
					   value
					}	
				} else {
					241
				}
			break
			case 201:
				if (value < 0)
					256 + value
				else if (value > 100)
					value - 256
				else
					value
			break
			case 202:
				if (value < 0)
					256 + value
				else if (value > 100)
					value - 256
				else
					value
			break
			case 203:
				if (value < 0)
					65536 + value
				else if (value > 1000)
					value - 65536
				else
					value
			break
			case 204:
				if (value < 0)
					256 + value
				else if (value > 100)
					value - 256
				else
					value
			break
			default:
				value
			break
		} //end switch
		
	}catch(Exception ex){
		logMessage("error", "convertParam(): " + ex.toString())
	}//End catch
}//End - convertParam

//************** END Common Functions ******************//



def configuration_model()
{
'''
<configuration>
    <Value type="list" index="101" label="Battery or USB?" min="240" max="241" value="241" byteSize="4" setting_type="zwave" fw="1.06,1.07,1.08,1.09,1.10,1.06EU,1.07EU,1.08EU,1.09EU,1.10EU,1.11EU" displayDuringSetup="true">
    <Help>
Is the device powered by battery or usb?
    </Help>
        <Item label="Battery" value="241" />
        <Item label="USB" value="240" />
  </Value>
  <Value type="list" index="40" label="Enable selective reporting?" min="0" max="1" value="0" byteSize="1" setting_type="zwave" fw="1.06,1.07,1.08,1.09,1.10,1.06EU,1.07EU,1.08EU,1.09EU,1.10EU,1.11EU">
    <Help>
Enable/disable the selective reporting only when measurements reach a certain threshold or percentage set below. This is used to reduce network traffic.
Default: No (Enable for Better Battery Life)
    </Help>
        <Item label="No" value="0" />
        <Item label="Yes" value="1" />
  </Value>
  <Value type="short" byteSize="2" index="41" label="Temperature Threshold" min="1" max="2120" value="20" setting_type="zwave" fw="1.06,1.07,1.08,1.09,1.10,1.06EU,1.07EU,1.08EU,1.09EU,1.10EU,1.11EU">
    <Help>
Threshold change in temperature to induce an automatic report. 
Range: 1~2120 (Firmware 1.09+ 10~2120)
Default: 20
Note:
Only used if selective reporting is enabled.
1. The unit is Fahrenheit for US version, Celsius for EU/AU version.
2. The value contains one decimal point. E.g. if the value is set to 20, the threshold value =2.0 ℃ (EU/AU version) or 2.0 ℉ (US version). When the current temperature gap is more then 2.0, which will induce a temperature report to be sent out.
    </Help>
  </Value>
  <Value type="byte" byteSize="1" index="42" label="Humidity Threshold" min="1" max="255" value="10" setting_type="zwave" fw="1.06,1.07,1.08,1.09,1.10,1.06EU,1.07EU,1.08EU,1.09EU,1.10EU,1.11EU">
    <Help>
Threshold change in humidity to induce an automatic report.
Range: 1~255.
Default: 10
Note:
Only used if selective reporting is enabled.
1. The unit is %.
2. The default value is 10, which means that if the current humidity gap is more than 10%, it will send out a humidity report.
    </Help>
  </Value>
  <Value type="short" byteSize="2" index="43" label="Luminance Threshold" min="1" max="30000" value="100" setting_type="zwave" fw="1.06,1.07,1.08,1.09,1.10,1.06EU,1.07EU,1.08EU,1.09EU,1.10EU,1.11EU">
    <Help>
Threshold change in luminance to induce an automatic report.
Range: 1~30000.
Default: 100
Note:
Only used if selective reporting is enabled.
    </Help>
  </Value>
  <Value type="byte" byteSize="1" index="44" label="Battery Threshold" min="1" max="99" value="10" setting_type="zwave" fw="1.06,1.07,1.08,1.09,1.10,1.06EU,1.07EU,1.08EU,1.09EU,1.10EU,1.11EU">
    <Help>
Threshold change in battery level to induce an automatic report.
Range: 1~99.
Default: 10
Note:
Only used if selective reporting is enabled.
1. The unit is %.
2. The default value is 10, which means that if the current battery level gap is more than 10%, it will send out a battery report.
    </Help>
  </Value>
  <Value type="byte" byteSize="1" index="45" label="Ultraviolet Threshold" min="1" max="11" value="2" setting_type="zwave" fw="1.06,1.07,1.08,1.09,1.10,1.06EU,1.07EU,1.08EU,1.09EU,1.10EU,1.11EU">
    <Help>
Threshold change in ultraviolet to induce an automatic report.
Range: 1~11.
Default: 2
Note: Firmware 1.06 and 1.07 only support a value of 2.
Only used if selective reporting is enabled.
    </Help>
  </Value>
  <Value type="short" byteSize="2" index="3" label="PIR reset time" min="10" max="3600" value="240" setting_type="zwave" fw="1.06,1.07,1.08,1.09,1.10,1.06EU,1.07EU,1.08EU,1.09EU,1.10EU,1.11EU" displayDuringSetup="true">
    <Help>
Number of seconds to wait to report motion cleared after a motion event if there is no motion detected.
Range: 10~3600.
Default: 240 (4 minutes)
Note:
(1), The time unit is seconds if the value range is in 10 to 255.
(2), If the value range is in 256 to 3600, the time unit will be minute and its value should follow the below rules:
a), Interval time =Value/60, if the interval time can be divided by 60 and without remainder.
b), Interval time= (Value/60) +1, if the interval time can be divided by 60 and has remainder.
    </Help>
  </Value>
    <Value type="byte" byteSize="1" index="4" label="PIR motion sensitivity" min="0" max="5" value="5" setting_type="zwave" fw="1.06,1.07,1.08,1.09,1.10,1.06EU,1.07EU,1.08EU,1.09EU,1.10EU,1.11EU" displayDuringSetup="true">
    <Help>
A value from 0-5, from disabled to high sensitivity
Range: 0~5
Default: 5
    </Help>
  </Value>
    <Value type="byte" byteSize="4" index="111" label="Reporting Interval" min="5" max="2678400" value="3600" setting_type="zwave" fw="1.06,1.07,1.08,1.09,1.10,1.06EU,1.07EU,1.08EU,1.09EU,1.10EU,1.11EU" displayDuringSetup="true">
    <Help>
The interval time of sending reports in Report group 1
Range: 30~
Default: 3600 seconds
Note:
The unit of interval time is in seconds. Minimum interval time is 30 seconds when USB powered and 240 seconds (4 minutes) when battery powered.
    </Help>
  </Value>
  <Value type="decimal" byteSize="1" index="201" label="Temperature offset" min="*" max="*" value="">
    <Help>
Range: None
Default: 0
Note: 
1. The calibration value = standard value - measure value.
E.g. If measure value =85.3F and the standard value = 83.2F, so the calibration value = 83.2F - 85.3F = -2.1F.
If the measure value =60.1F and the standard value = 63.2F, so the calibration value = 63.2F - 60.1℃ = 3.1F. 
    </Help>
  </Value>
  <Value type="byte" byteSize="1" index="202" label="Humidity offset" min="*" max="*" value="">
    <Help>
Range: None
Default: 0
Note:
The calibration value = standard value - measure value.
E.g. If measure value = 80RH and the standard value = 75RH, so the calibration value = 75RH – 80RH = -5RH.
If the measure value = 85RH and the standard value = 90RH, so the calibration value = 90RH – 85RH = 5RH. 
    </Help>
  </Value>
    <Value type="byte" byteSize="2" index="203" label="Luminance offset" min="*" max="*" value="">
    <Help>
Range: None
Default: 0
Note:
The calibration value = standard value - measure value.
E.g. If measure value = 800Lux and the standard value = 750Lux, so the calibration value = 750 – 800 = -50.
If the measure value = 850Lux and the standard value = 900Lux, so the calibration value = 900 – 850 = 50.
    </Help>
  </Value>
    <Value type="byte" byteSize="1" index="204" label="Ultraviolet offset" min="*" max="*" value="">
    <Help>
Range: None
Default: 0
Note:
The calibration value = standard value - measure value.
E.g. If measure value = 9 and the standard value = 8, so the calibration value = 8 – 9 = -1.
If the measure value = 7 and the standard value = 9, so the calibration value = 9 – 7 = 2. 
    </Help>
  </Value>
  <Value type="list" index="5" label="Command Option" min="1" max="2" value="1" byteSize="1" setting_type="zwave" fw="1.06,1.07,1.08,1.09,1.10,1.06EU,1.07EU,1.08EU,1.09EU,1.10EU,1.11EU">
    <Help>
Which command should be sent when the motion sensor is triggered
Default: Basic Set
    </Help>
        <Item label="Basic Set" value="1" />
        <Item label="Sensor Binary" value="2" />
  </Value>
  <Value type="list" index="81" label="LED Options" min="0" max="1" value="0" byteSize="1" setting_type="zwave" fw="1.08,1.09,1.10,1.08EU,1.09EU,1.10EU,1.11EU">
    <Help>
Choose how the LED functions. (Option 1, 2 firmware v1.08+, Option 1, 2, 3 firmware v1.10+)
Default: Enabled
    </Help>
        <Item label="Fully Enabled" value="0" />
        <Item label="Disable When Motion" value="1" />
        <Item label="Fully Disabled" value="2" />
  </Value>
  <Value type="byte" index="8" label="Stay Awake Time?" min="8" max="255" value="30" byteSize="1" setting_type="zwave" fw="1.08,1.09,1.10,1.08EU,1.09EU,1.10EU,1.11EU">
    <Help>
Set the timeout of awake after the Wake Up CC is sent out. (Works on Firmware v1.08 only)
Range: 8~255
Default: 30
Note: May help if config parameters aren't making it before device goes back to sleep.
    </Help>
  </Value>
<Value type="list" index="regionOverride" label="Region Override" min="0" max="3" value="0" setting_type="preference" fw="1.06,1.07,1.08,1.09,1.10,1.06EU,1.07EU,1.08EU,1.09EU,1.10EU,1.11EU">
    <Help>
The device handler tries to automatically detect the region of your firmware (US, EU, AU). If it is detecting it incorrectly you can change it here.
Default: Off
    </Help>
        <Item label="Off" value="0" />
        <Item label="US" value="1" />
        <Item label="EU" value="2" />
        <Item label="AU" value="3" />
  </Value>
  <Value type="boolean" index="enableDebugging" label="Enable Debug Logging?" value="true" setting_type="preference" fw="1.06,1.07,1.08,1.09,1.10,1.06EU,1.07EU,1.08EU,1.09EU,1.10EU,1.11EU">
    <Help>

    </Help>
  </Value>
</configuration>
'''
}