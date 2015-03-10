#!/bin/sh

echo "Don't forget to add Queue/Topic OpenJMSDemo-deals"

echo ./examples.sh client.fx.dealmanager.DealManager $*
echo ./examples.sh client.fx.dealcapture.DealCapture $*
echo ./examples.sh client.fx.dealblotter.DealBlotter $*
./examples.sh client.fx.dealmanager.DealManager $* &
./examples.sh client.fx.dealcapture.DealCapture $* &
./examples.sh client.fx.dealblotter.DealBlotter $* &
