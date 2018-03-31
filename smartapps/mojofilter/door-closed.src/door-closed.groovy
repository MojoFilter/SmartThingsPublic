/**
 *  We&#39;re In
 *
 *  Copyright 2018 Josh Weeks
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
    name: "Door Closed",
    namespace: "MojoFilter",
    author: "Josh Weeks",
    description: "Turn off lights when a door closes",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("When a door closes...") {
		input "contact", "capability.contactSensor", title: "Door?", required: false
	}
	section("Turn off...") {
		input "switches", "capability.switch", title: "These lights", multiple: true
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	subscribe()
}

def subscribe() {
	if (contact) {
		subscribe(contact, "contact.close", contactCloseHandler)
	}
}

def contactCloseHandler(evt) {
	log.debug "contact $evt.value"
	switches.off()
}

// TODO: implement event handlers