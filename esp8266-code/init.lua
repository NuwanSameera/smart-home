wifi.setmode(wifi.STATION)
wifi.sta.config("ssid", "password")
client_connected = false
m = mqtt.Client("ESP8266-" .. node.chipid(), 120, "", "")

tmr.alarm(0, 10000, 1, function()
    if (client_connected) then
        local payload = "ESP8266-" .. node.chipid()
        m:publish("SmartHome/Phone", payload, 0, 0, function(client)
            print("published")
        end)
    else
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
        m:connect("192.168.8.101", 1883, 0, function(client)
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
    end)
    m:on("offline", function(client)
        print("Disconnected")
        client_connected = false
    end)
end
