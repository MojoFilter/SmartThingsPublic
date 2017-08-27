definition(
    name: "Back To Bed",
    namespace: "MojoFilter",
    author: "Josh Weeks",
    description: "Turn specified lights back off after a delay when a switch is active.",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
	section("Turn off these lights..."){
		input "lights", "capability.switch", multiple: true
	}
	section("After a delay of..."){
		input "delayMinutes", "number", title: "Minutes?"
	}
	section("If this switch is set") {
		input "masterSwitch", "capability.switch", required: true
	}
}

def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	unschedule()
	initialize()
}

def initialize() {
    subscribe(lights, "switch.on", lightOnHandler)	
}

def lightOnHandler(evt) {
    if (evt.value == "on" && masterSwitch.currentSwitch == "on") {
        runIn(delayMinutes * 60, turnEmOff)
    }
}

def turnEmOff() {
    lights.off()
}