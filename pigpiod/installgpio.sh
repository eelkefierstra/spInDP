#!/bin/bash

cp ./pigpiod.service /etc/systemd/system/
echo "copied service file"
systemctl enable pigpiod.service
echo "enabled service"
