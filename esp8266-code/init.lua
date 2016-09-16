relay_status = "On"
relay = 0
gpio.mode(relay, gpio.OUTPUT)
gpio.write(relay, gpio.LOW)

wifi.setmode(wifi.STATION)
wifi.sta.config("<ssid>", "<password>")
client_connected = false
m = mqtt.Client("ESP8266-" .. node.chipid(), 120, "", "")

tmr.alarm(0, 10000, 1, function()
    if (client_connected == false) then
        connectMQTTClient()
    end
end)

function connectMQTTClient()
    local ip = wifi.sta.getip()
    if ip == nil then
        print("Waiting for network")
    else
        print("Client IP: " .. ip)
        print("Trying to connect MQTT client")
        m:connect("<mqtt server ip>", 1883, 0, function(client)
            client_connected = true
            print("MQTT client connected")
            subscribeToMQTTQueue()
        end)
    end
end

function subscribeToMQTTQueue()
    m:subscribe("SmartHome/Device", 0, function(client, topic, message)
        print("Subscribed to MQTT Queue")
    end)
    m:on("message", function(client, topic, message)
        print("MQTT message received")
        print(message)
        processMessage(message)
    end)
    m:on("offline", function(client)
        print("Disconnected")
        client_connected = false
    end)
end

function processMessage(message)
    if (string.match(message, node.chipid())) then
        if (string.match(message, "On")) then
            gpio.write(relay, gpio.HIGH)
            relay_status = "On"
        elseif (string.match(message, "Off")) then
            gpio.write(relay, gpio.LOW)
            relay_status = "Off"
        end
        sendCurrentStatus()
    end
end

function sendCurrentStatus()
    if (client_connected) then
        local payload = relay_status
        m:publish("SmartHome/Phone", payload, 0, 0, function(client)
        end)
    end
end
