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
 *	Button Controller
 *
 *	Author: SmartThings
 *	Date: 2014-5-21
 */
definition(
    name: "Button Controller",
    namespace: "SCHAHAR",
    author: "Sanjeev Chahar",
    description: "Aeon WAllmote Quad",
    category: "Convenience",
    iconUrl: "https://www.home4u-shop.de/media/image/07/8b/b7/aeoezw130_600x600.jpg" , //"https://s3.amazonaws.com/smartapp-icons/MyApps/Cat-MyApps.png",
    iconX2Url: "https://www.home4u-shop.de/media/image/07/8b/b7/aeoezw130_600x600.jpg", //"https://s3.amazonaws.com/smartapp-icons/MyApps/Cat-MyApps@2x.png",
    pausable: true
)

preferences {
	
    page(name: "selectButton")
	for (def i=1; i<=8; i++) {page(name: "configureButton$i")}

	page(name: "timeIntervalInput", title: "Only during a certain time") {
		section {
			input "starting", "time", title: "Starting", required: false
			input "ending", "time", title: "Ending", required: false
		}
	}
	
	 	
}

def selectButton() {
	dynamicPage(name: "selectButton", title: "First, select your button device", nextPage: "configureButton1", uninstall: configured()) {
		section {
			input "buttonDevice", "capability.button", title: "Button", multiple: false, required: true
		}

		 section("SmartApp Name") {
      	  label(name: "input_rulename",title: "Enter SmartApp Name",description: "SmartApp Name..",required: true, image:null)
    	}//End section
   
		section("Debug") {
             input(name: "input_enableLog", type: "bool", title: "Enable debug Logs?",description:null,multiple: false,required:true,submitOnChange:false, defaultValue:false)
		} //End section
	
		section("Button Click Interval") {
             input(name: "input_buttonClickInterval", type: "enum", title: "Minimum button click interval?",description:"Minimum time interval between clicks",multiple: false,required:true,submitOnChange:false, defaultValue:"2 seconds", options: ["0 Second","1 Second", "2 Seconds", "3 Seconds", "4 Seconds", "5 Seconds", "6 Seconds", "7 Seconds"] )
		} //End section
	
    
		section(title: "More options", hidden: hideOptionsSection(), hideable: true) {

			def timeLabel = timeIntervalLabel()

			href "timeIntervalInput", title: "Only during a certain time", description: timeLabel ?: "Tap to set", state: timeLabel ? "complete" : null

			input "days", "enum", title: "Only on certain days of the week", multiple: true, required: false,
				options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]

			input "modes", "mode", title: "Only when mode is", multiple: true, required: false
		}
	}
}

def createPage(pageNum) {
	if ((state.numButton == pageNum) || (pageNum == 8))
		state.installCondition = true
		dynamicPage(name: "configureButton$pageNum", title: "Set up button $pageNum here",
		nextPage: "configureButton${pageNum+1}", install: state.installCondition, uninstall: configured(), getButtonSections(pageNum))
}

def configureButton1() {
	state.numButton = buttonDevice.currentState("numberOfButtons")?.longValue ?: 4
	if (input_enableLog == true){log.debug "state variable numButton: ${state.numButton}"}
	state.installCondition = false
	createPage(1)
}
def configureButton2() {
	createPage(2)
}

def configureButton3() {
	createPage(3)
}

def configureButton4() {
	createPage(4)
}

def configureButton5() {
	createPage(5)
}

def configureButton6() {
	createPage(6)
}

def configureButton7() {
	createPage(7)
}

def configureButton8() {
	createPage(8)
}

def getButtonSections(buttonNumber) {
	return {
		section("Lights") {
			input "lights_${buttonNumber}_pushed", "capability.switch", title: "Pushed", multiple: true, required: false
			input "lights_${buttonNumber}_held", "capability.switch", title: "Held", multiple: true, required: false
		}
		section("Locks") {
			input "locks_${buttonNumber}_pushed", "capability.lock", title: "Pushed", multiple: true, required: false
			input "locks_${buttonNumber}_held", "capability.lock", title: "Held", multiple: true, required: false
		}
		section("Sonos") {
			input "sonos_${buttonNumber}_pushed", "capability.musicPlayer", title: "Pushed", multiple: true, required: false
			input "sonos_${buttonNumber}_held", "capability.musicPlayer", title: "Held", multiple: true, required: false
		}
		section("Modes") {
			input "mode_${buttonNumber}_pushed", "mode", title: "Pushed", required: false
			input "mode_${buttonNumber}_held", "mode", title: "Held", required: false
		}
		def phrases = location.helloHome?.getPhrases()*.label
		if (phrases) {
			section("Hello Home Actions") {
				if (input_enableLog == true){log.trace phrases}
				input "phrase_${buttonNumber}_pushed", "enum", title: "Pushed", required: false, options: phrases
				input "phrase_${buttonNumber}_held", "enum", title: "Held", required: false, options: phrases
			}
		}
        section("Sirens") {
            input "sirens_${buttonNumber}_pushed","capability.alarm" ,title: "Pushed", multiple: true, required: false
            input "sirens_${buttonNumber}_held", "capability.alarm", title: "Held", multiple: true, required: false
        }

		section("Custom Message") {
			input "textMessage_${buttonNumber}", "text", title: "Message", required: false
		}

        section("Push Notifications") {
            input "notifications_${buttonNumber}_pushed","bool" ,title: "Pushed", required: false, defaultValue: false
            input "notifications_${buttonNumber}_held", "bool", title: "Held", required: false, defaultValue: false
        }

        section("Sms Notifications") {
            input "phone_${buttonNumber}_pushed","phone" ,title: "Pushed", required: false
            input "phone_${buttonNumber}_held", "phone", title: "Held", required: false
        }
	}
}

def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(buttonDevice, "button", buttonEvent)
}

def configured() {
	return buttonDevice || buttonConfigured(1) || buttonConfigured(2) || buttonConfigured(3) || buttonConfigured(4)
}

def buttonConfigured(idx) {
	return settings["lights_$idx_pushed"] ||
		settings["locks_$idx_pushed"] ||
		settings["sonos_$idx_pushed"] ||
		settings["mode_$idx_pushed"] ||
        settings["notifications_$idx_pushed"] ||
        settings["sirens_$idx_pushed"] ||
        settings["notifications_$idx_pushed"]   ||
        settings["phone_$idx_pushed"]
}

def buttonEvent(evt){
	if(allOk) {
		def buttonNumber = evt.data // why doesn't jsonData work? always returning [:]
		def value = evt.value
		
		if (input_enableLog == true){
			log.debug "buttonEvent: $evt.name = $evt.value ($evt.data)"
			log.debug "button: $buttonNumber, value: $value"
		}
		
		
		def gap_interval = Integer.valueOf(input_buttonClickInterval.substring(0, input_buttonClickInterval.lastIndexOf(" ")).trim()) * 1000
		boolean performAction = true
        
        if (gap_interval <=0){
        	performAction = true
        }else{
        	def recentEvents = buttonDevice.eventsSince(new Date(now() - gap_interval)).findAll{it.value == evt.value && it.data == evt.data}
          	if (input_enableLog == true){
        		log.debug "Gap Interval: $gap_interval"
        		log.debug "Found ${recentEvents.size()} events in past $input_buttonClickInterval"
        	}//end if
            
            if(recentEvents.size <= 1){performAction = true}else{performAction = false}//endif
            
        }//end if
    
		//if(recentEvents.size <= 1){
		if (performAction ==true){
        	switch(buttonNumber) {
				case ~/.*1.*/:
					executeHandlers(1, value)
					break
				case ~/.*2.*/:
					executeHandlers(2, value)
					break
				case ~/.*3.*/:
					executeHandlers(3, value)
					break
				case ~/.*4.*/:
					executeHandlers(4, value)
					break
			}
		} else {
			if (input_enableLog == true){log.debug "Found recent button press events for $buttonNumber with value $value"}
		}
	}
}

def executeHandlers(buttonNumber, value) {
	if (input_enableLog == true){log.debug "executeHandlers: $buttonNumber - $value"}

	def lights = find('lights', buttonNumber, value)
	if (lights != null) toggle(lights)

	def locks = find('locks', buttonNumber, value)
	if (locks != null) toggle(locks)

	def sonos = find('sonos', buttonNumber, value)
	if (sonos != null) toggle(sonos)

	def mode = find('mode', buttonNumber, value)
	if (mode != null) changeMode(mode)

	def phrase = find('phrase', buttonNumber, value)
	if (phrase != null) location.helloHome.execute(phrase)

	def textMessage = findMsg('textMessage', buttonNumber)

	def notifications = find('notifications', buttonNumber, value)
	if (notifications?.toBoolean()) sendPush(textMessage ?: "Button $buttonNumber was pressed" )

	def phone = find('phone', buttonNumber, value)
	if (phone != null) sendSms(phone, textMessage ?:"Button $buttonNumber was pressed")

    def sirens = find('sirens', buttonNumber, value)
    if (sirens != null) toggle(sirens)
}

def find(type, buttonNumber, value) {
	def preferenceName = type + "_" + buttonNumber + "_" + value
	def pref = settings[preferenceName]
	if(pref != null) {
		if (input_enableLog == true){log.debug "Found: $pref for $preferenceName"}
	}

	return pref
}

def findMsg(type, buttonNumber) {
	def preferenceName = type + "_" + buttonNumber
	def pref = settings[preferenceName]
	if(pref != null) {
		if (input_enableLog == true){log.debug "Found: $pref for $preferenceName"}
	}

	return pref
}

def toggle(devices) {
	if (input_enableLog == true){log.debug "toggle: $devices = ${devices*.currentValue('switch')}"}

	if (devices*.currentValue('switch').contains('on')) {
		devices.off()
	}
	else if (devices*.currentValue('switch').contains('off')) {
		devices.on()
	}
	else if (devices*.currentValue('lock').contains('locked')) {
		devices.unlock()
	}
	else if (devices*.currentValue('lock').contains('unlocked')) {
		devices.lock()
	}
	else if (devices*.currentValue('alarm').contains('off')) {
        devices.siren()
    }
	else {
		devices.on()
	}
}

def changeMode(mode) {
	if (input_enableLog == true){log.debug "changeMode: $mode, location.mode = $location.mode, location.modes = $location.modes"}

	if (location.mode != mode && location.modes?.find { it.name == mode }) {
		setLocationMode(mode)
	}
}

// execution filter methods
private getAllOk() {
	modeOk && daysOk && timeOk
}

private getModeOk() {
	def result = !modes || modes.contains(location.mode)
	if (input_enableLog == true){log.trace "modeOk = $result"}
	result
}

private getDaysOk() {
	def result = true
	if (days) {
		def df = new java.text.SimpleDateFormat("EEEE")
		if (location.timeZone) {
			df.setTimeZone(location.timeZone)
		}
		else {
			df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
		}
		def day = df.format(new Date())
		result = days.contains(day)
	}
	
	if (input_enableLog == true){log.trace "daysOk = $result"}
	
	result
}

private getTimeOk() {
	def result = true
	if (starting && ending) {
		def currTime = now()
		def start = timeToday(starting, location.timeZone).time
		def stop = timeToday(ending, location.timeZone).time
		result = start < stop ? currTime >= start && currTime <= stop : currTime <= stop || currTime >= start
	}
	if (input_enableLog == true){log.trace "timeOk = $result"}
	result
}

private hhmm(time, fmt = "h:mm a")
{
	def t = timeToday(time, location.timeZone)
	def f = new java.text.SimpleDateFormat(fmt)
	f.setTimeZone(location.timeZone ?: timeZone(time))
	f.format(t)
}

private hideOptionsSection() {
	(starting || ending || days || modes) ? false : true
}

private timeIntervalLabel() {
	(starting && ending) ? hhmm(starting) + "-" + hhmm(ending, "h:mm a z") : ""
}

