#!/bin/bash
PID=$(ps -ef | grep MyDemoSpringboot.jar | grep -v grep | awk '{ print $2 }')
if [ -z "$PID" ]
then
    echo Application is already stopped
else
    echo kill -9 $PID
    kill -9 $PID
fi
