/**
 *  Lounge Scenes Switcher
 *
 *  Copyright 2018 Neil Ord
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
    name: "Lounge Scenes Switcher",
    namespace: "NoOrdInaryGuy",
    author: "Neil Ord",
    description: "Fibaro-emitted scene based scene switcher",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Lights") {
        input "ceilingLights", "capability.switchLevel", required: true, title: "Ceiling Light"
        input "floorLamp", "capability.switch", required: true, title: "Floor Lamp"
    }
    section("Scenes") {
    	input "sceneOfInterest", "number", required: true, default: 14, title: "Trigger Scene"
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
	subscribe(ceilingLights, "scene", sceneChangedHandler)
    state.scenes = [
    	[
    		lamp: true,
            ceiling: 100
        ],
        [
    		lamp: true,
            ceiling: 15
        ],
        [
    		lamp: false,
            ceiling: 50
        ],
        [
    		lamp: true,
            ceiling: 0
        ],
        [
    		lamp: false,
            ceiling: 0
        ]
    ]
	state.nextScene = 0
}

def updateNextScene() {
  if(state.nextScene == state.scenes.size() - 1) {
    state.nextScene = 0
  } else {
    state.nextScene = state.nextScene + 1
  }
  log.debug("Incremented scene to ${state.nextScene}")
}

def sceneChangedHandler(evt) {  
  log.debug "Scene changed! Scene type: ${evt.integerValue}"
  if(sceneOfInterest == evt.integerValue) {
    log.debug("It's the scene of interest, taking action - setting scene as per ${state.nextScene}")
    
    def newCeiling = state.scenes[state.nextScene].ceiling
    if (newCeiling == 0) {
      ceilingLights.off()
    } else {
	  ceilingLights.setLevel(newCeiling)
    }
    
    if(state.scenes[state.nextScene].lamp) {
      floorLamp.on()
    } else {
      floorLamp.off()
    }
    
    updateNextScene()
  }
}
