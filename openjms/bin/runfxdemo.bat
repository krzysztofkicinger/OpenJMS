@echo off
echo "Don't forget to add Queue/Topic OpenJMSDemo-deals"
echo
echo examples client.fx.dealmanager.DealManager %1 %2 %3 %4 %5 %6 %7 %8 %9
echo examples client.fx.dealcapture.DealCapture %1 %2 %3 %4 %5 %6 %7 %8 %9
echo examples client.fx.dealblotter.DealBlotter %1 %2 %3 %4 %5 %6 %7 %8 %9
start /b examples client.fx.dealmanager.DealManager %1 %2 %3 %4 %5 %6 %7 %8 %9
start /b examples client.fx.dealcapture.DealCapture %1 %2 %3 %4 %5 %6 %7 %8 %9
start /b examples client.fx.dealblotter.DealBlotter %1 %2 %3 %4 %5 %6 %7 %8 %9
