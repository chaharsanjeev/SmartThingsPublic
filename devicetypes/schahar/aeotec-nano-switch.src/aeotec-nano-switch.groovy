metadata {
	definition (name: "Aeotec Nano Switch", namespace: "SCHAHAR", author: "SCHAHAR", ocfDeviceType: "oic.d.switch", minHubCoreVersion: '000.017.0012', executeCommandsLocally: true) {
		capability "Energy Meter"
		capability "Actuator"
		capability "Switch"
		capability "Power Meter"
		capability "Refresh"
		capability "Configuration"
		capability "Sensor"
		capability "Light"
		capability "Health Check"


		command "reset"
		
        // Custom Attributes:
        attribute "syncPending", "number" // Number of config items that need to be synced with the physical device.

		fingerprint inClusters: "0x25,0x32"
		fingerprint mfr: "0086", prod: "0003", model: "0012", deviceJoinName: "Aeotec Micro Smart Switch"
		fingerprint mfr: "0086", prod: "0103", model: "0060", deviceJoinName: "Aeotec Smart Switch 6"  //US
		fingerprint mfr: "0086", prod: "0003", model: "0060", deviceJoinName: "Aeotec Smart Switch 6"  //EU
		fingerprint mfr: "0086", prod: "0103", model: "0074", deviceJoinName: "Aeotec Nano Switch"
		fingerprint mfr: "0086", prod: "0003", model: "0074", deviceJoinName: "Aeotec Nano Switch"
	} //end defination

	// simulator metadata
	simulator {
		status "on":  "command: 2003, payload: FF"
		status "off": "command: 2003, payload: 00"

		for (int i = 0; i <= 10000; i += 1000) {
			status "power  ${i} W": new physicalgraph.zwave.Zwave().meterV1.meterReport(
				scaledMeterValue: i, precision: 3, meterType: 4, scale: 2, size: 4).incomingMessage()
		}
		for (int i = 0; i <= 100; i += 10) {
			status "energy	${i} kWh": new physicalgraph.zwave.Zwave().meterV1.meterReport(
			   scaledMeterValue: i, precision: 3, meterType: 0, scale: 0, size: 4).incomingMessage()
		}

		// reply messages
		reply "2001FF,delay 100,2502": "command: 2503, payload: FF"
		reply "200100,delay 100,2502": "command: 2503, payload: 00"
	}//end simulator

	// tile definitions
	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
				attributeState("on", label: 'On', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#44b621", nextState:"turningOff") //green color
				attributeState("off", label: 'Off', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#fff", nextState:"turningOn") //white color
				attributeState("turningOn", label:'Turning On', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#44b621", nextState:"turningOff")// green color
				attributeState("turningOff", label:'Turning Off', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#f1d801", nextState:"turningOn") // yellow color
			} //End tileAttribute
		}//End multiAttributeTile
     
		valueTile("power", "device.power", width: 2, height: 2) {
			state "default", label:'Power: ${currentValue} W'
		}  //end standardTile
        
		valueTile("energy", "device.energy", width: 2, height: 2) {
			state "default", label:'Energy: ${currentValue} kWh'
		} //end standardTile
        
        valueTile("voltage", "device.voltage", width: 2, height: 2) {
			state "default", label:'Voltage: ${currentValue} V'
		} //end standardTile
        
        valueTile("current", "device.current", width: 2, height: 2) {
			state "current", label:'Current: ${currentValue} A'
		} //end standardTile
        
		standardTile("reset", "device.energy", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'Reset Energy', action:"reset"
		}  //end standardTile
        
		standardTile("refresh", "device.power", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		} //end standardTile
		
       standardTile("syncPending", "syncPending", decoration: "flat", width: 2, height: 2) {
            state "default", label:'Sync Pending', backgroundColor:"#FF6600", action:"sync", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x2_cycle.png"
            state "0", label:'Synced', backgroundColor:"#79b821", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x2_tick.png"
        }//end standardTile
              
		main(["switch"])
		details(["switch","power","current","voltage","energy","reset","syncPending","refresh"])
	}//end tiles
    
     preferences {
           input("device_S1_mode","enum", title: "S1 Switch Mode",description: "Select Mode for S1", defaultValue: "2-State Switch Mode", options: ["2-State Switch Mode","3-Way Switch Mode","Momentary Button Mode"], required: true, displayDuringSetup: true)
           input("device_S2_mode","enum", title: "S2 Switch Mode",description: "Select Mode for S2", defaultValue: "2-State Switch Mode", options: ["2-State Switch Mode","3-Way Switch Mode","Momentary Button Mode"], required: true, displayDuringSetup: true)
		   input "device_enableDebug", "bool", required: true,title: "Enable Debug Log?" ,description: "Enabled log messages for debug", defaultValue:false, displayDuringSetup: true
       } //End preferences
       
} //end metadata

private def logging(message) {
    if (device_enableDebug == null || device_enableDebug == "true") log.debug "$message"
}//end if logging

def installed() {
	try{
    
    	logging "installed()"
    
        // Device-Watch simply pings if no device events received for 32min(checkInterval)
        initialize()

    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:installed() Exception: " + ex
    }//End catch
    
} //End installed


def updated() {
	logging "updated()"
    
	// Device-Watch simply pings if no device events received for 32min(checkInterval)
	initialize()
    
	//if (zwaveInfo?.mfr?.equals("0063") || zwaveInfo?.mfr?.equals("014F")) { // These old GE devices have to be polled. GoControl Plug refresh status every 15 min.
	//	unschedule("poll", [forceForLocallyExecuting: true])
	//	runEvery15Minutes("poll", [forceForLocallyExecuting: true])
	//}
    
	try {
		if (!state.MSR) {response(zwave.manufacturerSpecificV2.manufacturerSpecificGet().format())}
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:updated() Exception: " + ex
    }//End catch
    
} //end updated

def initialize() {

	try{
    	logging "initialize()"
		sendEvent(name: "checkInterval", value: 2 * 15 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID])
    	
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:initialize() Exception: " + ex
    }//End catch
    
} //End initialize

def getCommandClassVersions() {
	
    [
		0x20: 1,  // Basic
		0x32: 3,  // Meter
		0x56: 1,  // Crc16Encap
		0x70: 1,  // Configuration
		0x72: 2,  // ManufacturerSpecific
	]
    
} //End getCommandClassVersions

// parse events into attributes
def parse(String description) {
    try{    
        logging ("parse() - description: "+description)

        def result = null
        if (description != "updated") {
            def cmd = zwave.parse(description, commandClassVersions)
            if (cmd) {
                result = zwaveEvent(cmd)
                if (device_enableDebug == true){log.debug("'$description' parsed to $result")}
            } else {
                if (device_enableDebug == true){log.debug("Couldn't zwave.parse '$description'")}
            }//end if 
        }//end if 

        result
		
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:parse() Exception: " + ex
    }//End catch
    
} //End parse

def handleMeterReport(cmd){
	logging "Meter Report: $cmd"
    
	try{        
        if (cmd.meterType == 1)/* Electric meter:*/ {
            if (cmd.scale == 0) /* Accumulated Energy (kWh):*/ {
                createEvent(name: "energy", value: cmd.scaledMeterValue, unit: "kWh")
            } else if (cmd.scale == 1) /* Accumulated Energy (kVAh):*/ {
                createEvent(name: "energy", value: cmd.scaledMeterValue, unit: "kVAh")
            } else if (cmd.scale == 2)/* Instantaneous Power (Watts) */ {
                createEvent(name: "power", value: Math.round(cmd.scaledMeterValue), unit: "W")
            //} else if (cmd.scale == 3)/* Accumulated Pulse Count */ {
            //	 createEvent(name: "pulse", value: cmd.scaledMeterValue, unit: "")
            } else if (cmd.scale == 4)/* Instantaneous Voltage (Volts) */ {
             	createEvent(name: "voltage", value: Math.round(cmd.scaledMeterValue), unit: "V")
            } else if (cmd.scale == 5)/* Instantaneous Current (Amps) */ {
                createEvent(name: "current", value: cmd.scaledMeterValue, unit: "A")
            //} else if (cmd.scale == 6)/* Instantaneous Power Factor */ {
            //	createEvent(name: "powerfactor", value: cmd.scaledMeterValue, unit: "")
            }else{
            	log.error "handleMeterReport - Unknown scale - $cmd"
            }//end if 
        }else{
        	log.error "handleMeterReport - Unknown meterType - $cmd"
        } //end if 
	
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:handleMeterReport() Exception: " + ex
    }//End catch
    
} //End handleMeterReport

def on() {
	try{
		encapSequence([zwave.basicV1.basicSet(value: 0xFF),zwave.switchBinaryV1.switchBinaryGet(),meterGet(scale: 2)], 3000)
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:on() Exception: " + ex
    }//End catch
}//End - on


def off() {
	try{
		encapSequence([	zwave.basicV1.basicSet(value: 0x00),zwave.switchBinaryV1.switchBinaryGet(),meterGet(scale: 2)], 3000)
	} catch(Exception ex) {
    	log.error "[Device name: $device]: Function:off() Exception: " + ex
    }//End catch
    
}//End - off

def ping() {
	try{
    
        logging( "ping()")
        refresh()
	
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:ping() Exception: " + ex
    }//End catch
    
}//End - ping

def poll() {
	try{
		sendHubCommand(refresh())
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:poll() Exception: " + ex
    }//End catch
    
}//End - poll

def refresh() {
	try{
            logging ("refresh()")
            encapSequence([zwave.switchBinaryV1.switchBinaryGet(),meterGet(scale: 0),meterGet(scale: 2)])
   	
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:refresh() Exception: " + ex
    }//End catch
             
}//End - refresh

def configure() {
	try{
            logging ("configure()")
            
            def result = []

            logging ( "Configure zwaveInfo: "+zwaveInfo)

            if (zwaveInfo.mfr == "0086") {	// Aeon Labs meter
            	//To set which notification would be sent to the associated nodes in association group 1 when the state of output load is changed. 
                //0 = Nothing ; 1 = Hail CC  ; 2 = Basic Report CC   ; 3 = Hail CC when using the external switch to switch the loads. 
                result << response(encap(zwave.configurationV1.configurationSet(parameterNumber: 80, size: 1, scaledConfigurationValue: 2)))	// basic report cc
                
                //To set which report would be sent in Report group 1 (See flags in table below). 
                result << response(encap(zwave.configurationV1.configurationSet(parameterNumber: 101, size: 4, scaledConfigurationValue: 12)))	// report power in watts
                
                
                result << response(encap(zwave.configurationV1.configurationSet(parameterNumber: 111, size: 4, scaledConfigurationValue: 300)))	 // every 5 min
            //} else if (zwaveInfo.mfr == "010F" && zwaveInfo.prod == "1801" && zwaveInfo.model == "1000") { // Fibaro Wall Plug UK
            //    result << response(encap(zwave.configurationV1.configurationSet(parameterNumber: 11, size: 1, scaledConfigurationValue: 2))) // 2% power change results in report
            //    result << response(encap(zwave.configurationV1.configurationSet(parameterNumber: 13, size: 2, scaledConfigurationValue: 5*60))) // report every 5 minutes
            //} else if (zwaveInfo.mfr == "014F" && zwaveInfo.prod == "5053" && zwaveInfo.model == "3531") {
            //    result << response(encap(zwave.configurationV1.configurationSet(parameterNumber: 13, size: 2, scaledConfigurationValue: 15))) //report kWH every 15 min
            }
            result << response(encap(meterGet(scale: 0)))
            result << response(encap(meterGet(scale: 2)))
            result
     	
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:configure() Exception: " + ex
    }//End catch
            
}//End - configure

def reset() {
	try{
		encapSequence([meterReset(),meterGet(scale: 0)])
	} catch(Exception ex) {
    	log.error "[Device name: $device]: Function:reset() Exception: " + ex
    }//End catch
    
}//End - reset

def meterGet(map){
	try{
		return zwave.meterV2.meterGet(map)
   	} catch(Exception ex) {
    	log.error "[Device name: $device]: Function:meterGet() Exception: " + ex
    }//End catch
    
}//End -  meterGet

def meterReset(){
	try{
		return zwave.meterV2.meterReset()
   	} catch(Exception ex) {
    	log.error "[Device name: $device]: Function:meterReset() Exception: " + ex
    }//End catch
    
}//End - meterReset



private encapSequence(cmds, Integer delay=250) {
	try{
		delayBetween(cmds.collect{ encap(it) }, delay)
    } catch(Exception ex) {
    	log.error "[Device name: $device]: Function:encapSequence() Exception: " + ex
    }//End catch
    
}//End - encapSequence



def zwaveEvent(physicalgraph.zwave.commands.meterv3.MeterReport cmd) {
	try{
        logging ( "v3 Meter report: "+cmd)
        handleMeterReport(cmd)
		
    } catch(Exception ex) {
    	log.error "[Device name: $device]: commands.meterv3.MeterReport Exception: " + ex
    }//End catch
    
}//End - physicalgraph.zwave.commands.meterv3.MeterReport

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd){
	try{
        logging ("Basic report: "+cmd)

        def value = (cmd.value ? "on" : "off")
        def evt = createEvent(name: "switch", value: value, type: "physical", descriptionText: "$device.displayName was turned $value")
        if (evt.isStateChange) {
            [evt, response(["delay 3000", meterGet(scale: 2).format()])]
        } else {
            evt
        }//end if 
		
    } catch(Exception ex) {
    	log.error "[Device name: $device]: commands.basicv1.BasicReport Exception: " + ex
    }//End catch
    
}//End - basicv1.BasicReport

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd){
    try{
    	logging ( "Switch binary report: "+cmd)

        def value = (cmd.value ? "on" : "off")
        createEvent(name: "switch", value: value, type: "digital", descriptionText: "$device.displayName was turned $value")
		
    } catch(Exception ex) {
    	log.error "[Device name: $device]: commands.switchbinaryv1.SwitchBinaryReport Exception: " + ex
    }//End catch
    
}//End - switchbinaryv1.SwitchBinaryReport

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
    try{
           def result = []

            def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
            logging ( "msr: $msr")
            
            updateDataValue("MSR", msr)

            result << createEvent(descriptionText: "$device.displayName MSR: $msr", isStateChange: false)
       	
    } catch(Exception ex) {
    	log.error "[Device name: $device]: commands.manufacturerspecificv2.ManufacturerSpecificReport Exception: " + ex
    }//End catch
    
}//End - manufacturerspecificv2.ManufacturerSpecificReport

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	log.error "$device.displayName: Unhandled: $cmd"
	[:]
}//End - physicalgraph.zwave.Command

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
	try{
        def encapsulatedCommand = cmd.encapsulatedCommand(commandClassVersions)
        if (encapsulatedCommand) {
            logging ( "Parsed SecurityMessageEncapsulation into: ${encapsulatedCommand}")
            zwaveEvent(encapsulatedCommand)
        } else {
            log.error "Unable to extract Secure command from $cmd"
        }//end if 
		
    } catch(Exception ex) {
    	log.error "[Device name: $device]: commands.securityv1.SecurityMessageEncapsulation Exception: " + ex
    }//End catch
    
}//End - zwave.commands.securityv1.SecurityMessageEncapsulation

def zwaveEvent(physicalgraph.zwave.commands.crc16encapv1.Crc16Encap cmd) {
	try{
		def version = commandClassVersions[cmd.commandClass as Integer]
        def ccObj = version ? zwave.commandClass(cmd.commandClass, version) : zwave.commandClass(cmd.commandClass)
        def encapsulatedCommand = ccObj?.command(cmd.command)?.parse(cmd.data)

        if (encapsulatedCommand) {
            logging ( "Parsed Crc16Encap into: ${encapsulatedCommand}")
            zwaveEvent(encapsulatedCommand)
        } else {
            log.error "Unable to extract CRC16 command from $cmd"
        }//end if 
		
    } catch(Exception ex) {
    	log.error "[Device name: $device]: commands.crc16encapv1.Crc16Encap Exception: " + ex
    }//End catch
    
}//End - physicalgraph.zwave.commands.crc16encapv1.Crc16Encap


private secEncap(physicalgraph.zwave.Command cmd) {
	try{
		logging ( "encapsulating command using Secure Encapsulation, command: $cmd")
		zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
    } catch(Exception ex) {
    	log.error "[Device name: $device]: secEncap:zwave.Command Exception: " + ex
    }//End catch
    
}//End - secEncap

private crcEncap(physicalgraph.zwave.Command cmd) {
	try{
         logging ("encapsulating command using CRC16 Encapsulation, command: $cmd")
         zwave.crc16EncapV1.crc16Encap().encapsulate(cmd).format()
  } catch(Exception ex) {
    	log.error "[Device name: $device]: crcEncap:zwave.Command Exception: " + ex
    }//End catch
    
}//End - crcEncap

private encap(physicalgraph.zwave.Command cmd) {
	try{
            if (zwaveInfo?.zw?.contains("s")) {
                secEncap(cmd)
            } else if (zwaveInfo?.cc?.contains("56")){
                crcEncap(cmd)
            } else {
                logging ( "no encapsulation supported for command: $cmd")
                cmd.format()
            }//end if 
    	
    } catch(Exception ex) {
    	log.error "[Device name: $device]: encap:zwave.Command Exception: " + ex
    }//End catch
 }//End - encap