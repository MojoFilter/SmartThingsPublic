/**
 *  Tea Light
 *
 *  Copyright 2017 Josh Weeks
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
 */
definition(
    name: "Tea Light",
    namespace: "MojoFilter",
    author: "Josh Weeks",
    description: "Color and dim a light in concert with tea timer",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {

	section("Choose hue lights you wish to control...") {
            input "lights", "capability.colorControl", title: "Which Color Changing Bulbs?", multiple: true
	}
    
	section("How long is the tea timer?"){
		input "timerLengthMinutes", "number", title: "Minutes?", defaultValue: 4, required: true
	}
	
    
    section("Trigger on which switch?") {
		input "triggerSwitch", "capability.switch", title: "Switch?", required: true
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	subscribe()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	subscribe()
}

def subscribe() {
	subscribe(triggerSwitch, "switch.on", triggerSwitchOnHandler)
    log.debug "Should be subscribed"
}

def unsubscribe() {

}

def triggerSwitchOnHandler(evt) {
	log.debug "switch $evt.value"
	triggerSwitch.off()
    log.debug "and now it should be off"
	startTimer()
}


def startTimer() {
	state.hue = 33
    state.saturation = 100 
	state.timerLength = timerLengthMinutes * 60 * 1000
	state.updateRate = 2 //seconds
	state.startTime = now()
	lights.setColor([hue: state.hue, saturation: state.saturation, level: 100])
	lights.on()
	updateLight()
}

def endTimer() {
	// orange. why not?
	lights.setColor([hue: 10, saturation: 100, level: 100])
}

def updateLight() {
    def passedTime = now() - state.startTime
	def currentBrightness = 100 - (passedTime / state.timerLength) * 100 
	def timeLeft = (state.timerLength - passedTime)
	def oneCycleMs =  (state.updateRate * 1000)
    log.debug "Timer lights at $currentBrightness ($passedTime / $state.timerLength)"
	log.debug "Time Left: $timeLeft"
	log.debug "Cycle length: $oneCycleMs"
	lights.setLevel(currentBrightness)
    if (timeLeft > oneCycleMs) {
		runIn(state.updateRate, updateLight)
	} else {
		endTimer()
	}
}

def captureStates() {
	def states = [:]
	for (theDevice in lights) {
		def deviceState = captureState(theDevice)
		def deviceID = theDevice.id
		states[deviceID] = deviceState
	}
	return states
}

def captureState(theDevice) {
	def deviceAttributes = theDevice.supportedAttributes
	def deviceAttrValue = [:]
	for ( attr in theDevice.supportedAttributes ) {
		def attrName = "${attr}"
		def attrValue = theDevice.currentValue(attrName)
		deviceAttrValue[attrName] = attrValue
	}
	return deviceAttrValue
}