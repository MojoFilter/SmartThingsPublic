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
}

def unsubscribe() {

}

def switchOnHandler(evt) {
	log.debug "switch $evt.value"
	startTimer()
	triggerSwitch.off()
}


def startTimer() {
	timerLength = timerLengthMinutes * 60 * 1000
	updateRate = 2 //seconds
	startTime = now()
	updateLight()
}

def updateLight() {
    def passedTime = now() - startTime
	def currentBrightness = (passedTime / timerLength) * 100
	lights.setColor([hue: hue, saturation: saturation, level: brightness])
	if ((timerLength - passedTime) > (updateRate * 1000)) {
		runIn(updateRate, updateLight)
	}
}